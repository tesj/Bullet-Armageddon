package com.speich.finalproject.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends GameObject 
{
	//in radians (0 to 2(pi) instead of 0 to 360)
	private float Angle;
	private float Velocity; 
	private Circle Hitbox;
	private boolean IsActive;
	
	
	public Bullet(float x, float y, float velocity, Texture image)
	{
		setPosition(x, y);
		Angle = 0;
		Velocity = velocity;
		setImage(image);
		Hitbox = new Circle();
		Hitbox.setRadius(4);
		Hitbox.x = this.getX();
		Hitbox.y = this.getY();
		IsActive = false;
	}
	
	public Bullet(float newX, float newY, float newVelocity, float newAngle, Texture newImage)
	{
		setPosition(newX, newY);
		Angle = (float) Math.toRadians(newAngle);
		Velocity = newVelocity;
		setImage(newImage);
		Hitbox = new Circle();
		Hitbox.setRadius(4);
		Hitbox.x = this.getX() + 4;
		Hitbox.y = this.getY() + 4;
	}
	
	public void update(Batch batch)
	{
		move();

		batch.draw(this.getImage(), this.getX(), this.getY());

	}
	
	public void update(Batch batch, int width, int height)
	{
		move();
		batch.draw(this.getImage(), this.getX(), this.getY(), width, height);

	}
	
	
	public void move()
	{
		float xChange = (float)Math.cos(Angle) * Velocity;
		float yChange = (float)Math.sin(Angle) * Velocity;
		
		this.changePosition(xChange, yChange);
		Hitbox.x += xChange;
		Hitbox.y += yChange;
	}
	
	public boolean isActive()
	{
		return IsActive;
	}
	
	public void activate(float x, float y, float newVelocity, float newAngle)
	{
		IsActive = true;
		this.setPosition(x, y);
		Hitbox.x = x;
		Hitbox.y = y;
		Velocity = newVelocity;
		Angle = (float) Math.toRadians(newAngle);
	}
	
	public void activate(float x, float y, float newVelocity, float newAngle, Texture image)
	{
		IsActive = true;
		this.setImage(image);
		this.setPosition(x, y);
		Hitbox.x = x;
		Hitbox.y = y;
		Velocity = newVelocity;
		Angle = (float) Math.toRadians(newAngle);
	}
	
	public void deactivate()
	{
		IsActive = false;
		this.setPosition(-100, -100);
		Hitbox.x = -100;
		Hitbox.y = -100;
	}
	
	public boolean isOutOfBounds()
	{
		float x = this.getX();
		float y = this.getY();
		return x < -50 || x > Gdx.graphics.getWidth() + 50 || y < -50 || y > Gdx.graphics.getHeight() + 50;
	}
	
	public boolean hitObject(Circle objectHitbox)
	{
		// Calculate the distance between the centers of the two circles
	    float distance = Vector2.dst(this.Hitbox.x, this.Hitbox.y, objectHitbox.x, objectHitbox.y);
	    
	    // Check if the distance is less than the sum of their radii
	    return distance < (this.Hitbox.radius + objectHitbox.radius);
	}
}
