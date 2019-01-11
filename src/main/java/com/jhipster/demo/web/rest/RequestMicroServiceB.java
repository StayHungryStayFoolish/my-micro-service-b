package com.jhipster.demo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jhipster.demo.security.AuthoritiesConstants;
import com.jhipster.demo.security.SecurityUtils;
import com.jhipster.demo.web.rest.vm.Person;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/demo/b")
public class RequestMicroServiceB {

    private final Logger log = LoggerFactory.getLogger(RequestMicroServiceB.class);

    @Autowired
    TokenStore tokenStore;

    @GetMapping("/login")
    public String getLogin(HttpServletRequest request) {

        // Parse Cookie base on HttpServletRequest
        Cookie[] cookies = request.getCookies();
        String access_token = "";
        if (null != cookies) {

            for (Cookie cookie : cookies) {
                if ("access_token".equalsIgnoreCase(cookie.getName())) {
                    access_token = cookie.getValue();
                    log.info("ACCESS_TOKEN : {} ", access_token);
                }
            }
        }

        // Parse Header Param base on HttpServletRequest
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            log.info("Header Key : {} - Value : {}", key, value);
        }

        String header = request.getHeader("Authorization");
        if (StringUtils.isNoneBlank(header)) {
            String token = StringUtils.substringAfter(header, "Bearer ");
            log.info("Bearer token : {}" + token);

            if (StringUtils.isEmpty(token)) {
                log.error("Obtaining user information anomaly");
            } else {

                OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
                Map<String, Object> map = accessToken.getAdditionalInformation();
                if (null == map) {
                    log.error("Obtaining user information anomaly");
                }

                String user_name = (String) map.get("user_name");
                if (StringUtils.isBlank(user_name)) {
                    log.error("Obtaining user information anomaly");
                }

                log.info("user_name : {}", user_name);
            }
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {

            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();

            if (null != details) {
                log.info("TokenValue : {}", details.getTokenValue());
            }

            String authorization = request.getHeader("Authorization");
            if (StringUtils.isNoneBlank(authorization)) {
                log.info("Authorization : {}", authorization);
            }
        }

        // judge the current user base on SecurityContext
        Authentication contextAuthentication = securityContext.getAuthentication();
        if (null != contextAuthentication && contextAuthentication.isAuthenticated()) {
            if (contextAuthentication.getPrincipal() instanceof String) {

                log.info("Principal : {} ", contextAuthentication.getPrincipal());

            }
        }

        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        if (login.isPresent()) {
            log.info("Login : {} ", login.get());
            return login.get();
        } else {
            return "None";
        }
    }

    @GetMapping("/string-b")
    @Timed
    public String getString() {
        return "Hello World , From MicroService B ! ";
    }

    @PutMapping("/string-b/{var}")
    @Timed
    public String setString(@PathVariable String var) {
        return "Hello World , " + var + ", From MicroService B ! ";
    }

    // MicroService A 依赖的 实体数据
    @GetMapping("/person-list")
    @Timed
    public List<Person> getPersonList() {
        List<Person> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Person person = new Person();
            person.setName("Name " + i);
            person.setAge(i);
            list.add(person);
        }
        return list;
    }

    /** 配置权限 ***********************************************/

    /**
     * @Secured是从之前Spring版本中引入进来的。它有一个缺点(限制)就是不支持Spring EL表达式
     * @PreAuthorize适合进入方法之前验证授权，可以兼顾，角色/登录用户权限，参数传递给方法等等。
     * 两者除了 EL 表达式支持之外基本相同。
     */

    /**
     * 该方法允许 USER、ADMIN 两种角色
     */
    @GetMapping("/secured/string-b")
    @Timed
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN}) // 指定不同角色
//    @PreAuthorize("hasAnyRole(['ROLE_ADMIN,ROLE_USER'])")  // 指定不同角色
    public String getStringSecured() {
        return " Hello World (secured) from MicroService B! ";
    }

    /**
     * 该方法只允许 ADMIN 用户访问
     */
    @PutMapping("/secured/string-b/{var}")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN})
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addStringSecured(@PathVariable String var) {
        return " Hello World , " + var + " , (secured) from MicroService B! ";
    }
}
