import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

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
		if(position % 8 != 0) {
			throw new MemoryException(position, "DoubleWord");
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
		
		for(long i = position; i < position + 8; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i-position)).generateHex()));
		
		return true;
	}
	
	public static void storeInstruction(long position, String[] instruction) {
		for(long i = position; i < position + instruction.length; i++)
			memory.put(i, new BYTE(instruction[((int) (i-position))]));
	}
	
	public static TreeMap<Long, DoubleWord> createImage() {
		TreeMap<Long, DoubleWord> image = new TreeMap<Long, DoubleWord>();
		Set<Long> usedAddresses = new HashSet<Long>();
		for(long address: Memory.memory.keySet()) {
			long modifiedAddress = address - address%8;
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
	
}



