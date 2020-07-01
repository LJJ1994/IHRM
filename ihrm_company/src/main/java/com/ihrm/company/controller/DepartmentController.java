package com.ihrm.company.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.company.service.CompanyService;
import com.ihrm.company.service.DepartmentService;
import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.reponse.DeptListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/company")
public class DepartmentController extends BaseController {
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CompanyService companyService;

    @PostMapping("/departments")
    public Result save(@RequestBody Department department){
        department.setCompanyId(parseCompanyId());
        departmentService.save(department);
        return new Result(ResultCode.SUCCESS);
    }

    @PutMapping("/departments/{id}")
    public Result update(@RequestBody Department department,
                         @PathVariable String id){
        department.setId(id);
        departmentService.update(department);
        return new Result(ResultCode.SUCCESS);
    }

    @DeleteMapping("/departments/{id}")
    public Result delete(@PathVariable("id") String id){
        departmentService.delete(id);
        return new Result(ResultCode.SUCCESS);
    }

    @GetMapping("/departments/{id}")
    public Result findById(@PathVariable("id") String id){
        Department department = departmentService.findById(id);
        Result<Department> result = new Result<>(ResultCode.SUCCESS);
        result.setData(department);
        return result;
    }

    /**
     * 查询指定公司下的部门列表
     * @return
     */
    @GetMapping("/departments")
    public Result findAll(){
        Company company = companyService.findById(parseCompanyId());
        List<Department> list = departmentService.findAll(parseCompanyId());
        DeptListResult deptListResult = new DeptListResult(company, list);
        return new Result<>(ResultCode.SUCCESS, deptListResult);
    }

    /**
     *  根据部门Id和公司id查询部门
     */
    @RequestMapping(value = "/departments/search" , method = RequestMethod.POST)
    public Department findByCode(@RequestParam("code") String code,@RequestParam("companyId") String companyId){
        return departmentService.findByCodeAndCompanyId(code , companyId);
    }
}
