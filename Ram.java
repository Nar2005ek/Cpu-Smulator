package hardware;

import java.util.Arrays;

public class Ram {
    private final byte[] main_memory = new byte[96];
    private final int P1_SIZE;
    private int P1_endPtr;

    public Ram(byte[] machineCode) {
        loadMachineCode(machineCode);
        P1_SIZE = machineCode.length;
        P1_endPtr = P1_SIZE;
    }

    public int getP1_SIZE() {
        return P1_SIZE;
    }

    public byte getElementStack(int index) {
        return main_memory[index + P1_SIZE];
    }

    public void setElementStack(int index, byte value) {
        main_memory[index + P1_SIZE] = value;
        if (index + P1_SIZE > P1_endPtr) {
            P1_endPtr = index + P1_SIZE;
        }
    }

    public byte[] getMain_memory() {
        return main_memory.clone();
    }

    private void loadMachineCode(byte[] array) {
        System.arraycopy(array, 0, main_memory, 0, array.length);
    }

    public byte[] getInstructionMachineCode(int indexGH) {
        int startIndex = indexGH;
        int endIndex = startIndex + 3;
        return Arrays.copyOfRange(main_memory, startIndex, endIndex);
    }

    public void dump_memory() {
        for (int i = P1_SIZE; i < P1_endPtr + 1; i++) {
            System.out.println(main_memory[i]);
        }
    }
}
