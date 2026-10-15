
package statemachine.app;


import statemachine.eventbus.SimpleEventBus;
import statemachine.model.Event;
import statemachine.runtime.StateMachineRuntime;
import statemachine.model.StateMachineDefinition; 

import java.util.List;

public class LightApp {
    private static final String SM_NAME = "Light";
    private static final String JSON_FILE = "LightStateMachine.json";
    private static final long EVENT_POLL_INTERVAL_MS = 500; 

    public static void main(String[] args) {
        System.out.println("Starting " + SM_NAME + " Application...");
        DistributedEventMediator.initialize(); 

     
        SimpleEventBus lightBus = new SimpleEventBus("LightBus");       

        StateMachineDefinition lightDefinition = StateMachineRuntime.loadDefinition(JSON_FILE);
        StateMachineRuntime lightRuntime = new StateMachineRuntime(SM_NAME, lightDefinition, lightBus);
        Thread runtimeThread = new Thread(lightRuntime, SM_NAME + "-RuntimeThread");
        runtimeThread.start();

        try {
            while (true) {
                List<Event> incomingEvents = DistributedEventMediator.consumeEvents(SM_NAME);
                incomingEvents.forEach(e -> lightBus.postEvent(e)); 

                Thread.sleep(EVENT_POLL_INTERVAL_MS); 
            }
        } catch (InterruptedException e) {
            System.out.println(SM_NAME + " Application interrupted.");
        } finally {
            lightRuntime.stop();
            // runtimeThread.join();
            System.out.println(SM_NAME + " Application stopped.");
        }
    }
}