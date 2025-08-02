package hello.typeconverters.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hello.typeconverters.type.IpPort;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class helloController {

	@GetMapping("/hello-v1")
	public String helloV1(HttpServletRequest request) {
		String data = request.getParameter("data");
		Integer intValue = Integer.valueOf(data);
		return "ok " + intValue;
	}
	
	@GetMapping("/hello-v2")
	public String helloV2(@RequestParam("data") Integer data) {
		return "ok";
	}
	
	@GetMapping("/ip-port")
	public String ipPort(@RequestParam("ip-port") IpPort ipPort) {
		System.out.println("ipPort Ip : " + ipPort.getIp());
		System.out.println("ipPort Port : " + ipPort.getPort());
		return "ok";
	}
	
}
