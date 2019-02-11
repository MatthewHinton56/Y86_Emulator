package Sequential;
import java.util.ArrayList;
import java.util.TreeMap;

public class RegisterFile extends TreeMap<String, DoubleWord> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a register file, filling entries with defaults
	 */
	public RegisterFile() {
		super.put("%rax", new DoubleWord());
		super.put("%rcx", new DoubleWord());
		super.put("%rdx", new DoubleWord());
		super.put("%rbx", new DoubleWord());
		super.put("%rsp", new DoubleWord());
		super.put("%rbp", new DoubleWord());
		super.put("%rsi", new DoubleWord());
		super.put("%rdi", new DoubleWord());
		super.put("%r8", new DoubleWord());
		super.put("%r9", new DoubleWord());
		super.put("%r10", new DoubleWord());
		super.put("%r11", new DoubleWord());
		super.put("%r12", new DoubleWord());
		super.put("%r13", new DoubleWord());
		super.put("%r14", new DoubleWord());
	}

	/**
	 * Sets a value in the register
	 * 
	 * @param register the register to place in
	 * @param value    the value to update the register with
	 */
	public void set(String register, DoubleWord value) {
		// Only places value if key already exists
		if (this.keySet().contains(register))
			this.put(register, value);

	}

	/**
	 * Gets a toString representation of the register file
	 */
	public String toString() {
		String ret = "{";
		for (String key : this.keySet()) {
			ret += key + " = 0x" + this.get(key) + ", ";
		}
		ret = ret.substring(0, ret.length() - 2) + "}";
		return ret;
	}

	/**
	 * Resets the register file to base values
	 */
	public void reset() {
		for (String key : super.keySet())
			set(key, new DoubleWord());
	}

	/**
	 * Creates an image of the register file by storing every key - value pair
	 * 
	 * @return the image of the register file
	 */
	public TreeMap<String, DoubleWord> createImage() {
		TreeMap<String, DoubleWord> image = new TreeMap<String, DoubleWord>();
		for (String reg : this.keySet()) {
			image.put(reg, this.get(reg));
		}
		return image;
	}

	/**
	 * Gets the difference between two register file images
	 * 
	 * @param registerFileBefore the previous image
	 * @param registerFileAfter  the new image
	 * @return the difference between the two
	 */
	public static ArrayList<String> getDif(TreeMap<String, DoubleWord> registerFileBefore,
			TreeMap<String, DoubleWord> registerFileAfter) {
		ArrayList<String> dif = new ArrayList<String>();
		for (String reg : registerFileBefore.keySet()) {
			if (!registerFileBefore.get(reg).equals(registerFileAfter.get(reg)))
				dif.add(reg);
		}
		return dif;
	}
}
