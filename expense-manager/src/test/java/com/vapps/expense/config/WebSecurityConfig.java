package com.vapps.expense.config;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vapps.security.config.URLConfig;
//import com.vapps.security.config.WebSecurityConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class WebSecurityConfig {

	private final static Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);
	//
	//    @Bean
	//    public WebSecurityConfiguration webSecurityConfiguration() throws IOException {
	//
	//        ObjectMapper mapper = new ObjectMapper();
	//        InputStream inputStream = getClass().getResourceAsStream("/security.json");
	//        List<URLConfig> urlConfigs = null;
	//        try {
	//            urlConfigs = Arrays.asList(mapper.readValue(inputStream, URLConfig[].class));
	//        } catch (IOException e) {
	//            LOGGER.error(e.getMessage(), e);
	//            throw e;
	//        }
	//
	//        return new WebSecurityConfiguration(urlConfigs);
	//    }
}
