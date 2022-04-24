package hello.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class QuerydslApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuerydslApplication.class, args);
	}

	/**
	 * JPAQueryFactory 스프링 빈 등록
	 *
	 * JPAQueryFactory 를 스프링 빈으로 등록해서 주입받아 사용 가능
	 *
	 * @param em
	 * @return
	 */
//	@Bean
//	JPAQueryFactory jpaQueryFactory(EntityManager em) {
//
//		return new JPAQueryFactory(em);
//	}
}
