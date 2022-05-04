package hello.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.querydsl.dto.MemberSearchCondition;
import hello.querydsl.dto.MemberTeamDto;
import hello.querydsl.dto.QMemberTeamDto;
import hello.querydsl.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static hello.querydsl.entity.QMember.member;
import static hello.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

/**
 * 리포지토리 지원 - QuerydslRepositorySupport
 *
 * 장점
 * - 스프링 데이터가 제공하는 페이징을 Querydsl 로 편리하게 변환 가능
 * - EntityManager 제공
 *
 * 단점
 * - Querydsl 3.x 버전을 대상으로 만들어 Querydsl 4.x 에 나온 JPAQueryFactory 로 시작 불가
 * - QueryFactory 를 제공하지 않음
 * - 스프링 데이터 Sort 기능이 정상 동작하지 않음
 */
//public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom {

/**
 * 사용자 정의 인터페이스 구현
 */
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {

        this.queryFactory = new JPAQueryFactory(em);
    }

//    public MemberRepositoryImpl() {
//        super(Member.class);
//    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {

//        List<MemberTeamDto> result = from(member)
//                                        .leftJoin(member.team, team)
//                                        .where(
//                                                usernameEq(condition.getUsername()),
//                                                teamNameEq(condition.getTeamName()),
//                                                ageGoe(condition.getAgeGoe()),
//                                                ageLoe(condition.getAgeLoe())
//                                        )
//                                        .select(new QMemberTeamDto(
//                                                member.id,
//                                                member.username,
//                                                member.age,
//                                                team.id,
//                                                team.name
//                                        ))
//                                        .fetch();

        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }

    /**
     * 전체 카운트를 한번에 조회하는 단순한 방법
     *
     * 단순한 페이징, fetchResults() 사용
     *
     * @param condition
     * @param pageable
     * @return
     */
    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {

//        JPQLQuery<MemberTeamDto> jpaQuery = from(member)
//                                                .leftJoin(member.team, team)
//                                                .where(
//                                                        usernameEq(condition.getUsername()),
//                                                        teamNameEq(condition.getTeamName()),
//                                                        ageGoe(condition.getAgeGoe()),
//                                                        ageLoe(condition.getAgeLoe())
//                                                )
//                                                .select(new QMemberTeamDto(
//                                                        member.id,
//                                                        member.username,
//                                                        member.age,
//                                                        team.id,
//                                                        team.name
//                                                ));
//
//        JPQLQuery<MemberTeamDto> query = getQuerydsl().applyPagination(pageable, jpaQuery);
//
//        query.fetch();

        QueryResults<MemberTeamDto> results = queryFactory
                                                .select(new QMemberTeamDto(
                                                        member.id,
                                                        member.username,
                                                        member.age,
                                                        team.id,
                                                        team.name
                                                ))
                                                .from(member)
                                                .leftJoin(member.team, team)
                                                .where(
                                                        usernameEq(condition.getUsername()),
                                                        teamNameEq(condition.getTeamName()),
                                                        ageGoe(condition.getAgeGoe()),
                                                        ageLoe(condition.getAgeLoe())
                                                )
                                                .offset(pageable.getOffset())
                                                .limit(pageable.getPageSize())
                                                .fetchResults();

        List<MemberTeamDto> content = results.getResults();

        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 데이터 내용과 전체 카운트를 별도로 조회하는 방법
     *
     * 데이터 조회 쿼리와, 전체 카운트 쿼리를 분리
     *
     * @param condition
     * @param pageable
     * @return
     */
    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {

        List<MemberTeamDto> content = queryFactory
                                        .select(new QMemberTeamDto(
                                                member.id,
                                                member.username,
                                                member.age,
                                                team.id,
                                                team.name
                                        ))
                                        .from(member)
                                        .leftJoin(member.team, team)
                                        .where(
                                                usernameEq(condition.getUsername()),
                                                teamNameEq(condition.getTeamName()),
                                                ageGoe(condition.getAgeGoe()),
                                                ageLoe(condition.getAgeLoe())
                                        )
                                        .offset(pageable.getOffset())
                                        .limit(pageable.getPageSize())
                                        .fetch();

        // PageableExecutionUtils.getPage() 로 최적화
        JPAQuery<Member> countQuery = queryFactory
                                        .select(member)
                                        .from(member)
                                        .leftJoin(member.team, team)
                                        .where(
                                                usernameEq(condition.getUsername()),
                                                teamNameEq(condition.getTeamName()),
                                                ageGoe(condition.getAgeGoe()),
                                                ageLoe(condition.getAgeLoe())
                                        );

//        return new PageImpl<>(content, pageable, total);
//        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchCount());
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    private BooleanExpression usernameEq(String username) {

        return hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {

        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {

        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {

        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    /**
     * 스프링 데이터 Sort 를 Querydsl 의 OrderSpecifier 로 변환
     *
     * 루트 엔티티 범위를 넘어가는 동적 정렬 기능이 필요하면
     * 스프링 데이터 페이징이 제공하는 Sort 를 사용하기 보다는
     * 파라미터를 받아서 직접 처리하는 것을 권장
     *
     * @param condition
     * @param pageable
     * @return
     */
    public List<Member> sort_orderSpecifier(MemberSearchCondition condition, Pageable pageable) {

        JPAQuery<Member> query = queryFactory.selectFrom(member);

        for (Sort.Order o : pageable.getSort()) {

            PathBuilder pathBuilder = new PathBuilder(member.getType(), member.getMetadata());

            query.orderBy(
                    new OrderSpecifier(
                            o.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(o.getProperty())
                    )
            );
        }

        return query.fetch();
    }
}
