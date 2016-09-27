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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.smpp.pdu.WrongLengthOfStringException;

/**
 *
 * @author Ahmed
 */
public class SessionMOHandler extends SessionController{
    private String serverUrl = "";
    private Utilities utils;
    private Padding padding;
    

    public SessionMOHandler(String msisdn, ESMEDaemon esme,USSDGateway gateway) {
        super(msisdn, esme,gateway);
        sessionname = "SessionMOHandler";
    }

    public SessionMOHandler(String msisdn, ESMEDaemon esme, String Url, USSDGateway gateway) {
        super(msisdn, esme, Url, gateway);
        serverUrl = Url;
        this.msisdn = msisdn;
        endSession = false;
    }
    
    
    @Override
    protected void HandleMOPSSRRequest(String sessionID,String requests){
        /*This signify a new USSD Session, PSSR Request*/
        utils = new Utilities();
        padding = new Padding();
        String returnUserData = "This is a service test";
        String ussdMenuHex = null;               
        System.out.println("Welcome");

        /*Retrive session data*/
//        JSONObject fromclient = new JSONObject(esme.sessionList);
//        JSONObject jsonobject = fromclient.getJSONObject("sessiondata");
//
//        System.out.println("jsonobject : " + jsonobject);
//
//        System.out.println("sessionID : " + jsonobject.getString("sessionID"));
//        System.out.println("ussdstring : " + jsonobject.getString("ussdString"));
//        System.out.println("mobilenumber : " + jsonobject.getString("msisdn"));
//        System.out.println("Url : " +jsonobject.getString("url"));
//
//        String USSDStr = jsonobject.getString("ussdString");
//
//        System.out.println("USSD String : " +USSDStr);
        

        
        try {

            System.out.println("Ussd String : " +requests);

        } catch (Exception ex) {
            System.out.println("UnsupportedEncodingException : " +ex.getMessage());

        }

        //Send the stuff to the third party
       String received = "";
       String StrValue = "";       
        System.out.println("Sending to ThirdParty : " +serverUrl);
        HttpClient httpClient = new DefaultHttpClient();        
        try{
            HttpGet httpGet = new HttpGet(serverUrl + "msisdn="+msisdn+"&userdata="+URLEncoder.encode(requests,"UTF-8")+"&sessionid="+URLEncoder.encode(sessionID.trim(),"UTF-8"));
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
                    System.out.println("Received from : " +serverUrl+ received);                   

                    try{
                        //JSONObject fromClient = new JSONObject(received);
                        //returnUserData = fromClient.getString("userdata").replaceAll("\\\\n", "\n");
                        //endSession = endSession || fromClient.getBoolean("endofsession");
                        returnUserData=received;
                        ussdMenuHex = utils.convertStringToHex(received);

                        System.out.println("USSDMenu In Hexadecimal : " +ussdMenuHex);

                    }catch(JSONException jsonex){
                        System.out.println("JSONException : " +jsonex);
                        returnUserData = "Service Error";
                        jsonex.printStackTrace();
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
         System.out.println("returnUserData : \n" + returnUserData);

         try{
             if(endSession){
                  System.out.println("Session Status : " +endSession);
                 System.out.println("Application is sending the last message and ending session");
                 SendMOPSSRResponse(msisdn, returnUserData, sessionID);
                 esme.sessionList.remove("sessiondata");

                 System.out.println("Session ended");
             }else{
                System.out.println("Session Continue");
             SendMOUSSRRequest(msisdn, returnUserData, sessionID);
            System.out.println("USSD Continue Request Message Sent to SMPP Gateway");
             }
                                               
    }catch(Exception ex1){
        System.out.println("USSDContinue Error : " +ex1.getMessage());
        ex1.printStackTrace();
    }

    }

    @Override
    protected void SendMOUSSRRequest(String msisdn,String returnUserData,String sessionID) throws IOException{
        try {
            try {
                esme.DataSM(msisdn, returnUserData, sessionID, Constants.USSR_REQUEST);
            } catch (WrongLengthOfStringException ex) {
                Logger.getLogger(SessionMOHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void HandleMOUSSRELRequest(String UssdString){
        /*This handle the USSD Abort request from the SMPP Gateway : USSREL */
        System.out.println("USSD Service Application Aborted");
    }

    @Override
    protected void HandleMOPSSDRequest(String sessionId,String UssdString){
        /*This handle PSSD Request*/
    }

    @Override
    protected void SendMOPSSRResponse(String msisdn,String returnUserData,String sessionID) throws IOException{
        try {
            try {
                /*This signify the end of the session after several USSD transaction between ESME and SMPP Gateway*/
                esme.DataSM(msisdn, returnUserData, sessionID, Constants.PSSR_RESPONSE);
            } catch (WrongLengthOfStringException ex) {
                Logger.getLogger(SessionMOHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SessionMOHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void SendMOPSSDResponse(String msisdn,String returnUserData,String sessionID){
        /*This send PSSD response and end the session*/
    }
}
