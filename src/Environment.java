import java.awt.*;

public class Environment
{
	public static final double gravityForce = 0.05, maxGravityForce = 1.5, drag = 0.01;

	private int[] terrain;
	private int[] xPoints;
	// TODO: Add stars -> when moving to the left or right, "new" stars appear

	public Environment()
	{
		// TODO: IMPLEMENT PERLIN NOISE!!!!!!
		terrain = new int[Game.WIDTH];
		double factorE = -1.2;
		double factorPi = 1.9;
		double factor1 = -3.2;
		double factorTotal = 10;
		double scaleE = -1.7;
		double scalePi = 0.7;
		double scale1 = -1.3;

		int nPointsCalculated = Game.WIDTH / 16;
		int initialTerrainY = 5 * Game.HEIGHT / 6;
		for(int i = 0; i < terrain.length; i += nPointsCalculated)
		{
			terrain[i] =
					(int) (initialTerrainY + factorTotal * (factor1 * Math.sin(scale1 * i) + factorE * Math.sin(scaleE * i) + factorPi * Math.sin(scalePi + Math.PI * i)));
		}

		for(int i = 0; i < terrain.length - nPointsCalculated; i++)
		{
			if(terrain[i] != 0)
			{
				continue;
			}
			int indexOfTerrainPoint = i / nPointsCalculated;
			double prevTerrainPoint = terrain[indexOfTerrainPoint++ * nPointsCalculated];
			double nextTerrainPoint = terrain[indexOfTerrainPoint * nPointsCalculated];
			double yDiff = nextTerrainPoint - prevTerrainPoint;
			double currentIndex = (double) i % nPointsCalculated / nPointsCalculated;
			terrain[i] = initialTerrainY + (int) (smoothstep(currentIndex) * yDiff);
		}

		xPoints = new int[Game.WIDTH];
		for(int i = 0; i < xPoints.length; i++)
		{
			xPoints[i] = i;
		}
	}

	private double smoothstep(double x)
	{
		// 3x^2 - 2x^3, for x element [0, 1]
		return x * x * (3 - 2 * x);
	}

	private double clamp(double x, double min, double max)
	{
		return Math.max(min, Math.min(max, x));
	}

	public void render(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.drawPolyline(xPoints, terrain, Game.WIDTH);
	}
}
