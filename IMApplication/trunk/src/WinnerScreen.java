import javax.microedition.lcdui.StringItem;


public class WinnerScreen extends Screen {

	public WinnerScreen (String title) {
		super(title);
		// TODO Auto-generated constructor stub
		
		StringItem str = new StringItem("Colour Colour", "Game Over");
		
		StringItem scores = new StringItem(null, "Congratulations Ram!");
		StringItem congrats = new StringItem(null, "You won with a score of 120.");
		
		
		this.append(str);
		this.append(congrats);
		
		this.addCommand(okCommand);
		this.addCommand(exitCommand);
		
	}

}
