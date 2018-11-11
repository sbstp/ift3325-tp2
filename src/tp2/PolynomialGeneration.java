package tp2;

public class PolynomialGeneration {
	
	public static BitVector polynomialGenerator(BitVector type, BitVector num, BitVector data){
		//RECEIVES: the type, number and data of the frame
		//RETURNS: the CRC
		
		//The polynomial the algorithm uses
		BitVector polynomial = BitVector.fromBitString("10001000000100001");
		
		//Concatenate the type, num, data and the 16 zeroes
		BitVector message = BitVector.fromBitString(type.toBitString() + num.toBitString() + data.toBitString() + "0000000000000000");
		
		//Executes the Algorithm
		while(message.length() > 17){
			BitVector temp = new BitVector();
			for(int i = 0; i < polynomial.length(); i++){
				if(!(message.get(i) == polynomial.get(i))){
					//XOR results in a 1
					temp.push(true);
				}
				else if(message.get(i) == polynomial.get(i) && temp.length() > 0){
					//temp is not empty and the XOR results in a 0
					//add 0
					temp.push(false);
				}
			}
			for(int i = polynomial.length(); i < message.length(); i++){
				temp.push(message.get(i));
			}
			message = temp;
		}
		
		return message;
	}
	
	public static boolean polynomialVerification(BitVector type, BitVector num, BitVector data, BitVector crc){
		//RECEIVES: the type, num, data and crc of the frame
		//RETURNS: True if the end value is all zeroes, otherwise it returns false
		
		//The polynomial the algorithm uses
		BitVector polynomial = BitVector.fromBitString("10001000000100001");
		
		//Concatenate the type, num, data and the crc
		BitVector message = BitVector.fromBitString(type.toBitString() + num.toBitString() + data.toBitString() + crc.toBitString());
		
		//Executes the Algorithm
		while(message.length() > 17){
			BitVector temp = new BitVector();
			for(int i = 0; i < polynomial.length(); i++){
				if(!(message.get(i) == polynomial.get(i))){
					//XOR results in a 1
					temp.push(true);
				}
				else if(message.get(i) == polynomial.get(i) && temp.length() > 0){
					//temp is not empty and the XOR results in a 0
					//add 0
					temp.push(false);
				}
			}
			for(int i = polynomial.length(); i < message.length(); i++){
				temp.push(message.get(i));
			}
			message = temp;
		}
		
		//Verifies if the message has all zeroes
		boolean error = false;
		for(int i = 0; i < message.length(); i++){
			if(message.get(i)){
				error = true;
			}
		}
		
		return !error;
	}
}
