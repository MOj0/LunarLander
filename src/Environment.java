import java.awt.*;
import java.util.Random;

public class Environment
{
	public static final double gravityForce = 0.05, maxGravityForce = 1.5, drag = 0.01;

	private static int[] terrain, xPoints;
	private static Random r;
	private static double offset;
	// TODO: Add stars -> when moving to the left or right, "new" stars appear

	static
	{
		terrain = new int[Game.WIDTH];
		xPoints = new int[Game.WIDTH];
		r = new Random();

		createTerrain();
	}

	public static void createTerrain()
	{
		offset = r.nextInt(10000);
		int scale = r.nextInt(25) + 375;

		for(int i = 0; i < xPoints.length; i++)
		{
			xPoints[i] = i;
			terrain[i] = (int) (5 * Game.HEIGHT / 6 + ImprovedNoise.noise(offset, 0, offset) * scale);
			offset += 0.005;
		}
	}

	public static void render(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.drawPolyline(xPoints, terrain, Game.WIDTH);
	}
}
