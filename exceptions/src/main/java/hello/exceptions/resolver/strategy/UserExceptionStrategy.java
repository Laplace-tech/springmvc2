package hello.exceptions.resolver.strategy;

import hello.exceptions.exception.UserException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class UserExceptionStrategy implements ExceptionResolverStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof UserException;
    }

    @Override
    public int getStatusCode() {
        return HttpServletResponse.SC_BAD_REQUEST;
    }

    @Override
    public String getViewName() {
        return "error/400";
    }

	@Override
	public String getError() {
		return "Bad Request";
	}
    
    @Override
    public String getMessage(Exception ex) {
        return ex.getMessage();
    }

}
