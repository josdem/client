package com.all.client.rest;

import static com.all.shared.messages.MessEngineConstants.SYNC_OWNER;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.Assert;

import com.all.shared.model.SyncValueObject;

public class SyncHttpResponseConverter extends StringHttpMessageConverter {

	private static final Log LOG = LogFactory.getLog(SyncHttpResponseConverter.class);

	public static final int BUFFER_SIZE = 4096;

	private final Map<String, SyncValueObject> currentRequests = new HashMap<String, SyncValueObject>();

	@SuppressWarnings( { "unchecked" })
	@Override
	protected String readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException {
		MediaType contentType = inputMessage.getHeaders().getContentType();
		Charset charset = contentType.getCharSet() != null ? contentType.getCharSet() : DEFAULT_CHARSET;
		String owner = inputMessage.getHeaders().getFirst(SYNC_OWNER);
		Reader in = new InputStreamReader(inputMessage.getBody(), charset);
		StringWriter out = new StringWriter();
		copy(in, out, inputMessage.getHeaders().getContentLength(), owner);
		return out.toString();
	}

	private int copy(Reader in, Writer out, long length, String email) throws IOException {
		Assert.notNull(in, "No Reader specified");
		Assert.notNull(out, "No Writer specified");
		try {
			int byteCount = 0;
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
				if (currentRequests.containsKey(email)) {
					int progress = (int) (byteCount * 100.0 / length);
					currentRequests.get(email).notifyProgress(progress);
				}
			}
			out.flush();
			return byteCount;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				LOG.error(e, e);
			}
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
	}

	public void addMergeRequest(SyncValueObject request) {
		currentRequests.put(request.getEmail(), request);
	}

	public void removeMergeRequest(SyncValueObject request) {
		currentRequests.remove(request.getEmail());
	}

}
