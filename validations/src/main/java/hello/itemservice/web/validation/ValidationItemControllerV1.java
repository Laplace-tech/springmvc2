package hello.itemservice.web.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/validation/v1/items")
public class ValidationItemControllerV1 {

	private final ItemRepository itemRepository;
	
	@GetMapping
	public String itemList(Model model) {
		List<Item> itemList = itemRepository.findAll();
		model.addAttribute("items", itemList);
		return "validation/v1/items";
	}

	@GetMapping("{itemId}")
	public String itemDetail(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "validation/v1/item";
	}
	
	@GetMapping("add")
	public String addForm(Item item) {
		return "validation/v1/addForm";
	}
	
	@PostMapping("add")
	public String addItem(Item item, RedirectAttributes redirectAttributes, Model model) {
		
		Map<String, String> errors = new HashMap<>();
		
		if(!StringUtils.hasText(item.getItemName())) {
			errors.put("itemName", "상품 이름은 필수야");
		}
		
		if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
			errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용한다");
		}
		
		if(item.getQuantity() == null || item.getQuantity() < 1 ||  item.getQuantity() > 10_000) {
			errors.put("quantity", "수량은 1 ~ 10,000 까지 허용한다.");
		}
		
		if(item.getPrice() != null && item.getQuantity() != null) {
			int resultPrice = item.getPrice() * item.getQuantity();
			
			if(resultPrice < 10000) {
				errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 함");
			}
		}
		
		if(!errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "validation/v1/addForm";
		}
		
		Item savedItem = itemRepository.save(item);
		redirectAttributes.addAttribute("itemId", savedItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/validation/v1/items/{itemId}";
	}
	
	@GetMapping("{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "validation/v1/editForm";
	}
	
	@PostMapping("{itemId}/edit")
	public String edit(@PathVariable("itemId") Long itemId, Item item) {
		itemRepository.update(itemId, item);
		return "redirect:/validation/v1/items/{itemId}";
	}
	
}
