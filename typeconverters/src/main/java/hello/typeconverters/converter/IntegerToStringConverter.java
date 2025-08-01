package hello.typeconverters.converter;


import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegerToStringConverter implements Converter<Integer, String>{
	
	@Override
	public String convert(Integer source) {
		log.info("convert source = {}", source);
		return String.valueOf(source);
	}

}
