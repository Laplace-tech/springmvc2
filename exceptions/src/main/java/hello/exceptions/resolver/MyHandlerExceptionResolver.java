package hello.exceptions.resolver;

import hello.exceptions.api.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * 최종 흐름 정리 (예외가 발생했을 때) 
 * 
 * [1] 클라이언트 요청 
 * 		↓ 
 * [2] DispatcherServlet 진입 
 * 		↓ 
 * [3] LogInterceptor.preHandle() 
 * 		↓ 
 * [4] 컨트롤러 호출 중 ❗ 예외 발생 
 * 		↓ 
 * [5] DispatcherServlet → HandlerExceptionResolver 목록 순회 
 * 		└─ MyHandlerExceptionResolver (직접 구현한 커스텀 리졸버)
 * 			- 처리 가능하면 JSON 응답, 상태코드 설정 → ModelAndView 리턴 → 끝 
 * 			- 처리 불가 → return null 
 * 		↓ 
 * [6] 다른 리졸버가 없거나 null 이면 → /error 로 포워딩 (예외 리졸버가 아무도 처리 못 했을 때)
 * 		└─ BasicErrorController 
 * 			- HTML 요청: error.html 
 * 			- API 요청: JSON 응답 
 * 		↓ 
 * [7] LogInterceptor.afterCompletion() 호출됨 
 * 		↓ 
 * [8] 응답 전송
 * 
 * ---------------------------------------------------------------------------
 * 
 * ✅ resolveException() 리턴값 의미
 * 
 * 리턴값                 의미
 * null                 "나는 처리 안 했음, 다음 리졸버에게 넘겨"
 * new ModelAndView()   "내가 처리했어! 더 이상 다른 리졸버, 에러 페이지 호출하지 마"
 * new ModelAndView("errorPage") "뷰도 내가 정했어! 이 페이지 보여줘"
 * 
 * ✔️ ModelAndView는 꼭 뷰 이름이 들어가야 하는 건 아님
 * JSON 응답일 경우 그냥 new ModelAndView()만 리턴하면 됨 (뷰 렌더링 없이 응답만 끝내겠다는 뜻)
 */


@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {
	
//   JSON이 아닌 뷰가 렌더링됨
//	 @Override
//	    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//	      
//	        try {
//	            if (ex instanceof IllegalArgumentException) {
//	                log.info("IllegalArgumentException resolver to 400");
//	                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
//	                return new ModelAndView();
//	            }
//	        } catch (IOException e) {
//	            log.error("resolver ex", e);
//	        }
//	      
//	        return null;
//	    }
	
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
										 HttpServletResponse response,
										 Object handler,
										 Exception ex) {

		log.info("MyHandlerExceptionResolver triggered", ex);

		try {
			if (ex instanceof IllegalArgumentException) {
				log.warn("IllegalArgumentException: {}", ex.getMessage());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
				writeJson(response, "입력 오류", ex.getMessage());
				return new ModelAndView(); // 예외 처리 완료!
			}

			if (ex instanceof UserException) {
				log.warn("UserException: {}", ex.getMessage());
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
				writeJson(response, "사용자 오류", ex.getMessage());
				return new ModelAndView(); // 예외 처리 완료! 
			}

		} catch (IOException ioEx) {
			log.error("Response write 실패", ioEx);
		}

		return null; // 다른 Resolver 또는 Spring 기본 처리
	}

	private void writeJson(HttpServletResponse response, String errorType, String message) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");

		String json = String.format("{\"errorType\": \"%s\", \"message\": \"%s\"}", errorType, message);
		response.getWriter().write(json);
	}
}
