package hello.login;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import hello.login.domain.member.Member;
import hello.login.web.session.SessionManager;

public class SessionManagerTest {

	SessionManager sessionManager = new SessionManager();

	@Test
	void sessionTest() {

		// 세션 생성
		MockHttpServletResponse response = new MockHttpServletResponse(); // response mocking
		Member member = new Member();
		sessionManager.createSession(member, response);

		// 요청에 응답 쿠키 저장
		MockHttpServletRequest request = new MockHttpServletRequest(); // request mocking
		request.setCookies(response.getCookies());

		// 세션 조회
		Object result = sessionManager.getSession(request);
		assertThat(result).isEqualTo(member);

		// 세션 만료
		sessionManager.expire(request);
		Object expired = sessionManager.getSession(request);
		assertThat(expired).isNull();

	}

}
