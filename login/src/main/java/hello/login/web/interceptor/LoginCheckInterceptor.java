package hello.login.web.interceptor;

import hello.login.web.session.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    /**
     * MEMO: java 8 에서 도입된 인터페이스의 디폴트 메서드(default method)
     * default method 는 구현 내용을 포함하는 메서드로
     * 구현체에서 해당 메서드를 재정의 하지 않아도 된다.
     * <p>
     * preHandle, postHandle, afterCompletion 모두 default method 이므로
     * 재정의가 필요한 메서드만 골라서 구현할 수 있음.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 시작 {}", requestURI);

        HttpSession session = request.getSession();

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            log.info("미인증 사용자 요청");

            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }

        return true;
    }
}
