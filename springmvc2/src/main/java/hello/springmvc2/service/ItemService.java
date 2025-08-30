package hello.springmvc2.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import hello.springmvc2.domain.item.Item;
import hello.springmvc2.form.item.ItemSaveForm;
import hello.springmvc2.form.item.ItemUpdateForm;
import hello.springmvc2.mapper.ItemMapper;
import hello.springmvc2.repository.ItemRepository;
import hello.springmvc2.web.exception.custom.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
    private final AtomicLong sequence = new AtomicLong(0L);
	
	public Item saveItem(ItemSaveForm form) {
		long sequenceId = sequence.incrementAndGet();
		Item item = ItemMapper.toItem(sequenceId, form);
		Item savedItem = itemRepository.save(item);
		return savedItem;
	}

	public Item findItemById(Long id) {
		return itemRepository.findById(id)
				.orElseThrow(() -> new ItemNotFoundException("아이템이 존재하지 않습니다. id : " + id));
	}

	public List<Item> findAllItems() {
		return itemRepository.findAll();
	}

	public boolean updateItem(Long id, ItemUpdateForm form) {
		Item exisingItem = itemRepository.findById(id)
				.orElseThrow(() -> new ItemNotFoundException("수정할 아이템이 존재하지 않습니다. id : " + id));

		Item updatedItem = exisingItem.toBuilder()
							.itemName(form.getItemName())
							.price(form.getPrice())
							.quantity(form.getQuantity())
							.build();
		
		return itemRepository.update(id, updatedItem);
	}

	public boolean deleteItem(Long id) {
		if(!itemRepository.delete(id)) {
			throw new ItemNotFoundException("삭제할 아이템이 존재하지 않습니다. id : " + id);
		}
		return true;
	}
}
