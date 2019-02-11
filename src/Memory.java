import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class Memory {
	// Any negative long is considered secured memory, and the program must have
	// permission to write to it
	public static final TreeMap<Long, BYTE> memory = new TreeMap<Long, BYTE>(new Comparator<Long>() {

		@Override
		public int compare(Long arg0, Long arg1) {
			return Long.compareUnsigned(arg0, arg1);
		}

	});

	public static final HashSet<Long> accessibleMemory = new HashSet<Long>();
	public static final Long RDI_POSITION = 0xA000000000000000L;
	public static final Long RSI_POSITION = 0xB000000000000000L;

	/**
	 * Gets the instruction in memory at position
	 * 
	 * @param position the position to read from
	 * @return the BYTE array representation of the instruction
	 */
	public static BYTE[] getInstruction(long position) {
		if (position < 0 && !accessibleMemory.contains(position)) {
			throw new MemoryException(position);
		}
		BYTE instructionArray[] = new BYTE[10];
		for (Long i = position; i < position + 10; i++) {
			BYTE instruct = memory.get(i);
			if (instruct == null)
				instruct = new BYTE("00");
			instructionArray[(int) (i - position)] = instruct;
		}
		return instructionArray;
	}

	/**
	 * Loads a double word from memory, checking if it is a valid position to read
	 * from
	 * 
	 * @param position the position to read from
	 * @return the DoubleWord that will be read
	 */
	public static DoubleWord loadDoubleWord(long position) {
		String immediate = "";
		if (position % 8 != 0) {
			throw new MemoryException(position, "DoubleWord");
		}

		if (position < 0 && !accessibleMemory.contains(position)) {
			throw new MemoryException(position);
		}

		for (long i = position; i < position + 8; i++) {
			if (memory.get(i) == null) {
				immediate += "00";
			} else {
				immediate += memory.get(i).generateHex();
			}
		}

		return new DoubleWord(immediate, true);
	}

	/**
	 * Stores a double word in memory at position
	 * 
	 * @param position
	 * @param val      the val to store
	 * @return true if it stores properly
	 */
	public static void storeDoubleWord(long position, DoubleWord val) {
		if (position % 8 != 0) {
			throw new MemoryException(position, "DoubleWord");
		}

		if (position < 0 && !accessibleMemory.contains(position)) {
			throw new MemoryException(position);
		}
		for (long i = position; i < position + 8; i++)
			memory.put(i, new BYTE(val.getBYTE((int) (i - position)).generateHex()));
	}

	/**
	 * Stores an instruction at position
	 * 
	 * @param position    the position to store at
	 * @param instruction the instruction to store in memory
	 */
	public static void storeInstruction(long position, String[] instruction) {
		if (position < 0 && !accessibleMemory.contains(position)) {
			throw new MemoryException(position);
		}
		for (long i = position; i < position + instruction.length; i++)
			memory.put(i, new BYTE(instruction[((int) (i - position))]));
	}

	/**
	 * Creates an image of memory
	 * 
	 * @return the image of memory
	 */
	public static TreeMap<Long, DoubleWord> createImage() {
		TreeMap<Long, DoubleWord> image = new TreeMap<Long, DoubleWord>();
		Set<Long> usedAddresses = new HashSet<Long>();
		for (long address : Memory.memory.keySet()) {
			long modifiedAddress = address - address % 8;
			if (address < 0)
				modifiedAddress = address - ((8 + address % 8) % 8);
			if (!usedAddresses.contains(modifiedAddress)) {
				usedAddresses.add(modifiedAddress);
				DoubleWord value = Memory.loadDoubleWord(modifiedAddress);
				image.put(modifiedAddress, value);
			}
		}
		return image;
	}

	/**
	 * Gets the difference of two images of memory
	 * 
	 * @param memoryBefore the previous memory
	 * @param memoryAfter  the memory after
	 * @return the difference in the two memories
	 */
	public static ArrayList<Long> getDif(TreeMap<Long, DoubleWord> memoryBefore,
			TreeMap<Long, DoubleWord> memoryAfter) {
		ArrayList<Long> dif = new ArrayList<Long>();
		for (Long reg : memoryAfter.keySet()) {
			if (!memoryBefore.containsKey(reg) || !memoryAfter.get(reg).equals(memoryBefore.get(reg)))
				dif.add(reg);
		}
		return dif;
	}

	/**
	 * Priority store used by internal processor methods to store in critical memory
	 * 
	 * @param position       the position to store at
	 * @param length         the length of the array to store
	 * @param zeroTerminator if a zero terminator must be added after
	 */
	public static void priorityStore(Long position, long length, boolean zeroTerminator) {
		Random r = new Random();
		for (long i = 0; i < length; i++) {
			Memory.accessibleMemory.add(position);
			Long l = r.nextLong();
			if (zeroTerminator && l == 0)
				l += 1;
			DoubleWord dw = new DoubleWord(l);
			Memory.storeDoubleWord(position, dw);
			position += 8;
		}
		if (zeroTerminator) {
			Memory.accessibleMemory.add(position);
			DoubleWord dw = new DoubleWord(0);
			Memory.storeDoubleWord(position, dw);
		}
	}
}
