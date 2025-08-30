package hello.exceptions.web.login.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginForm {

	@NotEmpty(message = "{login.form.loginId}")
	private String loginId;
	
	@NotEmpty(message = "{login.form.password}")
	private String password;
	
}
