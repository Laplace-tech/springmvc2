package hello.itemservice.domain.item;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Item {

    private Long id;
    
    /*
     * @NotBlank : 빈 값 + 공백만 있는 경우를 허용하지 않음
     */
    @NotBlank 
    private String itemName;
    
    /*
     * @NotNull : null 을 허용하지 않음
     * @Range(min = 1_000, max = 1_000_000)
     */
    @NotNull
    @Range(min = 1000, max = 1_000_000)
    private Integer price;
    
    @NotNull
    @Range(min = 1, max = 9999)
    private Integer quantity;

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
