package hello.login.domain.item;

import hello.login.web.item.form.ItemSaveForm;
import hello.login.web.item.form.ItemUpdateForm;

/**
 * ItemMapper
 * - DTO ↔ 도메인 객체 간 변환을 담당하는 유틸리티 클래스
 * - Controller ↔ Service ↔ Repository 사이에서 데이터 형식 전환에 사용됨
 */
public class ItemMapper {

    /**
     * ItemSaveForm → Item 변환
     * - 등록용 폼 데이터를 도메인 객체로 변환
     */
    public static Item toItem(long sequenceId, ItemSaveForm form) {
        return Item.builder()
        		.id(sequenceId)
                .itemName(form.getItemName())
                .price(form.getPrice())
                .quantity(form.getQuantity())
                .build();
    }

    /**
     * Item → ItemUpdateForm 변환
     * - 수정 폼을 위해 도메인 객체 데이터를 폼 객체로 변환
     */
    public static ItemUpdateForm toUpdateForm(Item item) {
        return ItemUpdateForm.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}
