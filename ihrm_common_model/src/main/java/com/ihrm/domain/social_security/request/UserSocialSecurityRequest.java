package com.ihrm.domain.social_security.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserSocialSecurityRequest {
    private List<String> departmentChecks;
    private List<String> providentFundChecks;
    private List<String> socialSecurityChecks;
    private String companyId;
    private String keyword;
    private int page;
    private int pageSize;
}
