/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.smpp.Data;
import org.smpp.pdu.ByteData;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.DeliverSMResp;
import org.smpp.pdu.PDU;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.ValueNotSetException;
import org.smpp.pdu.WrongLengthOfStringException;
import org.smpp.util.ByteBuffer;
import org.smpp.util.NotEnoughDataInByteBufferException;
import org.smpp.util.TerminatingZeroNotFoundException;
import sun.io.CharToByteConverter;

/**
 *
 * @author Ahmed
 */
public class ShortMessage extends ByteData{

    //Minimal size of the message in bytes
    private int minLength = 0;
    //Max size of the message in octets
    private int maxLength = 0;
    //the actual message encoded with the provided encoding.
    private String message = null;
    //the encoding of the message
    private String encoding = null;
    //the length of the message data
    private int length = 0;
    //the message data after convertion to the sequence of octets
    private byte[] messageData = null;

    /**
     * Construct the short message with max data length -- the max count
     * of octets carried by the massege. It's not count of chars when interpreted
     * with certain encoding.
     * @param maxLength the max length of the message
     */

    public ShortMessage(int maxLength){
        this.maxLength = maxLength;
    }
    /**
     * Construct the short message with mina nd max data length --
     * the min and max count of octets carried by the massege.
     * It's not count of chars when interpreted with certain encoding.
     * @param minLength the min length of the message
     * @param maxLength the max length of the message
     */
    public ShortMessage(int minLength,int maxLength){
        this.minLength = minLength;
        this.maxLength = maxLength;
    }
    /**
     * Reads data from the buffer and stores them into <code>messageData</code>.
     * The data can be later fetched using one of the <code>getMessage</code>
     * methods.
     * @param buffer the buffer containing the message data; must contain exactly
     *               the data of the message (not zero terminated nor length tagged)
     * @see #getMessage()
     * @see #getMessage(String)
     */

    @Override
    public void setData(ByteBuffer buffer) throws PDUException, NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException {

        byte[] messageData = null;
        int length = 0;
        if(buffer != null){
            messageData = buffer.getBuffer();
            length = messageData == null ? 0 : messageData.length;
            checkString(minLength, length, maxLength);
        }
        this.message = null;
        this.messageData = messageData;
        this.length = length;
    }

    /**
     * Returns the sequence of octets generated from the message according the encoding
     * provided.
     * @return the bytes generated from the message
     */

    @Override
    public ByteBuffer getData() throws ValueNotSetException {
       ByteBuffer buffer = null;
       buffer = new ByteBuffer(messageData);
       return buffer;
    }

    /**
     * Sets the message a new value. Default encoding <code>Data.ENC_ASCII</code>
     * is used.
     * @param message the message
     * @exception WrongLengthOfStringException thrown when the message
     *            too short or long
     */
    public void setMessage(String message) throws WrongLengthOfStringException,UnsupportedEncodingException{

            setMessage(message, Data.ENC_ASCII);
    }

    /**
     * Sets the message to a value with given encoding.
     * @param message the message
     * @param encoding the encoding of the message provided
     * @exception WrongLengthOfStringException thrown when the message
     *            too short or long
     * @exception UnsupportedEncodingException if the required encoding is not
     *            available for the Java Runtime system
     */

    public void setMessage(String message,String encoding){
        try {
            checkString(message, minLength, maxLength, encoding);
            if(message != null){
                messageData = message.getBytes(encoding);
               this.message = message;
               this.length = messageData.length;
               this.encoding = encoding;
            }else{
                this.message = null;
                this.messageData = null;
                this.encoding = encoding;
                this.length = 0;
            }
        } catch (WrongLengthOfStringException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Sets the encoding of the messasge.
     * Handy for message read from <code>ByteBuffer</code> to set the encoding ad hoc.
     * @param encoding the message encoding
     * @exception UnsupportedEncodingException if the required encoding is not
     *            available for the Java Runtime system
     */
    public void setEncoding(String encoding){
        try {
            message = new String(messageData, encoding);
            this.encoding = encoding;
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

    }
    /**
     * Returns the message. If the message was read from <code>ByteBuffer</code>
     * and no explicit encoding is set, the <code>Data.ENC_ASCII</code> encoding
     * is used. Otherwise the encoding set is used.
     */
    public String getMessage(){
        String useEncoding = encoding != null ? encoding : Data.ENC_ASCII;
        String theMessage = null;

        try{
            theMessage = getMessage(useEncoding);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return theMessage;
    }

    /**
     * Returns the message applying the provided encoding to convert
     * the sequence of octets.
     * @param encoding the required encoding of the resulting (String) message
     * @exception UnsupportedEncodingException if the required encoding is not
     *            available for the Java Runtime system
     */

    public String getMessage(String encoding){
        String message = null;
        try{
            if(messageData != null){
                if((encoding != null) && (this.encoding != null) && (encoding.equals(this.encoding))){
                    // if the required encoding is the same as current encoding
                // or if the encoding haven't been set yet
                    if(this.message == null){
                        this.message = new String(messageData,encoding);
                    }
                    message = this.message;
                }else
                    if(encoding != null){
                        message = new String(messageData,encoding);
                    }else{
                    message = new String(messageData);
                    }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return message;
    }

    /** Returns the length of the message in octets. */
    public int getLength(){
        return messageData.length;
    }
    /** Returns the encoding of the message. */
    public String getEncoding(){
        return encoding;
    }
    /** Returns if the encoding provided is supported by the Java Runtime system. */
    public static boolean encodingSupported(String encoding){
        boolean supported = true;
        try{
            CharToByteConverter.getConverter(encoding);
        }catch(Exception ex){
            supported = false;
        }

        return supported;
    }

    public String debugString() {
		String dbgs = "(sm: ";
		if (encoding != null) {
			dbgs += "enc: ";
			dbgs += encoding;
			dbgs += " ";
		}
		dbgs += "msg: ";
		if(encoding != null) {
			try {
				dbgs += getMessage(encoding);
			} catch(Exception e) {
				dbgs += getMessage();
			}
		} else {
			dbgs += getMessage();
		}
		dbgs += ") ";
		return dbgs;
	}

    

//    public static void main(String[] args) throws PDUException, NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException{
//        try {
//            Padding pad = new Padding();
//            String shortMsg = "0000001f000000090000000000000001746573740070617373000000010100";
//            //String pagMsg = pad.rightPad(shortMsg, 808464416, "0");
//            byte[] buffer = shortMsg.getBytes();
//            ByteBuffer buffer1 = new ByteBuffer(buffer);
//            ShortMessage msg = new ShortMessage(0, 62);
//            msg.setData(buffer1);
//            String message = msg.getMessage();
//            System.out.println("Message : " + message);
//            PDU pdu = null;
//            pdu = new PDU() {
//
//                @Override
//                public boolean isRequest() {
//                    return true;
//                }
//
//                @Override
//                public boolean isResponse() {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//            };
//            //pdu = buffer1;
//            pdu.setCommandStatus(067272);
//            int commandLength = pdu.getCommandStatus();
//            System.out.println("Debug String : " + msg.debugString());
//            System.out.println("Command Length  : " + commandLength);
//            Utilities utils = new Utilities();
//            msg.setMessage("*300#",Data.ENC_CP1252);
//
//            System.out.println("Msg : " +msg.getMessage());
//        }  catch (WrongLengthOfStringException ex) {
//            ex.printStackTrace();
//        }
//    }

}
