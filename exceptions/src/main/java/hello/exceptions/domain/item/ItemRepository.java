package hello.exceptions.domain.item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import hello.exceptions.web.item.form.ItemSaveForm;
import hello.exceptions.web.item.form.ItemUpdateForm;
import lombok.extern.slf4j.Slf4j;

/**
 * 메모리 기반 아이템 저장소 (Repository)
 * 
 * 설계 목적:
 * - 개발 중 테스트용으로 사용하는 임시 저장소
 * - 동시성 문제를 피하기 위해 스레드 안전한 자료구조 사용
 * 
 * 구성 요소:
 * - ConcurrentHashMap : 멀티스레드 환경에서도 안전한 Map
 * - AtomicLong : 고유 ID 생성을 위한 스레드 안전한 카운터
 * 
 */
@Slf4j
@Repository
public class ItemRepository {

    // 메모리 저장소 (ID → Item)
    private final Map<Long, Item> itemStore = new ConcurrentHashMap<>();
    // ID 자동 증가 시퀀스
    private final AtomicLong sequence = new AtomicLong(0L);

    /**
     * ID로 아이템을 단건 조회
     * 
     * @param id 아이템 ID
     * @return 아이템이 존재하면 Optional.of(Item), 없으면 Optional.empty()
     */
    public Optional<Item> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(itemStore.get(id));
    }

    /**
     * 저장된 전체 아이템 목록 조회
     * 
     * @return 전체 아이템 리스트 (읽기 전용)
     */
    public List<Item> findAll() {
        return List.copyOf(itemStore.values());
    }

    /**
     * 새 아이템 저장
     * 
     * @param form 저장 폼 객체 (ID는 없음)
     * @return 저장된 아이템 객체 (ID 포함)
     */
    public Item save(ItemSaveForm form) {
        Long sequenceId = sequence.incrementAndGet(); // 새로운 ID 생성
        Item newItem = ItemMapper.toItem(sequenceId, form);
        itemStore.put(sequenceId, newItem);
        return newItem;
    }

    /**
     * 기존 아이템 수정
     * 
     * @param id 수정 대상 아이템의 ID
     * @param form 수정 폼 객체
     * @return 항상 true (수정 실패 시 예외 발생)
     * @throws IllegalArgumentException 아이템이 존재하지 않을 경우
     */
    public boolean update(Long id, ItemUpdateForm form) {
        Item oldItem = itemStore.get(id);
        if (oldItem == null) {
            throw new IllegalArgumentException("수정 대상 아이템을 찾을 수 없습니다. id=" + id);
        }

        Item updatedItem = oldItem.toBuilder()
                .itemName(form.getItemName())
                .price(form.getPrice())
                .quantity(form.getQuantity())
                .build();

        itemStore.put(id, updatedItem);
        return true;
    }

    /**
     * 저장소 초기화 (테스트용)
     * - 저장된 데이터 모두 삭제
     * - ID 시퀀스 초기화
     */
    public void clearStorage() {
        itemStore.clear();
        sequence.set(0L);
    }

}
