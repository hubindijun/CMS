package com.cms.admin.security;

import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.admin.entity.SysUserRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Token 认证过滤器：从 Authorization header 解析 token，验证后加载用户角色并写入 SecurityContext
 */
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = tokenService.resolveToken(authHeader);

        if (token != null) {
            SysUser user = tokenService.getUserByToken(token);
            if (user != null) {
                List<SimpleGrantedAuthority> authorities = getUserAuthorities(user.getId());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                null,
                                authorities
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> getUserAuthorities(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (SysRole role : roles) {
            if (role.getStatus() != null && role.getStatus() == 1) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
            }
        }
        return authorities;
    }
}
