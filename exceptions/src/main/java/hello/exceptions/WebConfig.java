package hello.exceptions;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hello.exceptions.argumentresolver.LoginMemberArgumentResolver;
import hello.exceptions.filter.LogFilter;
import hello.exceptions.filter.LoginCheckFilter;
import hello.exceptions.interceptor.LogInterceptor;
import hello.exceptions.interceptor.LoginCheckInterceptor;
import hello.exceptions.resolver.MyHandlerExceptionResolver;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

@Configuration  
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoginMemberArgumentResolver loginMemberArgumentsResolver;

	private final static String[] STATIC_RESOURCES = { "/css/**", "/*.ico" };
	private final static String[] AUTH_WHITELIST = { 
			"/", "/members/add", "/login", "/logout", 
			"/error", "/error-page/**", "/error-test/**",
			"/api/**"};
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {	
		registry.addInterceptor(new LogInterceptor())
				.order(1)
				.addPathPatterns("/**")
				.excludePathPatterns(STATIC_RESOURCES);
		
		registry.addInterceptor(new LoginCheckInterceptor())
				.order(2)
				.addPathPatterns("/**")
				.excludePathPatterns(concatArrays(STATIC_RESOURCES, AUTH_WHITELIST));
	}
	
    /**
     * resolvers.add(new LoginMemberArgumentResolver());
     * 
     * 직접 new 연산자로 LoginMemberArgumentResolver 객체를 생성해서 등록하면,
     * 이 객체는 스프링 컨테이너가 관리하지 않는 일반 객체가 된다
     *
     * 따라서 @Autowired, @PostConstruct 같은 스프링 기능이 작동하지 않고,
     * 의존성 주입도 이루어지지 않으며, 초기화 메서드도 호출되지 않는다.
     *
     * 그래서 스프링 빈으로 등록된 LoginMemberArgumentResolver를 주입받아서
     * 아래처럼 등록해야만 스프링 관리 객체로서 정상 동작한다.
     */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
	    resolvers.add(loginMemberArgumentsResolver);
	}
	
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
		resolvers.add(new MyHandlerExceptionResolver());
	}
	
//	@Bean
	FilterRegistrationBean<Filter> logFilter() {
	    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
	    filterRegistrationBean.setFilter(new LogFilter()); // 로그 필터 등록
	    filterRegistrationBean.setOrder(1); // 필터 순서 (먼저 실행됨)
	    filterRegistrationBean.addUrlPatterns("/*"); // 모든 요청에 적용
	    filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
	    return filterRegistrationBean;
	}

//	@Bean
	FilterRegistrationBean<Filter> loginCheckFilter() {
	    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
	    filterRegistrationBean.setFilter(new LoginCheckFilter()); // 로그인 체크 필터 등록
	    filterRegistrationBean.setOrder(2); // 로그 필터 다음에 실행
	    filterRegistrationBean.addUrlPatterns("/*"); 
	    filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
	    return filterRegistrationBean;
	}

	private String[] concatArrays(String[] a, String[] b) {
		String[] result = new String[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
	
} 
