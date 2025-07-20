package hello.exceptions.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
//        ErrorPage error404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
//        ErrorPage error500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");
//        ErrorPage errorException = new ErrorPage(RuntimeException.class, "/error-page/500");
//
//        factory.addErrorPages(error404, error500, errorException);
    }
}
