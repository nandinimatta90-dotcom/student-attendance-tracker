package com.attendance.tracker.repository;

import com.attendance.tracker.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByRollNumberIgnoreCase(String rollNumber);

    boolean existsByRollNumberIgnoreCaseAndIdNot(String rollNumber, Long id);

    Optional<Student> findByRollNumberIgnoreCase(String rollNumber);

    List<Student> findAllByOrderByRollNumberAsc();
}
