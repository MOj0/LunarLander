import java.awt.*;

public class Environment
{
	public static final double gravityForce = 0.1, maxGravityForce = 1;

	private int terrainY = 5 * Game.HEIGHT / 6; // TODO: Change later (Perlin noise)
	// TODO: Add stars, parralax effect

	public void render(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.drawLine(0, terrainY, Game.WIDTH, terrainY);
	}
}
