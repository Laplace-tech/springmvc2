package hello.login.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Getter
@Builder
@ToString
@AllArgsConstructor
public class Member {

	private final Long id;
	private final String loginId;
	private final String password;
	private final String name;

}
