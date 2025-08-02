package hello.typeconverters.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.Data;

@Controller
public class FormatterController {

	@GetMapping("/formatter/edit")
	public String formatterForm(Model model) {
		Form form = new Form();
		form.setNumber(100_000_000);
		form.setLocalDateTime(LocalDateTime.now());
		
		model.addAttribute("form", form);
		return "formatter-form";
	}
	
	@PostMapping("/formatter/edit")
	public String formatterEdit(Form form) {
		return "formatter-view";
	}
	
	
	@Data
	static class Form {
		
		@NumberFormat(pattern = "###,###")
		private Integer number;
		
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime localDateTime;
	}
	
}
