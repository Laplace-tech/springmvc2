package hello.typeconverters;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hello.typeconverters.converter.IntegerToStringConverter;
import hello.typeconverters.converter.IpPortToStringConverter;
import hello.typeconverters.converter.StringToIntegerConverter;
import hello.typeconverters.converter.StringToIpPortConverter;
import hello.typeconverters.formatter.MyNumberFormatter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
//		registry.addConverter(new IntegerToStringConverter());
//		registry.addConverter(new StringToIntegerConverter());
		registry.addConverter(new IpPortToStringConverter());
		registry.addConverter(new StringToIpPortConverter());
		registry.addFormatter(new MyNumberFormatter());
	}
	
}
