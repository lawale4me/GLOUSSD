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
public class WrongDateFormatException extends PDUException {
	public WrongDateFormatException() {
		super("Date must be either null or of format YYMMDDhhmmsstnnp");
	}

	public WrongDateFormatException(String dateStr) {
		super("Date must be either null or of format YYMMDDhhmmsstnnp and not " + dateStr + ".");
	}

	public WrongDateFormatException(String dateStr, String msg) {
		super("Invalid date " + dateStr + ": " + msg);
	}
}
/*
 * $Log: WrongDateFormatException.java,v $
 * Revision 1.1  2003/07/23 00:28:39  sverkera
 * Imported
 *
 * 
 * Old changelog:
 * 03-10-01 ticp@logica.com added constructor with description of error.
 */
