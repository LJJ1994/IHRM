package com.ihrm.common.shiro.session;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

public class CustomSessionManager extends DefaultWebSessionManager {
    /**
     *头信息中有Authorization
     *请求头： Authorization: sessionid
     * 指定sessionId的获取方式
     * @param request
     * @param response
     * @return
     */
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //获取请求头Authorization中的数据
        String id = WebUtils.toHttp(request).getHeader("Authorization");
        if (StringUtils.isEmpty(id)){
            //新生成一个sessionid
            return super.getSessionId(request, response);
        }
        id = id.replaceAll("Bearer ", "");
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "header");
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        return id;
    }
}
