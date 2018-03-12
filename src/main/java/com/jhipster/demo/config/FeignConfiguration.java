package com.jhipster.demo.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.jhipster.demo")
public class FeignConfiguration {

}
