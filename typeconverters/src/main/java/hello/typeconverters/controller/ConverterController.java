package hello.typeconverters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import hello.typeconverters.type.IpPort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Controller
public class ConverterController {

	@GetMapping("/converter-view")
	public String convertView(Model model) {
		model.addAttribute("number", 100_000);
		model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
		return "converter-view";
	}
	
	@GetMapping("/converter/edit")
	public String convertForm(Model model) {
		IpPort ipPort = new IpPort("127.0.0.1", 8080);
		Form form = new Form(ipPort);
		model.addAttribute("form", form);
		return "converter-form";
	}
	
	@PostMapping("/converter/edit")
	public String convertEdit(Form form, Model model) {
		IpPort ipPort = form.getIpPort();
		model.addAttribute("ipPort", ipPort);
		return "converter-view";
	}
	
	@NoArgsConstructor
	@Setter
	@Getter
	static class Form {
		
		private IpPort ipPort;
		
		public Form(IpPort ipPort) {
			this.ipPort = ipPort;
		}
		
	}
	
}
