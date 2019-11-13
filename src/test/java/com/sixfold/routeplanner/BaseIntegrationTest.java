package com.sixfold.routeplanner;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@SpringBootTest
@ActiveProfiles("test")
public class BaseIntegrationTest {

    @Resource
    protected TestRepository repository;
}
