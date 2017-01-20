package ispel.integrator.datasource;

import java.util.Properties;

import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.utils.PropertiesUtil;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Aspect
public class SetDataSourceAspect implements ApplicationContextAware {

	private static final Logger logger = Logger
			.getLogger(SetDataSourceAspect.class);

	private ApplicationContext ctx;

	@Before("execution(* ispel.integrator.service.LogService.logResult(..))")
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void setDataSourceForLogService() {
		logger.trace("in pointcut");
		Properties props = ctx.getBean("props", Properties.class);
		logger.debug("ds: " + props.getProperty("adapter.main.dataSource"));
		DataSourceNameHolder.setDataSourceName(props
				.getProperty("adapter.main.dataSource"));
		logger.trace("out pointcut");
	}

	@Before("execution(* ispel.integrator.service.AdapterService.*(..)) && args(request,..)"
			+ "execution(* ispel.integrator.service.CarService.*(..)) && args(carInfo,..)")
	public void setDataSourceForAdapterService(AdapterRequest request) {
		logger.trace("in pointcut");
		logger.debug("ds: " + request.getDataSourceName());
		DataSourceNameHolder.setDataSourceName(request.getDataSourceName());
		logger.trace("out pointcut");
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
	}

}
