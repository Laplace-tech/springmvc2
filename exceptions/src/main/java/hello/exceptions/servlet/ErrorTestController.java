package hello.exceptions.servlet;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class ErrorTestController {

	@Deprecated
	@GetMapping("/error-test/ex")
	public void triggerException() {
		throw new RuntimeException("테스트용 예외 발생!");
	}

	@Deprecated
	@GetMapping("/error-test/404")
	public void trigger404(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), "404 테스트 오류");
	}

	@Deprecated
	@GetMapping("/error-test/500")
	public void trigger500(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "500 테스트 오류");
	}
}
