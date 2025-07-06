package hello.login.domain.member;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import hello.login.web.member.form.MemberForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	
	/**
	 * 회원 저장 (중복 검사 포함)
	 * @param form 사용자 입력 폼
	 * @return 저장된 회원
	 */
	public Member saveMember(MemberForm memberForm) {
		if(isDuplicateLoginId(memberForm.getLoginId())) {
			throw new IllegalArgumentException("이미 존재하는 로그인 ID입니다 : " + memberForm.getLoginId());
		}
		return memberRepository.save(memberForm);
	}
	
	/**
	 * 모든 회원 목록 조회
	 */
	public List<Member> findAll() {
		return memberRepository.findAll();
	}
	
	/**
	 * 회원 ID로 조회
	 */
	public Optional<Member> findById(Long id) {
		return memberRepository.findById(id);
	}
	
	/**
	 * 로그인 ID로 조회
	 */
	public Optional<Member> findByLoginId(String loginId) {
		return memberRepository.findByLoginId(loginId);
	}
	
	/**
	 * 저장소 초기화 (테스트용)
	 */
	public void clear() {
		memberRepository.clear();
	}
	
	/*
	 * 로그인 ID 중복 여부 확인
	 */
	public boolean isDuplicateLoginId(String loginId) {
		return memberRepository.findByLoginId(loginId).isPresent();
	}
}
