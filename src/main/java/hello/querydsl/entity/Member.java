package hello.querydsl.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Member 엔티티
 */
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) // 가급적 내부 필드만 (연관관계 없는 필드만)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {

        this(username, 0);
    }

    public Member(String username, int age) {

        this(username, age, null);
    }

    public Member(String username, int age, Team team) {

        this.username = username;
        this.age = age;

        if (team != null) {

            changeTeam(team);
        }
    }

    /**
     * 양방향 연관관계 한번에 처리 (연관관계 편의 메소드)
     *
     * @param team
     */
    public void changeTeam(Team team) {

        this.team = team;

        team.getMembers().add(this);
    }
}
