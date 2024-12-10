package com.devcors.hibernate_demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.devcors.hibernate_demo.entity.Classroom;
import com.devcors.hibernate_demo.repository.ClassroomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
class HibernateDemoApplicationTests {

	@Autowired
	ClassroomRepository classroomRepository;

	@Autowired
	EntityManagerFactory entityManagerFactory;

	@Autowired
	EntityManager springEntityManager;

	@BeforeEach
	void setup() {
		classroomRepository.deleteAll();
	}

	@Test
	//@Transactional - try with or without
	void test001PersistenceSpringData() {

		Classroom classroom = Classroom.builder()
				.name("First Classroom")
				.capacity(30)
				.build();
		Classroom modifiedClassroom;

		classroom = classroomRepository.save(classroom);

		classroom.setName("Modified First Classroom");
		classroom.setCapacity(20);

		modifiedClassroom = classroomRepository.findById(classroom.getId()).get();

		log.debug(classroomRepository.findAll().toString());
		log.debug(classroom.toString());
		log.debug(modifiedClassroom.toString());

    assertEquals("Modified First Classroom", classroom.getName());

		assertEquals(classroom, modifiedClassroom);

		assertEquals("Modified First Classroom", modifiedClassroom.getName());
	}

	@Test
	void test002PersistenceEntityManager() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Classroom classroom = Classroom.builder()
				.name("Second Classroom")
				.capacity(30)
				.build();
		Classroom modifiedClassroom;

		entityManager.persist(classroom);
		assertTrue(entityManager.contains(classroom));

		classroom.setName("Modified Second Classroom");
		classroom.setCapacity(20);

		// entityManager.getTransaction().begin();  // Try with or without this
		// entityManager.getTransaction().commit(); // Try with or without this

		modifiedClassroom = classroomRepository.findById(classroom.getId()).get();

		assertEquals("Modified Second Classroom", classroom.getName());

		assertEquals(classroom, modifiedClassroom);

		assertEquals("Modified Second Classroom", modifiedClassroom.getName());
	}

	void createClassroom() {
		Classroom classroom = Classroom.builder()
				.name("NEW Classroom")
				.capacity(30)
				.build();
		classroomRepository.save(classroom);
		classroom.setName("Modified Classroom");
	}

	@Test
	void test003Persistence() {
		createClassroom();
		Classroom classroom = classroomRepository.findAll().getFirst();
		assertEquals("NEW Classroom", classroom.getName());
	}

	@Test
	@Transactional
	void test004PersistenceTransactional() {
		createClassroom();
		Classroom classroom = classroomRepository.findAll().getFirst();
		assertEquals("Modified Classroom", classroom.getName());
	}

	Long createClassroomId() {
		Classroom classroom = Classroom.builder()
				.name("NEW Classroom")
				.capacity(30)
				.build();
		classroomRepository.save(classroom);
		return classroom.getId();
	}

	@Test
	@Transactional
	void test005Delete() {
		Long classroomId = createClassroomId();

		Classroom classroom = springEntityManager.find(Classroom.class, classroomId);
		assertNotNull(classroom);

		springEntityManager.remove(classroom);

		assertFalse(springEntityManager.contains(classroom));

		Classroom deletedClassroom = springEntityManager.find(Classroom.class, classroomId);

		assertNull(deletedClassroom);
	}


	@Test
	@Transactional
	void test006BonusTryToFixThis() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Classroom classroom = Classroom.builder()
				.name("Third Classroom")
				.capacity(30)
				.build();

		classroomRepository.save(classroom);

		assertTrue(springEntityManager.contains(classroom));

		Classroom detachedClassroom = Classroom.builder()
				.id(classroom.getId())
				.name("Modified Third Classroom")
				.capacity(40).build();

		assertTrue(springEntityManager.contains(classroom));
		assertFalse(springEntityManager.contains(detachedClassroom));

		springEntityManager.getTransaction().begin();
		entityManager.merge(detachedClassroom);
		entityManager.persist(detachedClassroom);
		classroom.setName("Modified Third Classroom");
		classroom.setCapacity(50);

		springEntityManager.getTransaction().commit();
		log.debug(classroomRepository.findAll().toString());

	}
}
