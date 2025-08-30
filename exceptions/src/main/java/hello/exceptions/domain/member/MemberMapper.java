package hello.exceptions.domain.member;

import hello.exceptions.web.member.form.MemberForm;

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
