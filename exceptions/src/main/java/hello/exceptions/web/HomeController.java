package hello.exceptions.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import hello.exceptions.argumentresolver.Login;
import hello.exceptions.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 
 * 요약 흐름도 (전체 요청 흐름)
 * 
 * [클라이언트] ─▶ [LogFilter] ─▶ [LoginCheckFilter] ─▶ [DispatcherServlet]
 *        ─▶ [HandlerMapping → Controller] ─▶ [ArgumentResolver(@Login)]
 *        ─▶ [Controller 메서드 실행] ─▶ [ViewResolver → View 렌더링]
 *        ◀────────────────────────────────────────────
 *            [LoginCheckFilter 종료] ◀── [LogFilter 종료]
 * 
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

	/**
	 * @Login 애너테이션을 붙인 파라미터가 있으면,
	 * 스프링 MVC가 컨트롤러 호출 시 LoginMemberArgumentResolver가 자동으로 동작하여
	 * 세션에서 Member 객체를 꺼내 해당 파라미터에 주입해준다.
	 * 
	 * 따라서 컨트롤러 내부에서는 로그인 여부 체크를 간단하게 null 체크로 할 수 있다.
	 */
	@GetMapping
	public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model) {
		
		if(loginMember == null) {  // 세션에 로그인 정보가 없으면 홈 화면으로 이동
			return "home";
		}
		
		model.addAttribute("member", loginMember); // 로그인된 회원 정보를 모델에 담아 뷰에 전달
		return "loginHome";  // 로그인 홈 화면으로 이동
	}

	
//	private final MemberRepository memberRepository;
//	private final SessionManager sessionManager;
	
//	@GetMapping
//	public String homeLoginV3Spring(
//			@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) 
//			Member loginMember, Model model) {
//
//		// 스프링에서 기본 제공하는 @SessionAttribute 애노테이션을 사용해 세션에서 바로 가져옴
//		if (loginMember == null) {
//			return "home";  // 세션에 로그인 정보 없으면 홈으로
//		}
//
//		model.addAttribute("member", loginMember);
//		return "loginHome";
//	}
	
//	@GetMapping
//	public String homeLoginV3(HttpServletRequest request, Model model) {
//		// HttpServletRequest에서 직접 세션을 얻어와 로그인 정보 확인
//		HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
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
//		// 직접 만든 SessionManager를 사용해 세션에서 로그인 회원 정보 조회
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
//		// 쿠키에 있는 memberId 값을 이용해 로그인 회원 조회 (초기 로그인 구현 방식)
//		if(memberId == null) {
//			return "home";
//		}
//
//		return memberRepository.findById(memberId)
//				.map(loginMember -> {
//						model.addAttribute("member", loginMember);
//						return "loginHome";
//					})
//				.orElse("home");  // memberId가 DB에 없으면 홈으로
//	}
	
}
