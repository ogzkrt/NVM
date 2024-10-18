package com.nlang.vm;

import java.util.Arrays;

import static com.nlang.vm.InstructionSet.*;

public class Diagnostic {

    public static void printCallMemory(Context context) {
        String format = String.format("Function name: %s Memory: %s", context.info.name, Arrays.toString(context.memory));
        System.out.println(format);
    }

    public static void printInstruction(int inst, int[] code, int ip) {
        InstInfo instInfo = INFO_MAP.get(inst);
        String name = instInfo.name;
        String format;
        if (instInfo.operandCount > 0) {
            format = String.format("%s %d", name, code[ip + 1]);

        } else {
            format = String.format("%s", name);
        }
        System.out.println(format);
    }

    public static void printStack(int[] stack, int sp) {
        StringBuilder sb = new StringBuilder();
        sb.append("Stack: [");
        for (int i = 0; i <= sp; i++) {
            if (i == sp) {
                sb.append(stack[i]);
            } else {
                sb.append(stack[i]);
                sb.append(",");
            }
        }
        sb.append("]");
        System.out.println(sb);
    }
}
