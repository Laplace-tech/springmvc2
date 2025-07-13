package hello.exceptions.interceptor;

import java.util.UUID;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*
 * 
 * [인터셉터가 개입한 전체 동작 흐름]
 * 
 * [1] 정상 요청 흐름 (예외 없음)
 *
 * 	1. 브라우저 → HTTP 요청 전송
 * 	2. WAS(Tomcat) 요청 수신
 * 	3. DispatcherServlet 진입 (Spring MVC 진입점)
 * 	4. 인터셉터의 preHandle() 호출
 * 	5. 컨트롤러 정상 실행
 * 	6. 인터셉터의 postHandle() 호출
 * 	7. ViewResolver 통해 View 렌더링 진행
 * 	8. 인터셉터의 afterCompletion() 호출
 * 	9. DispatcherServlet → WAS 반환
 * 	10. WAS → 브라우저에 정상 응답 전송
 * 
 * 
 * [2] 예외 발생 흐름 - throw new RuntimeException()
 * 
 * 1. HTTP 요청 → WAS(Tomcat)
 * 2. WAS → DispatcherServlet 진입
 * 3. DispatcherServlet → 인터셉터의 preHandle() 호출됨
 * 4. Controller 실행 중 예외 발생 (ex: throw new RuntimeException)
 *    → postHandle() 은 호출되지 않음 (정상 흐름 아님)
 * 5. DispatcherServlet → afterCompletion(ex) 호출됨
 *    → 전달된 ex는 예외 객체 (ex != null)
 * 6. DispatcherServlet → 예외 WAS로 전달
 * 7. WAS → 등록된 ErrorPage 확인 후 /error-page/500 으로 **내부 디스패치**
 *    → DispatcherType = ERROR 로 변경됨
 * 8. DispatcherServlet 재실행 (ERROR 디스패치로)
 *    → 인터셉터 preHandle() 호출됨 (DispatcherType = ERROR)
 *    → ErrorPageController 실행
 *    → postHandle() 호출됨
 *    → 오류 View 렌더링
 *    → afterCompletion() 호출됨
 *
 *
 * [3] 오류 상태코드 전송 흐름 - response.sendError(404 or 500)
 * 
 * 1. HTTP 요청 → WAS(Tomcat)
 * 2. WAS → DispatcherServlet 진입
 * 3. DispatcherServlet → 인터셉터의 preHandle() 호출됨
 * 4. Controller → response.sendError(404) 호출 (예외 발생 아님)
 *    → postHandle() 은 호출됨 (정상 흐름이므로)
 * 5. DispatcherServlet → afterCompletion(ex) 호출됨
 *    → 전달된 ex는 없음 (ex == null)
 * 6. DispatcherServlet → 응답 상태 코드를 WAS에 전달
 * 7. WAS → 등록된 ErrorPage 확인 후 /error-page/404 로 **내부 디스패치**
 *    → DispatcherType = ERROR 로 변경됨
 * 8. DispatcherServlet 재실행 (ERROR 디스패치로)
 *    → 인터셉터 preHandle() 호출됨 (DispatcherType = ERROR)
 *    → ErrorPageController 실행
 *    → postHandle() 호출됨
 *    → 오류 View 렌더링
 *    → afterCompletion() 호출됨
 */

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

	private static final String LOG_ID = "Log_ID";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println();
		String requestURI = request.getRequestURI();
		String uuid = UUID.randomUUID().toString();

		request.setAttribute(LOG_ID, uuid);
		logRequest(uuid, request.getDispatcherType(), requestURI, handler.getClass().getSimpleName());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		log.info("POST HANDLE - ModelAndView : {}", modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
		String requestURI = request.getRequestURI();
		String logID = (String) request.getAttribute(LOG_ID);
		logResponse(logID, request.getDispatcherType(), requestURI);
		
		if (ex != null) {
			log.error("AFTER COMPLETION - Exception occurred", ex);
		}
		System.out.println();
	}

	private void logRequest(String logId, Object dispatcherType, String uri, Object handler) {
		log.info("REQUEST  [{}][{}][{}][{}]", logId, dispatcherType, uri, handler);
	}

	private void logResponse(String logId, Object dispatcherType, String uri) {
		log.info("RESPONSE [{}][{}][{}]", logId, dispatcherType, uri);
	}

}
