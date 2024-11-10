package com.nlang.vm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        String s = Files.readString(Path.of("./examples/hello.nbyte"));
        BytecodeLexer lexer = new BytecodeLexer();
        lexer.parseCode(s);

        int[] bytecode = lexer.getBytecode();
        NVM vm = new NVM(bytecode, lexer.functionTable, false, lexer.getStringPool());
        vm.execute();
    }
}
