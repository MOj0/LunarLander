import java.awt.*;
import java.util.Arrays;

public class Ship
{
	// TODO: Feel of the game: Vse se mora dogajat bolj pocasi! https://youtu.be/LrEvoKI07Ww?t=436
	// Make private?
	public double x, y, width, height, payloadHeight, bodyHeight, landingGearSize;
	public double velX, velY, speed, angle;
	private int cabinSize;
	private double[] shipPolygonX, shipPolygonY;
	private int nPoints;
	private boolean accelerting;
	private int steer; // -1 - left, 0 - forward, 1 - right

	private final Color color = Color.white;

	public Ship(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		payloadHeight = height / 4;
		landingGearSize = height / 10;
		bodyHeight = height - payloadHeight - landingGearSize;
		cabinSize = 20;
		velX = velY = angle = 0;
		speed = 0.15;
		nPoints = 11;
		accelerting = false;
		steer = 0;

		updateShape();
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
		double lowerBodyY = y + payloadHeight + bodyHeight;
		double quarterWidth = width / 4;

		shipPolygonX = new double[]{x + width / 2, x, x, x + quarterWidth, x + quarterWidth - landingGearSize,
				x + quarterWidth, x + 3 * quarterWidth, x + 3 * quarterWidth + landingGearSize, x + 3 * quarterWidth,
				x + width, x + width};
		shipPolygonY = new double[]{y, y + payloadHeight, lowerBodyY, lowerBodyY, y + height, lowerBodyY, lowerBodyY,
				y + height, lowerBodyY, lowerBodyY, y + payloadHeight};

		double centerX = x + width / 2;
		double centerY = y + height / 2;
		for(int i = 0; i < nPoints; i++)
		{
			double[] rotatedPointXY = rotatePoint(shipPolygonX[i], shipPolygonY[i], centerX, centerY);
			shipPolygonX[i] = rotatedPointXY[0];
			shipPolygonY[i] = rotatedPointXY[1];
		}
	}

	private int[] convertDoubleToIntArray(double[] array)
	{
		return Arrays.stream(array).mapToInt(x -> (int) x).toArray();
	}

	public void tick()
	{
		angle += Math.PI * steer / 180;

		if(accelerting)
		{
			velX += Math.sin(angle) / 5;
			velY -= Math.cos(angle) * speed;
		}

		velX += Math.abs(velX) > Environment.drag ? velX > 0 ? -Environment.drag : Environment.drag : 0;
		velY += velY < Environment.maxGravityForce ? Environment.gravityForce : 0;

		x += velX;
		y += velY;

		updateShape();
	}

	public void render(Graphics g)
	{
		g.setColor(color);
		g.drawPolygon(convertDoubleToIntArray(shipPolygonX), convertDoubleToIntArray(shipPolygonY), 11);
		g.drawOval((int) (x + width / 2 - cabinSize / 2), (int) (y + height / 2 - cabinSize / 2), cabinSize,
				cabinSize);
	}
}
