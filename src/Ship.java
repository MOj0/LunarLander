import java.awt.*;

public class Ship
{
	private int x, y, width, height, payloadHeight, bodyHeight, landingGearSize, cabinSize;
	private double velX, velY, speed, angle;
	private int[] shipPolygonX, shipPolygonY;
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
		speed = 1;
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

	private int[] rotatePoint(int px, int py, int centerX, int centerY)
	{
		double sinx = Math.sin(angle);
		double cosx = Math.cos(angle);
		px -= centerX;
		py -= centerY;
		return new int[]{(int) (centerX + px * cosx - py * sinx), (int) (centerY + px * sinx + py * cosx)};
	}

	private void updateShape()
	{
		int lowerBodyY = y + payloadHeight + bodyHeight;
		int quarterWidth = width / 4;

		shipPolygonX = new int[]{x + width / 2, x, x, x + quarterWidth, x + quarterWidth - landingGearSize,
				x + quarterWidth, x + 3 * quarterWidth, x + 3 * quarterWidth + landingGearSize, x + 3 * quarterWidth,
				x + width, x + width};
		shipPolygonY = new int[]{y, y + payloadHeight, lowerBodyY, lowerBodyY, y + height, lowerBodyY, lowerBodyY,
				y + height, lowerBodyY, lowerBodyY, y + payloadHeight};

		int centerX = x + width / 2;
		int centerY = y + height / 2;
		for(int i = 0; i < nPoints; i++)
		{
			int[] rotatedPoint = rotatePoint(shipPolygonX[i], shipPolygonY[i], centerX, centerY);
			shipPolygonX[i] = rotatedPoint[0];
			shipPolygonY[i] = rotatedPoint[1];
		}
	}

	public void tick()
	{
		angle += Math.PI * steer / 180;

		if(accelerting)
		{
			velX += Math.sin(angle);
			velY -= Math.cos(angle) * speed;
		}

		velX += Math.abs(velX) < Environment.drag ? velX > 0 ? -Environment.drag : Environment.drag : 0;
		velY += Environment.gravityForce;
		x += velX;
		y += Math.min(velY, Environment.maxGravityForce);

		updateShape();
	}

	public void render(Graphics g)
	{
		g.setColor(color);
		g.drawPolygon(shipPolygonX, shipPolygonY, 11);
		g.drawOval(x + width / 2 - cabinSize / 2, y + height / 2 - cabinSize / 2, cabinSize, cabinSize);
	}
}
