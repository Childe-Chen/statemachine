package com.cxd.spring;

import com.cxd.event.Events;
import com.cxd.state.States;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import javax.annotation.Resource;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Resource
    private StateMachine<States, Events> stateMachine;

    @Override
    public void run(String... args) throws Exception {
        stateMachine.sendEvent(Events.CallDialed);
        stateMachine.sendEvent(Events.CallConnected);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}