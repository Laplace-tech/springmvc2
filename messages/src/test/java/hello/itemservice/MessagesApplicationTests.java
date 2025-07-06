package hello.itemservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

@SpringBootTest
class MessagesApplicationTests {

	@Autowired
	MessageSource messageSource;
	
	@Test
	void contextLoads() {
		String result = messageSource.getMessage("hello", null, null);
		assertThat(result).isEqualTo("안녕");
	}

	@Test
	void notFoundMessageCode() {
		assertThatThrownBy(() -> messageSource.getMessage("no_code", null, null))
		.isInstanceOf(NoSuchMessageException.class);
	}
	
	@Test
	void notFoundMessageCodeDefaultMessage() {
		String result = messageSource.getMessage("no_code", null, "기본 메세지", null);
		assertThat(result).isEqualTo("기본 메세지");
	}
	
	@Test
	void argumentMessage() {
		String result = messageSource.getMessage("hello.name", new Object[] {"동일 씨발련"}, null);
		assertThat(result).isEqualTo("안녕하세요 동일 씨발련아");
	}
	
	@Test
	void defaultLang() {
		assertThat(messageSource.getMessage("hello", null, null)).isEqualTo("안녕");
		assertThat(messageSource.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
	}
	
}
