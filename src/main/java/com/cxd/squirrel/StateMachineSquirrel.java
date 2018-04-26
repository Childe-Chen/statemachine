package com.cxd.squirrel;

import com.cxd.event.Events;
import com.cxd.state.States;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

/**
 * desc
 *
 * @author childe
 * @date 2018/4/25 15:22
 **/
@StateMachineParameters(stateType=States.class, eventType=Events.class, contextType=MyContext.class)
public class StateMachineSquirrel extends AbstractStateMachine<StateMachineSquirrel, States, Events, MyContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateMachineSquirrel.class);

    protected void transition(States from, States to, Events event, MyContext context) {
        LOGGER.warn("Transition from '"+from+"' to '"+to+"' on event '"+event+
                "' with context '"+context+"'.");
    }

    protected void entry(States from, States to, Events event, MyContext context) {
        LOGGER.warn("Entry State \'"+to+"\'.");
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
}
