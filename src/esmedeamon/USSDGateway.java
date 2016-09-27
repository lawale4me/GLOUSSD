/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esmedeamon;

import com.mysql.jdbc.CallableStatement;
import esmedeamon.ESMEDaemon.EnquiryLinkRequest;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.smpp.util.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameter.Tag;
import org.smpp.*;
import org.smpp.TCPIPConnection;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.*;
import org.smpp.pdu.PDUException;
import org.jsmpp.session.SMPPServerSession;
import org.jsmpp.session.SMPPServerSessionListener;
import org.jsmpp.session.ServerMessageReceiverListener;
import org.smpp.pdu.UnbindResp;
import org.smpp.pdu.tlv.TLVOctets;
import org.smpp.pdu.tlv.WrongLengthException;

/**
 *
 * @author Ahmed
 */
public class USSDGateway extends Thread implements Runnable{
       
    private ESMEDaemon esme;
    private Socket socket;
    private int port =2054;// LoadProperties.ll.PORT;
    private String connectionName;
    private String message;
    private boolean keepRunning = true;
    private long nextHandShakeTime;
    private static final int WAIT_LENGTH_SECS = 300;
    private long terminateTime;
    public org.smpp.Session session;
    private DataAccess dBAccess;
    private Socket gatewaySocket;
    private CallableStatement callablestatement;
    private String sourceMsisdn;
    private String destinationMsisdn;
    private boolean isConnected;
    private USSDGateway gateway;
    private String messageId;
    //private AbsoluteTimeFormatter timeFormatter;
    private String sessionId;
    private String messageString;
    private String circleId;
    private String bindResponse;
    private boolean asynchronous = false;
    private TCPIPConnection connection = null;
    private boolean bound = false;
     private Address sourceAddress = new Address();
    //Destination address of this USSD session(may include sub-address digit) In first application initiating session set to subscriber address,otherwise set to NULL(Unknown)
    private Address destAddress = new Address();
    private String scheduleDeliveryTime = "";
    private String validityPeriod = "";
    private String shortMessage = "";
    private int numberOfDestination = 1;
   // private String messageId = "";
    private byte esmClass = 0;//Indicate Message Mode and Message Type (0x00 shall be used)
    private byte protocolId = 0;//Protocol Identifier (0x00 shall be used)
    private byte priorityFlag = 0;//Designates the priority level of the message (0x00 shall be used)
    private byte registeredDelivery = 0;//Indicator to signify if an SMSC delivery receipt or an SME acknowledgement is required. (0x00 shall be used).
    private byte replaceIfPresentFlag = 0;//Flag indicating if submitted message  should replace an existing message.(0x00 shall be used)
    private byte dataCoding = 0;//Defines the encoding schemeof the USSD operation user data(short_message). IA5,7-bit character set(English),8-bit binary and 16 bit(UCS2) coding schemes are supported
    private byte smDefaultMsgId = 0;
    private byte ton_source = 0;//ignore by USSD GW,set to 0(UNKNOW)
    private byte npi_source = 0;//ignore by USSD GW,set to 0(UNKNOW)
    //In first application initiating session set to subscriber ton, otherwise set to 0 (Unknown).
    private int ton_destination = 1;
    //In first application initiating session set to subscriber ton, otherwise set to 0 (Unknown).
    private int npi_destination = 1;
    private String schedule_delivery_time = null;//The short message is to be scheduled by the SMSC for delivery. NULL shall  be used
    private String validity_period = null;//The validity period of this message. NULL shall be used
    private String sm_default_msg_id = null;//Indicates the short message to send from a list of predefined short messages stored on the SMSC. Set to NULL
    private int sm_length = 0;//length in octets of the short_message user data.
    private String short_message = "";//USSD phase 1 supports up to 200 octets of short_message and USSD phase 2 support up to 160 octets
    /*The ussd_service_op parameter is required to define the USSD service operation when SMPP is being used as an interface to a (GSM) USSD system
    A USSD session may be initiated by the Mobile or the USSD application . However, as a rule only the USSD application
      terminated the USSD session via specific values of the ussd_service_op. In certain error situations the USSD GW may inform
      the USSD application that the mobile used is disconnected from the USSD session via specific values of ussd_service_op
      optional parameter in the DELIVER_SM PDU .*/
    private byte ussd_service_op;
    /*The sessionID between  the USSD GW and the USSD application. When the USSD application starts a USSD session
     (by issuing a USSR request or USSN request for example) it does not include this parameter and receives an allocated
     sessionid from the USSD GW in the submit_sm_resp PDU*/
    private String ussd_session_id;
    //language indicator
    private String lagguage_indicator;
    private long session_init_timer = 0;
    private long enquiry_link_timer = 60;
    private long inactivity_timer = 60;
    private long response_timer = 1800;
    private ShortMessage shortMsg = null;
    private String serviceType = "USSD";
    private SMPPServerSessionListener sessionListener = null;
    private SMPPServerSession serverSession = null;
    private ServerMessageReceiverListener  messageReceiverListener;
    private SMPPPDUEventListener pduListener = null;
    private long receiveTimeout = Data.RECEIVE_BLOCKING;
    private String msisdn;
    public  String systemId = "";
//    public final String password = "";
//    public final String systemtype = "";
    private final String addressrange = "145";
    private Utilities utils;
    //private String IPAddress = "41.203.65.165";
    private PDU pdu = null;
    private SessionMonitor sessionMonitor;
    Date date = new Date();

       
    public USSDGateway(TCPIPConnection connection,org.smpp.Session session,ESMEDaemon esme){
        try {
            this.session = session;
            this.esme = esme;
            this.connectionName = " HOST : " + port;
            this.connection = connection;
            utils = new Utilities();
            sessionMonitor = new SessionMonitor();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, WAIT_LENGTH_SECS);
            nextHandShakeTime = cal.getTimeInMillis();
            terminateTime = cal.getTimeInMillis();
            dBAccess = new DataAccess();
            message = "Connection in progress";
            if(esme==null){
            throw new IOException("Enquiry Link is null");
            }
            esme.bind();            

            //System.out.println("MessageID : " +messageId);

            //while(keepRunning){
            //System.out.println("esme is sending new enquirylink request to the SMPP Server");
            org.smpp.pdu.EnquireLink request = new org.smpp.pdu.EnquireLink();
            EnquireLinkResp enquiryresponse = null;
            System.out.println("EnquiryLink " +request.debugString());
            if(esme==null||esme.session==null){
            throw new IOException("Enquiry Link is null");
            }
            enquiryresponse = esme.session.enquireLink(request);
            if(enquiryresponse==null){
            throw new IOException("Enquiry Link is null");
            }
            
            System.out.println("EnquiryLinkResp"+enquiryresponse.debugString());
            Thread.sleep(990);
            
            //}
            System.out.println("Weldone, you are good to go");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            System.out.println("**********InterruptedException : " +ex.getMessage()+"**********");
        } catch (ValueNotSetException ex) {
            ex.printStackTrace();
            System.out.println("**********ValueNotSetException : " +ex.getMessage()+"**********");
        } catch (TimeoutException ex) {
            ex.printStackTrace();
            System.out.println("**********TimeoutException : " +ex.getMessage()+"**********");
        } catch (PDUException ex) {
            ex.printStackTrace();
            System.out.println("**********PDUException : " +ex.getMessage()+"**********");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("**********IOException : " +ex.getMessage()+"**********");
            System.out.println("Reconnecting the ESME Client to the SMPP Server");
            esme.Start();
        } catch (WrongSessionStateException ex) {
            ex.printStackTrace();
            System.out.println("**********WrongSessionStateException : " +ex.getMessage()+"**********");
        }catch(NullPointerException ex){
            ex.printStackTrace();
            System.out.println("**********NullPointerException : " +ex.getMessage()+"**********");            
            esme.starter();
        }
        catch(Exception ex){
            ex.printStackTrace();
            System.out.println("**********NullPointerException : " +ex.getMessage()+"**********");            
            esme.starter();
        }
    }

    public void AutoReconnectGateway(){
        bind(esme.IPAddress, esme.port);
    }

    public void bind(String IpAddress,int port){
        try{
            
            org.smpp.pdu.BindRequest request = null;
            BindResponse response = null;

            request = new BindTransciever();

            connection = new TCPIPConnection(IpAddress, port);
            connection.setReceiveTimeout(3*60*1000);
            session = new org.smpp.Session(connection);

            //set value
            request.setSystemId(esme.systemId);
            request.setPassword(esme.password);
            request.setSystemType(esme.systemtype);
            request.setInterfaceVersion((byte)0x34);
            request.setAddressRange(esme.addressrange);



            //Send the request
            System.out.println("Bind request(esme) : " + request.debugString());
            response = session.bind(request);
            System.out.println("Bind response(esme) : " + response.debugString());

            //No Error
            if(response.getCommandStatus() == Data.ESME_ROK){
                keepRunning = true;
                System.out.println("CommandID : " +response.getCommandId());
                System.out.println("Bind Successful");
                
            }
            
            else
                if(response.getCommandStatus() == Data.ESME_RSYSERR){
                System.out.println("System Error, code : " + response.getCommandStatus());
            }else
                if(response.getCommandStatus() == Data.ESME_RINVADR){
                    System.out.println("Invalid address, code : " +response.getCommandStatus());
                }else
                    if(response.getCommandStatus() == Data.BIND_TRANSCEIVER){
                        System.out.println("Bind Tranceiver Error,code : " +response.getCommandStatus());
                    }else
                        if(response.getCommandStatus() == Data.BIND_TRANSCEIVER_RESP){
                            System.out.println("Bind Tranceiver response error, code : " +response.getCommandStatus());
                        }else
                            if(response.getCommandStatus() == Data.ESME_RINVDSTADR){
                                System.out.println("Invalid Destination Address, Error Code : " +response.getCommandStatus());
                               }else
                                   if(response.getCommandStatus() == Data.ESME_RINVSYSID){
                                       System.out.println("Invalid System ID, Error Code : " +response.getCommandStatus());
                                    }else
                                        if(response.getCommandStatus() == Data.ESME_RINVSYSTYP){
                                            System.out.println("Invalid System Type, Error Code : " +response.getCommandStatus());
                                          }else
                                              if(response.getCommandStatus() == Data.ESME_RINVPASWD){
                                                  System.out.println("Invalid Password, Error Code : " +response.getCommandStatus());
                                                }else
                                                    if(response.getCommandStatus() == Data.ESME_RINVMSGLEN){
                                                        System.out.println("Invalid Message Length, Error Code :  " +response.getCommandStatus());
                                                    }else
                                                        if(response.getCommandStatus() == Data.ESME_RINVCMDID){
                                                            System.out.println("Invalid Command ID, Error Code  : " +response.getCommandStatus());
                                                        }else{
                                                              System.out.println("Unknown Error, Code : " +response.getCommandStatus());
                                                        }
        }catch(Exception ex){
            System.out.println("Bind Operation failed EX: " + ex);
            ex.printStackTrace();
            if(connection != null){
                try {
                    connection.close();
                } catch (IOException ex1) {

                    ex1.printStackTrace();
                }
            }
            if(session !=null){
                try {
                    session.close();
                } catch (IOException ex1) {
                    ex1.getMessage();
                } catch (WrongSessionStateException ex1) {
                    ex1.getMessage();
                }
            }
            try {
                System.out.println("Reconnecting to the Gateway After 5minutes");
                Thread.sleep(5 * 60 * 1000);
                AutoReconnectGateway();
            } catch (InterruptedException ex1) {
                ex1.getMessage();
            }
            
        }
        AutoReconnectGateway();
    }

    public void unbind(){

        try{
            if(!bound){
                System.out.println("Not bound, cannot unbind");
                return;
            }
            //Send the request
            System.out.println("Prepare to unbind");
            if(session.getReceiver().isReceiver()){
                System.out.println("It can take a while to stop the receiver.");
            }
            UnbindResp response = session.unbind();
            System.out.println("Unbind response " + response.debugString());
            bound = false;
        }catch(TimeoutException ex){
            System.out.println("Unbind operation failed. " + ex);
        }catch(ValueNotSetException ex){
            System.out.println("ValueNotSetException : " +ex);
        }catch(PDUException ex){
            System.out.println("PDUException  : " +ex);
        }catch(IOException ex){
            System.out.println("IOException  : " +ex);
        }catch(WrongSessionStateException ex){
            System.out.println("WrongSessionStateException  : " + ex);
        }
    }

    public void enquiryLink(){

        try{
            org.smpp.pdu.EnquireLink request = new org.smpp.pdu.EnquireLink();
            org.smpp.pdu.EnquireLinkResp response = null;

            System.out.println("EnquiryLink request : " +request.debugString());

            response = session.enquireLink(request);

            System.out.println("EnquiryLink response : " +response.debugString());

            if(response.getCommandStatus() == Data.ESME_ROK){
              System.out.println("EnquiryLink Success");
            }else
               if(response.getCommandStatus() == Data.ESME_RENQCUSTFAIL){
                  System.out.println("Enquiry Link Failed : " + response.getCommandStatus());
               }else{
                System.out.println("Status : " +response.getCommandStatus());
               }

        }catch(TimeoutException ex){
            System.out.println("Enquire Link operation failed. " + ex);
        }catch(PDUException ex){
            System.out.println("PDUException : " + ex);
        }catch(IOException ex){
            System.out.println("IOException : " +ex);
            ex.printStackTrace();
        }catch(WrongSessionStateException ex){
            System.out.println("WrongSessionStateException : " +ex);
        }catch(NullPointerException nullex){
            System.out.println("NullPointerException  : " + nullex);
        }

    }

    public void receive(){
        

        try{

//            System.out.println("Going to receive a PDU. ");
//			if (receiveTimeout == Data.RECEIVE_BLOCKING) {
//				System.out.print(
//					"The receive is blocking, i.e. the application " + "will stop until a PDU will be received.");
//			} else {
//				System.out.print("The receive timeout is " + receiveTimeout / 1000 + " sec.");
//			}

			if (asynchronous) {
				ServerPDUEvent pduEvent = pduListener.getRequestEvent(receiveTimeout);
				if (pduEvent != null) {
					pdu = pduEvent.getPDU();
                                        received(pdu);
				}
			} else {
                try {
                    pdu = session.receive();
                } catch (UnknownCommandIdException ex) {
                    ex.printStackTrace();
                } catch (TimeoutException ex) {
                    ex.printStackTrace();
                } catch (NotSynchronousException ex) {
                    ex.printStackTrace();
                }
                                received(pdu);
			}
			if (pdu != null) {
				System.out.println("Received PDU " + pdu.debugString());
				if (pdu.isRequest()) {
					//Response response = ((Request) pdu).getResponse();
//					DataSMResp response = new DataSMResp();
//                                        DataSM dataSM = (DataSM)pdu;
//                                        response.setMessageId(dataSM.getReceiptedMessageId());
					// respond with default response
					///System.out.println("Going to send default response to request " + response.debugString());
					//session.respond(response);
                                        received(pdu);
				}
			} else {
				System.out.println("No PDU received this time.");
			}
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(ValueNotSetException vnsex){
            vnsex.printStackTrace();
        }catch(WrongLengthOfStringException wlosex){
            wlosex.printStackTrace();
        }catch(PDUException pduex){
            pduex.printStackTrace();
        }catch(NotEnoughDataInByteBufferException nedibbex){
            nedibbex.printStackTrace();
        }catch(TerminatingZeroNotFoundException tznfex){
            tznfex.printStackTrace();
        }
    }

public String submit(String msisdn, String shortMessage, String sender, byte senderTon, byte senderNpi) {
		try {
			SubmitSM request = new SubmitSM();
			SubmitSMResp response;

			// set values
			request.setServiceType(serviceType);

			if(sender != null) {
				if(sender.startsWith("+")) {
					sender = sender.substring(1);
					senderTon = 1;
					senderNpi = 1;
				}
				if(!sender.matches("\\d+")) {
					senderTon = 5;
					senderNpi = 0;
				}

				if(senderTon == 5) {
					request.setSourceAddr(new Address(senderTon, senderNpi, sender, 11));
				} else {
					request.setSourceAddr(new Address(senderTon, senderNpi, sender));
				}
			} else {
				request.setSourceAddr(sourceAddress);
			}

			if(msisdn.startsWith("+")) {
				msisdn = msisdn.substring(1);
			}
			request.setDestAddr(new Address((byte)ton_destination, (byte)npi_destination, msisdn));
			request.setReplaceIfPresentFlag(replaceIfPresentFlag);
			request.setShortMessage(shortMessage,Data.SERVICE_USSD);
			request.setShortMessage(shortMessage);
			request.setScheduleDeliveryTime(scheduleDeliveryTime);
			request.setValidityPeriod(validityPeriod);
			request.setEsmClass(esmClass);
			request.setProtocolId(protocolId);
			request.setPriorityFlag(priorityFlag);
			request.setRegisteredDelivery(registeredDelivery);
			request.setDataCoding(dataCoding);
			request.setSmDefaultMsgId(smDefaultMsgId);
                        request.setServiceType(serviceType);

                        //request.setUssdServiceOp((byte)ussd_service_op);
                        //request.setItsSessionInfo()
                        //request.setSmDefaultMsgId()


			// send the request

			request.assignSequenceNumber(true);
			System.out.println("Submit request " + request.debugString());
			response = session.submit(request);
			System.out.println("Submit response " + response.debugString());
			messageId = response.getMessageId();

                        System.out.println("MessageId : " +messageId);
		} catch (Exception ex) {
			System.out.println("Submit operation failed. " + ex);
                        ex.printStackTrace();
		}

                return messageId;
	}


public String DataSM(String msisdn,String message,String sessionId,USSDGateway gateway){

    DataSM dataSm = new DataSM();
    DataSMResp dataSmResp = null;
    String msg ="Welcome to Crosstee";//+LoadProperties.ll.PRODUCTNAME;
    ByteBuffer messagePayload = new ByteBuffer(msg.getBytes());

    try{

        dataSm.setServiceType(serviceType);
        dataSm.setDestAddr((byte)ton_destination,(byte)npi_destination, msisdn);
        dataSm.setMessagePayload(messagePayload);
        dataSm.setReceiptedMessageId(sessionId);
        dataSm.setEsmClass(esmClass);
        dataSm.setRegisteredDelivery(registeredDelivery);
        dataSm.setDataCoding(dataCoding);


        System.out.println("Sending Application Menu to the subscriber");

        System.out.println("SessionID : " +sessionId);
        System.out.println("Service Type : " +serviceType);
        System.out.println("MSISDN : " +msisdn);
        System.out.println("ESMCLASS : " +esmClass);
        System.out.println("registeredDelivery : " +esmClass);
        System.out.println("Data Coding : " +dataCoding);
        System.out.println("Destination ton  : " +ton_destination);
        System.out.println("Destination npi  : " +npi_destination);
        System.out.println("Message   : " +msg);

        
        dataSmResp = esme.session.data(dataSm);

        if(dataSmResp.getCommandStatus() == Data.ESME_RINVDSTADR){
            System.out.println("Invalid destination address,Error Code : " + dataSmResp.getCommandStatus());
        }
        if(dataSmResp.getCommandStatus() == Data.ESME_RINVDSTNPI){
            System.out.println("Invalid destination npi,Error Code : " + dataSmResp.getCommandStatus());
        }
        if(dataSmResp.getCommandStatus() == Data.ESME_RINVDSTTON){
            System.out.println("Invalid destination ton,Error Code : " + dataSmResp.getCommandStatus());
        }
        if(dataSmResp.getCommandStatus() == Data.ESME_RINVPARAM){
            System.out.println("Invalid parameter,Error Code : " + dataSmResp.getCommandStatus());
        }
        if(dataSmResp.getCommandStatus() == Data.ESME_RINVSERTYP){
            System.out.println("Invalid service type,Error Code : " + dataSmResp.getCommandStatus());
        }
        //dataSm.setDataCoding(Data.ENC_ASCII);
    }catch(WrongLengthOfStringException ex){
        ex.printStackTrace();
    }catch(WrongLengthException ex){
        ex.printStackTrace();
    }catch(ValueNotSetException ex){
        ex.printStackTrace();
    }catch(TimeoutException ex){
        ex.printStackTrace();
    }catch(PDUException ex){
        ex.printStackTrace();
    }catch(IOException ex){
        ex.printStackTrace();
    }catch(WrongSessionStateException ex){
        ex.printStackTrace();
    }catch(NullPointerException ex){
        ex.printStackTrace();
    }


    return dataSmResp.getMessageId();
}
public void received(PDU pdu) throws ValueNotSetException, PDUException, NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException{
        AtomicLong sent = new AtomicLong(0);
        AtomicLong successfullySent = new AtomicLong(0);
        AtomicLong delivered = new AtomicLong(0);

        try{

        if(pdu instanceof DeliverSM){
            System.out.println("DeliverSM Pdu Received from SMPP GATEWAY");
            delivered.incrementAndGet();
            DeliverSM deliverSm = (DeliverSM)pdu;

            shortMessage =  deliverSm.getShortMessage();
            String receiptedMessageId = deliverSm.getReceiptedMessageId();

            Address source_addr = deliverSm.getSourceAddr();
            msisdn = source_addr.getAddress();

            int msgLength = deliverSm.getSmLength();

            deliverSm.getSmDefaultMsgId();
            deliverSm.getItsSessionInfo();

            System.out.println("SUBSCRIBER MSISDN : " + msisdn);
            System.out.println("SHORT MESSAGE RECEIVED FROM SUBSCRIBER: " + shortMessage);
            System.out.println("SHORT MESSAGE LENGTH : " +msgLength);

            DeliverSMResp deliverSmResp  = new DeliverSMResp();
            deliverSmResp.setSequenceNumber(deliverSm.getSequenceNumber());

            try{
                gateway.session.respond(deliverSmResp);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }else
            if(pdu instanceof SubmitSM){
                System.out.println("SubmitSM Pdu Received from SMPP GATEWAY");
                sent.incrementAndGet();
                if(pdu.getCommandStatus() == 0){
                    successfullySent.incrementAndGet();
                    System.out.printf("Sent total = {}, successful = {}.", sent.get(), successfullySent.get());
                }
            }else
                if(pdu instanceof EnquireLinkResp){
                    System.out.println("EnquireLinkResp Pdu Received from SMPP GATEWAY");

//                    System.out.println("Enquiry link Command Length : " + pdu.getCommandLength());
//                    System.out.println("Enquiry link Command Id : " + pdu.getCommandId());
//                    System.out.println("Enquiry link Command Status : " + pdu.getCommandStatus());
                    System.out.println("Pdu Data : " + pdu.debugString());

                    EnquireLinkResp response = (EnquireLinkResp)pdu;
                    ByteBuffer buffer = response.getData();
                    shortMsg = new ShortMessage(0, 16);
                    shortMsg.setData(buffer);
                    String message1 = shortMsg.getMessage();

                    System.out.println("Enquiry Message Received : " + message1);
                    System.out.println("Enquiry Message Received Debug String : " + shortMsg.debugString());
                    System.out.println("HexadecimalDump : " + buffer.getHexDump());


                }else
                    if(pdu instanceof BindTransmitterResp){
                        System.out.println("BindTransmitterResp Pdu Received from SMPP GATEWAY");


                        System.out.println("BindTranceiver Command Length : " + pdu.getCommandLength());
                        System.out.println("BindTranceiver Command ID : " + pdu.getCommandId());
                        System.out.println("BindTranceiver Command Status : " + pdu.getCommandStatus());
                        System.out.println("Pdu Data : " + pdu.getData());

                        BindTransmitterResp response = (BindTransmitterResp)pdu;
                        ByteBuffer buffer = response.getData();

                        System.out.println("BindTransmitterResp HexadecimalDump : " +buffer.getHexDump());

                        shortMsg = new ShortMessage(0, 16);
                        shortMsg.setData(buffer);
                        String msg = shortMsg.getMessage();

                        System.out.println("BindTransmitterResp Message : " + msg.toString());
                    }
                    else
                        if(pdu instanceof BindResponse){
                            BindResponse response = (BindResponse)pdu;
                            systemId = response.getSystemId();
                            System.out.println("SystemId : " +systemId);

                        }else
                            if(pdu instanceof DataSM){
                                try{
                                DataSM incomingrequest = (DataSM)pdu;
                                DataSMResp dataSmResp = new DataSMResp();
                                DataAccess dataAccess = new DataAccess();

                                  ByteBuffer messagePayload = incomingrequest.getMessagePayload(); //USSD String encoded according to data_coding
                                  Address sourceAddr = incomingrequest.getSourceAddr();
                                  msisdn = sourceAddr.getAddress();
                                  ton_source = sourceAddr.getTon();
                                  npi_source = sourceAddr.getNpi();

                                  serviceType = incomingrequest.getServiceType();
                                  esmClass = incomingrequest.getEsmClass();

                                  int commandID = pdu.getCommandId();
                                  String ussdStringInHex = messagePayload.getHexDump();
                                  String payload = incomingrequest.getBody().getHexDump();

                                  dataCoding = incomingrequest.getDataCoding();


                                  String ussdString = utils.convertHexToString(ussdStringInHex);
                                  //short sessionInfo = incomingrequest.getItsSessionInfo();
                                  String data = incomingrequest.debugString();
                                   sessionId = incomingrequest.getReceiptedMessageId(); //Session Identifier
                                   String msg = dataAccess.LogSession(msisdn, sessionId);
                                   System.out.println(msg);

                                  System.out.println("Pay load : " +payload);
                                  System.out.println("MSISDN : " + msisdn);
                                  System.out.println("USSD String In Hexadecimal : " +ussdStringInHex);
                                  System.out.println("USSD String : " + ussdString);
                                  System.out.println("Debug String  : " +data);
                                  System.out.println("SessioID : " + sessionId);
                                  System.out.println("ton source : " + ton_source);
                                  System.out.println("npi : " + npi_source);
                                  System.out.println("service type : " + serviceType);
                                  System.out.println("esm class : " + esmClass);
                                  System.out.println("data coding : " + dataCoding);

                                 String ServiceCode = "*309#";//LoadProperties.ll.FIRSTSERVICECODE;

                                 gateway.session.getConnection();
                                 Response response = ((Request)pdu).getResponse();
                                 gateway.session.respond(response);
                                 ProcessRequest(msisdn, sessionId, ServiceCode,commandID);
                                }catch(Exception ioex){
                                    ioex.printStackTrace();
                                }

                            }else
                                if(pdu instanceof Outbind){
                                    Outbind response = (Outbind)pdu;
                                }else
                                    if(pdu instanceof GenericNack){
                                        GenericNack response = (GenericNack)pdu;
                                    }
            else{
               System.out.printf("No handler for {} found.", pdu.getClass().getName());
            }
        }catch(NullPointerException ex){
             ex.printStackTrace();
        }
    }


protected void Process(String msisdn,String sessionID,String ServiceCode,int ussd_service_op,String Url){
     System.out.println("USSD String : " +ServiceCode);
     
     SessionMOHandler shm = new SessionMOHandler(msisdn, esme,Url, gateway);
     shm.setMsisdn(msisdn);
     shm.setSessionID(sessionID);
     shm.setServiceCode(ServiceCode);
     shm.setUrl(Url);

     System.out.println("Saving Url into property file : " +"http://localhost/~din5/ussd/menu.php?");
     sessionMonitor.writeSessionIdToFile(sessionID, "http://localhost/~din5/ussd/menu.php?",msisdn);
     esme.sessionList.put("sessiondata", shm);

     shm.MOProcess(sessionID, ServiceCode,ussd_service_op);
}

protected void firstURL(String msisdn,String sessionID,String UssdString,int ussd_service_op){
     System.out.println("USSD String : " +UssdString);
     //String walletUrl = "http://localhost/test_app.php?";
     //String walletUrl = "http://localhost/USSDMenu/USSDNavigator.php?";
     SessionMOHandler shm = new SessionMOHandler(msisdn, esme, "http://localhost/~din5/ussd/menu.php?", gateway);
     shm.setMsisdn(msisdn);
     shm.setSessionID(sessionID);
     shm.setUssdString(UssdString);
     shm.setUrl("http://localhost/~din5/ussd/menu.php?");

     System.out.println("Saving Url into property file : " + "http://localhost/~din5/ussd/menu.php?");
     sessionMonitor.writeSessionIdToFile(sessionID, "http://localhost/~din5/ussd/menu.php?",msisdn);
     //DataAccess dBAccess = new DataAccess();
     esme.sessionList.put("sessiondata", shm);

     shm.MOProcess(sessionID, UssdString,ussd_service_op);
}
protected void secondURL(String msisdn,String sessionID,String UssdString,int ussd_service_op){
     System.out.println("USSD String  : " +UssdString);
     //String walletUrl = "http://localhost/test_app.php?";
     //String walletUrl = "http://localhost/USSDMenu/USSDNavigator.php?";
     SessionMOHandler shm = new SessionMOHandler(msisdn, esme, "http://localhost/~din5/ussd/menu.php?", gateway);
     shm.setMsisdn(msisdn);
     shm.setSessionID(sessionID);
     shm.setUssdString(UssdString);
     shm.setUrl("http://localhost/~din5/ussd/menu.php?");

     System.out.println("Saving Url into property file : " + "http://localhost/~din5/ussd/menu.php?");
     sessionMonitor.writeSessionIdToFile(sessionID, "http://localhost/~din5/ussd/menu.php?",msisdn);
     //DataAccess dBAccess = new DataAccess();
     esme.sessionList.put("sessiondata", shm);

     shm.MOProcess(sessionID, UssdString,ussd_service_op);
}
protected void thirdURL(String msisdn,String sessionID,String ServiceCode,int ussd_service_op){
     System.out.println("USSD String : " +ServiceCode);
     //String uwiniwin = "http://localhost/uwin.php?";
     SessionMOHandler shm = new SessionMOHandler(msisdn, esme, "http://localhost/~din5/ussd/menu.php?", gateway);
     shm.setMsisdn(msisdn);
     shm.setSessionID(sessionID);
     shm.setServiceCode(ServiceCode);
     shm.setUrl("http://localhost/~din5/ussd/menu.php?");

     System.out.println("Saving Url into property file : " +"http://localhost/~din5/ussd/menu.php?");
     sessionMonitor.writeSessionIdToFile(sessionID, "http://localhost/~din5/ussd/menu.php?",msisdn);
     esme.sessionList.put("sessiondata", shm);

     shm.MOProcess(sessionID, ServiceCode,ussd_service_op);
}

protected void ProcessRequest(String msisdn,String sessionID,String UssdString,int ussd_service_op){
    //if(esme.sessionList.containsKey("sessiondata")){
    if (esme.validateSession(sessionID)) {
        try {
            esme.sessionList.get("sessiondata").MOProcess(sessionID, UssdString, ussd_service_op);
        } catch (NullPointerException ex) {
            try {
                String theMessage = "the service code you dial is incorrect, please contact the customer care for the correct service code";
                esme.DataSM(msisdn, theMessage, sessionID, Constants.USSR_REQUEST);
            } catch (UnsupportedEncodingException ex1) {
                ex1.getMessage();
            } catch (WrongLengthOfStringException ex1) {
                ex1.getMessage();
            } catch (IOException ex1) {
                ex1.getMessage();
            }
        }
        //esme.sessionMonitor.MOProcess(sessionID, UssdString, ussd_service_op);

//        SessionController controller = new SessionController();
//         controller.MOProcess(sessionID, UssdString, ussd_service_op);
    } else {

        if (UssdString.equalsIgnoreCase("*309#")) 
        {
            System.out.println("Calling First ussd : " + UssdString);
            firstURL(msisdn, sessionID, UssdString, ussd_service_op);
        }
         else if (UssdString.equalsIgnoreCase("*309#")) {
            System.out.println("Calling Second ussd service : " + UssdString);            
            secondURL(msisdn, sessionID, UssdString, ussd_service_op);
        } else 
         if (UssdString.equalsIgnoreCase("*309#")) {
            System.out.println("Calling Third ussd : " + UssdString);            
            thirdURL(msisdn, sessionID, UssdString, ussd_service_op);
        }else {
            System.out.println("Calling invalid service code : " + UssdString);
            try {
                String theMessage = "the service code you dial is incorrect, please contact the customer care for the correct service code";
                esme.DataSM(msisdn, theMessage, sessionID, Constants.USSR_REQUEST);
            } catch (IOException ex) {
                Logger.getLogger(USSDGateway.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrongLengthOfStringException ex) {
                ex.printStackTrace();
            }
        }
    }

}
    
    public class EnquiryLinkRequest extends Thread{
        @Override
        public void run(){
//             Calendar cal = Calendar.getInstance();
//       	     cal.add(Calendar.SECOND, WAIT_LENGTH_SECS);
//            nextHandShakeTime = cal.getTimeInMillis();
            while(keepRunning){
                if(nextHandShakeTime < Calendar.getInstance().getTimeInMillis()){
                    esme.getMessage("We are sending an HandShake request");
                    try{

                    esme.getMessage(message);
                    EnquireLinkResp response = null;
                    response = esme.session.enquireLink();
                    System.out.println(response.debugString());
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    try{
                        System.out.println("Thread is going to sleep for 3minutes");
                        TimeUnit.MILLISECONDS.sleep(WAIT_LENGTH_SECS);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private static String byteArrayToString(byte[] b){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<b.length; i++)
            sb.append((char)b[i]);
        return sb.toString();
    }

    public void stopAllRunningThreads(){
        for(Thread thread : Thread.getAllStackTraces().keySet()){
            if(thread.getState() == Thread.State.RUNNABLE){
                thread.interrupt();
            }
        }
    }
    @Override
    public void run()
  {
    System.out.println("Trying to read input PDU from SMPP Gateway");
   
    //while (session.isBound()){
    while (keepRunning){
      try
      {
        System.out.println("Welcome to QUICKLOTTO");
        EnquireLinkResp first_enquiryLink = esme.session.enquireLink();        
        if(first_enquiryLink==null){
            throw new IOException("Enquiry Link is null");
        }
        System.out.println("First Enquiry Link response : " +first_enquiryLink.debugString());
        
          this.pdu = this.esme.session.receive();
         // System.out.println("Pdu : " + this.pdu.debugString());
         // (this.pdu.getCommandId() != 21) ||
          if ((this.pdu != null)) {
            System.out.println("Received PDU " + this.pdu.debugString());
            System.out.println("CommandID : " + this.pdu.getCommandId());
            if (this.pdu.getCommandId() == 21) {
              System.out.println("receiving enquiry link response : " +pdu.debugString());
             // this.esme.enquiryLink();
            }
            if(pdu instanceof EnquireLink){
                System.out.println("Enquiry Link received successfully");
                EnquireLink request = (EnquireLink)pdu;
                System.out.println("received request : " +request.debugString());
                Response response = ((Request)this.pdu).getResponse();
                esme.session.respond(response);
            }
            else if(pdu instanceof EnquireLinkResp){
                System.out.println("I  Got enquiry link response pdu");
                EnquireLinkResp response = (EnquireLinkResp)pdu;
                System.out.println("response : " + response.debugString());
            }
            else if ((this.pdu instanceof BindTransciever)) {
              try {
                System.out.println("BindTransceiverResp Pdu Received from SMPP GATEWAY");
                BindTranscieverResp response = (BindTranscieverResp)this.pdu;
                System.out.println("Bind Transceiver Debug String : " + response.debugString());
                ByteBuffer buffer = response.getData();
                System.out.println("BindTransmitterResp HexadecimalDump : " + buffer.getHexDump());
                this.shortMsg = new ShortMessage(0, 16);
                try {
                  this.shortMsg.setData(buffer);
                } catch (NotEnoughDataInByteBufferException ex) {
                  //ex.printStackTrace();
                    System.out.println("***************NotEnoughDataInByteBufferException : "+ex.getMessage()+"***************");
                } catch (TerminatingZeroNotFoundException ex) {
                  //ex.printStackTrace();
                    System.out.println("***************TerminatingZeroNotFoundException : "+ex.getMessage()+"***************");
                }
                String msg = this.shortMsg.getMessage();
                System.out.println("BindTransceiverResp Message : " + msg.toString());
              }
              catch (ValueNotSetException ex) {
                //ex.printStackTrace();
                  System.out.println("***************ValueNotSetException : "+ex.getMessage()+"***************");
              }
            }
            else if(pdu instanceof BindRequest){
                System.out.println("Bind response received");
            }
            else if ((this.pdu instanceof BindResponse)) {
              BindResponse response = (BindResponse)this.pdu;
              this.systemId = response.getSystemId();
              System.out.println("SystemId : " + this.systemId);
              System.out.println("Bind Response Debug String : " + response.debugString());
            }
            
            else if ((this.pdu instanceof BindTransmitterResp)) {
                System.out.println("The Received Pdu is an instance of BindTransmitterResponse");
              }
              else if ((this.pdu instanceof EnquireLink)) {
                System.out.println("enquiry Link Here");

              }
              else if ((this.pdu instanceof DataSM)){
                
                    System.out.println("Instance of DataSM pdu received from SMPP Gateway");
                  DataSM incomingrequest = (DataSM)this.pdu;                  
                  ByteBuffer messagePayload = incomingrequest.getMessagePayload();
                  Address sourceAddr = incomingrequest.getSourceAddr();
                  this.msisdn = sourceAddr.getAddress();
                  this.ton_source = sourceAddr.getTon();
                  this.npi_source = sourceAddr.getNpi();
                  this.serviceType = incomingrequest.getServiceType();
                  this.esmClass = incomingrequest.getEsmClass();
                  String ussdStringInHex = messagePayload.getHexDump();
                  String payload = incomingrequest.getBody().getHexDump();
                  this.dataCoding = incomingrequest.getDataCoding();
                  String ussdString = this.utils.decode(ussdStringInHex);
                  int commandID = this.pdu.getCommandId();
                  String data = incomingrequest.debugString();
                  this.sessionId = incomingrequest.getReceiptedMessageId();
                  this.ussd_service_op = incomingrequest.getUssdServiceOp();
                  //byte messageState = incomingrequest.getMessageState();
                   esme.SourceSubAddr = incomingrequest.getSourceSubaddress();
                   String sourceSubAddress = incomingrequest.getSourceSubaddress().getHexDump();

                   esme.destSubAddr = incomingrequest.getDestSubaddress();

                   String destSubAddress = incomingrequest.getDestSubaddress().getHexDump();

                   byte[] msgBuffer = messagePayload.getBuffer();

                   String stringBuffer = new String(msgBuffer);
                                  
                  System.out.println("StringBuffer : " +stringBuffer);
                  System.out.println("Pay load : " + payload);
                  System.out.println("MSISDN : " + this.msisdn);
                  System.out.println("USSD String In Hexadecimal : " + ussdStringInHex);
                  System.out.println("USSD String : " + ussdString);
                  System.out.println("Debug String  : " + data);
                  System.out.println("SessioID : " + this.sessionId);
                  System.out.println("ton source : " + this.ton_source);
                  System.out.println("npi : " + this.npi_source);
                  System.out.println("service type : " + this.serviceType);
                  System.out.println("esm class : " + this.esmClass);
                  System.out.println("data coding : " + this.dataCoding);
                  System.out.println("CommandID : " + commandID);
                  System.out.println("ussdServiceOp : " + this.ussd_service_op);
                  System.out.println("Source SubAddress HexDump : " + sourceSubAddress);
                  System.out.println("Source SubAddress : " + utils.convertHexToString(sourceSubAddress));
                  System.out.println("Destination SubAddress HexDump : " + destSubAddress);
                  System.out.println("Destination SubAddress : " + utils.convertHexToString(destSubAddress));

                  Response response = ((Request)this.pdu).getResponse();
                  this.esme.session.respond(response);
                  ProcessRequest(this.msisdn, this.sessionId, ussdString, this.ussd_service_op);
               
                      EnquireLinkResp response1 = this.esme.session.enquireLink();
                      System.out.println("Receiving Enquiry Link Response : " +response1.debugString());
                  
            }
          }
          else
          {
            System.out.println("No PDU received at this time");

            System.out.println("esme is sending new enquirylink request to the SMPP Server");
            EnquireLink request = new EnquireLink();
            EnquireLinkResp response = null;
            response = this.esme.session.enquireLink(request);
            if(response!=null){ System.out.println(response.debugString());}
            Thread.sleep(800L);
          }

      }

              catch (ValueNotSetException ex) {
                   System.out.println("ValueNotSetException : " +ex.getMessage());
                } catch (TimeoutException ex) {
                    System.out.println("TimeoutException : " + ex.getMessage());
                } catch (PDUException ex) {
                    System.out.println("PDUException : " +ex.getMessage());
                } catch (IOException ex) 
                {
                    System.out.println("IOException : " +ex.getMessage());
                    ex.printStackTrace();
                    if(connection!=null){
                    try {
                        System.out.println("Closing connection to the SMPP Gateway");
                        connection.close();
                    } catch (IOException ex1) {
                        ex1.getMessage();
                    }
                    }
                    if(session!=null){
                    try {
                        session.close();
                    } catch (IOException ex1) {
                        System.out.println("Closing session to the SMPP Gateway");
                        ex1.getMessage();
                    } catch (WrongSessionStateException ex1) {
                        System.out.println("Closing session to the Gateway");
                        ex1.getMessage();
                    }
                    }
                    
                    keepRunning = false;
                    System.out.println("Reconnecting to the SMPP Gateway");
//                    AutoReconnectGateway(); //////HAD BEEN COMMENTED BEFORE
                    //his.interrupt();//COMENTED BY LAA TO ACHIEVE RECONNET
                    //esme.Start();
                    esme.starter();
                } catch (WrongSessionStateException ex) {
                    System.out.println("WrongSessionStateException : " +ex.getMessage());
                }
      catch (NotSynchronousException ex) {
                   System.out.println("NotSynchronousException : " +ex.getMessage());
                }
      catch (InterruptedException ex) {
           System.out.println("InterruptedException : " +ex.getMessage());
                    }
      
    }

    System.out.println(".......ESME Not connected to the Gateway........");
    esme.starter();
  }
                 

}

