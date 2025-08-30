package hello.exceptions.web.item.form;

import lombok.Builder;
import lombok.Getter;

/**
 * View 전용 DTO 
 * - 도메인 Item 객체에서 뷰에 필요한 필드만 추출
 */
@Getter
@Builder
public class ItemViewDto {
	private Long id;
	private String itemName;
	private Integer price;
	private Integer quantity;
}
