package hello.login.domain.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class Item {

	private Long id;
	private String itemName;
	private Integer price;
	private Integer quantity;

	@Builder
	public Item(String itemName, Integer price, Integer quantity) {
		this.itemName = itemName;
		this.price = price;
		this.quantity = quantity;
		log.info("Item created: itemName={}, price={}, quantity={}", itemName, price, quantity);
	}

	public void changePrice(Integer newPrice) {
		log.info("Changing price: from {} to {}", this.price, newPrice);
		this.price = newPrice;
	}

	public void changeQuantity(Integer newQuantity) {
		log.info("Changing quantity: from {} to {}", this.quantity, newQuantity);
		this.quantity = newQuantity;
	}
}
