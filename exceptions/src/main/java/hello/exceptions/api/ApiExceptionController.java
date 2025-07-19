package hello.exceptions.api;

import org.springframework.web.bind.annotation.*;

@RestController
public class ApiExceptionController {

	@GetMapping("/api/members/{id}")
	public MemberDto getMember(@PathVariable("id") String id) {
		if (id.equals("ex")) {
			throw new RuntimeException("서버 오류 발생");
		}
		if (id.equals("bad")) {
			throw new IllegalArgumentException("잘못된 입력값입니다");
		}
		if (id.equals("user")) {
			throw new UserException("사용자 오류입니다");
		}
		return new MemberDto(id, "hello " + id);
	}
}
