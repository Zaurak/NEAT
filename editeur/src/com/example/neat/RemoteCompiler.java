package com.example.neat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;
 
 
public class RemoteCompiler {
    
    public static String ip_adress;
    
    public static void setIp(String ip) {
        ip_adress = ip;
    }
    
    public static void compile(String path, String name) {
        sendToServer(path, name);
    }

    // Transfer a file from in to out
    // in and out can be socket streams
	public static void transfer(InputStream in, OutputStream out) throws IOException {
		byte buf[] = new byte[1024];
		int n;

		while((n = in.read(buf)) > 0) {
			out.write(buf, 0, n);
		}
		out.flush();
		in.close();
		out.close();
	}

    // Send file to the server
	public static void sendToServer(String path, String name) {
		Socket socket;
        try {
        	Log.e("EXCEPTION", "Enter try");
			socket = new Socket(InetAddress.getByName(ip_adress), 8080);
			Log.e("EXCEPTION", "Sock created");

			InputStream	 sock_in = socket.getInputStream(); 
			OutputStream sock_out = socket.getOutputStream(); 
			Log.e("EXCEPTION", "sockIn and sockout created");
			PrintStream out = new PrintStream(sock_out);
			BufferedReader in = new BufferedReader(new InputStreamReader(sock_in));
			Log.e("EXCEPTION", "In and out created");
            out.println(name);
			out.println("SEND");
			Log.e("EXCEPTION", "Sending .TEX");
			System.out.println("Sending .TEX");
			transfer(new FileInputStream(path), sock_out);
			Log.e("EXCEPTION", "Transfer done");
			sock_in.close();
			sock_out.close();

			socket.close();
			
            getFromServer(path, name);
 
        }catch (UnknownHostException e) {
            e.printStackTrace();
            
            Log.e("EXCEPTION", "Uknown host");
            
        }catch (IOException e) {
            e.printStackTrace();
            Log.e("EXCEPTION", "IOexception");
        }
        Log.e("EXCEPTION", "End of function");
	}
	
    // Get PDF from server
	public static void getFromServer(String path, String name) {
	try{
        Thread.currentThread().sleep(3000);
        } catch(InterruptedException e) {
        }
		Socket socket;
        try {
			socket = new Socket(InetAddress.getByName(ip_adress), 8080);

			InputStream	 sock_in = socket.getInputStream(); 
			OutputStream sock_out = socket.getOutputStream(); 
			
			PrintStream out = new PrintStream(sock_out);
			BufferedReader in = new BufferedReader(new InputStreamReader(sock_in));

            out.println(name);
			out.println("CATCH");
			
			System.out.println("Catching PDF");
			transfer(sock_in, new FileOutputStream(path.replace(".tex", ".pdf")));

			sock_in.close();
			sock_out.close();

			socket.close();
 
        }catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
