package compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Compiler {
    private int CELL_SIZE = 3;
    private List<String> code;
    private byte[] instructions;
    private int instructionIndex = 0;
    private MapingSection mapping;


    public Compiler(String codePath) {
        this.code = readeCodeOfFile(codePath);
        instructions = new byte[code.size() * 3];
        mapping = new MapingSection();
    }

    private List<String> readeCodeOfFile(String codePath) {
        List<String> lineCode = new ArrayList<>();
        File file = new File(codePath);
        if (!file.canRead())
            throw new IllegalArgumentException("Not found file of file path: " + codePath);
        try (Scanner code = new Scanner(file);) {
            while (code.hasNextLine()) {
                lineCode.add(code.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return lineCode;
    }

    private List<List<String>> tokenising() {
        List<List<String>> splitCode = new ArrayList<>();
        String regex = "[,:\\s]+";
        for (String s : code) {
            splitCode.add(Arrays.asList(s.split(regex)));
        }
        return splitCode;
    }

    public Map<String, Integer> generateLableMap(List<List<String>> lineTokens) {
        Map<String, Integer> labels = new HashMap<>();

        for (int i = 0; i < lineTokens.size(); i++) {
            if (lineTokens.get(i).size() == 4) {
                labels.put(lineTokens.get(i).get(0), i * 3);
                lineTokens.set(i, Arrays.asList(lineTokens.get(i).get(1), lineTokens.get(i).get(2), lineTokens.get(i).get(3)));
            }
        }
        return labels;
    }

    private byte instractionLeftShift(int instruction, int operand1, int operand2) {
        int tmp = instruction << 4;
        tmp |= (operand1 << 2);
        tmp |= operand2;
        return (byte) tmp;
    }

    private int operandDetector(String operator, Map<String, Integer> labels) {
        if (mapping.getRegistrIndex(operator) != null) {
            return 0;
        } else if (labels.get(operator) != null) {
            return 1;
        } else if (operator.charAt(0) == '[') {
            return 2;
        }
        return 3;
    }

    private int operandIndex(String opt, Map<String, Integer> labels) {
        int operandType = operandDetector(opt, labels);
        return switch (operandType) {
            case 0 -> mapping.getRegistrIndex(opt);
            case 1 -> labels.get(opt);
            case 2 -> Integer.parseInt(opt.substring(1, opt.length() - 1));
            case 3 -> Integer.parseInt(opt);
            default -> throw new IllegalStateException("Unexpected value: " + operandType);
        };
    }

    private void instructionToMachineCode(byte[] cell) {
        for (int i = 0; i < 3; i++) {
            instructions[instructionIndex++] = cell[i];
        }
    }

    private byte creatingInstractionCell(List<String> line, Map<String, Integer> labels) {
        int res = 0;
        int typeOpt1;
        int typeOpt2;
        if (line.size() == 3) {
            String s = line.get(0);
            int instructionIndex = mapping.getInstractionIndex(line.get(0).toUpperCase());
            typeOpt1 = operandDetector(line.get(1), labels);
            typeOpt2 = operandDetector(line.get(2), labels);
            res = instractionLeftShift(instructionIndex, typeOpt1, typeOpt2);
        } else if (line.size() == 2) {
            int instructionIndex = mapping.getInstractionIndex(line.get(0).toUpperCase());
            typeOpt1 = operandDetector(line.get(1), labels);
            res = instractionLeftShift(instructionIndex, typeOpt1, 0);
        }
        return (byte) res;
    }

    private void generateMachinCode(Map<String, Integer> labels, List<List<String>> codeLine) {

        String opt;
        for (List<String> line : codeLine) {
            byte[] cell = new byte[3];

            cell[0] = creatingInstractionCell(line, labels);

            opt = line.get(1);
            cell[1] = (byte) operandIndex(opt, labels);

            if (line.size() == 3)
                opt = line.get(2);
            else {
                opt = "0";
            }
            cell[2] = (byte) operandIndex(opt, labels);

            instructionToMachineCode(cell);
        }
    }

    public byte[] toCompiler() {
        List<List<String>> lineTokens = tokenising();
        Map<String, Integer> labels = generateLableMap(lineTokens);
        generateMachinCode(labels, lineTokens);
        return instructions;
    }
}
