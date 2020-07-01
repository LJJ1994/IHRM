package com.ihrm.company.service.impl;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.company.dao.DepartmentDao;
import com.ihrm.company.service.DepartmentService;
import com.ihrm.domain.company.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DepartmentServiceImpl extends BaseService implements DepartmentService{
    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void save(Department department) {
        department.setId(idWorker.nextId()+"");
        department.setCreateTime(new Date());
        departmentDao.save(department);
    }

    @Override
    public void update(Department department) {
        Department one = departmentDao.findById(department.getId()).get();
        one.setName(department.getName());
        one.setPid(department.getPid());
        one.setManagerId(department.getManagerId());
        one.setManager(department.getManager());
        one.setIntroduce(department.getIntroduce());
        departmentDao.save(one);
    }

    @Override
    public Department findById(String id) {
        return departmentDao.findById(id).get();
    }

    @Override
    public void delete(String id) {
        departmentDao.deleteById(id);
    }

    /**
     * 获取和某个公司有关的部门列表
     * @param companyId
     * @return
     */
    @Override
    public List<Department> findAll(String companyId) {
        return departmentDao.findAll(getSpecification(companyId));
    }

    @Override
    public Department findByCodeAndCompanyId(String code, String companyid) {
        return departmentDao.findByCodeAndCompanyId(code, companyid);
    }
}
