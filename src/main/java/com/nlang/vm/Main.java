package com.nlang.vm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) throws IOException {

        String s = Files.readString(Paths.get("simple.nbyte"));
        System.out.println(s);

    }
}