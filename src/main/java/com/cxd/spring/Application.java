package com.cxd.spring;

import com.cxd.event.Events;
import com.cxd.state.States;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.statemachine.StateMachine;

import javax.annotation.Resource;

@ComponentScan("com.cxd.spring")
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Resource
    private StateMachine<States, Events> stateMachine;

    @Override
    public void run(String... args) throws Exception {
        stateMachine.sendEvent(Events.CallDialed);
        stateMachine.sendEvent(Events.CallConnected);

        if (!stateMachine.isComplete()) {
            Thread.sleep(1000 * 2);
        }


        stateMachine.stop();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}