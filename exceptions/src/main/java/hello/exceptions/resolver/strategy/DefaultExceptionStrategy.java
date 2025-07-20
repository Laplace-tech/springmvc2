package hello.exceptions.resolver.strategy;

import jakarta.servlet.http.HttpServletResponse;

//fallback 전략은 리스트에서 제외, 수동 처리
public class DefaultExceptionStrategy implements ExceptionResolverStrategy {

    @Override
    public boolean supports(Exception ex) {
        return true; // 항상 매칭되는 마지막 전략
    }

    @Override
    public int getStatusCode() {
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getViewName() {
        return "error/500";
    }
    
    @Override
    public String getError() {
        return "Internal Server Error";
    }

    @Override
    public String getMessage(Exception ex) {
        return "서버 내부 오류가 발생했습니다.";
    }
}
