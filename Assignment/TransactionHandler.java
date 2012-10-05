import java.io.*;
import java.net.*;
import java.util.*;

public class TransactionHandler extends Thread{
	
	private static Vector<Item> vectorItems = new Vector<Item>(); // the item list
	
	private static Integer index = new Integer(-1);  // use as the index of item list

	private static Notifier notifier = null;  // global timer.
	
	private static AuctionServer server;
	
	private static int duration;

	public static void setServer(AuctionServer server) {
		TransactionHandler.server = server;
	}

	public static void setDuration(int seconds) {
		TransactionHandler.duration = seconds;
	}

	Socket socket;

	ObjectInputStream objectInputStream;
	ObjectOutputStream objectOutputStream;
	String name;
	private static Vector<TransactionHandler> clients = new Vector<TransactionHandler>();
	
	public int getClientsCount(){
		return clients.size();
	}
	
	public TransactionHandler(String name, Socket socket) throws IOException{
		this.name = name;
		this.socket = socket;
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.flush();
		objectInputStream = new ObjectInputStream(socket.getInputStream());
	}
	
	// close all sockets.
	public static void disconnect(){
		broadcast(0,"All items have been displayed, good bye.");
		for(int i=0;i<clients.size();++i){
			try {
				clients.elementAt(i).socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try{
			sleep(1000);
		}catch (Exception e) {
			// TODO: handle exception
		}
		server.shutDown();
	}
	
	// read files to get all items list
	public static void initializeItems() throws IOException{
		BufferedReader bufferedReader = new BufferedReader(new FileReader("Items.txt"));
		String lineOfText = null;
		while((lineOfText = bufferedReader.readLine())!=null){
			String[] strings= lineOfText.split(" ");
			int id = Integer.parseInt(strings[0]);
			String name = strings[1];
			float amount = Float.parseFloat(strings[2]);
			Item item = new Item(id,name,amount);
			vectorItems.addElement(item);
		}
		index = 0;
	}

	public static Item getCurrentItem(){
		synchronized (index) {
			return vectorItems.elementAt(index);
		}
	}	
	
	public void run(){
		clients.addElement(this);
		
		// if the first time run, need to initialize timer
		if(notifier==null){
			notifier = new Notifier();
			notifier.start();
		}
		while(true){
			try {
				update();
			} catch (Exception e) {
				// TODO: handle exception
				//e.printStackTrace();
				System.out.println("Connection with " + name + " closed");
				break;
			}
		}
	}
	
	// waiting for client raise new bid
	public void update() throws IOException, ClassNotFoundException{
		Item clientItem = (Item) objectInputStream.readObject();
		Item item = getCurrentItem();
		synchronized (item) {
			if(item.getId()!=clientItem.getId()){
				
				// extreme case, client send bid after time out
				sendNotification("Time out, you cannot bid for item, id: "+ clientItem.getId() +"name: "+clientItem.getName()+".");
				try {
					sleep(1000*2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(item.getAmount()<clientItem.getAmount()){
				
				// new bid is raised, need to update information and reset timer.
				item.setAmount(clientItem.getAmount());
				item.setCurrentOwner(name);
				notifier.resetTimer("A new bid is raised, timer is reset, bidder: "+name+".");
			}
		}
	}
	
	// send notification tp only one client
	public void sendNotification(String str) throws IOException{
		Item item = new Item(-1, null, 0);
		item.setNotification(str);
		objectOutputStream.writeObject(item);
	}
	
	
	// send current item's status to all clients
	public static void broadcast(int seconds,String notification){
		Item item = getCurrentItem();
		synchronized (item) {
			item.setSeconds(seconds);
			item.setNotification(notification);
			Item newItem = new Item(item);
			System.out.println(newItem.getSeconds());
			synchronized (clients) {
				Iterator<TransactionHandler> itr = clients.iterator();
				
				while(itr.hasNext()){
					TransactionHandler handler = itr.next();
					try{					
						handler.objectOutputStream.writeObject(newItem);
						handler.objectOutputStream.flush();
					}catch (Exception e) {
						// TODO: handle exception
						itr.remove();
					}
				}
			}
		}
	}
	
	
	// get the index of current item
	public static int getIndex() {
		return index;
	}

	// if index is greater or equal to array length, all items have been display, call for disconnect all clients.
	public static boolean setIndex(int index) {
		if(index<vectorItems.size()){
			TransactionHandler.index = index;
			System.out.println("Current index is: " + TransactionHandler.index);
			return true;
		}
		else {
			disconnect();
			return false;
		}
	}
	
	
	// inner class notifier, use as timer.
	class Notifier extends Thread{
		long start;
		long cur; // current time
		Object object = new Object();
		
		public Notifier(){
			
		}
		
		// reset timer
		public void resetTimer(String msg){
			synchronized (object) {
				start = System.currentTimeMillis();	
				broadcast(duration, msg);
			}
		}
		
		private int calculateSeconds(long start, long current){
			return (int)(start+1000*duration-current)/1000;
		}
		
		public void run(){
			
			start = System.currentTimeMillis();	
			
			while(true){
				
				// flag for reset timer
				Boolean flag = false;
				
				synchronized (object) {
					cur = System.currentTimeMillis();
					int seconds = calculateSeconds(start,cur);
					
					// time out, display next item.
					if((cur-start)/1000>=duration){
						Item item = getCurrentItem();
						synchronized (item) {
							
							// broadcast timeout
							broadcast(seconds, "Bid time out, winner: " + item.getCurrentOwner()+".");	
						}
						synchronized (index) {
							if(!TransactionHandler.setIndex(index+1)){
								break;
							}
						}
						
						
						flag = true;
					}
					else{
						// count down
						TransactionHandler.broadcast(seconds,"");
					}					
				}

				try{
					// sleep for 1 second to reduce network packages.
					sleep(1000);
					
					if(flag){
						start = System.currentTimeMillis();	
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					break;
				}
			}
		}
	}	
}
