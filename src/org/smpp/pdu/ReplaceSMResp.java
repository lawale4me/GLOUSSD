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

/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version $Revision: 1.1 $
 */
public class ReplaceSMResp extends Response {
	public ReplaceSMResp() {
		super(Data.REPLACE_SM_RESP);
	}

	public void setBody(ByteBuffer buffer)
		throws NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException, PDUException {
	}

	public ByteBuffer getBody() {
		return null;
	}

	public String debugString() {
		String dbgs = "(replace_resp: ";
		dbgs += super.debugString();
		dbgs += ") ";
		return dbgs;
	}
}
/*
 * $Log: ReplaceSMResp.java,v $
 * Revision 1.1  2003/07/23 00:28:39  sverkera
 * Imported
 *
 */
