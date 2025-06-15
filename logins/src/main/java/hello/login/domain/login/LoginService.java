package hello.login.domain.login;

import org.springframework.stereotype.Service;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

	private final MemberRepository memberRepository;

	/**
	 * 로그인 처리
	 * 
	 * @param loginId
	 * @param password
	 * @return null : 로그인 실패
	 */
	public Member login(String loginId, String password) {
		log.info("Login attempt: loginId={}", loginId);

		return memberRepository.findByLoginId(loginId).filter(member -> {
			boolean matched = member.getPassword().equals(password);
			
			if (matched) {
				log.info("Login successful: loginId={}", loginId);
			} else {
				log.warn("Login failed (invalid password): loginId={}", loginId);
			}
			return matched;
		}).orElseGet(() -> {
			log.warn("Login failed (loginId not found): loginId={}", loginId);
			return null;
		});
	}
}
