package hello.login.domain.member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class Member {

	private Long id;

	@NotEmpty
	private String loginId;

	@NotEmpty
	private String name;

	@NotEmpty
	private String password;

	@Builder
	public Member(String loginId, String name, String password) {
		this.loginId = loginId;
		this.name = name;
		this.password = password;
	}
}
