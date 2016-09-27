/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

/**
 *
 * @author Ahmed
 */
public class Constants {
    public static int BIND = 1;
    public static int BIND_RESPONSE = 2;
    public static int ENQUIRY_LINK = 3;
    public static int ENQUIRY_LINK_RESPONSE = 4;
    public static int SUBMIT_SM = 5;
    public static int SUBMIT_SM_RESPONSE = 6;
    public static int DELIVERY_SM = 7;
    public static int DELIVERY_SM_RESPONSE = 8;
    public static int UNBIND = 9;
    public static int UNBIND_RESPONSE = 10;

    /*LIST USSD SERVICE OPERATIONS*/

    /* OUTGOING DATA_SM*/
    public static int PSSD_INDICATION = 0 ;//ProcessUssdData response
    /* OUTGOING DATA_SM*/
    public static int PSSR_INDICATION = 1;//ProcessUssdRequest response to start session, send by mobile user
    /*INCOMING DATA_SM*/
    public static int USSR_REQUEST = 2;//request to continue session send by ESME Service Application
    /*INCOMING DATA_SM*/
    public static int USSN_REQUEST = 3; //request
    /*4 to 15 = reserved*/

     /*INCOMING DATA_SM*/
    public static int PSSD_RESPONSE = 16;
    /*INCOMING DATA_SM*/
    public static int PSSR_RESPONSE = 17;//response to end session, send by ESME Service Application
    /* OUTGOING DATA_SM*/
    public static int USSR_CONFIRM = 18;//to continue session, send by mobile user
    /* OUTGOING DATA_SM*/
    public static int USSN_CONFIRM = 19;
    /*20 to 31 = reserved*/
    /*32 to 255 = reserved for vendor specific USSD Operations*/

    /* OUTGOING DATA_SM*/
    public static int HANGUP = 65;
    /* OUTGOING DATA_SM*/
    public static int FAILURE = 66;
    /* OUTGOING DATA_SM*/
    public static int REJECT = 67;
    /*INCOMING DATA_SM*/
    public static int ESME_HANGUP = 68;
    public static int USSREL_REQUEST = 128;//The request value denotes that the USSD application wishes to terminate the USSD session with the mobile station
    public static int USSREL_INDICATION = 129;
    public static int USSR_REQUEST_LAST_MESSAGE_INDICATION = 130; //The application must wait for a USSR confirm + last message indication from the USSD
    public static int USSN_REQUEST_LAST_MESSAGE_INDICATION= 131;
    public static int USSR_CONFIRM_LAST_MESSAGE_INDICATION = 146;

    //NOTE : The following values indicate session termination request from the application:
    //PSSD response,PSSR response,USSREL, USSR request + last message indication,USSN request + last message indication from USSD GW

    //NOTE : last message indicate values are allowed only in application originated USSD sessions.
    //Moble originated USSD session must terminate with PSSD response or PSSR response

//    public static String FIRSTURL = LoadProperties.FIRSTURL;//"http://localhost/~din5/ussd/menu.php?";
//    public static String SECONDURL = LoadProperties.SECONDURL;//"http://localhost/~din5/ussd/menu.php?";
//    public static String THIRDURL = LoadProperties.THIRDURL;//"http://localhost/~din5/ussd/menu.php?";
    

    public static String SERVIVETYPE_CMT = "CMT";
    public static String SERVIVETYPE_CPT = "CPT";
    public static String SERVIVETYPE_VMN = "VMN";
    public static String SERVIVETYPE_VMA = "VMA";
    public static String SERVIVETYPE_WAP = "WAP";
    public static String SERVIVETYPE_USSD = "USSD";

    public static int TON_UNKNWON = 0;
    public static int TON_INTERNATIONAL = 1;
    public static int TON_NATIONAL = 2;
    public static int TON_NETWORKSPECIFIC = 3;
    public static int TON_SUBSCRIBERNUMBER = 4;
    public static int TON_ALPHANUMERIC = 5;
    public static int TON_ABBREVIATED = 6;

    
    public static int NPI_UNKNOWN = 0;
    public static int NPI_ISDN = 1;
    public static int NPI_DATA = 2;
    public static int NPI_TELEX = 3;
    public static int NPI_LANDMOBILE = 6;
    public static int NPI_NATIONAL = 8;
    public static int NPI_PRIVATE = 9;
    public static int NPI_ERMES = 10;
    public static int NPI_INTERNET = 13;
    public static int NPI_WAPCLIENT = 18;
    
    //ADDED BY LAA
    //GLO credentials
//    public static  final String systemId = LoadProperties.SYSTEMID;//"quicklotto";//GLO systemId
//    public static final String password =LoadProperties.PASSWORD; //"quicklot";//GLO password
//    public static final String systemtype = LoadProperties.SYSTEMTYPE;//"quickussd";//GLO systemtype
//    public static final String addressrange = "145";    
//    public static final String IPAddress = LoadProperties.IPADDRESS;//"41.203.65.165";//GLO IPAddress    
//    public static final String ServiceCode = LoadProperties.FIRSTSERVICECODE;//"*309#"; //GLO service code
//    public static final int port =LoadProperties.PORT;//GLO port
    
//    smsc = smpp
//host = 41.203.65.165
//port = 7511
//smsc-username =quicklotto
//smsc-password =quicklot
//system-type = quickussd
//system-id =quicklotto
//service-type = USSD
    
    public static final String DIR = "/etc/USSDGLO/";   //SESSION DIRECTORY
    

    
}
