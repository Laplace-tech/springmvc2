package hello.login.interceptor;

import java.util.UUID;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

	public static final String LOG_ID = "LOG_ID";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String requestURI = request.getRequestURI();
		String uuid = UUID.randomUUID().toString();

		// afterCompletion에서 사용하기 위해 저장
		request.setAttribute(LOG_ID, uuid);

		/*
		 * "handler" 는 이 요청을 처리할 컨트롤러의 정보임. 대부분의 요청은 @Controller에 매핑된 메서드가 처리한다. -> 이 경우
		 * HandlerMethod 타입이다. 그래서 "instanceof" 체크 후 캐스팅한다
		 * 
		 * 로그 : 컨트롤러 정보 추출
		 */
		if (handler instanceof HandlerMethod handlerMethod) {
			String controllerName = handlerMethod.getBeanType().getSimpleName();
			String methodName = handlerMethod.getMethod().getName();
			log.info("REQUEST [{}] → {}.{}() [{}]", uuid, controllerName, methodName, requestURI);
		} else {
			log.info("REQUEST [{}] → Handler: {} [{}]", uuid, handler.getClass().getSimpleName(), requestURI);
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("postHandle - ModelAndView: {}", modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		String requestURI = request.getRequestURI();
		String logId = (String) request.getAttribute(LOG_ID);
		log.info("RESPONSE [{}] ← [{}]", logId, requestURI);

		if (ex != null) {
			log.error("❌ afterCompletion Exception!", ex);
		}

	}

}
