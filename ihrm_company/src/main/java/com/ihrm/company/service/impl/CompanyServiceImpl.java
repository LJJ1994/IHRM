package com.ihrm.company.service.impl;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.company.dao.CompanyDao;
import com.ihrm.company.service.CompanyService;
import com.ihrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private IdWorker idWorker;

    @Override
    public Company add(Company company) {
        company.setId(idWorker.nextId()+"");
        company.setAuditState("0"); //待审核
        company.setState(1); // 启用
        company.setBalance(0D); //余额
        company.setCreateTime(new Date()); // 创建时间

        return companyDao.save(company);
    }

    @Override
    public Company update(Company company) {
        return companyDao.save(company);
    }

    @Override
    public Company findById(String id) {
        Optional<Company> one = companyDao.findById(id);
        return one.get();
    }

    @Override
    public void deleteById(String id) {
        companyDao.deleteById(id);
    }

    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }
}
