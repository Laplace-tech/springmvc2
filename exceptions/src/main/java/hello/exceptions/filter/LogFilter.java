package hello.exceptions.filter;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * LogFilter
 * 
 * 전체 요청-응답 흐름을 로깅하는 서블릿 필터
 * DispatcherType에 따른 요청 구분 및 예외 상황 처리 로깅 포함
 */
@Slf4j
//@Component
public class LogFilter implements Filter {

    private static final String PREFIX = "REQUEST";
    private static final String SUFFIX = "RESPONSE";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LogFilter Initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

    	System.out.println();
    	
        // HttpServletRequest가 아니면 로그 없이 바로 필터 체인 진행
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uuid = UUID.randomUUID().toString();  // 요청별 고유 ID
        String uri = httpRequest.getRequestURI();
        DispatcherType dispatcherType = httpRequest.getDispatcherType();

        logPhase(PREFIX, uuid, dispatcherType, uri);

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            // 예외 로깅 후 예외 재던지기
            log.error("Exception during request [{}]: {}", uuid, e.toString(), e);
            throw e;
        } finally {
            logPhase(SUFFIX, uuid, dispatcherType, uri);
        }
    }

    @Override
    public void destroy() {
        log.info("LogFilter Destroyed");
    }

    private void logPhase(String phase, String uuid, DispatcherType type, String uri) {
        log.info("{} [{}][{}][{}]", phase, uuid, type, uri);
    }
}
