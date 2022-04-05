package hello.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.querydsl.entity.Member;
import hello.querydsl.entity.QMember;
import hello.querydsl.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static hello.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.*;

/**
 * JPQL vs Querydsl 테스트 코드
 */
@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {

        // EntityManager 로 JPAQueryFactory 생성
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    /**
     * JPQL 로 member1 조회
     *
     * JPQL 은 문자로 실행 시점에 오류
     *
     * 파라미터 바인딩 직접
     */
    @Test
    public void startJPQL() {

        String qlString = "SELECT m FROM Member m WHERE m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                                .setParameter("username", "member1")
                                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * Querydsl 로 member1 조회
     *
     * QueryDsl 은 코드로 컴파일 시점 오류
     *
     * 파라미터 바인딩 자동 처리
     */
    @Test
    public void startQuerydsl() {

        QMember m = new QMember("m");

        Member findMember = queryFactory.select(m)
                                        .from(m)
                                        .where(m.username.eq("member1"))
                                        .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 기본 인스턴스를 static import 와 함께 사용
     *
     * Q 클래스 인스턴스를 사용하는 2가지 방법
     * - 별칭 직접 지정
     *      예) QMember qMember = new QMember("m");
     * - 기본 인스턴스 사용
     *      예) QMember qMember = QMember.member;
     *
     * 같은 테이블을 조인해야 하는 경우가 아니면 기본 인스턴스를 사용하는 것이 좋음.
     */
    @Test
    public void startQuerydslV2() {

        Member findMember = queryFactory.select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
}
