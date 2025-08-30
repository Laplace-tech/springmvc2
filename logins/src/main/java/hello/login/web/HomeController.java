package hello.login.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import hello.login.argumentresolver.Login;
import hello.login.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

//	private final MemberRepository memberRepository;
//	private final SessionManager sessionManager;

	/*
	 * 아래처럼 @Login 애너테이션을 붙인 파라미터가 있으면, LoginMemberArgumentResolver가 자동으로
	 * 동작해서 세션에서 Member 객체를 꺼내 loginMember에 주입한다.
	 */
	@GetMapping
	public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model) {
		
		if(loginMember == null) {
			return "home";
		}
		
		model.addAttribute("member", loginMember);
		return "loginHome";
	}
	
//	@GetMapping
//	public String homeLoginV3Spring(
//			@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) 
//			Member loginMember, Model model) {
//
//		if (loginMember == null) {
//			return "home";
//		}
//
//		model.addAttribute("member", loginMember);
//		return "loginHome";
//	}
	
//	@GetMapping
//	public String homeLoginV3(HttpServletRequest request, Model model) {
//		HttpSession session = request.getSession(false);
//		if(session == null) {
//			return "home";
//		}
//		
//		Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
//		if(loginMember == null) {
//			return "home";
//		}
//		
//		model.addAttribute("member", loginMember);
//		return "loginHome";
//	}
	
//	@GetMapping
//	public String homeLoginV2(Model model, HttpServletRequest request) {
//		Member loginMember = (Member) sessionManager.getSession(request);
//
//		if (loginMember == null) {
//			return "home";
//		} else {
//			model.addAttribute("member", loginMember);
//			return "loginHome";
//		}
//	}
	
//	@GetMapping
//	public String homeLoginV1(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
//		if(memberId == null) {
//			return "home";
//		}
//
//		return memberRepository.findById(memberId)
//				.map(loginMember -> {
//						model.addAttribute("member", loginMember);
//						return "loginHome";
//					})
//				.orElse("home");
//	}
	
}
