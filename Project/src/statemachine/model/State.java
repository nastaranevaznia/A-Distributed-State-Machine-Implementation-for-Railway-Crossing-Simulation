package statemachine.model;

import java.util.List;

public class State {
    private String name;
    private List<Action> onEntryActions; 
    private List<Transition> transitions; 


    public State() {
    }


    public String getName() {
        return name;
    }

    public List<Action> getOnEntryActions() {
        return onEntryActions;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }
}