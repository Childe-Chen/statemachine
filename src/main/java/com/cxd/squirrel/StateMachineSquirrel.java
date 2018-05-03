package com.cxd.squirrel;

import com.cxd.event.Events;
import com.cxd.state.States;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.StateMachine;
import org.squirrelframework.foundation.fsm.annotation.AsyncExecute;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

/**
 * desc
 *
 * @author childe
 * @date 2018/4/25 15:22
 **/
@StateMachineParameters(stateType=States.class, eventType=Events.class, contextType=MyContext.class)
public class StateMachineSquirrel extends AbstractStateMachine<StateMachineSquirrel, States, Events, MyContext>
        implements StateMachine<StateMachineSquirrel, States, Events, MyContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateMachineSquirrel.class);

    /**
     * 该注解仅对action做异步操作
     * 但是比较蛋疼，调用线程需要等待该线程完成才继续往下走。
     * 参见AbstractExecutionService#doExecute
     * 既然用了异步为什么还要等待？不清楚为什么这么设计。
     * 10:54:24.591 [main] WARN com.cxd.squirrel.StateMachineSquirrel - beforeTransitionBegin
     * 10:54:24.594 [main] WARN com.cxd.squirrel.MyCondition - MyContext{no='yes'}
     * 10:54:24.611 [pool-1-thread-1] WARN com.cxd.squirrel.StateMachineSquirrel - beforeActionInvoked
     * 10:54:24.611 [pool-1-thread-1] WARN com.cxd.squirrel.StateMachineSquirrel - Transition from 'OffHook' to 'Ringing' on event 'CallDialed' with context 'MyContext{no='yes'}'.
     * 10:54:24.612 [pool-1-thread-1] WARN com.cxd.squirrel.StateMachineSquirrel - afterActionInvoked
     * 10:54:24.613 [main] WARN com.cxd.squirrel.StateMachineSquirrel - afterTransitionCompleted
     * 10:54:24.614 [main] WARN com.cxd.squirrel.StateMachineSquirrel - afterTransitionEnd
     * 10:54:24.614 [main] WARN com.cxd.squirrel.QuickStartSample - Current state is Ringing
     * @param from
     * @param to
     * @param event
     * @param context
     */
//    @AsyncExecute
    protected void transition(States from, States to, Events event, MyContext context) {
        LOGGER.warn("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 方法名为transitFrom[SourceStateName]To[TargetStateName]On[EventName]，
     * 参数名为[MyState, MyState, MyEvent, MyContext]的方法会被添加到transition “A-(GoToB)->B”的action列表中。
     * 当状态机从’A’到’B’且触发的event为GoToB的时候，该方法会被调用。
     *
     * BUT 好像没用！这种约定太过于约定了！
     * @param from
     * @param to
     * @param event
     * @param context
     */
//    @AsyncExecute
    public void transitionFromAnyToRinging(States from, States to, Events event, MyContext context) {
        LOGGER.warn("callMethod Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    protected void entry(States fromState, States toState, Events event, MyContext context) {
        //状态机初始化时也会处罚该方法，但因为是初始化所以from不存在
        //状态机的初始化也会发布进入事件，个人认为多此一举！
        LOGGER.warn("Entry State {}", toState);
    }

    protected void exit(States fromState, States toState, Events event, MyContext context) {
        //状态机初始化时也会处罚该方法，但因为是初始化所以from不存在
        //状态机的初始化也会发布进入事件，个人认为多此一举！
        LOGGER.warn("exit State {}", fromState);
    }

    @Override
    protected void afterTransitionCausedException(States fromState, States toState, Events event, MyContext context) {
        throw getLastException();
    }


    @Override
    public void fire(Events event, MyContext context) {
        Preconditions.checkNotNull(event, "Cannot fire null event.");
        super.fire(event, context);
    }

    @Override
    public void fireImmediate(Events event, MyContext context) {
        Preconditions.checkNotNull(event, "Cannot fire null event.");
        super.fire(event, context);
    }

    @Override
    public States test(Events event, MyContext context) {
        Preconditions.checkNotNull(event, "Cannot fire null event.");
        return super.test(event, context);
    }

    @Override
    protected void beforeTransitionBegin(States fromState, Events event, MyContext context) {
        LOGGER.warn("beforeTransitionBegin");
    }

    @Override
    protected void afterTransitionCompleted(States fromState, States toState, Events event, MyContext context) {
        LOGGER.warn("afterTransitionCompleted");
    }

    @Override
    protected void afterTransitionEnd(States fromState, States toState, Events event, MyContext context) {
        LOGGER.warn("afterTransitionEnd");
    }

    @Override
    protected void afterTransitionDeclined(States fromState, Events event, MyContext context) {
        LOGGER.warn("afterTransitionDeclined");
    }

    @Override
    protected void beforeActionInvoked(States fromState, States toState, Events event, MyContext context) {
        if (fromState == null) {
            return;
        }
//        try {
            LOGGER.warn("beforeActionInvoked");
//            Thread.sleep(1000 * 10);
//        } catch (InterruptedException e) {
//            LOGGER.error(e.getMessage(), e);
//        }
    }

    @Override
    protected void afterActionInvoked(States fromState, States toState, Events event, MyContext context) {
        if (fromState == null) {
            return;
        }
        LOGGER.warn("afterActionInvoked");
    }
}
