package ispel.integrator.test;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestClient {

	@Test
	public void getVINExpert() throws Exception {
		Socket s = new Socket("localhost", 6900);
        InputStream is = new BufferedInputStream(s.getInputStream(), 48);
        OutputStream os = new BufferedOutputStream(s.getOutputStream(), 48);
        StringBuilder sb = new StringBuilder()
                .append("010")
                .append("0000001118")
                .append("kto_1                         ")
                .append("        ");
        os.write(sb.toString().getBytes("UTF-8"), 0, 48);

		os.flush();
		byte[] frame = new byte[1024];
		is.read(frame, 0, 1024);
		String str = new String(frame);
		System.out.println("received: " + str);
	}

	@Test
	public void verifyCar() throws Exception {
		Socket s = new Socket("localhost", 6900);
        InputStream is = new BufferedInputStream(s.getInputStream(), 48);
        OutputStream os = new BufferedOutputStream(s.getOutputStream(), 48);
        StringBuilder sb = new StringBuilder()
                .append("020")
                .append("0000001118")
                .append("kto_1                         ")
                .append("     ");
        os.write(sb.toString().getBytes("UTF-8"), 0, 48);

        os.flush();
        byte[] frame = new byte[1024];
        is.read(frame, 0, 1024);
        String str = new String(frame);
        System.out.println("received: " + str);

    }

    @Test
    public void submitOrderData() throws Exception {
        Socket s = new Socket("localhost", 6900);
        InputStream is = new BufferedInputStream(s.getInputStream(), 48);
        OutputStream os = new BufferedOutputStream(s.getOutputStream(), 48);
        StringBuilder sb = new StringBuilder()
                .append("110")
                .append("kto_1                         ")
                .append("ZAK")
                .append("0012140001")
                .append("17");
        os.write(sb.toString().getBytes("UTF-8"), 0, 48);

        os.flush();
        byte[] frame = new byte[1024];
        is.read(frame, 0, 1024);
        String str = new String(frame);
        System.out.println("received: " + str);

    }

    @Test
    public void submitSlipData() throws Exception {
        Socket s = new Socket("localhost", 6900);
        InputStream is = new BufferedInputStream(s.getInputStream(), 48);
        OutputStream os = new BufferedOutputStream(s.getOutputStream(), 48);
        StringBuilder sb = new StringBuilder()
                .append("110")
                .append("kto_1                         ")
                .append("VYD")
                .append("0000600001")
                .append("16");
        os.write(sb.toString().getBytes("UTF-8"), 0, 48);

        os.flush();
        byte[] frame = new byte[1024];
        is.read(frame, 0, 1024);
        String str = new String(frame);
        System.out.println("received: " + str);

    }

    @Test
    public void submitMultipleData() throws Exception {
        Socket s = new Socket("localhost", 6900);
        InputStream is = new BufferedInputStream(s.getInputStream(), 48);
        OutputStream os = new BufferedOutputStream(s.getOutputStream(), 48);
        StringBuilder sb = new StringBuilder()
                .append("120")
                .append("kto_1                         ")
                .append("ZAK")
                .append("          ")
                .append("  ");
        os.write(sb.toString().getBytes("UTF-8"), 0, 48);

        os.flush();
        byte[] frame = new byte[1024];
        is.read(frame, 0, 1024);
        String str = new String(frame);
        System.out.println("received: " + str);

    }


    @Test
    public void importSZV() throws Exception {
        Socket s = new Socket("localhost", 6900);
        InputStream is = new BufferedInputStream(s.getInputStream(), 48);
        OutputStream os = new BufferedOutputStream(s.getOutputStream(), 48);
        StringBuilder sb = new StringBuilder()
                .append("030")
                .append("kto_1                         ")
                .append("ZAK")
                .append("0010140148")
                .append("15");
        os.write(sb.toString().getBytes("UTF-8"), 0, 48);

        os.flush();
        byte[] frame = new byte[1024];
        is.read(frame, 0, 1024);
        String str = new String(frame);
        System.out.println("received: " + str);
    }

}
