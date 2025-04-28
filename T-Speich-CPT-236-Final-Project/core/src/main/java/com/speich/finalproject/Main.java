package com.speich.finalproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.speich.finalproject.gameobjects.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter 
{
    private SpriteBatch batch;
    private Texture playerBulletImage;
    private Texture enemyBulletImage;
    private Texture playerIdleImage;
    private Texture playerAttackImage;
    private Texture enemyIdleImage;
    private Texture enemyAttackImage;
    private Texture[] winImages;
    private Bullet[] bulletPool;
    private BossEnemy boss;
    private Player player;
    private float timeSinceEnemyDeath;
    private boolean phase1;
    private Music phase1Music;
    private Music phaseSwitchMusic;
    private Music phase2Music;
    private int centerX;
    private int centerY;
    private boolean hasStarted;
    private boolean isPaused;
    
    private BitmapFont font;
    private BitmapFont uiFont;
    
    
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        
        //load in textures
        enemyBulletImage = new Texture("EnemyBullet.png");
        playerBulletImage = new Texture("PlayerBullet.png");
        
        playerIdleImage = new Texture("PlayerIdle.png");
        playerAttackImage = new Texture("PlayerAttack.png");
        
        enemyIdleImage = new Texture("EnemyIdle.png");
        enemyAttackImage = new Texture("EnemyAttack.png");
        
        winImages = new Texture[5];
        
        for(int i = 0; i < 5; i++)
        {
        	winImages[i] = new Texture("Win" + i + ".png");
        }
        
        
        //create fonts for use on-screen
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(3.5f, 3.5f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        
        uiFont = new BitmapFont();
        uiFont.setColor(Color.WHITE);
        uiFont.getData().setScale(1.5f, 1.5f);
        uiFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        
        /*
         * 
         * I was going to including sound effects for shooting, 
         * but it just didn't sound great with the music
         * 
         * */
        
        //load the music and change its settings
        phase1Music = Gdx.audio.newMusic(Gdx.files.internal("phase1.mp3"));
        phase1Music.setVolume(0.3f);
        phase1Music.setLooping(true);
        
        phaseSwitchMusic = Gdx.audio.newMusic(Gdx.files.internal("phaseSwitch.mp3"));
        phaseSwitchMusic.setVolume(0.3f);
        
        phase2Music = Gdx.audio.newMusic(Gdx.files.internal("phase2.mp3"));
        phase2Music.setVolume(0.3f);
        phase2Music.setLooping(true);
        
        //get the coordinates of the center of the screen
        centerX = Gdx.graphics.getWidth()/2;
        centerY = Gdx.graphics.getHeight()/2;
       
        reloadGame();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.3f, 0.00f, 0.5f, 1f);
        
        batch.begin();
        
        //draw the UI
        uiFont.draw(batch, "HP: " + (player != null ? player.getHP() : 0) + "/10", 30, 50);
        uiFont.draw(batch, "Boss HP: " + (boss != null ? boss.getHP() : 0), centerX - 80, Gdx.graphics.getWidth() - 25);
        
        //either allow the user to play or display start screen
	    if(hasStarted)
	    {
	    	//display pause menu
	    	if(isPaused)
	    	{
	    		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) 
	    			{
	    				isPaused = false;
	    				if(phase2Music.getPosition() != 0) phase2Music.play();
	    				if(phase1Music.getPosition() != 0) phase2Music.play();
	    				
	    			}
	    		font.draw(batch, "PAUSED", centerX - 110, centerY + 50);
	    		
	    		font.draw(batch, "PRESS ESC TO RESUME", centerX - 300, centerY - 50);
	    	}
	    	else
	    	{ //play the game
		        if(player != null)
		        {
		
		        	player.update(batch, boss, bulletPool);
		
		        	if(player.isDead())
		        	{
		        		player.releaseBullets();
		        		player = null;
		        	}
		        }
		        else
		        {
		        	font.draw(batch, "     PRESS R TO\nRETURN TO START", centerX-230, centerY+60);
		        	
		        	if(Gdx.input.isKeyJustPressed(Keys.R)) reloadGame();
		        	batch.end();
		        	return;
		        }
		        if(boss != null) 
		        {
		        	boss.update(batch, player, bulletPool);
		        	if(boss.isDead())
		        	{
		        		boss.releaseBullets();
		        		phase1Music.stop();
		        		boss = null;
		        	}
		
		        }
		        else
		        {
		        	timeSinceEnemyDeath += Gdx.graphics.getDeltaTime();
		        	if(phase1)
		        	{
		        		if(timeSinceEnemyDeath > 19)
			        	{
		        			phaseSwitchMusic.stop();
			        		phase1 = false;
			                player.releaseBullets();
			                player.setHP(10);
			                boss = new BossEnemy(centerX, centerY*1.5f, enemyIdleImage, enemyAttackImage, enemyBulletImage, 125, 1); //125 for demo, 500 otherwise
			                phase2Music.play();
		        		} 
		        		else if(timeSinceEnemyDeath < 10)
		        		{
		        			if(timeSinceEnemyDeath > 9)
		            		{
		        				batch.draw(winImages[4], centerX - 71, centerY - 67);
		            		}
		            		else if(timeSinceEnemyDeath > 8)
		            		{
		            			batch.draw(winImages[3], centerX - 71, centerY - 67);
		            		}
		            		else if(timeSinceEnemyDeath > 7)
		            		{
		            			batch.draw(winImages[2], centerX - 71, centerY - 67);
		            		}
		            		else if(timeSinceEnemyDeath > 6)
		            		{
		            			batch.draw(winImages[1], centerX - 71, centerY - 67);
		            		}
		            		else if(!phaseSwitchMusic.isPlaying() && timeSinceEnemyDeath > 5)
		            		{
		            			phaseSwitchMusic.play();
		            			batch.draw(winImages[0], centerX - 71, centerY - 67);
		            		}
		            		else
		            		{
		            			batch.draw(winImages[0], centerX - 71, centerY - 67);
		            		}
		        		}
		        	}
		        	else
		        	{
		        		batch.draw(winImages[0], centerX - 71, centerY - 67);
		        		font.draw(batch, "     PRESS R TO\nRETURN TO START", centerX-230, centerY-100);
		        		if(Gdx.input.isKeyJustPressed(Keys.R)) reloadGame();
		        		
		        	}
		        }
		        
		        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) isPaused = true;
	    	}
	    }
	    else
	    {
	    	if(Gdx.input.isKeyJustPressed(Keys.SPACE))
	    	{
	    		hasStarted = true;
	    		phase2Music.stop();
	    		phase1Music.play();
	    	}
	    	
	    	font.draw(batch, "WASD and Arrows to Move\n        Space to Shoot", centerX - 300, centerY + 50);
	    	font.draw(batch, "PRESS SPACE TO START", centerX - 300, centerY - 150);
	    }
        batch.end();
        
    }

    @Override
    public void dispose() {
        batch.dispose();
        enemyAttackImage.dispose();
        playerAttackImage.dispose();
        playerIdleImage.dispose();
        enemyIdleImage.dispose();
        enemyBulletImage.dispose();
        playerBulletImage.dispose();
    }
    
    private void reloadGame()
    {
    	 //some set-up stuff
        timeSinceEnemyDeath = 0;
        phase1 = true;
        hasStarted = false;
        isPaused = false;
        
      //instantiate the boss and player
        boss = new BossEnemy(centerX, centerY*1.5f, enemyIdleImage, enemyAttackImage, enemyBulletImage);
        player = new Player(centerX, 250, 1, playerIdleImage, playerAttackImage, playerBulletImage);
        
        //create and instantiate the bullet pool
        //this is more efficient because Java doesn't have to
        //constantly dispose of and recreate bullet objects,
        //instead just referencing ones from the pool
        bulletPool = new Bullet[5000];
        
        for(int i = 0; i < bulletPool.length; i++)
        {
        	bulletPool[i] = new Bullet(-100, -100, 3, enemyBulletImage);
        }
    }
}
