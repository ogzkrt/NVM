package com.nlang.vm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.nlang.vm.InstructionSet.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NVMTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private NVM vm;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errStream));
    }

    @Test
    void testSum() {

        int[] code = {
                PUSH,  242,
                PUSH,  22,
                IADD,
                PRINT,
                STOP,

        };
        FunctionInfo[] functionTable = {new FunctionInfo("main", 0, 0)};
        vm = new NVM(code, functionTable);
        vm.execute();
        assertEquals("264" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testAverageFunction() {

        int[] code = {
                // avg function
                LOAD, 0,        //0
                LOAD, 1,        //2
                IADD,           //4
                PUSH,  2,       //5
                IDIV,           //7
                RET,            //8

                PUSH,  150,     //9
                PUSH,  44,      //11
                CALL, 1,        //13  CALL <Function_Index>
                PRINT,          //15
                STOP,           //16
        };


        FunctionInfo[] functionTable = {
                new FunctionInfo("main", 0, 9),
                new FunctionInfo("avg", 2, 0)
        };
        vm = new NVM(code, functionTable);
        vm.execute();
        assertEquals("97" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testFactorialRecursiveFunction() {

        int[] code = {
                // if (n<2) return 1;
                LOAD, 0,        //0
                PUSH,  2,       //2
                ILT,            //4
                JZ, 10,         //5
                PUSH,  1,       //7
                RET,            //9
                // return n * fact(n-1);
                LOAD, 0,        //10
                LOAD, 0,        //12
                PUSH,  1,       //14
                ISUB,           //16
                CALL, 1,        //17
                IMUL,           //19
                RET,            //20

                PUSH,  4,       //21 MAIN
                CALL, 1,        //23 CALL <Function_Index>
                PRINT,          //25
                STOP,           //26
        };


        FunctionInfo[] functionTable = {
                new FunctionInfo("main", 0, code.length - 6),
                new FunctionInfo("factorial", 1, 0)
        };
        vm = new NVM(code, functionTable);
        vm.execute();
        assertEquals("24" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testFibonacciRecursiveFunction() {

        int[] code = {
                // if (n<2) return n;
                LOAD, 0,        //0
                PUSH,  2,       //2
                ILT,            //4
                JZ, 10,         //5
                LOAD, 0,        //7
                RET,            //9
                // return fib(n-1) + fib(n-2);
                LOAD, 0,        //10
                PUSH,  1,
                ISUB,
                CALL, 1,
                LOAD, 0,
                PUSH,  2,
                ISUB,
                CALL, 1,
                IADD,
                RET,

                PUSH,  23,      // MAIN
                CALL, 1,        // CALL <Function_Index>
                PRINT,
                STOP,
        };


        FunctionInfo[] functionTable = {
                new FunctionInfo("main", 0, code.length - 6),
                new FunctionInfo("fibonacci", 1, 0)
        };
        vm = new NVM(code, functionTable);
        vm.execute();
        assertEquals("28657" + System.lineSeparator(), outputStream.toString());
    }


    @Test
    void readFromFileFibonacciRecursive() throws IOException {
        executeFromFile("./examples/fibonacci_recursive.nbyte", "28657");
    }

    @Test
    void readFromFileFactorialRecursive() throws IOException {
        executeFromFile("./examples/factorial_recursive.nbyte", "120");
    }

    @Test
    void readFromFileFactorialLoop() throws IOException {
        executeFromFile("./examples/factorial_loop.nbyte", "120");
    }

    @Test
    void readFromFileAverage() throws IOException {
        executeFromFile("./examples/average.nbyte", "8");
    }

    @Test
    void readFromFileFloat() throws IOException {
        executeFromFile("./examples/float.nbyte", "7.5360007");
    }

    @Test
    void readFromFileStringPrint() throws IOException {
        executeFromFile("./examples/hello.nbyte", "Hello, World");
    }


    private void executeFromFile(String filePath, String expected) throws IOException {
        String s = Files.readString(Path.of(filePath));
        BytecodeLexer lexer = new BytecodeLexer();
        lexer.parseCode(s);

        int[] bytecode = lexer.getBytecode();
        NVM vm = new NVM(bytecode, lexer.functionTable, lexer.getStringPool());
        vm.execute();
        assertEquals(expected + System.lineSeparator(), outputStream.toString());
    }

}