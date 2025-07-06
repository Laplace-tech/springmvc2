package hello.login.interceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.web.servlet.HandlerInterceptor;

import hello.login.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestURI = request.getRequestURI();
		
		log.info("인증 체크 인터셉터 실행 : {}", requestURI);
		HttpSession session = request.getSession(false);
		
		if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
			log.info("미인증 사용자 요청");
			// 로그인으로 리다이렉트
			redirectToLogin(response, requestURI);
			log.info("리다이렉트 : /login?redirectURL={}", requestURI);
			return false;
		}
		
		log.info("인증 체크 완료 : {}", requestURI);
		return true;
	}

	private void redirectToLogin(HttpServletResponse response, String requestURI) throws IOException {
		String encodedURI = URLEncoder.encode(requestURI, StandardCharsets.UTF_8);
		response.sendRedirect("/login?redirectURL=" + encodedURI);
	}

}
