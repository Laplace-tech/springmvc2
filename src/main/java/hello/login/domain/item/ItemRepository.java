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

/**
 * 메모리 기반 아이템 저장소
 * - 동시성 문제를 피하기 위해 ConcurrentHashMap, AtomicLong 사용
 * - 개발용 또는 테스트용 임시 저장소 역할
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
     * @param id 아이템 ID
     * @return 아이템이 존재하면 Optional<Item>, 없으면 Optional.empty()
     */
    public Optional<Item> findById(Long id) {
        if (id == null) {
//            log.warn("[findById] 호출 실패: id가 null입니다.");
            return Optional.empty();
        }
        return Optional.ofNullable(itemStore.get(id));
    }

    /**
     * 저장된 전체 아이템 목록 조회
     * @return 아이템 리스트 (불변)
     */
    public List<Item> findAll() {
        return List.copyOf(itemStore.values());
    }

    /**
     * 새 아이템 저장
     * @param itemWithoutId ID가 없는 상태의 아이템 객체
     * @return 저장된 아이템 (ID가 채워진 새 객체)
     */
    public Item save(ItemSaveForm form) {
        Long sequenceId = sequence.incrementAndGet(); // ID 생성
        Item newItem = ItemMapper.toItem(sequenceId, form);

        itemStore.put(sequenceId, newItem);
//        log.debug("[save] 아이템 저장됨: {}", newItem);
        return newItem;
    }

    /**
     * 기존 아이템 수정
     * @param id 수정 대상 아이템의 ID
     * @param form 수정 요청 정보 (폼 객체)
     * @return 수정 성공 여부 (항상 true, 실패 시 예외 발생)
     * @throws IllegalArgumentException 아이템이 존재하지 않을 경우
     */
    public boolean update(Long id, ItemUpdateForm form) {
        Item oldItem = itemStore.get(id);

        if (oldItem == null) {
//            log.warn("[updateItem] 수정 실패: 존재하지 않는 아이템 id={}", id);
            throw new IllegalArgumentException("수정 대상 아이템을 찾을 수 없습니다. id=" + id);
        }

        // 기존 객체 복사 후 일부 필드만 수정
        Item updatedItem = oldItem.toBuilder()
                .itemName(form.getItemName())
                .price(form.getPrice())
                .quantity(form.getQuantity())
                .build();

        itemStore.put(id, updatedItem);
//        log.debug("[updateItem] 아이템 수정 완료: {}", updatedItem);
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
//        log.debug("[clearStorage] 아이템 저장소가 초기화되었습니다.");
    }

}
