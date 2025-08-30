package hello.login;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hello.login.argumentresolver.LoginMemberArgumentResolver;
import hello.login.filter.LogFilter;
import hello.login.filter.LoginCheckFilter;
import hello.login.interceptor.LogInterceptor;
import hello.login.interceptor.LoginCheckInterceptor;
import jakarta.servlet.Filter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new LoginMemberArgumentResolver());
	}
	
	/*
	 * "전체 경로(/**)에 대해 LogInterceptor를 적용하는데, 
	 *  정적 자원(/css/**, /*.ico)이나 /error 요청은 제외해라.
	 *  그리고 이 인터셉터는 가장 먼저(order = 1) 작동시켜라."
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LogInterceptor())
			.order(1)
			.addPathPatterns("/**")
			.excludePathPatterns("/css/**", "/*.ico", "/error");
		
		registry.addInterceptor(new LoginCheckInterceptor())
			.order(2)
			.addPathPatterns("/**")
			.excludePathPatterns("/", "/members/add", "/login", "/logout", 
					             "/css/**", "/*.ico", "/error");
	}
	
//  @Bean
    FilterRegistrationBean<Filter> logFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new LogFilter());
		filterRegistrationBean.setOrder(1);
		filterRegistrationBean.addUrlPatterns("/*");
		return filterRegistrationBean;
	}
	
//  @Bean
	FilterRegistrationBean<Filter> loginCheckFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new LoginCheckFilter());
		filterRegistrationBean.setOrder(2);
		filterRegistrationBean.addUrlPatterns("/*");
		return filterRegistrationBean;
	}
	
	
}
