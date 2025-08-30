package hello.exceptions.argumentresolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import hello.exceptions.domain.member.Member;
import hello.exceptions.session.SessionConst;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 커스텀 ArgumentResolver
 * 
 * 컨트롤러 메서드의 파라미터 중 
 *  - @Login 애너테이션이 붙고
 *  - 타입이 Member 또는 그 하위 타입일 경우,
 *  
 *  세션에서 로그인된 Member 객체를 찾아 자동으로 바인딩 해준다.
 */
@Slf4j
@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * 이 ArgumentResolver가 특정 파라미터를 지원하는지 여부 판단
	 * 
	 * Spring MVC는 모든 컨트롤러 메서드 파라미터에 대해 이 메서드를 호출하여
	 * 해당 리졸버가 처리할 대상인지 검사한다
	 * 
	 * @param parameter 컨트롤러 메서드 파라미터
	 * @return true : 이 리졸버가 처리할 대상 / false : 무시
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());
		log.info("supportsParameter - parameterName={}, hasLoginAnnotation={}, hasMemberType={}",
				parameter.getParameterName(), hasLoginAnnotation, hasMemberType);
		return hasLoginAnnotation && hasMemberType;
	}

	/**
	 * 실제 파라미터에 주입할 객체를 반환
	 * 
	 * - HttpServletRequest를 통해 세션을 가져온 후,
	 * - 세션에 저장된 로그인 회원 정보(Member)를 꺼내 리턴한다.
	 * - 세션에 없거나 회원 정보가 없으면 null 리턴
	 * 
	 * @return 로그인된 회원(Member) 또는 null
	 */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환

        if (session == null) {
            log.info("세션이 존재하지 않음");
            return null;
        }

        Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
        log.info("세션에서 조회한 로그인 사용자: {}", loginMember);
        return loginMember;
    }

	
	@PostConstruct
	public void init() {
		log.info("LoginMemberArgumentResolver Initialized");
	}
	
	@PreDestroy
	public void destroy() {
		log.info("LoginMemberArgumentsResolver Destroyed");
	}

}