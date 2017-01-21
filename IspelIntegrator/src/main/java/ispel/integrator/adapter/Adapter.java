package ispel.integrator.adapter;

import ispel.integrator.adapter.AdapterRequest.MethodName;
import ispel.integrator.service.AdapterService;
import ispel.integrator.service.ServiceCallTimestampHolder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

@Component
public class Adapter {

	private static final Logger logger = Logger.getLogger(Adapter.class);

	private final ExecutorService exec = Executors.newFixedThreadPool(10);
	private int port;

	private AdapterService service;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public AdapterService getService() {
		return service;
	}

	public void setService(AdapterService service) {
		this.service = service;
	}

	public void start() throws IOException {
		logger.info("adapter started");
		ServerSocket socket = new ServerSocket(port);
		while (!exec.isShutdown()) {
			try {
				final Socket conn = socket.accept();
				exec.execute(new Runnable() {
					public void run() {
						try {
							handleRequest(conn);
						} catch (IOException e) {
							logger.error(e);
						}
					}
				});
			} catch (RejectedExecutionException e) {
				if (!exec.isShutdown()) {
					logger.error(e);
				}
			}
		}
		logger.info("Adapter finished");

	}

	public void stop() {
		exec.shutdown();
	}

	void handleRequest(Socket conn) throws IOException {
        int bufFrame = 48;
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
				conn.getOutputStream(), bufFrame));
		DataInputStream dis = new DataInputStream(new BufferedInputStream(
				conn.getInputStream(), bufFrame));
		String reply = null;
		try {
			byte[] buf = new byte[bufFrame];
			dis.readFully(buf, 0, bufFrame);
			String s = new String(buf);
			String opCode = s.substring(0, 3);
			int operationCode = Integer.parseInt(opCode);
			if (operationCode == 999) {
				stop();
				return;
			}
			AdapterRequest request = AdapterRequest.getRequest(s);
			logger.info("Adapter request received: " + request.toString());
			Result result = null;
			ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());
			switch (request.getMethodName()) {
			case GetVinExpert:
				result = service.getVinExpert(request);
				break;
			case VerifyCar:
				result = service.verifyCar(request);
				break;
			case SubmitInvoiceData:
				result = service.submitInvoiceData(request);
				break;
			default:
				throw new IllegalStateException("wrong method name: " + request.getMethodName());
			} // switch

			logger.info("Result: " + result.toString());
			reply = buildReply(request, result);
			dos.write(reply.getBytes());

		} catch (Exception e) {
			ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());
			StringBuilder sb = new StringBuilder();
			sb.append("ER").append(ServiceCallTimestampHolder.getAsDateTime())
					.append("Send request not successful");
			try {
				dos.write(sb.toString().getBytes());
			} catch (Exception e1) {
			}
			logger.error(e);
		} finally {
			try {
				dos.flush();
				dos.close();
				logger.info("Reply sent: " + reply);
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	private String buildReply(AdapterRequest request, Result result)
			throws IOException {
		StringBuilder reply = new StringBuilder("");
		if (MethodName.GetVinExpert.equals(request.getMethodName())) {
			if (result.getErrorText() == null
					|| result.getErrorText().isEmpty()) {
				reply.append("OK").append(
						ServiceCallTimestampHolder.getAsDateTime());
			} else {
				reply.append("ER")
						.append(ServiceCallTimestampHolder.getAsDateTime())
						.append(result.getErrorText());
			}
        } else if (MethodName.VerifyCar.equals(request.getMethodName())) {
            if (result.getErrorText() == null
                    || result.getErrorText().isEmpty()) {
                reply.append("OK").append(
                        ServiceCallTimestampHolder.getAsDateTime());
            } else {
                reply.append("ER")
                        .append(ServiceCallTimestampHolder.getAsDateTime())
                        .append(result.getErrorText());
            }
        } else if (MethodName.SubmitInvoiceData.equals(request.getMethodName())) {
            if (result.getErrorText() == null
                    || result.getErrorText().isEmpty()) {
                reply.append("OK").append(
                        ServiceCallTimestampHolder.getAsDateTime());
            } else {
                reply.append("ER")
                        .append(ServiceCallTimestampHolder.getAsDateTime())
                        .append(result.getErrorText());
            }
        } else {
            ServiceCallTimestampHolder.setTimestamp(System.currentTimeMillis());
            reply.append("ER")
                    .append(ServiceCallTimestampHolder.getAsDateTime())
                    .append("Unknown request method");
        }
		return reply.toString();
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		String log4jConfig = System.getProperty("log4j.configuration");
		if (log4jConfig == null) {
			BasicConfigurator.configure();
		} else {
			PropertyConfigurator.configure(log4jConfig);
		}

		ApplicationContext ctx;
		String resProp = System.getProperty("ext.res.dir");
		try {
			if (resProp != null) {
				ctx = new FileSystemXmlApplicationContext(resProp
						+ "/run-config.xml");
			} else {
				ctx = new ClassPathXmlApplicationContext(
						"/spring/run-config.xml");
			}

			Adapter adapter = ctx.getBean("adapter", Adapter.class);
			try {
				adapter.start();
			} catch (IOException e) {
				logger.error(e);
			}
		} finally {
		}
	}
}
