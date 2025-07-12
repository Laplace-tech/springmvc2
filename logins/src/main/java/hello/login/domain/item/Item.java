package hello.login.domain.item;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 도메인 객체: Item (상품)
 * - 불변 객체로 설계되어 변경이 불가능함 (모든 필드 final)
 * - 생성 시 Lombok의 @Builder를 이용해 유연하게 객체 생성
 * - toBuilder = true 를 통해 기존 객체를 기반으로 수정된 사본을 쉽게 만들 수 있음
 */
@Getter
@ToString
@Builder(toBuilder = true)
public class Item {

    private final Long id;
    private final String itemName;
    private final Integer price;
    private final Integer quantity;
}
