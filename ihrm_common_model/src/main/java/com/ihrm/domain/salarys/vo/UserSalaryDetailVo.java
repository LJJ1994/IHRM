package com.ihrm.domain.salarys.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserSalaryDetailVo {
    //用户id
    private String userId;
    //用户名
    private String username;
    //入职时间
    private Date timeOfEntry;
    //最新工资基数
    private BigDecimal latestSalaryBase;
    //当月最新工资基数
    private BigDecimal basicWageBaseForTheLatestMonth;
    //当月工资基数
    private BigDecimal basicWageBaseForThatMonth;
    //最新当月基本工资
    private BigDecimal salaryBaseForTheLatestMonth;
    //当月基本工资
    private BigDecimal salaryBaseForTheMonth;


    //交通补贴
    private BigDecimal transportationSubsidyAmount;
    //通讯补贴
    private BigDecimal communicationSubsidyAmount;
    //午餐补贴
    private BigDecimal lunchAllowanceAmount;
    //住房补贴
    private BigDecimal housingSubsidyAmount;


    //社保基数
    private BigDecimal socialSecurityBase;
    //公积金基数
    private BigDecimal providentFundBase;
    //企业缴纳社保
    private BigDecimal socialSecurityCompanyBase;
    //个人缴纳社保
    private BigDecimal socialSecurityPersonalBase;
    //企业缴纳公积金
    private BigDecimal providentFundCompanyBase;
    //个人缴纳公积金
    private BigDecimal providentFundPersonalBase;


    //实际出勤天数（正式）
    private String actualAttendanceDaysAreOfficial;
    //计薪天数（正式）
    private String officialSalaryDays;
    //计薪标准
    private String salaryStandard;
    //计税方式
    private int taxCountingMethod;
}
