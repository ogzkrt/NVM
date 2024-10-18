package com.nlang.vm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BytecodeLexer {

    private static final Map<String, Integer> INSTRUCTION_SET = new HashMap<>();

    static {
        INSTRUCTION_SET.put("STOP", 1);
        INSTRUCTION_SET.put("IPUSH", 2);
        INSTRUCTION_SET.put("IADD", 3);
        INSTRUCTION_SET.put("ISUB", 4);
        INSTRUCTION_SET.put("IMUL", 5);
        INSTRUCTION_SET.put("IDIV", 6);
        INSTRUCTION_SET.put("JMP", 7);
        INSTRUCTION_SET.put("JNZ", 8);
        INSTRUCTION_SET.put("JZ", 9);
        INSTRUCTION_SET.put("ILT", 10);
        INSTRUCTION_SET.put("IGE", 11);
        INSTRUCTION_SET.put("PRINT", 12);
        INSTRUCTION_SET.put("LOAD", 13);
        INSTRUCTION_SET.put("STORE", 14);
        INSTRUCTION_SET.put("CALL", 15);
        INSTRUCTION_SET.put("RET", 16);
    }

    private final FunctionInfo[] functionTable = new FunctionInfo[10];
    private final List<Integer> bytecode = new ArrayList<>();
    private final Map<String, Integer> labelAddresses = new HashMap<>();
    private final List<PendingJump> pendingJumps = new ArrayList<>();
    private final List<PendingCall> pendingCalls = new ArrayList<>();
    private int currentAddress = 0;

    private static class PendingJump {
        int addressIndex;
        String label;

        public PendingJump(int addressIndex, String label) {
            this.addressIndex = addressIndex;
            this.label = label;
        }
    }

    private static class PendingCall {
        int addressIndex;
        String functionLabel;

        public PendingCall(int addressIndex, String functionLabel) {
            this.addressIndex = addressIndex;
            this.functionLabel = functionLabel;
        }
    }

    public void parse(List<String> lines) {
        for (String s : lines) {
            String line = s.trim();

            // Skip comments or empty lines
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            line = line.trim();
            if (line.contains("//")) {
                line = line.substring(0, line.indexOf("//")).trim();
            }

            if (line.contains(":") && !line.contains(",")) {
                // It's either a function or a label
                parseFunctionOrLabel(line);
            } else {
                // It's a bytecode instruction
                parseInstruction(line);
            }
        }

        // Resolve any pending jumps to labels and calls to functions
        resolvePendingJumpsAndCalls();
    }

    private void parseFunctionOrLabel(String line) {
        String[] parts = line.split(":");
        if (parts.length >= 3) {
            // It's a function header (e.g., "factorial:1:1:")
            String functionName = parts[0];
            int functionIndex = Integer.parseInt(parts[1]);
            int numArgs = Integer.parseInt(parts[2]);
            functionTable[functionIndex] = new FunctionInfo(functionName, numArgs, currentAddress);
            labelAddresses.put(functionName, currentAddress); // Store function label as a label
        } else if (parts.length == 1) {
            // It's a label (e.g., "recursion:")
            String label = parts[0];
            labelAddresses.put(label, currentAddress); // Record the label's address
        }
    }

    private void parseInstruction(String line) {
        String[] parts = line.split(",\\s*");
        String instruction = parts[0];

        // Translate the instruction to its corresponding opcode
        Integer opcode = INSTRUCTION_SET.get(instruction);
        if (opcode == null) {
            throw new RuntimeException("Unknown instruction: " + instruction);
        }

        bytecode.add(opcode);
        currentAddress++;

        // Handle special cases where an argument is expected
        if (parts.length > 1) {
            String argument = parts[1];

            // If it's a label (for jump instructions), defer its resolution
            if (opcode.equals(INSTRUCTION_SET.get("JZ"))) {
                bytecode.add(0); // Placeholder for the jump target
                pendingJumps.add(new PendingJump(currentAddress - 1, argument));
                currentAddress++;
            } else if (opcode.equals(INSTRUCTION_SET.get("CALL"))) {
                // If the call references a function label, defer its resolution
                if (isLabel(argument)) {
                    bytecode.add(0); // Placeholder for the function address
                    pendingCalls.add(new PendingCall(currentAddress - 1, argument));
                    currentAddress++;
                } else {
                    // Otherwise, it's a numeric function index
                    int arg = Integer.parseInt(argument);
                    bytecode.add(arg);
                    currentAddress++;
                }
            } else {
                // Otherwise, it's a normal numeric argument
                int arg = Integer.parseInt(argument);
                bytecode.add(arg);
                currentAddress++;
            }
        }
    }

    private boolean isLabel(String argument) {
        return !argument.matches("-?\\d+"); // Not a number, assume it's a label
    }

    private void resolvePendingJumpsAndCalls() {
        // Resolve pending jumps
        for (PendingJump jump : pendingJumps) {
            Integer targetAddress = labelAddresses.get(jump.label);
            if (targetAddress == null) {
                throw new RuntimeException("Undefined label: " + jump.label);
            }
            bytecode.set(jump.addressIndex + 1, targetAddress); // Replace the placeholder with the actual address
        }

        // Resolve pending function calls
        for (PendingCall call : pendingCalls) {
            boolean functionFound = false;
            // Look for the function in the functionTable to get its index
            for (int i = 0; i < functionTable.length; i++) {
                FunctionInfo function = functionTable[i];
                if (function != null && function.name.equals(call.functionLabel)) {
                    bytecode.set(call.addressIndex + 1, i); // Replace the placeholder with the function index
                    functionFound = true;
                    break;
                }
            }

            if (!functionFound) {
                throw new RuntimeException("Undefined function label: " + call.functionLabel);
            }
        }
    }


    // Get bytecode as int array
    public int[] getBytecode() {
        return bytecode.stream().mapToInt(i -> i).toArray();
    }

    public static void main(String[] args) throws IOException {

        String s = Files.readString(Path.of("fibonacci.nbyte"));
        BytecodeLexer lexer = new BytecodeLexer();
        lexer.parse(List.of(s.split("\n")));

        int[] bytecode = lexer.getBytecode();
        NVM vm = new NVM(bytecode, lexer.functionTable);
        vm.execute();
    }
}



