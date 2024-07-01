package com.sgcc.framework.security.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.StringUtils;
import com.sgcc.framework.web.service.TokenService;

/**
 * token过滤器 验证token有效性
 * 
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
    @Autowired
    private TokenService tokenService;

    /**
     * 重写doFilterInternal方法，用于在过滤器链中执行特定的过滤操作
     *
     * @param request     HttpServletRequest对象，表示客户端发送的HTTP请求
     * @param response    HttpServletResponse对象，表示服务器对HTTP请求的响应
     * @param chain       FilterChain对象，表示过滤器链的剩余部分
     * @throws ServletException 如果在过滤器链中发生Servlet异常，则抛出此异常
     * @throws IOException      如果在过滤器链中发生I/O异常，则抛出此异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        // 获取登录用户信息
        LoginUser loginUser = tokenService.getLoginUser(request);
        // 判断登录用户信息是否存在且当前未认证
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
        {
            // 验证登录用户的Token
            tokenService.verifyToken(loginUser);
            // 创建UsernamePasswordAuthenticationToken对象，表示已认证的用户
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            // 设置认证用户的详细信息
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将认证用户信息设置到安全上下文中
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // 继续过滤器链的后续处理
        chain.doFilter(request, response);
    }
}
