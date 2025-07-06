package hello.login.domain.member;

import hello.login.web.member.form.MemberForm;

public class MemberMapper {

	public static Member toMember(long sequenceId, MemberForm form) {
		return Member.builder()
					.id(sequenceId)
					.loginId(form.getLoginId())
					.password(form.getPassword())
					.name(form.getName())
					.build();
	}	
	
}
