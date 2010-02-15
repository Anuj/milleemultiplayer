package com.example.helloworld;

import com.example.helloworld.HelloWorldMidlet;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class SplashScreen extends Screen {
	
	public SplashScreen(String title) {
		super(title);
		
		Image arrow = null;
        
        try {
			arrow = Image.createImage("/splashScreen.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.addCommand(HelloWorldMidlet.startCommand);
		this.addCommand(HelloWorldMidlet.exitCommand);
             
        this.append(arrow);
	}
	
    /**
     * This method resizes an image by resampling its pixels
     * @param src The image to be resized
     * @param screenWidth The width to resize to fit
     * @param screenHeight The height to resize to fit
     * @return The resized image
     */

     public static Image resizeImage(Image src, int screenWidth, int screenHeight) {
         int srcWidth = src.getWidth();
         int srcHeight = src.getHeight();
         Image tmp = Image.createImage(screenWidth, srcHeight);
         Graphics g = tmp.getGraphics();
         int ratio = (srcWidth << 16) / screenWidth;
         int pos = ratio/2;

         //Horizontal Resize        

         for (int x = 0; x < screenWidth; x++) {
             g.setClip(x, 0, 1, srcHeight);
             g.drawImage(src, x - (pos >> 16), 0, Graphics.LEFT | Graphics.TOP);
             pos += ratio;
         }

         Image resizedImage = Image.createImage(screenWidth, screenHeight);
         g = resizedImage.getGraphics();
         ratio = (srcHeight << 16) / screenHeight;
         pos = ratio/2;        

         //Vertical resize

         for (int y = 0; y < screenHeight; y++) {
             g.setClip(0, y, screenWidth, 1);
             g.drawImage(tmp, 0, y - (pos >> 16), Graphics.LEFT | Graphics.TOP);
             pos += ratio;
         }
         return resizedImage;
     }
}
