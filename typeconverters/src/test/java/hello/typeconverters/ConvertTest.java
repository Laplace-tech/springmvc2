package hello.typeconverters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hello.typeconverters.converter.IntegerToStringConverter;
import hello.typeconverters.converter.IpPortToStringConverter;
import hello.typeconverters.converter.StringToIntegerConverter;
import hello.typeconverters.converter.StringToIpPortConverter;
import hello.typeconverters.type.IpPort;

public class ConvertTest {


    @Test
    void stringToInteger() {
        StringToIntegerConverter converter = new StringToIntegerConverter();
        Integer result = converter.convert("10");
        assertThat(result).isEqualTo(10);
    }

    @Test
    void integerToString() {
        IntegerToStringConverter converter = new IntegerToStringConverter();
        String result = converter.convert(10);
        assertThat(result).isEqualTo("10");
    }
	
    @Test
    void ipPortToString() {
        IpPortToStringConverter converter = new IpPortToStringConverter();
        IpPort source = new IpPort("127.0.0.1", 8080);
        String result = converter.convert(source);
        assertThat(result).isEqualTo("127.0.0.1:8080");
    }

    @Test
    void stringToIpPort() {
        StringToIpPortConverter converter = new StringToIpPortConverter();
        String source = "127.0.0.1:8080";
        IpPort result = converter.convert(source);

        /** MEMO: isEqualTo 사용이 가능한 이유
         * @EqualsAndHashCode 을 사용하는 경우 객체 자체를 비교하는 것이 아닌
         * 객체의 값을 비교하기 때문이다. (IpPort 클래스에서 사용)
         */
        assertThat(result).isEqualTo(new IpPort("127.0.0.1", 8080));
    }
}
