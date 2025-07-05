package hello.login.domain.member;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hello.login.web.member.form.MemberForm;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

	private final MemberRepository memberRepository;

	@GetMapping("/add")
	public String showAddForm(MemberForm memberForm) {
		return "members/addMemberForm";
	}

	@PostMapping("/add")
	public String addMember(@Validated MemberForm memberForm, BindingResult bindingResult) {

		String loginId = memberForm.getLoginId();

		// 중복 로그인 아이디 검증
		if (memberRepository.isDuplicateLoginId(loginId)) {
			bindingResult.reject("duplicatedLoginId", new Object[] { loginId }, null);
		}
		
		// 검증 실패 시 다시 폼으로
		if (bindingResult.hasErrors()) {
			return "members/addMemberForm";
		}
		
		// 저장
		memberRepository.saveMember(memberForm);
		return "redirect:/";
	}

}
