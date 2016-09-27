/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.smpp.TimeoutException;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.ValueNotSetException;

/**
 *
 * @author Ahmed
 */
public class ESMEEnquiryLink extends Thread implements Runnable{
    private ESMEDaemon esme;

    public ESMEEnquiryLink(){
      esme = new ESMEDaemon();
    }

    @Override
    public void run(){
    while(esme.session.isOpened()){
            try {
             EnquireLinkResp response  = esme.session.enquireLink();
             System.out.println("Response : " +response);
            } catch (ValueNotSetException ex) {
                ex.printStackTrace();
            } catch (TimeoutException ex) {
                ex.printStackTrace();
            } catch (PDUException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (WrongSessionStateException ex) {
                ex.printStackTrace();
            }

    }
    }

}
