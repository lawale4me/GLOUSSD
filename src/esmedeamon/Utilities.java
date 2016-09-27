/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Ahmed
 */
public class Utilities {

    private String pduTxt;
    private String pduTxtLen;
    private boolean withUDH;

    private static final int[] GSM7CHARS = {
        0x0040, 0x00A3, 0x0024, 0x00A5, 0x00E8, 0x00E9, 0x00F9, 0x00EC,
        0x00F2, 0x00E7, 0x000A, 0x00D8, 0x00F8, 0x000D, 0x00C5, 0x00E5,
        0x0394, 0x005F, 0x03A6, 0x0393, 0x039B, 0x03A9, 0x03A0, 0x03A8,
        0x03A3, 0x0398, 0x039E, 0x00A0, 0x00C6, 0x00E6, 0x00DF, 0x00C9,
        0x0020, 0x0021, 0x0022, 0x0023, 0x00A4, 0x0025, 0x0026, 0x0027,
        0x0028, 0x0029, 0x002A, 0x002B, 0x002C, 0x002D, 0x002E, 0x002F,
        0x0030, 0x0031, 0x0032, 0x0033, 0x0034, 0x0035, 0x0036, 0x0037,
        0x0038, 0x0039, 0x003A, 0x003B, 0x003C, 0x003D, 0x003E, 0x003F,
        0x00A1, 0x0041, 0x0042, 0x0043, 0x0044, 0x0045, 0x0046, 0x0047,
        0x0048, 0x0049, 0x004A, 0x004B, 0x004C, 0x004D, 0x004E, 0x004F,
        0x0050, 0x0051, 0x0052, 0x0053, 0x0054, 0x0055, 0x0056, 0x0057,
        0x0058, 0x0059, 0x005A, 0x00C4, 0x00D6, 0x00D1, 0x00DC, 0x00A7,
        0x00BF, 0x0061, 0x0062, 0x0063, 0x0064, 0x0065, 0x0066, 0x0067,
        0x0068, 0x0069, 0x006A, 0x006B, 0x006C, 0x006D, 0x006E, 0x006F,
        0x0070, 0x0071, 0x0072, 0x0073, 0x0074, 0x0075, 0x0076, 0x0077,
        0x0078, 0x0079, 0x007A, 0x00E4, 0x00F6, 0x00F1, 0x00FC, 0x00E0,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
        -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
    };

    
    public Utilities(){
        
    }

     public  String toBinary( byte[] bytes ){

    StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
    for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
        sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
    return sb.toString();
}


 public  byte[] fromBinary( String s ){

    int sLen = s.length();
    byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
    char c;
    for( int i = 0; i < sLen; i++ )
        if( (c = s.charAt(i)) == '1' )
            toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
        else if ( c != '0' )
            throw new IllegalArgumentException();
    return toReturn;
}


    public  String convertStringToHex(String str){

	  char[] chars = str.toCharArray();

	  StringBuffer hex = new StringBuffer();
	  for(int i = 0; i < chars.length; i++){
	    hex.append(Integer.toHexString((int)chars[i]));
	  }

	  return hex.toString();
  }



    public  String fromHex(String s) throws UnsupportedEncodingException {
  byte bs[] = new byte[s.length() / 2];
  for (int i=0; i<s.length(); i+=2) {
    bs[i/2] = (byte) Integer.parseInt(s.substring(i, i+2), 16);
  }
  return new String(bs, "UTF8");
}



    public  byte[] hexStringToByteArray(String data) {
    int k = 0;
    byte[] results = new byte[data.length() / 2];
    for (int i = 0; i + 1 < data.length(); i += 2, k++){
    results[k] = (byte) (Character.digit(data.charAt(i), 16) << 4);
    results[k] += (byte) (Character.digit(data.charAt(i + 1), 16));
}
    return results;
}


     String  HEX_STRING  = "0123456789abcdef";
  public   String convertBinary2Hexadecimal(byte[] binary) {
  StringBuffer buf = new StringBuffer();
  int block = 0;

  for (int i=0; i<binary.length; i++) {
    block = binary[i] & 0xFF;
    buf.append(HEX_STRING.charAt(block >> 4));
    buf.append(HEX_STRING.charAt(binary[i] & 0x0F));
  }
  return buf.toString();
}



 public   String stringToHex(String string) {
  StringBuilder buf = new StringBuilder(200);
  for (char ch: string.toCharArray()) {
    if (buf.length() > 0)
      buf.append(' ');
    buf.append(String.format("%04x", (int) ch));
  }
  return buf.toString();
}



 final protected static char[] hexArray = "0123456789abcdef".toCharArray();
public  String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for ( int j = 0; j < bytes.length; j++ ) {
        v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}

public void sendFile(File file, DataOutputStream dos) throws FileNotFoundException, IOException {
    if(dos!=null&&file.exists()&&file.isFile())
    {
        FileInputStream input = new FileInputStream(file);
        dos.writeLong(file.length());
        System.out.println(file.getAbsolutePath());
        int read = 0;
        while ((read = input.read()) != -1)
            dos.writeByte(read);
        dos.flush();
        input.close();
        System.out.println("File successfully sent!");
    }
}

public String convertHexToString(String hex){

	  StringBuilder sb = new StringBuilder();
	  StringBuilder temp = new StringBuilder();
 
	  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
	  for( int i=0; i<hex.length()-1; i+=2 ){

	      //grab the hex in pairs
	      String output = hex.substring(i, (i + 2));
	      //convert hex to decimal
	      int decimal = Integer.parseInt(output, 16);
	      //convert the decimal to character
	      sb.append((char)decimal);

	      temp.append(decimal);
	  }
	  //System.out.println("Decimal : " + temp.toString());

	  return sb.toString();
  }

public int HexadecimalToInteger(String hexValue) throws IOException{
    BufferedReader read =
  new BufferedReader(new InputStreamReader(System.in));
  System.out.println("Enter the hexadecimal value:!");
  String s = hexValue;//read.readLine();
  int intValue = Integer.valueOf(s, 16).intValue();

  return intValue;
}

private  String from7BitBinaryToHexReversed(String binary){
        String ret = "";
        String temp = "";
        int length = binary.length();
        int rem = length % 8;
        int missingLenth = 8 - rem;
        StringBuilder zeros = new StringBuilder();
        for(int i = 0; i < missingLenth; i++) {
            zeros.append("0");
        }
        binary = zeros.toString() + binary;
        length = binary.length();
        for (int i = length; i >= 8; i -= 8) {//read in reverse direction
            temp = binary.substring((i - 8), i);//chop into 8 bits
            int val = Integer.parseInt(temp, 2);//get decimal value of binary
            String code = Integer.toHexString(val);
            if (code.length() < 2) {
                code = "0" + code;
            }
            ret += code;
        }
        return ret.toUpperCase();
    }

    public String encode(String ascci){
        StringBuilder sb = new StringBuilder();
        int length = ascci.length();
        int gsm7Length = GSM7CHARS.length;
        for (int i = length; i > 0; i--) {

            char c = ascci.charAt((i - 1));
            for (int j = 0; j < gsm7Length; j++) {

                if ((char) GSM7CHARS[j] == c) {
                    int num = GSM7CHARS[j];
                    sb.append(makeSevenBits(Integer.toBinaryString(num)));
                }
            }
        }
        String encoded = from7BitBinaryToHexReversed(sb.toString());
        return encoded;
//        return stringToHex(sb.toString());
    }
    public String encodeToGSM(String ascci){
        StringBuilder sb = new StringBuilder();
        int length = ascci.length();
        int gsm7Length = GSM7CHARS.length;
        for (int i = length; i > 0; i--) {

            char c = ascci.charAt((i - 1));
            for (int j = 0; j < gsm7Length; j++) {

                if ((char) GSM7CHARS[j] == c) {
                    int num = GSM7CHARS[j];
                    sb.append(makeSevenBits(Integer.toBinaryString(num)));
                }
            }
        }
        String encoded = from7BitBinaryToHexReversed(sb.toString());
        return encoded;
//        return stringToHex(sb.toString());
    }

    public String decode(String hex) {
        StringBuilder binary = new StringBuilder();
        StringBuilder decoded = new StringBuilder();
        int length = hex.length();
        for (int i = length; i >= 2; i -= 2) {
            String twos = hex.substring((i - 2), i);//2 characters at a time on reverse direction
            binary.append(fromHexTo8BitBinary(twos));//Convert to 8 bit binary
        }
        int gsm7Length = GSM7CHARS.length;
        for (int i = binary.length(); i >= 7; i -= 7) {
            String seven = binary.substring((i - 7), i);//Chop into 7 bits binary in reverse direction
            int decimalOfSeven = Integer.parseInt(seven, 2);
            for (int j = 0; j < gsm7Length; j++) {
                if (GSM7CHARS[j] == decimalOfSeven) {
                    decoded.append("").append((char) GSM7CHARS[j]);//Do translation
                }
            }
        }
        return decoded.toString();
    }
    public String decodeGSMTOPlain(String GSMencoded) {
        StringBuilder decoded = new StringBuilder();
        StringBuilder binary = new StringBuilder();
        int gsm7Length = GSM7CHARS.length;
        for (int i = GSMencoded.length(); i >= 7; i -= 7) {
            String seven = binary.substring((i - 7), i);//Chop into 7 bits binary in reverse direction
            int decimalOfSeven = Integer.parseInt(seven, 2);
            for (int j = 0; j < gsm7Length; j++) {
                if (GSM7CHARS[j] == decimalOfSeven) {
                    decoded.append("").append((char) GSM7CHARS[j]);//Do translation
                }
            }
        }
        return decoded.toString();
    }

    private String fromHexTo8BitBinary(String hex){
        StringBuilder ret = new StringBuilder();
        String binary = Integer.toBinaryString(Integer.parseInt(hex, 16));//Convert hex to binary
        int length = 8 - binary.length();
        for (int i = 0; i < length; i++) {
            ret.append("0");//Append missing 0's
        }
        ret.append(binary);
        return ret.toString();
    }

    private String makeSevenBits(String binaryStr){
        String ret = "";
        StringBuilder zeros = new StringBuilder();
        int length = binaryStr.length();
        int appends = 7 - length;
        for (int i = 0; i < appends; i++) {
            zeros.append("0");//Append missing 0's to make 7 bits
        }
        ret = zeros + binaryStr;
        return ret;
    }

    public  String stringTo7bit(String string) {
        String hex = "";
        byte[] bytes = string.getBytes();
        int f = 0;
        while (f < bytes.length - 1) {
            int t = (f % 8) + 1;
            if (t < 8) {
                byte b = (byte) (((bytes[f] >>> (t - 1)) |
                    (bytes[f + 1] << (8 - t))) & 0x000000FF);
                hex += intToHex(b & 0x000000FF).toUpperCase();
            }
            f++;
        }
        if ((f % 8) + 1 < 8)
            hex += intToHex((bytes[f] >>> (f % 8)) & 0x000000FF).toUpperCase();
        return hex;
    }

public static String intToHex(int i) {
        String hex = Integer.toHexString(i);
        if (hex.length() % 2 != 0) hex = 0 + hex;
        return hex;
    }

//private void GSMEncode(byte[] inText){
//    pduTxt = new String();
//    int paddingBits = 0;
//    int in_text_length = inText.length;
//    if(this.withUDH == false){
//        pduTxtLen = hexa_2_string(in_text_length);
//        paddingBits = 0;
//    }else{
//        pduTxtLen = hexa_2_string(in_text_length+7);
//        paddingBits = ((5+1)*8)%7;
//        if(paddingBits !=0){
//            paddingBits = 7-paddingBits;
//        }
//    }
//    int bits = 0;
//    int i;
//    int octect;
//    if(paddingBits != 0){
//        bits = 7-paddingBits;
//        pduTxt += hexa_2_string(inText[0] << (7 - bits));
//        bits++;
//        for(i=0;i>bits; if(i<inText.length-1){
//            octect |= inText[i+1]<<(7-bits);
//        }
//    }
//}
//
//private String hexa_2_string( int hex )
//{
//hex = hex & 0x00FF;
//String str1 = Integer.toHexString(hex); // convert # in string
//if(str1.length()== 2 )
//{
//str1 = str1.substring(0, 3);
//}
//return str1.toUpperCase();
//}

}
