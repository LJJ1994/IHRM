package com.ihrm.salarys.service;

import com.ihrm.common.entity.PageResult;
import com.ihrm.common.utils.DateUtil;
import com.ihrm.domain.atte.entity.ArchiveMonthlyInfo;
import com.ihrm.domain.salarys.Settings;
import com.ihrm.domain.salarys.UserSalary;
import com.ihrm.domain.salarys.UserSalaryChange;
import com.ihrm.domain.salarys.vo.TipsVo;
import com.ihrm.domain.salarys.vo.UserSalaryDetailVo;
import com.ihrm.domain.social_security.ArchiveDetail;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.salarys.dao.SettingsDao;
import com.ihrm.salarys.dao.UserSalaryChangeDao;
import com.ihrm.salarys.dao.UserSalaryDao;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SalaryService {
	
    @Autowired
    private UserSalaryDao userSalaryDao;

    @Autowired
    private UserSalaryChangeDao userSalaryChangeDao;

    @Autowired
    private SettingsDao settingsDao;

    @Autowired
    private FeignClientService feignClientService;

    //定薪或者调薪
    public void saveUserSalary(UserSalary userSalary) {
        userSalaryDao.save(userSalary);
    }

	//查询用户薪资
    public UserSalary findUserSalary(String userId) {
        Optional<UserSalary> optional = userSalaryDao.findById(userId);
        return optional.isPresent() ? optional.get() : null;
    }

	//分页查询当月薪资列表
	public PageResult findAll(Integer page, Integer pageSize, String companyId) {
		Page page1 = userSalaryDao.findPage(companyId, new PageRequest(page - 1, pageSize));
		page1.getContent();
		return new PageResult(page1.getTotalElements(),page1.getContent());
	}

    /**
     * 获取列表页tip 提示信息
     * @param yearMonth 201809
     * @return
     */
	public TipsVo getTips(String yearMonth) {
        List<User> users = feignClientService.getUser();

        TipsVo tipsVo = new TipsVo();
        int worksCount = 0; //入职
        int leavesCount = 0; //离职
        int adjustCount = 0; //调薪
        int unGradingCount = 0; //需定薪
        for (User user : users) {
            //判断入职时间是否为本月 且为在职状态
            Date timeOfEntry = user.getTimeOfEntry();
            if (yearMonth.equals(DateUtil.parseDate2String(timeOfEntry, "yyyyMM"))
                    && ("1".equals(String.valueOf(user.getInServiceStatus())))) {
                worksCount += 1;
            }
            //判断离职时间是否为本月, 且为离职状态
            Date timeOfDimission = user.getTimeOfDimission();
            if (yearMonth.equals(DateUtil.parseDate2String(timeOfDimission, "yyyyMM"))
                    && "2".equals(String.valueOf(user.getInServiceStatus()))) {
                leavesCount += 1;
            }
            //计算本月调薪人数
            UserSalaryChange userSalaryChange = userSalaryChangeDao.findByUserId(user.getId());
            if (userSalaryChange != null){
                if (userSalaryChange.getEffectiveTimeOfPayAdjustment() != null
                        && yearMonth.equals(DateUtil.parseDate2String(userSalaryChange.getEffectiveTimeOfPayAdjustment(), "yyyyMM"))){
                    adjustCount += 1;
                }
            }

            //计算本月需要定薪的人数
            Optional<UserSalary> optional = userSalaryDao.findById(user.getId());
            if (optional.isPresent()){
                UserSalary userSalary = optional.get();
                if (userSalary.getFixedBasicSalary() == null && userSalary.getFixedPostWage() == null){
                    unGradingCount += 1;
                }
            }
        }
        tipsVo.setDateRange(yearMonth);
        tipsVo.setWorksCount(worksCount);
        tipsVo.setLeavesCount(leavesCount);
        tipsVo.setAdjustCount(adjustCount);
        tipsVo.setUnGradingCount(unGradingCount);

        return tipsVo;
	}

    /**
     * 获取薪资模块用户详情信息
     * @param userId
     * @param yearMonth
     * @return
     */
	public UserSalaryDetailVo getUserSalaryDetail(String userId, String yearMonth, String companyId){
        UserSalaryDetailVo userSalaryDetailVo = new UserSalaryDetailVo();
	    //用户信息
        UserResult userResult = feignClientService.findById(userId);
        if (userResult != null){
            userSalaryDetailVo.setUserId(userResult.getId());
            userSalaryDetailVo.setUsername(userResult.getUsername());
            userSalaryDetailVo.setTimeOfEntry(userResult.getTimeOfEntry());
        }
        //薪资信息
        Optional<UserSalary> userSalaryOptional = userSalaryDao.findById(userId);
        if (userSalaryOptional.isPresent()){
            UserSalary one = userSalaryOptional.get();
            userSalaryDetailVo.setLatestSalaryBase(one.getCurrentBasicSalary());
            userSalaryDetailVo.setBasicWageBaseForThatMonth(one.getCurrentPostWage());
            userSalaryDetailVo.setBasicWageBaseForTheLatestMonth(one.getCurrentPostWage());
            userSalaryDetailVo.setSalaryBaseForTheMonth(one.getCurrentBasicSalary());
            userSalaryDetailVo.setSalaryBaseForTheLatestMonth(one.getCurrentBasicSalary());
        }

        // 补贴类型:
        // 1.每出勤日 金额*实际出勤天数
        // 2.每计薪日 金额*计薪天数
        // 3.每月固定 固定金额
        Optional<Settings> settingsOptional = settingsDao.findById(companyId);
        BigDecimal transportationSubsidyAmount;
        BigDecimal communicationSubsidyAmount;
        BigDecimal lunchAllowanceAmount;
        BigDecimal housingSubsidyAmount;

        //获取每月考勤归档信息
        ArchiveMonthlyInfo atteInfo = feignClientService.getAtteInfo(userId, yearMonth);
        //实际出勤天数
        String actualAtteOfficialDays = atteInfo.getActualAtteOfficialDays();
        BigDecimal actualAtteOfficialDays1 = BigDecimal.valueOf(Double.parseDouble(actualAtteOfficialDays));
        //计薪天数
        String salaryOfficialDays = atteInfo.getSalaryOfficialDays();
        BigDecimal salaryOfficialDays1 = BigDecimal.valueOf(Double.parseDouble(salaryOfficialDays));

        userSalaryDetailVo.setActualAttendanceDaysAreOfficial(atteInfo.getActualAtteOfficialDays());
        userSalaryDetailVo.setOfficialSalaryDays(salaryOfficialDays);
        userSalaryDetailVo.setSalaryStandard(atteInfo.getSalaryStandards());

        if (settingsOptional.isPresent()){
            Settings settings = settingsOptional.get();
            userSalaryDetailVo.setTaxCountingMethod(settings.getTaxCalculationType());//计税方式
            //1.判断交通补贴类型
            Integer transportationSubsidyScheme = settings.getTransportationSubsidyScheme();
            if ("1".equals(String.valueOf(transportationSubsidyScheme))){
                //1.1每出勤日
                transportationSubsidyAmount = settings.getTransportationSubsidyAmount().multiply(actualAtteOfficialDays1);
            }else if ("2".equals(String.valueOf(transportationSubsidyScheme))){
                //1.2 每计薪日
               transportationSubsidyAmount =  settings.getTransportationSubsidyAmount().multiply(salaryOfficialDays1);
            }else{
                //1.3 固定金额
                transportationSubsidyAmount = settings.getTransportationSubsidyAmount();
            }
            userSalaryDetailVo.setTransportationSubsidyAmount(transportationSubsidyAmount);

            //2.判断通讯补贴
            Integer communicationSubsidyScheme = settings.getCommunicationSubsidyScheme();
            if ("1".equals(String.valueOf(communicationSubsidyScheme))){
                communicationSubsidyAmount = settings.getCommunicationSubsidyAmount().multiply(actualAtteOfficialDays1);
            } else if ("2".equals(String.valueOf(communicationSubsidyScheme))) {
                communicationSubsidyAmount = settings.getCommunicationSubsidyAmount().multiply(salaryOfficialDays1);
            } else {
                communicationSubsidyAmount = settings.getCommunicationSubsidyAmount();
            }
            userSalaryDetailVo.setCommunicationSubsidyAmount(communicationSubsidyAmount);

            //3.判断午餐补贴
            Integer lunchAllowanceScheme = settings.getLunchAllowanceScheme();
            if ("1".equals(String.valueOf(lunchAllowanceScheme))) {
                lunchAllowanceAmount = settings.getLunchAllowanceAmount().multiply(actualAtteOfficialDays1);
            } else if ("2".equals(String.valueOf(lunchAllowanceScheme))) {
                lunchAllowanceAmount = settings.getLunchAllowanceAmount().multiply(salaryOfficialDays1);
            } else {
                lunchAllowanceAmount = settings.getLunchAllowanceAmount();
            }
            userSalaryDetailVo.setLunchAllowanceAmount(lunchAllowanceAmount);

            //4.判断住房补贴
            Integer housingSubsidyScheme = settings.getHousingSubsidyScheme();
            if ("1".equals(String.valueOf(housingSubsidyScheme))) {
                housingSubsidyAmount = settings.getHousingSubsidyAmount().multiply(actualAtteOfficialDays1);
            } else if ("2".equals(String.valueOf(housingSubsidyScheme))) {
                housingSubsidyAmount = settings.getHousingSubsidyAmount().multiply(salaryOfficialDays1);
            } else {
                housingSubsidyAmount = settings.getHousingSubsidyAmount();
            }
            userSalaryDetailVo.setHousingSubsidyAmount(housingSubsidyAmount);
        }

        //社保公积金设置
        ArchiveDetail socialInfo = feignClientService.getSocialInfo(userId, yearMonth);
        userSalaryDetailVo.setSocialSecurityBase(socialInfo.getSocialSecurityBase());
        userSalaryDetailVo.setProvidentFundBase(socialInfo.getProvidentFundBase());
        userSalaryDetailVo.setSocialSecurityCompanyBase(socialInfo.getSocialSecurityEnterprise());
        userSalaryDetailVo.setSocialSecurityPersonalBase(socialInfo.getSocialSecurityIndividual());
        userSalaryDetailVo.setProvidentFundCompanyBase(socialInfo.getProvidentFundEnterprises());
        userSalaryDetailVo.setProvidentFundPersonalBase(socialInfo.getProvidentFundIndividual());

        return userSalaryDetailVo;
    }

    /**
     *
     * @param type 补贴类型
     * @param money 金额
     * @param actualAttendanceDaysAreOfficial 实际出勤人数
     * @return
     */
    private BigDecimal handleType(int type, BigDecimal money, BigDecimal fixedMoney, int actualAttendanceDaysAreOfficial, int officialSalaryDays){
        switch (type) {
            case 1:
                BigDecimal actualDays = BigDecimal.valueOf(Double.valueOf(actualAttendanceDaysAreOfficial));
                return actualDays.multiply(money);
            case 2:
                BigDecimal officialDays = BigDecimal.valueOf(Double.valueOf(officialSalaryDays));
                return money.multiply(officialDays);
            case 3:
                return fixedMoney;
            default:
                return new BigDecimal(0L);
        }
    }
}
