package com.speich.finalproject.gameobjects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;

public class Player extends GameObject {

	private float MoveSpeed;
	private int HP;
	private Circle Hitbox;
	private List<Bullet> Bullets;
	private float TimeSinceLastFire;
	private Texture IdleImage;
	private Texture AttackImage;
	private Texture BulletImage;
	
	

	public Player(float x, float y, float newMoveSpeed, Texture idleImage, Texture attackImage, Texture bulletImage)
	{
		HP = 10;
		this.setPosition(x, y);
		this.setImage(idleImage);
		this.setIdleImage(idleImage);
		this.setAttackImage(attackImage);
		this.setBulletImage(bulletImage);
		Hitbox = new Circle();
		Hitbox.radius = 2;
		Hitbox.setX(this.getX() + 12);
		Hitbox.setY(this.getY() + 12);
		MoveSpeed = newMoveSpeed;
		Bullets = new ArrayList<Bullet>();
		TimeSinceLastFire = 1;
	}
	
	public void update(Batch batch, BossEnemy enemy, Bullet[] pool) 
	{
		float xChange = 0;
		float yChange = 0;
		boolean isShift = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
		//reduced speed
		if(isShift)
		{
			//horizontal movement
			if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) xChange -= MoveSpeed*0.67;
			if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) xChange += MoveSpeed*0.67;
			
			//vertical movement
			if(Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) yChange -= MoveSpeed*0.67;
			if(Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) yChange += MoveSpeed*0.67;
		}
		else 
		{
			//horizontal movement
			if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) xChange -= MoveSpeed;
			if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) xChange += MoveSpeed;
			
			//vertical movement
			if(Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) yChange -= MoveSpeed;
			if(Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) yChange += MoveSpeed;
		}
		if(Gdx.input.isKeyPressed(Keys.SPACE) && TimeSinceLastFire >= 0.2)
		{
			TimeSinceLastFire = 0;
			fireBullets(isShift, pool);
			this.setImage(getAttackImage());
		}
		else if(TimeSinceLastFire >= 0.1)
		{
			this.setImage(getIdleImage());
		}
		
		if(this.getX() + xChange < 0 || this.getX() + xChange > 768)
		{
			xChange = 0;
		}
		if(this.getY() + yChange < 0 || this.getY() + yChange > 768)
		{
			yChange = 0;
		}
		
		this.changePosition(xChange, yChange);
		Hitbox.x += xChange;
		Hitbox.y += yChange;
		
		//if the enemy exists, check for hits
		if(enemy != null)
		{
			Circle enemyHitbox = enemy.getHitbox();
			for(int i = 0; i < Bullets.size(); i++)
			{
				if(Bullets.get(i).isOutOfBounds())
				{
					Bullets.get(i).deactivate();
					Bullets.remove(i);
					i--;
				}
				else
				{
					Bullets.get(i).update(batch, 8, 8);
				
				
					if(Bullets.get(i).hitObject(enemyHitbox))
					{
						enemy.takeDamage();
						
						Bullets.get(i).deactivate();
						Bullets.remove(i);
						i--;
					}
				}
				
			}
		}
		
		TimeSinceLastFire += Gdx.graphics.getDeltaTime();
		batch.draw(this.getImage(), this.getX(), this.getY(), 32, 32);
		
		
	}
	public void update(Batch batch) 
	{
		batch.draw(this.getImage(), this.getX(), this.getY(), 32, 32);
	}
	public void takeDamage()
	{
		HP--;
		System.out.println("Player Hit\nHP: " + HP);
	}
	
	private void fireBullets(boolean isShift, Bullet[] pool)
	{
		
		int start = 0;
		
		//fire bullets with a spread depending on if the player is pressing shift
	    if(isShift)
	    {
			for(int j = start; j < pool.length; j++)
			{
				if(!pool[j].isActive())
				{
					pool[j].activate(this.getX() + 6, this.getY() + 12, 2f, 90.5f, BulletImage);
					Bullets.add(pool[j]);
					start = j;
					j = pool.length;
				}
			}
			for(int j = start; j < pool.length; j++)
			{
				if(!pool[j].isActive())
				{
					pool[j].activate(this.getX() + 12, this.getY() + 12, 2f, 90, BulletImage);
					Bullets.add(pool[j]);
					start = j;
					j = pool.length;
				}
			}
			for(int j = start; j < pool.length; j++)
			{
				if(!pool[j].isActive())
				{
					pool[j].activate(this.getX() + 18, this.getY() + 12, 2f, 89.5f, BulletImage);
					Bullets.add(pool[j]);
					start = j;
					j = pool.length;
				}
			}
	    }
	    else
	    {
	    	for(int j = start; j < pool.length; j++)
			{
				if(!pool[j].isActive())
				{
					pool[j].activate(this.getX(), this.getY() + 12, 2f, 92, BulletImage);
					Bullets.add(pool[j]);
					start = j;
					j = pool.length;
				}
			}
			for(int j = start; j < pool.length; j++)
			{
				if(!pool[j].isActive())
				{
					pool[j].activate(this.getX() + 12, this.getY() + 12, 2f, 90, BulletImage);
					Bullets.add(pool[j]);
					start = j;
					j = pool.length;
				}
			}
			for(int j = start; j < pool.length; j++)
			{
				if(!pool[j].isActive())
				{
					pool[j].activate(this.getX() + 24, this.getY() + 12, 2f, 88f, BulletImage);
					Bullets.add(pool[j]);
					start = j;
					j = pool.length;
				}
			}
	    }
	}
	

	public Circle getHitbox()
	{
		return Hitbox;
	}
	
	public boolean isDead()
	{
		return HP <= 0;
	}

	public Texture getIdleImage()
	{
		return IdleImage;
	}
	public void setIdleImage(Texture idleImage)
	{
		IdleImage = idleImage;
	}
	
	public Texture getAttackImage()
	{
		return AttackImage;
	}
	public void setAttackImage(Texture attackImage)
	{
		AttackImage = attackImage;
	}

	public void releaseBullets() {
		for(int i = 0; i < Bullets.size(); i++)
		{
			Bullets.get(i).deactivate();
			Bullets.remove(i);
			i--;
		}
	}
	
	public void setHP(int hp)
	{
		HP = hp;
	}
	public int getHP()
	{
		return HP;
	}
	
	public Texture getBulletImage() {
		return BulletImage;
	}

	public void setBulletImage(Texture bulletImage) {
		BulletImage = bulletImage;
	}
}
