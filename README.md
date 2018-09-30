# Guide for the Y86 Emulator

## Starting Up
To start up, you must download either the jar or the exe for windows, the jar is runnable and will run with a JRE installed
On boot up of the application, either a file must be created or loaded in, generating a YAS page.

## YAS
The YAS page serves as an IDE to develop Y86 code, and provides a compiler to generate the executable for the emulator to run. The compile button runs the compiler and displays the compiler output in the output window. If an error does occur, the compiler displays the line and reason for the error.

### Instruction tab
The instruction tab provides the list of all Y86 instructions, and the necessary parameters to run the instruction. All parameters must be replaced with proper inputs to compile. 

### Compilation 
At completion of compilation, a runnable state is generated, and the YIS tab is created 

## YIS
The YIS tab serves as the main interface to the emulator, allowing the user to preform multiple tasks in relation to the machine, even some that occur pre start up

### Data Display 
Data Display allows the configuration of the emulator to display double words in four different forms as listed below:

| Option | Description |
|--------|-------------|
| Hex    | Displays the DoubleWords in Hex format |
| Hex LE | Displays the DoubleWord as displayed in memory using little endian format
| Signed | Displays the DoubleWords as their signed value |
| Unsigned | Displays the DoubleWords as their unsigned value |

### Parameters
Allows for the designation of input parameters to be placed in the input registers RDI, RSI, RDX, RCX. RDI and RSI both can be supplied with arrays of size 0 through 30 of random values, with an option to store their lengths in RDX and RCX respectively. If the length is not stored, the arrays are 0 terminated. 

Example: 

RDI --> | Elem1 | Elem2 | Elem3 |

RDX --> 3

### Instruction and Binary Display
The top left corner displays the instructions as aligned in memory, as well as a pointer to the current instruction. It also displays the Hexidecimal form of the instruction

### Initialize 
The initialize buttons sets the processor to be ready to begin executing instructions as dictated by the binary file, it aligns instructions and constants in memory, as well as initializing the parameter data in the critical section of memory

#### Critical Section
The critical is any address that has a one in the 63 bit of their address. You can only write or read from this section if given permission, currently only available through parameters.

### Register and Status Display
The display to the right displays the values of each of the 15 registers, as well as the status of the processor, and the condition flags
Information on the specific registers and statues, along with the condition codes can be found in either the book or slides

### Memory display module
The memory display module presents all the DoubleWords stored in memory for the user to examine.

### Processor Output
Displays the output of every initialization, step, and run command, allowing tracing of the various processor states

### Step
Step causes the next instruction the program counter points to to execute

### Run
Runs the program until either a halt is encountered, or an error is generated

## Binaries 

| System | Option 1 | Option 2 | 
| -------|----------|----------|
| Windows | [exe](https://github.com/MatthewHinton56/Y86_Emulator/releases/download/1.0/Y86_Emulator.exe "Y86_Emulator.exe") | [jar](https://github.com/MatthewHinton56/Y86_Emulator/releases/download/1.0/Y86_Emulator.jar "Y86_Emulator.jar") |
| Mac | [jar](https://github.com/MatthewHinton56/Y86_Emulator/releases/download/1.0/Y86_Emulator.jar "Y86_Emulator.jar") | 
| Linux | [jar](https://github.com/MatthewHinton56/Y86_Emulator/releases/download/1.0/Y86_Emulator.jar "Y86_Emulator.jar") | 
