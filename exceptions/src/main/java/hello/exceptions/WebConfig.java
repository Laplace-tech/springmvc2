package hello.exceptions;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hello.exceptions.argumentresolver.LoginMemberArgumentResolver;
import hello.exceptions.filter.LogFilter;
import hello.exceptions.filter.LoginCheckFilter;
import hello.exceptions.interceptor.LogInterceptor;
import hello.exceptions.interceptor.LoginCheckInterceptor;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;

/*
 * ✅ [1단계: 최초 요청 처리 흐름 - 예외 발생 상황]
 * 
 * 1. 클라이언트가 /error-test/ex 등 요청을 보냄
 * 
 * 2. DispatcherServlet 진입 (스프링 MVC 진입점)
 * 
 * 3. 스프링 인터셉터 실행
 *    - LogInterceptor.preHandle() 실행 (요청 경로가 매칭되면)
 *    - LoginCheckInterceptor.preHandle() 실행
 * 
 * 4. 컨트롤러 실행 중 예외 발생 (예: RuntimeException)
 *    → 이 경우 postHandle()은 실행되지 않고 바로 예외 처리 흐름으로 이동
 * 
 * 5.  LogInterceptor.afterCompletion(ex) 호출 (예외 객체 전달됨)
 * 
 * 6. 예외는 DispatcherServlet을 통해 WAS(서블릿 컨테이너)로 전달됨
 * 
 * -------------------------------------------------------------------
 * 
 * ✅ [2단계: 오류 처리 흐름 - 내부 에러 페이지 요청]
 * 
 * 7. WAS가 ErrorPage 설정에 따라 "/error" 경로로 내부 디스패치 시작
 *    (DispatcherType = ERROR 상태)
 * 
 * 8. DispatcherServlet 재진입 (ERROR DispatcherType)
 * 
 * 9. 스프링 인터셉터가 다시 실행됨
 *    - LogInterceptor.preHandle() 호출됨 (경로가 매칭되면)
 *    - postHandle()과 afterCompletion()도 정상 실행됨
 *    (인터셉터는 DispatcherType 구분 없이 URL 경로 기반으로 동작)
 * 
 * 10. BasicErrorController가 "/error" 요청을 처리하여 에러 정보를 모델에 세팅
 * 
 * 11. ViewResolver가 에러에 맞는 뷰 (예: error/404.html, error/500.html) 렌더링
 * 
 * 12. 완성된 오류 페이지가 클라이언트로 응답 전송
 */

@Configuration  
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new LoginMemberArgumentResolver());
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(new LogInterceptor())       
	            .order(1)
	            .addPathPatterns("/**")
	            .excludePathPatterns("/css/**", "/*.ico");

		registry.addInterceptor(new LoginCheckInterceptor()) 
				.order(2)
				.addPathPatterns("/**")
				.excludePathPatterns("/", "/members/add", "/login", "/logout",
									 "/css/**", "/*.ico", "/error", "/error-page/**",
									 "/error-test/**");
	}
	
//	@Bean
	FilterRegistrationBean<Filter> logFilter() {
	    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
	    filterRegistrationBean.setFilter(new LogFilter());   
	    filterRegistrationBean.setOrder(1); 
	    filterRegistrationBean.addUrlPatterns("/*"); 
	    filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
	    return filterRegistrationBean;
	}

//	@Bean
	FilterRegistrationBean<Filter> loginCheckFilter() {
	    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
	    filterRegistrationBean.setFilter(new LoginCheckFilter()); 
	    filterRegistrationBean.setOrder(2); 
	    filterRegistrationBean.addUrlPatterns("/*"); 
	    filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
	    return filterRegistrationBean;
	}

} 
