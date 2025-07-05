package hello.login.domain.item;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.login.web.item.form.ItemSaveForm;
import hello.login.web.item.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemRepository itemRepository;
	
	@GetMapping
	public String showAllItems(Model model) {
		model.addAttribute("itemList", itemRepository.findAll());
		return "items/items";
	}
	
	@GetMapping("/{itemId}")
	public String showItemDetail(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다: id=" + itemId));
		model.addAttribute("item", item);
		return "items/item";
	}
	
	// 상품 등록 폼 보여주기
	@GetMapping("/add")
	public String showAddForm(ItemSaveForm itemSaveForm) {
		return "items/addForm";
	}
	
	// POST: 상품 등록 처리
	@PostMapping("/add")
	public String addItem(@Validated ItemSaveForm saveForm,
						  BindingResult bindingResult,
						  RedirectAttributes redirectAttributes) {
		
		validateTotalPrice(saveForm.getPrice(), saveForm.getQuantity(), bindingResult);
		
		if(bindingResult.hasErrors()) {
			log.debug("아이템 저장 실패: {}", bindingResult);
			return "items/addForm";
		}
		
		Item savedItem = itemRepository.saveItem(saveForm);
		redirectAttributes.addAttribute("itemId", savedItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/items/{itemId}";
	}
	
	@GetMapping("{itemId}/edit")
	public String showEditForm(@PathVariable("itemId") Long itemId, Model model) {
		
		Item item = this.itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("수정할 아이템을 찾을 수 없습니다: id=" + itemId));
		
		ItemUpdateForm updateForm = ItemUpdateForm.builder()
										.id(item.getId())
										.itemName(item.getItemName())
										.price(item.getPrice())
										.quantity(item.getQuantity())
										.build();
		
		model.addAttribute("item", updateForm);
		return "items/editForm";
	}
	
	@PostMapping("{itemId}/edit")
	public String editItem(@PathVariable("itemId") Long itemId,
						   @Validated @ModelAttribute("item") ItemUpdateForm updateForm,
						   BindingResult bindingResult) {
		
		validateTotalPrice(updateForm.getPrice(), updateForm.getQuantity(), bindingResult);

		if (bindingResult.hasErrors()) {
			log.debug("아이템 수정 실패: {}", bindingResult);
			return "items/editForm";
		}

		itemRepository.updateItem(itemId, updateForm);
		return "redirect:/items/{itemId}";

	}
	
	private void validateTotalPrice(Integer price, Integer quantity, BindingResult bindingResult) {
		if(price != null && quantity != null) {
			int totalPrice = price * quantity;
			if(totalPrice < 10_000)
				bindingResult.reject("totalPriceMin", new Object[] {10_000, totalPrice}, null);
		}
	}
	
}
