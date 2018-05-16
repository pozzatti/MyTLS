package mytlsimp.cipher.symmetric;

import java.util.Arrays;

import mytlsimp.util.BitOperator;

public class DES extends Cipher{
	protected static final int DES_BLOCK_SIZE = 8;
	private static final int EXPANSION_BLOCK_SIZE = 6;
	private static final int PC1_KEY_SIZE = 7;
	private static final int SUBKEY_SIZE = 6;
	
	int[] ipTable = {58, 50, 42, 34, 26, 18, 10, 2,
			  60, 52, 44, 36, 28, 20, 12, 4,
			  62, 54, 46, 38, 30, 22, 14, 6,
			  64, 56, 48, 40, 32, 24, 16, 8,
			  57, 49, 41, 33, 25, 17, 9,  1,
			  59, 51, 43, 35, 27, 19, 11, 3,
			  61, 53, 45, 37, 29, 21, 13, 5,
			  63, 55, 47, 39, 31, 23, 15, 7 };
	
	int[] fpTable = { 40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41,  9, 49, 17, 57, 25 };
	

	private int [] pc1Table = { 57, 49, 41, 33, 25, 17,  9, 1,
            58, 50, 42, 34, 26, 18, 10, 2,
            59, 51, 43, 35, 27, 19, 11, 3,
            60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15, 7,
            62, 54, 46, 38, 30, 22, 14, 6,
            61, 53, 45, 37, 29, 21, 13, 5,
            28, 20, 12,  4 };
	
	private int[] pc2Table = { 14, 17, 11, 24,  1,  5,
            3, 28, 15,  6, 21, 10,
           23, 19, 12,  4, 26,  8,
           16,  7, 27, 20, 13,  2,
           41, 52, 31, 37, 47, 55,
           30, 40, 51, 45, 33, 48,
           44, 49, 39, 56, 34, 53,
           46, 42, 50, 36, 29, 32 };
	
	private int[] expansionTable = {
		     32,  1,  2,  3,  4,  5,
		      4,  5,  6,  7,  8,  9,
		      8,  9, 10, 11, 12, 13,
		      12, 13, 14, 15, 16, 17,
		      16, 17, 18, 19, 20, 21,
		      20, 21, 22, 23, 24, 25,
		      24, 25, 26, 27, 28, 29,
		      28, 29, 30, 31, 32,  1};
	
	private int[][] sbox = {
			{ 14, 0, 4, 15, 13, 7, 1, 4, 2, 14, 15, 2, 11, 13, 8, 1,
			3, 10, 10, 6, 6, 12, 12, 11, 5, 9, 9, 5, 0, 3, 7, 8,
			4, 15, 1, 12, 14, 8, 8, 2, 13, 4, 6, 9, 2, 1, 11, 7,
			15, 5, 12, 11, 9, 3, 7, 14, 3, 10, 10, 0, 5, 6, 0, 13 },
			{ 15, 3, 1, 13, 8, 4, 14, 7, 6, 15, 11, 2, 3, 8, 4, 14,
			9, 12, 7, 0, 2, 1, 13, 10, 12, 6, 0, 9, 5, 11, 10, 5,
			0, 13, 14, 8, 7, 10, 11, 1, 10, 3, 4, 15, 13, 4, 1, 2,
			5, 11, 8, 6, 12, 7, 6, 12, 9, 0, 3, 5, 2, 14, 15, 9 },
			{ 10, 13, 0, 7, 9, 0, 14, 9, 6, 3, 3, 4, 15, 6, 5, 10,
			1, 2, 13, 8, 12, 5, 7, 14, 11, 12, 4, 11, 2, 15, 8, 1,
			13, 1, 6, 10, 4, 13, 9, 0, 8, 6, 15, 9, 3, 8, 0, 7,
			11, 4, 1, 15, 2, 14, 12, 3, 5, 11, 10, 5, 14, 2, 7, 12 },
			{ 7, 13, 13, 8, 14, 11, 3, 5, 0, 6, 6, 15, 9, 0, 10, 3,
			1, 4, 2, 7, 8, 2, 5, 12, 11, 1, 12, 10, 4, 14, 15, 9,
			10, 3, 6, 15, 9, 0, 0, 6, 12, 10, 11, 1, 7, 13, 13, 8,
			15, 9, 1, 4, 3, 5, 14, 11, 5, 12, 2, 7, 8, 2, 4, 14 },
			{ 2, 14, 12, 11, 4, 2, 1, 12, 7, 4, 10, 7, 11, 13, 6, 1,
			8, 5, 5, 0, 3, 15, 15, 10, 13, 3, 0, 9, 14, 8, 9, 6,
			4, 11, 2, 8, 1, 12, 11, 7, 10, 1, 13, 14, 7, 2, 8, 13,
			15, 6, 9, 15, 12, 0, 5, 9, 6, 10, 3, 4, 0, 5, 14, 3 },
			{ 12, 10, 1, 15, 10, 4, 15, 2, 9, 7, 2, 12, 6, 9, 8, 5,
			0, 6, 13, 1, 3, 13, 4, 14, 14, 0, 7, 11, 5, 3, 11, 8,
			9, 4, 14, 3, 15, 2, 5, 12, 2, 9, 8, 5, 12, 15, 3, 10,
			7, 11, 0, 14, 4, 1, 10, 7, 1, 6, 13, 0, 11, 8, 6, 13 },
			{ 4, 13, 11, 0, 2, 11, 14, 7, 15, 4, 0, 9, 8, 1, 13, 10,
			3, 14, 12, 3, 9, 5, 7, 12, 5, 2, 10, 15, 6, 8, 1, 6,
			1, 6, 4, 11, 11, 13, 13, 8, 12, 1, 3, 4, 7, 10, 14, 7,
			10, 9, 15, 5, 6, 0, 8, 15, 0, 14, 5, 2, 9, 3, 2, 12 },
			{ 13, 1, 2, 15, 8, 13, 4, 8, 6, 10, 15, 3, 11, 7, 1, 4,
			10, 12, 9, 5, 3, 6, 14, 11, 5, 0, 0, 14, 12, 9, 7, 2,
			7, 2, 11, 1, 4, 14, 1, 7, 9, 4, 12, 10, 14, 8, 2, 13,
			0, 15, 6, 12, 10, 9, 13, 0, 15, 3, 3, 5, 5, 6, 8, 11 }
	};
	
	private int[] pTable =  { 
			16,  7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2,  8, 24, 14,
            32, 27,  3,  9,
            19, 13, 30,  6,
            22, 11,  4, 25 };
	
	protected byte[] desBlockOperate(byte[] plainText, byte[] key, boolean encrypt){	
		byte output[] = new byte[DES_BLOCK_SIZE];
		
		byte ipBlock[] = new byte[DES_BLOCK_SIZE];
		byte expansionBlock[] = new byte[EXPANSION_BLOCK_SIZE];
		byte substitutionBlock[] = new byte[DES_BLOCK_SIZE / 2];
		byte pboxTarget[] = new byte[DES_BLOCK_SIZE / 2];
		byte recombBox[] = new byte[DES_BLOCK_SIZE / 2];
		
		byte pc1Key[] = new byte[PC1_KEY_SIZE];
		byte subKey[] = new byte[SUBKEY_SIZE];
		
		int round;
		BitOperator.permute(ipBlock, plainText, ipTable);		
		BitOperator.permute(pc1Key, key, pc1Table);
		
		for (round=0; round < 16; round++){
			BitOperator.permute(expansionBlock, 0, ipBlock, 4, expansionTable, 0, expansionTable.length);
			
			if (encrypt){
				BitOperator.leftRotation(pc1Key);
				if (!(round<=1||round==8||round==15))
				{
					// Rotate twice except in rounds 1, 2, 9 & 16
					BitOperator.leftRotation(pc1Key);
				}
			}
			BitOperator.permute(subKey, pc1Key, pc2Table);
			if (!encrypt){
				BitOperator.rightRotation(pc1Key);
				if (!(round>=14||round==7||round==0))
				{
					BitOperator.rightRotation(pc1Key);
				}
			}		
			
			BitOperator.xorArray(expansionBlock, subKey);
			
			substitutionBlock = new byte[DES_BLOCK_SIZE / 2];
			substitutionBlock[0] = (byte)(sbox[0][(expansionBlock[0] & 0xFC )>>2] << 4);
			substitutionBlock[0] |= (byte)(sbox[1][(expansionBlock[0] & 0x03) << 4 | (expansionBlock[1] & 0xF0) >> 4 ]);
			substitutionBlock[1] = (byte)(sbox[2][(expansionBlock[1] & 0x0F) << 2 | (expansionBlock[2] & 0xC0) >> 6 ] << 4);
			substitutionBlock[1] |= (byte)(sbox[3][(expansionBlock[2] & 0x3F)]);
			substitutionBlock[2] = (byte)(sbox[4][(expansionBlock[3] & 0xFC) >> 2 ] << 4);
			substitutionBlock[2] |= (byte)(sbox[5][(expansionBlock[3] & 0x03 ) << 4 | (expansionBlock[4] & 0xF0 ) >> 4 ]);
			substitutionBlock[3] = (byte)(sbox[6][(expansionBlock[4] & 0x0F) << 2 | (expansionBlock[5] & 0xC0) >> 6 ] << 4);
			substitutionBlock[3] |= (byte)(sbox[7][(expansionBlock[5] & 0x3F)]);
			
			
			BitOperator.permute(pboxTarget, substitutionBlock, pTable);
			
			recombBox = Arrays.copyOf(ipBlock, DES_BLOCK_SIZE/2);
			for(int i=0;i<DES_BLOCK_SIZE/2;i++){
				ipBlock[i] = ipBlock[i+4];
			}
			BitOperator.xorArray(recombBox, pboxTarget);
			for(int i=0;i<DES_BLOCK_SIZE/2;i++){
				ipBlock[i+4] = recombBox[i];
			}
		}
			
		recombBox = Arrays.copyOf(ipBlock, DES_BLOCK_SIZE/2);
		for(int i=0;i<DES_BLOCK_SIZE/2;i++){
			ipBlock[i] = ipBlock[i+4];
		}
		for(int i=0;i<DES_BLOCK_SIZE/2;i++){
			ipBlock[i+4] = recombBox[i];
		}
		BitOperator.permute(output, ipBlock, fpTable);
		
		return output;
	}
	
	private byte[] desOperateCBC(byte[] input, byte[] iv, byte[] key, boolean encrypt){
		byte[] inputBlock = new byte[DES_BLOCK_SIZE];
		byte[] outputBlock = new byte[DES_BLOCK_SIZE];
		byte[] output = new byte[input.length];
		int i=0;
		
		if (input.length%DES_BLOCK_SIZE==0){	
			while (i < output.length) {
				inputBlock = Arrays.copyOfRange(input, i, i+DES_BLOCK_SIZE);
				
				if (encrypt){
					BitOperator.xorArray(inputBlock, iv); // implement CBC		
					outputBlock = desBlockOperate(inputBlock, key, encrypt);
					
					for (int j=0;j < outputBlock.length; j++) {
						iv[j] = outputBlock[j];
						output[i++] = outputBlock[j];
					}					
				} else {
					outputBlock = desBlockOperate(inputBlock, key, encrypt);
					
					BitOperator.xorArray(outputBlock, iv); // implement CBC		
					iv = Arrays.copyOfRange(input, i, i+DES_BLOCK_SIZE);
					
					for (int j=0;j < outputBlock.length; j++) {
						output[i++] = outputBlock[j];
					}
				}				
			}
			
			return output;
		} 
		
		
		return null;
	}
		
	@Override
	public byte[] encrypt(byte[] data, byte[] key, byte[] iv, String mode) {
		if ("CBC".equals(mode)){
			return desOperateCBC(data, iv, key, true);
		}
		
		return null;
	}
	
	@Override
	public byte[] decrypt(byte[] data, byte[] key, byte[] iv, String mode) {
		if ("CBC".equals(mode)){
			return desOperateCBC(data, iv, key, false);
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		byte[] enc = new DES().desOperateCBC("abcdefgh".getBytes(), "initialz".getBytes(), "twentyfo".getBytes(), true);
		System.out.println(BitOperator.getRadix16FromByteArray(enc));
		
		byte[] dec = new DES().desOperateCBC(enc, "initialz".getBytes(), "twentyfo".getBytes(), false);		
		System.out.println(new String(dec));
		
		
		Cipher c = Cipher.getInstance("AES");
		byte[] b = c.encrypt("abcdefghijklmnopabcdefghijklmnop".getBytes(), "abcdefghabcdefgh".getBytes(), "initialzinitialz".getBytes(), "CBC");
		System.out.println(BitOperator.getRadix16FromByteArray(b));
		b = c.decrypt(b, "abcdefghabcdefgh".getBytes(), "initialzinitialz".getBytes(), "CBC");
		System.out.println(new String(b));		
	}

}

