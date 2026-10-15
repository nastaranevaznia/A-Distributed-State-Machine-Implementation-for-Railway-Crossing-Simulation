package statemachine.app;

import statemachine.eventbus.SimpleEventBus;
import statemachine.model.Event;
import statemachine.runtime.StateMachineRuntime;
import statemachine.model.StateMachineDefinition; 
import java.util.List;

public class GateApp {
    private static final String SM_NAME = "Gate";
    private static final String JSON_FILE = "GateStateMachine.json"; 
    private static final long EVENT_POLL_INTERVAL_MS = 500; 

    public static void main(String[] args) {
        System.out.println("Starting " + SM_NAME + " Application...");
        DistributedEventMediator.initialize(); 

   
       SimpleEventBus gateBus = new SimpleEventBus("GateBus");
     
        StateMachineDefinition gateDefinition = StateMachineRuntime.loadDefinition(JSON_FILE); 

        StateMachineRuntime gateRuntime = new StateMachineRuntime(SM_NAME, gateDefinition, gateBus);
        Thread runtimeThread = new Thread(gateRuntime, SM_NAME + "-RuntimeThread");
        runtimeThread.start();
        try {
            while (true) {
                List<Event> incomingEvents = DistributedEventMediator.consumeEvents(SM_NAME);
                incomingEvents.forEach(e -> gateBus.postEvent(e)); 

                Thread.sleep(EVENT_POLL_INTERVAL_MS);
            }
        } catch (InterruptedException e) {
            System.out.println(SM_NAME + " Application interrupted.");
        } finally {
            gateRuntime.stop();
            System.out.println(SM_NAME + " Application stopped.");
        }}
    
}