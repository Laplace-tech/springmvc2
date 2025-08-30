package hello.exceptions.exhandler.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import hello.exceptions.exception.UserException;
import hello.exceptions.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 📌 @RestControllerAdvice
 * - @ControllerAdvice + @ResponseBody 기능을 합친 애너테이션
 * - 전역적으로(모든 컨트롤러 대상) 예외를 잡아서 JSON 응답으로 처리 가능
 * - API 예외 처리에 특화된 전역 예외 처리기 역할
 * 
 * ✅ 실무에서 사용하는 이유:
 * - 컨트롤러의 정상 처리 코드와 예외 처리 코드를 분리하여 **관심사의 분리 (Separation of Concerns)** 실현
 * - 코드의 **가독성 향상**, **재사용성 증가**, **유지보수 용이**
 */
@Slf4j
@RestControllerAdvice(basePackages = "hello.exceptions.api")
public class ExControllerAdvice {

    /**
     * 📌 IllegalArgumentException 처리 핸들러
     * - 클라이언트가 잘못된 요청을 보낸 경우 발생하는 예외
     * - 400 Bad Request 상태 코드로 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult handleIllegalArgument(IllegalArgumentException e) {
        log.error("[ExControllerAdvice] IllgalArgumentException 예외 발생 : ", e);
        return new ErrorResult("Bad Request", e.getMessage());
    }

    /**
     * 📌 UserException 처리 핸들러
     * - 사용자 정의 예외에 대한 처리
     * - 상황에 따라 HTTP 상태 코드를 자유롭게 설정 가능
     */
    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult handleUserException(UserException e) {
        log.error("[ExControllerAdvice] UserException 예외 발생 : ", e);
        return new ErrorResult("User-Exception", e.getMessage());
    }

    /**
     * 📌 모든 예외의 최상위 처리 핸들러
     * - 위에서 처리되지 않은 예외는 이 핸들러로 넘어옴
     * - 서버 내부 오류로 간주하고 500 응답 반환
     * - 실제 운영에서는 여기서 **알림 시스템 연동**, **모니터링**도 수행 가능
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResult handleGenericException(Exception e) {
        log.error("[ExControllerAdvice] Exception 예외 발생 : ", e);
        return new ErrorResult("Generic Exception", "내부 오류");
    }
}
