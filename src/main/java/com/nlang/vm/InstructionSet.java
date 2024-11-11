package com.nlang.vm;

import java.util.HashMap;
import java.util.Map;

public class InstructionSet {

    static final int STOP = 1;
    static final int PUSH = 2;

    static final int IADD = 3;
    static final int ISUB = 4;
    static final int IMUL = 5;
    static final int IDIV = 6;
    static final int FADD = 23;
    static final int FSUB = 24;
    static final int FMUL = 25;
    static final int FDIV = 26;

    static final int JMP = 7;
    static final int JNZ = 8;
    static final int JZ = 9;
    static final int ILT = 10;
    static final int IGTE = 11;

    static final int FLT = 33;
    static final int FGTE = 34;

    static final int PRINT = 12;
    static final int FPRINT = 42;
    static final int SPRINT = 49;

    static final int LOAD = 13;
    static final int STORE = 14;
    static final int ALOAD = 29;
    static final int ASTORE = 30;
    static final int LDC = 44;

    static final int CALL = 15;
    static final int RET = 16;


    static final int COPY = 27;
    static final int NEW_ARRAY = 28;
    static final int IASTORE = 31;
    static final int FASTORE = 32;


    static class InstInfo {
        int code;
        int operandCount;
        String name;

        public InstInfo(int code, int operandCount, String name) {
            this.code = code;
            this.operandCount = operandCount;
            this.name = name;
        }
    }


    public static final Map<String, Integer> STOI = new HashMap<>();
    public static final Map<Integer, InstInfo> INFO_MAP = new HashMap<>();

    static {
        INFO_MAP.put(STOP, new InstInfo(STOP, 0, "STOP"));
        INFO_MAP.put(PUSH,  new InstInfo(PUSH,  1, "PUSH"));

        INFO_MAP.put(IADD, new InstInfo(IADD, 0, "IADD"));
        INFO_MAP.put(ISUB, new InstInfo(ISUB, 0, "ISUB"));
        INFO_MAP.put(IMUL, new InstInfo(IMUL, 0, "IMUL"));
        INFO_MAP.put(IDIV, new InstInfo(IDIV, 0, "IDIV"));

        INFO_MAP.put(FADD, new InstInfo(FADD, 0, "FADD"));
        INFO_MAP.put(FSUB, new InstInfo(FSUB, 0, "FSUB"));
        INFO_MAP.put(FMUL, new InstInfo(FMUL, 0, "FMUL"));
        INFO_MAP.put(FDIV, new InstInfo(FDIV, 0, "FDIV"));

        INFO_MAP.put(JMP, new InstInfo(JMP, 1, "JMP"));
        INFO_MAP.put(JNZ, new InstInfo(JNZ, 1, "JNZ"));
        INFO_MAP.put(JZ, new InstInfo(JZ, 1, "JZ"));
        INFO_MAP.put(ILT, new InstInfo(ILT, 0, "ILT"));
        INFO_MAP.put(IGTE, new InstInfo(IGTE, 0, "IGE"));

        INFO_MAP.put(PRINT, new InstInfo(PRINT, 0, "PRINT"));
        INFO_MAP.put(FPRINT, new InstInfo(FPRINT, 0, "FPRINT"));
        INFO_MAP.put(SPRINT, new InstInfo(SPRINT, 0, "SPRINT"));

        INFO_MAP.put(LOAD, new InstInfo(LOAD, 1, "LOAD"));
        INFO_MAP.put(LDC, new InstInfo(LDC, 1, "LDC"));
        INFO_MAP.put(STORE, new InstInfo(STORE, 1, "STORE"));
        INFO_MAP.put(CALL, new InstInfo(CALL, 1, "CALL"));
        INFO_MAP.put(RET, new InstInfo(RET, 0, "RET"));

    }

    static {
        STOI.put("STOP", STOP);
        STOI.put("PUSH", PUSH);

        STOI.put("IADD", IADD);
        STOI.put("ISUB", ISUB);
        STOI.put("IMUL", IMUL);
        STOI.put("IDIV", IDIV);

        STOI.put("FADD", FADD);
        STOI.put("FSUB", FSUB);
        STOI.put("FMUL", FMUL);
        STOI.put("FDIV", FDIV);

        STOI.put("JMP", JMP);
        STOI.put("JNZ", JNZ);
        STOI.put("JZ", JZ);
        STOI.put("ILT", ILT);
        STOI.put("IGE", IGTE);

        STOI.put("PRINT", PRINT);
        STOI.put("FPRINT", FPRINT);
        STOI.put("SPRINT", SPRINT);

        STOI.put("LOAD", LOAD);
        STOI.put("LDC", LDC);
        STOI.put("STORE", STORE);


        STOI.put("CALL", CALL);
        STOI.put("RET", RET);
    }

}
