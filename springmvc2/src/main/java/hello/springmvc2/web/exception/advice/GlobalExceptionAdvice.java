package hello.springmvc2.web.exception.advice;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;

import hello.springmvc2.web.exception.custom.ItemNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

	private final ErrorAttributes errorAttributes;

	/** 404 - Not Found */
	@ExceptionHandler(ItemNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleItemNotFound(ItemNotFoundException ex, 
									 HttpServletRequest request,  
									 HttpServletResponse response,
									 Model model) {
		
		response.setStatus(HttpStatus.NOT_FOUND.value());
		Map<String, Object> errorAttributes = buildErrorAttributes(request, response);
		log.info("{}", errorAttributes);
		
		model.addAttribute("errorMessage", ex.getMessage());
		model.addAllAttributes(errorAttributes);
		return "error/404";
	}

	private Map<String, Object> buildErrorAttributes(HttpServletRequest request, HttpServletResponse response) {
	    ErrorAttributeOptions options = ErrorAttributeOptions.of(
	        ErrorAttributeOptions.Include.EXCEPTION,
	        ErrorAttributeOptions.Include.STACK_TRACE
	    );
	    Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(new ServletWebRequest(request, response), options);

	    // 강제로 기본 정보 채우기 (값이 없으면 넣기)
	    errorAttributes.putIfAbsent("status", response.getStatus());
	    errorAttributes.putIfAbsent("error", HttpStatus.valueOf(response.getStatus()).getReasonPhrase());
	    errorAttributes.putIfAbsent("path", request.getRequestURI());
	    errorAttributes.putIfAbsent("message", "No message available");

	    return errorAttributes;
	}



}
