package statemachine.model;

public abstract class Action {
   
    private String type; 

    public Action(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}