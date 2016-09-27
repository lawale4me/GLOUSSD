/*
 * Copyright (c) 1996-2001
 * Logica Mobile Networks Limited
 * All rights reserved.
 *
 * This software is distributed under Logica Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package org.smpp.pdu;

import org.smpp.Data;
import org.smpp.util.*;
import org.smpp.pdu.Response;

/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version $Revision: 1.3 $
 */
public class SubmitSMResp extends Response {

	private String messageId = Data.DFLT_MSGID;

	public SubmitSMResp() {
		super(Data.SUBMIT_SM_RESP);
	}

	public void setBody(ByteBuffer buffer)
		throws NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException, WrongLengthOfStringException, InvalidPDUException {

		if (getCommandStatus() == 0) {
			setMessageId(buffer.removeCString());
			return;
		}

		if (buffer.length() > 0) {
			// This is broken in so many implementations that it's not practical
			// to be so pedantic about it, so we now just accept it.
			// throw new InvalidPDUException(this,"command_status non-zero, but body was present");
			debug.enter(this,"setBody");
			debug.write("invalid SubmitSMResp: command_status non-zero, but body was present (ignoring body)");
			debug.exit(this);
			event.write("invalid SubmitSMResp sequenceNumber ["+getSequenceNumber()+"]: command_status non-zero, but body was present (ignoring body)");
			buffer.removeBytes(buffer.length()); // discard body
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		if (getCommandStatus() == 0) buffer.appendCString(messageId);
		return buffer;
	}

	public void setMessageId(String value) throws WrongLengthOfStringException {
		checkString(value, Data.SM_MSGID_LEN);
		messageId = value;
	}

	public String getMessageId() {
		return messageId;
	}

	public String debugString() {
		String dbgs = "(submit_resp: ";
		dbgs += super.debugString();
		dbgs += getMessageId();
		dbgs += " ";
		dbgs += debugStringOptional();
		dbgs += ") ";
		return dbgs;
	}
}
/*
 * $Log: SubmitSMResp.java,v $
 * Revision 1.3  2004/12/11 08:21:22  paoloc
 * My previous changes were too restrictive to be practical: there are many implementations that incorrectly return a PDU body with a failure response. Now just logging to the event log and ignoring the body.
 *
 * Revision 1.2  2004/09/04 08:10:56  paoloc
 * Changes to enforce this statement from SMPP spec (v3.4 p. 67): "The submit_sm_resp PDU Body is not returned if the command_status field contains a non-zero value".
 *
 * Revision 1.1  2003/07/23 00:28:39  sverkera
 * Imported
 *
 */
