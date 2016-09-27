/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.SmppObject;
import org.smpp.pdu.PDU;
import org.smpp.util.Queue;

/**
 *
 * @author Ahmed
 */
public class SMPPPDUEventListener  extends SmppObject implements ServerPDUEventListener{
    
                Session session;
		Queue requestEvents = new Queue();

		public SMPPPDUEventListener(Session session) {
			this.session = session;
		}

		public void handleEvent(ServerPDUEvent event) {
			PDU pdu = event.getPDU();
			if (pdu.isRequest()) {
				System.out.println("async request received, enqueuing " + pdu.debugString());
				synchronized (requestEvents) {
					requestEvents.enqueue(event);
					requestEvents.notify();
				}
			} else if (pdu.isResponse()) {
				System.out.println("async response received " + pdu.debugString());
			} else {
				System.out.println(
					"pdu of unknown class (not request nor " + "response) received, discarding " + pdu.debugString());
			}
		}

		/**
		 * Returns received pdu from the queue. If the queue is empty,
		 * the method blocks for the specified timeout.
		 */
		public ServerPDUEvent getRequestEvent(long timeout) {
			ServerPDUEvent pduEvent = null;
			synchronized (requestEvents) {
				if (requestEvents.isEmpty()) {
					try {
						requestEvents.wait(timeout);
					} catch (InterruptedException e) {
						// ignoring, actually this is what we're waiting for
					}
				}
				if (!requestEvents.isEmpty()) {
					pduEvent = (ServerPDUEvent) requestEvents.dequeue();
				}
			}
			return pduEvent;
		}
    
}
