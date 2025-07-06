package hello.itemservice.domain.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class ItemRepository {

	private static final Map<Long, Item> store = new HashMap<>();
	private static long sequence = 0L;

	public Item save(Item item) {
		item.setId(++sequence);
		store.put(sequence, item);
		return item;
	}

	public Item findById(Long itemId) {
		return store.get(itemId);
	}

	public List<Item> findAll() {
		return new ArrayList<>(store.values());
	}

	public void update(Long itemId, Item updateParam) {
		Item oldItem = this.findById(itemId);
		oldItem.setItemName(updateParam.getItemName());
		oldItem.setPrice(updateParam.getPrice());
		oldItem.setQuantity(updateParam.getQuantity());
		oldItem.setOpen(updateParam.getOpen());
		oldItem.setRegions(updateParam.getRegions());
		oldItem.setItemType(updateParam.getItemType());
		oldItem.setDeliveryCode(updateParam.getDeliveryCode());
	}

	public void clearStore() {
		store.clear();
	}

}
