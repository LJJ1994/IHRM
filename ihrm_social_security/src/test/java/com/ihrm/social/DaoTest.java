package com.ihrm.social;

import com.github.pagehelper.Page;
import com.ihrm.domain.social_security.UserSocialSecurity;
import com.ihrm.domain.social_security.request.UserSocialSecurityRequest;
import com.ihrm.domain.social_security.response.UserSocialSecurityDTO;
import com.ihrm.social.dao.UserSocialSecurityMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTest {
    @Autowired
    private UserSocialSecurityMapper securityMapper;

    @Test
    public void test(){
        UserSocialSecurityRequest request = new UserSocialSecurityRequest();
        request.setCompanyId("1");
        Page<UserSocialSecurityDTO> list = securityMapper.findList(request);
        List<UserSocialSecurityDTO> result = list.getResult();
        for (UserSocialSecurityDTO security : result) {
            System.out.println(security);
        }
    }
}
