package com.all.client.util;

import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.all.networking.NetworkingSocketFactory;
import com.all.networking.util.MinaIoFactory;

@Ignore("Used to test encoding from Mac to Windows and viceversa.")
public class TestEncoding {

	private static final int PORT = 10000;
	private NetworkingSocketFactory socketFactory = new NetworkingSocketFactory(new MinaIoFactory());
	private JFrame testFrame;
	private JTextField textField;
	private Charset utfCharset = Charset.forName("UTF-8");
	private CharsetEncoder utfEncoder = utfCharset.newEncoder();
	private CharsetDecoder utfDecoder = utfCharset.newDecoder();

	@Test
	public void shouldSendEncodedMessage() throws Exception {
		SocketAddress destinationSocketAddress = new InetSocketAddress("192.168.1.32", PORT);
		IoConnector connector = socketFactory.newConnector();
		connector.setHandler(new IoHandlerAdapter());
		ConnectFuture connectionFuture = connector.connect(destinationSocketAddress);
		connectionFuture.await();
		IoSession session = connectionFuture.getSession();

		testFrame.setVisible(true);
		while (testFrame.isVisible()) {
			if (textField.getText().endsWith(".")) {
				ByteBuffer byteBuffer = utfEncoder.encode(CharBuffer.wrap(textField.getText()));
				String encodedMessage = new String(Base64.encode(byteBuffer.array()));
				// String encodedMessage = new
				// String(Base64.encode(textField.getText().getBytes()));
				session.write(encodedMessage);
			}
			Thread.sleep(2000);
		}
		session.close(true);
		connector.dispose();
	}

	@Test
	public void shouldReceiveEncodedMessage() throws Exception {
		IoAcceptor acceptor = socketFactory.newAcceptor();
		acceptor.setHandler(new IoHandlerAdapter() {
			@Override
			public void messageReceived(IoSession session, Object message) throws Exception {
				byte[] decodedBytes = Base64.decode(message.toString().getBytes());
				CharBuffer charBuff = utfDecoder.decode(ByteBuffer.wrap(decodedBytes));
				String decodedMessage = charBuff.toString();
				// byte[] decodedBytes = Base64.decode(message.toString().getBytes());
				// String decodedMessage = new String(decodedBytes);
				textField.setText(decodedMessage);
			}
		});
		acceptor.bind(new InetSocketAddress(PORT));
		testFrame.setVisible(true);
		while (testFrame.isVisible()) {
			Thread.sleep(1000);
		}
		acceptor.dispose();
	}

	@Before
	public void setup() {
		testFrame = new JFrame("Encoding Test");
		Dimension dimension = new Dimension(300, 200);
		testFrame.setSize(dimension);
		testFrame.setMaximumSize(dimension);
		testFrame.setMinimumSize(dimension);
		testFrame.setLayout(null);
		textField = new JTextField();
		textField.setBounds(20, 50, 250, 50);
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testFrame.add(textField);
	}

}
