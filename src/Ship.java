import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Arrays;

public class Ship
{
	// Make private eventually?
	public double x, y, width, height;
	public double velX, velY, velocity, speed, angle;
	public BufferedImage shipImage;
	private double[] shipHitboxX, shipHitboxY;
	private int nPoints;
	private boolean accelerting;
	private int steer; // -1 - left, 0 - forward, 1 - right
	private final Color color = Color.white;

	// DEBUG
	private int[] collisionPoint;

	public Ship(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		velX = velY = angle = 0;
		shipHitboxX = shipHitboxY = new double[nPoints];
		speed = 0.15;
		nPoints = 3;
		accelerting = false;
		steer = 0;

		shipImage = readImage("res/shipImage.png");

		// DEBUG
		collisionPoint = new int[2];

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
	}

	public void setAcceleration(boolean accelerating)
	{
		this.accelerting = accelerating;
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

	private void updateShape()
	{
		// Updating ship hitbox every tick
		shipHitboxX = new double[]{x + width / 2, x, x + width};
		shipHitboxY = new double[]{y, y + height, y + height};

		// Rotating hitbox around the center
		double centerX = x + width / 2;
		double centerY = y + height / 2;
		for(int i = 0; i < nPoints; i++)
		{
			double[] rotatedPointXY = rotatePoint(shipHitboxX[i], shipHitboxY[i], centerX, centerY);
			shipHitboxX[i] = rotatedPointXY[0];
			shipHitboxY[i] = rotatedPointXY[1];
		}
	}

	private int[] convertDoubleToIntArray(double[] array)
	{
		return Arrays.stream(array).mapToInt(x -> (int) x).toArray();
	}

	private double areaOfTriangle(double[] xPoints, double[] yPoints)
	{
		//Area=0.5 * (-p1y   * p2x   + p0y   * (-p1x  + p2x   ) + p0x     * (p1y  -p2y) + p1x  * p2y)
		return 0.5 * (-yPoints[1] * xPoints[2] + yPoints[0] * (-xPoints[1] + xPoints[2]) + xPoints[0] * (yPoints[1] - yPoints[2]) + xPoints[1] * yPoints[2]);
	}

	private boolean isColliding(double area, double px, double py, double p0x, double p1x, double p2x, double p0y,
								double p1y, double p2y)
	{
		double s = 1 / (2 * area) * (p0y * p2x - p0x * p2y + (p2y - p0y) * px + (p0x - p2x) * py);
		double t = 1 / (2 * area) * (p0x * p1y - p0y * p1x + (p0y - p1y) * px + (p1x - p0x) * py);

		return s + t <= 1 && s >= 0 && t >= 0;
	}

	private boolean checkCollision()
	{
		int[] terrain = Environment.getTerrain();
		double hitboxArea = areaOfTriangle(shipHitboxX, shipHitboxY);
		double minShipX = x - width / 5;
		double maxShipX = x + 6 * width / 5;

		double p0x = shipHitboxX[0];
		double p1x = shipHitboxX[1];
		double p2x = shipHitboxX[2];
		double p0y = shipHitboxY[0];
		double p1y = shipHitboxY[1];
		double p2y = shipHitboxY[2];

		for(int terrainX = 0; terrainX < terrain.length; terrainX++)
		{
			if(terrainX < minShipX || terrainX > maxShipX) // Cannot collide with ship - optimization
			{
				continue;
			}

			double terrainY = terrain[terrainX];
			if(isColliding(hitboxArea, terrainX, terrainY, p0x, p1x, p2x, p0y, p1y, p2y))
			{
				// DEBUG
				collisionPoint[0] = terrainX;
				collisionPoint[1] = (int) terrainY;
				return true;
			}
		}
		return false;
	}

	public void tick()
	{
		if(checkCollision())
		{
			//TODO: Check if landed successfully or crashed
		}

		angle += Math.PI * steer / 180;

		if(accelerting)
		{
			velX += Math.sin(angle) / 5; // Hardcoded value yikes
			velY -= Math.cos(angle) * speed;
		}

		velX += Math.abs(velX) > Environment.drag ? velX > 0 ? -Environment.drag : Environment.drag : 0;
		velY += velY < Environment.maxGravityForce ? Environment.gravity : 0;
		velocity = Math.sqrt(velX * velX + velY * velY);

		x += velX;
		y += velY;

		updateShape();
	}

	public void render(Graphics g)
	{
		g.setColor(color);
		Graphics2D g2d = (Graphics2D) g.create();
		int cx = (int) (width / 2);
		int cy = (int) (height / 2);
		g2d.translate(x + cx, y + cy); // Ship's center is in top left (of the screen)
		g2d.rotate(angle);
		g2d.drawImage(shipImage, -cx, -cy, null); // draw image in top left point of the ship

		// DEBUG
		g.setColor(Color.red);
		g.fillOval(collisionPoint[0] - 5, collisionPoint[1] - 5, 10, 10);
	}
}
