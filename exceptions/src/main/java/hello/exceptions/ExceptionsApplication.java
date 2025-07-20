package hello.exceptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 *   [주요 컴포넌트 별 역할]
 *
 * 1. Filter : 서블릿 레벨의 기능, 요청 전/후 외부 요청 전체 감싸기
 * 2. DispacherServlet : 스프링 웹의 중앙 컨트롤러, 모든 요청은 이곳을 거쳐감
 * 3. HandlerInterceptor : 스프링 내부 요청 흐름 제어, 인증 권한 검사등 비즈니스 로직 전처리
 * 4. Controller : 컨트롤러 실질적인 요청 처리코드
 * 5. ViewResolver : 뷰 이름을 HTML 파일 경로로 변환
 * 6. View : HTML, JSON 등 클라이언트로 전달될 실제 응답 데이터 생성
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
