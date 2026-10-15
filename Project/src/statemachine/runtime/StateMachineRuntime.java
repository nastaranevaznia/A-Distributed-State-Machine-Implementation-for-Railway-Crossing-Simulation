package statemachine.runtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import statemachine.eventbus.SimpleEventBus;
import statemachine.functions.RailwayFunctions; 
import statemachine.model.Action;
import statemachine.model.Event; 
import statemachine.model.EventEmissionAction;
import statemachine.model.FunctionCallAction;
import statemachine.model.State; 
import statemachine.model.StateMachineDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap; 


public class StateMachineRuntime implements Runnable { 
    private final String name;
    private final StateMachineDefinition definition;
    private final SimpleEventBus eventBus;
    private volatile boolean running = true; 
    private State currentState; 
    private final Map<String, Runnable> registeredFunctions;

    public StateMachineRuntime(String name, StateMachineDefinition definition, SimpleEventBus eventBus) {
        this.name = name;
        this.definition = definition;
        this.eventBus = eventBus;
        this.registeredFunctions = new ConcurrentHashMap<>(); 

        RailwayFunctions.registerFunctions(registeredFunctions); 

        this.currentState = definition.getInitialState(); 
        System.out.println(" Initializing. Current state: " + currentState.getName());
        executeOnEntryActions(currentState);
    }

    public static StateMachineDefinition loadDefinition(String jsonFileName) {
        RuntimeTypeAdapterFactory<Action> actionTypeFactory = RuntimeTypeAdapterFactory
                .of(Action.class, "type")
                .registerSubtype(FunctionCallAction.class, "functionCall")
                .registerSubtype(EventEmissionAction.class, "eventEmission");

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapterFactory(actionTypeFactory)
                .create();

        try (InputStream is = StateMachineRuntime.class.getClassLoader().getResourceAsStream(jsonFileName);
             Reader reader = new InputStreamReader(Objects.requireNonNull(is, "Resource not found: " + jsonFileName))) {

            StateMachineDefinition definition = gson.fromJson(reader, StateMachineDefinition.class);
            definition.initializeTransientFields(); 
                return definition;
        } catch (IOException | NullPointerException | JsonSyntaxException | JsonIOException e) {
          
            throw new RuntimeException("Failed to load state machine definition from " + jsonFileName, e);
        }
    }

    @Override
    public void run() {
        System.out.println(" Runtime started. Waiting for events...");
        while (running) { 
            try {
                eventBus.processEvents(event -> {
                    System.out.println(" Received event: " + event.getEventType());
                    State nextState = definition.getNextState(currentState, event.getEventType());

                    if (nextState != null && !nextState.equals(currentState)) {
                        System.out.println("Transitioned from " + currentState.getName() + " to " + nextState.getName() + " on event " + event.getEventType());
                        currentState = nextState;
                        executeOnEntryActions(currentState); // Execute actions for the new state
                    } else {
                        System.out.println(" No transition from " + currentState.getName() + " for event " + event.getEventType());
                    }
                });

                Thread.sleep(100);

            } catch (InterruptedException e) {
                System.out.println("Runtime interrupted.");
                Thread.currentThread().interrupt(); 
                running = false; 
            } catch (Exception e) {
                System.err.println(" An error occurred during runtime processing: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Runtime stopped.");
    }

    public void stop() {
        System.out.println("Stopping runtime thread...");
        this.running = false; 
    }


    private void executeOnEntryActions(State state) {
        if (state.getOnEntryActions() != null) {
            for (Action action : state.getOnEntryActions()) {
                if (action instanceof FunctionCallAction) {
                    FunctionCallAction fca = (FunctionCallAction) action;
                    System.out.println("Executing function: " + fca.getFunctionName());
                    Runnable func = registeredFunctions.get(fca.getFunctionName());
                    if (func != null) {
                        func.run();
                    } else {
                        System.err.println(" WARNING: Function '" + fca.getFunctionName() + "' not registered for " + name + " state machine.");
                    }
                } else if (action instanceof EventEmissionAction) {
                    EventEmissionAction eea = (EventEmissionAction) action;
                    System.out.println(" Emitting outbound event: " + eea.getEventName());
                  
                    eventBus.postOutboundEvent(new Event(eea.getEventName(), null)); 
                }
            }
        }
    }
}