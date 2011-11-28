package com.all.client.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.all.commons.Md5FileGenerator;

public abstract class AbstractFileHttpMessageConverter implements HttpMessageConverter<File> {
	private static final int DELAY = 90; // seconds for the timeout
	private static final Log LOG = LogFactory.getLog(AbstractFileHttpMessageConverter.class);
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final int GUESS_CONTENT_LENGTH = 10 * 1024 * 1024; // 10 MB
	private static final String FILENAME = "filename=";
	private MediaType supportedMediatype = new MediaType("application", "octet-stream");
	private List<MediaType> supportedMeditypes;
	private double contentLength; // defined double to avoid casting it in progress events
	private boolean contentLengthHeader = false; // defined double to avoid casting it in progress events
	private String crcCheck;
	private FileHttpMessageConverterListener fileHttpMessageConverterListener = new FileHttpMessageConverterAdapter();
	private Md5FileGenerator md5FileGenerator = new Md5FileGenerator();

	public AbstractFileHttpMessageConverter() {
		supportedMeditypes = new ArrayList<MediaType>(1);
		supportedMeditypes.add(supportedMediatype);
		supportedMeditypes = Collections.unmodifiableList(supportedMeditypes);
	}

	public void setFileHttpMessageConverterListener(FileHttpMessageConverterListener fileHttpMessageConverterListener) {
		this.fileHttpMessageConverterListener = fileHttpMessageConverterListener;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return File.class.equals(clazz) && (mediaType == null || supportedMediatype.includes(mediaType));
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return supportedMeditypes;
	}

	@Override
	public File read(Class<? extends File> clazz, HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {

		if (!updateavailable(inputMessage)) {
			throw new HttpMessageNotReadableException("No update available");
		}

		String downloadFileName = getDownloadFileName(inputMessage);
		fileHttpMessageConverterListener.onDownloadStarted(new DownloadStartedEvent(this, downloadFileName));

		File downloadFile = createDownloadFile(inputMessage);

		if (downloadFile.exists() && isSameAsFileInServer(downloadFile)) {
			fileHttpMessageConverterListener.onDownloadProgress(new DownloadProgressEvent(this, downloadFileName, 100));
			fileHttpMessageConverterListener.onDownloadCompleted(new DownloadCompletedEvent(this, downloadFileName,
					downloadFile));
			return downloadFile;
		}

		writeContents(downloadFile, inputMessage);

		return downloadFile;
	}

	private boolean isSameAsFileInServer(File downloadFile) {
		if (crcCheck != null) {
			return checkDownloadFileDataIntegrity(downloadFile);
		} else if (contentLengthHeader && downloadFile.length() == contentLength) {
			LOG.warn(String.format("Content-MD5 header not found, using Content-Length"));
			return true;
		}

		LOG.warn("There are not Content-MD5 header nor Content-Length header, cannot determine download progress accuretely "
				+ "or data corruption while transfering");

		return false;
	}

	private boolean checkDownloadFileDataIntegrity(File downloadFile) {
		String calculatedMd5Checksum = calculateMd5Checksum(downloadFile);
		boolean equals = crcCheck.equals(calculatedMd5Checksum);
		LOG.info(String.format("Content-MD5 [%s], file CRC check pass: %b", crcCheck, equals));
		return equals;
	}

	private String calculateMd5Checksum(File updateFile) {
		byte[] calculatedMd5Checksum = md5FileGenerator.calculateMd5Checksum(updateFile);
		return md5FileGenerator.getByteToBase64String(calculatedMd5Checksum);
	}

	private boolean updateavailable(HttpInputMessage inputMessage) {
		try {
			if (inputMessage instanceof ClientHttpResponse) {
				ClientHttpResponse clientHttpMessage = (ClientHttpResponse) inputMessage;
				if (clientHttpMessage.getStatusCode() == HttpStatus.OK) {
					extractDataFromHeaders(clientHttpMessage);
					return true;
				}
			}
		} catch (IOException e) {
			LOG.error(e, e);
		}
		return false;
	}

	private void extractDataFromHeaders(ClientHttpResponse clientHttpMessage) {
		crcCheck = clientHttpMessage.getHeaders().getFirst("Content-MD5");
		String contentLengthStr = clientHttpMessage.getHeaders().getFirst("Content-Length");
		try {
			contentLength = Long.valueOf(contentLengthStr);
			contentLengthHeader = true;
		} catch (NumberFormatException nfe) {
			LOG.error("Unable to read content length, using a gues value", nfe);
			contentLength = GUESS_CONTENT_LENGTH;
		}
	}

	private void writeContents(File downloadFile, final HttpInputMessage inputMessage) {
		final Timer timer = new Timer();
		final String filename = downloadFile.getName();
		FileOutputStream fos = null;
		
		try {
			final InputStream body = inputMessage.getBody();
			fos = new FileOutputStream(downloadFile, false);

			// TODO change, find a better way to detect timeout while reading from the inputStream body
			final AtomicInteger secondsElapsed = new AtomicInteger(-5);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					//LOG.warn(this.getClass().getSimpleName() + " read timeout thread running... " + secondsElapsed + "/" + DELAY);
					if (secondsElapsed.incrementAndGet() > DELAY) {
						String error = "Timeout receiving file";
						LOG.error(error, new InterruptedIOException(error));
						fileHttpMessageConverterListener.onDownloadError(new DownloadErrorEvent(this, filename, error));
						this.cancel();
						
						//TODO force by any means to close the socker.read operation blocking the thread
						try {
							body.close();
						} catch(IOException ioe) {
							LOG.error(ioe, ioe);
						}
						((ClientHttpResponse)inputMessage).close();
					}
				}
			}, 0, 1000);
			// END TODO

			byte[] readBytes = new byte[1024];
			int length;
			double total = 0;
			while ((length = body.read(readBytes)) != -1) {
				fos.write(readBytes, 0, length);
				total += length;
				secondsElapsed.set(0);
				fileHttpMessageConverterListener.onDownloadProgress(new DownloadProgressEvent(this, filename,
						(int) (total * 100. / contentLength)));
			}
			secondsElapsed.set(0);
			timer.cancel();
			fos.flush();

			if (crcCheck != null && !checkDownloadFileDataIntegrity(downloadFile)) {
				fileHttpMessageConverterListener.onDownloadError(new DownloadErrorEvent(this, filename,
						"Update file CRC check failed"));
			} else {
				fileHttpMessageConverterListener.onDownloadCompleted(new DownloadCompletedEvent(this, filename, downloadFile));
			}

		} catch (Exception e) {
			fileHttpMessageConverterListener.onDownloadError(new DownloadErrorEvent(this, filename, e.getMessage()));
			LOG.error("Unable to save update file into hard disk", e);
		} finally {
			close(timer, fos);
		}
	}

	private void close(final Timer timer, FileOutputStream fos) {
		IOUtils.closeQuietly(fos);
		timer.cancel();
	}

	protected abstract File createDownloadFile(HttpInputMessage inputMessage) throws IOException;

	protected String getDownloadFileName(HttpInputMessage inputMessage) {
		String contentDisposition = inputMessage.getHeaders().getFirst(CONTENT_DISPOSITION);
		String updateFileName = contentDisposition.substring(contentDisposition.lastIndexOf(FILENAME) + FILENAME.length());
		return updateFileName;
	}

	@Override
	public void write(File t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		throw new UnsupportedOperationException("Not implemented");
	}

}
