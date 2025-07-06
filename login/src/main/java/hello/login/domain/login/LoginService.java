package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * @return null 로그인 실패
     */
    public Member login(String loginId, String password) {
        /**
             Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);
             Member member = findMemberOptional.get();
             if (member.getPassword().equals(password)) {
                return member;
             } else {
                return null;
             }
         */
        //[Java 8] Lambda : 위의 내용을 아래와 같은 코드로 대체하여 쓸 수 있음
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
