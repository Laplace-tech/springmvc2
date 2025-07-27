package hello.exceptions.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hello.exceptions.exception.BadRequestException;
import hello.exceptions.exception.UserException;
import lombok.extern.slf4j.Slf4j;

/**
 *  예외 처리 흐름 정리 (Spring MVC)
 *  
 *  [1] 클라이언트 → 요청
 *    → 필터 (LogFilter 등)
 *     → DispatcherServlet
 *     	→ 인터셉터 (preHandle)
 *       → 컨트롤러 실행 중 예외 발생
 *       
 *  [2] DispatcherServlet이 예외 감지
 *   → HandlerExceptionResolver 목록 순서대로 호출
 *    - ExceptionHandlerExceptionResolver (@ExceptionHandler)
 *    - ResponseStatusExceptionResolver (@ResponseStatus, ResponseStatusException)
 *    - DefaultHandlerExceptionResolver (타입 변환 오류 등)
 *    - 사용자 정의 Resolver
 *      · GlobalExceptionResolver (전략 패턴)
 *   
 *  [3] 적절한 리졸버가 응답 처리
 *   → JSON 또는 HTML 결정 (Accept 헤더 기준)
 *   
 *  [4] 모든 Resolver 실패 시
 *   → /error 로 내부 포워딩 → BasicErrorController 실행
 *   
 *  [5] 예외 처리 View 렌더링 이후
 *    → 인터셉터 afterCompletion
 *   → DispacherServlet → 필터 종료 → 응답 반환
 */

@Slf4j
@RestController
@RequestMapping("/api/test")
public class ApiExceptionController {

    // [1] 기본 런타임 예외 - 스프링 기본 /error 처리 흐름 (500)
    @GetMapping("/runtime-exception")
    public String runtimeEx() {
        log.info("예외 테스트: RuntimeException 발생");
        throw new RuntimeException("서버 내부 오류");
    }

    // [2] IllegalArgumentException - MyHandlerExceptionResolver 처리 대상
    @GetMapping("/illegal-argument")
    public String illegalArgumentEx() {
        log.info("예외 테스트: IllegalArgumentException 발생");
        throw new IllegalArgumentException("잘못된 인자");
    }

    // [3] 사용자 정의 예외 - UserHandlerExceptionResolver 처리 대상
    @GetMapping("/user-exception")
    public String userEx() {
        log.info("예외 테스트: UserException 발생");
        throw new UserException("사용자 정의 예외 발생");
    }

    // [4] @ResponseStatus 기반 예외 - 컴파일 시점에 HTTP 상태 지정
    @GetMapping("/response-status-ex1")
    public String responseStatusEx1() {
        log.info("예외 테스트: BadRequestException (@ResponseStatus)");
        throw new BadRequestException(); // @ResponseStatus 붙은 클래스
    }

    // [5] ResponseStatusException - 동적 상태 지정 (권장)
    @GetMapping("/response-status-ex2")
    public String responseStatusEx2() {
        log.info("예외 테스트: ResponseStatusException 동적 사용");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없음");
    }

    // [6] 기본 Handler 예외 처리 테스트 - 타입 변환 실패 (400)
    @GetMapping("/default-handler-ex")
    public String defaultHandlerEx(@RequestParam("data") Integer data) {
        log.info("정상 파라미터 data = {}", data);
        return "OK";
    }

    // [7] 정상 응답 확인
    @GetMapping("/member/{id}")
    public MemberDto getMember(@PathVariable String id) {
        log.info("정상 요청 처리: id = {}", id);
        return new MemberDto(id, "hello " + id);
    }

}
