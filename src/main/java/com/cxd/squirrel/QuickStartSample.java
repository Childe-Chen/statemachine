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

    private static StateMachineBuilder<StateMachineSquirrel, States, Events, MyContext> BUILDER = getStateMachineBuilder();

    public static void main(String[] args) {
//         4. Use State Machine
        Thread thread = new Thread(() -> {
                for (int i = 0; i <= 100_0000; i++) {
                    StateMachine<StateMachineSquirrel, States, Events, MyContext> fsm = BUILDER.newStateMachine(States.OffHook);

                    MyContext context10 = new MyContext();
                    context10.setNo("10");
                    try {
                        //            fsm.fire(Events.CallDialed, context10);
                        context10.setNo("yes");
                        fsm.fire(Events.CallDialed,context10);
                        fsm.fire(Events.HungUp,context10);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage());
                    }
                    LOGGER.warn("Current state is "+fsm.getCurrentState());
                }
            }
        );
        thread.setName("fori");
//        thread.start();
//
//        LOGGER.warn("----------------------------------------------");

//        for (int i = 0; i < 10_0000; i++) {
            StateMachine<StateMachineSquirrel, States, Events, MyContext> fsm1 = BUILDER.newStateMachine(States.OffHook);
            MyContext contextYes = new MyContext();
            contextYes.setNo("yes");
            try {
                fsm1.fire(Events.CallDialed, contextYes);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
//        }

//        LOGGER.warn("Current state is "+fsm1.getCurrentState());
//
//        fsm1.terminate();
    }

    private static StateMachineBuilder<StateMachineSquirrel, States, Events, MyContext> getStateMachineBuilder() {
        StateMachineBuilder<StateMachineSquirrel, States, Events, MyContext> builder = StateMachineBuilderFactory.create(StateMachineSquirrel.class,States.class,Events.class,MyContext.class);

        builder.externalTransition().from(States.OffHook).to(States.Ringing)
                .on(Events.CallDialed).when(new MyCondition()).callMethod("transitionFromAnyToRinging");

        builder.externalTransition().from(States.Ringing).to(States.OffHook).on(Events.HungUp).callMethod("transition");

        builder.onEntry(States.OffHook).callMethod("entry");

        builder.onExit(States.OffHook).callMethod("exit");
        builder.onEntry(States.Ringing).callMethod("entry");

        return builder;
    }
}
