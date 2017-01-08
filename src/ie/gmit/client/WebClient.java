package ie.gmit.client;

/* Name - Conor Tighe GMIT ID - G00314417
 * WebClient is thr class that sets up the main menu and controls the threads
 * that will interact with the server in the background */
import java.io.*; //We need the Java IO library to read from the socket's input stream and write to its output stream
import java.net.*; //Sockets are packaged in the java.net library
import java.util.Scanner;

import ie.gmit.requests.Access;

public class WebClient { //The class WebClient must be declared in a file called WebClient.java
	
	//Main method to get the ball rolling
	public static void main(String[] args) throws Throwable{
		 
		Scanner sc = new Scanner(System.in); // user input
		Access acc = new Access(); // get server details
		DomParser init = new DomParser(acc); // Set up XML parser
		init.setData(acc); // initilize Access class
		init.parser(); // Start parsing XML
		ConnectionWorker connect = new ConnectionWorker(); // Worker that will be incharge of connecting
		
		boolean flag = true; // Contol while	
		String username = acc.getUsername();
		int port = Integer.parseInt(acc.getServerPort());
		String host = acc.getServerHost();
		String dir = acc.getDownloadDir();
		boolean connection = false; // control access to server and thread actions
		int choice; // user input control
		
		while(flag == true){
			
		System.out.println("Enter the corresponding number to pick a option");
		if(connection == false){
		System.out.println("1. Connect to server");
		}
		else{ System.out.println("1. Disconnect from server? "); }
		System.out.println("2. Print File Listing");
		System.out.println("3. Download File");
		System.out.println("4. Quit");
		choice = sc.nextInt();
		
		if(connection == false && choice !=1){
			System.out.println("\nNo connenction detected"); // cant access 2&3 without Connection Worker
		}
		
		if(choice == 1){
			if(connection == false){
			System.out.println(acc); // Print toString
			connection = true; // Open connection
			}else{ System.out.println(username + " Disconnected " );
			connection = false; //  close connection 
			}
			
		}
		//Loop 10 times to simulate 10 concurrent connections to the server. Examine the output and increase to 10000 
		if (connection == true){
			
			String response;
			String fileRequest;
	
		   try { //Attempt the following. If something goes wrong, the flow jumps down to catch()	
			   
			if(choice == 1){ // connect to server
				connect.setSocket(new Socket(host, port));
				connect.run();
			}else if(choice == 2){ // print files on server
				PrintWorker prnt = new PrintWorker();
				prnt.setSocket(new Socket(host, port));
				prnt.run();
				response = prnt.getResponse();
				System.out.println(response);
			}else if(choice == 3){ // download requested file
				System.out.println("Please enter the file you would like to request");
				sc.nextLine(); // flush the buffer
				fileRequest = sc.nextLine();
				DownloadWorker download = new DownloadWorker(fileRequest,dir);
				download.setSocket(new Socket(host, port));
				download.run();
			}else if(choice == 4){ // quit
				flag = false; // release from while
				sc.close(); // tidy up
				connect.getSocket().close(); // close socket
				System.out.println(username + " Disconnected " ); // user is disconnected from socket
			}	
						
						
	    } catch (Exception e) { //Deal with the error here. A try/catch stops a programme crashing on error  
			System.out.println("Error: " + e.getMessage());
		    }//End of try /catch
				
		  }//End of for loop
		
		}//End of while
		
		System.out.println("Main method will return now...."); // program will exit
	}//End of main method
}//End of class

