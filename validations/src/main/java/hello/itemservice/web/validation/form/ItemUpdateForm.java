package hello.itemservice.web.validation.form;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUpdateForm {

	@NotNull
	private Long id;
	
	@NotBlank(message = "{item.itemName.notBlank}")
	private String itemName;
	
	@NotNull(message = "{item.price.notNull}")
	@Range(min = 1000, max = 1_000_000, message = "{item.price.range}")
	private Integer price;
	
	@NotNull(message = "{item.quantity.notNull}")
	@Range(min = 1, message = "{item.quantity.range.min}")
	private Integer quantity;
	
}
