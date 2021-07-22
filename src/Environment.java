import java.awt.*;
import java.util.Random;

public class Environment
{
	public static final double gravityForce = 0.05;
	public static final double maxGravityForce = 2;
	public static final double drag = 0.01;
	public static final int WIDTH_MULTIPLIER = 4;
	public static double gravity;

	private static int[] terrain, xPoints;
	private static Random r;
	private static double offset;
	private static final int terrainY;
	// TODO: Add stars -> when moving to the left or right, "new" stars appear

	static
	{
		terrain = new int[WIDTH_MULTIPLIER * Game.WIDTH];
		xPoints = new int[terrain.length];
		r = new Random();
		gravity = gravityForce;
		terrainY = 5 * Game.HEIGHT / 6;

		createTerrain();
	}

	public static void createTerrain()
	{
		offset = r.nextInt(10000);
		int scale = r.nextInt(25) + 375;

		for(int i = 0; i < terrain.length; i++)
		{
			xPoints[i] = i;
			terrain[i] = (int) (terrainY + ImprovedNoise.noise(offset, 0, offset) * scale);
			offset += 0.005;
		}
	}

	public static void setGravity(boolean g)
	{
		gravity = g ? gravityForce : 0;
	}

	public static int[] getTerrain()
	{
		return terrain;
	}

	public static void render(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.drawPolyline(xPoints, terrain, terrain.length);
	}
}
