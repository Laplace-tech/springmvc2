package hello.exceptions.domain.item;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import hello.exceptions.web.item.form.ItemSaveForm;
import hello.exceptions.web.item.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * ItemService 
 * 
 * - 아이템 도메인의 비즈니스 로직을 처리
 * - Controller ↔ Repository 사이에서 데이터 흐름과 유효성 검증을 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private static final int MIN_TOTAL_PRICE = 10_000; // 총 가격 최소값

	/**
	 * 아이템 저장
	 * 
	 * @param form - 등록 폼 객체
	 * @return 저장된 아이템
	 */
	public Item saveItem(ItemSaveForm form) {
		return itemRepository.save(form);
	}

	/**
	 * 아이템 수정
	 * 
	 * @param itemId - 수정할 아이템 ID
	 * @param form - 수정 폼 데이터
	 */
	public void updateItem(Long itemId, ItemUpdateForm form) {
		itemRepository.update(itemId, form);
	}

	/**
	 * 아이템 단건 조회 (못 찾으면 예외 발생)
	 * 
	 * @param itemId - 조회할 ID
	 * @return 아이템 객체
	 * @throws IllegalArgumentException - 존재하지 않을 경우
	 */
	public Item findItemOrThrow(Long itemId) {
		return itemRepository.findById(itemId)
				 .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다. id=" + itemId));
	}

	/**
	 * 아이템 조회 (Optional 반환)
	 * 
	 * @param itemId - 조회할 ID
	 * @return Optional<Item>
	 */
	public Optional<Item> findItem(Long itemId){
		return itemRepository.findById(itemId);
	}

	/**
	 * 모든 아이템 목록 반환
	 * @return 전체 아이템 리스트
	 */
	public List<Item> findAllItems() {
		return itemRepository.findAll();
	}

	/**
	 * 저장소 초기화 (테스트용)
	 */
	public void clearAll() {
		itemRepository.clearStorage();
	}

	/**
	 * 총 가격 유효성 검사
	 * 
	 * @param price - 가격
	 * @param quantity - 수량
	 * @param bindingResult - 에러 결과 저장 객체
	 */
	private void validateTotalPrice(Integer price, Integer quantity, BindingResult bindingResult) {
		if(price != null && quantity != null) {
			int totalPrice = price * quantity;
			if(totalPrice < MIN_TOTAL_PRICE) {
				// 글로벌 오류 등록
				bindingResult.reject("totalPriceMin", new Object[] {MIN_TOTAL_PRICE, totalPrice}, null);
			}
		}
	}

	/**
	 * 총 가격 유효성 검사 (저장용 폼)
	 * @param form - 저장용 폼 객체
	 * @param bindingResult - 에러 결과 저장 객체
	 */
	public void validateTotalPrice(ItemSaveForm form, BindingResult bindingResult) {
		validateTotalPrice(form.getPrice(), form.getQuantity(), bindingResult);
	}

	/**
	 * 총 가격 유효성 검사 (수정용 폼)
	 * @param form - 수정용 폼 객체
	 * @param bindingResult - 에러 결과 저장 객체
	 */
	public void validateTotalPrice(ItemUpdateForm form, BindingResult bindingResult) {
		validateTotalPrice(form.getPrice(), form.getQuantity(), bindingResult);
	}
}
