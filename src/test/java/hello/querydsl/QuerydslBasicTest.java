package hello.querydsl;

import com.querydsl.core.QueryResults;
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

import java.util.List;

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

        Member findMember = queryFactory
                                .select(member)
                                .from(member)
                                .where(member.username.eq("member1"))
                                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 기본 검색 쿼리
     *
     * 검색 조건은 .and(), .or() 를 메서드 체인으로 연결 가능
     *
     * selectFrom = select + from
     */
    @Test
    public void search() {

        Member findMember = queryFactory
                            .selectFrom(member)
                            .where(member.username.eq("member1")
                                    .and(member.age.eq(10)))
                            .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

        // age between 10, 30
        Member findMember2 = queryFactory
                                .selectFrom(member)
                                .where(member.username.eq("member1")
                                    .and(member.age.between(10, 30)))
                                .fetchOne();

        assertThat(findMember2.getUsername()).isEqualTo("member1");
    }

    /**
     * AND 조건을 파라미터로 처리
     *
     * where() 에 파라미터로 검색 조건을 추가하면 AND 조건이 추가된다.
     */
    @Test
    public void searchAndParam() {

        Member findMember = queryFactory
                                .selectFrom(member)
                                .where(
                                    member.username.eq("member1"),
                                    member.age.eq(10)
                                )
                                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 결과 조회
     */
    @Test
    public void resultFetch() {

        // 리스트 조회
        // 데이터 없으면 빈 리스트 반환
        List<Member> fetch = queryFactory
                                .selectFrom(member)
                                .fetch();

        // 단 건 조회
        // 결과가 없으면 null
        // 결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
        Member fetchOne = queryFactory
                                .selectFrom(member)
                                .fetchOne();

        // 첫 번째 한 건 조회
        Member fetchFirst = queryFactory
                                .selectFrom(member)
                                .fetchFirst(); // .limit(1).fetchOne(); 와 같은 의미

        // 페이징에서 사용
        // 페이징 정보 포함, total count 쿼리 추가 실행
        QueryResults<Member> fetchResults = queryFactory
                                                .selectFrom(member)
                                                .fetchResults();

//        fetchResults.getTotal();
//        List<Member> content = fetchResults.getResults();

        // count 쿼리로 변경해서 count 수 조회
        long count = queryFactory
                        .selectFrom(member)
                        .fetchCount();

    }

    /**
     * 정렬
     *
     * desc(), asc() : 일반 정렬
     * nullsLast(), nullsFirst() : null 데이터 순서 부여
     */
    @Test
    public void sort() {

        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                                    .selectFrom(member)
                                    .where(member.age.eq(100))
                                    .orderBy(member.age.desc(), member.username.asc().nullsLast())
                                    .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }
}
