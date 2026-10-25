package com.security.SpringSecurityApplication.services;

import com.security.SpringSecurityApplication.entities.Employees;
import com.security.SpringSecurityApplication.enums.Department;
import com.security.SpringSecurityApplication.repositories.EmployeeRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employees> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employees getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Employee not found with Id: " + id
        ));
    }

    public Employees addEmployee(Employees emp) {
       return employeeRepository.save(emp);
    }

    public Employees updateEmployee(Employees emp) {
        Employees employee = employeeRepository.findById(emp.getId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Employee not found with Id: " + emp.getId()
        ));

        employee.setAge(emp.getAge());
        employee.setName(emp.getName());
        employee.setDept(emp.getDept());
        employee.setSalary(emp.getSalary());
        employee.setTitle(emp.getTitle());

        return employeeRepository.save(employee);
    }

    public Employees patchEmployee(Long id, Map<String, Object> updates) {

        Employees employee = employeeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Employee not found with Id: " + id
        ));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> employee.setName((String) value);
                case "age" -> employee.setAge((Integer) value);
                case "salary" -> employee.setSalary((Double) value);
                case "dept" -> employee.setDept((Department) value);
                case "title" -> employee.setTitle((String) value);
                default -> throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Invalid field: " + key
                    );
            }
        });

        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        Employees employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found with Id: " + id
                ));
        employeeRepository.deleteById(id);
    }
}
