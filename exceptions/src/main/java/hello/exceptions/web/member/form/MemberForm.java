package hello.exceptions.web.member.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class MemberForm {
	
	@NotBlank(message = "{member.loginId.notBlank}")
	private String loginId;
	
	@NotBlank(message = "{member.password.notBlank}")
	private String password;
	
	@NotBlank(message = "{member.name.notBlank}")
	private String name;
}
