package statemachine.app;

import statemachine.eventbus.SimpleEventBus;
import statemachine.model.Event;
import statemachine.runtime.StateMachineRuntime;
import statemachine.model.StateMachineDefinition; 
import java.util.Collections;
import java.util.List;

public class ControllerApp {
    private static final String SM_NAME = "Controller";
    private static final String JSON_FILE = "ControllerStateMachine.json";
    private static final long SENSOR_POLL_INTERVAL_MS = 2000; 

    public static void main(String[] args) {
        System.out.println("Starting " + SM_NAME + " Application...");
        DistributedEventMediator.initialize();
       
        SimpleEventBus controllerBus = new SimpleEventBus("ControllerBus");

    
        controllerBus.setOutboundEventPublisher(event -> {
           DistributedEventMediator.publishEvent("Gate", event);
             DistributedEventMediator.publishEvent("Light", event);
        });


        StateMachineDefinition controllerDefinition = StateMachineRuntime.loadDefinition(JSON_FILE);
        StateMachineRuntime controllerRuntime = new StateMachineRuntime(SM_NAME, controllerDefinition, controllerBus);
        Thread runtimeThread = new Thread(controllerRuntime, SM_NAME + "-RuntimeThread");
         runtimeThread.start();

        try {
           
            String[] sensorEvents = {"sensor_seen", "sensor_seen", "sensor_seen", "leaving", "sensor_seen", "sensor_seen", "sensor_seen"};
           int eventIndex = 0;

            while (true) {
              
                List<Event> incomingEvents = DistributedEventMediator.consumeEvents(SM_NAME);
              incomingEvents.forEach(e -> controllerBus.postEvent(e));

           
                if (eventIndex < sensorEvents.length) {
                    controllerBus.postEvent(new Event(sensorEvents[eventIndex], Collections.emptyMap()));                   
                    eventIndex++;
                } else {
            
                    eventIndex = 0; 
                    Thread.sleep(SENSOR_POLL_INTERVAL_MS * 4); 
                }

                 Thread.sleep(SENSOR_POLL_INTERVAL_MS);
            }
        } catch (InterruptedException e) {
            System.out.println(SM_NAME + " Application interrupted.");
        } finally {
           controllerRuntime.stop();
        
           System.out.println(SM_NAME + " Application stopped.");
        }
    }}
