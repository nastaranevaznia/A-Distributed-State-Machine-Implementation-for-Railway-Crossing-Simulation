package statemachine.model;

public class EventEmissionAction extends Action {
    private String eventName; 

    public EventEmissionAction() {
        super("eventEmission"); 
    }

    public String getEventName() {
        return eventName;
    }
}