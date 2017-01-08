package ie.gmit.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ie.gmit.requests.Access;

public class DownloadWorker extends Access {
	private static final long serialVersionUID = 1L;

	File fileResponse; // where we will store the incoming file
	String fileName; // name of requested file
	String path; // pathway of file
	
	public DownloadWorker(String fileName, String path) {
		this.fileName = fileName;
		this.path = path;
	}
		
		public void run(){
			
			try {
				
				File file = new File(path + fileName); // create file
				//long length = file.length();
				int count; // counter for reading the bytes
				byte[] bytes = new byte[(int)file.length()]; // where the incoming bytes will go
				
				String message = "DL:" + fileName; // message that will be sent to the server
				ObjectOutputStream out = new ObjectOutputStream(getSocket().getOutputStream()); // message stream for communication
				FileOutputStream fout = new FileOutputStream(file); // file out stream
				FileInputStream fin = new FileInputStream(file); // file in stream
				
				System.out.println("Attemping to retrieve " + fileName); // let client know we are now ready to get the file
				
				out.writeObject(message); //Serialise
				out.flush(); //Ensure all data sent by flushing buffers
				Thread.yield(); //Pause the current thread for a short time (not used much)
				
				/*While loop for readinf the incoming bytes and stoing them in the download directory */
				while ((count = fin.read(bytes)) > 0)
				{
				  fout.write(bytes, 0, count);
				}
				System.out.println(fileName + " download to" + path); // inform client where they can find there new file
				
				// Tidy up
				fin.close();
				fout.close();
				out.close();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
}
