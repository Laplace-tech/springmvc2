package hello.springmvc2.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hello.springmvc2.domain.item.Item;
import hello.springmvc2.form.item.ItemSaveForm;
import hello.springmvc2.form.item.ItemUpdateForm;
import hello.springmvc2.mapper.ItemMapper;
import hello.springmvc2.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

	private final ItemService itemService;
	
	@GetMapping
	public String listItems(Model model) {
		List<Item> itemList = itemService.findAllItems();
		model.addAttribute("items", itemList);
		return "items/list"; // templates/items/list.html
	}
	
	@GetMapping("/{itemId}")
	public String getItem(@PathVariable("itemId") Long id, Model model) {
		Item item = itemService.findItemById(id);
		model.addAttribute("item", item);
		return "items/detail"; // templates/items/detail.html
	}
	
	@GetMapping("/add")
	public String addForm(ItemSaveForm form) {
		return "items/addForm";
	}
	
	
	@PostMapping("/add")
	public String saveItem(@Validated ItemSaveForm form, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			log.warn("상품 등록 유효성 실패 : {}", bindingResult);
			return "items/addForm";
		}
		
		Item savedItem = itemService.saveItem(form);
		return "redirect:/items/" + savedItem.getId();
	}
	
	@GetMapping("/{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long id, Model model) {
		Item item = itemService.findItemById(id);
		ItemUpdateForm form = ItemMapper.toItemUpdateForm(item);
		model.addAttribute("form", form);
		return "items/editForm";
	}
	
    @PostMapping("/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long id,
                             @Validated ItemUpdateForm form,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("상품 수정 유효성 실패: {}", bindingResult);
            return "items/editForm";
        }
        itemService.updateItem(id, form);
        return "redirect:/items/" + id;
    }
	
}
