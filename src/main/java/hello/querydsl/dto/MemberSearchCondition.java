package hello.querydsl.dto;

import lombok.Data;

/**
 * 회원 검색 조건
 */
@Data
public class MemberSearchCondition {

    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
