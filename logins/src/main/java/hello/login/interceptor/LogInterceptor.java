package hello.login.interceptor;

import java.util.UUID;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*
 * (1) 정상 흐름에서의 인터셉터 호출 순서
 *   HTTP 요청 -> WAS -> 필터 
 * 	  -> DispatcherServlet
 * 	    -> Interceptor.preHandle() : return false 되면 postHandle(), afterCompletion() 모두 실행되지 않음
 * 	 	  -> HandlerAdapter 호출 (컨트롤러 실행)
 * 		    -> Interceptor.postHandle() : 컨트롤러 실행 중, 예외 발생 시 호출되지 않음
 * 		  	  -> View 렌더링 
 * 			    -> Interceptor.afterCompletion() : 컨트롤러 실행 중, 예외가 있든 없든 항상 실행 됨 
 * 
 * (2) 예외 발생 시 흐름
 *   HTTP 요청 -> WAS -> 필터
 *     -> DispatcherServlet
 *       -> interceptor.preHandle()
 *         -> HandlerAdapter 호출 (컨트롤러 실행 중 예외 발생)
 *           X postHandle() 생략
 *             -> View 에러 처리 : 에러를 표시하는 View 를 렌더링하는 과정
 *               -> interceptor.afterCompletion(ex)
 */

/*
 * 요청마다 UUID로 구분 가능한 로그를 남기는 인터셉터
 * 컨트롤러 호출 전/후, 뷰 렌더링 후 로깅 지원
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

	public static final String LOG_ID = "LOG_ID";

	/*
	 * preHandle() : 컨트롤러 메서드가 호출되기 직전에 실행됨 
	 * - 로그인 인증 여부 확인
	 * - 요청 로그 찍기
	 * - 권한 체크
	 * return true : 다음으로 넘어감 (컨트롤러 실행됨)
	 * return false : 요청 중단됨 (컨트롤러로 안감)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String requestURI = request.getRequestURI();
		String uuid = UUID.randomUUID().toString();

		// UUID를 request 에 저장 (afterCompletion에서 재사용)
		request.setAttribute(LOG_ID, uuid);

		/*
		 * "handler" 는 이 요청을 처리할 컨트롤러의 정보 
		 * 대부분의 요청은 @Controller에 매핑된 메서드가 처리 
		 * -> 이 경우 HandlerMethod 타입이다. 그래서 "instanceof" 체크 후 캐스팅한다
		 */
		if (handler instanceof HandlerMethod handlerMethod) {
			String controllerName = handlerMethod.getBeanType().getSimpleName();
			String methodName = handlerMethod.getMethod().getName();
			log.info("REQUEST [{}] -> {}.{}() [{}]", uuid, controllerName, methodName, requestURI);
		} else {
			log.info("REQUEST [{}] -> Handler : {} [{}]", uuid, handler.getClass().getSimpleName(), requestURI);
		}

		return true;
	}

	/*
	 * postHandle() : 컨트롤러 메서드가 성공적으로 실행되고 난 후, 뷰 렌더링 전에 호출됨 
	 * - 컨트롤러가 리턴한 모델 확인 가능
	 * - 예외 발생 시 호출되지 않음
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler, ModelAndView modelAndView) throws Exception {
		log.info("postHandle - ModelAndView : {}", modelAndView);
	}

	/*
	 * afterCompletion() : 뷰가 렌더링된 이후 호출됨 (예외가 있어도 무조건 호출됨)
	 * - 응답 로그 분석
	 * - 예외가 있으면 로깅
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		String requestURI = request.getRequestURI();
		String uuid = (String) request.getAttribute(LOG_ID);
		
		log.info("RESPONSE [{}] <- [{}]", uuid, requestURI);
		
		if(ex != null) {
			log.error("X afterCompletion Exception!", ex);
		}
	}

}
