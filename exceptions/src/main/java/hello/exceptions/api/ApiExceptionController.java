package hello.exceptions.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.exceptions.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * [요청 흐름 요약]
 * 
 * 클라이언트 요청
 *  → 필터
 *   → 인터셉터 (preHandle)
 *    → 컨트롤러 진입 중 예외 발생
 *      → DispatcherServlet이 HandlerExceptionResolver 호출
 *        → (Accept 헤더 따라 JSON or View)
 *           [1] IllegalArgumentException → MyHandlerExceptionResolver가 처리 
 *           [2] UserException           → UserHandlerExceptionResolver가 처리
 *           [3] 그 외 예외               → 기본 오류 처리 /error 로 내부 포워딩
 *    → 인터셉터 (afterCompletion)
 *  → 필터 → 응답 반환
 */

@Slf4j
@RestController
@RequestMapping("/api/members")
public class ApiExceptionController {

	
    /**
     * 테스트용 API
     * 요청 URL 예: /api/members/ex, /api/members/bad, /api/members/user
     */
	@GetMapping("/{id}")
	public MemberDto getMember(@PathVariable("id") String id) {
		
		if("ex".equals(id)) {
            // RuntimeException 발생 → 아무 Resolver도 처리 못하므로 기본 오류 처리 (500)
			throw new RuntimeException("잘못된 사용자");
		}
		if("bad".equals(id)) {
			// IllegalArgumentException 발생 → MyHandlerExceptionResolver가 처리 (400 + JSON 또는 HTML)
			throw new IllegalArgumentException("잘못된 입력값");
		}
		if("user".equals(id)) {
            // UserException 발생 → UserHandlerExceptionResolver가 처리 (400 + JSON 또는 HTML)
			throw new UserException("사용자 오류");
		}
		
		// 예외 없을 경우 정상 응답
		return new MemberDto(id, "hello " + id);
	}

	@Data
	@AllArgsConstructor
	static class MemberDto {
		private String memberId;
		private String name;
	}
}
