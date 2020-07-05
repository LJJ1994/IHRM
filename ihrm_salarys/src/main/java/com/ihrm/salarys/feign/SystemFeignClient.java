package com.ihrm.salarys.feign;

import com.ihrm.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ihrm-system")
public interface SystemFeignClient {
    @RequestMapping(value = "/sys/user/list", method = RequestMethod.GET)
    Result findAll();

    @RequestMapping(value = "/sys/user/{userId}", method = RequestMethod.GET)
    Result findById(@PathVariable("userId") String userId);
}
