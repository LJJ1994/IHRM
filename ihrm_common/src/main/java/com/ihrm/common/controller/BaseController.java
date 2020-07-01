package com.ihrm.common.controller;

import com.ihrm.domain.system.response.ProfileResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基本控制器，获取companyId, companyName, request, response
 */
public class BaseController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected String companyId;
    protected String companyName;

    /**
     * 通过shiro方式获取
     * @param request
     * @param response
     */
    @ModelAttribute
    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response =response;
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        if (principals != null && !principals.isEmpty()) {
            ProfileResult profileResult = (ProfileResult) principals.getPrimaryPrincipal();
            this.companyId = profileResult.getCompanyId();
            this.companyName = profileResult.getCompany();
        }
    }

    /**
     * 获取企业id
     */
    public String parseCompanyId(){
        return "1";
    }

    /**
     * 获取企业名称
     */
    public String parseCompanyName(){
        return "江苏传智播客教育股份有限公司";
    }

    //使用jwt方式获取
//    @ModelAttribute
//    public void setResAnReq(HttpServletRequest request,HttpServletResponse response) {
//        this.request = request;
//        this.response = response;
//
//        Object obj = request.getAttribute("user_claims");
//
//        if(obj != null) {
//            this.claims = (Claims) obj;
//            this.companyId = (String)claims.get("companyId");
//            this.companyName = (String)claims.get("companyName");
//        }
//    }


}
