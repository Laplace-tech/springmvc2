package hello.exceptions.domain.login;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hello.exceptions.domain.member.Member;
import hello.exceptions.session.SessionConst;
import hello.exceptions.web.login.form.LoginForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

	private final LoginService loginService;
//	private final SessionManager sessionManager;
	
	/*
	 * 로그인 폼 페이지 반환
	 */
	@GetMapping("/login")
	public String loginForm(LoginForm loginForm) {
		return "login/loginForm";
	}
	
	@PostMapping("/login")
	public String loginV4(
			@Validated LoginForm loginForm, BindingResult bindingResult, 
			@RequestParam(value = "redirectURL", defaultValue = "/") String redirectURL, 
			HttpServletRequest request) {
		
		if(bindingResult.hasErrors()) { 
			return "login/loginForm";
		}
		
		Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
		
		if(loginMember == null) {
			bindingResult.reject("login.form.loginFail");
			return "login/loginForm";
		}
		
		HttpSession session = request.getSession();
		session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
		return "redirect:" + redirectURL;
	}
	
	@PostMapping("/logout")
	public String logoutV3(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		return "redirect:/";
	}
	
//	@PostMapping("/login")
//	public String loginV3(@Validated LoginForm loginForm, 
//			BindingResult bindingResult, HttpServletRequest request) {
//		log.info("[POST /login] 로그인 처리 요청");
//		if (bindingResult.hasErrors()) {
//			return "login/loginForm";
//		}
//		Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
//		if (loginMember == null) {
//			bindingResult.reject("login.form.loginFail");
//			return "login/loginForm";
//		}
//		log.info("[POST /login] 로그인 성공 : {}", loginMember);
//		HttpSession session = request.getSession();
//		session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//		return "redirect:/";
//	}
	
//	@PostMapping("/login")
//	public String loginV2(@Validated LoginForm loginForm, BindingResult bindingResult,
//			HttpServletResponse response) { 
//		
//		if(bindingResult.hasErrors()) {
//			return "login/loginForm";
//		}
//		
//		Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
//		log.info("login : {}", loginMember);
//		
//		if(loginMember == null) {
//			bindingResult.reject("login.form.loginFail");
//			return "login/loginForm";
//		}
//		
//		// 로그인 성공 처리 + SessionManager
//		sessionManager.createSession(loginMember, response);
//		return "redirect:/";
//	}
	
//	@PostMapping("/login")
//	public String loginV1(@Validated LoginForm loginForm, BindingResult bindingResult,
//			HttpServletResponse response) { 
//		
//		if(bindingResult.hasErrors()) {
//			return "login/loginForm";
//		}
//		
//		Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
//		log.info("login : {}", loginMember);
//		
//		if(loginMember == null) {
//			bindingResult.reject("login.form.loginFail");
//			return "login/loginForm";
//		}
//		
//		Cookie cookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
//		response.addCookie(cookie);
//		return "redirect:/";
//	}
//
//	
//	@PostMapping("/logout")
//	public String logoutV2(HttpServletRequest request) {
//		sessionManager.expire(request);
//		return "redirect:/";
//	}
//
//	@PostMapping("/logout")
//	public String logoutV1(HttpServletResponse response) {
//		expireCookie(response, "memberId");
//		return "redirect:/";
//	}
//	
//	private void expireCookie(HttpServletResponse response, String cookieName) {
//		Cookie cookie = new Cookie(cookieName, null);
//		cookie.setMaxAge(0);
//		response.addCookie(cookie);
//	}
}
