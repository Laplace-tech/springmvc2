package hello.login.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MemberRepository {

    private static final Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    /**
     * 회원 저장
     */
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        log.info("save: member = {}", member);
        return member;
    }

    /**
     * ID로 회원 조회
     */
    public Member findById(Long id) {
        Member member = store.get(id);
        log.debug("findById: id = {}, member = {}", id, member);
        return member;
    }

    /**
     * loginId로 회원 조회
     */
    public Optional<Member> findByLoginId(String loginId) {
        Optional<Member> member = store.values().stream()
            .filter(m -> m.getLoginId().equals(loginId))
            .findAny();
        log.debug("findByLoginId: loginId = {}, found = {}", loginId, member.isPresent());
        return member;
    }

    /**
     * 모든 회원 조회
     */
    public List<Member> findAll() {
        List<Member> allMembers = new ArrayList<>(store.values());
        log.debug("findAll: count = {}", allMembers.size());
        return allMembers;
    }

    /**
     * 저장소 초기화 (테스트용)
     */
    public void clearStore() {
        store.clear();
        log.info("clearStore: store cleared");
    }
}
