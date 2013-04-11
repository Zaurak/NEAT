import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
 
public class NeatServer {
	
	final static int N_THREADS = 2000;
    private ServerSocket listener;

	public static void main(String[] zero) {
		NeatServer server = new NeatServer();
		server.start();
	}

    // Start every threads available
	public void start() {
		Thread t;
	    try {
			this.listener = new ServerSocket(8080);
			for (int i = 0 ; i < N_THREADS ; i++) {
				t = new Thread(new ClientHandler());
				t.start();
			}
		} catch (IOException e) {	
			e.printStackTrace();
		} finally {
		}
	}

    // Represent a thread handling a client
	public class ClientHandler implements Runnable {

		private Socket client;
        
        // Transfer a file from in to out
        // in and out can be socket streams
		public final void transfer(InputStream in, OutputStream out) throws IOException {
			byte buf[] = new byte[1024];
			int n;
			while((n = in.read(buf)) > 0) {
				out.write(buf, 0, n);
			}
			out.flush();
			in.close();
			out.close();
		}

        // Execute the compilation command
		public void pdflatex(String dir, String name) throws IOException {
    		try {
		        Runtime run = Runtime.getRuntime();
		        Process proc = run.exec(
			        "pdflatex -output-directory " + dir + " " + name + ".tex" +
			        " 2&1> " + dir + name + ".outputLog");
		        String line;
		        BufferedReader input = 
                   new BufferedReader(
                    new InputStreamReader(
                        proc.getInputStream()));
                while ((line = input.readLine()) != null) {
                    System.out.print(".");
                }
                input.close();
                proc.waitFor();
                System.out.println("end process.");
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
		}

        // Main function of the thread
		public void run() {
			try {
				for (;;) {  // Infinite loop
					client = listener.accept(); // wait a connection
					System.out.println("New Client transaction:");

                    // Get the streams of the created socket
					InputStream  sock_in = client.getInputStream();
					OutputStream sock_out = client.getOutputStream();
					
					// Usefull to exchange small messages
					PrintStream out = new PrintStream(sock_out);
					BufferedReader in = new BufferedReader(new InputStreamReader(sock_in));
                    
                    // Get the file name
                    String name = in.readLine();
					String dir = "Files/";
					
					// What action the user want to perform
					// SEND => The server receive the .tex file
					// CATCH (or other) => The server send the .pdf
					String action = in.readLine();
					if (action.compareTo("SEND") == 0) {
					    System.out.println("Getting .TEX");
					    transfer(sock_in, new FileOutputStream(
					        dir + name + ".tex"));

					    System.out.println("Compiling .TEX");
					    pdflatex(dir, name);
                    } else {
                        System.out.println("Sending PDF");
                        transfer(new FileInputStream(dir + name + ".pdf"), sock_out);
                    }
					
					sock_in.close();
					sock_out.close();
					

					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
