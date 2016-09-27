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
 * @version $Revision: 1.2 $
 */
public class InvalidPDUException extends PDUException {
	private Exception underlyingException = null;

	public InvalidPDUException(PDU pdu, Exception e) {
		super(pdu, e);
		underlyingException = e;
	}

	public InvalidPDUException(PDU pdu, String s) {
		super(pdu, s);
	}

	public String toString() {
		String s = super.toString();
		if (underlyingException != null) {
			s += "\nUnderlying exception: " + underlyingException.toString();
		}
		return s;
	}

	public Exception getException() {
		return underlyingException;
	}
}
/*
 * $Log: InvalidPDUException.java,v $
 * Revision 1.2  2003/07/24 14:31:18  sverkera
 * Added support for nested exceptions
 *
 * Revision 1.1  2003/07/23 00:28:39  sverkera
 * Imported
 *
 */
