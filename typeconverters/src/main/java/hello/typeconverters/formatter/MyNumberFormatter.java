package hello.typeconverters.formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyNumberFormatter implements Formatter<Number> {

	@Override
	public Number parse(String text, Locale locale) throws ParseException {
		log.info("text = {}, locale = {}", text, locale);
		// "1,000" -> 1000
		NumberFormat format = NumberFormat.getInstance(locale);
		return format.parse(text);
	}
	
	@Override
	public String print(Number object, Locale locale) {
		log.info("object = {}, locale = {}", object, locale);
		// 1000 -> "1,000"
		return NumberFormat.getInstance(locale).format(object);
	}

}
