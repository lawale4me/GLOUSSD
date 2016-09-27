/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esmedeamon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Ahmed
 */
public class InstanceManager {
    
    
    private static InstanceListener subListener;
    //Randomly chosen, but static, high socket number 
    public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 44332;
    //Must end with newline
    public static final String SINGLE_INSTANCE_SHARED_KEY = "$$NewInstance$$\n";
    

    public static boolean registerInstance(){
        // returnValueOnError should be true if lenient (allows app to run on network error) or false if strict.
        boolean returnValueOnError = true;
        // try to open network socket
        // if success, listen to socket for new instance message, return true
        // if unable to open, connect to existing and send new instance message, return false
        try{
            final ServerSocket socket = new ServerSocket(SINGLE_INSTANCE_NETWORK_SOCKET, 10, InetAddress.getLocalHost());
            System.out.println("Listening for application instances on socket " + SINGLE_INSTANCE_NETWORK_SOCKET);

            Thread instanceListenerThread = new Thread(new Runnable() {

                public void run() {
                    boolean socketClosed = false;
                    while(!socketClosed){
                        if(socket.isClosed()){
                            socketClosed = true;
                        }else{
                            try{
                                Socket client = socket.accept();
                                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                String message = in.readLine();
                                if(SINGLE_INSTANCE_SHARED_KEY.trim().equals(message.trim())){
                                    System.out.println("Shared key matched - new application instance found");
                                    fireNewInstance();
                                }
                                in.close();
                                client.close();
                            }catch(IOException ex){
                                socketClosed = true;
                                
                            }
                        }
                    }
                }
            });

            instanceListenerThread.start();

        }catch(UnknownHostException ex){
            System.out.println(ex.getMessage());
           return returnValueOnError;
        }catch(Exception ex){
            try{
                Socket clientSocket = new Socket(InetAddress.getLocalHost(),SINGLE_INSTANCE_NETWORK_SOCKET);
                OutputStream out = clientSocket.getOutputStream();
                out.write(SINGLE_INSTANCE_SHARED_KEY.getBytes());
                out.close();
                clientSocket.close();
                System.out.println("Successfully notified first instance");

                return false;
            }catch(IOException ex1){
                System.out.println("Error connecting to local port for single instance notification");
                System.out.println(ex1.getMessage());
                return returnValueOnError;
            }
        }
        return true;
    }

    public static void setInstanceListener(InstanceListener listener) {
        subListener = listener;
    }

    private static void fireNewInstance(){
        if(subListener != null){
            subListener.newInstanceCreated();
        }
    }
    
}
