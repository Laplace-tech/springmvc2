package hello.login.session;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionManager {

	public static final String SESSION_COOKIE_NAME = "mySessionId";
	private final Map<String, Object> sessionMap = new ConcurrentHashMap<>();
	
	/*
	 * 세션 생성
	 */
	public void createSession(Object value, HttpServletResponse response) {
		String sessionId = UUID.randomUUID().toString();
		sessionMap.put(sessionId, value);
        log.info("✅ 세션 생성 - sessionId: {}, value: {}", sessionId, value);

        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(mySessionCookie);
        log.info("✅ 쿠키 생성 - name: {}, value: {}", mySessionCookie.getName(), mySessionCookie.getValue());
	}
    
	/*
     * 세션 조회
     */
	public Object getSession(HttpServletRequest request) {
		Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
		if (sessionCookie == null) {
			log.warn("⚠️ 세션 쿠키 없음");
			return null;
		}
		
		String sessionId = sessionCookie.getValue();
		Object sessionValue = sessionMap.get(sessionId);
		log.info("🔍 세션 조회 - sessionId: {}, value: {}", sessionId, sessionValue);

        return sessionValue;
	}
	
	/*
     * 세션 만료
     */
	public void expire(HttpServletRequest request) {
		Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
		
		if (sessionCookie != null) {
			String sessionId = sessionCookie.getValue();
			sessionMap.remove(sessionId);
			log.info("❌ 세션 만료 - sessionId: {}", sessionId);
		} else {
			log.warn("⚠️ 만료 시도했지만 세션 쿠키 없음");
		}
	}
	
	
	private Cookie findCookie(HttpServletRequest request, String name) {
		if(request.getCookies() == null) {
			return null;
		}
		return Arrays.stream(request.getCookies())
					.filter(cookie -> cookie.getName().equals(name))
					.findAny()
					.orElse(null);
	}
	
}
