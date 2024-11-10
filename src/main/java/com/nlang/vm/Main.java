package com.nlang.vm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String s = Files.readString(Path.of("./examples/float.nbyte"));
        BytecodeLexer lexer = new BytecodeLexer();
        lexer.parse(List.of(s.split("\n")));

        int[] bytecode = lexer.getBytecode();
        NVM vm = new NVM(bytecode, lexer.functionTable, false);
        vm.execute();
    }
}
