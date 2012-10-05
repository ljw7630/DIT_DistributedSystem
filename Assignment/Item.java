import java.io.Serializable;

// a class use for communication, store all data needed for client & server.
public class Item implements Serializable{

	int id; // item id

	String name; // item name

	float amount; // item current price

	String currentOwner; // item current owner

	String ownerAddr; // owner information

	String notification; // store messages if server or client want to tell something to each other.

	int seconds;
	
	
	// constructor
	public Item(int id, String name, float amount){
		this.id = id;
		this.name = name;
		this.amount = amount;
		currentOwner = new String();
		ownerAddr = new String();
		notification = new String();
	}
	
	// copy constructor
	public Item(Item item){
		this.id = item.getId();
		this.name = item.getName();
		this.amount = item.getAmount();
		this.currentOwner = item.getCurrentOwner();
		this.ownerAddr = item.getOwnerAddr();
		this.notification = item.getNotification();
		this.seconds = item.getSeconds();
	}
	
	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getAmount() {
		return amount;
	}

	public String getCurrentOwner() {
		if(currentOwner.isEmpty()==true)
			return "NULL";
		else
			return currentOwner;
	}

	public void setCurrentOwner(String currentOwner) {
		this.currentOwner = currentOwner;
	}

	public String getOwnerAddr() {
		return ownerAddr;
	}

	public void setOwnerAddr(String ownerAddr) {
		this.ownerAddr = ownerAddr;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
