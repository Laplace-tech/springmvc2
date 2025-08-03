package hello.springmvc2.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import hello.springmvc2.domain.item.Item;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class ItemRepository {

    private final Map<Long, Item> store = new ConcurrentHashMap<>();

    public Item save(Item item) {
    	store.put(item.getId(), item);
        log.info("Item saved: {}", item);
        return item;
    }
 
    public Optional<Item> findById(Long id) {
    	return Optional.ofNullable(store.get(id));
    }
    
    public List<Item> findAll() {
    	return List.copyOf(store.values());
    }
    
    public boolean update(Long id, Item updatedItem) {
    	if(!store.containsKey(id)) {
    		log.warn("Item update failed. id={} not found", id);
    		return false;
    	}
    	store.put(id, updatedItem);
    	log.info("Item updated: {}", updatedItem);
        return true;
    }
    
    public boolean delete(Long id) {
    	return store.remove(id) != null;
    }
}

