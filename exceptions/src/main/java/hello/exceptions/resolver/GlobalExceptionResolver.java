package hello.exceptions.resolver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.LinkedHashMap;
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

/**
 * ✅ 스프링 MVC의 예외 처리 방식 요약
 * 
 * 1. HandlerExceptionResolver란?
 * -------------------------------------
 * - 스프링 MVC는 예외가 발생했을 때 DispatcherServlet이 
 *   HandlerExceptionResolver를 통해 예외를 처리함.
 * - 즉, 컨트롤러에서 예외가 발생해도 WAS(예: 톰캣)까지 전달되지 않고,
 *   스프링 내부에서 예외 처리를 마무리할 수 있음.
 *
 * - 결과적으로 WAS 입장에서는 "정상 처리된 응답"으로 간주됨.
 *   → HTTP 상태코드와 응답 바디 모두 스프링이 컨트롤 가능.
 *
 *
 * 2. ExceptionResolver의 장점
 * -------------------------------------
 * - 복잡한 WAS 오류 페이지 처리 로직을 피할 수 있음.
 * - JSON, HTML, TEXT 등 다양한 형태로 예외 응답을 구성 가능.
 * - 예외 발생 시점에서 컨트롤러처럼 로직 제어가 가능하여 유연함.
 * - 필터나 인터셉터 등에서 로깅을 하거나 상태 추적이 쉬움.
 *
 *
 * 3. ExceptionResolver가 예외를 못 처리하면?
 * -------------------------------------
 * - 스프링이 response.sendError(500) 등을 호출하여 WAS에게 예외 위임.
 * - 이후 동작 흐름:
 *
 *   → WAS가 등록된 오류 페이지 경로(/error 등)로 재호출
 *   → 스프링 부트에서는 BasicErrorController가 자동 등록되어 있음
 *   → DefaultErrorAttributes가 에러 정보를 모델에 담고
 *   → 뷰 이름(error/500 등)에 따라 HTML 오류 페이지 렌더링됨
 *
 * - 하지만 이 방식은 다음과 같은 단점이 있음:
 *   → 응답 제어가 어렵고 상황에 따라 예외 메시지가 노출될 수 있음
 *   → JSON 등 API 응답 처리에는 적합하지 않음
 *   → 중간에 여러 번의 디스패칭(내부 재요청)이 일어나기 때문에 복잡
 *
 *
 * ✅ 결론
 * -------------------------------------
 * - 가능하면 ExceptionResolver 또는 @ExceptionHandler를 사용해
 *   예외를 컨트롤러 수준에서 직접 처리하는 것이 가장 깔끔하고 확장성 높음.
 */


@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    /**
     * [ExceptionResolver의 핵심 개념]
     * 
     * 컨트롤러에서 예외가 발생해도 ExceptionResolver가 예외를 처리하면,
     * 예외가 서블릿 컨테이너까지 전달되지 않고 스프링 MVC에서 예외 처리가 종료된다.
     * 따라서 WAS 입장에서는 정상 처리된 것으로 간주된다.
     * 
     * 예외가 서블릿 컨테이너까지 전달되면 복잡하고 불필요한 추가 프로세스가 실행될 수 있는데,
     * ExceptionResolver를 사용하면 예외 처리 흐름이 훨씬 깔끔해진다.
     * 
     * ---------------------------------------------------------
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

        log.warn("GlobalExceptionResolver triggered : {}", ex.getClass().getName());

        // 1. 등록된 전략 목록 중에서 해당 예외를 처리할 수 있는 전략을 찾음
        ExceptionResolverStrategy selectedStrategy = strategyList.stream()
                .filter(strategy -> strategy.supports(ex))
                .findFirst()
                .orElseGet(DefaultExceptionStrategy::new); // 여기서만 fallback

        log.warn("Selected ExceptionResolverStrategy: {}", selectedStrategy.getClass().getSimpleName());

        // 2. 전략에서 상태 코드, 오류, 뷰 이름, 메시지 정보 추출
        int status = selectedStrategy.getStatusCode();
        String error = selectedStrategy.getError();
        String viewName = selectedStrategy.getViewName();
        String message = selectedStrategy.getMessage(ex);
        log.warn("Response : [{} {}], ViewName: {}, Message: {}", status, error, viewName, message);

        Map<String, Object> errorAttributes = buildErrorAttributes(ex, request, status, message, error);
        
        // 3. 응답 상태 코드 설정
        response.setStatus(status);     // 3. 응답 상태 코드 설정

        // 4. 요청 헤더에서 Accept 정보 확인 (JSON or HTML 판단용)
        String accept = request.getHeader("Accept");
        log.warn("Request Accept header: {}", accept);

        try {
            // 클라이언트가 JSON 응답을 기대할 경우 (Ajax 요청 등)
            if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
                String json = objectMapper.writeValueAsString(errorAttributes);

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
        
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addAllObjects(errorAttributes);
        return modelAndView; // 해당 뷰로 포워딩
    }

    @PostConstruct
    public void init() {
        log.info("GlobalExceptionResolver Initialized");
    }

    @PreDestroy
    public void destroy() {
        log.info("GlobalExceptionResolver Destroyed");
    }
    
    private Map<String, Object> buildErrorAttributes(Exception ex, HttpServletRequest request, int status, String message, String error) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", status);
        map.put("error", error);
        map.put("message", message);
        map.put("path", request.getRequestURI());
        map.put("exception", ex.getClass().getName());

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));  // 콘솔 출력 없이 sw에 저장
        String stackTrace = sw.toString();        // 문자열로 변환
        map.put("trace", stackTrace);              // 모델에 담기

        return map;
    }
} 