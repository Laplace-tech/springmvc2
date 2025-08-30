package hello.login.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.util.PatternMatchUtils;

import hello.login.session.SessionConst;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/*
 * 인증이 필요한 요청인지 확인하고,
 * 인증되지 않은 경우 로그인 페이지로 리다이렉트 하는 필터
 */
@Slf4j
public class LoginCheckFilter implements Filter {
	
	private static final String[] WHITELIST 
		= {"/", "/members/add", "/login", "/logout", "/css/*"};
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String requestURI = httpRequest.getRequestURI();
		
		try {
			log.info("[인증 필터 시작] URI : {}", requestURI);
			
			if(isLoginRequired(requestURI)) {
				log.info("[인증 체크 필요] URI : {}", requestURI);
				
				HttpSession session = httpRequest.getSession(false);
				boolean notLoggedIn = (session == null) || (session.getAttribute(SessionConst.LOGIN_MEMBER) == null);
				if(notLoggedIn) {
					log.info("[미인증 사용자 접근] URI : {}", requestURI);
					redirectToLogin(httpResponse, requestURI);
					return; // 인증 안 됐으면 필터 체인 종료
				}
			}
			//화이트리스트 또는 인증 성공 : 다음 필터 또는 컨트롤러로 진행
			chain.doFilter(request, response);
			
		} catch (Exception e) {
			throw e; // 예외는 톰캣까지 전달해야 함
		} finally {
			log.info("[인증 필터 종료] URI : {}", requestURI);
		}
		
	}

	/*
	 * 로그인 페이지로 리다이렉트 (원래 요청 URI는 쿼리 파라메터로 전달)
	 */
	private void redirectToLogin(HttpServletResponse response, String requestURI) throws IOException {
		String encodedURI = URLEncoder.encode(requestURI, StandardCharsets.UTF_8);
		response.sendRedirect("/login?redirectURL=" + encodedURI);
	}
	
	
	/*
	 * 화이트 리스트의 경우 인증 체크 X
	 */
	private boolean isLoginRequired(String requestURI) {
		return !PatternMatchUtils.simpleMatch(WHITELIST, requestURI);
	}
}
