package hello.itemservice.web.validation;

import java.util.List;

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

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

	private final ItemRepository itemRepository;

	@GetMapping
	public String showItemList(Model model) {
		List<Item> itemList = itemRepository.findAll();
		model.addAttribute("itemList", itemList);
		return "validation/v4/items";
	}

	@GetMapping("/{itemId}")
	public String showItemDetail(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "validation/v4/item";
	}

	@GetMapping("/add")
	public String addForm(ItemSaveForm saveForm) {
		return "validation/v4/addform";
	}

	@PostMapping("/add")
	public String addItem(@Validated ItemSaveForm saveForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		// 특정 필드가 아닌 복합 룰 검증
		validateTotalPrice(saveForm.getPrice(), saveForm.getQuantity(), bindingResult);

		// 검증에 실패하면 다시 입력 폼으로
		if (bindingResult.hasErrors()) {
			log.info("bindingResult = {}", bindingResult);
			return "validation/v4/addform";
		}

		Item newItem = ToItem(saveForm);
		Item savedItem = itemRepository.save(newItem);

		redirectAttributes.addAttribute("itemId", savedItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/validation/v4/items/{itemId}";
	}

	@GetMapping("/{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long itemId, Model model) {
		ItemUpdateForm editForm = toForm(itemRepository.findById(itemId));
		model.addAttribute("item", editForm);
		return "validation/v4/editform";
	}

	@PostMapping("/{itemId}/edit")
	public String edit(@PathVariable("itemId") Long itemId, @ModelAttribute("item") @Validated ItemUpdateForm editForm,
			BindingResult bindingResult) {

		// 특정 필드가 아닌 복합 룰 검증
		validateTotalPrice(editForm.getPrice(), editForm.getQuantity(), bindingResult);

		if (bindingResult.hasErrors()) {
			log.info("bindingResult = {}", bindingResult.toString());
			return "validation/v4/editform";
		}

		Item updatedItem = ToItem(editForm);
		itemRepository.update(itemId, updatedItem);
		return "redirect:/validation/v4/items/{itemId}";
	}

	/**
	 * 복합 검증 로직 (가격 * 수량 최소값)
	 */
	private void validateTotalPrice(Integer price, Integer quantity, BindingResult bindingResult) {
		if (price != null && quantity != null) {
			int resultPrice = price * quantity;
			if (resultPrice < 10_000) {
				bindingResult.reject("totalPriceMin", new Object[] { 10_000, resultPrice }, null);
			}
		}
	}
	
	/*
	 * 폼 객체 -> 도메인 객체로 변환
	 */
	private Item ToItem(ItemSaveForm saveForm) {
		  return Item.builder()
		    		.itemName(saveForm.getItemName())
		    		.price(saveForm.getPrice())
		    		.quantity(saveForm.getQuantity())
		    		.build();
	}
	
	private Item ToItem(ItemUpdateForm editForm) {
		  return Item.builder()
		    		.itemName(editForm.getItemName())
		    		.price(editForm.getPrice())
		    		.quantity(editForm.getQuantity())
		    		.build();
	}

	private ItemUpdateForm toForm(Item item) {
		return ItemUpdateForm.builder()
				.id(item.getId())
				.itemName(item.getItemName())
				.price(item.getPrice())
				.quantity(item.getQuantity())
				.build();
	}
	
}

