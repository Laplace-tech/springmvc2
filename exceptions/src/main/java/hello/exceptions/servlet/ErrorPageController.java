package hello.exceptions.servlet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*
 * 1. [예외 발생 흐름]
 *  
 *   HTTP 요청 시작
 * 	  └▶ 필터 (Filter)            ← DispatcherType = REQUEST
 *      └▶ 서블릿 (DispatcherServlet)
 *        └▶ 스프링 인터셉터 (HandlerInterceptor)
 *          └▶ 컨트롤러 (Controller)
 *             └▶ 예외 발생 또는 response.sendError(404, 500 등)
 *               └▶ 예외가 WAS(Tomcat)까지 전달됨
 *
 *  
 *  2. [WAS 내부 오류 처리]
 *	
 * 	 WAS 내부에서 예외 수신
 *    └▶ 등록된 ErrorPage 확인
 *       ├─ 예: new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500")
 *       └▶ 오류에 해당하는 경로로 **내부 요청 디스패치** 수행
 *
 *    
 *    **내부 요청 디스패치** : 클라이언트(브라우저)가 새로운 요청을 보내지는 않았지만 
 *     서버 내부에서 같은 어플리케이션 안의 다른 경로로 요청을 전달(포워딩)하는 동작
 *      └▶ 현재 요청 안에서, 지정된 오류 경로로 내부적으로 포워딩됨
 *      └▶ DispatcherType = ERROR로 변경됨
 *     
 *  3. [오류 페이지 요청 흐름]
 *  
 *  내부 요청: /error/500 ← DispatcherType = ERROR
 *    └▶ 필터 (Filter) → DispatcherType = ERROR
 *      └▶ 서블릿 (DispatcherServlet)
 *        └▶ 스프링 인터셉터 (HandlerInterceptor)
 *          └▶ 오류 처리 컨트롤러 실행 (ErrorPageController 등)
 *            └▶ 뷰(View) 반환 및 렌더링
 *
 */

@Slf4j
@Controller
public class ErrorPageController {

	// 서블릿 예외 처리 관련 표준 속성 상수
	public static final String ATTR_EXCEPTION = RequestDispatcher.ERROR_EXCEPTION;
	public static final String ATTR_EXCEPTION_TYPE = RequestDispatcher.ERROR_EXCEPTION_TYPE;
	public static final String ATTR_MESSAGE = RequestDispatcher.ERROR_MESSAGE;
	public static final String ATTR_REQUEST_URI = RequestDispatcher.ERROR_REQUEST_URI;
	public static final String ATTR_SERVLET_NAME = RequestDispatcher.ERROR_SERVLET_NAME;
	public static final String ATTR_STATUS_CODE = RequestDispatcher.ERROR_STATUS_CODE;

	/**
	 * 404 오류 처리 페이지
	 */
	@GetMapping("/error-page/404")
	public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
		log.info("🛑 [404 ERROR PAGE] 진입");
		return "error-page/404";
	}

	/**
	 * 500 오류 처리 페이지
	 */
	@GetMapping("/error-page/500")
	public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
		log.info("💥 [500 ERROR PAGE] 진입");
		return "error-page/500";
	}

	/**
	 * request에 포함된 오류 정보를 로그로 출력
	 */
	private void printErrorInfo(HttpServletRequest request) {
		log.info("✔️ dispatchType: {}", request.getDispatcherType());
		log.info("✔️ statusCode: {}", request.getAttribute(ATTR_STATUS_CODE));
		log.info("✔️ requestURI: {}", request.getAttribute(ATTR_REQUEST_URI));
		log.info("✔️ servletName: {}", request.getAttribute(ATTR_SERVLET_NAME));
		log.info("✔️ exceptionType: {}", request.getAttribute(ATTR_EXCEPTION_TYPE));
		log.info("✔️ message: {}", request.getAttribute(ATTR_MESSAGE));

		Throwable ex = (Throwable) request.getAttribute(ATTR_EXCEPTION);
		if (ex != null) {
			log.info("✔️ exception: ", ex);
		}
	}
}
