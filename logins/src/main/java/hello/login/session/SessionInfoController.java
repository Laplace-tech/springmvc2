package hello.login.session;

import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "세션이 없습니다.";
        }

        //세션 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> 
                log.info("session name={}, value={}", name, session.getAttribute(name)));

        /**
         * sessionId (세션 아이디)
         * maxInactiveInterval (세션의 유효 시간)
         * creationTime (세션 생성일시)
         * lastAccessedTime (최근에 서버에 접근한 시간, 클라이언트의 요청 시 갱신)
         * isNew (새로 생성되었는지, 과거에 생성되어 조회 요청된 세션인지)
         * */
        log.info("sessionId={}", session.getId());
        log.info("maxInactiveInterval={}", session.getMaxInactiveInterval());
        log.info("creationTime={}", new Date(session.getCreationTime())); //long -> Date
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));
        log.info("isNew={}", session.isNew());

        return "세션 출력";
    }
}