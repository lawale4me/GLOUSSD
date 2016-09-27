/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;
import java.net.Socket;
import org.jsmpp.session.SMPPServerSession;
import org.jsmpp.session.SMPPServerSessionListener;
import org.jsmpp.session.ServerMessageReceiverListener;
import org.jsmpp.session.ServerSession;

/**
 *
 * @author Ahmed
 */
public class SessionMTHandler  extends SessionController implements Runnable{

    private SMPPServerSession serverSession = null;
    private boolean keepRunning = false;
    private Socket socket;

    public SessionMTHandler(Socket socket,ESMEDaemon esme,USSDGateway gateway){
        super(gateway);
        //this.serverSession = serverSession;
        this.socket = socket;
        this.esme = esme;
        this.sessionname = "MobileTerminatedSession";
    }
    public SessionMTHandler(USSDGateway gateway) {
        super(gateway);
    }

    public SessionMTHandler(String msisdn, ESMEDaemon esme, USSDGateway gateway) {
        super(msisdn, esme, gateway);
    }

    public SessionMTHandler(String msisdn, ESMEDaemon esme, String Url, USSDGateway gateway) {
        super(msisdn, esme, Url, gateway);
    }        

    @Override
    protected void SendMTUSSRRequest(String sessionID,String UssdString ){
        /*Mobile Terminated Session Initiated By the ESME Application*/
    }
    
    @Override
    protected void HandleMTUSSRResponse(String sessionID,String requests){
        /*Handle USSR Response from the SMPP Gateway*/
    }

    @Override
    protected void SendMTUSSRRequestWithLastMessageIndication(String sessionID,String UssdString){
        /*This instructs the SMPP Gateway to close the session after response to the USSR Request is received,
         In this case the SMPP Gateway will forward the response in a USSR confirm + last message indication data_sm*/
    }

    @Override
    protected void HandleMTUSSRResponseWithLastMessageIndication(){
        /*This handle USSRResponse+last message indication from SMPP Gateway*/
    }

    @Override
    protected void SendMTUSSRELRequest(){
        /*application send USSREL Request to the SMPP Gateway and close the session without sending a message to the subscriber*/
    }

    protected void SendMTUSSNRequest(){
    }

    @Override
    protected void SendMTUSSNRequestWithLastMessageIndication(String sessionID,String UssdString){
        /*This instructs the SMPP Gateway to close the session after the confirmation from the mobile as been received*/
    }

    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
