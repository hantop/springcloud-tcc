package com.tuandai.architecture.logs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class MyLogConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MyLogEndpoint changeLogEndpoint() {
        return new MyLogEndpoint();
    }

    @Bean
    @ConditionalOnBean(MyLogEndpoint.class)
    public MyLogMvcEndpoint changeLogMvcEndpoint(MyLogEndpoint delegate) {
        return new MyLogMvcEndpoint(delegate);
    }

}