package utility;

import java.util.List;

public class VByte {
	boolean vByte = false;
	
	public VByte(boolean vb) {
		vByte = vb;
	}
	
	public void changeEncode(boolean b) {
		vByte = b;
	}
	
	public boolean isEncode() {
		return vByte;
	}
	
	public void encoder(List<Byte> bf, int x) {
		while(x>127) {
			bf.add((byte)(x&0x7F));
			x>>>=7;
		}
		bf.add((byte)(x | 0x80));
	}
	
	public int decoder(int[] i, byte[] input) {
		if(vByte) return vByteDecoder(i, input);
		return NormalDecoder(i, input);
	}
	
	public int vByteDecoder(int[] i, byte[] input) {
		int pos = 0, result = ((int)input[i[0]]&0x7F);
		while((input[i[0]]&0x80)==0) {
			i[0]++; pos++;
			result |= (((int)input[i[0]]&0x7F)<<(7*pos));
		}
		i[0]++;
		return result;
	}
	
	public int NormalDecoder(int[] i, byte[] input) {
		int result = 0;
		for(int j=0; j<4; j++) {
			//result |= ((input[i[0]]& 0xFF)<<((3-j)*8));
			result<<=8;
			result |= (input[i[0]]& 0xFF);
			i[0]++;
		}
		return result;
	}
}

