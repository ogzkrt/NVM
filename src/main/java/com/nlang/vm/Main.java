package com.nlang.vm;

import static com.nlang.vm.InstructionSet.*;

public class Main {
    public static void main(String[] args) {

        int[] code = {
                // if (n<2) return n;
                LOAD, 0,        //0
                IPUSH, 2,       //2
                ILT,            //4
                JZ, 10,         //5
                LOAD, 0,       //7
                RET,            //9
                // return fib(n-1) + fib(n-2);
                LOAD, 0,        //10
                IPUSH,1,
                ISUB,
                CALL,1,
                LOAD, 0,        //12
                IPUSH, 2,       //14
                ISUB,           //16
                CALL, 1,        //17
                IADD,           //19
                RET,            //20

                IPUSH, 23,       //21 MAIN
                CALL, 1,        //23 CALL <Function_Index>
                PRINT,          //25
                STOP,           //26
        };


        FunctionInfo[] functionTable = {
                new FunctionInfo("main", 0, code.length - 6),
                new FunctionInfo("fibonacci", 1, 0)
        };
        NVM vm = new NVM(code, functionTable);
        vm.execute();

    }
}