package awesome.team.conf;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorPageConfig {
	/**
    * SpringBoot2.0以上版本WebServerFactoryCustomizer代替之前版本
	*的EmbeddedWebServerFactoryCustomizerAutoConfiguration
    *
    * @return
    */
	//@Bean必须加上
	@Bean
	public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
		return (factory -> {
			ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
			factory.addErrorPages(errorPage404);
		});
	}
}		