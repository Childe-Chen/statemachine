package com.cxd.squirrel;

import com.cxd.event.Events;
import com.cxd.state.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.StateMachine;
import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;

/**
 * desc
 *
 * @author childe
 * @date 2018/4/25 10:23
 **/
public class QuickStartSample {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuickStartSample.class);

    static StateMachineBuilder BUILDER = getStateMachineBuilder();

    public static void main(String[] args) {
        // 4. Use State Machine
        StateMachine fsm = BUILDER.newStateMachine(States.OffHook);

        MyContext context10 = new MyContext();
        context10.setNo("10");
        try {
            fsm.fire(Events.CallDialed, context10);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.warn("Current state is "+fsm.getCurrentState());

        LOGGER.info("----------------------------------------------");

        StateMachine fsm1 = BUILDER.newStateMachine(States.OffHook);
        MyContext contextYes = new MyContext();
        contextYes.setNo("yes");
        try {
            fsm1.fire(Events.CallDialed, contextYes);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.warn("Current state is "+fsm1.getCurrentState());

    }

    private static StateMachineBuilder getStateMachineBuilder() {
        StateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachineSquirrel.class,States.class,Events.class,MyContext.class);
        builder.externalTransition().from(States.OffHook).to(States.Ringing)
                .on(Events.CallDialed).when(new MyCondition()).callMethod("transition");
        builder.onEntry(States.OffHook).callMethod("entry");
        builder.onEntry(States.Ringing).callMethod("entry");
        return builder;
    }
}
