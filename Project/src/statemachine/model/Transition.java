package statemachine.model;

public class Transition {
    private String eventType; 
    private String targetStateName; 
private String sourceState;
 private String targetState;
    public Transition() {
    }

    public String getEventType() {
        return eventType;
    }

    public String getTargetState() {
        return targetState;
    }
    public String getSourceState() {
return sourceState;
}
}