package com.ihrm.domain.social_security.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserSocialSecurityDTO {
    private String id;
    private String username;
    private String mobile;
    private String workNumber;
    private String departmentName;
    private Date timeOfEntry;
    private Date leaveTime;
    private String participatingInTheCity;
    private String participatingInTheCityId;
    private String providentFundCityId;
    private String providentFundCity;
    private int socialSecurityBase;
    private int providentFundBase;
}
