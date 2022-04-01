package hello.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.querydsl.entity.Hello;
import hello.querydsl.entity.QHello;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Querydsl Q 타입, lombok 정상 동작 확인 테스트
 */
@SpringBootTest
@Transactional
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {

		Hello hello = new Hello();

		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);

		// Querydsl Q 타입 동작 확인
		QHello qHello = QHello.hello;

		Hello result = query.selectFrom(qHello)
							.fetchOne();

		assertThat(result).isEqualTo(hello);

		// lombok 동작 확인 (hello.getId())
		assertThat(result.getId()).isEqualTo(hello.getId());
	}
}
