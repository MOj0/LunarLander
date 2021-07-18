import java.awt.*;

public class Ship
{
	private int x, y, width, height, payloadHeight, bodyHeight, landingGearSize;
	private double velX, velY, speed, angle;
	private int[] payloadShapeX, payloadShapeY; // Triangle
	private int[] landingGearX;

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
		velX = velY = angle = 0;
		speed = 3;
		payloadShapeX = new int[]{x + width / 2, x, x + width};
		payloadShapeY = new int[]{y, y + payloadHeight, y + payloadHeight};
		landingGearX = new int[]{width / 4, 3 * width / 4};
	}

	public void accelerate()
	{
		velY = -speed;  // Has to be negative because we are going up
	}

	public void steer(int direction)
	{
		angle += Math.PI * direction / 180;
	}

	private void updatePayloadShape()
	{
		payloadShapeX[0] = x + width / 2;
		payloadShapeX[1] = x;
		payloadShapeX[2] = x + width;
		payloadShapeY[0] = y;
		payloadShapeY[1] = payloadShapeY[2] = y + payloadHeight;
	}

	public void tick()
	{
		velY += Environment.gravityForce;
		y += Math.min(velY, Environment.maxGravityForce);

		updatePayloadShape();
	}

	public void render(Graphics g)
	{
		// TODO Uncomment
//		g.setColor(color);
//		g.drawPolygon(payloadShapeX, payloadShapeY, 3);
//		g.drawRect(x, y + payloadHeight, width, bodyHeight);
//		g.drawOval(x + width / 4 + 1, y + payloadHeight + width / 4, width / 2, width / 2);
//		for(int i = 0; i < 2; i++)
//		{
//			g.drawLine(x + landingGearX[i], y + height - landingGearSize, x + landingGearX[i] + (i == 0 ?
//					-landingGearSize : landingGearSize), y + height);
//		}

		// DEBUG
		System.out.println("Angle: " + angle);
		g.setColor(Color.red);
		g.fillOval(x + width / 2 - 5, y + height - 5, 10, 10);

		g.setColor(color);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.translate(x + width / 2, y + height / 2);
		g2d.rotate(angle);
		g2d.translate(-x - width / 2, -y - height / 2);
		g2d.drawRect(x, y, width, height);
	}
}
