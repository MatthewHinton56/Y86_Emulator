import java.util.Arrays;
import java.util.Scanner;

public class ALU {
	//only ALU can set these
	private static boolean ZF, SF, OF;
	
	//a.length ==  b.length
	public static boolean[] AND(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		for(int pos = 0; pos < a.length; pos++)
			c[pos] = a[pos] && b[pos];
		ZF = ALU.Equal(c, new boolean[a.length]);
		SF = c[c.length-1];
		OF = false;
		return c;
	}

	public static boolean[] XOR(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		for(int pos = 0; pos < a.length; pos++)
			c[pos] = a[pos] ^ b[pos];
		ZF = ALU.Equal(c, new boolean[a.length]);
		SF = c[c.length-1];
		OF = false;
		return c;
	}

	public static boolean[] NOT(boolean[] a) {
		boolean[] c = new boolean[a.length];
		for(int pos = 0; pos < a.length; pos++)
			c[pos] = !a[pos];
		return c;
	}

	public static boolean Equal(boolean[] a, boolean[] b) {
		for(int pos = 0; pos < a.length; pos++)
			if(a[pos] != b[pos])
				return false;
		return true;
	}

	public static boolean[] ADD(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		boolean carry = false;
		for(int pos = 0; pos < a.length; pos++) {
			c[pos] = a[pos] ^ b[pos] ^ carry;
			boolean carryTemp = (a[pos] && b[pos]) || (a[pos] && carry) || (b[pos] && carry);
			if(pos == c.length-1) {
				OF = carry ^ carryTemp;
			}
			carry = carryTemp;
		}
		ZF = ALU.Equal(c, new boolean[a.length]);
		SF = c[c.length-1];
		return c;
	}
	
	public static boolean[] IADD(boolean[] a, boolean[] b) {
		boolean[] c = new boolean[a.length];
		boolean carry = false;
		for(int pos = 0; pos < a.length; pos++) {
			c[pos] = a[pos] ^ b[pos] ^ carry;
			boolean carryTemp = (a[pos] && b[pos]) || (a[pos] && carry) || (b[pos] && carry);
			carry = carryTemp;
		}
		return c;
	}
	
	public static boolean[] SUB(boolean[] a, boolean[] b) {
		a = NOT(a);
		a = ADDONE(a);
		boolean[] c = ADD(a,b);
		return c;
	}
	
	public static boolean[] ADDFOUR(boolean[] a) {
		boolean[] b = new boolean[a.length];
		b[2] = true;
		return IADD(a,b);
	}
	
	public static boolean[] INCREMENTEIGHT(boolean[] a) {
		boolean[] b = new boolean[a.length];
		b[3] = true;
		return IADD(a,b);
	}
	
	public static boolean[] DECREMENTEIGHT(boolean[] a) {
		boolean[] b = NEGATIVE_EIGHT;
		return IADD(a,b);
	}
	
	
	public static boolean[] ADDONE(boolean[] a) {
		boolean[] b = new boolean[a.length];
		b[0] = true;
		return IADD(a,b);
	}
	
	public static boolean[] signExtension(boolean[] a, boolean U, int targetSize) {
		boolean[] c = new boolean[targetSize];
		boolean sign = a[a.length-1];
		System.arraycopy(a, 0, c, 0, a.length);
		if(U && !sign) {
			return c;
		}
		for(int pos = a.length; pos < targetSize; pos++)
			c[pos] = true;
		return c;
	}
	
	// Test Bed
	
	//arraySize <= 64
	// -2^(arraySize-1) <= l <= 2^(arraySize-1) -1
	public static boolean[] longToBitArray(long l, int arraySize) {
		long T_MIN = (long) (-1 * Math.pow(2, arraySize-1));
		long T_MAX =  (long) (-1 + Math.pow(2, arraySize-1));
		boolean[] c = new boolean[arraySize];
		if(l == T_MIN) {
			c[c.length-1] = true;
			return c;
		}
		boolean neg = l < 0;
		l = Math.abs(l);
		for(int pos = arraySize-2; pos >= 0; pos--) {
			long val = ((long)Math.pow(2, pos));
			if(val <= l) {
				c[pos] = true;
				l-=val;
			}
		}
		if(neg)
			c = ADDONE(NOT(c));
		return c;
	}
	//arraySize <= 63
		// -2^(arraySize-1) <= l <= 2^(arraySize-1) -1
	public static boolean[] longToBitArrayUnsigned(long l, int arraySize) {
		long T_MAX =  (long) (-1 + Math.pow(2, arraySize));
		boolean[] c = new boolean[arraySize];
		for(int pos = arraySize-1; pos >= 0; pos--) {
			long val = ((long)Math.pow(2, pos));
			if(val <= l) {
				c[pos] = true;
				l-=val;
			}
		}
		return c;
	}
	
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		while(true) {
		System.out.print("valA: ");
		int valA = scan.nextInt();
		System.out.print("valB: ");
		int valB = scan.nextInt();
		boolean[] a = longToBitArray(valA,8);
		System.out.println(Arrays.toString(a));
		boolean[] b = longToBitArray(valB,8);
		System.out.println(Arrays.toString(b));
		boolean[] c = AND(a,b);
		BYTE byt = new BYTE(c);
		System.out.println(Arrays.toString(c));
		System.out.println(byt.generateHex());
		System.out.println(byt.generateBitString());
		System.out.println(byt.calculateValueUnSigned());
		System.out.println("SF:" + SF);
		System.out.println("OF:" + OF);
		System.out.println("ZF:" + ZF);
		}
	}

	public static boolean ZF() {
		return ZF;
	}

	public static boolean SF() {
		return SF;
	}


	public static boolean OF() {
		return OF;
	}
	
	public static boolean[] NEGATIVE_EIGHT = longToBitArray(-8, 64);
	
}
