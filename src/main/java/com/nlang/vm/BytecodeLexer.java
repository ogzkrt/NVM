package com.nlang.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nlang.vm.InstructionSet.STOI;

class BytecodeLexer {


    final FunctionInfo[] functionTable = new FunctionInfo[10];
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

            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            line = line.trim();
            if (line.contains("//")) {
                line = line.substring(0, line.indexOf("//")).trim();
            }

            if (line.contains(":") && !line.contains(",")) {
                parseFunctionOrLabel(line);
            } else {
                parseInstruction(line);
            }
        }
        resolvePendingJumpsAndCalls();
    }

    private void parseFunctionOrLabel(String line) {
        String[] parts = line.split(":");
        if (parts.length >= 3) {
            String functionName = parts[0];
            int functionIndex = Integer.parseInt(parts[1]);
            int numArgs = Integer.parseInt(parts[2]);
            functionTable[functionIndex] = new FunctionInfo(functionName, numArgs, currentAddress);
            labelAddresses.put(functionName, currentAddress); // Store function label as a label
        } else if (parts.length == 1) {
            String label = parts[0];
            labelAddresses.put(label, currentAddress); // Record the label's address
        }
    }

    private void parseInstruction(String line) {
        String[] parts = line.split(",\\s*");
        String instruction = parts[0];

        Integer opcode = STOI.get(instruction);
        if (opcode == null) {
            throw new RuntimeException("Unknown instruction: " + instruction);
        }

        bytecode.add(opcode);
        currentAddress++;

        if (parts.length > 1) {
            String argument = parts[1];

            if (opcode.equals(STOI.get("JZ")) || opcode.equals(STOI.get("JMP"))) {
                bytecode.add(0);
                pendingJumps.add(new PendingJump(currentAddress - 1, argument));
                currentAddress++;
            } else if (opcode.equals(STOI.get("CALL"))) {

                if (isLabel(argument)) {
                    bytecode.add(0);
                    pendingCalls.add(new PendingCall(currentAddress - 1, argument));
                } else {

                    int arg = Integer.parseInt(argument);
                    bytecode.add(arg);
                }
                currentAddress++;
            } else {
                if (isInteger(argument)) {
                    bytecode.add(Integer.parseInt(argument));
                } else if (isFloat(argument)) {
                    bytecode.add(Float.floatToIntBits(Float.parseFloat(argument)));
                }
                currentAddress++;
            }
        }
    }

    private boolean isFloat(String arg) {
        try {
            Float.parseFloat(arg);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isInteger(String arg) {
        try {
            Integer.parseInt(arg);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isLabel(String argument) {
        return !argument.matches("-?\\d+");
    }

    private void resolvePendingJumpsAndCalls() {
        for (PendingJump jump : pendingJumps) {
            Integer targetAddress = labelAddresses.get(jump.label);
            if (targetAddress == null) {
                throw new RuntimeException("Undefined label: " + jump.label);
            }
            bytecode.set(jump.addressIndex + 1, targetAddress);
        }

        for (PendingCall call : pendingCalls) {
            boolean functionFound = false;

            for (int i = 0; i < functionTable.length; i++) {
                FunctionInfo function = functionTable[i];
                if (function != null && function.name.equals(call.functionLabel)) {
                    bytecode.set(call.addressIndex + 1, i);
                    functionFound = true;
                    break;
                }
            }

            if (!functionFound) {
                throw new RuntimeException("Undefined function label: " + call.functionLabel);
            }
        }
    }

    public int[] getBytecode() {
        return bytecode.stream().mapToInt(i -> i).toArray();
    }

}



