package millee.game.mechanics;

import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.TiledLayer;

import millee.game.ApplicationMain;
import millee.game.Round;
import millee.game.initialize.Utilities;

/**
 * @author Simon
 *
 */
public class GameController {
	
	// Data structures that make up the game
	private GameGrid _grid;
	private Vector _players;
	//private Vector _goodies = new Vector();
	//private Hashtable _playerToGoodies = new Hashtable();
	
	// State variables
	private int _nGoodies = 0;
	//private boolean _isDone = false;
	protected static int clock;
	
	// Constants
	private static final int TILE_DIMENSIONS = 20;
	private static final int NUM_GOODIES_PER_PLAYER = 2;
	private static final int BONUS_SCORE = 100;
	private static final int INITIAL_SCREEN_TIME = 18;
	
	// Utilities
	private Random random = new Random();
	
	// Stuff for drawing
	private LayerManager _layers = new LayerManager();
	private int _screenWidth;
	private int _screenHeight;
	
	public GameController(Vector players, int screenWidth, int screenHeight) {
		this._players = players;
		this._screenWidth = screenWidth;
		this._screenHeight = screenHeight;
		
		// Create base grid object
		this._grid = new GameGrid(
				screenWidth/TILE_DIMENSIONS,
				screenHeight/TILE_DIMENSIONS,
				TILE_DIMENSIONS
				);
		
		_layers.append(_grid.getTiledLayer());
		clock = 0;
	}

	/**
	 * Takes current players and builds a complete gamegrid configuration
	 * @return String configuration
	 */
	public StringBuffer buildConfiguration() {
		StringBuffer broadcastString = new StringBuffer("");
		
		// Holder variables
		Player p = null;
		int i, goodieType, nPlayers = _players.size();
		
		// Begin random coordinates
		int x = random.nextInt(_grid.width);
		int y = random.nextInt(_grid.height);
		
		// Assign player coordinates
		for (i = 0; i < nPlayers; i++) {
			p = (Player) _players.elementAt(i);
			
			// Don't overlap players
			while (_grid.hasPlayerAt(x, y)) {
				x = random.nextInt(_grid.width);
				y = random.nextInt(_grid.height);
			}
			
			// Take this opportunity to assign players' colors - needs to be done before inserting
			// player on the board because the color determines the player's avatar.
			// Uncomment the rest below to have colors change each round.
			p.setColor((p.getColor()%nPlayers)+1);
			
			this.insertPlayer(p, x, y);
			broadcastString.append(i);
			broadcastString.append(',');
			broadcastString.append(x);
			broadcastString.append(',');
			broadcastString.append(y);
			broadcastString.append(';');
		}
		
		broadcastString.append('|');
		
		// Add goodies - two for each player
		for (i = 0; i < nPlayers*NUM_GOODIES_PER_PLAYER; i++) {
			goodieType = (i%nPlayers)+1;

			// Don't put goodies in cells that already have them or where players are
			while (_grid.hasPlayerAt(x, y) || _grid.hasGoodieAt(x, y)) {
				x = random.nextInt(_grid.width);
				y = random.nextInt(_grid.height);
			}
			
			_grid.insertGoodie(new Goodie(goodieType), x, y);
			_nGoodies++;
			broadcastString.append(goodieType);
			broadcastString.append(',');
			broadcastString.append(x);
			broadcastString.append(',');
			broadcastString.append(y);
			broadcastString.append(';');
		}
		
		return broadcastString;
	}
	
	/**
	 * Takes in a configuration string and builds up the GameGrid
	 * @param input
	 */
	public void buildFromConfiguration(String input) {
		String[] blocks = Utilities.split(input, "|", 2);
		
		// Get the player information
		String[] sPlayers = Utilities.split(blocks[0], ";", 0);
		String[] playerInfo = null;
		Player tmpPlayer = null;
		int x,y;
		
		//ApplicationMain.log.trace("sPlayers = " + sPlayers);
		for (int i = 0; i < sPlayers.length; i++) {
			//ApplicationMain.log.trace("splayers[" + i + "] = " + sPlayers[i]);
			//ApplicationMain.log.trace("putting player " + i + " on the board");
			playerInfo = Utilities.split(sPlayers[i], ",", 3);
			//ApplicationMain.log.trace("playerInfo = " + playerInfo);
			tmpPlayer = (Player) _players.elementAt(i);
			//ApplicationMain.log.trace("player's name is " + tmpPlayer);
			x = Integer.parseInt(playerInfo[1]);
			y = Integer.parseInt(playerInfo[2]);
			
			// Take this opportunity to assign players' colors - needs to be done before inserting
			// player on the board because the color determines the player's avatar.
			// Uncomment the rest below to have colors change each round.
			tmpPlayer.setColor((tmpPlayer.getColor()%_players.size())+1);
			
			this.insertPlayer(tmpPlayer, x, y);
		}
		
		// Get the goodie information
		String[] sGoodies = Utilities.split(blocks[1], ";", 0);
		String[] goodieInfo = null;
		Goodie tmpGoodie = null;
		for (int i = 0; i < sGoodies.length; i++) {
			goodieInfo = Utilities.split(sGoodies[i], ",", 3);
			tmpGoodie = new Goodie(Integer.parseInt(goodieInfo[0]));
			x = Integer.parseInt(goodieInfo[1]);
			y = Integer.parseInt(goodieInfo[2]);
			_grid.insertGoodie(tmpGoodie, x, y);
			_nGoodies++;
		}
	}
	
	// Place one player on the field
	private void insertPlayer(Player p, int cellX, int cellY) {
		p.setupSprites(_layers);
		p.setTargetNumGoodies(NUM_GOODIES_PER_PLAYER);
		
		p.x = cellX;
		p.y = cellY;
		p.placeSprites(TILE_DIMENSIONS*cellX, TILE_DIMENSIONS*cellY);
		
		_grid.getCellAt(cellX, cellY).addPlayer(p);
	}
	
	public void loadGoodieStackVisual(int playerID) {
		TiledLayer tl = ((Player) _players.elementAt(playerID)).goodies.getTiledLayer();
		tl.setPosition(0, _screenHeight-TILE_DIMENSIONS+5);
		_layers.insert(tl, 0);
	}

	/**
	 * Takes a player and a character 'command', executes it on the board
	 * @param playerID
	 * @param input
	 */
	public void interpretCommand(int playerID, char input) {
		switch (input) {
		case 'u':
			this.movePlayer(playerID, 0, -1);
			break;
		case 'd':
			this.movePlayer(playerID, 0, 1);
			break;
		case 'r':
			this.movePlayer(playerID, 1, 0);
			break;
		case 'l':
			this.movePlayer(playerID, -1, 0);
			break;
		case 'x':
			this.playerDrop(playerID);
			break;
		case 'n':
			break;
		}
	}
	
	// Moves one player character in terms of cells
	private void movePlayer(int id, int dx, int dy) {
		Player p = (Player) _players.elementAt(id);
		
		// Get player off of the cell he's currently on
		_grid.getCellAt(p.x, p.y).removePlayer(p);
		
		// Move while wrapping around borders
		p.x = (p.x + _grid.width + dx) % _grid.width;
		p.y = (p.y + _grid.height + dy) % _grid.height;
		//ApplicationMain.log.trace("P: " + p.x + ", " + p.y);
		p.placeSprites(TILE_DIMENSIONS*p.x, TILE_DIMENSIONS*p.y);
		
		// Get destination cell (where the player landed)
		GameCell cNew = _grid.getCellAt(p.x, p.y);
		cNew.addPlayer(p);
		
		// Now check for 'collisions' with goodies
		if (cNew.hasGoodie()) { // && p.assignedColor() == cNew.getGoodie().getType()) {
			p.collectGoodie(cNew.getGoodie());
			ApplicationMain.log.trace("Picked up Goodie: " + cNew.getGoodie());
			cNew.unsetGoodie();
			_grid.setTileGrass(p.x, p.y);
			_nGoodies--;
		}
	}
	
	private void playerDrop(int id) {
		Player p = (Player) _players.elementAt(id);
		
		// Can't drop if the spot has a goodie already
		if (_grid.hasGoodieAt(p.x, p.y)) {
			ApplicationMain.log.info("Player " + p.getID() + " couldn't drop on a spot with a goodie already.");
			return;
		}
		
		Goodie g = p.dropGoodie();
		
		if (g == null) { return; }
		
		_grid.insertGoodie(g, p.x, p.y);
		_nGoodies++;
		
		ApplicationMain.log.info("Player " + p.getID() + " dropped goodie on " + p.x + "," + p.y);
		this.movePlayer(id, 0, -1);
	}
	
	/**
	 * Draw things to the screen
	 */
	public void updateScreen(int localID) {
		// Animate the players and their goodies if necessary
		Player p = null;
		for (int i = 0; i < _players.size(); i++) {
			p = (Player) _players.elementAt(i);
			p.animateIfNeeded();
		}
		
		_layers.paint(Round.graphics, 0, 0);
		
		// Status messages specific to the local player
		p = (Player) _players.elementAt(localID);
		if (!p.isInGoodStanding) {
			setFloatingStatusMessage("DROP!");
			p.goodies.animate();
		}
		
		if (clock < INITIAL_SCREEN_TIME) {
			showInitialScreen(p);
		} else {
			showLowerStatusMessage("Collect " + colorFromID(p.getColor()));
		}
		
	}
	
	
	private void showInitialScreen(Player localPlayer) {
		
		Round.graphics.setColor(0,0,0);
		
		Round.graphics.fillRect(_grid.width, _grid.height, _grid.width*(TILE_DIMENSIONS-2), _grid.height*(TILE_DIMENSIONS-2));
		Round.graphics.setColor(255, 255, 255);
		Round.graphics.drawRect(_grid.width, _grid.height, _grid.width*(TILE_DIMENSIONS-2), _grid.height*(TILE_DIMENSIONS-2));
		
		int line = 1;
		int startX = 3*_grid.width/2;
		int startY = 3*_grid.height/2;
		
		Round.graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		Round.graphics.drawString("New round!", startX, startY*line++, Graphics.TOP | Graphics.LEFT);
		
		Round.graphics.drawString("----------------", startX, startY*line++, Graphics.TOP | Graphics.LEFT);
		Round.graphics.drawString("", startX, startY*line++, Graphics.TOP | Graphics.LEFT);

		String colorToCollect = colorFromID(localPlayer.getColor());
		//Round.graphics.drawString("In this round, you must ", startX, startY*line++, Graphics.TOP | Graphics.LEFT);
		Round.graphics.drawString("Collect colour: ", startX, startY*line++, Graphics.TOP | Graphics.LEFT);
		Round.graphics.drawString("", startX, startY*line++, Graphics.TOP | Graphics.LEFT);

		Round.graphics.drawString("		" + colorToCollect.toUpperCase(), startX, startY*line++, Graphics.TOP | Graphics.LEFT);
		
		Round.graphics.drawString("", startX, startY*line++, Graphics.TOP | Graphics.LEFT);

		String avatarString = "Your avatar is: ";
		Round.graphics.drawString(avatarString, startX, startY*line, Graphics.TOP | Graphics.LEFT);
		
		Image img = localPlayer.getAvatar();
		Round.graphics.drawImage(img, startX + 5*avatarString.length()+4, startY*line++, Graphics.TOP | Graphics.LEFT);
		
		Round.graphics.drawString("", startX, startY*line++, Graphics.TOP | Graphics.LEFT);		
		Round.graphics.drawString("Starting in " + (INITIAL_SCREEN_TIME - clock)/3 + " s", startX, startY*line++, Graphics.TOP | Graphics.LEFT);
	}
	
	public void setFloatingStatusMessage(String msg) {
		Round.graphics.setColor(0,0,0);
		Round.graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
		Round.graphics.drawString(msg, 3, _grid.height*TILE_DIMENSIONS, Graphics.BOTTOM | Graphics.LEFT);
	}

	private void showLowerStatusMessage(String msg) {
		Round.graphics.setColor(255,255,255);
		Round.graphics.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		Round.graphics.drawString(msg, _screenWidth, _screenHeight, Graphics.BOTTOM | Graphics.RIGHT);
	}

	/**
	 * Gets a textual representation of the scores
	 * @return String score report
	 */
	public String generateScoreReport() {
		String scoreReport = "";
		Vector rankedPlayers = generateRankedPlayers();
		Player p = null;
		
		scoreReport += "---------------------------------------------------\n";
		scoreReport += "Group Score is " + Player.groupScore + "\n";
		scoreReport += "---------------------------------------------------\n";
		
		for (int i = 0; i < rankedPlayers.size(); i++) {
			p = ((Player) rankedPlayers.elementAt(i));
			scoreReport += "#" + (i+1) + ": " + p.getName() + " = " + p.getScore() + "\n";
		}
		return scoreReport;
	}
	
	/**
	 * Mapping between ID number and a color string
	 * @param id
	 * @return String
	 */
	private String colorFromID(int id) {
		switch (id) {
			case 1: return "BLACK";
			case 2: return "RED";
			case 3: return "GREEN";
			case 4: return "BLUE";
			default: return "UNKNOWN_COLOR";
		}
	}
	
	public boolean isDone() {
		if (_nGoodies != 0) { return false; }
		
		Player p = null;
		
		// Check that each player has only his respective color goodies
		for (int i = 0; i < _players.size(); i++) {
			p = (Player) _players.elementAt(i);
			if (!p.isFinished) { return false; }
		}
		
		return true;
	}
	
	/**
	 * Increment time counter
	 */
	public void clockTick() {
		clock++;
	}
	
	private Vector generateRankedPlayers() {
		final int nPlayers = _players.size();
		
		Vector rankedPlayers = new Vector();
		Player[] playerPool = new Player[nPlayers];
		_players.copyInto(playerPool);
		
		// Find each 'minimum time' Player, entering them into rankedPlayers
		int i, min, winnerIndex = 0;
		while (rankedPlayers.size() != nPlayers) {
			min = Integer.MAX_VALUE;
			
			// Find the minimum score out of those that are left
			for (i = 0; i < nPlayers; i++) {
				if (playerPool[i] == null) { continue; }
				
				if (playerPool[i].timeFinished < min) {
					min = playerPool[i].timeFinished;
					winnerIndex = i;
				}
			}
			
			// Take the player out of the pool
			rankedPlayers.addElement(playerPool[winnerIndex]);
			playerPool[winnerIndex] = null;
		}
		
		// Award the players by rank
		Player pTmp = null;
		for (i = 0; i < nPlayers; i++) {
			pTmp = (Player) rankedPlayers.elementAt(i);
			pTmp.incrementScore(BONUS_SCORE*(nPlayers-i));
		}
		
		return rankedPlayers;
	}
}
