package statemachine.model;

public class FunctionCallAction extends Action {
    private String functionName; 

    public FunctionCallAction() {
        super("functionCall"); 
    }

    public String getFunctionName() {
        return functionName;
    }}
