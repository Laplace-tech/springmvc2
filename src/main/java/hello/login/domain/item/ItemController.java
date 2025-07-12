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

/**
 * 아이템 관련 웹 요청을 처리하는 Spring MVC 컨트롤러
 * - 아이템 등록, 조회, 수정 기능 제공
 * - 서비스 계층(ItemService)에 비즈니스 로직 위임
 */
@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	/**
	 * 전체 아이템 목록 페이지 반환
	 *
	 * @param model 뷰에 전달할 모델 객체
	 * @return 아이템 목록 페이지 뷰 이름
	 */
	@GetMapping
	public String showAllItems(Model model) {
		model.addAttribute("itemList", itemService.findAllItems());
//		log.info("[GET /items] 전체 아이템 목록 요청");
		return "items/items";
	}

	/**
	 * 단건 아이템 상세 보기
	 *
	 * @param itemId 조회할 아이템 ID
	 * @param model  뷰에 전달할 모델 객체
	 * @return 아이템 상세 페이지 뷰 이름
	 */
	@GetMapping("/{itemId}")
	public String showItemDetail(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemService.findItemOrThrow(itemId);
		model.addAttribute("item", item);
//		log.info("[GET /items/{}] 아이템 상세 페이지 뷰 요청", itemId);
		return "items/item";
	}

	/**
	 * 아이템 등록 폼 페이지
	 *
	 * @param form 빈 폼 객체 (타임리프에서 사용됨)
	 * @return 아이템 등록 폼 뷰 이름
	 */
	@GetMapping("/add")
	public String showAddForm(ItemSaveForm form) {
//		log.info("[GET /items/add] 아이템 등록 폼 페이지 요청");
		return "items/addForm";
	}

	/**
	 * 아이템 등록 처리
	 *
	 * @param form                사용자가 입력한 등록 폼
	 * @param bindingResult       검증 결과
	 * @param redirectAttributes  리다이렉트 시 전달할 속성
	 * @return 등록 성공 시 상세 페이지로 리다이렉트, 실패 시 등록 폼 뷰
	 */
	@PostMapping("/add")
	public String addItem(@Validated ItemSaveForm form,
						  BindingResult bindingResult,
						  RedirectAttributes redirectAttributes) {

//		log.info("[POST /items/add] 아이템 등록 처리 요청");
		
		itemService.validateTotalPrice(form, bindingResult);
		if (bindingResult.hasErrors()) {
//			log.debug("아이템 저장 실패: {}", bindingResult);
			return "items/addForm";
		}

		Item savedItem = itemService.saveItem(form);
//		log.info("[POST /items/add] 아이템 등록 처리 성공");
		
		redirectAttributes.addAttribute("itemId", savedItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/items/{itemId}";
	}

	/**
	 * 아이템 수정 폼 페이지
	 *
	 * @param itemId 수정할 아이템 ID
	 * @param model  뷰에 전달할 모델 객체
	 * @return 아이템 수정 폼 뷰 이름
	 */
	@GetMapping("/{itemId}/edit")
	public String showEditForm(@PathVariable("itemId") Long itemId, Model model) {
//		log.info("[GET /items/{}/edit] 아이템 수정 폼 페이지 요청", itemId);
		Item item = itemService.findItemOrThrow(itemId);
		ItemUpdateForm updateForm = ItemMapper.toUpdateForm(item);
		model.addAttribute("item", updateForm);
		return "items/editForm";
	}

	/**
	 * 아이템 수정 처리
	 *
	 * @param itemId        수정 대상 아이템 ID
	 * @param updateForm    사용자가 입력한 수정 폼
	 * @param bindingResult 검증 결과
	 * @return 수정 성공 시 상세 페이지로 리다이렉트, 실패 시 수정 폼 뷰
	 */
	@PostMapping("/{itemId}/edit")
	public String editItem(@PathVariable("itemId") Long itemId,
						   @Validated @ModelAttribute("item") ItemUpdateForm updateForm,
						   BindingResult bindingResult) {

//		log.info("[POST /items/{}/edit] 아이템 수정 처리 요청", itemId);
		
		itemService.validateTotalPrice(updateForm, bindingResult);
		if (bindingResult.hasErrors()) {
//			log.debug("아이템 수정 실패: {}", bindingResult);
			return "items/editForm";
		}

		itemService.updateItem(itemId, updateForm);
//		log.info("[POST /items/{}/edit] 아이템 수정 처리 성공", itemId);
		
		return "redirect:/items/{itemId}";
	}
}
