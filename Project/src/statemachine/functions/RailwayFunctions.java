package statemachine.functions;

import java.util.Map; 

public class RailwayFunctions {

    public static void lowerGate() {
        System.out.println(" Gate is lowering.");
   
    }

    public static void raiseGate() {
        System.out.println("Gate is raising.");
    }

    public static void setLightsToRed() {
        System.out.println(" Lights are now RED.");
       
    }

    public static void setLightsToGreen() {
        System.out.println("Lights are now GREEN.");

    }

    public static void turnLightsOff() {
        System.out.println(" Lights are OFF.");
    }

    public static void registerFunctions(Map<String, Runnable> functionsMap) {
        functionsMap.put("lowerGate", RailwayFunctions::lowerGate);
        functionsMap.put("raiseGate", RailwayFunctions::raiseGate);
        functionsMap.put("setLightsToRed", RailwayFunctions::setLightsToRed);
        functionsMap.put("setLightsToGreen", RailwayFunctions::setLightsToGreen);
        functionsMap.put("turnLightsOff", RailwayFunctions::turnLightsOff);
    }
}