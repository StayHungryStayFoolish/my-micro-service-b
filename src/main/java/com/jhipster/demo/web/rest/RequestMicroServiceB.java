package com.jhipster.demo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jhipster.demo.web.rest.vm.Person;
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
}
