package com.ihrm.domain.social_security;

import com.ihrm.domain.poi.ExcelAttribute;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "ss_user_social_security")
@Setter
@Getter
@NoArgsConstructor
//@AllArgsConstructor
public class UserSocialSecurity implements Serializable {
    private static final long serialVersionUID = 9039032874868025548L;
    //ID
    @Id
    @ExcelAttribute(sort = 0)
    private String userId;

    /**
     * 本月是否缴纳社保 0为不缴纳 1为缴纳
     */
    @ExcelAttribute(sort = 1)
    private Integer enterprisesPaySocialSecurityThisMonth;
    /**
     * 本月是否缴纳公积金 0为不缴纳 1为缴纳
     */
    @ExcelAttribute(sort = 2)
    private Integer enterprisesPayTheProvidentFundThisMonth;
    /**
     * 参保城市id
     */
    @ExcelAttribute(sort = 3)
    private String participatingInTheCityId;

    /**
     * 参保类型  1为首次开户 2为非首次开户
     */
    @ExcelAttribute(sort = 4)
    private Integer socialSecurityType;

    /**
     * 户籍类型 1为本市城镇 2为本市农村 3为外埠城镇 4为外埠农村
     */
    @ExcelAttribute(sort = 5)
    private Integer householdRegistrationType;

    /**
     * 社保基数
     */
    @ExcelAttribute(sort = 6)
    private BigDecimal socialSecurityBase;

    /**
     * 工伤比例
     */
    @ExcelAttribute(sort = 7)
    private BigDecimal industrialInjuryRatio;

    /**
     * 社保备注
     */
    @ExcelAttribute(sort = 8)
    private String socialSecurityNotes;
    /**
     * 公积金城市id
     */
    @ExcelAttribute(sort = 9)
    private String providentFundCityId;
    /**
     * 公积金基数
     */
    @ExcelAttribute(sort = 10)
    private BigDecimal providentFundBase;
    /**
     * 公积金企业比例
     */
    @ExcelAttribute(sort = 11)
    private BigDecimal enterpriseProportion;
    /**
     * 公积金个人比例
     */
    @ExcelAttribute(sort = 12)
    private BigDecimal personalProportion;
    /**
     * 公积金企业缴纳数额
     */
    @ExcelAttribute(sort = 13)
    private BigDecimal enterpriseProvidentFundPayment;
    /**
     * 公积金个人缴纳数额
     */
    @ExcelAttribute(sort = 14)
    private BigDecimal personalProvidentFundPayment;
    /**
     * 公积金备注
     */
    @ExcelAttribute(sort = 15)
    private String providentFundNotes;

    /**
     * 最后修改时间
     */
    @ExcelAttribute(sort = 16)
    private Date lastModifyTime;
    /**
     * 社保是否缴纳变更时间
     */
    @ExcelAttribute(sort = 17)
    private Date socialSecuritySwitchUpdateTime;
    /**
     * 公积金是否缴纳变更时间
     */
    @ExcelAttribute(sort = 18)
    private Date providentFundSwitchUpdateTime;
    @ExcelAttribute(sort = 19)
    private String householdRegistration;
    @ExcelAttribute(sort = 20)
	private String participatingInTheCity;
    @ExcelAttribute(sort = 21)
	private String providentFundCity;

	public UserSocialSecurity(Object[] values){
	    this.userId = (String) values[0];
	    this.enterprisesPaySocialSecurityThisMonth = Integer.valueOf((String)values[1]);
	    this.enterprisesPayTheProvidentFundThisMonth = Integer.valueOf((String)values[2]);
	    this.participatingInTheCityId = (String)values[3];
        this.socialSecurityType = Integer.valueOf((String)values[4]);
        this.householdRegistrationType = Integer.valueOf((String)values[5]);
//        this.socialSecurityBase = BigDecimal.valueOf((double)values[6]);
        this.socialSecurityBase = (BigDecimal) values[6];
        this.industrialInjuryRatio = BigDecimal.valueOf((double)values[7]);
        this.socialSecurityNotes = (String)values[8];
        this.providentFundCityId = (String) values[9];
        this.providentFundBase = BigDecimal.valueOf((double)values[10]);
        this.enterpriseProportion = BigDecimal.valueOf((double)values[11]);
        this.personalProportion = BigDecimal.valueOf((double)values[12]);
        this.enterpriseProvidentFundPayment = BigDecimal.valueOf((double)values[13]);
        this.personalProvidentFundPayment = BigDecimal.valueOf((double)values[14]);
        this.providentFundNotes = (String)values[15];
        try {
            this.lastModifyTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse((String) values[16]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            this.socialSecuritySwitchUpdateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse((String) values[17]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            this.providentFundSwitchUpdateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse((String) values[18]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.householdRegistration = (String)values[19];
        this.participatingInTheCity = (String)values[20];
        this.providentFundCity = (String)values[21];
    }
}
