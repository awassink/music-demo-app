package cddb4.main;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan({ "cddb4.rest" })
@EnableWebMvc
public class DispatcherConfig extends WebMvcConfigurerAdapter {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry){
		registry.addResourceHandler("/angularclient/**")
				.addResourceLocations("/angularclient/")
				.setCachePeriod(0)
				.resourceChain(true)
				.addResolver(new PathResourceResolver());
		
		
	}
	
	//The CORS mappings are ONLY necessary when the client is hosted from another domain.
	//For example, if you develop the in NetBeans and run it from a different server :@
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*")
        						  .allowedMethods("PUT", "DELETE", "GET", "POST", "OPTIONS");
    }
    
}
