package hello.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.querydsl.dto.MemberSearchCondition;
import hello.querydsl.dto.MemberTeamDto;
import hello.querydsl.dto.QMemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static hello.querydsl.entity.QMember.member;
import static hello.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

/**
 * 사용자 정의 인터페이스 구현
 */
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {

        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {

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

        long total = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
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
}
