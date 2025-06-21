package hello.login;

import org.springframework.stereotype.Component;

import hello.login.domain.item.Item;
import hello.login.domain.item.ItemRepository;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInit {

	private final ItemRepository itemRepository;
	private final MemberRepository memberRepository;

	/**
	 * 테스트용 데이터 추가
	 */
	@PostConstruct
	public void init() {
		itemRepository.save(new Item("itemA", 10000, 10));
		itemRepository.save(new Item("itemB", 20000, 20));

		Member member1 = new Member("Anna", "Anna", "28482848a");
		Member member2 = new Member("Erma", "Erma", "28482848a");
		Member member3 = new Member("Romi", "Romi", "28482848a");

		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
	}
}
