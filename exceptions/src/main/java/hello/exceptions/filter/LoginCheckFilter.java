package hello.exceptions.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.util.PatternMatchUtils;

import hello.exceptions.session.SessionConst;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
/*
 * ✅ [인증된 사용자 요청 흐름]
 * 
 * 1. 클라이언트가 `/items` 요청을 보냄
 * 2. WAS → LoginCheckFilter.doFilter() 호출됨
 *    - requestURI = "/items"
 *    - 화이트리스트에 포함되지 않음 → 로그인 인증 필요
 *    - 세션이 존재하고, 세션에 LOGIN_MEMBER 속성이 존재함 → 인증 성공
 * 3. 인증 성공 → chain.doFilter() 실행 → 다음 필터 또는 DispatcherServlet으로 요청 전달
 * 4. DispatcherServlet → HandlerMapping → HandlerAdapter → 컨트롤러(ItemController 등) 호출
 * 5. 컨트롤러 로직 처리 → View 반환 (예: ModelAndView 또는 @ResponseBody)
 * 6. 응답 직전에 finally 블록 실행 → "[필터 종료] 인증 체크 종료 : /items" 로그 출력
 * 7. 최종 응답이 클라이언트에 반환됨
 */

/*
 * ❌ [인증되지 않은 사용자 요청 흐름]
 * 
 * 1. 클라이언트가 `/items` 요청을 보냄
 * 2. WAS → LoginCheckFilter.doFilter() 호출됨
 *    - requestURI = "/items"
 *    - 화이트리스트에 포함되지 않음 → 인증 필요
 *    - 세션이 없거나, 세션에 LOGIN_MEMBER 속성이 없음 → 인증 실패
 * 3. "[필터] 미인증 사용자 요청" 로그 출력
 * 4. redirectToLogin() 실행 → `/login?redirectURL=/items`로 리다이렉트 응답 생성
 * 5. chain.doFilter() 호출되지 않음 → DispatcherServlet/컨트롤러로 전달되지 않음
 * 6. finally 블록에서 "[필터 종료] 인증 체크 종료 : /items" 로그 출력
 * 7. 클라이언트는 302 응답을 받고 자동으로 `/login?redirectURL=/items`로 이동함
 */

/*
 * 🔐 [로그인 과정 흐름]
 * 
 * 8. 클라이언트가 `/login?redirectURL=/items` 요청
 * 9. @GetMapping("/login") → 로그인 폼 뷰 반환
 * 10. 사용자 폼 입력 후 POST `/login` 요청
 * 11. 로그인 성공 시
 *     - 세션이 없으면 생성하고, LOGIN_MEMBER 속성에 로그인 사용자 저장
 *     - redirect:/items 로 리다이렉트 응답
 * 12. 클라이언트가 다시 `/items` 요청
 *     - 이번에는 세션에 로그인 정보가 존재하므로 인증 성공 → 정상 흐름으로 처리됨
 */

/*
 * 🧩 [@Login 애노테이션 기반 파라미터 주입 흐름]
 * 
 * 사용 예시:
 *   public String itemList(@Login Member loginMember)
 * 
 * 1. DispatcherServlet → HandlerAdapter가 컨트롤러 호출 준비
 * 2. 등록된 HandlerMethodArgumentResolver 중 LoginMemberArgumentResolver가 supportsParameter() 호출
 *    - 파라미터에 @Login 애노테이션이 있고, 타입이 Member인지 확인
 * 3. 조건 만족 시 resolveArgument() 실행
 *    - 세션에서 LOGIN_MEMBER 속성 값을 꺼내 loginMember 파라미터에 주입
 * 4. 이후 컨트롤러 메서드 실행 시 loginMember 객체가 자동으로 전달됨
 */

@Slf4j
//@Component
public class LoginCheckFilter implements Filter {

	private static final String[] WHITELIST = { 
			"/", "/members/add", "/login", "/logout", 
			"/css/*", "/*.ico", "/error",
			"/error-page/*" };

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("LoginCheckFilter Initialized");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String requestURI = httpRequest.getRequestURI();

		log.info("[필터 진입] 로그인 인증 체크 시작 : {}", requestURI);

		try {
			if (requiresLogin(requestURI)) {
				log.info("[필터] 로그인 필요 URI : {}", requestURI);

				if (isNotLoggedIn(httpRequest)) {
					log.info("[필터] 미인증 사용자 요청 : {}", requestURI);
					redirectToLogin(httpResponse, requestURI);
					return;
				}
				log.info("[필터] 로그인 인증 성공 : {}", requestURI);
			} else {
				log.info("[필터] 로그인 필요 없는 URI : {}", requestURI);
			}

			chain.doFilter(request, response);

		} catch (Exception e) {
			log.error("[필터] 예외 발생: {}", e.toString(), e);
			throw e;
		} finally {
			log.info("[필터 종료] 인증 체크 종료 : {}", requestURI);
		}
	}

	@Override
	public void destroy() {
		log.info("LoginCheckFilter Destroyed");
	}
	
	private void redirectToLogin(HttpServletResponse httpResponse, String requestURI) throws IOException {
		String encodedURI = URLEncoder.encode(requestURI, StandardCharsets.UTF_8);
		httpResponse.sendRedirect("/login?redirectURL=" + encodedURI);
		log.info("[필터] 로그인 후 리다이렉트 : /login?redirectURL={}", requestURI);
	}

	private boolean isNotLoggedIn(HttpServletRequest httpRequest) {
		HttpSession session = httpRequest.getSession(false);
		return session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null;
	}

	private boolean requiresLogin(String requestURI) {
		return !PatternMatchUtils.simpleMatch(WHITELIST, requestURI);
	}

}
