import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements ActionListener, KeyListener{
	static final int gameX = 0, gameY = 50, gameWidth = 320, gameHeight = 402;//game area
	
	//The solid sprites image 
	Image sheet, sheet2, wall, player, floor, 
	sword, monster, explode, shield, upStairs, downStairs, 
	swordIcon, shieldIcon, redKey, redDoor, redKeyIcon, yellowDoor, blueDoor,
	yellowKey, yellowKeyIcon, blueKey, blueKeyIcon,
	masterSword, hylianShield, masterSwordIcon, hylianShieldIcon;
	
	//The sprites image for animation
	Image[] playerFrames, monsterFrames, bossFrames, starFrames, magmaFrames, 
	monster2Frames, monster3Frames, princessFrames;
	
	//The buttons for users
	JButton menu, start, restart, help, quit;
	
	//The maze map
	int[][] map, map1, map2, map3, map4, map5, map6;
	
    //Some variables
	int playerX, playerY, armor, playerCurrentFrame, playerFrameCounter, playerFrameFixer, 
	othersCurrentFrame, level1MonstersRemained,level5MonstersRemained, redKeys, yellowKeys, blueKeys;
	
	boolean playerMoving, swordEquipped, redKeyRevealed, masterSwordEquipped, hylianShieldEquipped,
	masterShieldRevealed, bossKilled;
	String playerDirection;
	AudioClip backgroundMusic;
	int level, score, gameOverCountDown;
	int gameStage = 0;
	long startTime, endTime;
	
	public GamePanel(){
		this.addKeyListener(this);
		init();//initial the variables in game
	    MyThread myThread = new MyThread();//call myThread to refresh the screen
	    myThread.start();
	    
	    menu = new JButton("Menu");//Create some buttons for player
		start = new JButton("Start");
		restart = new JButton("Restart");
	    help = new JButton("Help");
	    quit = new JButton("Quit");
	    
	    setLayout(new FlowLayout(FlowLayout.LEFT));
	    
	    this.add(menu);
	    this.add(start);
	    this.add(restart);
	    this.add(help);
	    this.add(quit);
	    
	    menu.addActionListener(this);
	    start.addActionListener(this);
	    restart.addActionListener(this);
	    help.addActionListener(this);
	    quit.addActionListener(this);
	    
	    menu.setFocusable(false);//make the screen that can focus on the keyboard
	    start.setFocusable(false);
	    restart.setFocusable(false);
	    help.setFocusable(false);
	    quit.setFocusable(false);
	    
	    backgroundMusic = loadAudio("BGM.mid");//read the music file and loop it during playing the game
		startAudioLoop(backgroundMusic);
	}
	
	public void init() {
		sheet = loadImage("picture.png");//Picture of all image source
		sheet2 = loadImage("picture1.png");
		wall = subImage(sheet, 0, 512, 32, 32);
		playerFrames = new Image[12];
		monsterFrames = new Image[2];
		bossFrames = new Image[2];
		starFrames = new Image[2];
		magmaFrames = new Image[2];
		monster2Frames = new Image[2];
		monster3Frames = new Image[2];
		princessFrames = new Image[2];
		
		//extract the image we need in this game
		playerFrames[0] = subImage(sheet, 192, 192, 32, 32);
		playerFrames[1] = subImage(sheet, 192+32, 192, 32, 32);
		playerFrames[2] = subImage(sheet, 192+64, 192, 32, 32);
		playerFrames[3] = subImage(sheet, 192+96, 192, 32, 32);
		playerFrames[4] = subImage(sheet, 192, 192+32, 32, 32);
		playerFrames[5] = subImage(sheet, 192+32, 192+32, 32, 32);
		playerFrames[6] = subImage(sheet, 192+64, 192+32, 32, 32);
		playerFrames[7] = subImage(sheet, 192+96, 192+32, 32, 32);
		playerFrames[8] = subImage(sheet, 192, 192+64, 32, 32);
		playerFrames[9] = subImage(sheet, 192+32, 192+64, 32, 32);
		playerFrames[10] = subImage(sheet, 192+64, 192+64, 32, 32);
		playerFrames[11] = subImage(sheet, 192+96, 192+64, 32, 32);
		monsterFrames[0] = subImage(sheet, 64, 32, 32, 32);
		monsterFrames[1] = subImage(sheet, 96, 32, 32, 32);
		monster2Frames[0] = subImage(sheet, 64, 128, 32, 32);
		monster2Frames[1] = subImage(sheet, 96, 128, 32, 32);
		monster3Frames[0] = subImage(sheet, 256, 160, 32, 32);
		monster3Frames[1] = subImage(sheet, 288, 160, 32, 32);
		bossFrames[0] = subImage(sheet, 0, 192, 96, 96);
		bossFrames[1] = subImage(sheet, 96, 192, 96, 96);
		starFrames[0] = subImage(sheet, 192, 512, 32, 32);
		starFrames[1] = subImage(sheet, 224, 512, 32, 32);
		magmaFrames[0] = subImage(sheet, 128, 512, 32, 32);
		magmaFrames[1] = subImage(sheet, 160, 512, 32, 32);
		princessFrames[0] = subImage(sheet, 192, 288, 32, 32);
		princessFrames[1] = subImage(sheet, 224, 288, 32, 32);
		floor = subImage(sheet, 288, 512, 32, 32);
		sword = subImage(sheet, 192, 384, 32, 32);
		shield = subImage(sheet, 192, 416, 32, 32);
		explode = subImage(sheet, 288, 448, 32, 32);
		upStairs = subImage(sheet, 96, 512, 32, 32);
		downStairs = subImage(sheet, 64, 512, 32, 32);
		swordIcon = subImage(sheet2, 192, 384, 32, 32);
		shieldIcon = subImage(sheet2, 192, 416, 32, 32);
		redKey = subImage(sheet, 160, 448, 32, 32);
		redKeyIcon = subImage(sheet2, 160, 448, 32, 32);
		redDoor = subImage(sheet, 256, 480, 32, 32);
		yellowDoor = subImage(sheet, 192, 480, 32, 32);
	    blueDoor = subImage(sheet, 224, 480, 32, 32);
	    yellowKey = subImage(sheet, 96, 448, 32, 32);
		yellowKeyIcon = subImage(sheet2, 96, 448, 32, 32);
		blueKey = subImage(sheet, 128, 448, 32, 32);
		blueKeyIcon = subImage(sheet2, 128, 448, 32, 32);
		masterSword = subImage(sheet, 224, 448, 32, 32);
		hylianShield = subImage(sheet, 256, 448, 32, 32);
		masterSwordIcon = subImage(sheet2, 224, 448, 32, 32);
		hylianShieldIcon = subImage(sheet2, 256, 448, 32, 32);
		//player position
		playerX = 1;
		playerY = 8;
		//number of items
		armor = 0;
		redKeys = 0;
		yellowKeys = 0;
		blueKeys = 0;
		//record the time
		startTime = 0;
		endTime = 0;
		//some variable used for controlling the game
		playerCurrentFrame = 9;
		playerFrameCounter = 0;
		playerFrameFixer = 9;
		playerDirection = "RIGHT";
		playerMoving = false;
		swordEquipped = false;
		masterSwordEquipped = false;
		hylianShieldEquipped = false;
		bossKilled = false;
		othersCurrentFrame = 0;
		gameOverCountDown = 50;
		level = 1;
		level1MonstersRemained = 7;
		level5MonstersRemained = 8;
		redKeyRevealed = false;
		masterShieldRevealed = false;
		//6 different maps
		map1 = new int[][] 
			    {{1,1,1,1,1,1,1,1,1,1},
	            {1,1,20,1,1,6,1,1,1,1},
	            {1,20,0,20,1,4,1,1,1,1},
	            {1,1,20,1,1,4,1,1,1,1},
	            {1,3,3,4,0,0,1,1,1,1},
	            {1,1,1,1,1,14,1,1,1,1},
	            {1,3,0,0,0,0,0,0,2,1},
	            {1,1,1,1,1,0,1,1,1,1},
	            {1,0,0,0,0,16,0,0,8,1},
	            {1,1,1,1,1,1,1,1,1,1}};
	     
	     map2 =  new int[][]
	    		 {{1,1,1,1,1,1,1,1,1,1},
                  {1,13,13,10,11,11,13,13,13,1},
                  {1,13,13,11,11,11,13,13,13,1},
                  {1,13,13,11,11,11,13,13,13,1},
                  {1,13,13,13,9,13,13,13,13,1},
                  {1,13,13,13,15,13,13,13,13,1},
                  {1,13,13,13,14,13,13,13,13,1},
                  {1,1,1,1,4,1,1,1,1,1},
                  {1,7,0,0,0,0,0,0,6,1},
                  {1,1,1,1,1,1,1,1,1,1}};
         
         map3 = new int[][]
        		 {{1,1,1,1,1,1,1,1,1,1},
        	      {1,1,1,1,1,6,1,1,1,1},
        	      {1,4,0,20,1,3,1,1,3,1},
        	      {1,0,1,16,1,0,1,1,3,1},
        	      {1,4,1,1,1,0,1,1,3,1},
        	      {1,0,0,4,4,0,4,0,4,1},
        	      {1,3,3,1,1,0,1,1,3,1},
        	      {1,1,1,1,1,0,1,1,1,1},
        	      {1,7,0,0,0,3,0,0,1,1},
        	      {1,1,1,1,1,1,1,1,1,1},
        		 };
        		 
         map4 = new int[][]
        		 {{1,1,1,1,1,1,1,1,1,1},
   	             {1,17,3,1,1,1,1,20,17,1},
   	             {1,3,3,1,0,4,4,4,20,1},
   	             {1,1,4,1,0,1,1,1,1,1},
   	             {1,0,0,0,0,0,0,0,0,1},
   	             {1,1,1,1,1,15,1,1,1,1},
   	             {1,3,0,0,0,0,0,0,0,1},
   	             {1,0,0,1,1,1,1,4,1,1},
   	             {1,6,0,1,17,16,14,0,7,1},
   	             {1,1,1,1,1,1,1,1,1,1},
   	         	 };
                		 
         map5 = new int[][]
        		 {{1,1,1,1,1,1,1,1,1,1},
   	              {1,1,1,1,1,1,1,0,19,1},
   	              {1,3,1,20,20,20,21,0,0,1},
   	              {1,3,1,4,1,1,1,1,1,1},
   	              {1,3,1,4,1,1,1,3,3,1},
   	              {1,3,1,15,1,1,21,3,3,1},
   	              {1,3,1,0,1,1,0,1,1,1},
   	              {1,21,0,0,0,0,0,0,0,1},
   	              {1,7,0,1,1,1,1,1,6,1},
   	              {1,1,1,1,1,1,1,1,1,1},
   		          };
   	     map6 = new int[][]  
   		       {{1,1,1,1,1,1,1,1,1,1},
         	      {1,13,13,13,13,13,13,13,13,1},
         	      {1,13,13,13,18,13,13,13,13,1},
         	      {1,13,13,13,21,13,13,13,13,1},
         	      {1,13,13,13,20,13,13,13,13,1},
         	      {1,13,13,13,20,13,13,13,13,1},
         	      {1,13,13,13,4,13,13,13,13,1},
         	      {1,1,1,1,4,1,1,1,1,1},
         	      {1,7,0,0,0,0,0,0,0,1},
         	      {1,1,1,1,1,1,1,1,1,1},
         		 };
                        		 
            
	}
	//record the maps
	public int[][] currentMap() {
		if (level == 2) return map2;
		if (level == 3) return map3;
		if (level == 4) return map4;
		if (level == 5) return map5;
		if (level == 6) return map6;
		return map1;
	}
	
	public void update() {
		map = currentMap();//read the map
		if (playerMoving) {
			if (playerDirection == "UP") {
				if ((map[playerY-1][playerX] != 1) //the player cannot enter some solid place such as wall
						&& (map[playerY-1][playerX] != 4) 
						&& (map[playerY-1][playerX] != 9) 
						&& (map[playerY-1][playerX] != 10) 
						&& (map[playerY-1][playerX] != 11) 
						&& (map[playerY-1][playerX] != 13)
						&& (map[playerY-1][playerX] != 14)
						&& (map[playerY-1][playerX] != 15)
						&& (map[playerY-1][playerX] != 20)
						&& (map[playerY-1][playerX] != 21)) playerY -= 1;
			}
			if (playerDirection == "DOWN") {
				if ((map[playerY+1][playerX] != 1) 
						&& (map[playerY+1][playerX] != 4) 
						&& (map[playerY+1][playerX] != 9) 
						&& (map[playerY+1][playerX] != 10) 
						&& (map[playerY+1][playerX] != 11) 
						&& (map[playerY+1][playerX] != 13)
						&& (map[playerY+1][playerX] != 14)
						&& (map[playerY+1][playerX] != 15)
						&& (map[playerY+1][playerX] != 20)
						&& (map[playerY+1][playerX] != 21)) playerY += 1;
			}
			if (playerDirection == "LEFT") {
				if ((map[playerY][playerX-1] != 1) 
						&& (map[playerY][playerX-1] != 4) 
						&& (map[playerY][playerX-1] != 9) 
						&& (map[playerY][playerX-1] != 10) 
						&& (map[playerY][playerX-1] != 11) 
						&& (map[playerY][playerX-1] != 13)
						&& (map[playerY][playerX-1] != 14)
						&& (map[playerY][playerX-1] != 15)
						&& (map[playerY][playerX-1] != 20)
						&& (map[playerY][playerX-1] != 21)) playerX -= 1;
			}
			if (playerDirection == "RIGHT") {
				if ((map[playerY][playerX+1] != 1) 
						&& (map[playerY][playerX+1] != 4) 
						&& (map[playerY][playerX+1] != 9) 
						&& (map[playerY][playerX+1] != 10) 
						&& (map[playerY][playerX+1] != 11) 
						&& (map[playerY][playerX+1] != 13)
						&& (map[playerY][playerX+1] != 14)
						&& (map[playerY][playerX+1] != 15)
						&& (map[playerY][playerX+1] != 20)
						&& (map[playerY][playerX+1] != 21)) playerX += 1;
			}
		}
		if (map[playerY][playerX] == 2) {
			map[playerY][playerX] =0;
			swordEquipped = true;//player gets the sword
		}
		if (map[playerY][playerX] == 18) {
			map[playerY][playerX] =0;
			masterSwordEquipped = true;//player gets the master sword
		}
		if ((map[playerY][playerX] == 19) && masterShieldRevealed) {
			map[playerY][playerX] =0;
			hylianShieldEquipped = true;//player gets the hylian shield
		}
		if (map[playerY][playerX] == 3) {
			map[playerY][playerX] =0;
			armor ++;//player gains the shield
		}
		if ((map[playerY][playerX] == 8) && redKeyRevealed) {
			map[playerY][playerX] =0;
			redKeys ++;//player gets the red key
		}
		if (map[playerY][playerX] == 16) {
			map[playerY][playerX] =0;
			yellowKeys ++;//player gets the yellow key
		}
		if (map[playerY][playerX] == 17) {
			map[playerY][playerX] =0;
			blueKeys ++;//player gets the blue keys
		}
			playerCurrentFrame ++;
			playerFrameCounter ++;
			//display the animation of the player or other element in the game, such as the monster
			if (playerFrameCounter == 3) {playerFrameCounter = 0;playerCurrentFrame = playerFrameFixer;}
			if (othersCurrentFrame == 0) othersCurrentFrame = 1; else othersCurrentFrame = 0;
			//set the position of the player when he gets upstairs
			if(map[playerY][playerX] == 6) {
				if (level == 1) {
					map1 = map;
					playerX = 2;
					playerY = 8;
				}
				if (level == 2) {
					map2 = map;
					playerX = 2;
					playerY = 8;
				}
				if (level == 3) {
					map3 = map;
					playerX = 7;
					playerY = 8;
				}
				if (level == 4) {
					map4 = map;
					playerX = 2;
					playerY = 8;
				}
				if (level == 5) {
					map5 = map;
					playerX = 2;
					playerY = 8;
				}
				playerDirection = "RIGHT";
				level++;
				map = currentMap();
			}
			//set the position of the player when he gets downstairs
			if(map[playerY][playerX] == 7) {
				if (level == 2) {
					map2 = map;
					playerX = 5;
					playerY = 2;
				}
				if (level == 3) {
					map3 = map;
					playerX = 7;
					playerY = 8;
				}
				if (level == 4) {
					map4 = map;
					playerX = 5;
					playerY = 2;
				}
				if (level == 5) {
					map5 = map;
					playerX = 2;
					playerY = 8;
				}
				if (level == 6) {
					map6 = map;
					playerX = 8;
					playerY = 7;
				}
				playerDirection = "DOWN";
				level--;
				map = currentMap();
			}
			//the red key and hylian shield only appears when the monsters are defeated at same level
			if (level1MonstersRemained == 0) redKeyRevealed = true;
			if (level5MonstersRemained == 0) masterShieldRevealed = true;
	}
	
	class MyThread extends Thread{//inner class to run and refresh the game paint
		@Override
		public void run() {
			while(true){
				try {
					update();//keep updating the variable of games
					sleep(150);
					repaint();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void paintComponent(Graphics g){//draw game main area
        super.paintComponent(g); 
        g.setColor(Color.black);
        g.fillRect(gameX, gameY, gameWidth, gameHeight);  
        g.setColor(Color.white);
        g.drawRect(gameX, gameY, gameWidth, gameHeight); 
        if(gameStage == 0) {//main page
        	g.setColor(Color.white);
        	g.setFont(new Font("Arial", Font.BOLD, 12));
        	g.drawString("Click button 'Start' to start the game", 50, 200);
        	g.drawString("Click button 'Help' to get game guide", 50, 230);
        	init();
        }
        if(gameStage == 1) {//game start
        	for (int i=0;i<=9;i++) {
            	for (int j=0;j<=9;j++) {//provide the unique digit for each element of image
            		if (map[j][i] == 0) g.drawImage(floor, i*32, j*32+50, this);	
            		if (map[j][i] == 1) g.drawImage(wall, i*32, j*32+50, this);	
            		if (map[j][i] == 2) g.drawImage(sword, i*32, j*32+50, this);	
            		if (map[j][i] == 3) g.drawImage(shield, i*32, j*32+50, this);
            		if (map[j][i] == 4) g.drawImage(monsterFrames[othersCurrentFrame], i*32, j*32+50, this);
            		if (map[j][i] == 5) {g.drawImage(explode, i*32, j*32+50, this);map[j][i] = 0;}
            		if (map[j][i] == 6) g.drawImage(upStairs, i*32, j*32+50, this);
            		if (map[j][i] == 7) g.drawImage(downStairs, i*32, j*32+50, this);
            		if ((map[j][i] == 8) && redKeyRevealed) g.drawImage(redKey, i*32, j*32+50, this);
            		if ((map[j][i] == 8) && !redKeyRevealed) g.drawImage(floor, i*32, j*32+50, this);
            		if (map[j][i] == 9) g.drawImage(redDoor, i*32, j*32+50, this);
            		if (map[j][i] == 10) g.drawImage(bossFrames[othersCurrentFrame], i*32, j*32+50, this);
            		if (map[j][i] == 12) g.drawImage(starFrames[othersCurrentFrame], i*32, j*32+50, this);
            		if (map[j][i] == 13) g.drawImage(magmaFrames[othersCurrentFrame], i*32, j*32+50, this);
            		if (map[j][i] == 14) g.drawImage(yellowDoor, i*32, j*32+50, this);
            		if (map[j][i] == 15) g.drawImage(blueDoor, i*32, j*32+50, this);
            		if (map[j][i] == 16) g.drawImage(yellowKey, i*32, j*32+50, this);
            		if (map[j][i] == 17) g.drawImage(blueKey, i*32, j*32+50, this);
            		if (map[j][i] == 18) g.drawImage(masterSword, i*32, j*32+50, this);	
              		if ((map[j][i] == 19) && masterShieldRevealed) g.drawImage(hylianShield, i*32, j*32+50, this);
              		if ((map[j][i] == 19) && !masterShieldRevealed) g.drawImage(floor, i*32, j*32+50, this);
              		if (map[j][i] == 20) g.drawImage(monster2Frames[othersCurrentFrame], i*32, j*32+50, this);
              		if (map[j][i] == 21) g.drawImage(monster3Frames[othersCurrentFrame], i*32, j*32+50, this);
              		if (map[j][i] == 22) g.drawImage(princessFrames[othersCurrentFrame], i*32, j*32+50, this);
            	}
            }
        	//player animation in four direction
            if (playerDirection == "UP") {
            	g.drawImage(playerFrames[playerCurrentFrame], playerX*32, playerY*32+50, this);
    		}
    		if (playerDirection == "DOWN") {
    			g.drawImage(playerFrames[playerCurrentFrame], playerX*32, playerY*32+50, this);
    		}
    		if (playerDirection == "LEFT") {
    			g.drawImage(playerFrames[playerCurrentFrame], playerX*32, playerY*32+50, this);
    		}
    		if (playerDirection == "RIGHT") {
    			g.drawImage(playerFrames[playerCurrentFrame], playerX*32, playerY*32+50, this);
    		}
    		//equipments area, let user knows what items are gotten
    		if (swordEquipped) g.drawImage(swordIcon, 0, 370, this);
            g.drawImage(shieldIcon, 32, 370, this);
            g.drawString(Integer.toString(armor), 66, 400);
            if(redKeys==1) g.drawImage(redKeyIcon, 80, 370, this);
            if(yellowKeys==1) g.drawImage(yellowKeyIcon, 112, 370, this);
            if(blueKeys==1) g.drawImage(blueKeyIcon, 144, 370, this);
            if (masterSwordEquipped) g.drawImage(masterSwordIcon, 0, 402, this);
            if (hylianShieldEquipped) g.drawImage(hylianShieldIcon, 32, 402, this);
            if (bossKilled) gameOverCountDown--;
            if (gameOverCountDown == 0) gameStage = 3;
        }
        
        if(gameStage == 3) {//game over
        	g.setColor(Color.white);
        	g.setFont(new Font("Arial", Font.BOLD, 12));
        	int playTime = (int)endTime - (int)startTime;
        	g.drawString("You win! You successfully rescue ", 50, 200);
        	g.drawString("the princess from the maze", 50, 220);
        	g.drawString("You have " + Integer.toString(armor) +" shields left ", 50, 240);
        	g.drawString("and you spend "+ Integer.toString((playTime/1000)/60) +" minutes to win", 50, 260);
        	//score bases on how many shields remain and how quick the user wins the game
        	if((playTime/100)>=10000) {
        		g.drawString("Your score is " + Integer.toString(armor*100), 50, 280);
        	}else {
        		g.drawString("Your score is " + Integer.toString(armor*100 + 10000 - (playTime/100)), 50, 280);
        	}
        	g.drawString("Click button 'Menu' to back main page", 50, 300);
        }
        
        if(gameStage == 4) {//simple game rules
        	g.setColor(Color.white);
        	g.setFont(new Font("Arial", Font.BOLD, 12));
        	g.drawString("Use arrow keys up, down, left and right to", 10, 150);
        	g.drawString(" control the knight and press 'X' to fight",10,170);
        	g.drawString("Click button 'Restart' to restart the game", 10, 190);
        	g.drawString("Click button 'Quit' to quit the game", 10, 210);
        	g.drawString("For more detail, please check Player Guide", 10, 230);
        	g.drawString("Click button 'Menu' to back main page", 10, 250);
        }
        
	}
	
	//get time from computer
	public long getTime() {
        return System.currentTimeMillis();
    }
	
	//this loadImage function is copied from GameEngine.java
	// Loads an image from file
		public Image loadImage(String filename) {
			try {
				// Load Image
				Image image = ImageIO.read(new File(filename));

				// Return Image
				return image;
			} catch (IOException e) {
				// Show Error Message
				System.out.println("Error: could not load image " + filename);
				System.exit(1);
			}

			// Return null
			return null;
		}
		
		//this subImage function is copied from GameEngine.java
		// Loads a sub-image out of an image
		public Image subImage(Image source, int x, int y, int w, int h) {
			// Check if image is null
			if(source == null) {
				// Print Error message
				System.out.println("Error: cannot extract a subImage from a null image.\n");

				// Return null
				return null;
			}

			// Convert to a buffered image
			BufferedImage buffered = (BufferedImage)source;

			// Extract sub image
			Image image = buffered.getSubimage(x, y, w, h);

			// Return image
			return image;
		}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//user uses four arrow keys to control the player
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			playerMoving = true; playerDirection = "UP";playerFrameFixer = 3;playerCurrentFrame = 3;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			playerMoving = true; playerDirection = "DOWN";playerFrameFixer = 0;playerCurrentFrame = 0;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			playerMoving = true; playerDirection = "LEFT";playerFrameFixer = 6;playerCurrentFrame = 6;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			playerMoving = true; playerDirection = "RIGHT";playerFrameFixer = 9;playerCurrentFrame = 9;
		}
		//user uses X key to defeat the monsters or open the doors
		if (e.getKeyCode() == KeyEvent.VK_X) {
			if (swordEquipped) {
				if (playerDirection == "UP") {
					if (((map[playerY-1][playerX] == 4) || (map[playerY-1][playerX] == 20)|| (map[playerY-1][playerX] == 21))&& (armor > 0)) {
						map[playerY-1][playerX] = 5;//monster is defeated
						if(!hylianShieldEquipped) {
							armor--;//player spends one shield to defeat one monster if he has no hylian shield
						}
						if (level == 1) level1MonstersRemained--;
						if (level == 5) level5MonstersRemained--;
					}
					if (((map[playerY-1][playerX] == 4) || (map[playerY-1][playerX] == 20)|| (map[playerY-1][playerX] == 21))&& (armor == 0) && hylianShieldEquipped) {
						map[playerY-1][playerX] = 5;
						if (level == 1) level1MonstersRemained--;
						if (level == 5) level5MonstersRemained--;
					}
					if ((map[playerY-1][playerX] == 9) && (redKeys > 0)) {
						map[playerY-1][playerX] = 0;
						redKeys--;//open the red door
					}
					if ((map[playerY-1][playerX] == 14) && (yellowKeys > 0)) {
						map[playerY-1][playerX] = 0;
						yellowKeys--;//open the yellow door
					}
					if ((map[playerY-1][playerX] == 15) && (blueKeys > 0)) {
						map[playerY-1][playerX] = 0;
						blueKeys--;//open the blue door
					}
					if(masterSwordEquipped) {//defeat the boss
						if ((map[playerY-1][playerX] == 10) || (map[playerY-1][playerX] == 11)){
							for (int i=1;i<4;i++) {
								for (int j=3;j<6;j++) {
									map[i][j] = 12;
								}
							}
							bossKilled = true;
							map[2][4] = 22;//player finds the princess
							endTime = getTime();//game is finished, records the end time
						}
					}
				}
				//the other three directions are almost the same with UP direction
				if (playerDirection == "DOWN") {
					if (((map[playerY+1][playerX] == 4) || (map[playerY+1][playerX] == 20)|| (map[playerY+1][playerX] == 21))&& (armor > 0)) {
						map[playerY+1][playerX] = 5;
						if(!hylianShieldEquipped) {
							armor--;
						}
						
						if (level == 1) level1MonstersRemained--;
						if (level == 5) level5MonstersRemained--;
					}
					if (((map[playerY+1][playerX] == 4) || (map[playerY+1][playerX] == 20)|| (map[playerY+1][playerX] == 21))&& (armor == 0) && hylianShieldEquipped) {
						map[playerY+1][playerX] = 5;
						if (level == 1) level1MonstersRemained--;
						if (level == 5) level5MonstersRemained--;
					}
				
					if ((map[playerY+1][playerX] == 9) && (redKeys > 0)) {
						map[playerY+1][playerX] = 0;
						redKeys--;
					}
					if ((map[playerY+1][playerX] == 14) && (yellowKeys > 0)) {
						map[playerY+1][playerX] = 0;
						yellowKeys--;
					}
					if ((map[playerY+1][playerX] == 15) && (blueKeys > 0)) {
						map[playerY+1][playerX] = 0;
						blueKeys--;
					}
				}
				if (playerDirection == "LEFT") {
					if (((map[playerY][playerX-1] == 4) || (map[playerY][playerX-1] == 20)|| (map[playerY][playerX-1] == 21))&& (armor > 0)) {
						map[playerY][playerX-1] = 5;
						if(!hylianShieldEquipped) {
							armor--;
						}
						if (level == 1) level1MonstersRemained--;
						if (level == 5) level5MonstersRemained--;
					}
					if (((map[playerY][playerX-1] == 4) || (map[playerY][playerX-1] == 20)|| (map[playerY][playerX-1] == 21))&& (armor == 0) && hylianShieldEquipped) {
						map[playerY][playerX-1] = 5;
						if (level == 1) level1MonstersRemained--;
						if (level == 5) level5MonstersRemained--;
					}
					if ((map[playerY][playerX-1] == 9) && (redKeys > 0)) {
						map[playerY][playerX-1] = 0;
						redKeys--;
					}
					if ((map[playerY][playerX-1] == 14) && (yellowKeys > 0)) {
						map[playerY][playerX-1] = 0;
						yellowKeys--;
					}
					if ((map[playerY][playerX-1] == 15) && (blueKeys > 0)) {
						map[playerY][playerX-1] = 0;
						blueKeys--;
					}
					if (map[playerY][playerX-1] == 10) {
						for (int i=1;i<4;i++) {
							for (int j=3;j<6;j++) {
								map[i][j] = 12;
							}
						}
					}
					if (map[playerY][playerX-1] == 11) {
						for (int i=1;i<4;i++) {
							for (int j=3;j<6;j++) {
								map[i][j] = 12;
							}
						}}
				}
				if (playerDirection == "RIGHT") {
					if (((map[playerY][playerX+1] == 4) || (map[playerY][playerX+1] == 20)|| (map[playerY][playerX+1] == 21))&& (armor > 0)) {
						map[playerY][playerX+1] = 5;
						if(!hylianShieldEquipped) {
							armor--;
						}
						if (level == 1) level1MonstersRemained--;
						if (level == 5) level5MonstersRemained--;
					}
					if (((map[playerY][playerX+1] == 4) || (map[playerY][playerX+1] == 20)|| (map[playerY][playerX+1] == 21))&& (armor == 0) && hylianShieldEquipped) {
						map[playerY][playerX+1] = 5;
						if (level == 1) level1MonstersRemained--;
						if (level == 5) level5MonstersRemained--;
					}
					if ((map[playerY][playerX+1] == 9) && (redKeys > 0)) {
						map[playerY][playerX+1] = 0;
						redKeys--;
					}
					if ((map[playerY][playerX+1] == 14) && (yellowKeys > 0)) {
						map[playerY][playerX+1] = 0;
						yellowKeys--;
					}
					if ((map[playerY][playerX+1] == 15) && (blueKeys > 0)) {
						map[playerY][playerX+1] = 0;
						blueKeys--;
					}
					if (map[playerY][playerX+1] == 10) {
						for (int i=1;i<4;i++) {
							for (int j=3;j<6;j++) {
								map[i][j] = 12;
							}
						}}
					if (map[playerY][playerX+1] == 11) {
						for (int i=1;i<4;i++) {
							for (int j=3;j<6;j++) {
								map[i][j] = 12;
							}
						}}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		//when the keys are released, the player will stop
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			playerMoving = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			playerMoving = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			playerMoving = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			playerMoving = false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//different utilities for buttons
		if(e.getSource() == menu) {
        	gameStage = 0;
            repaint();
        }
		if(e.getSource() == start) {
			gameStage = 1;
			startTime = getTime();
            repaint();
        }
		if(e.getSource() == restart) {
			gameStage = 1;
			init();
			init();
			repaint();
        }
		if(e.getSource() == help) {
            gameStage = 4;
            repaint();
        }
		if(e.getSource() == quit) {
            System.exit(0);
        }	
		
	}
	
	//this AudioClip class is copied from GameEngine.java
	public class AudioClip {
		// Format
		AudioFormat mFormat;

		// Audio Data
		byte[] mData;

		// Buffer Length
		long mLength;

		// Loop Clip
		Clip mLoopClip;

		public Clip getLoopClip() {
			// return mLoopClip
			return mLoopClip;
		}

		public void setLoopClip(Clip clip) {
			// Set mLoopClip to clip
			mLoopClip = clip;
		}

		public AudioFormat getAudioFormat() {
			// Return mFormat
			return mFormat;
		}

		public byte[] getData() {
			// Return mData
			return mData;
		}

		public long getBufferSize() {
			// Return mLength
			return mLength;
		}

		public AudioClip(AudioInputStream stream) {
			// Get Format
			mFormat = stream.getFormat();

			// Get length (in Frames)
			mLength = stream.getFrameLength() * mFormat.getFrameSize();

			// Allocate Buffer Data
			mData = new byte[(int)mLength];

			try {
				// Read data
				stream.read(mData);
			} catch(Exception exception) {
				// Print Error
				System.out.println("Error reading Audio File\n");

				// Exit
				System.exit(1);
			}

			// Set LoopClip to null
			mLoopClip = null;
		}
	}

	// Loads the AudioClip stored in the file specified by filename
	public AudioClip loadAudio(String filename) {
		try {
			// Open File
			File file = new File(filename);

			// Open Audio Input Stream
			AudioInputStream audio = AudioSystem.getAudioInputStream(file);

			// Create Audio Clip
			AudioClip clip = new AudioClip(audio);

			// Return Audio Clip
			return clip;
		} catch(Exception e) {
			// Catch Exception
			System.out.println("Error: cannot open Audio File " + filename + "\n");
		}

		// Return Null
		return null;
	}
	
	public void startAudioLoop(AudioClip audioClip) {
		// Check audioClip for null
		if(audioClip == null) {
			// Print error message
			System.out.println("Error: audioClip is null\n");

			// Return
			return;
		}

		// Get Loop Clip
		Clip clip = audioClip.getLoopClip();

		// Create Loop Clip if necessary
		if(clip == null) {
			try {
				// Create a Clip
				clip = AudioSystem.getClip();

				// Load data
				clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

				// Set Clip to Loop
				clip.loop(Clip.LOOP_CONTINUOUSLY);

				// Set Loop Clip
				audioClip.setLoopClip(clip);
			} catch(Exception exception) {
				// Display Error Message
				System.out.println("Error: could not play Audio Clip\n");
			}
		}

		// Set Frame Position to 0
		clip.setFramePosition(0);

		// Start Audio Clip playing
		clip.start();
	}
}