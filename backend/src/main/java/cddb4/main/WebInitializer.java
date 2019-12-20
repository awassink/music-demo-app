package cddb4.main;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebInitializer implements WebApplicationInitializer{
	@Override
    public void onStartup(ServletContext container) {
		//create root Spring appcontext based on SpringConfiguration.class
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(AppConfig.class);
		rootContext.register(HibernateConfig.class);
		
		// Manage the lifecycle of the root application context
	    container.addListener(new ContextLoaderListener(rootContext));
	    
	    //create the servlet's application context
	    AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
	    dispatcherContext.register(DispatcherConfig.class);
		
		//Register and map the dispatcher servlet, inject dispatcherContext
		ServletRegistration.Dynamic registration = container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
	    registration.setLoadOnStartup(1);
	    registration.addMapping("/");
    }

}
