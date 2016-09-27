/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.smpp.util.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.smpp.*;
import org.smpp.TCPIPConnection;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.*;
import org.smpp.pdu.PDUException;
import org.jsmpp.session.SMPPServerSession;
import org.jsmpp.session.SMPPServerSessionListener;
import org.jsmpp.session.ServerMessageReceiverListener;
import org.smpp.pdu.tlv.WrongLengthException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import org.smpp.pdu.PDU;


/**
 *
 * @author Ahmed
 */
public class ESMEDaemon extends SessionMonitor {
   
    public boolean rebind=false;    
    public  String IPAddress = "41.203.65.15";//LoadProperties.ll.IPADDRESS;//GLO IPAddress    
    public   int port = 2054;//LoadProperties.ll.PORT;//GLO port
    private final byte interfaceVersion = 34;
    private final int keepalive = 20;
    private final int idletimeout = 90;
    private final int instance = 1;
    public  String systemId = "crosstee";//GLO systemId    
    public final String password = "@rosstee";//LoadProperties.ll.PASSWORD;//GLO password    
    //Invalid System Type, Error Code : 83
    public final String systemtype ="";// LoadProperties.ll.SYSTEMTYPE;//GLO systemtype
    public final String addressrange = "145";
    private String msisdn;
   // private  TimeFormatter tf;
    private final int timeout = 600;//LoadProperties.ll.TIMEOUT;//24*60*60*1000;
    //private BindResponse response = null;
    //private final Bind request = null;
    //public Session session = null;
    //private long receiveTimeout = Data.RECEIVE_BLOCKING;
    //private PDU pdu = null;
    //private DeliverSM deliver = null;
    private CallableStatement callablestatement;
    private String sourceMsisdn;
    private String destinationMsisdn;
    //private  MessageClass msgClass;
    //private  SMPPSession smppSession;
    //private AutoReconnectGateway autoreconnec;
    //private BindParameter bindparameter;
    private ServerSocket serverSocket;
    //private SMPPServerSession smppServerSession;
    private boolean isConnected;
    private USSDGateway gateway;
    private String messageId;
    //private AbsoluteTimeFormatter timeFormatter;
    private String sessionId;
    private String messageString;
    private String circleId;
    private String bindResponse;
    private Socket socket;
    public HashMap<String,SessionController> sessionList;
    public Vector<SessionController> clientList;
    private static String message;
    //private AutoReconnectGateway reconnection;
    private  org.smpp.Session session1;
    private org.smpp.ServerPDUEventListener listener;
    private boolean asynchronous = false;
    private TCPIPConnection connection = null;
    public  boolean bound = false;
    public  org.smpp.Session session;
    private long receiveTimeout = Data.RECEIVE_BLOCKING;
    private SMPPPDUEventListener pduListener = null;   
    String systemType = "";
    private String serviceType = "USSD";
    private Address sourceAddress = new Address();
    //Destination address of this USSD session(may include sub-address digit) In first application initiating session set to subscriber address,otherwise set to NULL(Unknown)
    private Address destAddress = new Address();
    private String scheduleDeliveryTime = "";
    private String validityPeriod = "";
    private String shortMessage = "";
    private int numberOfDestination = 1;
   // private String messageId = "";
    private int esmClass = 1;//Indicate Message Mode and Message Type (0x00 shall be used)
    private byte protocolId = 0;//Protocol Identifier (0x00 shall be used)
    private byte priorityFlag = 0;//Designates the priority level of the message (0x00 shall be used)
    private byte registeredDelivery = 0;//Indicator to signify if an SMSC delivery receipt or an SME acknowledgement is required. (0x00 shall be used).
    private byte replaceIfPresentFlag = 0;//Flag indicating if submitted message  should replace an existing message.(0x00 shall be used)
    private byte dataCoding = 0x00;//Defines the encoding schemeof the USSD operation user data(short_message). IA5,7-bit character set(English),8-bit binary and 16 bit(UCS2) coding schemes are supported
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
    private String ussd_service_op = "PSSD";
    /*The sessionID between  the USSD GW and the USSD application. When the USSD application starts a USSD session
     (by issuing a USSR request or USSN request for example) it does not include this parameter and receives an allocated
     sessionid from the USSD GW in the submit_sm_resp PDU*/
    private String ussd_session_id;
    //language indicator
    private byte lagguage_indicator = 0x01;
    private long session_init_timer = 0;
    private long enquiry_link_timer = 60;
    private long inactivity_timer = 60;
    private long response_timer = 1800;
    private ShortMessage shortMsg = null;
    private SMPPServerSessionListener sessionListener = null;
    private SMPPServerSession serverSession = null;
    private ServerMessageReceiverListener  messageReceiverListener;
    private boolean keepRunning = false;
    private long nextHandShakeTime;
    private static final int WAIT_LENGTH_SECS = 3;
    private Charset charset = Charset.forName("UTF-8");
    private CharsetEncoder encoder = charset.newEncoder();
    private CharsetDecoder decoder = charset.newDecoder();
    private byte destBearerType = 0x04;
    public ByteBuffer SourceSubAddr;
    public ByteBuffer destSubAddr;
    public SessionMonitor sessionMonitor;
    
    
    private Utilities utils;

    public ESMEDaemon(){

        sessionList = new HashMap<String,SessionController>();
        clientList = new Vector<SessionController>();
        sessionMonitor = new SessionMonitor();
        utils = new Utilities();
    }
    /**
     * @param args the command line arguments
     */

    protected void Start(){
        try
       {            
      if(!keepRunning){
            connection = new TCPIPConnection(IPAddress, port);            
            connection.setReceiveTimeout(timeout);
            //connection.setCommsTimeout(24*60*60*1000);
            session = new org.smpp.Session(connection);            
            connection.accept();            
            gateway = new USSDGateway(connection,session, this);
            keepRunning = true;
            gateway.start();            
      }            
    }catch(Exception ex){
        System.out.println("Application not connected to the remote server retry after 0.5  minutes");
            try {
                ex.printStackTrace();
                Thread.sleep(500L);
                Start();
            } catch (InterruptedException ex1) {
                ex1.printStackTrace();
            }
    }
    }

    protected void bind(){
        try{

            org.smpp.pdu.BindRequest request = null;
            BindResponse response = null;

            request = new BindTransciever();           
            
            //set value
            request.setSystemId(systemId);
            request.setPassword(password);
            request.setSystemType(systemtype);
            request.setInterfaceVersion((byte)0x34);
            request.setAddressRange(addressrange);

            System.out.println("systemId:"+systemId);
            System.out.println("password:"+password);
            System.out.println("systemtype:"+systemtype);
            System.out.println("addressrange:"+addressrange);


            //Send the request
            System.out.println("Bind request(bind) : " + request.debugString());
            //response = session.bind(request);
            response = session.bind(request, listener);
            
            System.out.println("Bind response(bind) : " + response.debugString());

            //No Error
            if(response.getCommandStatus() == Data.ESME_ROK){
                //bound = true;
                //new EnquiryLinkRequest().start();
                System.out.println("CommandID : " +response.getCommandId());
                System.out.println("Bind Successful");                
            }
            //System Error
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
        }

               catch (UnknownCommandIdException ex) {
                    System.out.println("UnknownCommandIdException : " +ex.getMessage());
                } catch (TimeoutException ex) {
                    System.out.println("TimeoutException : " +ex.getMessage());
                }  catch (PDUException ex) {
                    System.out.println("PDUException : " +ex.getMessage());
                } catch (IOException ex) {
                    System.out.println("Bind Operation failed IO : " + ex.getMessage() );
                    System.out.println("IOException : " +ex.getMessage());
                    ex.printStackTrace();
                    keepRunning = false;
//                    USSDGateway.currentThread().interrupt();
                    try {
                System.out.println("Sleeping At IOException");
                Thread.currentThread().sleep(100000l);
                System.out.println("Woke up At IOException");
            } catch (InterruptedException iex) {
                Logger.getLogger(ESMEDaemon.class.getName()).log(Level.SEVERE, null, iex);
            }
                    Start();
                }
                 catch (StackOverflowError sof) {
                    System.out.println("Bind Operation failed SOF: " + sof.getMessage() );
                    System.out.println("IOException : " +sof.getMessage());
                    keepRunning = false;
                    USSDGateway.currentThread().interrupt();
            try {
                System.out.println("Sleeping At StackOverFLow");
                Thread.currentThread().sleep(300000l);
                System.out.println("Woke up At StackOverFLow");
            } catch (InterruptedException ex) {
                Logger.getLogger(ESMEDaemon.class.getName()).log(Level.SEVERE, null, ex);
            }
                    Start();
                }
               catch (WrongSessionStateException ex) {
                Logger.getLogger(ESMEDaemon.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }

    protected PDU receive() throws NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException{

        PDU pdu = null;

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
            //ex.printStackTrace();
            System.out.println("***************IOException***************");
        }catch(ValueNotSetException vnsex){
            //vnsex.printStackTrace();
            System.out.println("***************ValueNotSetException***************");
        }catch(WrongLengthOfStringException wlosex){
            //wlosex.printStackTrace();
            System.out.println("***************WrongLengthOfStringException***************");
        }catch(PDUException pduex){
            //pduex.printStackTrace();
            System.out.println("***************PDUException***************");
        }

        return pdu;
    }

protected void received(PDU pdu) throws ValueNotSetException, PDUException, NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException{
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

                                 String ServiceCode ="*309#";// LoadProperties.ll.FIRSTSERVICECODE;

                                 gateway.session.getConnection();
                                 Response response = ((Request)pdu).getResponse();
                                 gateway.session.respond(response);
                                 //ProcessRequest(msisdn, sessionId, ServiceCode);
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

protected EnquireLinkResp enquiryLink(){

    org.smpp.pdu.EnquireLinkResp response = null;

        try{
            org.smpp.pdu.EnquireLink request = new org.smpp.pdu.EnquireLink();
            

            System.out.println("EnquiryLink request : " +request.debugString());
            
            response = session.enquireLink();

            System.out.println("EnquiryLink response : " +response.debugString());

            if(response.getCommandStatus() == Data.ESME_ROK){
              System.out.println("EnquiryLink Success");
              System.out.println("EnquiryLink response : " +response.debugString());
            }else
               if(response.getCommandStatus() == Data.ESME_RENQCUSTFAIL){
                  System.out.println("Enquiry Link Failed : " + response.getCommandStatus());
               }else{
                System.out.println("Status : " +response.getCommandStatus());
               }
            try {
                Thread.sleep(990);
                System.out.println("Thread is going to sleep for 5seconds");
                enquiryLink();
            } catch (InterruptedException ex) {
                System.out.println("Interrupted Exception : " + ex.getMessage());
            }catch(NullPointerException ex){
                System.out.println("Line 554: stop enquiry link for datasm pdu  " );
                if(!connection.isOpened()){
                  System.out.println(" reconnecting the ESME to the Gateway  " );
                   Start();
                }else{
                    System.out.println("The connection is still open");
                }
            }
            
            
        }catch(TimeoutException ex){
            System.out.println("Enquire Link operation failed. " );
            System.out.println("***************TimeoutException***************");
        }catch(PDUException ex){
            System.out.println("PDUException : " );
            System.out.println("***************PDUException***************");
        }catch(IOException ex){
            System.out.println("IOException : " );
            System.out.println("Line 561 stop enquiry link for datasm pdu  " );
            if(!connection.isOpened()){
                  System.out.println(" reconnecting the ESME to the Gateway  " );
                  //keepRunning = false;
                  //USSDGateway.currentThread().interrupt();
                   Start();
                }else{
                   System.out.println("The connection is still open");
                }
        }catch(WrongSessionStateException ex){
            System.out.println("WrongSessionStateException : " +ex);
        }catch(NullPointerException nullex){
            System.out.println("Line 571 stop enquiry link for datasm pdu  " );
            if(!connection.isOpened()){
                  System.out.println(" reconnecting the ESME to the Gateway  " );
                  //keepRunning = false;
                  //USSDGateway.currentThread().interrupt();
                  Start();
                }else{
                  System.out.println("The connection is still open");
                }
        }

        return response;

    }


protected String DataSM(String msisdn, String message, String sessionId,int UssdServiceOp) throws UnsupportedEncodingException, WrongLengthOfStringException, IOException
  {

     String encodedMsg = utils.convertHexToString(utils.encode(message));
    DataSM dataSm = new DataSM();

    DataSMResp dataSmResp = null;    

    ByteBuffer messagePayload = new ByteBuffer();   
    messagePayload.appendString(encodedMsg,encodedMsg.length(),Data.ENC_ISO8859_1);        
    
    try
    {
      dataSm.setServiceType(Constants.SERVIVETYPE_USSD);
      dataSm.setDestAddr((byte)this.ton_destination, (byte)this.npi_destination, msisdn);      
      dataSm.setMessagePayload(messagePayload);
      dataSm.setReceiptedMessageId(sessionId);
      dataSm.setEsmClass((byte)esmClass);
      dataSm.setRegisteredDelivery(this.registeredDelivery);
      dataSm.setDataCoding(dataCoding);
      dataSm.setUssdServiceOp((byte)UssdServiceOp);
      dataSm.setLanguageIndicator(lagguage_indicator);
      dataSm.setDestBearerType(destBearerType);
      dataSm.setQosTimeToLive(24*60*60*1000);
      //dataSm.set
      
      
      
      System.out.println("Sending Application Menu to the subscriber");

      System.out.println("SessionID : " + sessionId);
      System.out.println("Service Type : " + this.serviceType);
      System.out.println("MSISDN : " + msisdn);
      System.out.println("ESMCLASS : " + this.esmClass);
      System.out.println("registeredDelivery : " + this.esmClass);
      System.out.println("Data Coding : " + this.dataCoding);
      System.out.println("Destination ton  : " + this.ton_destination);
      System.out.println("Destination npi  : " + this.npi_destination);
      System.out.println("UssdServiceOp : " + dataSm.getUssdServiceOp());
      System.out.println("Message   : " + message);

      System.out.println("Debug String : " +dataSm.debugString());

      dataSmResp = this.session.data(dataSm);
      String responseMsg = dataSmResp.getData().getHexDump();

      System.out.println("Request Debug String : " + dataSm.debugString());
      System.out.println("Response Debug String : " + dataSmResp.debugString());

      System.out.println("MessageID : " + dataSmResp.getMessageId());
      System.out.println("Response CommandID : " + dataSmResp.getCommandId());
      System.out.println("Message Payload In Hexadecimal : " + responseMsg);
      System.out.println("Message Payload In Readable String : " + this.utils.convertHexToString(responseMsg));

      if (dataSmResp.getCommandStatus() == 11) {
        System.out.println("Invalid destination address,Error Code : " + dataSmResp.getCommandStatus());
      }
      else if (dataSmResp.getCommandStatus() == 81) {
        System.out.println("Invalid destination npi,Error Code : " + dataSmResp.getCommandStatus());
      }
      else if (dataSmResp.getCommandStatus() == 80) {
        System.out.println("Invalid destination ton,Error Code : " + dataSmResp.getCommandStatus());
      }
      else if (dataSmResp.getCommandStatus() == 50) {
        System.out.println("Invalid parameter,Error Code : " + dataSmResp.getCommandStatus());
      }
      else if (dataSmResp.getCommandStatus() == 21)
        System.out.println("Invalid service type,Error Code : " + dataSmResp.getCommandStatus());
      else
        System.out.println("Command Status : " + dataSm.getCommandStatus());
    }
     catch (WrongLengthOfStringException ex){
      //ex.printStackTrace();
         System.out.println("***************WrongLengthOfStringException***************");
    } catch (WrongLengthException ex) {
      //ex.printStackTrace();
        System.out.println("***************WrongLengthException***************");
    } catch (ValueNotSetException ex) {
      ex.printStackTrace();
        System.out.println("***************ValueNotSetException***************");
    } catch (TimeoutException ex) {
      //ex.printStackTrace();
        System.out.println("***************TimeoutException***************");
    } catch (PDUException ex) {
      //ex.printStackTrace();
        System.out.println("***************PDUException***************");
    } catch (IOException ex) {
        System.out.println("**********IOException : " +ex.getMessage()+"**********");
      if(connection == null){
          System.out.println(" reconnecting the ESME to the Gateway  " );
           Start();
         }else{
          System.out.println("The connection is still open");
         }
    } catch (WrongSessionStateException ex) {
      //ex.printStackTrace();
        System.out.println("***************WrongSessionStateException***************");
    } catch (NullPointerException ex) {
        System.out.println("**********IOException : " +ex.getMessage()+"**********");
      if(connection == null){
          System.out.println(" reconnecting the ESME to the Gateway  " );
           Start();
         }else{
          System.out.println("The connection is still open");
      }
    }

    return dataSmResp.getMessageId();
  }

public String SubmitSM(){
    String messageId = null;
        try {
            message = "welcome to "+"QUICKLOTTO";//LoadProperties.ll.PRODUCTNAME;
            
            SubmitSM submitSM = new SubmitSM();
            submitSM.setDestAddr((byte) ton_destination, (byte) npi_destination, "2347055932235");
            submitSM.setDataCoding(dataCoding);
            submitSM.setEsmClass((byte)esmClass);
            submitSM.setShortMessage(message, Data.ENC_ASCII);
            submitSM.setServiceType("SMS");
            submitSM.setProtocolId(protocolId);
	    submitSM.setPriorityFlag(priorityFlag);
	    submitSM.setRegisteredDelivery(registeredDelivery);
            submitSM.setSmDefaultMsgId(smDefaultMsgId);
            submitSM.setScheduleDeliveryTime(scheduleDeliveryTime);
	    submitSM.setValidityPeriod(validityPeriod);
            submitSM.setReplaceIfPresentFlag(replaceIfPresentFlag);

           messageId = session.submit(submitSM).getMessageId() ;

        } catch (ValueNotSetException ex) {
            Logger.getLogger(ESMEDaemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(ESMEDaemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PDUException ex) {
            Logger.getLogger(ESMEDaemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ESMEDaemon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WrongSessionStateException ex) {
            Logger.getLogger(ESMEDaemon.class.getName()).log(Level.SEVERE, null, ex);
        }

    return messageId;
}

//public void DataSM(String msisdn,String message,String sessionId) throws UnsupportedEncodingException{
//
//    DataSM dataSm = new DataSM();
//    //SubmitSM submitSm = new SubmitSM();
//    //submitSm.setUssdServiceOp(esmClass);
//    DataSMResp dataSmResp = null;
//
//    String msg = "Welcome to LAA Nigeria Limited, merry xmas and happy New Year in advance";
//
//    //String encodeMsg = utils.stringTo7bit(message);
//
//    ByteBuffer messagePayload = new ByteBuffer(message.getBytes());
//
//    try{
//
//        dataSm.setServiceType(serviceType);
//        dataSm.setDestAddr((byte)ton_destination,(byte)npi_destination, msisdn);
//        dataSm.setMessagePayload(messagePayload);
//        dataSm.setReceiptedMessageId(sessionId);
//        dataSm.setEsmClass((byte)esmClass);
//        dataSm.setRegisteredDelivery(registeredDelivery);
//        dataSm.setDataCoding((byte) dataCoding);
//        //dataSm.setDataCoding(Alphabet.ALPHA_8_BIT.MASK_ALPHABET);
//        dataSm.setUssdServiceOp((byte)Constants.USSR_REQUEST);
//        //dataSm.setData(messagePayload);
////        dataSm.setMsMsgWaitFacilities(ton_source);
////        dataSm.setMsValidity(ton_source);
////        dataSm.setDestAddrSubunit(ton_source);
//
//        System.out.println("Sending Application Menu to the subscriber");
//
//        System.out.println("SessionID : " +sessionId);
//        System.out.println("Service Type : " +serviceType);
//        System.out.println("MSISDN : " +msisdn);
//        System.out.println("ESMCLASS : " +esmClass);
//        System.out.println("registeredDelivery : " +registeredDelivery);
//        System.out.println("Data Coding : " +dataCoding);
//        System.out.println("Destination ton  : " +ton_destination);
//        System.out.println("Destination npi  : " +npi_destination);
//        System.out.println("UssdServiceOp : " +dataSm.getUssdServiceOp());
//        System.out.println("Message   : " +message);
//        System.out.println("Test Message : " +msg);
//
//
//        dataSmResp = session.data(dataSm);
//        //String responseMsg = dataSmResp.getData().getHexDump();
//        //byte failure_reason = dataSmResp.getDeliveryFailureReason();
//        //String response_meaning = dataSmResp.getAdditionalStatusInfoText();
//
//        System.out.println("Request Debug String : " +dataSm.debugString());
//        //System.out.println("Response Debug String : " +dataSmResp.debugString());
//        //System.out.println("Response String : " +response_meaning);
//        //System.out.println("MessageID : " +dataSmResp.getMessageId());
//        //System.out.println("Response CommandID : " +dataSmResp.getCommandId());
//
//
//
//
////        if(dataSmResp.getCommandStatus() == Data.ESME_RINVDSTADR){
////            System.out.println("Invalid destination address,Error Code : " + dataSmResp.getCommandStatus());
////        }
////        else if(dataSmResp.getCommandStatus() == Data.ESME_RINVDSTNPI){
////            System.out.println("Invalid destination npi,Error Code : " + dataSmResp.getCommandStatus());
////        }
////        else if(dataSmResp.getCommandStatus() == Data.ESME_RINVDSTTON){
////            System.out.println("Invalid destination ton,Error Code : " + dataSmResp.getCommandStatus());
////        }
////        else if(dataSmResp.getCommandStatus() == Data.ESME_RINVPARAM){
////            System.out.println("Invalid parameter,Error Code : " + dataSmResp.getCommandStatus());
////        }
////        else if(dataSmResp.getCommandStatus() == Data.ESME_RINVSERTYP){
////            System.out.println("Invalid service type,Error Code : " + dataSmResp.getCommandStatus());
////        }else{
////            System.out.println("Command Status : " +dataSm.getCommandStatus());
////        }
//
//    }catch(WrongLengthOfStringException ex){
//        ex.printStackTrace();
//    }catch(WrongLengthException ex){
//        ex.printStackTrace();
//    }catch(ValueNotSetException ex){
//        ex.printStackTrace();
//    }catch(TimeoutException ex){
//        ex.printStackTrace();
//    }catch(PDUException ex){
//        ex.printStackTrace();
//    }catch(IOException ex){
//        ex.printStackTrace();
//    }catch(WrongSessionStateException ex){
//        ex.printStackTrace();
//    }catch(NullPointerException ex){
//        ex.printStackTrace();
//    }
//
//
//    //return dataSmResp.getMessageId();
//}
    
protected class EnquiryLinkRequest extends Thread{
        @Override
        public void run(){
             Calendar cal = Calendar.getInstance();
       	     cal.add(Calendar.MINUTE, WAIT_LENGTH_SECS);
            nextHandShakeTime = cal.getTimeInMillis();
            while(keepRunning){
                if(nextHandShakeTime < Calendar.getInstance().getTimeInMillis()){
                    getMessage("We are sending an HandShake request");
                    try{

                    getMessage(message);
                    enquiryLink();

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    try{
                        System.out.println("Thread is going to sleep for 3minutes");
                        TimeUnit.MINUTES.sleep(WAIT_LENGTH_SECS);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    protected boolean setupServer(){
        
        Thread thread = new Thread(){
          @Override
          public void run(){
              boolean keepRunning = true;
              while(keepRunning){
                  
                  try{                      
                      InetSocketAddress endPoint = new InetSocketAddress(port);
                      serverSocket.bind(endPoint);
                      socket = serverSocket.accept();
                      socket.setKeepAlive(true);
                      new Thread(new SessionMTHandler(socket, ESMEDaemon.this, gateway)).start();
                  }catch(Exception ex){
                      System.out.println("SocketException : " +ex);
                      keepRunning = false;
                  } 
              }
          }
        };
        thread.start();


        return true;
    }

    protected void getMessage(String message){

        System.out.println(message);
    }

    

    
    public static void main(String[] args) {
        InstanceManager manager = new InstanceManager();
        if (!manager.registerInstance()) {            
            System.out.println("Another instance of this application is running. Exiting.");
            System.exit(0);
        }
        manager.setInstanceListener(new InstanceListener() {

            @Override
            public void newInstanceCreated() {
                System.out.println("New instance detected..");
            }
        });

        ESMEDaemon esme = starter();
        while(esme.rebind){
          starter();
          esme.rebind=false;
        }
        
    }

    public static ESMEDaemon starter() {
        ESMEDaemon esme = new ESMEDaemon();
        esme.Start();
        message = "Login Successful";
        esme.getMessage(message);
        return esme;
    }

}
