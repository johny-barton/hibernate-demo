package com.devcors.hibernate_demo.repository;

import com.devcors.hibernate_demo.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

}
