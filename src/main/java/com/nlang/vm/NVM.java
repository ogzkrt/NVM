package com.nlang.vm;

import java.util.HashMap;
import java.util.Map;

import static com.nlang.vm.InstructionSet.*;


public class NVM {

    private final boolean debugMode;
    private final int[] code;
    private final int[] stack;
    private final Map<Integer, String> stingPools;

    private int sp = -1;
    private int ip = 0;

    private Context context;

    private final FunctionInfo[] functionTable;


    public NVM(int[] code, FunctionInfo[] functionTable, boolean debugMode, Map<Integer, String> stringPools) {
        this.debugMode = debugMode;
        this.code = code;
        this.stack = new int[1024];
        this.functionTable = functionTable;
        this.context = new Context(null, functionTable[0], code.length - 1);
        this.stingPools = stringPools;
    }

    public NVM(int[] code, FunctionInfo[] functionTable, Map<Integer, String> stringPool) {
        this(code, functionTable, false, stringPool);

    }

    public NVM(int[] code, FunctionInfo[] functionTable) {
        this(code, functionTable, null);

    }


    public void execute() {
        ip = context.info.address;
        int inst = code[ip];
        while (inst != STOP) {
            if (debugMode) {
                Diagnostic.printInstruction(inst, code, ip);
                execute(inst);
                Diagnostic.printStack(stack, sp);
                Diagnostic.printCallMemory(context);
            } else {
                execute(inst);
            }
            inst = code[ip];
        }
    }

    private void execute(int inst) {
        int first, second;
        int result, address, index;
        float fFirst, fSecond;
        switch (inst) {
            case PUSH:
                push(getOperand());
                ip++;
                break;
            case IADD:
                first = pop();
                second = pop();
                push(second + first);
                ip++;
                break;
            case ISUB:
                first = pop();
                second = pop();
                push(second - first);
                ip++;
                break;
            case IMUL:
                first = pop();
                second = pop();
                push(second * first);
                ip++;
                break;
            case IDIV:
                first = pop();
                second = pop();
                push(second / first);
                ip++;
                break;
            case FADD:
                fFirst = popFloat();
                fSecond = popFloat();
                pushFloat(fSecond + fFirst);
                ip++;
                break;
            case FSUB:
                fFirst = popFloat();
                fSecond = popFloat();
                pushFloat(fSecond - fFirst);
                ip++;
                break;
            case FMUL:
                fFirst = popFloat();
                fSecond = popFloat();
                pushFloat(fSecond * fFirst);
                ip++;
                break;
            case FDIV:
                fFirst = popFloat();
                fSecond = popFloat();
                pushFloat(fSecond / fFirst);
                ip++;
                break;
            case ILT:
                first = pop();
                second = pop();
                push(second < first ? 1 : 0);
                ip++;
                break;
            case IGTE:
                first = pop();
                second = pop();
                push(second >= first ? 1 : 0);
                ip++;
            case JZ:
                address = getOperand();
                result = pop();
                ip = result == 0 ? address : ip + 1;
                break;
            case JNZ:
                address = getOperand();
                result = pop();
                ip = result == 1 ? address : ip + 1;
                break;
            case JMP:
                ip = code[ip + 1];
                break;
            case LOAD:
                index = getOperand();
                push(context.memory[index]);
                ip++;
                break;
            case LDC:
                index = getOperand();
                push(index);
                ip++;
                break;
            case STORE:
                index = getOperand();
                context.memory[index] = pop();
                ip++;
                break;
            case CALL:
                int fIndex = getOperand();
                FunctionInfo functionInfo = functionTable[fIndex];
                context = new Context(context, functionInfo, ip + 1);
                int srcPosition = sp - (functionInfo.argCount - 1);
                if (functionInfo.argCount >= 0)
                    System.arraycopy(stack, srcPosition, context.memory, 0, functionInfo.argCount);
                sp -= functionInfo.argCount;
                ip = functionInfo.address;
                break;
            case RET:
                ip = context.returnAddress;
                context = context.parent;
                break;
            case PRINT:
                System.out.println(pop());
                ip++;
                break;
            case FPRINT:
                System.out.println(popFloat());
                ip++;
                break;
            case SPRINT:
                System.out.println(stingPools.get(pop()));
                ip++;
                break;
            default:
                throw new UnsupportedOperationException(String.format("INST %s is not supported", inst));
        }
    }

    private int getOperand() {
        return code[++ip];
    }

    private int pop() {
        if (sp == -1) {
            Diagnostic.printInstruction(code[ip], code, ip);
            throw new UnsupportedOperationException("Trying to pop from empty stack");
        }
        return stack[sp--];
    }

    private void push(int value) {
        if (sp >= stack.length - 1) {
            Diagnostic.printInstruction(code[ip], code, ip);
            throw new UnsupportedOperationException("Stackoverflow");
        }
        stack[++sp] = value;
    }

    private float popFloat() {
        return Float.intBitsToFloat(pop());
    }

    private void pushFloat(float value) {
        push(Float.floatToIntBits(value));
    }
}
