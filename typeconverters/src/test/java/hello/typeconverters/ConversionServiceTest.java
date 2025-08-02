package hello.typeconverters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import hello.typeconverters.converter.IntegerToStringConverter;
import hello.typeconverters.converter.IpPortToStringConverter;
import hello.typeconverters.converter.StringToIntegerConverter;
import hello.typeconverters.converter.StringToIpPortConverter;
import hello.typeconverters.type.IpPort;

public class ConversionServiceTest {

	@Test
	public void conversionService() {
		DefaultConversionService conversionService = new DefaultConversionService();
		conversionService.addConverter(new StringToIntegerConverter());
		conversionService.addConverter(new IntegerToStringConverter());
		conversionService.addConverter(new StringToIpPortConverter());
		conversionService.addConverter(new IpPortToStringConverter());
		
		assertThat(conversionService.convert("100", Integer.class)).isEqualTo(100);
		assertThat(conversionService.convert(10, String.class)).isEqualTo("10");
		assertThat(conversionService.convert("127.0.0.1:8080", IpPort.class)).isEqualTo(new IpPort("127.0.0.1", 8080));
		assertThat(conversionService.convert(new IpPort("127.0.0.1", 8080), String.class)).isEqualTo("127.0.0.1:8080");
	}
	
}
