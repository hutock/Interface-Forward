package com.whub507.interfaceforwarder.service;

import com.whub507.interfaceforwarder.config.InterfaceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Order(1)
@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    InterfaceConfig interfaceConfig;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        interfaceConfig.initConfig();
    }
}
