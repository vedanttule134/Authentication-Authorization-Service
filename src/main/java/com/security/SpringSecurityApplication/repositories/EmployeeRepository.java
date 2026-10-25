package com.security.SpringSecurityApplication.repositories;

import com.security.SpringSecurityApplication.entities.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employees, Long> {
}
