package hello.login;

import org.springframework.stereotype.Component;

import hello.login.domain.item.ItemService;
import hello.login.domain.member.MemberService;
import hello.login.web.item.form.ItemSaveForm;
import hello.login.web.member.form.MemberForm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestDataInit {

	private final ItemService itemService;
	private final MemberService memberService;

	@PostConstruct
	public void initTestData() {	
		initItems();
		initMembers();
	}
	
	private void initItems() {
		ItemSaveForm item1 = new ItemSaveForm();
		item1.setItemName("연필");
		item1.setPrice(3000);
		item1.setQuantity(10);
		
		ItemSaveForm item2 = new ItemSaveForm();
		item2.setItemName("공책");
		item2.setPrice(5000);
		item2.setQuantity(30);
		
		itemService.saveItem(item1);
		itemService.saveItem(item2);
	}
	
	private void initMembers() {
		MemberForm member1 = new MemberForm();
		member1.setLoginId("anna");
		member1.setPassword("2848");
		member1.setName("안나리");

		MemberForm member2 = new MemberForm();
		member2.setLoginId("erma");
		member2.setPassword("2848");
		member2.setName("엘마");
		
        memberService.saveMember(member1);
        memberService.saveMember(member2);
	}
	
	
}
