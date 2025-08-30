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
	 * @param memberForm 사용자로부터 입력 받은 회원 가입 폼
	 * @return 저장된 Member 객체
	 */
	public Member save(MemberForm memberForm) {
		long sequenceId = sequence.incrementAndGet();
		Member newMember = MemberMapper.toMember(sequenceId, memberForm);
		memberStorage.put(sequenceId, newMember);
//		log.debug("새 회원 저장됨 : {}", newMember);
		return newMember;
	}
	
	/**
	 * ID로 회원 조회
	 * @param id 회원 ID
	 * @return 해당 ID의 회원 (없으면 Optional.empty)
	 */
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(memberStorage.get(id));
    }
	
	
    /**
	 * 로그인 ID로 회원 조회
	 * @param loginId 로그인 ID
	 * @return 해당 로그인 ID의 회원 (없으면 Optional.empty)
	 */
	public Optional<Member> findByLoginId(String loginId) {
		if(loginId == null) 
			return Optional.empty();
		
		return memberStorage.values().stream()
				.filter(member -> loginId.equals(member.getLoginId()))
				.findAny();
	}
	
	/**
	 * 전체 회원 목록 조회
	 */
	public List<Member> findAll() {
		return List.copyOf(memberStorage.values());
	}

	/**
	 * 저장소 초기화 (테스트용)
	 */
	public void clear() {
		memberStorage.clear();
		sequence.set(0L);
//		log.debug("회원 저장소 초기화됨");
	}

}
