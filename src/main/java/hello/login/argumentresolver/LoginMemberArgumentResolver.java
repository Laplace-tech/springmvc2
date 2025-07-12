package hello.login.argumentresolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import hello.login.domain.member.Member;
import hello.login.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 컨트롤러 메서드 파라미터에 (@Login Member loginMember) 파라미터가 있으면
 * 세션에서 로그인 회원 정보를 찾아 자동으로 주입해주는 ArgumentResolver
 */
@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * 이 리졸버가 해당 파라미터를 처리할 수 있는지 판단
	 * - @Login 애너테이션이 붙어 있고
	 * - 타입이 Member 또는 그 자식 타입일 경우
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		log.info("supportsParameter 실행");
		
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());
		
		return hasLoginAnnotation && hasMemberType;
	}

	/*
	 * 2. 세션에서 로그인 회원 객체를 꺼낸다
	 *  -> 위에서 조건에 맞았으면, 이 메서드가 호출됨
	 *  -> 이 메서드 안에서는 
	 *  	1. HTTP 요청 객체(HttpServletRequest)를 얻고
	 *  	2. 세션을 꺼낸 뒤
	 *  	3. session.getAttribute("LOGIN_MEMBER")를 해서 로그인된 회원을 찾음
	 *  	4. 그걸 컨트롤러 메서드에 자동으로 넣어줌
	 *  
	 *  실제 Argument 를 생성하는 로직
	 *  - 세션에서 로그인된 Member 객체를 session.getAttribute()를 사용해서 반환
	 *  - 세션이 없거나 세션에 Member 객체르 못찾으면 null 반환
	 */
	@Override
	public Object resolveArgument(
			MethodParameter parameter, 
			ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, 
			WebDataBinderFactory binderFactory) throws Exception {
		
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
		HttpSession session = request.getSession(false);
		
		if(session == null)
			return null;
		
		// 세션에서 로그인한 사용자 정보 반환
		Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
		log.info("세션에서 조회한 로그인 사용자: {}", loginMember);
		return loginMember;

	}

}
