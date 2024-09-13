package org.zerock.myapp.association;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.zerock.myapp.domain.Member;
import org.zerock.myapp.domain.Team;
import org.zerock.myapp.util.PersistenceUnits;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DoAssociationMappingTests {
	private EntityManagerFactory emf;
	private EntityManager em;

	@BeforeAll
	void beforeAll() {
		log.trace("beforeAll() invoked.");
		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.H2);

		assertNotNull(emf);

		this.em = this.emf.createEntityManager();
		assert this.em != null;
	} // beforeAll

	@AfterAll
	void afterAll() {
		log.trace("afterAll() invoked.");

		this.em.clear();

		try {
			this.em.close();
		} catch (Exception _ignored) {
		}
		try {
			this.emf.close();
		} catch (Exception _ignored) {
		}
	} // afterAll

//	@Disabled
	@Order(1)
	@Test
//	@RepeatedTest(1)
	@DisplayName("1. prepareData")
	@Timeout(value = 5L, unit = TimeUnit.MINUTES)
	void prepareData() {
		log.trace("prepareData() invoked.");

		try {
			this.em.getTransaction().begin();

			LongStream.of(1L, 2L, 3L).forEachOrdered(seq -> {
				Team transientTeam = new Team();

				transientTeam.setName("TEAM_" + seq);

				this.em.persist(transientTeam);
			}); // .forEachOrdered

			Team team1 = em.<Team>find(Team.class, 1L);
			Team team2 = em.<Team>find(Team.class, 2L);
			Team team3 = em.<Team>find(Team.class, 3L);

			Objects.requireNonNull(team1);
			assertNotNull(team2);
			assert team3 != null;

			IntStream.rangeClosed(1, 6).forEachOrdered(seq -> {
				Member transientMember = new Member();
				transientMember.setName("NAME_" + seq);
				transientMember.setTeam(team1);

				this.em.persist(transientMember);
			}); // .forEachOrdered

			IntStream.of(7, 8, 9).forEachOrdered(seq -> {
				Member transientMember = new Member();

				transientMember.setName("NAME_" + seq);

				if (seq != 9) {
					transientMember.setTeam(team3);
				} // if

				this.em.persist(transientMember);
			}); // .forEachOrdered

			this.em.getTransaction().commit();
		} catch (Exception _original) {
			this.em.getTransaction().rollback();
			throw new IllegalStateException(_original);
		} // try-catch
	} // prepareData

//	@Disabled
	@Order(2)
	@Test
//	@RepeatedTest(1)
	@DisplayName("2. testFindBelongedToTheTeam")
	@Timeout(value = 5L, unit = TimeUnit.MINUTES)
	void testFindBelongedToTheTeam() {
		log.trace("testFindBelongedToTheTeam() invoked.");

		// For the specified member
		final Long id = 7L;

		Member foundMember7 = this.em.<Member>find(Member.class, id);

		Objects.requireNonNull(foundMember7);
		log.info("\t+ foundMember7 : {}, isContains : {}", foundMember7, this.em.contains(foundMember7));

		Team myTeam = foundMember7.getTeam();
		assertNotNull(myTeam);
		log.info("\t+ myTeam : {}", myTeam);

		// For all members
		LongStream.rangeClosed(1, 9).forEachOrdered(seq -> {
			Member foundMember = this.em.<Member>find(Member.class, seq);

			assert foundMember != null;

			log.info("\t+ team : {}", foundMember.getTeam());
		});

	} // testFindBelongedToTheTeam

//	@Disabled
	@Order(3)
	@Test
//	@RepeatedTest(1)
	@DisplayName("3. testFindAllMembersWithJPQL")
	@Timeout(value = 5L, unit = TimeUnit.MINUTES)
	void testFindAllMembersWithJPQL() {
		log.trace("testFindAllMembersWithJPQL() invoked.");

		String jpql = "SELECT m FROM Member m WHERE m.team.id = 3 ORDER BY m.id DESC";
		
		TypedQuery<Member> typedQuery = this.em.createQuery(jpql, Member.class);

		assertNotNull(typedQuery);

		log.info("\t+ typedQuey : {}", typedQuery);

		List<Member> resultList = typedQuery.getResultList();

		 resultList.forEach(obj -> {
		        if (obj instanceof Member m) {
		            log.info("\t+ member : id{}, team id{}", m.getId(), m.getTeam().getId());
		        }
		  });

	} // testFindAllMembersWithJPQL

	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Disabled
	@Order(4)
	@Test
//	@RepeatedTest(1)
	@DisplayName("4. testFindAllMembersWithNativeSQL")
	@Timeout(value = 5L, unit = TimeUnit.MINUTES)
	void testFindAllMembersWithNativeSQL() {
		log.trace("testFindAllMembersWithNativeSQL() invoked.");

		String sql = "SELECT * FROM member WHERE my_team = 1 ORDER BY member_id DESC";

		Query nativeQuery = this.em.createNativeQuery(sql, Member.class);
		log.info("\t+ nativeQuery : {}", nativeQuery);

		List resultList = nativeQuery.getResultList();

		resultList.forEach(obj -> {
			if (obj instanceof Member m) {
				log.info("\t+ member : id{}, team id{}", m.getId(), m.getTeam().getId());
			} // if
		}); // forEach

	} // testFindAllMembersWithNativeSQL
} // end class