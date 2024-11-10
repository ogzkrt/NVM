package com.nlang.vm;

public class Context {
    Context parent;
    FunctionInfo info;
    int[] memory;
    int returnAddress;

    public Context(Context parent, FunctionInfo info, int returnAddress) {
        this.parent = parent;
        this.info = info;
        memory = new int[info.argCount * 10 + 10];
        this.returnAddress = returnAddress;
    }
}
