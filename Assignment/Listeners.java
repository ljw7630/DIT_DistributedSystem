import java.awt.event.*;

class EnterListener extends KeyAdapter{

	AuctionClient client;

	AuctionFrame gui;
	
	public EnterListener(AuctionClient client, AuctionFrame gui) {
		this.client = client;
		this.gui = gui;
	}
	
	
	// if user type enter, perform value check, and send valid data to server.
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			try{
				float f = Float.parseFloat(gui.input.getText());
				if(f <= client.getCurrentAmount()){
					gui.setMessage("error: Amount is less than or equal to origin");
				}
				else {
					client.send(f);	
				}
			}catch (Exception err) {
				// if error occurred, send it on screen.
				gui.setMessage("error: " + err.getMessage());
				//System.out.println("error in keyListener");
			}
			gui.input.setText("");
		}
	}
}


class ExitListener extends WindowAdapter{

	AuctionClient client;
	
	public ExitListener(AuctionClient client){
		this.client = client;
	}
	
	// close the program when user click "Close" button on top-right window
	public void windowsClosing(WindowEvent e){
		client.disconnect();
		System.exit(0);
	}
}