import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class Memory {
	//Any negative long is considered secured memory, and the program must have permission to write to it
	public static final HashMap<Long,BYTE> memory = new HashMap<Long,BYTE>();
	public static final HashSet<Long> accessibleMemory = new HashSet<Long>();
	public static final Long RDI_POSITION = 0xA000000000000000L;
	public static final Long RSI_POSITION = 0xB000000000000000L;
	public static BYTE[] getInstruction(long position) {
		if(position < 0 && !accessibleMemory.contains(position)) {
			throw new MemoryException(position);
		}
		BYTE instructionArray[] = new BYTE[10];
		for(Long i = position; i < position + 10; i++) {
			BYTE instruct = memory.get(i);
			if(instruct == null)
				instruct = new BYTE("00");
			instructionArray[(int) (i-position)] =  instruct;
		}
		return instructionArray;
	}
	
	public static DoubleWord loadDoubleWord(long position) {
		String immediate = "";
		if(position % 8 != 0) {
			throw new MemoryException(position, "DoubleWord");
		}
		
		if(position < 0 && !accessibleMemory.contains(position)) {
			throw new MemoryException(position);
		}
		
		for(long i = position; i < position + 8; i++) {
			if(memory.get(i) == null) {
				immediate += "00";
			} else {
				immediate += memory.get(i).generateHex();
			}
		}

		return new DoubleWord(immediate, true);
	}

	public static boolean storeDoubleWord(long position, DoubleWord val) {
		if(position % 8 != 0) {
			throw new MemoryException(position, "DoubleWord");
		}
		
		if(position < 0 && !accessibleMemory.contains(position)) {
			throw new MemoryException(position);
		}
		for(long i = position; i < position + 8; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
		
		return true;
	}
	
	public static void storeInstruction(long position, String[] instruction) {
		if(position < 0 && !accessibleMemory.contains(position)) {
			throw new MemoryException(position);
		}
		for(long i = position; i < position + instruction.length; i++)
			memory.put(i, new BYTE(instruction[((int) (i-position))]));
	}
	
	public static TreeMap<Long, DoubleWord> createImage() {
		TreeMap<Long, DoubleWord> image = new TreeMap<Long, DoubleWord>();
		Set<Long> usedAddresses = new HashSet<Long>();
		for(long address: Memory.memory.keySet()) {
			long modifiedAddress = address - address%8;
			if(address < 0)
				modifiedAddress = address - ((8 + address%8)%8);
			if(!usedAddresses.contains(modifiedAddress)) {
				usedAddresses.add(modifiedAddress);	
				DoubleWord value = Memory.loadDoubleWord(modifiedAddress);
				image.put(modifiedAddress, value);
			}
		}
		return image;
	}
	
	public static ArrayList<Long> getDif(TreeMap<Long, DoubleWord> memoryBefore, TreeMap<Long, DoubleWord> memoryAfter) {
		ArrayList<Long> dif = new ArrayList<Long>();
		for(Long reg: memoryAfter.keySet()) {
			if(!memoryBefore.containsKey(reg) || !memoryAfter.get(reg).equals(memoryBefore.get(reg)))
				dif.add(reg);
		}
		return dif;
	}

	public static void priorityStore(Long position, long length, boolean zeroTerminator) {
		Random r = new Random();
		for(long i = 0; i < length; i++) {
			Memory.accessibleMemory.add(position);
			Long l = r.nextLong();
			if(zeroTerminator && l == 0)
				l += 1;
			DoubleWord dw = new DoubleWord(l);
			Memory.storeDoubleWord(position, dw);
			position += 8;
		}
		if(zeroTerminator) {
			Memory.accessibleMemory.add(position);
			DoubleWord dw = new DoubleWord(0);
			Memory.storeDoubleWord(position, dw);
		}
	}
}



