package hello.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

/**
 * 조회 최적화용 DTO
 */
@Data
public class MemberTeamDto {

    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    /**
     * @QueryProjection 어노테이션
     * : 해당 DTO 가 Querydsl 을 의존하게 된다.
     *
     * QMemberTeamDto 를 생성하기 위해 ./gradlew compileQuerydsl 을 한번 실행 필요
     *
     * @param memberId
     * @param username
     * @param age
     * @param teamId
     * @param teamName
     */
    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
