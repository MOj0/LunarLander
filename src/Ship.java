import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Arrays;

public class Ship
{
	// Make private eventually?
	public double x, y, width, height;
	public double velX, velY, velocity, speed, angle, deviation;
	public int terrainDelta;
	public BufferedImage shipImage;
	private double[] shipHitboxX, shipHitboxY;
	private int nPoints;
	private boolean accelerating, reversing;
	private int steer; // -1 - left, 0 - forward, 1 - right
	private final Color color = Color.white;
	private final int[] terrain = Environment.getTerrain();

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
		velX = velY = angle = deviation = terrainDelta = 0;
		shipHitboxX = shipHitboxY = new double[nPoints];
		speed = 0.15;
		nPoints = 3;
		accelerating = reversing = false;
		steer = 0;

		shipImage = readImage("res/shipImage.png");

		// DEBUG
		collisionPoint = new int[2];
		collisionColor = Color.red;
		terrainSlopePoints = new int[2][2];

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
		double hitboxArea = areaOfTriangle(shipHitboxX, shipHitboxY);
		double minShipX = x - width / 5;
		double maxShipX = x + 6 * width / 5;

		double p0x = shipHitboxX[0];
		double p1x = shipHitboxX[1];
		double p2x = shipHitboxX[2];
		double p0y = shipHitboxY[0];
		double p1y = shipHitboxY[1];
		double p2y = shipHitboxY[2];

		for(int terrainXIndex = 0; terrainXIndex < terrain.length; terrainXIndex++)
		{
			if(terrainXIndex < minShipX || terrainXIndex > maxShipX) // Cannot collide with ship - optimization
			{
				continue;
			}

			double terrainY = terrain[terrainXIndex];
			if(isColliding(hitboxArea, terrainXIndex, terrainY, p0x, p1x, p2x, p0y, p1y, p2y))
			{
				// DEBUG
				collisionPoint[0] = terrainXIndex;
				collisionPoint[1] = (int) terrainY;
				return true;
			}
		}
		return false;
	}


	private boolean checkPointsOnLine(double deviation, double x1, double y1, double x2, double y2, double x3,
									  double y3)
	{
		double slope1 = (y2 - y1) / (x2 - x1);
		double slope2 = (y3 - y1) / (x3 - x1);
		return Math.abs(slope1 - slope2) < deviation;
	}

	private boolean checkSlopes(double deviation, double x1, double y1, double x2, double y2, double x3, double y3,
								double x4, double y4)
	{
		// (x1, y1), (x2, y2) - Ship (bottom line)
		// (x3, y3), (x4, y4) - Terrain
		double slopeShip = (y2 - y1) / (x2 - x1);
		double slopeTerrain = (y4 - y3) / (x4 - x3);
		return Math.abs(slopeShip - slopeTerrain) < deviation;
	}

	public void tick()
	{
		if(checkCollision())
		{
			double x1 = shipHitboxX[1];
			double y1 = shipHitboxY[1];
			double x2 = shipHitboxX[2];
			double y2 = shipHitboxY[2];
			int x3 = collisionPoint[0] - terrainDelta;
			double y3 = terrain[x3];
			int x4 = collisionPoint[0] + terrainDelta;
			double y4 = terrain[x4];
			terrainSlopePoints[0][0] = x3;
			terrainSlopePoints[0][1] = (int) y3;
			terrainSlopePoints[1][0] = x4;
			terrainSlopePoints[1][1] = (int) y4;
			collisionColor = checkSlopes(deviation, x1, y1, x2, y2, x3, y3, x4, y4) ? Color.green : Color.red;
		}

		angle += Math.PI * steer / 180;
		terrainDelta = (int) Math.min(20, (1 - Math.abs(Math.cos(angle))) * 300) + 6;

		if(accelerating)
		{
			velX += Math.sin(angle) / 5; // Hardcoded value yikes
			velY -= Math.cos(angle) * speed;
		}
		else if(reversing)
		{
			velX -= Math.sin(angle) / 25; // Hardcoded value yikes
			velY += Math.cos(angle) * speed / 2;
		}

		velX += Math.abs(velX) > Environment.drag ? velX > 0 ? -Environment.drag : Environment.drag : 0;
		velY += velY < Environment.maxGravityForce ? Environment.gravity : 0;
		velocity = Math.sqrt(velX * velX + velY * velY);
//		deviation = Math.max(0.05, 0.3 - velocity);
		deviation = velocity > 1 ? 0 : Math.max(0.05, 0.25 - Math.abs(angle));

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
//		g.setColor(Color.red);
//		g.drawPolygon(convertDoubleToIntArray(shipHitboxX), convertDoubleToIntArray(shipHitboxY), 3);
		g.setColor(collisionColor);
		g.fillOval(collisionPoint[0] - 5, collisionPoint[1] - 5, 10, 10);
		g.drawLine(terrainSlopePoints[0][0], terrainSlopePoints[0][1], terrainSlopePoints[1][0],
				terrainSlopePoints[1][1]);
		g.drawLine((int) shipHitboxX[1], (int) shipHitboxY[1], (int) shipHitboxX[2], (int) shipHitboxY[2]);
	}
}
