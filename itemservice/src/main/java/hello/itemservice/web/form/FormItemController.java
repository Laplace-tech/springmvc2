package hello.itemservice.web.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {

	private final ItemRepository itemRepository;
	
	/*
	 * @ModelAttribute가 메서드 선언부에 붙는 경우
	 *  그 메서드는 컨트롤러의 어떤 요청이 오더라도 자동으로 실행된다.
	 *  반환값은 Model 객체에 자동으로 추가된다.
	 */
	
	@ModelAttribute("regions")
	public Map<String, String> regions() {
		Map<String, String> regions = new HashMap<>();
		regions.put("SEOUL", "서울");
		regions.put("BUSAN", "부산");
		regions.put("JEJU", "제주");
		return regions;
	}
	
	@ModelAttribute("itemTypes")
	public ItemType[] itemTypes() {
		return ItemType.values();
	}
	
	@ModelAttribute("deliveryCodes")
	public List<DeliveryCode> deliveryCodes() {
		List<DeliveryCode> deliveryCodes = new ArrayList<>();
		deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
		deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
		deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
		return deliveryCodes;
	}
	
	@GetMapping
	public String showItemList(Model model) {
		List<Item> itemList = this.itemRepository.findAll();
		model.addAttribute("items", itemList);
		return "form/items";
	}
	
	@GetMapping("/{itemId}")
	public String showItemDetail(@PathVariable("itemId") Long itemId, Model model) {
		Item item = this.itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "form/item";
	}
	
	@GetMapping("/add")
	public String addForm(Item item) {
		return "form/addForm";
	}
	
	@PostMapping("/add")
	public String addItem(Item item, RedirectAttributes redirectAttributes) {
		log.info("item.open={}", item.getOpen());
		log.info("item.regions={}", item.getRegions());
		
		Item newItem = itemRepository.save(item);
		redirectAttributes.addAttribute("itemId", newItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/form/items/{itemId}";
	}
	
	@GetMapping("/{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long itemId, Model model) {
		Item item = this.itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "form/editForm";
	}
	
	@PostMapping("/{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long itemId, Item updatedItem) {
		log.info("item.open={}", updatedItem.getOpen());
		log.info("item.regions={}", updatedItem.getRegions());

		itemRepository.update(itemId, updatedItem);
		return "redirect:/form/items/{itemId}";
	}
	
}

