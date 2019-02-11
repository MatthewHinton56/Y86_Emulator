
public class BYTE extends LittleEndian {
	/**
	 * Byte Constant
	 */
	public static final int BYTESIZE = 8;
	
	/**
	 * Creates a BYTE object using a pre made bit arry
	 * @param bitArray the input bit array
	 */
	public BYTE(boolean[] bitArray) {
		super(bitArray);
	}
	
	/**
	 * Creates a default bit array
	 */
	public BYTE() {
		super(BYTESIZE);
	}
	
	/**
	 * Creates a bit array using an input hex value
	 * @param hex the hex value for the BYTE
	 */
	public BYTE(String hex) {
		super(getByte(hex));
	}
	
	public HalfWord extendToHalfWord(boolean signed) {
		 String hex = this.generateHex();
		 if(signed) {
			 boolean neg = this.getSign();
			 hex = (neg) ? "FF" + hex : "00" +hex;
			 return new HalfWord(hex, false);
		 } 
		 return new Word("00" + hex, false);
	}
	
	public Word extendToWord(boolean signed) {
		 String hex = this.generateHex();
		 if(signed) {
			 boolean neg = this.getSign();
			 hex = (neg) ? "FFFFFF" + hex : "000000" +hex;
			 return new Word(hex, false);
		 } 
		 return new Word("000000" + hex, false);
	}
	
	public DoubleWord extendToDoubleWord(boolean signed) {
		String hex = this.generateHex();
		 if(signed) {
			 boolean neg = this.getSign();
			 hex = (neg) ? "FFFFFFFFFFFFFF" + hex : "00000000000000" +hex;
			 return new DoubleWord(hex, false);
		 } 
		 return new DoubleWord("00000000000000" + hex, false);
	}
	
	
	
	
}
