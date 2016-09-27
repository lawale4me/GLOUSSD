/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 *
 * @author Ahmed
 */
public class AccessList implements Filter{
    private String serviceCode;
    private ArrayList<String> listServiceCode;

    public AccessList(){
        listServiceCode = new ArrayList<String>();
        listServiceCode.add("*578#");
        listServiceCode.add("*578#100#");
        listServiceCode.add("*578#200#");
        listServiceCode.add("*578#300#");
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    public static void main(String[] args){
//
//    }

}
