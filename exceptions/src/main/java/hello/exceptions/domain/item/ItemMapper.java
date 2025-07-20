package hello.exceptions.domain.item;

import hello.exceptions.web.item.form.ItemSaveForm;
import hello.exceptions.web.item.form.ItemUpdateForm;
import hello.exceptions.web.item.form.ItemViewDto;

/**
 * ItemMapper
 *
 * 도메인 객체(Item)와 폼 객체(ItemSaveForm, ItemUpdateForm) 간의 변환을 담당하는 유틸리티 클래스.
 * 주로 컨트롤러, 서비스, 리포지토리 사이의 데이터 흐름에서 객체 타입을 맞춰주는 역할을 함
 */
public class ItemMapper {

    /**
     * ItemSaveForm → Item 변환
     * 
     * @param sequenceId 새로 생성한 ID
     * @param form 등록 폼 객체
     * @return 변환된 Item 객체
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
	 *
	 * @param item 도메인 객체
	 * @return 변환된 수정 폼 객체
     */
    public static ItemUpdateForm toUpdateForm(Item item) {
        return ItemUpdateForm.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
    
    /**
     * Item → ItemViewDto 변환
	 *
	 * @param item 도메인 객체
	 * @return 변환된 View DTO 객체
     */
    public static ItemViewDto toViewDto(Item item) {
    	return ItemViewDto.builder()
    			.id(item.getId())
    			.itemName(item.getItemName())
    			.price(item.getPrice())
    			.quantity(item.getQuantity())
    			.build();
    }
    
    
}
