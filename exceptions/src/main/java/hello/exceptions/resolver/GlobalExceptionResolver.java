package hello.exceptions.resolver;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import hello.exceptions.resolver.strategy.DefaultExceptionStrategy;
import hello.exceptions.resolver.strategy.ExceptionResolverStrategy;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    /*
     * [ExceptionResolver의 핵심 개념]
     * 
     * 컨트롤러에서 예외가 발생해도 ExceptionResolver가 예외를 처리하면,
     * 예외가 서블릿 컨테이너까지 전달되지 않고 스프링 MVC에서 예외 처리가 종료된다.
     * 따라서 WAS 입장에서는 정상 처리된 것으로 간주된다.
     * 
     * 예외가 서블릿 컨테이너까지 전달되면 복잡하고 불필요한 추가 프로세스가 실행될 수 있는데,
     * ExceptionResolver를 사용하면 예외 처리 흐름이 훨씬 깔끔해진다.
     * 
     * 
     * [resolveException() 반환값에 따른 동작]
     * 
     * 1. 빈 ModelAndView (new ModelAndView()) 반환:
     *    - 뷰를 렌더링하지 않고, 서블릿이 정상적으로 리턴되어 응답이 이미 완료됨을 의미.
     *    - 주로 JSON 등의 직접 응답 처리 시 사용.
     * 
     * 2. ModelAndView에 View 정보 지정하여 반환:
     *    - 지정한 뷰를 렌더링하며 응답 처리.
     * 
     * 3. null 반환:
     *    - 현재 ExceptionResolver가 처리하지 않음을 의미.
     *    - 다음 등록된 ExceptionResolver가 호출되어 예외 처리 시도.
     *    - 처리 가능한 ExceptionResolver가 없으면 예외가 서블릿 밖으로 던져짐.
     */
    
    // ObjectMapper는 JSON 문자열 변환에 사용됨
    private final ObjectMapper objectMapper;

    // 전략 패턴으로 구현된 예외 처리 전략 리스트
    private final List<ExceptionResolverStrategy> strategyList;

    /**
     * 예외 발생 시 DispatcherServlet이 호출하는 메서드
     * 
     * @param request  현재 HTTP 요청
     * @param response HTTP 응답
     * @param handler  요청을 처리하던 컨트롤러 핸들러 객체 (보통 사용하지 않음)
     * @param ex       발생한 예외
     * @return 예외 처리 결과로 사용한 ModelAndView 객체
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {

        log.warn("GlobalExceptionResolver triggered : {}", ex.getClass().getSimpleName());

        // 1. 등록된 전략 목록 중에서 해당 예외를 처리할 수 있는 전략을 찾음
        ExceptionResolverStrategy selectedStrategy = strategyList.stream()
                .filter(s -> s.supports(ex))
                .findFirst()
                .orElseGet(() -> new DefaultExceptionStrategy()); // 여기서만 fallback

        log.warn("Selected ExceptionResolverStrategy: {}", selectedStrategy.getClass().getSimpleName());

        // 2. 전략에서 상태 코드, 뷰 이름, 메시지 정보 추출
        int status = selectedStrategy.getStatusCode();
        String viewName = selectedStrategy.getViewName();
        String message = selectedStrategy.getMessage(ex);
        log.warn("Response status: {}, ViewName: {}, Message: {}", status, viewName, message);

        // 3. 응답 상태 코드 설정
        response.setStatus(status);

        // 4. 요청 헤더에서 Accept 정보 확인 (JSON or HTML 판단용)
        String accept = request.getHeader("Accept");
        log.warn("Request Accept header: {}", accept);

        try {
            // 클라이언트가 JSON 응답을 기대할 경우 (Ajax 요청 등)
            if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
                Map<String, Object> errorResult = Map.of(
                        "exception", ex.getClass().getSimpleName(), 
                        "message", message,
                        "status", status);
                String json = objectMapper.writeValueAsString(errorResult);

                // JSON 응답 설정
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("utf-8");
                response.getWriter().write(json);

                /**
                 * ✔ [중요] JSON을 직접 응답했기 때문에 더 이상 뷰를 렌더링할 필요 없음 
                 * 	=> 빈 ModelAndView 반환 → 스프링은 응답을 완료된 것으로 판단하고 종료함
                 */
                log.warn("JSON response written successfully");
                return new ModelAndView(); // 응답을 직접 완료했다는 의미
            }
        } catch (Exception e) {
            log.error("JSON 응답 처리 실패", e);
        }

        log.warn("Non-JSON request, forwarding to error view: {}", viewName);
        return new ModelAndView(viewName); // 해당 뷰로 포워딩
    }

    @PostConstruct
    public void init() {
        log.info("GlobalExceptionResolver Initialized");
    }

    @PreDestroy
    public void destroy() {
        log.info("GlobalExceptionResolver Destroyed");
    }
}
