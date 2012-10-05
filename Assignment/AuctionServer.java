import java.net.*;
import java.io.*;

public class AuctionServer {
	
	private ServerSocket serverSocket;
	
	public AuctionServer(int port, int duration) throws IOException {
		serverSocket = new ServerSocket(port);
		TransactionHandler.initializeItems();
		TransactionHandler.setServer(this);
		TransactionHandler.setDuration(duration);
	}
	
	public void start(){
		System.out.println("The server has been stared up, waiting for clients..");
		while(true){
			try{
				Socket client = serverSocket.accept();
				DataInputStream inputStream = new DataInputStream(client.getInputStream());
				String name = inputStream.readUTF();
				System.out.println("New client "+name+" enter.");
				TransactionHandler handler = new TransactionHandler(name, client);
				handler.start();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	// system exit
	public void shutDown(){
		System.out.println("Transaction closed");
		System.exit(0);
	}
	

	// Main entry
	public static void main(String[] args) {
		
		// default port, duration
		int port = 12345;
		int duration = 30;
		System.out.println("Usage: java program port duration_per_item_in_secs");
		if(args.length!=2){
			System.out.println("Using default port: " 
					+ port + " and duration: " + duration);
		}
		else {
			port = Integer.parseInt(args[0]);
			duration = Integer.parseInt(args[1]);
			System.out.println("using customized port: " + port + " and duration: " + duration + "...");
		}
		try {
			
			// start the main server thread
			(new AuctionServer(port,duration)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
