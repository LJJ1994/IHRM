package com.ihrm.system.client;

import com.ihrm.common.entity.Result;
import com.ihrm.domain.company.Department;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ihrm-company")
public interface CompanyFeignClient {
    /**
     * 调用微服务的接口
     */
    @RequestMapping(value = "/company/departments/search" , method = RequestMethod.POST)
    Department findByCode(@RequestParam("code") String code, @RequestParam("companyId") String companyId);

    @RequestMapping(value = "/company/{id}" , method = RequestMethod.GET)
    Result findCompanyById(@PathVariable(value="id") String id);
}
