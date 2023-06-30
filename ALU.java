package hardware;

public class ALU {
    private Register register;

    public ALU(Register register) {
        this.register = register;
    }

    byte getValueOperand(int operandType, int index, Ram ram, Register register) {
        if (operandType == 0) {
            return register.getRegisterValue(index);
        } else if (operandType == 2) {
            return ram.getElementStack(index);
        }
        return (byte) index;
    }

    byte getInstructionType(byte cell) {
        return (byte) ((cell & 0xF0) >>> 4);
    }

    byte getOperandOneType(byte cell) {
        return (byte) ((cell & 0x0C) >> 2);
    }

    byte getOperandTwoType(byte cell) {
        return (byte) (cell & 0x03);
    }

    void execute(byte[] cell, Ram ram) {
        byte instruction = getInstructionType(cell[0]);
        byte op1Type = getOperandOneType(cell[0]);
        byte op2Type = getOperandTwoType(cell[0]);

        byte storagePlaceIndex = cell[1];
        byte opOneValue = getValueOperand(op1Type, cell[1], ram, register);
        byte opTwoValue = getValueOperand(op2Type, cell[2], ram, register);

        if (instruction < 7) {
            byte res = switch (instruction) {
                case 0 -> {
                    initDA(opTwoValue);
                    yield opTwoValue;
                }
                case 1 -> add(opOneValue, opTwoValue);
                case 2 -> sub(opOneValue, opTwoValue);
                case 3 -> mul(opOneValue, opTwoValue);
                case 4 -> div(opOneValue, opTwoValue);
                case 5 -> or(opOneValue, opTwoValue);
                case 6 -> not(opOneValue);
                default -> -11;
            };
            if (op1Type == 0) {
                register.setRegisterValue(storagePlaceIndex, res);
            } else if (op1Type == 2) {
                ram.setElementStack(storagePlaceIndex, res);
            }
            register.defaultIncGH();
        } else {
            switch (instruction) {
                case 7: {
                    cmp(opOneValue, opTwoValue);
                    register.defaultIncGH();
                    break;
                }
                case 8:
                    JMP(opOneValue);
                    break;
                case 9:
                    JG(opOneValue);
                    break;
                case 10:
                    JL(opOneValue);
                    break;
                case 11:
                    JE(opOneValue);
                    break;
            }
        }
    }

    private byte sub(byte a, byte b) {
        initDA(a - b);
        return (byte) (a - b);
    }

    private byte add(byte a, byte b) {
        initDA(a + b);
        return (byte) (a + b);
    }

    private byte mul(byte a, byte b) {
        initDA(a * b);
        return (byte) (a * b);
    }

    private byte div(byte a, byte b) {
        if (b == 0) {
            return -1;
        }
        initDA(a / b);
        return (byte) (a / b);
    }

    private byte and(byte a, byte b) {
        initDA(a & b);
        return (byte) (a & b);
    }

    private byte or(byte a, byte b) {
        initDA(a | b);
        return (byte) (a | b);
    }

    private byte not(byte a) {
        initDA(~a);
        return (byte) ~a;
    }

    private void cmp(byte a, byte b) {
        sub(a, b);
    }

    private void JMP(byte index) {
        register.setGH(index);
    }

    private void JG(byte index) {
        if (register.getOF() == register.getSF() && register.getZF() == 0) {
            JMP(index);
        } else {
            register.defaultIncGH();
        }
    }

    private void JL(byte index) {
        if (register.getOF() != register.getSF()) {
            JMP(index);
        } else {
            register.defaultIncGH();
        }
    }

    private void JE(byte index) {
        if (register.getZF() == 1) {
            JMP(index);
        } else {
            register.defaultIncGH();
        }
    }

    private void initDA(int value) {
        if (value == 0) {
            register.setZF((byte) 1);
        } else {
            register.setZF((byte) 0);
        }

        if (value < 0) {
            register.setSF((byte) 1);
        } else {
            register.setSF((byte) 0);
        }

        if (value < -128 || value > 127) {
            register.setOF((byte) 1);
        } else {
            register.setOF((byte) 0);
        }
    }
}
