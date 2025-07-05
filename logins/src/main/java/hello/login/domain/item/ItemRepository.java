package hello.login.domain.item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import hello.login.web.item.form.ItemSaveForm;
import hello.login.web.item.form.ItemUpdateForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ItemRepository {

	private static final Map<Long, Item> itemStorage = new ConcurrentHashMap<>();
	private static AtomicLong sequence = new AtomicLong(0L);
	
	/**
	 * 아이템 단건 조회
	 */
	public Optional<Item> findById(Long id) {
		if(id == null) {
			log.warn("findById 호출 시 id가 null입니다.");
			return Optional.empty();
		}
		return Optional.ofNullable(itemStorage.get(id));
	}
	
	/**
	 * 전체 아이템 조회
	 */
	public List<Item> findAll() {
		return List.copyOf(itemStorage.values());
	}
	
	
	/**
	 * 아이템 저장
	 */
	public Item saveItem(ItemSaveForm saveForm) {
		
		long sequenceId = sequence.incrementAndGet();
		
		Item newItem = Item.builder()
						.id(sequenceId)
						.itemName(saveForm.getItemName())
						.price(saveForm.getPrice())
						.quantity(saveForm.getQuantity())
						.build();
		
		itemStorage.put(sequenceId, newItem);
		
		log.debug("새 아이템 저장됨 : {}", newItem);
		return newItem;
	}
	
	/**
	 * @param itemId : 수정할 아이템 ID
	 * @param updateForm : 수정할 정보가 담긴 폼
	 * @return : 수정 성공 여부
	 * 
	 */
	public boolean updateItem(Long itemId, ItemUpdateForm updateForm) {
		Item item = findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("수정 대상 아이템을 찾을 수 없습니다."));
		
		item.update(updateForm.getItemName(), updateForm.getPrice(), updateForm.getQuantity());
		
		itemStorage.put(itemId, item);
		log.debug("item updated : {}", item);
		return true;
	}
	
	/**
	 * 저장소 초기화 (테스트용)
	 */
	public void clearStorage() {
		itemStorage.clear();
		sequence.set(0L);
		log.debug("아이템 저장소 초기화됨");
	}
}
