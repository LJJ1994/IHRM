package com.ihrm.social.dao;

import com.github.pagehelper.Page;
import com.ihrm.domain.social_security.request.UserSocialSecurityRequest;
import com.ihrm.domain.social_security.response.UserSocialSecurityDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Mapper
public interface UserSocialSecurityMapper {
    Page<UserSocialSecurityDTO> findList(UserSocialSecurityRequest request);
}
