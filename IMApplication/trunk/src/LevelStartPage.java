import javax.microedition.lcdui.StringItem;


public class LevelStartPage extends Screen {

	public LevelStartPage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
		
		StringItem str = new StringItem("Colour Colour", "Level 1: Colours");
		StringItem str2 = new StringItem(null, "Ready! Set! Go!");
		
		this.append(str);
		this.append(str2);
		this.addCommand(startCommand);
		this.addCommand(exitCommand);
		
	}

}
