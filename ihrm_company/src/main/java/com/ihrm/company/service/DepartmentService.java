package com.ihrm.company.service;

import com.ihrm.domain.company.Department;

import java.util.List;

public interface DepartmentService {

    void save(Department department);

    void update(Department department);

    Department findById(String id);

    void delete(String id);

    List<Department> findAll(String companyId);

    Department findByCodeAndCompanyId(String code, String companyid);
}
