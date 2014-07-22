package next.wildgoose.backcontroller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BackControllerListener implements ServletContextListener {

	
	@Override
	public void contextInitialized(ServletContextEvent event) {
//		ServletContext context = event.getServletContext();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
