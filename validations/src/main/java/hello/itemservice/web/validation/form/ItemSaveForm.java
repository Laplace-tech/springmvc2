package hello.itemservice.web.validation.form;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ItemSaveForm {

	@NotBlank(message = "{item.itemName.notBlank}")
	private String itemName;
	
	@NotNull(message = "{item.price.notNull}")
	@Range(min = 1000, max = 1_000_000, message = "{item.price.range}")
	private Integer price;
	
	@NotNull(message = "{item.quantity.notNull}")
	@Range(min = 1, max = 9999, message = "{item.quantity.range}")
	private Integer quantity;
	
}
