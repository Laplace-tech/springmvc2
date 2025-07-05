package hello.login.domain.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Item {

	private Long id;
	private String itemName;
	private Integer price;
	private Integer quantity;

	public Item update(String itemName, Integer price, Integer quantity) {
		this.itemName = itemName;
		this.price = price;
		this.quantity = quantity;
		return this;
	}

}
