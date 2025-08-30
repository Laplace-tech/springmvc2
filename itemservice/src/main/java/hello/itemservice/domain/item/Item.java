package hello.itemservice.domain.item;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Item {
	
	private Long id;
	private String itemName;
	private Integer price;
	private Integer quantity;
	
	private Boolean open;
	private List<String> regions;
	private ItemType itemType;
	private String deliveryCode;
	
	public Item(String itemName, Integer price, Integer quantity) {
		this.itemName = itemName;
		this.price = price;
		this.quantity = quantity;
	}
}
