package com.ihrm.company.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.company.service.CompanyService;
import com.ihrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/company")
public class CompanyController extends BaseController {
    @Autowired
    private CompanyService companyService;

    @GetMapping("/{id}")
    public Result findById(@PathVariable("id") String id){
        Company one = companyService.findById(parseCompanyId());
        Result<Company> result = new Result<>(ResultCode.SUCCESS);
        result.setData(one);
        return result;
    }

    @PostMapping
    public Result save(@RequestBody Company company){
        Company add = companyService.add(company);
        Result<Company> result = new Result<>(ResultCode.SUCCESS);
        result.setData(add);
        return result;
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable("id") String id,
                         @RequestBody Company company){
        company.setId(id);
        companyService.update(company);
        return new Result(ResultCode.SUCCESS);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id){
        companyService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

    @GetMapping
    public Result findAll(){
//        int i = 10/0;
        List<Company> all = companyService.findAll();
        Result<List<Company>> result = new Result<>(ResultCode.SUCCESS);
        result.setData(all);
        return result;
    }
}
