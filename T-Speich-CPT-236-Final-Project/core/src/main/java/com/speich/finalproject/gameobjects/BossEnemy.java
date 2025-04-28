package com.speich.finalproject.gameobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;

public class BossEnemy extends GameObject
{
	private List<Bullet> Bullets;
	private Circle Hitbox;
	private boolean IsAttacking;
	private int HP;
	private float TimeSinceAttackStart;
	private float TimeSinceLastFire;
	private float AttackMult;
	private float TimeSinceAttackEnd;
	private int CurrentAttack;
	private Texture IdleImage;
	private Texture AttackImage;
	private Texture BulletImage;
	private int maxAttack;
	private float AngleOfAttack;


	private Random Rand;
	
	private float nextX;
	private float nextY;
	
	
	
	public BossEnemy(float newX, float newY, Texture idleImage, Texture attackImage, Texture bulletImage, int hp, float attackMult)
	{
		Bullets = new ArrayList<Bullet>();
		setPosition(newX, newY);
		IsAttacking = false;
		TimeSinceAttackStart = 0;
		TimeSinceLastFire = 0;
		TimeSinceAttackEnd = 0;
		CurrentAttack = 0;
		Hitbox = new Circle();
		Hitbox.radius = 5;
		Hitbox.setX(this.getX() + 12);
		Hitbox.setY(this.getY() + 12);
		Rand = new Random();
		setImage(idleImage);
		setIdleImage(idleImage);
		setAttackImage(attackImage);
		this.setBulletImage(bulletImage);
		HP = hp;
		AttackMult = attackMult;
		nextX = newX;
		nextY = newY;
		maxAttack = 4;
		AngleOfAttack = 0;
	}
	
	public BossEnemy(float newX, float newY, Texture idleImage, Texture attackImage, Texture bulletImage)
	{
		Bullets = new ArrayList<Bullet>();
		setPosition(newX, newY);
		IsAttacking = false;
		TimeSinceAttackStart = 0;
		TimeSinceLastFire = 0;
		TimeSinceAttackEnd = 0;
		CurrentAttack = 0;
		Hitbox = new Circle();
		Hitbox.radius = 5;
		Hitbox.setX(this.getX() + 12);
		Hitbox.setY(this.getY() + 12);
		Rand = new Random();
		setImage(idleImage);
		setIdleImage(idleImage);
		setAttackImage(attackImage);
		this.setBulletImage(bulletImage);
		HP = 25; //25 for the demo, 100 otherwise
		AttackMult = 2;
		maxAttack = 2;
		AngleOfAttack = 0;
	}
	
	public void createBulletCircle(int count, float startAngle, Bullet[] pool)
	{
		createBulletCircle(count, startAngle, 0.67f, pool);

	}
	public void createBulletCircle(int count, float startAngle, float velocity, Bullet[] pool)
	{
		//calculate the angle to increase by for each bullet created for the circle
		float angleIncrement = 360f / count;
		
		//instantiate new bullets in a circle with a center that is the same as the center of the enemy
		for(int i = 0; i < count; i++)
		{
			for(int j = 0; j < pool.length; j++)
			{
				if(!pool[j].isActive())
				{
					pool[j].activate(this.getX() + 12, this.getY() + 12, velocity, startAngle + angleIncrement * i, BulletImage);
					Bullets.add(pool[j]);
					j = pool.length;
				}
			}
			//bullets.add(new Bullet(x+12, y+12, velocity, startAngle + angleIncrement * i, bulletImage));
		}
	}
	
	public void createBulletWall(int count, float arcLength, float targetX, float targetY, Bullet[] pool)
	{
		//find the angle to start at (angle to player - half of the arc length)
		float startAngle = (float) Math.toDegrees(Math.atan2(targetY - this.getY(), targetX - this.getX()) - Math.toRadians(arcLength/2));
		//calculate the angle to increase by with each iteration
		float angleIncrement = arcLength / (count-1);

		//instantiate new bullets in a wall with a specified arc length
		for(int i = 0; i < count; i++)
		{
			float angle = startAngle + angleIncrement * i;//calculate the angle that the new bullet should use
			for(int j = 0; j < pool.length; j++) 
			{
				if(!pool[j].isActive())
				{
					pool[j].activate(this.getX() + 12 + (float)Math.cos(Math.toRadians(angle)) * 15 , this.getY() + 12 + (float)Math.sin(Math.toRadians(angle)) * 15, 0.67f, angle, BulletImage);
					Bullets.add(pool[j]);
					j = pool.length;
				}
			}
			//bullets.add(new Bullet(x + 12 + (float)Math.cos(Math.toRadians(angle)) * 15, y + 12 + (float)Math.sin(Math.toRadians(angle)) * 15, 0.67f, angle, bulletImage));
		}
		
	}
	
	
	//draw the enemy + attack
	public void update(Batch batch, Player player, Bullet[] pool) 
	{
		//draw the boss
		batch.draw(this.getImage(), this.getX(), this.getY(), 32, 32);

		
		//make sure the player exists
		if(player != null)
		{
			Circle playerHitbox = player.getHitbox();		//handle bullets
			for(int i = 0; i < Bullets.size(); i++)
			{
				//remove bullet from list if it's out of bounds, otherwise call its update
				if(Bullets.get(i).isOutOfBounds()) 
				{
					Bullets.get(i).deactivate();
					Bullets.remove(i);
					i--;
					continue;
				}
				else
				{
					Bullets.get(i).update(batch, 8, 8);
				
				
					if(Bullets.get(i).hitObject(playerHitbox))
					{
						player.takeDamage();
						
						Bullets.get(i).deactivate();
						Bullets.remove(i);
						i--;
					}
				}
	
			}
		}
	
		if(!IsAttacking)
		{
			//increase the timer by the time passed in seconds since the last frame
			TimeSinceAttackEnd += Gdx.graphics.getDeltaTime();
			
			//check if it has been more than 1 second since the end of the attack
			if(TimeSinceAttackEnd > 1) 
			{
				//reset the attack timers
				TimeSinceAttackStart = 0;
				TimeSinceAttackEnd = CurrentAttack == 2 ? -2 : 0;
				
				//enable the isAttacking flag
				IsAttacking = true;
				
				//choose the next attack and move to a new location
				CurrentAttack = Rand.nextInt(maxAttack);
				
				//SetPosition(200 + rand.nextInt(601), 200 + rand.nextInt(601));
			}
		}
		else 
		{
			//increase the time since the last attack/fire by the amount of time
			//in seconds that has passed since the last frame
			TimeSinceLastFire += Gdx.graphics.getDeltaTime();
			
			//check if the current attack has been running for more than 5 seconds
			if(TimeSinceAttackStart > 1000)
			{
				//disable the isAttacking flag
				IsAttacking = false;
				
				//reset attack timers
				TimeSinceAttackStart = 0;
				TimeSinceAttackEnd = 0;
			}
			//check if it has been half a second since the last fire
			else
			{
				//call an attack method
				switch(CurrentAttack)
				{
					case 0:
						if(TimeSinceLastFire > 0.75 * AttackMult)
						{
							createBulletCircle((int)(64 / AttackMult), 0 + Rand.nextInt(30), 1, pool);
							Hitbox.setPosition(this.getX() + 12, this.getY() + 12);
							
							TimeSinceAttackStart += 150 * AttackMult;
							//reset the fire timer
							TimeSinceLastFire = 0;
							this.setImage(getAttackImage());
						}
						else if(TimeSinceLastFire > 0.25 * AttackMult)
						{
							//this sets up like a right triangle
							//dx is the base's length and dy is the height's length
							float dx = nextX - this.getX();
							float dy = nextY - this.getY();

							// Calculate the length of the hypotenuse
							// a^2 + b^2 = c^2
							float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
							
							float xChange = 0;
							float yChange = 0;
							if(magnitude != 0)
							{
								//only move if more than 10 pixels away
								if(Math.abs(dx) > 10)
								{
									xChange = dx / magnitude;
								}
								if(Math.abs(dy) > 10)
								{
									yChange = dy / magnitude;
								}
								this.changePosition(xChange, yChange);
								Hitbox.setPosition(this.getX() + 12, this.getY() + 12);
							}
						
							
							this.setImage(getIdleImage());
						}
						else
						{
							nextX = Math.min(Math.max(this.getX() + Rand.nextFloat(250) - 125, 50), 750);
							nextY = Math.min(Math.max(this.getY() + Rand.nextFloat(250) - 125, 400), 750);
						}
						break;
					case 1:
						if(TimeSinceLastFire > 0.75 && player != null)
						{
							createBulletWall((int)(25 / AttackMult), 90, player.getX(), player.getY(), pool);     
							//reset the fire timer
							TimeSinceLastFire = 0;
							TimeSinceAttackStart += 150 * AttackMult;
							this.setImage(getAttackImage());
						}
						else if(TimeSinceLastFire > 0.25)
						{
							float dx = nextX - this.getX();
							float dy = nextY - this.getY();

							// Calculate the distance
							float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
							
							float xChange = 0;
							float yChange = 0;
							if(magnitude != 0)
							{
								//only move if more than 10 pixels away
								if(Math.abs(dx) > 10)
								{
									xChange = dx / magnitude;
								}
								if(Math.abs(dy) > 10)
								{
									yChange = dy / magnitude;
								}
								this.changePosition(xChange, yChange);
								Hitbox.setPosition(this.getX() + 12, this.getY() + 12);
							}
							

							

							this.setImage(getIdleImage());
						}
						else
						{
							

							nextX = Math.min(Math.max(this.getX() + Rand.nextFloat(250) - 125, 50), 750);
							nextY = Math.min(Math.max(this.getY() + Rand.nextFloat(250) - 125, 400), 750);
						}
						break;
						
					case 2:
						if(TimeSinceLastFire > 0.1)
						{
							createBulletCircle(20, AngleOfAttack, 0.4f, pool);
							AngleOfAttack += 1.25;
							//reset the fire timer
							TimeSinceLastFire = 0;
							TimeSinceAttackStart += 3 * AttackMult;
							this.setImage(getAttackImage());
							
						}
						else if(TimeSinceAttackStart > 0.1)
						{
							float dx = nextX - this.getX();
							float dy = nextY - this.getY();
							
							// Calculate the magnitude (length) of the vector
							float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
							
							float xChange = 0;
							float yChange = 0;
							if(magnitude != 0)
							{
								//only move if more than 10 pixels away
								if(Math.abs(dx) > 10)
								{
									xChange = dx / magnitude;
								}
								if(Math.abs(dy) > 10)
								{
									yChange = dy / magnitude;
								}
							    
								this.changePosition(xChange, yChange);
								Hitbox.setPosition(this.getX() + 12, this.getY() + 12);
							}
							

							this.setImage(getIdleImage());
						}
						else if(TimeSinceAttackStart <= 0.5)
						{
							TimeSinceLastFire = -3;
							TimeSinceAttackStart += 1;
							AngleOfAttack = 0;
							nextX = Gdx.graphics.getWidth() / 2;
							nextY = Gdx.graphics.getHeight() / 2;
						}
						break;
						
					case 3:
						if(TimeSinceLastFire > 0.01)
						{
							createBulletCircle(16, AngleOfAttack, 1.25f, pool);
							AngleOfAttack += 0.12;
							//reset the fire timer
							TimeSinceLastFire = 0;
							TimeSinceAttackStart += 0.3 * AttackMult;
							
							if(TimeSinceAttackStart % 60 > 59.7)
							{
								createBulletCircle(48, 0, pool);
							}
							
							this.setImage(getAttackImage());
						}
						else if(TimeSinceAttackStart > 0.5 && TimeSinceAttackStart < 6)
						{
							float dx = nextX - this.getX();
							float dy = nextY - this.getY();
							System.out.println("Moving towards center");
							// Calculate the magnitude (length) of the vector
							float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
							
							float xChange = 0;
							float yChange = 0;
							System.out.println("Magnitude: " + magnitude);
							if(magnitude != 0)
							{
								//only move if more than 10 pixels away
								if(Math.abs(dx) > 10)
								{
									xChange = dx / magnitude;
								}
								if(Math.abs(dy) > 10)
								{
									yChange = dy / magnitude;
								}
								System.out.println("DX: " + dx);
								System.out.println("DY: " + dy);
								
								System.out.println("X: " + getX());
								System.out.println("Y: " + getY());
								
								System.out.println("Next X: " + nextX);
								System.out.println("Next Y: " + nextY);
								
								System.out.println("X Change: " + xChange);
								System.out.println("Y Change: " + yChange);
								
								System.out.println("Time Since Last Fire: " + TimeSinceLastFire);
								System.out.println("Time Since Attack Start: " + TimeSinceAttackStart);
								System.out.println("\n\n");
								this.changePosition(xChange, yChange);
								Hitbox.setPosition(this.getX() + 12, this.getY() + 12);
							}

							this.setImage(getIdleImage());
						}
						else if(TimeSinceAttackStart <= 0.5)
						{
							System.out.println("Setting up");
							TimeSinceLastFire = -5;
							TimeSinceAttackStart += 1;
							AngleOfAttack = 0;
							nextX = Gdx.graphics.getWidth() / 2;
							nextY = Gdx.graphics.getHeight() / 2;
							System.out.println("nextX: " + nextX);
							System.out.println("nextY: " + nextY);
						}
						break;
				}
				
				
				
			}
			
			
		}
	}

	
	//just draw the enemy
	public void update(Batch batch) {
		batch.draw(this.getImage(), this.getX(), this.getY());
	}
	
	public Circle getHitbox()
	{
		return Hitbox;
	}
	
	public void takeDamage()
	{
		HP--;
	}
	public boolean isDead()
	{
		return HP <= 0;
	}

	public Texture getIdleImage() {
		return IdleImage;
	}

	public void setIdleImage(Texture idleImage) {
		IdleImage = idleImage;
	}

	public Texture getAttackImage() {
		return AttackImage;
	}

	public void setAttackImage(Texture attackImage) {
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
	
	public Texture getBulletImage() {
		return BulletImage;
	}

	public void setBulletImage(Texture bulletImage) {
		BulletImage = bulletImage;
	}
	
	public int getHP()
	{
		return HP;
	}

}
