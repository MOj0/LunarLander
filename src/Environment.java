import java.awt.*;
import java.util.Random;

public class Environment
{
	public static final double gravityForce = 0.05;
	public static final double maxGravityForce = 2;
	public static final double drag = 0.01;
	public static final int WIDTH_MULTIPLIER = 4;
	private static final int terrainY = 5 * Game.HEIGHT / 6;
	private static final int starsPerWidth = Game.WIDTH / 12;
	public static double gravity;

	private static int[] terrain, xPoints;
	private static Random r;
	private static double offset;
	private static int[][] stars;

	static
	{
		terrain = new int[WIDTH_MULTIPLIER * Game.WIDTH];
		xPoints = new int[terrain.length];
		r = new Random();
		gravity = gravityForce;
		stars = new int[WIDTH_MULTIPLIER * starsPerWidth][3]; // [n_stars],[x, y, size]

		createTerrainAndStars();
	}

	public static void createTerrainAndStars()
	{
		offset = r.nextInt(10000);
		int scale = r.nextInt(25) + 375;

		for(int i = 0; i < terrain.length; i++)
		{
			xPoints[i] = i;
			terrain[i] = (int) (terrainY + ImprovedNoise.noise(offset, 0, offset) * scale);
			offset += 0.005;
		}

		for(int i = 0; i < WIDTH_MULTIPLIER; i++)
		{
			int currentStarsWidth = 0;
			while(currentStarsWidth < starsPerWidth)
			{
				int randX = r.nextInt(Game.WIDTH) + i * Game.WIDTH;
				// 33% to be spawned way up, else spawned in the "play" area
				int randY = r.nextDouble() <= 0.33 ? r.nextInt(4 * Game.HEIGHT) - 5 * Game.HEIGHT :
						r.nextInt(2 * terrain[randX] - 100) - terrain[randX];
				stars[currentStarsWidth + i * starsPerWidth][0] = randX;
				stars[currentStarsWidth + i * starsPerWidth][1] = randY;
				stars[currentStarsWidth + i * starsPerWidth][2] = r.nextInt(4) + 3;
				currentStarsWidth++;
			}
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

		for(int i = 0; i < WIDTH_MULTIPLIER * starsPerWidth; i++)
		{
//			g.fillOval(stars[i][0], stars[i][1], r.nextInt(2) + 4, r.nextInt(2) + 4); // TRIPPY
			g.fillOval(stars[i][0], stars[i][1], stars[i][2], stars[i][2]);
		}
	}
}
