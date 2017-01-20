package ispel.integrator.service;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ServiceCallTimestampAspect {

	private static final Logger logger = Logger
			.getLogger(ServiceCallTimestampAspect.class);

	@After("execution(* ispel.integrator.service.IspelService.*(..))")
	public void setDataSourceForAdapterService() {
		long timestamp = System.currentTimeMillis();
		logger.debug("Service call timestamp created: " + timestamp);
		ServiceCallTimestampHolder.setTimestamp(timestamp);
	}
}
