/* autogenerated by Processing revision 1293 on 2024-10-27 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import ddf.minim.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class aMain extends PApplet {

//Maxime FOUCHER 



Minim minim;
AudioInput input;

PImage img, imgstart;
Orca orca;
Character character;
ArrayList<Orca> orcas = new ArrayList<>();
int lastOrcaTime = 0;
int orcaInterval = 10000;
int nborca = 1;
int score = 0;
int scoretemp = 0;
boolean screenstart = true;
boolean screenloose = false;
int gameStartTime = 0;

public void setup() {
  /* size commented out by preprocessor */; // size of screen

  // Initialisation of Minim for audio
  minim = new Minim(this);
  input = minim.getLineIn(Minim.MONO, 512); // input audio mono

  img = loadImage("background.png");
  orcas.add(new Orca());
  character = new Character();
  imgstart = loadImage("getready.png");
}

public void draw() {
  fill(255, 255, 0);
  textSize(40);
  if (screenstart) { // if we don't pressed enter
    image(imgstart, 0, 0, width, height); // start screen
    if (screenloose) {
      text("GAME OVER !!", (width - textWidth("GAME OVER !!")) / 2, height / 2);
      text("Score : " + score, (width - textWidth("Score : " + score)) / 2, height * 3 / 5);
    } else {
      text("GET READY !!", (width - textWidth("GET READY !!")) / 2, height / 2);
    }
    text("Press ENTER to Start a New Game", width - textWidth("Press ENTER to Start a New Game") - 20, height - 35); // En bas à droite
  } else { // start game
    image(img, 0, 0, width, height);
    character.DrawCharacter();

    int[] Characterbounds = character.getBounds();

    // input volume
    float volume = input.mix.level();
    float threshold = 0.005f; // to adjust to increase or decrease the volume detected

    println("Volume actuel : " + volume);

    if (volume > threshold) { // to moove charcater thanks to volume
      character.move(-10);
    } else {
      character.move(5);
    }

    int timer = (int)((millis() - gameStartTime) / 1000); // start timer of seconds

    if (millis() - lastOrcaTime > orcaInterval) {
      orcas.add(new Orca());
      lastOrcaTime = millis();
      orcaInterval = (int) random(2000, 8000);
      nborca += 1; // counter of pasts orcas
      scoretemp = scoretemp + (int)lastOrcaTime / 1000 * nborca;
    }

    score = timer + scoretemp;

    text("Score : " + score, 10, 40); // the score is time + time past * orcas past

    for (int i = orcas.size() - 1; i >= 0; i--) {
      Orca orca = orcas.get(i);
      orca.DrawOrca(); //draw orcas

      int[] Orcabounds = orca.getBounds();

      if (Characterbounds[0] + Characterbounds[2] / 2 > Orcabounds[0] - Orcabounds[2] / 2 &&
        Characterbounds[0] - Characterbounds[2] / 2 < Orcabounds[0] + Orcabounds[2] / 2 &&
        Characterbounds[1] + Characterbounds[3] / 2 > Orcabounds[1] - Orcabounds[3] / 2 &&
        Characterbounds[1] - Characterbounds[3] / 2 < Orcabounds[1] + Orcabounds[3] / 2) {
        println("Partie perdue !");
        screenloose = true; // says that you the game is over
        screenstart = true; // screen start again
      }

      if (orca.isOffScreen()) {
        orcas.remove(i); // if orcas are outside the screen, delete it
      }
    }
  }
}

public void keyPressed() {
  if (key == ENTER) {
    if (screenloose) {
      // if game is over you can start a new game
      screenloose = false;
      score = 0; 
      orcas.clear(); // clear all orcas
    }
    screenstart = false; // start game
    gameStartTime = millis(); // start timer now
    lastOrcaTime = millis(); // start again last orca generate
  }
}

public void stop() {
  input.close(); // close audio
  minim.stop();  // stop minim
  super.stop(); // stop all
}
//Maxime FOUCHER 
class Character {
  PImage img1, img2;
  int x;
  int y;
  int lastY;
  boolean image1 = true;
  boolean image2 = false;
  int imgWidth = 100;
  int imgHeight = 150;
  int upperLimit;
  int lowerLimit;
  int returnx, returny, returnsize1, returnsize2;

  Character() {
    img1 = loadImage("character1.png");
    img2 = loadImage("character2.png");
    img1.resize(imgWidth, imgHeight);
    img2.resize(imgWidth, imgHeight);


    // Initialisation position
    x = width / 5;
    y = height / 2;
    lastY = y;

    upperLimit = imgHeight / 6;
    lowerLimit = height - imgHeight*3/2;
  }

  public void DrawCharacter() { // different image if the character go down or up (sprite)
    if (y > lastY) {
      image1 = true;
      image2 = false;
    } else if (y < lastY) {
      image1 = false;
      image2 = true;
    }

    if (image1) {
      image(img1, x, y);
    } else {
      image(img2, x, y);
    }

    lastY = y;

    returnx = x+55;
    returny = y+80;
    returnsize1 = 70;
    returnsize2 = 100;

  }

  public int[] getBounds() { // to do the hitbox
    return new int[] { returnx, returny, returnsize1, returnsize2 };
  }


  public void move(int deltaY) { // to moove
    if ((y + deltaY) >= upperLimit && (y + deltaY) <= lowerLimit) {
      y += deltaY;
    }
  }
}
//Maxime FOUCHER
class Orca {
  PImage img1, img2, img3;

  int x;
  int y;
  int frameIndex = 0;
  int delay = 200;
  int lastChangeTime;
  int velocity = (int)random(5, 15);
  int returnx, returny, returnsize1, returnsize2;
  
   // Y Position for upper orcas
  int SPECIFIC_Y_POSITION = height / 4;
  float SPECIFIC_Y_CHANCE = 0.15f; // 15% chance for an upper orca


  Orca() {
    img1 = loadImage("orque1.png");
    img2 = loadImage("orque2.png");
    img3 = loadImage("orque3.png");

    x = width + 200;
    
    
    // to make an upper orca (with the pourcent chance or another
    if (random(1) < SPECIFIC_Y_CHANCE) {
      y = SPECIFIC_Y_POSITION; // Y position
    } else {
      y = height / 2; // Normal y position
    }
    
    
    lastChangeTime = millis();
  }

  public void DrawOrca() {
    // if the time since the last orca is enought
    if (millis() - lastChangeTime > delay) {
      frameIndex++;  // change image
      lastChangeTime = millis();  // change the last time orca
    }

    PImage currentImage; // to altern between all sprite
    if (frameIndex % 3 == 0) {
      currentImage = img1;
    } else if (frameIndex % 3 == 1) {
      currentImage = img2;
    } else {
      currentImage = img3;
    }
    image(currentImage, x, y, 200, 100);

    x -= velocity;

    returnx = x+105;
    returny = y+55;
    returnsize1 = 180;
    returnsize2 = 80;
    
  }

  public int[] getBounds() { // for the hit box
    return new int[] { returnx, returny, returnsize1, returnsize2 };
  }

  public boolean isOffScreen() {
    return x < -150; // return true if orca is outside the screen
  }
}


  public void settings() { size(1920/2, 1080/2); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "aMain" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}