package hello.login.domain.member;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hello.login.web.member.form.MemberForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원 등록을 처리하는 웹 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;

	/**
	 * 회원 가입 폼 화면 반환
	 * GET /members/add
	 */
	@GetMapping("/add")
	public String showAddForm(MemberForm memberForm) {
//		log.info("[GET /members/add] 회원 가입 폼 요청");
		return "members/addMemberForm"; // View: members/addMemberForm.html
	}

	/**
	 * 회원 등록 처리
	 * POST /members/add
	 */
	@PostMapping("/add")
	public String addMember(@Validated MemberForm memberForm,
							BindingResult bindingResult) {

		// 입력된 로그인 ID 전처리 (앞뒤 공백 제거)
		String loginId = memberForm.getLoginId().trim();
//		log.info("[POST /members/add] 회원 등록 요청: loginId={}", loginId);

		// 중복 로그인 ID 검사
		if (memberService.isDuplicateLoginId(loginId)) {
//			log.info("중복 로그인 ID 감지: {}", loginId);
			bindingResult.reject("duplicatedLoginId", new Object[]{loginId}, null);
		}

		// 검증 오류 시 회원 가입 폼 다시 보여줌
		if (bindingResult.hasErrors()) {
//			log.info("회원 등록 검증 실패: {}", bindingResult);
			return "members/addMemberForm";
		}

		// 회원 저장
		memberService.saveMember(memberForm);
//		log.info("회원 등록 성공: loginId={}", loginId);
		return "redirect:/";
	}
}
