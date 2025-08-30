package hello.exceptions.domain.login;

import org.springframework.stereotype.Service;

import hello.exceptions.domain.member.Member;
import hello.exceptions.domain.member.MemberRepository;
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
		return memberRepository.findByLoginId(loginId)
				.filter(member -> password.equals(member.getPassword()))
				.orElseGet(() -> null);

	}
}