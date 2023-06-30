package OC;

import compiler.Compiler;
import hardware.Cpu;
import hardware.Ram;

public class OS {
    private String filePath;

    // Executes the OS by compiling the source code, loading it into RAM, and executing it on the CPU
    public void execute(String filePath) {
        Compiler compiler = new Compiler(filePath); // Create a compiler object for the given file path
        byte[] machineCode = compiler.toCompiler(); // Compile the source code into machine code
        Ram ram = new Ram(machineCode); // Create a RAM object and load the machine code into it
        Cpu cpu = new Cpu(ram);
        cpu.executeCpu();    // Execute the CPU to run the program
    }
}
