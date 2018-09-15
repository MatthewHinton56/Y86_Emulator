import java.util.HashMap;
import java.util.HashSet;

public class Memory {
	public static final HashMap<Long,BYTE> memory = new HashMap<Long,BYTE>();
	
	public static BYTE[] getInstruction(long position) {
		BYTE instructionArray[] = new BYTE[10];
		for(Long i = position; i < position + 10; i++) {
			BYTE instruct = memory.get(i);
			if(instruct == null)
				memory.put(i, BYTE.randomBYTE());
			instructionArray[(int) (i-position)] =  memory.get(i);
		}
		return instructionArray;
	}
	
	public static DoubleWord loadDoubleWord(long position) {
		String immediate = "";
		for(long i = position; i < position + 8; i++) {
			if(memory.get(i).generateHex() == null) {
				memory.put(i, BYTE.randomBYTE());
			}
			immediate += memory.get(i).generateHex();
		}
		
		return new DoubleWord(immediate, true);
	}
	
	public static void storeDoubleWord(long position, DoubleWord val) {
		for(long i = position; i < position + 8; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
	}
	
	public static void storeInstruction(long position, String[] instruction) {
		for(long i = position; i < position + instruction.length; i++)
			memory.put(i, new BYTE(instruction[((int) (i-position))]));
	}
	/*public static final BYTE[] memory = new BYTE[8192];
	public static final HashSet<Integer> memoryInUse = new HashSet<Integer>();
	static {
		for(int pos = 0; pos < memory.length;pos++)
			memory[pos] = new BYTE();
	}

	public static BYTE[] getInstruction(int position) {
		BYTE instructionArray[] = new BYTE[10];
		for(int i = position; i < position + 10; i++)
			instructionArray[i-position] = memory[i];
		return instructionArray;
	}
	
	
	public static DoubleWord loadDoubleWord(int position) {
		String immediate = "";
		for(int i = position; i < position + 8; i++)
			immediate += memory[i].generateHex();
		return new DoubleWord(immediate, true);
	}
	
	public static void storeDoubleWord(int position, DoubleWord val) {
		for(int i = position; i < position + 8; i++)
			memory[i] = new BYTE(val.getBYTE(i-position).generateHex());
	}*/
	
}



