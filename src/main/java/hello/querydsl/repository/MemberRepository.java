package hello.querydsl.repository;

import hello.querydsl.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 스프링 데이터 리포지토리에 사용자 정의 인터페이스 상속
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // SELECT m FROM Member m WHERE m.username = ?;
    List<Member> findByUsername(String username);
}
