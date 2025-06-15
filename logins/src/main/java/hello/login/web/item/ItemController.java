package hello.login.web.item;

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

import hello.login.domain.item.Item;
import hello.login.domain.item.ItemRepository;
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
	public String showItemList(Model model) {
		List<Item> itemList = itemRepository.findAll();
		log.info("상품 목록 조회, itemCount={}", itemList.size());
		model.addAttribute("itemList", itemList);
		return "items/items";
	}

	@GetMapping("/{itemId}")
	public String showItemDetail(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		log.info("상품 상세 조회, itemId={}, item={}", itemId, item);
		model.addAttribute("item", item);
		return "items/item";
	}

	@GetMapping("/add")
	public String addForm(ItemSaveForm saveForm) {
		log.info("상품 등록 폼 요청");
		return "items/addForm";
	}

	@PostMapping("/add")
	public String addItem(@Validated ItemSaveForm saveForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		log.info("상품 등록 요청: saveForm={}", saveForm);

		validateTotalPrice(saveForm.getPrice(), saveForm.getQuantity(), bindingResult);

		if (bindingResult.hasErrors()) {
			log.warn("상품 등록 검증 오류: {}", bindingResult);
			return "items/addForm";
		}

		Item newItem = ToItem(saveForm);
		Item savedItem = itemRepository.save(newItem);
		log.info("상품 등록 성공: savedItem={}", savedItem);

		redirectAttributes.addAttribute("itemId", savedItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/items/{itemId}";
	}

	@GetMapping("/{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long itemId, Model model) {
		ItemUpdateForm editForm = toForm(itemRepository.findById(itemId));
		log.info("상품 수정 폼 요청: itemId={}, editForm={}", itemId, editForm);
		model.addAttribute("item", editForm);
		return "items/editForm";
	}

	@PostMapping("/{itemId}/edit")
	public String edit(@PathVariable("itemId") Long itemId, @ModelAttribute("item") @Validated ItemUpdateForm editForm,
			BindingResult bindingResult) {

		log.info("상품 수정 요청: itemId={}, editForm={}", itemId, editForm);

		validateTotalPrice(editForm.getPrice(), editForm.getQuantity(), bindingResult);

		if (bindingResult.hasErrors()) {
			log.warn("상품 수정 검증 오류: {}", bindingResult);
			return "items/editForm";
		}

		Item updatedItem = ToItem(editForm);
		itemRepository.update(itemId, updatedItem);
		log.info("상품 수정 성공: itemId={}, updatedItem={}", itemId, updatedItem);
		return "redirect:/items/{itemId}";
	}

	private void validateTotalPrice(Integer price, Integer quantity, BindingResult bindingResult) {
		if (price != null && quantity != null) {
			int resultPrice = price * quantity;
			if (resultPrice < 10_000) {
				bindingResult.reject("totalPriceMin", new Object[] { 10_000, resultPrice }, null);
				log.warn("복합 검증 실패: 가격*수량={} < 10000", resultPrice);
			}
		}
	}

	private Item ToItem(ItemSaveForm saveForm) {
		return Item.builder().itemName(saveForm.getItemName()).price(saveForm.getPrice())
				.quantity(saveForm.getQuantity()).build();
	}

	private Item ToItem(ItemUpdateForm editForm) {
		return Item.builder().itemName(editForm.getItemName()).price(editForm.getPrice())
				.quantity(editForm.getQuantity()).build();
	}

	private ItemUpdateForm toForm(Item item) {
		return ItemUpdateForm.builder().id(item.getId()).itemName(item.getItemName()).price(item.getPrice())
				.quantity(item.getQuantity()).build();
	}
}
