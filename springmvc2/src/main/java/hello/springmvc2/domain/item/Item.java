package hello.springmvc2.domain.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @Getter
 * - 모든 필드에 대해 getter 메서드를 자동 생성
 * 
 * @ToString  
 * - 객체의 모든 필드를 포함하는 toString() 메서드를 자동 생성
 * - 로깅, 디버깅 시 객체 내용을 보기 좋게 출력
 * 
 * @Builder
 * - 빌더 패턴을 자동 생성해서 객체 생성 시 가독성과 안정성을 높임
 * - @Builder를 쓰면 모든 필드를 받는 생성자가 내부적으로 필요 -> @AllArgsConstructor가 있어야 함
 * 
 * @EqualsAndHashCode(of = "id")
 * - equals()와 hashCode() 메서드를 자동 생성
 * - of = "id" -> id 필드만 비교/해시생성 기준으로 사용함
 * 
 * @NoArgsConstructor
 * - 기본 생성자를 자동으로 만들어주는 애너테이션
 * 
 * @AllArgsConstructor
 * - 모든 필드를 파라미터로 받는 생성자 자동 생성
 */

@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
public class Item {

	private final Long id;
	private final String itemName;
	private final Integer price;
	private final Integer quantity;
	
}
