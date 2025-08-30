package hello.exceptions.interceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import hello.exceptions.session.SessionConst;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String requestURI = request.getRequestURI();
		log.info("[인터셉터 진입] 로그인 인증 체크 시작 : {}", requestURI);

		if (isLoginMember(request)) {
			log.info("[인터셉터 - 로그인 인증 성공] : {}", requestURI);
			return true;
		}

		log.warn("[인터셉터 - 미인증 사용자 요청] 요청 URI: {}", requestURI);
		redirectToLogin(request, response);
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
	                       Object handler, ModelAndView modelAndView) throws Exception {
	    log.info("[인터셉터 postHandle] 요청 처리 후 View 렌더링 전 실행 - URI: {}", request.getRequestURI());
	    log.info("[인터셉터 postHandle] 모델앤뷰 정보 : {}", modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
	                            Object handler, Exception ex) throws Exception {
	    log.info("[인터셉터 afterCompletion] 요청 완료 후 실행 - URI: {}", request.getRequestURI());
	    if (ex != null) {
	        log.error("[인터셉터 afterCompletion] 요청 처리 중 예외 발생", ex);
	    }
	}
	
	private boolean isLoginMember(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		boolean loggedIn = (session != null && session.getAttribute(SessionConst.LOGIN_MEMBER) != null);
		
		 log.warn("    [인증 상태 체크] 세션 존재: {}, 로그인 여부: {}", (session != null), loggedIn);
		return loggedIn;
	}

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    String redirectURI = URLEncoder.encode(request.getRequestURI(), StandardCharsets.UTF_8);
	    response.sendRedirect("/login?redirectURL=" + redirectURI);
	    log.info("    [리다이렉트] 미인증 사용자 → 로그인 페이지로 이동 (/login?redirectURL={})", redirectURI);
	}
	
    @PostConstruct
    public void init() {
        log.info("LoginCheckInterceptor Initialized");
    }

    @PreDestroy
    public void destroy() {
        log.info("LoginCheckInterceptor Destroyed");
    }

}
