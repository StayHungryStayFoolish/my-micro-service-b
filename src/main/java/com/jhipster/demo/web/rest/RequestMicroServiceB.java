package com.jhipster.demo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jhipster.demo.security.AuthoritiesConstants;
import com.jhipster.demo.web.rest.vm.Person;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/demo")
public class RequestMicroServiceB {


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
