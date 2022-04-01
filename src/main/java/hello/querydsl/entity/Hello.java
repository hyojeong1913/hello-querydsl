package hello.querydsl.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Querydsl 환경설정 검증용 엔티티
 */
@Entity
@Getter @Setter
public class Hello {

    @Id @GeneratedValue
    private Long id;
}
