package com.ticket.config;

import com.ticket.filter.HeaderEnhanceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class FilterConfig {

    @Autowired
    HeaderEnhanceFilter headerEnhanceFilter;

    /**
     * HeaderEnhanceFilter也要进行注册。Spring 提供了FilterRegistrationBean类
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(headerEnhanceFilter);
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public HeaderEnhanceFilter headerEnhanceFilter() {
        return new HeaderEnhanceFilter();
    }

}

