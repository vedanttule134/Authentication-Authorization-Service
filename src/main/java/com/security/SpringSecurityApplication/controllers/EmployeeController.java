package com.security.SpringSecurityApplication.controllers;

import com.security.SpringSecurityApplication.entities.Employees;
import com.security.SpringSecurityApplication.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<Employees>> getAllEmployee() {
        List<Employees> list = employeeService.getAllEmployees();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employees> getEmployeeById(@PathVariable Long id) {
        Employees emp = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(emp);
    }

    @PostMapping("/add")
    public ResponseEntity<Employees> addEmployee(@RequestBody Employees emp) {
        Employees employee = employeeService.addEmployee(emp);

        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @PutMapping("/update")
    public ResponseEntity<Employees> updateEmployee(@RequestBody Employees emp) {
        Employees employee = employeeService.updateEmployee(emp);

        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<Employees> patchEmployee(@PathVariable Long id,
                                                  @RequestBody Map<String, Object> updates) {

        return ResponseEntity.status(HttpStatus.OK).body(employeeService.patchEmployee(id, updates));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee with Id: " + id + " deleted!");
    }
}
