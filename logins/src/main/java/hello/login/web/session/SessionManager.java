package hello.login.web.session;

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

    private final Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /*
     * 세션 생성
     */
    public void createSession(Object value, HttpServletResponse response) {
        log.info("[createSession] Start");

        // 세션 ID를 생성하고, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);
        log.info("[createSession] New session created. sessionId={}, value={}", sessionId, value);

        // 쿠키 생성
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        sessionCookie.setHttpOnly(true); // 보안 강화
        sessionCookie.setPath("/"); // 모든 경로에서 유효하도록 설정
        log.info("[createSession] Cookie created. name={}, value={}", sessionCookie.getName(), sessionCookie.getValue());

        response.addCookie(sessionCookie);

        log.info("[createSession] End");
    }

    /*
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request) {
        log.info("[getSession] Start");

        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie == null) {
            log.warn("[getSession] No session cookie found");
            return null;
        }

        String sessionId = sessionCookie.getValue();
        Object sessionData = sessionStore.get(sessionId);

        log.info("[getSession] Found cookie. name={}, value={}", sessionCookie.getName(), sessionId);
        log.info("[getSession] Session data for sessionId={} is {}", sessionId, sessionData);

        log.info("[getSession] End");
        return sessionData;
    }

    /*
     * 세션 만료
     */
    public void expire(HttpServletRequest request) {
        log.info("[expire] Start");

        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie != null) {
            String sessionId = sessionCookie.getValue();
            log.info("[expire] Expiring session. sessionId={}", sessionId);
            sessionStore.remove(sessionId);
        } else {
            log.warn("[expire] No session cookie found to expire");
        }

        log.info("[expire] End");
    }

    private Cookie findCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            log.warn("[findCookie] Request has no cookies");
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }
}

