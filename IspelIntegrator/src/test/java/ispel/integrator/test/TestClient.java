package ispel.integrator.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.junit.Test;

public class TestClient {

	@Test
	public void getVINExpert() throws Exception {
		Socket s = new Socket("localhost", 6900);
		InputStream is = new BufferedInputStream(s.getInputStream(), 43);
		OutputStream os = new BufferedOutputStream(s.getOutputStream(), 43);
		StringBuilder sb = new StringBuilder();
		sb.append("010").append("0000000001")
				.append("kto_1                         ");
		os.write(sb.toString().getBytes("UTF-8"), 0, 43);

		os.flush();
		byte[] frame = new byte[1024];
		is.read(frame, 0, 1024);
		String str = new String(frame);
		System.out.println("received: " + str);
	}

	@Test
	public void verifyCar() throws Exception {
		Socket s = new Socket("localhost", 6900);
		InputStream is = new BufferedInputStream(s.getInputStream(), 43);
		OutputStream os = new BufferedOutputStream(s.getOutputStream(), 43);
		StringBuilder sb = new StringBuilder();
		sb.append("020").append("0000000002")
				.append("kto_1                         ");
		os.write(sb.toString().getBytes("UTF-8"), 0, 43);

		os.flush();
		byte[] frame = new byte[1024];
		is.read(frame, 0, 1024);
		String str = new String(frame);
		System.out.println("received: " + str);

	}
}
