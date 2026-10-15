package statemachine.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StateMachineDefinition {
    @SerializedName("initialState")
    private String initialStateName;
    private String name;
    private List<State> states;
    private List <Transition> transitions;

   
    private transient Map<String, State> stateMap;

    public StateMachineDefinition() {
    }
    public List<Transition> getTransitions() {
return transitions;
}
// public void setTransitions(List<Transition> transitions) {
// this.transitions = transitions;
// }
    public String getName() {
        return name;
    }

    public List<State> getStates() {
        return states;
    }

    public String getInitialStateName() {
        return initialStateName;
    }

    public void initializeTransientFields() {
        stateMap = new java.util.HashMap<>();
        if (states != null) {
            for (State state : states) {
                stateMap.put(state.getName(), state);
            }
        }
    }


    public State getInitialState() {
        if (stateMap == null) {
            initializeTransientFields();
        }
        State initialState = stateMap.get(initialStateName);
        if (initialState == null) {
            throw new IllegalStateException("Initial state '" + initialStateName + "' not found in state machine definition.");
        }
        return initialState;
    }

    public State getNextState(State currentState, String eventType) {
        if (currentState == null || currentState.getTransitions() == null) {
            return null; 
        }
        for (Transition transition : currentState.getTransitions()) {
            if (Objects.equals(transition.getEventType(), eventType)) {
                if (stateMap == null) {
                    initializeTransientFields();
                }
                State targetState = stateMap.get(transition.getTargetState());
                if (targetState == null) {
                    System.err.println("WARNING: Target state '" + transition.getTargetState() + "' for event '" + eventType + "' not found.");
                    return null;
                }
                return targetState;
            }
        }
        return null; 
    }}
