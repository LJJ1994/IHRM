package com.ihrm.social.service;

import com.github.pagehelper.PageHelper;
import com.ihrm.common.entity.PageResult;
import com.ihrm.domain.social_security.UserSocialSecurity;
import com.ihrm.domain.social_security.request.UserSocialSecurityRequest;
import com.ihrm.domain.social_security.response.UserSocialSecurityDTO;
import com.ihrm.social.dao.UserSocialSecurityRepository;
import com.ihrm.social.dao.UserSocialSecurityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class UserSocialService {
	
    @Autowired
    private UserSocialSecurityRepository userSocialSecurityDao;

    @Autowired
    private UserSocialSecurityMapper userSocialSecurityMapper;

    /**
     * 分页查询用户的社保数据
     * @param page  页数
     * @param pageSize  每页条数
     * @param companyId 公司id
     * @return  分页结果
     */
//    public PageResult findAll(int page, int pageSize, String companyId) {
//
//        Page<Map> pageData = userSocialSecurityDao.findPage(companyId, new PageRequest(page - 1, pageSize));
//        return new PageResult(pageData.getTotalElements() , pageData.getContent());
//    }

    /**
     * Mabatis分页查询用户社保数据
     * @param request
     * @param companyId
     * @return
     */
    public PageResult<UserSocialSecurityDTO> findList(UserSocialSecurityRequest request, String companyId){
        if (request == null){
            request = new UserSocialSecurityRequest();
        }
        int page = request.getPage();
        int pageSize = request.getPageSize();
        if (page < 1){
            page = 1;
        }
        if (pageSize < 1){
            pageSize = 10;
        }
        request.setCompanyId(companyId);
        PageHelper.startPage(page, pageSize);
        com.github.pagehelper.Page<UserSocialSecurityDTO> result = userSocialSecurityMapper.findList(request);
        List<UserSocialSecurityDTO> list = result.getResult();

        long total = result.getTotal();
        PageResult<UserSocialSecurityDTO> pageResult = new PageResult<>();
        pageResult.setRows(list);
        pageResult.setTotal(total);

        return pageResult;
    }


    /**
     * 根据id查询社保数据
     * @param id    用户id
     * @return  根据ID查询的社保数据
     */
    public UserSocialSecurity findById(String id) {
        Optional<UserSocialSecurity> userSocialSecurity = userSocialSecurityDao.findById(id);
        return userSocialSecurity.isPresent() ? userSocialSecurity.get() : null;
    }

    /**
     * 保存或更新用户社保
     * @param uss   用户社保
     */
    public void save(UserSocialSecurity uss) {
        userSocialSecurityDao.save(uss);
    }
}
