package com.ihrm.company.service;

import com.ihrm.domain.company.Company;

import java.util.List;

public interface CompanyService {
    Company add(Company company);
    Company update(Company company);
    Company findById(String id);
    void deleteById(String id);
    List<Company> findAll();
}
