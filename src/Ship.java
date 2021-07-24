import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Arrays;

public class Ship
{
	private final Color COLOR = Color.white;
	private final int[] TERRAIN = Environment.getTerrain();

	// Make private eventually?
	public double x, y, width, height;
	public double velX, velY, velocity, speed, angle, deviation; // deviation - max rounding error when checking slopes
	public int terrainDelta; // How many indecies of the terrain to the left and right does collision check do
	public BufferedImage shipImage;
	private double[] shipHitboxX, shipHitboxY;
	private int nPoints;
	private boolean accelerating, reversing;
	private int steer; // -1 - left, 0 - forward, 1 - right
	private double[][] boostShape;
	private double boostShapeHeight, boostShapeHeightVariying, boostShapeHeightCounter;

	// DEBUG
	private int[] collisionPoint;
	private Color collisionColor;
	private int[][] terrainSlopePoints;

	public Ship(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		velX = velY = angle = deviation = terrainDelta = steer = 0;
		shipHitboxX = shipHitboxY = new double[nPoints];
		speed = 0.05;
		nPoints = 3;
		accelerating = reversing = false;
		boostShape = new double[nPoints][2];
		boostShapeHeight = 3 * this.height / 4;
		boostShapeHeightVariying = boostShapeHeightCounter = 0;

		shipImage = readImage("res/shipImage.png");

		resetDebug();
		updateShape();
	}

	private BufferedImage readImage(String path)
	{
		try
		{
			return ImageIO.read(new FileInputStream(path));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void reset(int x, int y)
	{
		this.x = x;
		this.y = y;
		velX = velY = angle = 0;
		resetDebug();
	}

	public void resetDebug()
	{
		collisionPoint = new int[2];
		collisionColor = Color.red;
		terrainSlopePoints = new int[2][2];
	}

	public void setAcceleration(boolean accelerating)
	{
		this.accelerating = accelerating;
	}

	public void setReverse(boolean reversing)
	{
		this.reversing = reversing;
	}

	public void setSteer(int steer)
	{
		this.steer = steer;
	}

	private double[] rotatePoint(double px, double py, double centerX, double centerY)
	{
		double sinx = Math.sin(angle);
		double cosx = Math.cos(angle);
		px -= centerX;
		py -= centerY;
		return new double[]{centerX + px * cosx - py * sinx, centerY + px * sinx + py * cosx};
	}

	private double lerp(double a, double b, double t)
	{
		return a + (b - a) * t;
	}

	private void updateShape()
	{
		// Updating ship hitbox every tick
		shipHitboxX = new double[]{x + width / 2, x, x + width};
		shipHitboxY = new double[]{y, y + height, y + height};

		// Update boost shape variables
		boostShapeHeight = accelerating ? 3 * height / 4 : lerp(boostShapeHeight, 0, 0.33);
		boostShapeHeightVariying = 1.5 * Math.sin(boostShapeHeightCounter++);

		// Update boost shape
		boostShape[0] = new double[]{x + 16, x + width - 16, x + width / 2};
		boostShape[1] = new double[]{y + height - 3, y + height - 3,
				y + height + boostShapeHeight + boostShapeHeightVariying};

		// Rotate hitbox and boost shape around the center of the ship
		double centerX = x + width / 2;
		double centerY = y + height / 2;
		for(int i = 0; i < nPoints; i++)
		{
			double[] rotatedPointShip = rotatePoint(shipHitboxX[i], shipHitboxY[i], centerX, centerY);
			shipHitboxX[i] = rotatedPointShip[0];
			shipHitboxY[i] = rotatedPointShip[1];

			double[] rotatedPointBoost = rotatePoint(boostShape[0][i], boostShape[1][i], centerX, centerY);
			boostShape[0][i] = rotatedPointBoost[0];
			boostShape[1][i] = rotatedPointBoost[1];
		}
	}

	private int[] convertDoubleToIntArray(double[] array)
	{
		return Arrays.stream(array).mapToInt(x -> (int) x).toArray();
	}

	private double areaOfTriangle(double p0x, double p1x, double p2x, double p0y, double p1y, double p2y)
	{
		return 0.5 * (-p1y * p2x + p0y * (-p1x + p2x) + p0x * (p1y - p2y) + p1x * p2y);
	}

	private boolean isPointInsideTriangle(double area, double px, double py, double p0x, double p1x, double p2x,
										  double p0y, double p1y, double p2y)
	{
		double s = 1 / (2 * area) * (p0y * p2x - p0x * p2y + (p2y - p0y) * px + (p0x - p2x) * py);
		double t = 1 / (2 * area) * (p0x * p1y - p0y * p1x + (p0y - p1y) * px + (p1x - p0x) * py);

		return s + t <= 1 && s >= 0 && t >= 0;
	}

	private boolean checkCollision(double[] shipHitboxX, double[] shipHitboxY)
	{
		double p0x = shipHitboxX[0];
		double p1x = shipHitboxX[1];
		double p2x = shipHitboxX[2];
		double p0y = shipHitboxY[0];
		double p1y = shipHitboxY[1];
		double p2y = shipHitboxY[2];

		double hitboxArea = areaOfTriangle(p0x, p1x, p2x, p0y, p1y, p2y);
		double minShipX = x - width / 5;
		double maxShipX = x + 6 * width / 5;

		for(int terrainXIndex = 0; terrainXIndex < TERRAIN.length; terrainXIndex++)
		{
			if(terrainXIndex < minShipX || terrainXIndex > maxShipX) // Cannot collide with ship - optimization
			{
				continue;
			}

			double terrainY = TERRAIN[terrainXIndex];
			if(isPointInsideTriangle(hitboxArea, terrainXIndex, terrainY, p0x, p1x, p2x, p0y, p1y, p2y))
			{
				// DEBUG
				collisionPoint[0] = terrainXIndex;
				collisionPoint[1] = (int) terrainY;
				return true;
			}
		}
		return false;
	}

	// Checks slope between base of triangle and terrain points
	private boolean checkSlopes(double deviation, double x1, double y1, double x2, double y2, double x3, double y3,
								double x4, double y4)
	{
		double slopeShip = (y2 - y1) / (x2 - x1);
		double slopeTerrain = (y4 - y3) / (x4 - x3);
		return Math.abs(slopeShip - slopeTerrain) < deviation;
	}

	public void tick()
	{
		if(checkCollision(shipHitboxX, shipHitboxY))
		{
			// Points in terrain
			int terrainX0 = collisionPoint[0] - terrainDelta;
			double terrainY0 = TERRAIN[terrainX0];
			int terrainX1 = collisionPoint[0] + terrainDelta;
			double terrainY1 = TERRAIN[terrainX1];

			// 												ShipHitboxX/Y[1, 2] - base of triangle
			boolean landed = checkSlopes(deviation, shipHitboxX[1], shipHitboxY[1], shipHitboxX[2], shipHitboxY[2],
					terrainX0, terrainY0, terrainX1, terrainY1);

			Game.gameState = landed ? State.Won : State.Lost;

			//DEBUG
			terrainSlopePoints[0][0] = terrainX0;
			terrainSlopePoints[0][1] = (int) terrainY0;
			terrainSlopePoints[1][0] = terrainX1;
			terrainSlopePoints[1][1] = (int) terrainY1;

			collisionColor = landed ? Color.green : Color.red;
		}

		angle += Math.PI * steer / 180;
		terrainDelta = (int) Math.min(20, (1 - Math.abs(Math.cos(angle))) * 300) + 6;

		if(accelerating)
		{
			velX += Math.sin(angle) * speed;
			velY -= Math.cos(angle) * speed;
		}
		else if(reversing)
		{
			velX -= Math.sin(angle) * speed / 2;
			velY += Math.cos(angle) * speed / 2;
		}

		velY += velY < Environment.MAX_GRAVITY_FORCE ? Math.max(1, Math.abs(velY)) * Environment.gravity : 0;

		velocity = Math.sqrt(velX * velX + velY * velY);
		deviation = velocity > 1 || Math.abs(angle) > Math.PI / 5 ? 0 : Math.max(0, 0.25 - Math.abs(angle));

		x += velX;
		y += velY;

		updateShape();
	}

	public void render(Graphics g)
	{
		g.setColor(COLOR);
		Graphics2D g2d = (Graphics2D) g.create();
		int cx = (int) (width / 2);
		int cy = (int) (height / 2);
		g2d.translate(x + cx, y + cy); // Ship's center is in top left (of the screen)
		g2d.rotate(angle);
		g2d.drawImage(shipImage, -cx, -cy, null); // draw image in top left point of the ship
		if(boostShapeHeight > 0.2)
			g.drawPolygon(convertDoubleToIntArray(boostShape[0]), convertDoubleToIntArray(boostShape[1]), 3);


		// DEBUG
//		g.setColor(Color.red);
//		g.drawPolygon(convertDoubleToIntArray(shipHitboxX), convertDoubleToIntArray(shipHitboxY), 3);
		if(collisionPoint[0] != 0)
		{
			g.setColor(collisionColor);
			g.fillOval(collisionPoint[0] - 5, collisionPoint[1] - 5, 10, 10);
			g.drawLine(terrainSlopePoints[0][0], terrainSlopePoints[0][1], terrainSlopePoints[1][0],
					terrainSlopePoints[1][1]);
		}
	}
}
