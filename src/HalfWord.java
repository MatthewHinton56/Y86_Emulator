import java.util.Arrays;

public class HalfWord extends LittleEndian{

	public static final int HALFWORDSIZE = 32;
	
	public HalfWord() {
		super(HALFWORDSIZE);
	}
	
	
	public HalfWord(String hex, boolean LE) {
		super(HALFWORDSIZE);
		if(LE)
			hex = LEHexFixer(hex, HALFWORDSIZE);
		else
			hex = hexFixer(hex ,HALFWORDSIZE);
		for(int pos = 0; pos < hex.length(); pos += 2) {
			boolean[] hexBYTE = getByte(hex.substring(pos, pos+2));
			if(LE) {
				System.arraycopy(hexBYTE, 0, bitArray, pos*BYTE.BYTESIZE/2, BYTE.BYTESIZE);
			} else {
				System.arraycopy(hexBYTE, 0, bitArray, BYTE.BYTESIZE - (pos/2+1)*BYTE.BYTESIZE, BYTE.BYTESIZE);
			}
			
		}
	}
	
	public HalfWord(boolean bitArray[]) {
		super(bitArray);
		
	}
	

	
	public Word extendToWord(boolean signed) {
		 String hex = this.generateHex();
		 if(signed) {
			 boolean neg = this.getSign();
			 hex = (neg) ? "FFFF" + hex : "0000" +hex;
			 return new Word(hex, false);
		 } 
		 return new Word("0000" + hex, false);
	}
	
	public DoubleWord extendToDoubleWord(boolean signed) {
		String hex = this.generateHex();
		 if(signed) {
			 boolean neg = this.getSign();
			 hex = (neg) ? "FFFFFFFFFFFF" + hex : "000000000000" +hex;
			 return new DoubleWord(hex, false);
		 } 
		 return new DoubleWord("000000000000" + hex, false);
	}
	// 0 <= pos < 2
	public BYTE getBYTE(int pos) {
		pos = pos*BYTE.BYTESIZE;
		boolean[] bitArray = new boolean[BYTE.BYTESIZE];
		System.arraycopy(this.bitArray, pos, bitArray, 0, BYTE.BYTESIZE);
		return new BYTE(bitArray);
	}
}
