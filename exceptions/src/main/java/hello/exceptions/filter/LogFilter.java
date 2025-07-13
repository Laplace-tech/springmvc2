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

@Slf4j
public class LogFilter implements Filter {

	private static final String LOG_PREFIX = "REQUEST";
	private static final String LOG_SUFFIX = "RESPONSE";
	
	// 필터가 초기화될 때 한 번만 호출됨
	@Override
	public void init(FilterConfig filterConfig) {
		log.info("LogFilter initialized");
	}
	
	// 모든 요청마다 호출되는 메서드
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();
		String uuid = UUID.randomUUID().toString();
		DispatcherType dispatcherType = request.getDispatcherType();
		
		try {
			logRequest(uuid, dispatcherType, requestURI);
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Exception occured : {}", e.getMessage());
			throw e;
		} finally {
			logResponse(uuid, dispatcherType, requestURI);
		}
	}

	@Override
	public void destroy() {
		log.info("LogFilter destroyed");
	}
	
	private void logRequest(String uuid, DispatcherType dispatcherType, String uri) {
		log.info("{} [{}][{}][{}]", LOG_PREFIX, uuid, dispatcherType, uri);
	}
	
	private void logResponse(String uuid, DispatcherType dispatcherType, String uri) {
		log.info("{} [{}][{}][{}]", LOG_SUFFIX, uuid, dispatcherType, uri);
	}
	
}