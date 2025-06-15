package hello.login.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

	private final MemberRepository memberRepository;

	@GetMapping
	public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
		log.info("[homeLogin] 요청 처리 시작. memberId={}", memberId);

		if (memberId == null) {
			log.info("[homeLogin] memberId 쿠키 없음, 기본 home 화면으로 이동");
			return "home";
		}

		Member loginMember = memberRepository.findById(memberId);
		if (loginMember == null) {
			log.warn("[homeLogin] memberId={} 에 해당하는 회원 없음, 기본 home 화면으로 이동", memberId);
			return "home";
		}

		log.info("[homeLogin] 로그인 회원 조회 성공: {}", loginMember);
		model.addAttribute("member", loginMember);
		return "loginHome";
	}
}
