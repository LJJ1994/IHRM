package com.ihrm.employee;

import com.ihrm.domain.employee.response.EmployeeReportResult;
import com.ihrm.employee.dao.UserCompanyPersonalDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DAOTest {
    @Autowired
    private UserCompanyPersonalDao userCompanyPersonalDao;

    @Test
    public void testGetUserCompanyPersonal(){
        String month = "2018-03-01";
        String companyId = "1";
        List<EmployeeReportResult> list = userCompanyPersonalDao.findByReport(companyId, month);
        for (EmployeeReportResult result : list) {
            System.out.println(result.getUsername());
        }
    }
}
