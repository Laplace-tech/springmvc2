package hello.typeconverters.converter;

import org.springframework.core.convert.converter.Converter;

import hello.typeconverters.type.IpPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringToIpPortConverter implements Converter<String, IpPort>{
	
	@Override
	public IpPort convert(String source) {
		log.info("convert source : {}", source);
		// 127.0.0.1:8080
		String[] split = source.split(":");
		String ip = split[0];
		int port = Integer.valueOf(split[1]);
		return new IpPort(ip, port);
	}

}
