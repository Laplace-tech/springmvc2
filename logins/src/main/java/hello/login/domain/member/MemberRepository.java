package hello.login.domain.member;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import hello.login.web.member.form.MemberForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MemberRepository {

	private static final Map<Long, Member> memberStorage = new ConcurrentHashMap<>();
	private static AtomicLong sequence = new AtomicLong(0L);
	
	/**
     * 회원 저장
     */
	public Member saveMember(MemberForm memberForm) {
		long sequenceId = sequence.incrementAndGet();
		String encryptedPassword = memberForm.getPassword();
		
		// 원래 MemberForm 기반의 Member 생성
		Member newMember = Member.builder()
						.id(sequenceId)
						.name(memberForm.getName())
						.loginId(memberForm.getLoginId())
						.password(encryptedPassword)
						.build();

		memberStorage.put(sequenceId, newMember);
		log.debug("새 회원 저장됨 : {}", newMember);
		return newMember;
	}
	
    /**
     * ID로 회원 조회
     */
    public Optional<Member> findById(Long id) {
        if (id == null) {
            log.warn("findById 호출 시 id가 null");
            return Optional.empty();
        }
        return Optional.ofNullable(memberStorage.get(id));
    }
	
	
    /**
     * loginId로 회원 조회
     */
	public Optional<Member> findByLoginId(String loginId) {
		if(loginId == null || loginId.isBlank()) 
			return Optional.empty();
		
		return memberStorage.values().stream()
				.filter(member -> loginId.trim().equals(member.getLoginId()))
				.findAny();
	}
	
	/**
     * 모든 회원 조회
     */
	public List<Member> findAll() {
		return List.copyOf(memberStorage.values());
	}

    /**
     * 저장소 초기화 (테스트용)
     */
	public void clearStorage() {
		memberStorage.clear();
		sequence.set(0L);
		log.debug("회원 저장소 초기화됨");
	}

	public boolean isDuplicateLoginId(String loginId) {
		return findByLoginId(loginId).isPresent();
	}
	
}
