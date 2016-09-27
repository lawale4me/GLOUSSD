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

/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version $Revision: 1.1 $
 */
public class UnexpectedOptionalParameterException extends PDUException {
	private int tag = 0;

	public UnexpectedOptionalParameterException() {
		super("The optional parameter wasn't expected for the PDU.");
	}

	public UnexpectedOptionalParameterException(short tag) {
		super("The optional parameter wasn't expected for the PDU:" + " tag=" + tag + ".");
	}
}
/*
 * $Log: UnexpectedOptionalParameterException.java,v $
 * Revision 1.1  2003/07/23 00:28:39  sverkera
 * Imported
 *
 */
