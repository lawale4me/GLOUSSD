/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esmedeamon;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Ahmed
 */
public class LoadProperties {
    static LoadProperties ll=new LoadProperties();
    private Properties props;
//    public static String IPADDRESS="",SYSTEMID="",SYSTEMTYPE,FIRSTURL,SECONDURL,FIRSTSERVICECODE,SECONDSERVICECODE,
//            THIRDSERVICECODE,
//            USERNAME,PASSWORD,THIRDURL,PRODUCTNAME;
//    public static int PORT,TIMEOUT;
    private final String propsFile="/etc/gloussd.properties";
//    private final String propsFile="C:/TEMP/gloussd.properties";
    public static final String addressrange = "145";
    
    public LoadProperties()
    {
         loadProperties();
    }

    private void loadProperties() {
//        try 
//                {
//			props = new Properties();            
//                        props.load(new FileInputStream(propsFile));
//			/* Extract the values for the values in the configuration file */
//                        PRODUCTNAME = props.getProperty("PRODUCTNAME");
//			IPADDRESS = props.getProperty("IPADDRESS");
//                        USERNAME = props.getProperty("USERNAME");
//                        PASSWORD = props.getProperty("PASSWORD");
//                        SYSTEMID =props.getProperty("SYSTEMID");
//                        SYSTEMTYPE=props.getProperty("SYSTEMTYPE");
//                        PORT=Integer.parseInt(props.getProperty("PORT"));
//                        FIRSTURL =props.getProperty("FIRSTURL");
//                        SECONDURL =props.getProperty("SECONDURL");
//                        THIRDURL =props.getProperty("THIRDURL");
//                        FIRSTSERVICECODE =props.getProperty("FIRSTSERVICECODE");
//                        SECONDSERVICECODE =props.getProperty("SECONDSERVICECODE");
//                        THIRDSERVICECODE =props.getProperty("THIRDSERVICECODE");
//                        TIMEOUT=Integer.parseInt(props.getProperty("TIMEOUT"));
//                        
//                        System.out.println("systemIdddd:"+SYSTEMID);
//                        
//		} catch (IOException ioe)
//                {                    
//			System.err.println("caught IOException attempting to loadProperties "+ ioe);
//                        System.exit(0);
//		}
    }
    
}
