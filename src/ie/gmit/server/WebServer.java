
package ie.gmit.server;
//Contains classes for all kinds of I/O activity
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//Contains basic networking classes
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WebServer {
	private ServerSocket ss; //A server socket listens on a port number for incoming requests
	static String ip;
	//The first 1024 ports require administrator privileges. We'll use 8080 instead. The range 
	//of port numbers runs up to 2 ^ 16 = 65536 ports.
	private static final int SERVER_PORT = 7777;  
	
	//The boolean value keepRunning is used to control the while loop in the inner class called
	//Listener. The volatile keyword tells the JVM not to cache the value of keepRunning during
	//optimisation, but to check it's value in memory before using it.
	private volatile boolean keepRunning = true;
	
	
	
	//A null constructor for the WebServer class
	private WebServer(){
		try { //Try the following. If anything goes wrong, the error will be passed to the catch block
			
			ss = new ServerSocket(SERVER_PORT); //Start the server socket listening on port 8080
			
			/* A Thread is a worker. A runnable is a job. We'll give the worker thread called "server"
			 * the job of handling incoming requests from clients.
			 * Note: calling start results in a new JVM stack being created. The run() method of the Thread
			 * or Runnable will be placed on the new stack and executed when the Thread Scheduler (consider
			 * this a cantankerous and uncommunicative part of the JVM) decides so. There is absolutely NO
			 * GUARANTEE of either order or execution time. We can however ask the Thread Scheduler 
			 * (politely) to run a thread as a max, min or normal priority. 
			 */
			Thread server = new Thread(new Listener(), "Web Server Listener"); //We can also name threads
			server.setPriority(Thread.MAX_PRIORITY); //Ask the Thread Scheduler to run this thread as a priority
			server.start(); //The Hollywood Principle - Don't call us, we'll call you
			System.out.println("Server started and listening on port " + SERVER_PORT);
			
		} catch (IOException e) { //Something nasty happened. We should handle error gracefully, i.e. not like this...
			System.out.println("Yikes! Something bad happened..." + e.getMessage()); // error notification
		}
	}
	
	//A main method is required to start a standard Java application
	public static void main(String[] args) {
		
			new WebServer(); // start web server
		 
	}
	
	
	
	/* The inner class Listener is a Runnable, i.e. a job that can be given to a Thread. The job that
	 * the class has been given is to intercept incoming client requests and farm them out to other
	 * threads. Each client request is in the form of a socket and will be handled by a separate new thread.
	 */
	private class Listener implements Runnable{ //A Listener IS-A Runnable
		
		private BlockingQueue<Thread> queue = new ArrayBlockingQueue<Thread>(10);
		
		//The interface Runnable declare the method "public void run();" that must be implemented
		public void run() {
			int counter = 0; //A counter to track the number of requests
			
			while (keepRunning){ //Loop will keepRunning is true. Note that keepRunning is "volatile"
				try { //Try the following. If anything goes wrong, the error will be passed to the catch block
					
					Socket s = ss.accept(); //This is a blocking method, causing this thread to stop and wait here for an incoming request
					ip = s.getRemoteSocketAddress().toString(); // ip address that belongs to the client connected
					System.out.println(ip + " Connected"); // print new ip that has connected
					
				    Thread job = new Thread(new HTTPRequest(s), "T-" + counter); //Give the new job to the new worker and tell it to start work
				    //System.out.println(ip + " got here");
				    do{
					    queue.put(job); // add to blocking queue
					    counter++; //Increment counter
						queue.poll(); // taking from blocking queue
						job.start(); //start last jobs taken out
					  }while(!queue.isEmpty()); // while something is in queue
					
					
					 // Handle the client requests in order of FCFS
					
				} catch (IOException e) { //Something nasty happened. We should handle error gracefully, i.e. not like this...
					System.out.println("Error handling incoming request..." + e.getMessage());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}//End of inner class Listener
	
	
	/* The inner class HTTPRequest is a Runnable, i.e. a job that can be given to a Thread. The job that
	 * the class has been given is to handle an individual client request, by reading information from the
	 * socket's input stream (bytes) and responding by sending information to the socket's output stream (more
	 * bytes).
	 */
	private class HTTPRequest implements Runnable{
		private Socket sock; //A specific socket connection between some computer on a network and this programme
	
		
		private HTTPRequest(Socket request) { //Taking the client socket as a constructor enables the Listener class above to farm out the request quickly
			this.sock = request; //Assign to the instance variable sock the value passed to the constructor. 
		}
		
		
		//The interface Runnable declare the method "public void run();" that must be implemented
        public void run() {
            try{ //Try the following. If anything goes wrong, the error will be passed to the catch block
            	
            	// For Accessing the files stored on the server
            	  ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
            	File f = new File("server");
                File[] paths;
              
		        paths = f.listFiles(); // returns pathnames for files and directory
                PrintWriter log = new PrintWriter("ServerLog.txt", "UTF-8"); // file logs will be saved in
				DateFormat curTime = new SimpleDateFormat("hh:mm a"); // the time
				DateFormat curDate = new SimpleDateFormat("dd MM yyyy "); // the date
            	//Read in the request from the remote computer to this programme. This process is called Deserialization or Unmarshalling
            	ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                String command = (String)in.readObject(); //Deserialise the request into an Object
                System.out.println("[Recieved] " + command); // print what we recieved from client before processing
              
                if(command.toLowerCase().contains("Connection Successful!".toLowerCase())){
                	
                	System.out.println("Connection Successful!"); // connection set up
                	log.println("[INFO] Connection from " + ip + " at " + curTime + " on " + curDate); // log this connection
                	
                }else if(command.toLowerCase().contains("LIST FILES".toLowerCase())){
                	
                	//System.out.println("File list requested!");
					log.println("[INFO] Listing requested by " + ip + " at " + curTime + " on " + curDate); // log this connection
					String list = "";
			         
			         // for each pathname in pathname array
			         for(File path:paths)
			         {
			           list += path + "\n";
			           log.println("[INFO] " + path + " was sent to" + ip); // log sent file
			         }
					
			         // Sends file and directory paths then logs the conversation
			         out.writeObject(list);
			         out.flush();
			         
                }else{
                     // check to see if the command matches anyfile witht he download tag
                	 String fname;
                	 for(File path:paths)
                	 {
                		 fname = path.toString();
                		 int count;
                		 if( command.toLowerCase().contains("DL:" + fname.toLowerCase())){
                			
                			 try {
                		            FileOutputStream fout = new FileOutputStream(fname);
                		            byte[] bytes = new byte[16*1024]; // we send files over the stream in bytes

                		            while ((count = in.read(bytes)) > 0) { // write all bytes out until there none left
                		               fout.write(bytes, 0, count);
                		            }
                		            log.println("[INFO] " + path + " requested by " + ip + " at " + curTime + " on " + curDate); // log this connection
                		            fout.close(); // close stream
                		            
                			 	} catch (FileNotFoundException ex) {
                		            System.out.println("File not found. "); // file requested not found
                		        }
                			 
                			 
                		 }
                	
                	 } // else end
                	
                
                }
                
                //Write out a response back to the client. This process is called Serialization or Marshalling
                //String message = "<h1>Happy Days</h1>";
            	
                
                log.close(); // free resource
                out.close(); //Tidy up after and don't wolf up resources unnecessarily
                
            } catch (Exception e) { //Something nasty happened. We should handle error gracefully, i.e. not like this...
            	System.out.println("Error processing request from " + sock.getRemoteSocketAddress());
            	e.printStackTrace();
            }
        }
	}//End of inner class HTTPRequest
}//End of class WebServer