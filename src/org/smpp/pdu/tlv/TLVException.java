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
package org.smpp.pdu.tlv;

import org.smpp.pdu.PDUException;

/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version $Revision: 1.2 $
 */
public class TLVException extends PDUException {
	private static final long serialVersionUID = -6659626685298184198L;

	public TLVException() {
		super();
	}

	public TLVException(String s) {
		super(s);
	}
}
/*
 * $Log: TLVException.java,v $
 * Revision 1.2  2006/03/09 16:24:14  sverkera
 * Removed compiler and javadoc warnings
 *
 * Revision 1.1  2003/07/23 00:28:39  sverkera
 * Imported
 *
 */