package hello.exceptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ✅ [Spring MVC 핵심 요청 처리 컴포넌트 상세 설명] 
 * 
 * 1. Filter
 *  - WAS 수준의 서블릿 필터로, 모든 HTTP 요청/응답을 가장 먼저 가로챔
 *  - 인증, 로깅, XSS 방지, CORS 처리, GZIP 압축 등의 전처리에 자주 사용
 *  - chain.doFilter(request, response) 호출 전/후에 전후처리 가능
 *  - DispatcherServlet 진입 전 필터링, ServletContainer에 의해 실행됨
 *  
 * 2. DispatcherServlet
 *  - 스프링 웹 MVC의 핵심 프론트 컨트롤러 (Front Controller 패턴)
 *  - 모든 요청을 받아 HandlerMapping, HandlerAdapter, ViewResolver 등으로 위임
 *  - 예외가 발생하면 HandlerExceptionResolver 목록을 실행해 예외 처리
 *  - Filter 를 지난 후 최초로 진입하는 스프링 내부 진입 지점
 *  
 * 3. HandlerMapping
 *  - 어떤 컨트롤러가 요청을 처리할지 결정 (ex. @RequestMapping 기반 매핑)
 *  - URL, HTTP 메서드, 헤더 등을 기준으로 적절한 핸들러(Controller 메서드)를 탐색
 *  
 * 4. HandlerAdapter
 *  - 매핑된 핸들러를 실행하는 방법을 정의 (ex. @Controller, @RestController 등 지원)
 *  - HandlerExecutionChain을 받아 실제 컨트롤러 메서드 실행
 * 
 * 5. HandlerIntereptor (스프링 인터셉터)
 *  - DispatcherServlet과 Controller 사이에서 동작
 *  - preHandle() : 컨트롤러 호출 전 실행
 *  - postHandle() : 컨트롤러 실행 후,  View 렌더링 전 실행
 *  - afterCompletion() : View 렌더링 후 실행 (예외 포함 최종 정리 단계)
 *  - 인증, 권한 체크, 공통 로그 추적, 성능 측정 등에 사용
 *  
 * 6. Controller (ex. @Controller, @RestController)
 *  - 사용자의 요청을 실제로 처리하는 비즈니스 로직 작성 위치
 *  - 요청 파라미터 바인딩, 서비스 호출, 결과 반환 등의 역할
 *  - @ResponseBody 또는 @RestController 는 뷰를 거치지 않고 JSON 직접 반환
 *  
 * 7. ExceptionResolver (HandlerExceptionResolver)
 *  - 컨트롤러나 인터셉터에서 예외가 발생했을 때 이를 처리함
 *  - @ExceptioHandler, ResponseStatusExceptionResolver, DefaultExceptionResolver 등이 기본 제공됨
 *  - 사용자 정의 Resolver 를 통해 JSON 또는 HTML 반환 방식 커스터마이징 가능
 *  
 * 8. ViewResolver 
 *  - 컨트롤러가 반환한 논리 뷰 이름(예: "error/404")을 실제 물리 뷰 경로(예: /WEB-INF/views/error/404.html)로 변환
 *  - 기본은 ThymeleafViewResolver, InternalResourceViewResolver (JSP) 등
 *  
 * 9. View
 *  - ViewResolver가 찾은 템플릿을 렌더링해 응답을 생성
 *  - HTML, JSON, XML 등 디양한 형태의 응답 생성 가능
 *  - REST API의 경우에는 View 단계 없이 메세지 컨버터(HttpMessageConverter)로 바로 JSON 응답
 *  
 * 10. HttpMessageConverter (REST 전용)
 *  - @ResponseBody 또는 @RestController와 함께 사용됨
 *  - JSON <-> 객체 간의 변환 등을 자동 처리 
 *  - HTTP 요청/응답 본문을 직접 처리하는 역할
 *  
 * 11. ArgumentResolver (HandlerMethodArgumentResolver)
 *  - 컨트롤러 메서드 파라미터를 스프링이 자동으로 주입해주는 기능
 *  - @RequestParam, @ModelAttribute, @RequestBody, @PathVariable 등을 처리
 *  - 사용자 정의 리졸버를 통해 로그인 정보 등 커스텀 객체 주입도 가능 (ex. @LoginUser)
 *  
 * 12. ModelAndView
 *  - 컨트롤러가 반환할 수 있는 객체로, 뷰 이름 + 모델 데이터를 함께 담음
 *  - 뷰 이름은 ViewResolver로 전달되고, 모델은 템플릿 렌더링에 사용됨
 * 
 */

/**
 * DispatcherServlet 기본 요청 흐름
 * 
 * [HTTP 요청] 
 * 		↓ 
 * [WAS (Tomcat 등)] 
 * 		↓ 
 * [DispatcherServlet] ← 스프링 MVC 진입 지점 
 * 		↓
 * [HandlerMapping] → 어떤 컨트롤러가 처리할지 찾음 
 * 		↓ 
 * [HandlerAdapter] → 그 컨트롤러 호출 
 * 		↓
 * [Controller (예: @GetMapping 등)] 
 * 		↓ 
 * [ViewResolver + View] → 응답 렌더링 
 * 		↓
 * [DispatcherServlet → WAS → 클라이언트]
 * 
 */
@SpringBootApplication
public class ExceptionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExceptionsApplication.class, args);
	}

}
