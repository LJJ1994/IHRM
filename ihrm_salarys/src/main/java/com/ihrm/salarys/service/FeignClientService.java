package com.ihrm.salarys.service;

import com.alibaba.fastjson.JSON;
import com.ihrm.common.entity.Result;
import com.ihrm.domain.atte.entity.ArchiveMonthlyInfo;
import com.ihrm.domain.social_security.ArchiveDetail;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.salarys.feign.SystemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ihrm.salarys.feign.AttendanceFeignClient;
import com.ihrm.salarys.feign.SocialSecurityFeignClient;

import java.util.List;

@Service
public class FeignClientService {

	@Autowired
	private AttendanceFeignClient attendanceFeignClient;

	@Autowired
	private SocialSecurityFeignClient socialFeignClient;

	@Autowired
	private SystemFeignClient systemFeignClient;

	//考勤
	public ArchiveMonthlyInfo getAtteInfo(String userId, String yearMonth) {
		Result result = attendanceFeignClient.historyData(userId, yearMonth);
		ArchiveMonthlyInfo info = null;
		if (result.isSuccess()) {
			info = JSON.parseObject(JSON.toJSONString(result.getData()), ArchiveMonthlyInfo.class);
		}else{
			System.out.println("请求考勤模块失败");
		}
		return info;
	}

	//社保
	public ArchiveDetail getSocialInfo(String userId, String yearMonth) {
		Result result = socialFeignClient.historyData(userId, yearMonth);
		ArchiveDetail info = null;
		if (result.isSuccess()) {
			info = JSON.parseObject(JSON.toJSONString(result.getData()), ArchiveDetail.class);
		}else{
			System.out.println("请求考勤社保失败");
		}
		return info;
	}

	//获取所有用户信息
	public List getUser(){
		Result result = systemFeignClient.findAll();
		List list = null;
		if (result.isSuccess()){
			list = JSON.parseArray(JSON.toJSONString(result.getData()), User.class);
		}else{
			System.out.println("获取用户信息失败!");
		}
		return list;
	}

	//获取单个用户信息
	public UserResult findById(String userId){
		Result result = systemFeignClient.findById(userId);
		UserResult userResult = new UserResult();
		if (result != null) {
			userResult = JSON.parseObject(JSON.toJSONString(result.getData()), UserResult.class);
			return userResult;
		}
		return null;
	}
}
