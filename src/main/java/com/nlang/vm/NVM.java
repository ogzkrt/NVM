package com.nlang.vm;

import static com.nlang.vm.InstructionSet.*;


public class NVM {

    private final boolean debugMode;
    private final int[] code;
    private final int[] stack;

    private int sp = -1;
    private int ip = 0;

    private Context context;

    private final FunctionInfo[] functionTable;

    public NVM(int[] code, FunctionInfo[] functionTable) {
        this(code, functionTable, false);
    }

    public NVM(int[] code, FunctionInfo[] functionTable, boolean debugMode) {
        this.debugMode = debugMode;
        this.code = code;
        this.stack = new int[1024];
        this.functionTable = functionTable;
        this.context = new Context(null, functionTable[0], code.length - 1);
    }


    public void execute() {
        ip = context.info.address;
        int inst = code[ip];
        while (inst != STOP) {
            execute(inst);
            if (debugMode) {
                Diagnostic.printInstruction(inst, code, ip);
                Diagnostic.printStack(stack, sp);
                Diagnostic.printCallMemory(context);
            }
            inst = code[ip];
        }
    }

    private void execute(int inst) {
        int first, second;
        switch (inst) {
            case IPUSH:
                int val = code[++ip];
                stack[++sp] = val;
                ip++;
                break;
            case IADD:
                first = stack[sp--];
                second = stack[sp--];
                stack[++sp] = first + second;
                ip++;
                break;
            case ISUB:
                first = stack[sp--];
                second = stack[sp--];
                stack[++sp] = second - first;
                ip++;
                break;
            case IMUL:
                first = stack[sp--];
                second = stack[sp--];
                stack[++sp] = first * second;
                ip++;
                break;
            case IDIV:
                first = stack[sp--];
                second = stack[sp--];
                stack[++sp] = second / first;
                ip++;
                break;
            case ILT:
                first = stack[sp--];
                second = stack[sp--];
                if (second < first) {
                    stack[++sp] = 1;
                } else {
                    stack[++sp] = 0;
                }
                ip++;
                break;
            case JZ:
                int loc = code[++ip];
                int r = stack[sp--];
                if (r == 1) {
                    ip++;
                } else {
                    ip = loc;
                }
                break;
            case JNZ:
                int jnz = code[++ip];
                int rJnz = stack[sp--];
                if (rJnz == 1) {
                    ip = jnz;
                } else {
                    ip++;
                }
                break;
            case JMP:
                ip = code[ip + 1];
                break;
            case LOAD:
                int idx = code[++ip];
                stack[++sp] = context.memory[idx];
                ip++;
                break;
            case STORE:
                int sIdx = code[++ip];
                context.memory[sIdx] = stack[sp--];
                ip++;
                break;
            case CALL:
                int fIndex = code[++ip];
                FunctionInfo functionInfo = functionTable[fIndex];
                context = new Context(context, functionInfo, ip + 1);
                int firstArg = sp - functionInfo.argCount + 1;
                //System.out.println("CALL WITH: " + stack[firstArg]);
                for (int i = 0; i < functionInfo.argCount; i++) {
                    context.memory[i] = stack[firstArg + i];
                }
                sp -= functionInfo.argCount;
                ip = functionInfo.address;
                break;
            case RET:
                ip = context.returnAddress;
                context = context.parent;
                break;
            case PRINT:
                System.out.println(stack[sp--]);
                ip++;
                break;
            default:
                throw new UnsupportedOperationException(String.format("INST %s is not supported", inst));
        }
    }
}
