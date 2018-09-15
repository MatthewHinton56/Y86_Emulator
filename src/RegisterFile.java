import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;


public class RegisterFile extends TreeMap<String, DoubleWord>{

	private static final long serialVersionUID = 1L;



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

	public void set(String key, DoubleWord value) {
		// TODO Auto-generated method stub
			if(this.keySet().contains(key))
				this.put(key, value);
		
	}
	
	public static void main(String[] args) {
		RegisterFile file = new RegisterFile();
		System.out.println(file);
	}
	
	public String toString() {
		String ret = "{";
		for(String key: this.keySet()) {
			ret+= key +" = 0x"+this.get(key)+", ";
		}
		ret = ret.substring(0,ret.length()-2) + "}";
		return ret;
	}

	public void reset() {
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
	
	
}

