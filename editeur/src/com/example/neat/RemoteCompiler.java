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
 
 
public class RemoteCompiler {       
    
    static String ip_adress;
    
    public static void setIp(String ip) {
        ip_adress = ip;
    }
    
    public static void compile(String path) {
        sendToServer(path);
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
	public static void sendToServer(String path) {
		Socket socket;
        try {
			socket = new Socket(InetAddress.getByName(ip_adress), 8080);

			InputStream	 sock_in = socket.getInputStream(); 
			OutputStream sock_out = socket.getOutputStream(); 
			
			PrintStream out = new PrintStream(sock_out);
			BufferedReader in = new BufferedReader(new InputStreamReader(sock_in));

            out.println(path);
			out.println("SEND");

			System.out.println("Sending .TEX");
			transfer(new FileInputStream(path + ".tex"), sock_out);
			
			sock_in.close();
			sock_out.close();

			socket.close();
			
            getFromServer(path);
 
        }catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
	}
	
    // Get PDF from server
	public static void getFromServer(String path) {
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

            out.println(path);
			out.println("CATCH");
			
			System.out.println("Catching PDF");
			transfer(sock_in, new FileOutputStream(path + ".pdf"));

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
