package hello.login.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * 모든 HTTP 요청과 응답에 대해 로그를 출력하는 필터
 * - 각 요청마다 UUID를 생성해 요청-응답 한 쌍을 식별
 */
@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("[LogFilter] 초기화됨");
    }

    /**
     * 요청이 들어올 때마다 실행됨
     * - UUID로 요청-응답 구분
     * - 요청 URI 로깅
     * - 요청 처리 완료 후 응답 로그 출력
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // HTTPServletRequest로 형변환 (URI 확인하려면 필요)
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 요청 식별을 위한 UUID
        String uuid = UUID.randomUUID().toString();

        try {
            log.info("[REQUEST] [{}] {}", uuid, requestURI);
            chain.doFilter(request, response); // 다음 필터 or 서블릿으로 넘기기
        } catch (Exception e) {
            throw e; // 예외 전달 (로깅 외 따로 처리하지 않음)
        } finally {
            log.info("[RESPONSE] [{}] {}", uuid, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("[LogFilter] 종료됨");
    }
}
