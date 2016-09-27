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

/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version $Revision: 1.1 $
 */

public class BindReceiver extends BindRequest {
	public BindReceiver() {
		super(Data.BIND_RECEIVER);
	}

	protected Response createResponse() {
		return new BindReceiverResp();
	}

	public boolean isTransmitter() {
		return false;
	}

	public boolean isReceiver() {
		return true;
	}
}
/*
 * $Log: BindReceiver.java,v $
 * Revision 1.1  2003/07/23 00:28:39  sverkera
 * Imported
 *
 */