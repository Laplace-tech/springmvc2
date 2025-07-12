package hello.login.exception.servlet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//
//	[1] 사용자의 HTTP 요청
//	↓
//	[2] DispatcherServlet → Controller(요청 처리 시도)
//	↓
//	[3-1] (예외 발생)
//	 → 예외를 throw: throw new RuntimeException("에러");
// 	 → 또는 sendError() 호출: response.sendError(500);
//	↓
//	[3-2] Spring 예외 처리 불가 (스프링의 @ExceptionHandler 또는 @ControllerAdvice가 처리 못함)
//	↓
//	[4] 예외가 WAS(Tomcat)까지 전달됨
//	↓
//	[5] WAS는 내부 설정 또는 WebServerCustomizer에서 등록된 ErrorPage 정보를 사용하여
//	해당 상태 코드나 예외 타입에 맞는 오류 페이지 경로로 포워딩
//	예: 
//  	- 404 → "/error-page/404"
//  	- 500 → "/error-page/500"
//  	- RuntimeException → "/error-page/500"
//	↓
//	[6] 해당 경로를 처리할 컨트롤러(ErrorPageController) 호출됨
//	↓
//	[7] ErrorPageController는 request 객체에서 오류 관련 정보를 꺼내고, 로그 남기고
//	↓
//	[8] 지정된 뷰 템플릿 반환 (예: error-page/404.html, error-page/500.html)
//	↓
//	[9] 사용자에게 오류 페이지 HTML 응답됨


@Controller
@Slf4j
public class ErrorPageController {

	/*
	 * WAS가 WebServerCustomizer 설정에 따라 /error-page/404로 forward 이때 WAS는 요청
	 * 객체(HttpServletRequest)에 여러 정보를 setAttribute()하여 이곳까지 넘긴다.
	 * 
	 * Attribute 이름 (Key) 의미 javax.servlet.error.status_code HTTP 상태 코드 (예: 404)
	 * javax.servlet.error.exception 예외 객체 (500에 해당) javax.servlet.error.message 에러
	 * 메시지 javax.servlet.error.request_uri 원래 요청 URI
	 * javax.servlet.error.servlet_name 서블릿 이름
	 * 
	 * 이 정보는 모두 HttpServletRequest의 getAttribute()로 꺼낼 수 있음.
	 * 
	 * 
	 * 
	 * 서블릿 예외 처리 - 오류 페이지 작동 원리 예외 발생 흐름 
	 * WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
	 * 
	 * sendError 흐름 
	 * WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(response.sendError()) 
	 * 
	 * WAS는 해당 예외를 처리하는 오류 페이지 정보를 확인
	 * new ErrorPage(RuntimeException.class, "/error-page/500")
	 * 
	 * RuntimeException 예외가 WAS까지 전달되면, WAS는 오류 페이지 정보를 확인 
	 * 확인해보니 RuntimeException 의 오류 페이지로 /error-page/500이 지정 
	 * WAS는 오류 페이지를 출력하기 위해 /error-page/500를 다시 요청
	 * 
	 * 
	 * 오류 페이지 요청 흐름
	 * WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500) -> View
	 * 
	 * 
	 * 예외 발생과 오류 페이지 요청 흐름 
	 * 1. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생) 
	 * 2. WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/errorpage/500) -> View
	 * 
	 * 중요한 점은 웹 브라우저(클라이언트)는 서버 내부에서 이런 일이 일어나는지 전혀 모른다는 점이다.
	 * 오직 서버 내부에서 오류 페이지를 찾기 위해 추가적인 호출을 진행
	 * 
	 * 정리 
	 * 1. 예외가 발생해서 WAS까지 전파 
	 * 2. WAS는 오류 페이지 경로를 찾아서 내부에서 오류 페이지를 호출. 
	 * 이때 오류 페이지 경로로 필터, 서블릿, 인터셉터, 컨트롤러가 모두 다시 호출
	 * 
	 */
    @RequestMapping("/error-page/404")
    public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 404");
        return "error-page/404";
    }

    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 500");
        return "error-page/500";
    }
}

