package hello.exceptions.domain.member;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hello.exceptions.web.member.form.MemberForm;
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
	 */
	@GetMapping("/add")
	public String showAddForm(MemberForm memberForm) {
		return "members/addMemberForm"; // View: members/addMemberForm.html
	}

	/**
	 * 회원 등록 처리
	 */
	@PostMapping("/add")
	public String addMember(@Validated MemberForm memberForm,
							BindingResult bindingResult) {

		String loginId = memberForm.getLoginId().trim();

		if (memberService.isDuplicateLoginId(loginId)) {
			bindingResult.reject("duplicatedLoginId", new Object[]{loginId}, null);
		}

		if (bindingResult.hasErrors()) {
			return "members/addMemberForm";
		}

		memberService.saveMember(memberForm);
		return "redirect:/";
	}
}
