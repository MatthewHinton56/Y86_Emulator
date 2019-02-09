
public class BYTE extends LittleEndian {

	public static final int BYTESIZE = 8;
	
	public BYTE(boolean[] bitArray) {
		super(bitArray);
	}
	
	public BYTE() {
		super(BYTESIZE);
	}
	
	public BYTE(String hex) {
		super(getByte(hex));
	}
	
}
