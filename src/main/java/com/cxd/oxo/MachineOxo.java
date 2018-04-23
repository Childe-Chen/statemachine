package com.cxd.oxo;

import com.cxd.event.Trigger;
import com.cxd.state.States;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
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
                .permit(Trigger.CallDialed, States.Ringing);

        phoneCallConfig.configure(States.Ringing)
                .permit(Trigger.HungUp, States.OffHook)
                .permit(Trigger.CallConnected, States.Connected);

        phoneCallConfig.configure(States.Connected)
                .onEntry(MachineOxo::startCallTimer)
                .onExit(MachineOxo::stopCallTimer)
                .permit(Trigger.LeftMessage, States.OffHook)
                .permit(Trigger.HungUp, States.OffHook)
                .permit(Trigger.PlacedOnHold, States.OnHold);

        phoneCallConfig.configure(States.OnHold)
                .permitDynamic(new TriggerWithParameters1(Trigger.BOON, String.class), (String s) -> {
                    log.info(s);
                    return States.OffHook;
                });

        phoneCallConfig.setTriggerParameters(Trigger.BOON,String.class);

        // ...

        StateMachine<States, Trigger> phoneCall = new StateMachine<>(States.OnHold, phoneCallConfig);

        phoneCall.onUnhandledTrigger((States state,Trigger trigger) -> {
            System.out.println(state.name() + "--" + trigger.name());
        });

        log.info("before fire {}",phoneCall.getState());
        log.warn("can fire {} ", phoneCall.canFire(Trigger.BOON));
        TriggerWithParameters1 parameters1 = new TriggerWithParameters1<String,States,Trigger>(Trigger.BOON, String.class);
        phoneCall.fire(parameters1,"e");
        log.info("after fire {}",phoneCall.getState());

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
