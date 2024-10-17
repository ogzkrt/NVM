package com.nlang.vm;

public class FunctionInfo {

    String name;
    int argCount;
    int address;

    public FunctionInfo(String name, int argCount, int address) {
        this.name = name;
        this.argCount = argCount;
        this.address = address;
    }
}
