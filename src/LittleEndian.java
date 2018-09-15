
public abstract class LittleEndian {

	protected final boolean bitArray[];
	
	public long calculateValueSigned() {
		long val = (bitArray[bitArray.length-1]) ? ((long)Math.pow(-2, bitArray.length-1)) : 0;
		for(int pos = 0; pos < bitArray.length-1; pos++) {
			if(bitArray[pos]) {
				val+= ((long)Math.pow(2, pos));
			}
		}
		return val;
	}
	public String calculateValueUnSigned() {
		long val = (bitArray[bitArray.length-1]) ? ((long)Math.pow(2, bitArray.length-1)) : 0;
		for(int pos = 0; pos < bitArray.length-1; pos++) {
			if(bitArray[pos]) {
				val+= ((long)Math.pow(2, pos));
			}
		}
		System.out.println(val);
		return Long.toUnsignedString(val);
	}
	public String generateBitString() {
		String s = "";
		for(int pos = bitArray.length-1; pos >=0; pos--)
			s+= (bitArray[pos]) ? "1" : "0";
		return s;
	}
	public boolean[] generateBitArray() {
		return bitArray;
	}
	public String generateHex(){
		String hex = "";
		for(int pos = bitArray.length - 8; pos >=0; pos-=8) {
			hex += generateHex(bitArray,pos);
		}
		return hex;
	}
	public String generateHexLE(){
		String hex = "";
		for(int pos = 0; pos <= bitArray.length - 8; pos+=8) {
			hex += generateHex(bitArray,pos);
		}
		return hex;
	}
	public String generateBitStringLE(){
		String bitString = "";
		for(int pos = 7; pos <= bitArray.length - 1; pos +=8) {
			for(int x = pos; x >= pos-7; x--) {
				bitString += (bitArray[x]) ? "1" : "0";
			}
		}
		return bitString;
	}
	
	public boolean getSign() {
		return bitArray[bitArray.length-1];
	}
	
	public String toString() {
		String s = this.generateHex();
		return s;
	}
	
	public String displayToString() {
		String s = this.generateHex();
		int pos = 0;
		int firstNonZero = -1;
		while(pos < s.length() && firstNonZero == -1) {
			if(s.charAt(pos) != '0')
				firstNonZero = pos;
			pos++;	
		}
		if(firstNonZero == -1)
			firstNonZero = s.length();
		s = s.substring(firstNonZero);
		return (s.length() == 0) ? "0" : s;
	}
	
	
	public static String LEHexFixer(String hex, int requiredSize) {
		requiredSize = requiredSize/4;
		char c = hex.charAt(hex.length()-2);
		boolean sign = (c >= 'A' && c <= 'F');
		String signExtension = (sign) ? "F" : "0"; 
		int pad = requiredSize - hex.length();
		if(pad %2 == 1) {
			hex = hex.substring(0, hex.length()-1) + signExtension + hex.substring(hex.length()-1);
			pad--;
		}
		for(int i = 0; i < pad; i++) {
			hex = hex + signExtension;
		}
		return hex;
	}
	
	public static String hexFixer(String hex, int requiredSize) {
		requiredSize = requiredSize/4;
		char c = hex.charAt(0);
		boolean sign = (c >= 'A' && c <= 'F');
		String signExtension = (sign) ? "F" : "0"; 
		int pad = requiredSize - hex.length();
		for(int i = 0; i < pad; i++)
				hex = signExtension+hex;
		return hex;
	}
	
	public static String generateHex(boolean[] bitArray, int start)
	{
		int valLowerFour = 0;
		int valUpperFour = 0;
		for(int pos = 0; pos < 4; pos++) {
			if(bitArray[pos+start]) {
				valLowerFour+= ((int)Math.pow(2, pos));
			}
			if(bitArray[pos+start+4]) {
				valUpperFour+= ((int)Math.pow(2, pos));
			}
		}
		String lowerFour = (valLowerFour > 9) ? ((char)(55+valLowerFour))+"" : ""+ valLowerFour;
		String upperFour = (valUpperFour > 9) ? ((char)(55+valUpperFour))+"" : ""+ valUpperFour;
		return upperFour+lowerFour;
	}
	
	public static boolean[] getNibble(char hex) {
		boolean[] nibble = new boolean[4];
		int val = 0;
		if(Character.isLowerCase(hex)) {
			 val = hex - 'a'+10;
		} else if(Character.isUpperCase(hex)) {
			val = hex - 'A' +10;
		} else {
			val = hex - '0';
		}
		for(int pos = 3; pos >= 0; pos--) {
			int pow = ((int)Math.pow(2, pos));
			if(val >= pow) {
				nibble[pos] = true;
				val-=pow;
			}
		}
		return nibble;
		
	}
	
	public static boolean[] getByte(String hex) {
		hex = LittleEndian.hexFixer(hex, 2);
		boolean[] byteArray = new boolean[8];
		char[] nibbles = hex.toCharArray();
		System.arraycopy(getNibble(nibbles[1]), 0, byteArray, 0, 4);
		System.arraycopy(getNibble(nibbles[0]), 0, byteArray, 4, 4);
		return byteArray;
	}
	
	public LittleEndian(int size) {
		bitArray = new boolean[size];
	}
	
	public LittleEndian(boolean[] bitArray) {
		this.bitArray = bitArray.clone();
	}
	
}
