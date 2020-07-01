package com.ihrm.employee.dao;

import com.ihrm.domain.employee.UserCompanyPersonal;
import com.ihrm.domain.employee.response.EmployeeReportResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * 数据访问接口
 */
public interface UserCompanyPersonalDao extends JpaRepository<UserCompanyPersonal, String>, JpaSpecificationExecutor<UserCompanyPersonal> {

    UserCompanyPersonal findByUserId(String userId);

    @Query(value = "select new com.ihrm.domain.employee.response.EmployeeReportResult(a,b) " + "FROM UserCompanyPersonal a LEFT JOIN EmployeeResignation b ON a.userId=b.userId WHERE a.companyId = ?1 AND a.timeOfEntry LIKE ?2 OR (b.resignationTime LIKE ?2)")
    List<EmployeeReportResult> findByReport(String companyId, String month);
}