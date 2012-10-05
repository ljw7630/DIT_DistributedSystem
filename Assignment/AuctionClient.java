import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;


public class AuctionClient {
	

	private AuctionFrame gui;
	private Socket socket;
	ObjectOutputStream outputStream;

	Item item;
	Object object = new Object();
	
	public AuctionClient(String name, String sererAddr, int port) {
		
		// initialize gui
		gui = new AuctionFrame("Online auction client -- Name: " + name, this);
		try{
			socket = new Socket(sererAddr, port);
			
			// first time connect to server, tell server its name
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(name);
			
			// create object input&output stream for further use.
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.flush();
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			
			while(true){
				try{
					
					// read new data from server
					Item bidItem = (Item)inputStream.readObject();
						//System.out.println(bidItem.getSeconds());
					synchronized (object){
						
						// perform update, and keep an object locally for later use.
						gui.update(bidItem);
						item = new Item(bidItem);
						bidItem = null;
						if(!gui.isVisible()){
							break;
						}
					}
				}catch (Exception e) {
					
					// if the socket has been closed by server, it will goes to here, and we should disable the input
					gui.setMessage("Connection Closed by server.");
					gui.disableAll();
					Thread.sleep(5000);
					break;
				}
			}
		}catch (ConnectException e) {
			
			// if the server address or port number is wrong 
			gui.setMessage("Connection Refused");
			gui.disableAll();
		}catch (Exception e) {
			// other unexpected exceptions
			e.printStackTrace();
		}
	}
	
	public float getCurrentAmount(){
		synchronized (object) {
			return item.getAmount();	
		}
	}
	
	public void send(float amount) {
		synchronized (object) {
			item.setAmount(amount);
			try {
				outputStream.writeObject(item);	
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
	
	public void disconnect(){
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Problem in Socket Close");
		}
	}
	

	public static void main(String[] args) {
		
		// Initialize default parameters
		int port = 12345;
		String serverAddr = "localhost";
		String name = ManagementFactory.getRuntimeMXBean().getName();
		
		System.out.println("Usage: java program client_name host port");
		if(args.length!=3){
			System.out.println("Using default server address: " + serverAddr + ", port: " + port + " and name: " + name + " ...");
		}
		else {
			
			// parse user input parameters
			serverAddr = args[0];
			port = Integer.parseInt(args[1]);
			name = args[2];
			System.out.println("using customized server address: " + serverAddr + ", port: " + port + " and name: " + name + " ...");
		}
		
		// start the client
		new AuctionClient(name,serverAddr,port);
	}

}
