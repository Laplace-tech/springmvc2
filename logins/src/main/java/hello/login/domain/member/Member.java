package hello.login.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@Builder
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Member {

	private Long id;
	private String loginId;
	private String password;
	private String name;

}
