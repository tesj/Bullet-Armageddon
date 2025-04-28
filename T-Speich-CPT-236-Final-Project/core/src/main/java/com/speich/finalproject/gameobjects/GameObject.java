package com.speich.finalproject.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class GameObject 
{
	//can't use integers due to problems with angles at low speeds
	private float X;
	private float Y;
	private Texture Image;
	
	public void setPosition(float x, float y)
	{
		X = x;
		Y = y;
	}
	public void changePosition(float xChange, float yChange)
	{
		X += xChange;
		Y += yChange;
	}
	public void setImage(Texture image)
	{
		Image = image;
	}
	
	public float getX()
	{
		return X;
	}
	public float getY()
	{
		return Y;
	}
	public Texture getImage() 
	{
		return Image;
	}
	
	public abstract void update(Batch batch);
}
