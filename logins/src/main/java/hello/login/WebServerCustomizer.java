package hello.login;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

/*
 * WebServerCustomizer - 오류 페이지 등록
 */

@Controller
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.addErrorPages(
            new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404"),
            new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500"),
            new ErrorPage(RuntimeException.class, "/error-page/500")
        );
    }
}


