package com.cxd.oxo;

import com.cxd.event.Trigger;
import com.cxd.exception.BizException;
import com.cxd.state.States;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;
import com.github.oxo42.stateless4j.transitions.Transition;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * desc
 *
 * @author childe
 * @date 2018/4/23 15:19
 **/
public class MachineOxo {

    final static Logger log = LoggerFactory.getLogger(MachineOxo.class);

    public static void main(String[] args) {
        StateMachineConfig<States, Trigger> phoneCallConfig = new StateMachineConfig<>();

        phoneCallConfig.configure(States.OffHook)
                .onEntry((Transition<States, Trigger> entryTransition) -> {
                    log.warn("States.OffHook entry {}", entryTransition.isReentry());
                }).permitIf(Trigger.CallDialed, States.Ringing, () -> {
                        throw new BizException("故意的");
                    }
                );

        phoneCallConfig.configure(States.Ringing)
                .onEntry((Transition<States, Trigger> entryTransition) -> {
                    log.warn("States.Ringing entry {}", entryTransition.isReentry());
                })
                .permit(Trigger.HungUp, States.OffHook)
                .permit(Trigger.CallConnected, States.Connected);

        phoneCallConfig.configure(States.Connected)
                .onEntry(MachineOxo::startCallTimer)
                .onExit(MachineOxo::stopCallTimer)
                .permit(Trigger.LeftMessage, States.OffHook)
                .permit(Trigger.HungUp, States.OffHook)
                .permit(Trigger.PlacedOnHold, States.OnHold);

        phoneCallConfig.configure(States.OnHold)
                .onExit((Transition<States, Trigger> triggerTransition) ->
                    log.warn("States.OnHold exit {}", triggerTransition.getDestination())
                ).permitDynamic(new TriggerWithParameters1(Trigger.BOON, String.class), (String s) -> {
                    log.warn("States.OnHold permit {}", s);
                    return States.OffHook;
                });

        phoneCallConfig.setTriggerParameters(Trigger.BOON,String.class);

        // ...

        StateMachine<States, Trigger> phoneCall = new StateMachine<>(States.OffHook, phoneCallConfig);

        phoneCall.onUnhandledTrigger((States state,Trigger trigger) -> {
            log.error(state.name() + "--" + trigger.name());
        });


//        TriggerWithParameters1 parameters1 = new TriggerWithParameters1<String,States,Trigger>(Trigger.BOON, String.class);
//        phoneCall.fire(parameters1, "ssss");


        try {
            phoneCall.fire(Trigger.CallDialed);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("current state {}", phoneCall.getState().name());

    }

    private static void stopCallTimer() {
        // ...
        log.info("stopCallTimer");
    }

    private static void startCallTimer() {
        // ...
        log.info("startCallTimer");
    }
}
