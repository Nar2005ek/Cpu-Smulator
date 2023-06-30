package hardware;


public class Register {
    private final byte REG_COUNT = 5;

    private byte registerGH; // EIP (Instruction Pointer)
    private byte registerDA;  // EFLAGS (Flags Register)

    private byte[] registersMemory;  // Array to store register values

    public Register() {
        registersMemory = new byte[REG_COUNT];   // Initialize the register memory array with size REG_COUNT
    }

    // Retrieve the value of the register at the given index
    public byte getRegisterValue(int index) {
        return registersMemory[index];   // Set the value of the register at the given index to the provided value
    }

    public void setRegisterValue(int index, byte value) {
        registersMemory[index] = value;
    }

    public void defaultIncGH() {
        registerGH += 3;  // Increment the value of registerGH by 3 (used for instruction pointer)
    }


    public void setGH(byte numberLine) {
        registerGH = numberLine; // Set the value of registerGH to the provided number (used for instruction pointer)
    }

    public byte getRegisterGH() {
        return registerGH;  // Retrieve the value of registerGH (used as the instruction pointer)
    }

    void setZF(byte value) { // firstIndex of DA register
        // Sets the Zero Flag (ZF) in the DA register
        if (value == 1) {
            registerDA |= 1; // binary 00000001
        } else {
            registerDA &= 0xFE;
        }
    }

    void setSF(byte value) {     // second Index of DA register
        // Sets the Sign Flag (SF) in the DA register
        if (value == 1) {
            registerDA |= 2; // binary 00000010
        } else {
            registerDA &= 0xFD;  //11111101
        }
    }

    void setOF(byte value) {     // third Index of DA register
        // Sets the Overflow Flag (OF) in the DA register
        if (value == 1) {
            registerDA |= 4;// binary 00000100
        } else {
            registerDA &= 0xFB;   //11111011
        }
    }

    public byte getZF() {
        // Retrieves the value of the Zero Flag (ZF) from the DA register
        return (byte) (registerDA & 1);
    }

    public byte getSF() {
        // Retrieves the value of the Sign Flag (SF) from the DA register
        return (byte) ((registerDA >> 1) & 1);
    }

    public byte getOF() {
        // Retrieves the value of the Overflow Flag (OF) from the DA register
        return (byte) ((registerDA >> 2) & 1);
    }


    // Prints the values of the registers
    public void dump_register() {
        for (byte b : registersMemory) {
            System.out.println(b);
        }
    }
}
