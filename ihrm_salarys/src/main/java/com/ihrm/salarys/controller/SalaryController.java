package com.ihrm.salarys.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.salarys.UserSalary;
import com.ihrm.domain.salarys.vo.TipsVo;
import com.ihrm.domain.salarys.vo.UserSalaryDetailVo;
import com.ihrm.salarys.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/salarys")
public class SalaryController extends BaseController {

	@Autowired
	private SalaryService salaryService;

	//查询用户薪资
	@RequestMapping(value = "/modify/{userId}", method = RequestMethod.GET)
	public Result modifyGet(@PathVariable(value = "userId") String userId) throws Exception {
		UserSalary userSalary = salaryService.findUserSalary(userId);
		return new Result(ResultCode.SUCCESS, userSalary);
	}

	//查询用户薪资明细
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public Result getDetail(@PathVariable("userId") String userId,
							@RequestParam("yearMonth") String yearMonth){
		UserSalaryDetailVo userSalaryDetail = salaryService.getUserSalaryDetail(userId, yearMonth, companyId);
		return new Result<>(ResultCode.SUCCESS, userSalaryDetail);
	}

	//调薪
	@RequestMapping(value = "/modify/{userId}", method = RequestMethod.POST)
	public Result modify(@RequestBody UserSalary userSalary,
						 @PathVariable("userId") String userId) throws Exception {
		salaryService.saveUserSalary(userSalary);
		return new Result(ResultCode.SUCCESS);
	}

	//定薪
	@RequestMapping(value = "/init/{userId}", method = RequestMethod.POST)
	public Result init(@RequestBody UserSalary userSalary) {
		userSalary.setFixedBasicSalary(userSalary.getCurrentBasicSalary());
		userSalary.setFixedPostWage(userSalary.getCurrentPostWage());
		salaryService.saveUserSalary(userSalary);
		return new Result(ResultCode.SUCCESS);
	}

	//查询列表
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Result list(@RequestBody Map map) {
		//1.获取请求参数,page,size
		Integer page = (Integer)map.get("page");
		Integer pageSize = (Integer)map.get("pageSize");
		//2.调用service查询
		PageResult pr = salaryService.findAll(page, pageSize, companyId);
		return new Result(ResultCode.SUCCESS, pr);
	}

	/**
	 * 获取本月入职，离职，调薪，未调薪的计数
	 * @param yearMonth
	 * @return
	 */
	@RequestMapping(value = "/tips/{yearMonth}", method = RequestMethod.GET)
	public Result getTip(@PathVariable("yearMonth") String yearMonth){
		TipsVo tips = salaryService.getTips(yearMonth);
		return new Result<>(ResultCode.SUCCESS, tips);
	}
}
