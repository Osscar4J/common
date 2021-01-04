package com.zhao.common.utils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JSEngineManager {

    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("javascript");

    public static Object execFunctions(String jsFunc, String mainFunc, Object ...args){
        try {
            engine.eval(jsFunc);
            if (engine instanceof Invocable) {
                Invocable in = (Invocable) engine;
                return in.invokeFunction(mainFunc, args);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Object execMainFunction(String jsFunc, Object ...args){
        return execFunctions(jsFunc, "main", args);
    }

    public static Object execMainFunction(String jsFunc){
        return execFunctions(jsFunc, "main", null);
    }

}
