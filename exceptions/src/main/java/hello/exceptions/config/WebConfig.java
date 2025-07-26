package hello.exceptions.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hello.exceptions.argumentresolver.LoginMemberArgumentResolver;
import hello.exceptions.interceptor.LogInterceptor;
import hello.exceptions.interceptor.LoginCheckInterceptor;
import hello.exceptions.resolver.GlobalExceptionResolver;
import lombok.RequiredArgsConstructor;

@Configuration  
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoginMemberArgumentResolver loginMemberArgumentsResolver;
	
	private final GlobalExceptionResolver globalExceptionResolver;
	
	private final LogInterceptor logInterceptor;
	private final LoginCheckInterceptor loginCheckInterceptor;
	
	private final static String[] STATIC_RESOURCES = { "/css/**", "/*.ico" };
	private final static String[] AUTH_WHITELIST = { 
			"/", "/members/add", "/login", "/logout", 
			"/error", "/error-page/**", "/error-test/**",
			"/api/**"};


    /**
     * [스프링 MVC 설정 시, ArgumentResolver 또는 ExceptionResolver 등록 시 주의점]
     * 
     * 1. 직접 new 연산자로 객체를 생성해서 등록하는 경우
     * 	- 해당 객체를 Spring 컨테이너에서 관리하지 않는 일반 객체가 됨
     *  - 따라서 @Autowried, @Value, @PostConstruct 등 스프링 의존성 주입 및 
     *    라이프사이클 콜백이 작동하지 않음.
     *  - 의존성 주입이 필요한 컴포넌트라면 내부 필드가 null 이 되거나 초기화가 되지 않아 오류 발생 가능
     *  
     * 2. 스프링 빈으로 등록된 객체를 주입받아 등록하는 경우 (권장 방법)
     * 	- Spring 컨테이너가 객체를 관리하므로 @Autowired 등 의존성 주입이 정상 동작
     *  - 초기화 메서드( @PostConstruct 등)도 정상 호출됨
     *  - 유지보수 및 테스트가 용이해지고, 스프링의 다양한 기능 활용 가능
     *  
     */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
	    resolvers.add(loginMemberArgumentsResolver);
	}
	
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
		resolvers.add(globalExceptionResolver);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {	
		registry.addInterceptor(logInterceptor)
				.order(1)
				.addPathPatterns("/**")
				.excludePathPatterns(STATIC_RESOURCES);
		
		registry.addInterceptor(loginCheckInterceptor)
				.order(2)
				.addPathPatterns("/**")
				.excludePathPatterns(concatArrays(STATIC_RESOURCES, AUTH_WHITELIST));
	}
	
	
	private String[] concatArrays(String[] a, String[] b) {
		String[] result = new String[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
	
//	private final LogFilter logFilter;
//	private final LoginCheckFilter loginCheckFilter;
//	
//	@Bean
//	FilterRegistrationBean<Filter> logFilterRegistration() {
//	    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
//	    filterRegistrationBean.setFilter(logFilter); // 로그 필터 등록
//	    filterRegistrationBean.setOrder(1); // 필터 순서 (먼저 실행됨)
//	    filterRegistrationBean.addUrlPatterns("/*"); // 모든 요청에 적용
//	    filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.FORWARD);
//	    return filterRegistrationBean;
//	}
//
//	@Bean
//	FilterRegistrationBean<Filter> loginCheckFilterRegistration() {
//	    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
//	    filterRegistrationBean.setFilter(loginCheckFilter); // 로그인 체크 필터 등록
//	    filterRegistrationBean.setOrder(2); // 로그 필터 다음에 실행
//	    filterRegistrationBean.addUrlPatterns("/*"); 
//	    filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
//	    return filterRegistrationBean;
//	}

} 
