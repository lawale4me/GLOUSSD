/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.smpp.Data;
import org.smpp.charset.Gsm7BitCharsetProvider;
import org.smpp.pdu.DataSM;
import org.smpp.pdu.ValueNotSetException;
import org.smpp.pdu.WrongLengthOfStringException;
import org.smpp.util.ByteBuffer;


/**
 *
 * @author Ahmed
 */
public class Test {

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


    public Test(){

    }

//    public static void main(String[] args) throws UnsupportedEncodingException, WrongLengthOfStringException, IOException, ValueNotSetException, Exception {
//        // TODO code application logic here
//        Utilities utils = new Utilities();
//        System.out.println("Ussd String : " + utils.convertHexToString("363231353037303030323031343236"));
//
//        System.out.println("ConvertHexToString : " +utils.convertHexToString("2347055932235"));
//
//        System.out.println("Result : "+hextoBytes("aa190c3602") );
//
//        System.out.println("Ussd String : " + new String(hextoBytes("aa190c3602")));
//        //DataAccess dataAccess = new DataAccess();
//      // String msg = dataAccess.LogSession("08153652377", "daaggaha");
//       //System.out.println(msg);
//
//       System.out.println("Service Code : " +utils.convertHexToString("2a33303023"));
//
//       Test test = new Test();
//
//       System.out.println("Decode Service Code : " +test.decode("aa190c3602"));
//       System.out.println("Decode Msisdn : " +utils.convertStringToHex("621500206047028"));
//       System.out.println("Encode Service Code: " +test.decode("aa190c3602"));
//       System.out.println("Encode Phone Number: " +test.encode("621500206047028"));
//
//       System.out.println("Response  USSD String : " +utils.convertHexToString("757373645f736572766963655f6f70206d697373696e6700"));
//
//       System.out.println("Requests USSD String : " +utils.convertHexToString("57656c636f6d6520746f2043656c6c756c616e74204e696765726961204c696d697465642c206d6572727920786d617320616e64206861707079204e6577205965617220696e20616476616e6365"));
//
//
//       System.out.println("ConvertFromHexToInt : " +utils.convertHexToString("0000002d"));
//
//       Integer intValue = new Integer(16);
//        String hexValue = Integer.toHexString(2);
//
//        System.out.println("commandID : " +hexValue);
//
//        System.out.println("Mobile Phone In Hex : " +utils.convertStringToHex("2347055932235"));
//
//        System.out.println("Command ID : " + utils.HexadecimalToInteger("00000009"));
//
//        System.out.println("Application Menu " +utils.convertHexToString("555353440001013233343831353336353233373700000000010000"));
//
//        String ucs2 = "00001000";
//
//        System.out.println("UCS2 :" +utils.fromHex(ucs2));
//
//        System.out.println("New Test : " +utils.stringTo7bit("*300#"));
//
//        System.out.println("Message : " +test.encode("AA190C3602"));
//
//        System.out.println("Decode : " +test.decode(("339bac06")));
//
//        System.out.println("New Game : " + utils.decode(" 44373332374246433645393734314634333736383543363642334542454342303942304537324136434636353739334130433632413644423639374139393043"));
//
//        System.out.println("ESME MENU : " + utils.convertHexToString("C2D586611300"));
//
//        ByteBuffer buffer = new ByteBuffer("*300#".getBytes("UTF-8"));
//
//        System.out.println("Buffer : " + buffer.getHexDump());
//
//        String msg = new String(buffer.getBuffer());
//
//        System.out.println("Message : " +msg);
//
//        System.out.println("Gsm7Bit : " + utils.convertHexToString("696e76616c69645f73697a65"));
//        System.out.println("Decode : " + utils.decode("D9775D0EBA86D9EC321D141C8FDF75371D240EB3C3EE7119949E839CB158CD65832900"));
//        System.out.println("Encode : " + utils.encode("Try Again..."));
//
////        DataAccess dbAccess = new DataAccess();
////        String url = dbAccess.fetchUrl("*300#200#");
////
////        System.out.println("Url : " +url);
//
//        String binary = "00000001";
//        Integer i = Integer.parseInt(binary,2);
//        System.out.println("Integer value: "+i);
//
//        DataSM dataSm = new DataSM();
//
//        //FilePermission permission = new FilePermission("META-INF/services/java.nio.charsets.spi.CharsetProvider", "read");
//
//        //System.out.println("Permission String : " +permission.getActions());
//        //grant{permission java.nio.charsets.spi.CharsetProvider "*","read","write"};
//
//        //SecurityPermission perm = new SecurityPermission(binary, ucs2);
////        AllPermission allPermission = new AllPermission("META-INF/services/java.nio.charsets.spi.CharsetProvider", "read");
////        allPermission.newPermissionCollection();
//
//        Gsm7BitCharsetProvider gsm = new Gsm7BitCharsetProvider();
//
//        Charset charset =  gsm.charsetForName(Data.ENC_GSM7BIT);
//         byte[] byteArray = charset.encode("*300#").array();
//
//        System.out.println("Encript : " + charset.encode(utils.encode("*300#")).array());
//
//
//        //System.out.println("Test : "+gha.newEncoder());
//        //java.nio.ByteBuffer result = gsm.charsetForName(Data.ENC_GSM7BIT).encode("*300#");
//
//
//        Gsm7BitEncoderDecoder gbh = new Gsm7BitEncoderDecoder();
//        String encode = gbh.encode("aa190c3602");
//        System.out.println("Encode String : " +encode);
//
//
//        ByteBuffer buffer1 = new ByteBuffer();
//        buffer1.appendBytes(encode.getBytes());
//
//
//
//
//        //System.out.println("Result : " + new String(result.toString()));
//
//        //buffer1.appendString(result.toString());
//      // buffer1.setBuffer(message.getBytes());
//
//     //ByteArrayOutputStream msg1 = new ByteArrayOutputStream();
//
//     //msg1.write(utils.encode(message).getBytes(),0,message.getBytes().length);
//     //buffer1.appendBytes(msg1.toByteArray());
//
//      dataSm.setMessagePayload(buffer1);
//     System.out.println("ASCII : " + dataSm.getMessagePayload().getHexDump());
//
//     //buffer1.appendBytes(utils.bytesToHex("*300#".getBytes()).getBytes());
//     System.out.println("gsm7 : " +(dataSm.getMessagePayload().getHexDump()));
//     System.out.println("Debug String : " +dataSm.debugString());
//     //System.out.println("Message Payload : " + dataSm.getMessagePayload().getBuffer());
//
//     String strBuffer = dataSm.getMessagePayload().getHexDump();
//     System.out.println("StringBuffer : " +new String(strBuffer));
//
//
//      System.out.println("StringToHex : " +utils.convertHexToString("AA190C3602"));
//      System.out.println("HexToString : " +utils.convertHexToString("63616e63656c5f696e64"));
//
//      //String gsm = test.GSMChar("*360#");
//
//     // System.out.println("New GSM : " +gsm);
//     // buffer.appendBytes(gsm.getBytes());
//      System.out.println("Test GSM7 : " +buffer1.getHexDump());
//      //System.out.println("New Test : " + message.getBytes("GSM_DEFAULT"));
//
//      //Base64Util encode = new Base64Util();
//     //String encode =  Base64Util.encode("*300#".getBytes());
//
//     // System.out.println("Base64 : " +encode);
//
//      //buffer1.setBuffer(message.getBytes());
//      //dataSm.setMessagePayload(buffer.readBytes(5));
//      dataSm.setDataCoding((byte)13);
//
//     System.out.println( buffer.getHexDump());
//     System.out.println("Debug String : " +dataSm.debugString());
//
//
//System.out.println("StringToHex : " +utils.convertHexToString("AA190C3602"));
//
//System.out.println("Destination SubAddress : " +utils.convertHexToString("32333438303530303031303039"));
//
//System.out.println("Source SubAddress : " +utils.convertHexToString("363231353030323036303437303238"));
//
//System.out.println("Service Type : " +utils.convertStringToHex("USSD"));
//System.out.println("Mobile Number : " +utils.convertStringToHex("2347055932235"));
//
//System.out.println("Service Code : "+utils.convertStringToHex("360"));
//System.out.println("Service Code : "+utils.encode("360"));
//
//Integer intValue1 = new Integer(49);
//String hexValue1 = Integer.toHexString(intValue);
//System.out.println("Service Code : "+utils.HexadecimalToInteger("1b0c"));
//
//System.out.println("Service Code : "+utils.convertStringToHex("*300#"));
//
//System.out.println("Real Test decode 1 : " +utils.decode("aa996ca6cae546"));
//System.out.println("Real Test decode 2: " +utils.decode("77656c636f6d6520746f2077616c6c6574"));
//System.out.println("Real Test : " +utils.convertHexToString("aa996ca6cae546"));
//String ecoded = utils.encode("*323*99#");
//System.out.println("Real Test encode : " +ecoded);
//System.out.println("Real Test decode : " +utils.decode(ecoded));
//System.out.println("Real Test to hex : " +utils.convertHexToString(utils.encode("welcome to wallet").toLowerCase()));
//
//String msg1 = utils.convertHexToString(utils.encode("welcome to LAA wallet nigeria , africa")).toString();
//
//ByteBuffer buff = new ByteBuffer();
//buff.appendString(msg1,msg1.length(),Data.ENC_ISO8859_1);
////buff.appendCString(msg1,Data.ENC_CP1252);
//
//System.out.println("Another Text1 : " +buff.getHexDump());
//System.out.println("Another Text2 : " +utils.encode("welcome to wallet"));
//System.out.println("Another Text3 : " +utils.decode(buff.getHexDump()));
//System.out.println("Convert : " +utils.convertHexToString("63616e63656c5f696e64"));
//
//dataSm.setMessagePayload(buff);
//
////System.out.println("Another Text : " +buff.getHexDump());
//
//
//
//Gsm7BitCharsetProvider gsm1 = new Gsm7BitCharsetProvider();
//Gsm7BitEncoderDecoder dec = new Gsm7BitEncoderDecoder();
//
//Charset encodingString = (gsm.charsetForName(Data.ENC_GSM7BIT));
//System.out.println("Encode Ascii " + dec.toString().concat("*323*99#"));
//
//
////TLV octects1 = new TLV();
////octects1.setData(buffer);
//
////System.out.println("Octects : " +octects1);
//
//
////    ByteBuffer messagePayload = new ByteBuffer();
////    messagePayload.appendCString("*300#");
////
////     // System.out.println("ByteToHex : "+utils.bytesToHex(messagePayload.getBuffer()));
////      dataSm.setMessagePayload(messagePayload);
////      ByteBuffer payload = dataSm.getMessagePayload();
////
////      byte[] byte1 = payload.getBuffer();
////
////      System.out.println("Byte1 : " + new String(byte1));
////    System.out.println("Buffer Msg: " + dataSm.getMessagePayload().getGsmDump()) ;
////
////    System.out.println("Service Code : " +utils.decode("aa996ca6cae546"));
////
////   System.out.println("Good  : " + new String("..l...F"));
//
//
//
//    //System.out.println("Test :  " +utils.encode(utils.convertHexToString("43656c6c756c616e742057616c6c6574204d656e750a313e546f204765742042616c616e63650a323e4d696e692053746174656d656e740a333e5472616e73666572204d6f6e65790a343e4275792041697274696d650a353e50617920445354560a363e5769746864726177616c0a373e4368616e67652050696e0a383e45786974")));
//
////    ByteBuffer buff = new ByteBuffer();
////
////    String theMessage = "C3329B5D6787DD74D035CC6697E9A066D95D57C47CD437E858A683846176D83D2E2B64BE66DA9D064DE9617AB95D76D315331F551E76CFCD6539A8F97697F30A9A4F58CF838269393DDD2E2B6A3E68380F224EA95685CD774DD3D16479F81E662B6EBE213AEC3E9741D0B45B81F315F1693A";
////    buff.appendString(message);
////    //messagePayload.getHexDump().replaceAll("2a33303023", theMessage);
////
////    System.out.println("theMessage : " +new String(buff.getBuffer()));
//
//
//
//       //AA190C3602
//
//
////        System.out.println(Charset.defaultCharset().toString());
////        //byte[] data = new byte[] {9, 22, 9, 65, 9, 54, 9, 22, 9, 44, 9, 48, 9, 64};
////        byte[] data = "aa190c3602".getBytes();
////        System.out.println(Arrays.toString(data));
////        System.out.println(new String(data, Data.ENC_ASCII));
////        //Data.ENC_GSM7BIT;
//
//    }

    public static synchronized byte[] hextoBytes(String hex) {

       System.out.println("Hex is:" + hex);
       byte[] bts = new byte[hex.length() / 2];
       for (int i = 0; i < bts.length; i++) {
      bts[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
}
return bts;

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

    private String fromPDUText(String PDUSMSText) {
    String endoding = PDUSMSText.substring(0, 2);
    String out = null;
    PDUSMSText = PDUSMSText.substring(15);
    byte bs[] = new byte[PDUSMSText.length() / 2];
    for(int i = 0; i < PDUSMSText.length(); i += 2) {
        bs[i / 2] = (byte) Integer.parseInt(PDUSMSText.substring(i, i + 2), 16);
    }
    try {
         out = new String(bs, "ASCII");
    } catch(UnsupportedEncodingException e) {
        e.printStackTrace();
        return "";
    } finally {
        return out;
    }
}

    public static String stringTo7bit(String string) {
        String hex = "";
        byte[] bytes = string.getBytes();
        int f = 0;
        while (f < bytes.length - 1) {
            int t = (f % 8) + 1;
            if (t < 8) {
                byte b = (byte) (((bytes[f] >>> (t - 1)) |
                    (bytes[f + 1] << (8 - t))) & 0x000000FF);
                //hex += intToHex(b & 0x000000FF).toUpperCase();
                hex += intToHex(b & 0x000000FF).toLowerCase();
            }
            f++;
        }
        if ((f % 8) + 1 < 8)
            //hex += intToHex((bytes[f] >>> (f % 8)) & 0x000000FF).toUpperCase();
            hex += intToHex((bytes[f] >>> (f % 8)) & 0x000000FF).toLowerCase();
        return hex;
    }

public static String intToHex(int i) {
        String hex = Integer.toHexString(i);
        if (hex.length() % 2 != 0) hex = 0 + hex;
        return hex;
    }

public static String GSMChar(String PlainText)
        {
            // ` is not a conversion, just a untranslatable letter
            String strGSMTable ="";
            strGSMTable += "@£$¥èéùìòÇ`Øø`Åå";
            strGSMTable += "Δ_ΦΓΛΩΠΨΣΘΞ`ÆæßÉ";
            strGSMTable += " !\"#¤%&'()*=,-./";
            strGSMTable += "0123456789:;<=>?";
            strGSMTable += "¡ABCDEFGHIJKLMNO";
            strGSMTable += "PQRSTUVWXYZÄÖÑÜ`";
            strGSMTable += "¿abcdefghijklmno";
            strGSMTable += "pqrstuvwxyzäöñüà";

            String strExtendedTable = "";
            strExtendedTable += "````````````````";
            strExtendedTable += "````^```````````";
            strExtendedTable += "````````{}`````\\";
            strExtendedTable += "````````````[~]`";
            strExtendedTable += "|```````````````";
            strExtendedTable += "````````````````";
            strExtendedTable += "`````€``````````";
            strExtendedTable += "````````````````";

            String strGSMOutput = "";
            for(char cPlainText : PlainText.toCharArray())
            {
                int intGSMTable = strGSMTable.indexOf(cPlainText);

                if (intGSMTable != -1)
                {
                    strGSMOutput += String.valueOf(intGSMTable);//intGSMTable.ToString("X2");
                    continue;
                }
                int intExtendedTable = strExtendedTable.indexOf(cPlainText);

                if (intExtendedTable !=-1)
                {
                    strGSMOutput += String.valueOf(27);
                    strGSMOutput += String.valueOf(intExtendedTable);
                }
            }
            return strGSMOutput;
        }


}
