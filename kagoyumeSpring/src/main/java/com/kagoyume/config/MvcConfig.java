package com.kagoyume.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kagoyume.controller.BaseController;


@Configuration
public class MvcConfig  extends BaseController implements WebMvcConfigurer {

    //ビューを返すだけの時用
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/kagoyume/login").setViewName("kagoyume/login");
        registry.addViewController("/kagoyume/index").setViewName("kagoyume/index");
        registry.addViewController("/kagoyume/registration").setViewName("kagoyume/registration");
        registry.addViewController("/kagoyume/mydata").setViewName("kagoyume/mydata");

        registry.addViewController("/kagoyume/myupdate").setViewName("kagoyume/myupdate");

        registry.addViewController("/kagoyume/mydelete").setViewName("kagoyume/mydelete");

    }

}
