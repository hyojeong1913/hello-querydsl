package hello.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.querydsl.entity.Member;
import hello.querydsl.entity.QMember;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static hello.querydsl.entity.QMember.member;

/**
 * 순수 JPA 리포지토리
 */
@Repository
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    /**
     * JPAQueryFactory 스프링 빈 등록 시 사용
     * @param em
     * @param queryFactory
     */
//    public MemberJpaRepository(EntityManager em, JPAQueryFactory queryFactory) {
//        this.em = em;
//        this.queryFactory = queryFactory;
//    }

    public MemberJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public void save(Member member) {

        em.persist(member);
    }

    public Optional<Member> findById(Long id) {

        Member findMember = em.find(Member.class, id);

        return Optional.ofNullable(findMember);
    }

    public List<Member> findAll() {

        return em.createQuery(
                "SELECT m FROM Member m",
                        Member.class
        ).getResultList();
    }

    /**
     * Querydsl 사용
     *
     * @return
     */
    public List<Member> findAll_Querydsl() {

        return queryFactory
                .selectFrom(member)
                .fetch();
    }

    public List<Member> findByUsername(String username) {

        return em
                .createQuery(
                "SELECT m FROM Member m WHERE m.username = :username",
                        Member.class
                )
                .setParameter("username", username)
                .getResultList();
    }

    /**
     * Querydsl 사용
     *
     * @param username
     * @return
     */
    public List<Member> findByUsername_Querydsl(String username) {

        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }
}
