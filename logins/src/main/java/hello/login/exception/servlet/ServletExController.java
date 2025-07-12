package hello.login.exception.servlet;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

/*
 * [요청 처리 흐름]
 *   클라이언트 요청
 *     → WAS(Tomcat)
 *     → 필터(Filter)
 *     → 서블릿(DispatcherServlet)
 *     → 스프링 인터셉터
 *     → 컨트롤러(@Controller)
 *     
 * [서블릿 수준의 예외처리 방식 2가지]
 *   1. ❗ 예외(Exception) 발생
 *      - 컨트롤러 또는 필터 등에서 예외를 던지면 (ex. throw new RuntimeException())
 *      - 예외는 WAS까지 전달되고, 최종적으로 WAS가 예외를 처리함
 *      - 이 경우 HTTP 상태 코드 500을 포함한 기본 오류 페이지가 보여짐
 *    
 *   2. ❗ sendError() 호출
 *      - 예외를 직접 던지지 않고, response.sendError(상태코드, 메시지) 호출
 *      - 이 메서드는 WAS에게 오류가 발생했다고 알려주는 역할
 *      - WAS는 상태 코드에 맞는 오류 페이지를 출력함
 *    
 */
@Controller
public class ServletExController {

	/*
	 * [Notice] 
	 * 1. 서블릿 예외 처리는 톰캣이 해주는 기본 기능
	 * 2. 스프링이 잡지 못한 예외는 결국 WAS가 처리한다
	 * 3. 예외를 직접 던지거나, sendError()로 상태 코드를 설정하면 톰캣이 알아서 페이지를 보여준다
	 * 4. 기본적으로 404, 500 등에 대해 톰캣이 HTML로 설명 페이지를 응답한다
	 */
	
    // 예외를 직접 발생시킴 → WAS가 잡아서 500 오류 페이지를 출력
	@GetMapping("/error-ex")
	public void errorEx() {
		throw new RuntimeException("예외 발생!");
	}
	
    // 명시적으로 404 오류 상태 코드 전송 → WAS가 기본 404 페이지 출력
	@GetMapping("/error-404")
	public void error404(HttpServletResponse response) throws IOException { 
		response.sendError(404, "404 오류");
	}
	
    // 명시적으로 400 오류 상태 코드 전송 → WAS가 기본 400 페이지 출력
	@GetMapping("/error-400")
	public void error400(HttpServletResponse response) throws IOException {
		response.sendError(400, "400 오류");
	}
	
    // 명시적으로 500 오류 상태 코드 전송 → WAS가 기본 500 페이지 출력
	@GetMapping("/error-500")
	public void error500(HttpServletResponse response) throws IOException {
		response.sendError(500, "500 오류");
	}
	
}
