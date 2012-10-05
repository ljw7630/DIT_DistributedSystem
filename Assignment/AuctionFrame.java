import java.awt.*;

import javax.swing.*;


public class AuctionFrame extends JFrame {
	
	AuctionClient client;
	public JTextField input;

	private JLabel _itemName;
	private JLabel _itemAmount;
	private JLabel _itemID;
	private JLabel _message;
	private JLabel itemAmount;
	private JLabel itemName;
	private JLabel itemID;
	private JLabel _timeLeft;
	private JLabel timeLeft;
	private JLabel _input;
	private JTextArea message;
	private JScrollPane scrollPane;
	
	// messy code for creating ui
	public AuctionFrame(String title, AuctionClient client){
		super(title);
		
		this.client = client;
	
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		_itemName = new JLabel("Item Name:");
		_itemName.setBounds(26, 78, 85, 14);
		getContentPane().add(_itemName);
		
		_itemAmount = new JLabel("Current Price:");
		_itemAmount.setBounds(26, 138, 97, 25);
		getContentPane().add(_itemAmount);
		
		_itemID = new JLabel("Item ID:");
		_itemID.setBounds(26, 22, 85, 14);
		getContentPane().add(_itemID);
		
		input = new JTextField();
		input.setBounds(155, 291, 108, 20);
		getContentPane().add(input);
		input.setColumns(10);
		
		_input = new JLabel("Input your offer:");
		_input.setBounds(26, 294, 108, 14);
		getContentPane().add(_input);
		
		_timeLeft = new JLabel("Time Left: ");
		_timeLeft.setBounds(155, 22, 85, 14);
		getContentPane().add(_timeLeft);
		
		timeLeft = new JLabel("New label");
		timeLeft.setBounds(280, 22, 69, 14);
		getContentPane().add(timeLeft);
		
		_message = new JLabel("Message: ");
		_message.setBounds(155, 47, 85, 14);
		getContentPane().add(_message);
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(155, 73, 331, 207);
		getContentPane().add(scrollPane);
		
		scrollPane.getVerticalScrollBar();
		
		message = new JTextArea();
		message.setWrapStyleWord(true);
		message.setFont(UIManager.getFont("TextArea.font"));
		message.setForeground(Color.BLACK);
		scrollPane.setViewportView(message);
		message.setLineWrap(true);
		message.setEditable(false);
		
		itemID = new JLabel("");
		itemID.setBounds(26, 47, 85, 14);
		getContentPane().add(itemID);
		
		itemName = new JLabel("");
		itemName.setBounds(26, 113, 85, 14);
		getContentPane().add(itemName);
		
		itemAmount = new JLabel("");
		itemAmount.setBounds(26, 174, 85, 14);
		getContentPane().add(itemAmount);
		
		this.input.addKeyListener(new EnterListener(client,this));
		this.addWindowListener(new ExitListener(client));
		
		this.setSize(512, 400);
		
		this.setVisible(true);
		
		this.setResizable(false);
	}
	
	// set message in JTextArea
	public void setMessage(String msg) {
		if(msg.isEmpty()){
			return;
		}
		String str = "ItemId: " + itemID.getText() + ", Item: " + itemName.getText()  + ". " + msg+"\n";
		message.append(str);
		
		message.setCaretPosition(message.getText().length());
	}
	
	
	// set all information provided by server
	public void update(Item item){
		itemID.setText(Integer.toString(item.getId()));
		itemName.setText(item.getName());
		itemAmount.setText(Float.toString(item.getAmount()));
		timeLeft.setText(Integer.toString(item.getSeconds()) + " secs");
		setMessage(item.getNotification());
	}
	
	public void disableAll(){
		input.setEditable(false);
	}
}
