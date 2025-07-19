package hello.exceptions.servlet;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * ✅ [1단계: 최초 요청 처리 중 예외 발생]
 * 
 * 1. 클라이언트가 /example 경로로 요청을 보냄
 * 2. DispatcherServlet이 요청을 처리하기 위해 컨트롤러를 호출
 * 3. 컨트롤러 실행 도중 예외 발생 (예: RuntimeException)
 *    - 이 경우 스프링 MVC는 예외를 처리하지 않고 WAS로 예외를 전달함
 * 4. WAS (예: Tomcat)가 예외를 감지하고, 등록된 기본 에러 페이지 경로인 "/error"로 내부 디스패치함
 *    - 이때 DispatcherType은 ERROR로 설정됨
 * 
 * 		**내부 요청 디스패치** : 클라이언트(브라우저)가 새로운 요청을 보내지는 않았지만 
 *     		서버 내부에서 같은 어플리케이션 안의 다른 경로로 요청을 전달(포워딩)하는 동작
 *      	 └▶ 현재 요청 안에서, 지정된 오류 경로로 내부적으로 포워딩됨
 *      	 └▶ DispatcherType = ERROR로 변경됨
 *      
 * -------------------------------------------------------------------
 * 
 * ✅ [2단계: 스프링 부트의 기본 오류 처리]
 * 
 * 5. DispatcherServlet이 "/error" 경로를 다시 처리함 (DispatcherType = ERROR)
 * 6. 스프링 부트가 자동 등록한 기본 오류 처리 컨트롤러인 `BasicErrorController`가 실행됨
 *    - `BasicErrorController`는 스프링 부트에서 제공하는 컨트롤러로, 오류 정보를 자동 수집하고 응답을 생성함
 * 7. 응답 방식 결정
 *    - 요청 헤더의 `Accept` 값이 `text/html`이면: HTML 오류 페이지를 렌더링
 *      → 예: `resources/templates/error/500.html` 이 있으면 렌더링됨
 *      → 없으면 스프링 부트가 제공하는 기본 HTML 오류 페이지를 보여줌
 *    - `Accept`가 JSON 또는 기타일 경우: JSON 형태로 에러 정보를 응답함
 *      → {"timestamp": "...", "status": 500, "error": "Internal Server Error", ...}
 * 
 * 8. 최종 오류 응답이 클라이언트에 전송됨
 * 
 * --------------------------------------------------------------------
 * 
 * [HTML 오류 페이지 응답 메서드]
 * 
 * @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
 * public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
 *   return null; // 실제 구현에서는 ModelAndView 반환
 * }
 * 
 * 클라이언트의 요청 헤더에 Accept: text/html이 포함된 경우 이 메서드가 실행됩니다.
 * 주로 웹 브라우저에서 요청할 때 사용되며, 오류 HTML 뷰를 렌더링하여 사용자에게 보여줍니다.
 *
 * 내부적으로 다음 경로의 뷰 템플릿을 찾습니다:
 *  - templates/error/{status}.html
 *  - templates/error/4xx.html
 *  - templates/error/5xx.html
 *
 * 예시:
 *   GET /not-found
 *   Accept: text/html
 *   → 이 메서드 실행 → error/404.html 뷰 반환
 *
 * ----------------------------------------------------------------------------
 * 
 * [JSON 오류 응답 메서드]
 * 
 * @RequestMapping
 * public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
 *   return null; // 실제 구현에서는 ResponseEntity 반환
 * }
 * 
 * 클라이언트의 요청 헤더에 Accept: application/json 또는 기타 비-HTML 요청일 경우 이 메서드가 실행됩니다.
 * 주로 API 클라이언트(axios, fetch, Postman 등)에서 호출되며, JSON 형식의 오류 정보를 응답합니다.
 *
 * 반환 형식은 ResponseEntity<Map<String, Object>>이며, 다음 정보를 포함합니다:
 *  - status: HTTP 상태 코드 (예: 404)
 *  - error: 오류명 (예: "Not Found")
 *  - message: 오류 메시지 등
 *
 * 예시:
 *   GET /not-found
 *   Accept: application/json
 *   → 이 메서드 실행 → { "status": 404, "error": "Not Found", ... } JSON 반환
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

	@Deprecated
	@GetMapping(value = "/error-page/500", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> errorPage500Api(HttpServletRequest request) {
	    
		log.info("[500 JSON-ERROR PAGE] 진입");
		
		int statusCode = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
	    String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

	    Map<String, Object> errorBody = Map.of(
	        "status", statusCode,
	        "error", (exception != null ? exception.getClass().getSimpleName() : "UnknownError"),
	        "message", (exception != null ? exception.getMessage() : "Internal Server Error"),
	        "path", (requestUri != null ? requestUri : "N/A"),
	        "timestamp", LocalDateTime.now().toString()
	    );

	    return ResponseEntity.status(statusCode).body(errorBody);
	}
	
	@Deprecated
	@GetMapping("/error-page/404")
	public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
		log.info("🛑 [404 ERROR PAGE] 진입");
		return "error/404";
	}

	@Deprecated
	@GetMapping("/error-page/500")
	public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
		log.info("💥 [500 ERROR PAGE] 진입");
		return "error/500";
	}

	/**
	 * request에 포함된 오류 정보를 로그로 출력
	 */
	@SuppressWarnings("unused")
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
