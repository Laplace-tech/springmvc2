package hello.springmvc2.mapper;

import hello.springmvc2.domain.item.Item;
import hello.springmvc2.form.item.ItemSaveForm;
import hello.springmvc2.form.item.ItemUpdateForm;

public class ItemMapper {

	public static ItemUpdateForm toItemUpdateForm(Item item) {
		return ItemUpdateForm.builder()
				.id(item.getId())
				.itemName(item.getItemName())
				.price(item.getPrice())
				.quantity(item.getQuantity())
				.build();
	}
	
	public static Item toItem(Long Id, ItemSaveForm saveForm) {
		return Item.builder()
					.id(Id)
					.itemName(saveForm.getItemName())
					.price(saveForm.getPrice())
					.quantity(saveForm.getQuantity())
					.build();
	}
	
	public static Item toItem(ItemSaveForm saveForm) {
		return Item.builder()
					.itemName(saveForm.getItemName())
					.price(saveForm.getPrice())
					.quantity(saveForm.getQuantity())
					.build();
	}
}
