package org.pra.nse.config.experimental;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

//@Configuration
//@ConfigurationProperties("spring.datasource")
//@SuppressWarnings("unused")
public class DbConfig {
    private String driverClassName;
    private String url;
    private String username;
    private String password;


//    @Profile("dev")
//    @Bean
//    public String devDatabaseConnection() {
//        System.out.println("DB Conn for [dev]:" + driverClassName);
//        System.out.println("Driver class name for [dev]:" + driverClassName);
//        System.out.println("username [dev]:" + driverClassName);
//        System.out.println("password [dev]:" + driverClassName);
//        return "DB for Dev Env";
//    }
//
//    @Profile("prod")
//    @Bean
//    public String prodDatabaseConnection() {
//        System.out.println("DB Conn for [prod]:" + driverClassName);
//        System.out.println("Driver class name for [prod]:" + driverClassName);
//        System.out.println("username [prod]:" + driverClassName);
//        System.out.println("password [prod]:" + driverClassName);
//        return "DB for Prod Env";
//    }
}
