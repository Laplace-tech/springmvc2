package hello.exceptions.resolver.strategy;

public interface ExceptionResolverStrategy {
    boolean supports(Exception ex);
    int getStatusCode();
    String getViewName();
    String getMessage(Exception ex);
}
