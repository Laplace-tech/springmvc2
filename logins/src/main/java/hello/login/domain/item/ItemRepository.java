package hello.login.domain.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>(); // static
    private static long sequence = 0L; // static

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        log.info("Item saved: {}", item);
        return item;
    }

    public Item findById(Long id) {
        Item item = store.get(id);
        if (item != null) {
            log.info("Item found: id={}, item={}", id, item);
        } else {
            log.warn("Item not found: id={}", id);
        }
        return item;
    }

    public List<Item> findAll() {
        List<Item> items = new ArrayList<>(store.values());
        log.info("findAll called: total {} items", items.size());
        return items;
    }

    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);
        if (findItem != null) {
            log.info("Before update: {}", findItem);
            findItem.setItemName(updateParam.getItemName());
            findItem.setPrice(updateParam.getPrice());
            findItem.setQuantity(updateParam.getQuantity());
            log.info("After update: {}", findItem);
        } else {
            log.warn("Update failed: item not found with id={}", itemId);
        }
    }

    public void clearStore() {
        store.clear();
        log.info("Item store cleared");
    }
}
