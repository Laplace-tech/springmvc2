package hello.itemservice.web.validation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
@RequestMapping("/validation/v2/items")
public class ValidationItemControllerV2 {

	private final ItemRepository itemRepository;
	private final ItemValidator itemValidator;
	
	@InitBinder
	public void init(WebDataBinder dataBinder) {
		log.info("init binder : {}", dataBinder);
		dataBinder.addValidators(itemValidator);
	}
	
	@GetMapping
	public String itemList(Model model) {
		List<Item> itemList = itemRepository.findAll();
		model.addAttribute("items", itemList);
		return "validation/v2/items";
	}

	@GetMapping("{itemId}")
	public String itemDetail(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "validation/v2/item";
	}
	
	@GetMapping("add")
	public String addForm(Item item) {
		return "validation/v2/addForm";
	}
	
	@PostMapping("/add")
	public String addItemV6(@Validated Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes,
			Model model) {
		
		if(bindingResult.hasErrors()) {
			log.info("bindingResult = {}", bindingResult);
			return "validation/v2/addForm";
		}
		
		Item savedItem = itemRepository.save(item);
		redirectAttributes.addAttribute("itemId", savedItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/validation/v2/items/{itemId}";
	}
	
	
	@GetMapping("{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "validation/v2/editForm";
	}
	
	@PostMapping("{itemId}/edit")
	public String edit(@PathVariable("itemId") Long itemId, Item item) {
		itemRepository.update(itemId, item);
		return "redirect:/validation/v2/items/{itemId}";
	}
	
}

//@PostMapping("/add")
//public String addItemV1(Item item, BindingResult bindingResult,
//                      RedirectAttributes redirectAttributes, Model model) {
//    // 검증 오류 결과를 보관
//    // 검증 로직
//    if (!StringUtils.hasText(item.getItemName())) {
//        bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
//    }
//    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() >1000000) {
//        bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
//    }
//    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
//        bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
//    }
//
//    // 특정 필드가 아닌 복합 룰 검증
//    if (item.getPrice() != null && item.getQuantity() != null) {
//        int resultPrice = item.getPrice() * item.getQuantity();
//        if (resultPrice < 10000) {
//            bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
//        }
//    }
//
//    // 검증에 실패하면 다시 입력 폼으로
//    if (bindingResult.hasErrors()) {
//        log.info("bindingResult = {}", bindingResult);
//        return "validation/v2/addForm";
//    }
//
//    Item savedItem = itemRepository.save(item);
//    redirectAttributes.addAttribute("itemId", savedItem.getId());
//    redirectAttributes.addAttribute("status", true);
//    return "redirect:/validation/v2/items/{itemId}";
//}

//@PostMapping("/add")
//public String addItemV2(Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
//	
//	if (!StringUtils.hasText(item.getItemName())) {
//		bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), 
//				false, null, null, "상품 이름은 필수입니다."));
//	}
//	
//	if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
//		bindingResult.addError(new FieldError("item", "price", item.getPrice(), 
//				false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
//	}
//	
//	if (item.getQuantity() == null || item.getQuantity() < 1 || item.getQuantity() > 10_000) {
//		bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), 
//				false, null, null, "수량은 최대 9,999 까지 허용합니다."));
//	}
//	
//	if(item.getPrice() != null && item.getQuantity() != null) {
//		int resultPrice = item.getPrice() * item.getQuantity();
//		
//		if (resultPrice < 10000) {
//			bindingResult.addError(new ObjectError("item", null, null, 
//					"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
//		}
//	}
//	
//	if(bindingResult.hasErrors()) {
//		log.info("bindingResult = {}", bindingResult);
//		return "validation/v2/addForm";
//	}
//	
//	Item savedItem = itemRepository.save(item);
//	redirectAttributes.addAttribute("itemId", savedItem.getId());
//	redirectAttributes.addAttribute("status", true);
//	return "redirect:/validation/v2/items/{itemId}";
//}

//@PostMapping("/add")
//public String addItemV3(Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
//
//	/*
//	 * public FieldError(
//	 * 		String objectName, : 오류가 발생한 객체 이름
//	 * 		String field, : 오류 필드
//	 * 		Object rejectedValue, : 사용자가 입력한 값(거절된 값)
//	 * 		boolean bindingFailure, : 타입 오류같은 바인딩 실패인지, 검증 실패인지 구분
//	 * 		String[] codes, : 메세지 코드
//	 * 		Object[] arguments, : 메세지에서 사용하는 인자
//	 * 		String defaultMessage : 기본 오류 메세지 
//	 * )
//	 */
//	
//	if(!StringUtils.hasText(item.getItemName())) {
//        bindingResult.addError(new FieldError(
//        		"item", 
//        		"itemName", 
//        		item.getItemName(),
//                false, 
//                new String[] {"required.item.itemName"}, 
//                null, 
//                "빼애애애애액"));
//	}
//	
//	if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
//        bindingResult.addError(new FieldError(
//        		"item", 
//        		"price", 
//        		item.getPrice(),
//                false, 
//                new String[] {"range.item.price"}, 
//                new Object[] {1000, 1_000_000},
//                "빼애애애애액"));
//	}
//	
//	if (item.getQuantity() == null || item.getQuantity() < 1 || item.getQuantity() > 10_000) {
//		bindingResult.addError(new FieldError(
//				"item", 
//				"quantity", 
//				item.getQuantity(), 
//				false, 
//				new String[] {"max.item.quantity"}, 
//				new Object[] {10_000},
//				"빼애애애애애액"));
//	}
//	
//	if(item.getPrice() != null && item.getQuantity() != null) {
//		int resultPrice = item.getPrice() * item.getQuantity();
//		
//		if(resultPrice < 10000) {
//		    bindingResult.addError(new ObjectError(
//		    		"item", 
//		    		new String[] {"totalPriceMin"}, 
//		    		new Object[] {10_000, resultPrice},
//                    "빼애애애애애애액"));
//        }
//	}
//	
//	if(bindingResult.hasErrors()) {
//		log.info("bindingResult = {}", bindingResult);
//		return "validation/v2/addForm";
//	}
//	
//	Item savedItem = itemRepository.save(item);
//	redirectAttributes.addAttribute("itemId", savedItem.getId());
//	redirectAttributes.addAttribute("status", true);
//	return "redirect:/validation/v2/items/{itemId}";
//}
//@PostMapping("/add")
//public String addItemV4(Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes,
//		Model model) {
//
//	ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");
//	
////	if(!StringUtils.hasText(item.getItemName())) {
////		bindingResult.rejectValue("itemName", "required");
////	}
//	
//	if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
//		bindingResult.rejectValue("price", "range", new Object[] {1000, 10000}, null);
//	}
//	
//	if (item.getQuantity() == null || item.getQuantity() < 1 || item.getQuantity() > 10_000) {
//		bindingResult.rejectValue("quantity", "max", new Object[] {10_000}, null);
//	}
//	
//	if(item.getPrice() != null && item.getQuantity() != null) {
//		int resultPrice = item.getPrice() * item.getQuantity();
//		if(resultPrice < 10000) {
//			bindingResult.reject("totalPriceMin", new Object[] {10000, resultPrice}, null);
//		}
//	}
//	
//	if(bindingResult.hasErrors()) {
//		log.info("bindingResult = {}", bindingResult);
//		return "validation/v2/addForm";
//	}
//	
//	Item savedItem = itemRepository.save(item);
//	redirectAttributes.addAttribute("itemId", savedItem.getId());
//	redirectAttributes.addAttribute("status", true);
//	return "redirect:/validation/v2/items/{itemId}";
//}