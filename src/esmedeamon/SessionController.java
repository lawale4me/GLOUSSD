/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;
import org.smpp.pdu.WrongLengthOfStringException;



/**
 *
 * @author Ahmed
 */
public class SessionController extends SessionMonitor{
    protected String sessionname = "SessionController";
    private long initTime;
    protected boolean endSession = false;
    protected String msisdn = "";
    protected String sessionID = "";
    protected String sessiondata = "";
    protected String ussdString = "";
    protected ESMEDaemon esme;
    protected USSDGateway gateway;
    private Utilities utilis;
    private String serverUrl;
    private Padding padding;
    private String serviceCode = "";
    private String sessionId = "";
    private DataAccess dataAccess = null;
    private String Url;
    private Utilities utils;
    private String serverURL = "";
    
    

    public SessionController(){

    }
    
    public SessionController(USSDGateway gateway){
        this.gateway = gateway;
        utilis = new Utilities();
        
    }
    
    public SessionController(String msisdn,ESMEDaemon esme,USSDGateway gateway){
        this.esme = esme;
        this.msisdn = msisdn;
        this.gateway = gateway;
    }
    public SessionController(String msisdn,ESMEDaemon esme,String Url,USSDGateway gateway){
        this.msisdn = msisdn;
        this.esme = esme;
        this.gateway = gateway;
        this.Url = Url;
    }
    
//    protected void EndSession(){
//       esme.sessionList.remove(msisdn);
//       String message = "Session " + msisdn + "ended";
//       //esme.getMessage(message);
//    }
    
    
    protected void MOProcess(String sessionId, String UssdString, int ussd_service_op){
//       Thread t = new Thread(){
//            @Override
//           public void run(){
                try {                 
                     if(ussd_service_op == Constants.PSSR_INDICATION){
                         /*PSSR Request/Indication : Mobile Initiated Session, New Session Started*/
                         HandleMOPSSRRequest(sessionId, UssdString);
                     }else
                         if(ussd_service_op == Constants.USSREL_REQUEST){
                            /*USSREL : Session Aborted By the SMPP Gateway*/
                             HandleMOUSSRELRequest(UssdString);
                         }else
                           if(ussd_service_op == Constants.USSR_CONFIRM){
                         /*USSR Response/Confirm : Send Response to the subscribers*/
                         HandleMOUSSRResponse(sessionId, UssdString);
                     }else
                         if(ussd_service_op == Constants.HANGUP){
                             /*Hangup*/
                             MOHangUp(sessionId,UssdString,ussd_service_op);
                         }else
                             if(ussd_service_op == Constants.FAILURE){
                                 /*Failure*/
                                 MOFailure(sessionId, UssdString, ussd_service_op);
                             }else
                                 if(ussd_service_op == Constants.REJECT){
                                     /*Reject*/
                                     MORejected(sessionId, UssdString, ussd_service_op);
                                 }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
//           }
//       };
//       t.start();
    }

    protected void MTProcess(final String sessionId,final String UssdString,final int ussd_service_op){

//        Thread t = new Thread(){
//            @Override
//           public void run(){
                try {
                 if(ussd_service_op == Constants.USSREL_REQUEST){
                     /*Application Send USSR Request :   Network Initiated Mobile Terminated Session*/
                     SendMTUSSRRequest(sessionId, UssdString);
                 }
                 else
                     if(ussd_service_op == Constants.USSREL_REQUEST){
                      /*Application send USSREL Request to the SMPP Gateway and close the session without sending a message to the subscriber*/
                       SendMTUSSRELRequest();
                  }
                 else
                     if(ussd_service_op == Constants.USSR_CONFIRM){
                      /*Application Send USSR Response : USSD Continue to the subscriber*/
                       HandleMTUSSRResponse(sessionId, UssdString);
                  }
                 else
                     if(ussd_service_op == Constants.USSREL_INDICATION){

                 }
                 else
                     if(ussd_service_op == Constants.USSR_REQUEST_LAST_MESSAGE_INDICATION){
                   /*This instructs the SMPP Gateway to close the session after response to the USSR Request is received,
                    In this case the SMPP Gateway will forward the response in a USSR confirm + last message indication data_sm*/
                     SendMTUSSRRequestWithLastMessageIndication(sessionId,UssdString);
                 }
                 else
                     if(ussd_service_op == Constants.USSR_CONFIRM_LAST_MESSAGE_INDICATION){
                         /*This handle USSRResponse+last message indication from SMPP Gateway*/
                     HandleMTUSSRResponseWithLastMessageIndication();
                 }
                 else
                     if(ussd_service_op == Constants.USSN_REQUEST_LAST_MESSAGE_INDICATION){
                         /*The Application send USSN Request + last message indication*/
                     SendMTUSSNRequestWithLastMessageIndication(sessionId, UssdString);
                 }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//           }
//       };
//       t.start();
    }

    protected void HandleMOPSSRRequest(String sessionId,String UssdString){
        /*This handle a new USSD Session initiated by the subscriber, PSSR Request*/
    }        

    protected void HandleMOUSSRELRequest(String UssdString){
        /*This handle the USSD Abort request from the SMPP Gateway : USSREL */
        System.out.println("USSD Service Application Aborted");
    }

    protected void HandleMOPSSDRequest(String sessionId,String UssdString){
        /*This handle PSSD Request*/
    }

    protected void SendMTUSSRRequestWithLastMessageIndication(String sessionID,String UssdString){
        /*This instructs the SMPP Gateway to close the session after response to the USSR Request is received,
         In this case the SMPP Gateway will forward the response in a USSR confirm + last message indication data_sm*/
    }

    protected void HandleMTUSSRResponseWithLastMessageIndication(){
        /*This handle USSRResponse+last message indication from SMPP Gateway*/
    }

    protected void SendMTUSSRELRequest(){
        /*application send USSREL Request to the SMPP Gateway and close the session without sending a message to the subscriber*/
    }

    protected void SendMTUSSRRequest(String sessionID,String UssdString){
        /*Send USSD Continue Response to the subscribers  */
    }
    protected void SendMTUSSRRequest(String UssdString){
        /*Mobile Terminated Session Initiated By the ESME Application*/
    }

    protected void SendMTUSSNRequestWithLastMessageIndication(String sessionID,String UssdString){
        
    }

    protected void HandleMTUSSRResponse(String sessionID,String requests){
        /*Handle USSR Response from the SMPP Gateway*/
    }

    protected void MOHangUp(String sessionID,String UssdString,int UssdServiceOp){
        try {
            System.out.println("sessionID : " + sessionID);
            System.out.println("request : " + UssdString);
            System.out.println("UssdServiceOp : " + UssdServiceOp);
            String hangup = "hangup";
            System.out.println("The subscriber has hangup");
            esme.sessionList.remove("sessiondata");
        } catch(Exception ex){
         //ex.getMessage();
            System.out.println("***************ValueNotSetException : "+ex.getMessage()+"***************");
        }
    }

    protected void MOFailure(String sessionID,String UssdString,int UssdServiceOp){
        try {
            System.out.println("sessionID : " + sessionID);
            System.out.println("request : " + UssdString);
            System.out.println("UssdServiceOp : " + UssdServiceOp);
            String hangup = "failure";

            System.out.println("your request was failed");
            esme.sessionList.remove("sessiondata");
        } catch(Exception ex){
            //ex.getMessage();
            System.out.println("***************ValueNotSetException  :"+ex.getMessage()+"***************");
        }
    }

    protected void MORejected(String sessionID,String UssdString,int UssdServiceOp){
        try {
            System.out.println("sessionID : " + sessionID);
            System.out.println("request : " + UssdString);
            System.out.println("UssdServiceOp : " + UssdServiceOp);
            String reject = "rejected";

            System.out.println("your request was rejected by the Gateway");
            esme.sessionList.remove("sessiondata");
        } catch (Exception ex) {
            //ex.getMessage();
            System.out.println("***************ValueNotSetException : "+ex.getMessage()+"***************");
        }
    }
    
    protected void HandleMOUSSRResponse(String sessionID,String UssdString) throws UnsupportedEncodingException{
        /*This handle the USSR Response from the SMPP Gateway*/
        padding = new Padding();
        utils = new Utilities();
        //dataAccess = new DataAccess();
        String sessionid = "";
                
                
        //String USSDStr = dBAccess.fetchServices(sessionID);
//        JSONObject fromclient = new JSONObject(esme.sessionList);
//        JSONObject jsonobject = fromclient.getJSONObject("sessiondata");
//
//        System.out.println("jsonobject : " + jsonobject);
//
//        System.out.println("mobilenumber : " + jsonobject.getString("msisdn"));
//        System.out.println("ussdstring : " + jsonobject.getString("ussdString"));
//        System.out.println("sessionID : " + jsonobject.getString("sessionID"));
//        System.out.println("Url : " +jsonobject.getString("url"));
//
//        String USSDStr = jsonobject.getString("ussdString");
//        String Url2 = jsonobject.getString("url");

        String messageBody[] = this.loadSession(sessionID).split("\\|");

        Url= messageBody[0];
        msisdn = messageBody[1];
        sessionID=messageBody[2];

        System.out.println("Session Url : " +Url);
        System.out.println("Session msisdn : " +msisdn);
                
        System.out.println("Url from property file : " +Url);

        System.out.println("response from subscriber : " +UssdString);
        System.out.println("Phone : " + msisdn);
        System.out.println("SessionID : " + sessionID);
        

        String StrValue = "";
        String received = "";
        String returnUserData = "";
        String userrquestHex = null;

          HttpClient httpClient = new DefaultHttpClient();
        try{           
            HttpGet httpGet = new HttpGet(Url + "msisdn="+msisdn+"&userdata="+URLEncoder.encode(UssdString.trim(),"UTF-8")+"&sessionid="+URLEncoder.encode(sessionID.trim(),"UTF-8") );
            
            HttpResponse httpResponse;
            httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if(entity != null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                try{

                    while((StrValue = reader.readLine()) != null){
                        System.out.println("String Value : " +StrValue);
                        received += StrValue+"\n";
                    }
                    System.out.println("Received from : " + serverURL+ received);                    

                    try{
                        //JSONObject fromClient = new JSONObject(received);
                        returnUserData = received;// = fromClient.getString("userdata").replaceAll("\\\\n", "\n");
                        //endSession = endSession || fromClient.getBoolean("endofsession");
                        //sessionid = fromClient.getString("sessionid");
                        userrquestHex = utils.convertStringToHex(returnUserData);

                        System.out.println("USSDMenu In Hexadecimal : " +userrquestHex);

                    }catch(JSONException jsonex){

                        if(UssdString.equals("*309*200#")){
                            returnUserData = "Your mobile number is not recognised on the Nokia Ingite system.  Please visit www.nokiashine.mobi to register or update your details\n";
                            userrquestHex = utils.convertStringToHex(returnUserData);                            
                        }else{
                            System.out.println("JSONException : " +jsonex);
                            returnUserData = "your request is processing, please wait for a while";
                            userrquestHex = utils.convertStringToHex(returnUserData);                           
                        }

                    }
                }catch(IOException ioex){
                    System.out.println("IOException Error : " +ioex.getMessage());
                }finally{
                    try{
                        reader.close();
                    }catch(IOException ioex){
                        System.out.println("IOException : " +ioex);
                    }
                }

            }
        }catch(ClientProtocolException clientprotocolEx){
            System.out.println("Client Protocol Exception : " +clientprotocolEx.getMessage());
        }catch(IOException ioex){
            System.out.println("IOException : " +ioex);
        }
           if(endSession){
            try {
                System.out.println("Session Status : " +endSession);
                //SendMOPSSRResponse(msisdn, returnUserData, sessionID);
                System.out.println("Application is sending the last message and ending the session");
                SendMOPSSRResponse(msisdn, returnUserData, sessionID);
                esme.sessionList.remove("sessiondata");
                System.out.println("This is the end of the session");
            } catch (IOException ex) {

                //ex.printStackTrace();
                System.out.println("***************ValueNotSetException : "+ex.getMessage()+"***************");
            }
           } else{
              try{
              System.out.println("MSISDN : " +msisdn);
              System.out.println("sessionID : " +sessionID);
              System.out.println("ReturnUserData : " +returnUserData);
              
              System.out.println("Session Continue");
              SendMOUSSRRequest(msisdn, returnUserData, sessionID);

          } catch (IOException ex) {
                //ex.printStackTrace();
              System.out.println("***************ValueNotSetException : "+ex.getMessage()+"***************");
            }catch(NullPointerException ioex){
              System.out.println("Unable to send the request to the subscriber with following data  : " +ioex.getMessage());
              System.out.println("MSISDN : " +msisdn);
              System.out.println("sessionID : " +sessionID);
              System.out.println("ReturnUserData : " +returnUserData);
              //ioex.printStackTrace();
              System.out.println("***************ValueNotSetException : "+ioex.getMessage()+"***************");
              ioex.printStackTrace();
              esme.sessionList.remove("sessiondata");
          }
           }
    }

    protected void SendMOUSSRRequest(String msisdn,String returnUserData,String sessionID) throws IOException{
        try {
            try {
                /*This signify the USSD Continue, meaning that the session is still continue*/
                //esme.DataSM(msisdn, returnUserData, sessionID, Constants.USSR_REQUEST);
                System.out.println("UssdServiceOp : " +Constants.USSR_REQUEST);
                esme.DataSM(msisdn, returnUserData, sessionID, Constants.USSR_REQUEST);
            } catch (WrongLengthOfStringException ex) {
                //ex.printStackTrace();
                System.out.println("***************ValueNotSetException : "+ex.getMessage()+"***************");
                ex.printStackTrace();
            }
        } catch (UnsupportedEncodingException ex) {
            //ex.printStackTrace();
            System.out.println("***************UnsupportedEncodingException : "+ex.getMessage()+"***************");
        }
    }

    protected void SendMOPSSRResponse(String msisdn,String returnUserData,String sessionID) throws IOException{
        try {
            try {
                /*This signify the end of the session after several USSD transaction between ESME and SMPP Gateway*/
                System.out.println("UssdServiceOp : " +Constants.USSR_REQUEST);
                esme.DataSM(msisdn, returnUserData, sessionID, Constants.PSSR_RESPONSE);
            } catch (WrongLengthOfStringException ex) {
                System.out.println("***************ValueNotSetException : "+ex.getMessage()+"***************");
            }
        } catch (UnsupportedEncodingException ex) {
           System.out.println("***************ValueNotSetException : "+ex.getMessage()+"***************");
        }
    }

    protected void SendMOPSSDResponse(String msisdn,String returnUserData,String sessionID){
        /*This send PSSD response and end the session*/
    }

//    public void endSession(){
//        esme.sessionList.remove(msisdn);
//    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessiondata() {
        return sessiondata;
    }

    public void setSessiondata(String sessiondata) {
        this.sessiondata = sessiondata;
    }

    public String getSessionname() {
        return sessionname;
    }

    public void setSessionname(String sessionname) {
        this.sessionname = sessionname;
    }

    public String getUssdString() {
        return ussdString;
    }

    public void setUssdString(String ussdString) {
        this.ussdString = ussdString;
    }
    
    
}
