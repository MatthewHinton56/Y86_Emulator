
public class Word extends LittleEndian{
	
	public static final int WORDSIZE = 32;
	
	public Word() {
		super(WORDSIZE);
	}
	
	public Word(String hex, boolean LE) {
		super(WORDSIZE);
		if(LE)
			hex = LEHexFixer(hex, WORDSIZE);
		else
			hex = hexFixer(hex, WORDSIZE);
		for(int pos = 0; pos < hex.length(); pos += 2) {
			boolean[] hexBYTE = getByte(hex.substring(pos, pos+2));
			if(LE) {
				System.arraycopy(hexBYTE, 0, bitArray, pos*BYTE.BYTESIZE/2, BYTE.BYTESIZE);
			} else {
				System.arraycopy(hexBYTE, 0, bitArray, BYTE.BYTESIZE - (pos/2+1)*BYTE.BYTESIZE, BYTE.BYTESIZE);
			}
			
		}
	}
	
	public Word(boolean bitArray[]) {
		super(bitArray);
	}
	
	public DoubleWord extendToDoubleWord(boolean signed) {
		String hex = this.generateHex();
		 if(signed) {
			 boolean neg = this.getSign();
			 hex = (neg) ? "FFFFFFFF" + hex : "00000000" +hex;
			 return new DoubleWord(hex, false);
		 } 
		 return new DoubleWord("00000000" + hex, false);
	}
	// 0 <= pos < 2
	public BYTE getBYTE(int pos) {
		pos = pos*BYTE.BYTESIZE;
		boolean[] bitArray = new boolean[BYTE.BYTESIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, BYTE.BYTESIZE);
		return new BYTE(bitArray);
	}
	
	public HalfWord getHalfWord(int pos) {
		pos = pos*HalfWord.HALFWORDSIZE;
		boolean[] bitArray = new boolean[HalfWord.HALFWORDSIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, HalfWord.HALFWORDSIZE);
		return new HalfWord(bitArray);
	}

}
