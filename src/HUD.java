import java.awt.*;
import java.awt.font.FontRenderContext;

public class HUD
{
	private final Ship ship;
	private final Font font;
	private long lastTime;
	private double timer;

	public HUD(Ship ship)
	{
		this.ship = ship;
		font = new Font("Yu Gothic Regular", Font.BOLD, 18);
		resetTimer();
	}

	public void resetTimer()
	{
		lastTime = 0;
		timer = 1;
	}

	public void render(Graphics g)
	{
		g.setColor(Color.white);
		g.setFont(font);

		if(Game.gameState == State.Game)
		{
			// Calculate player's time
			long now = System.nanoTime();
			timer += (now - (lastTime == 0 ? now : lastTime)) / 1000000000.0; // Edge case: 1st call calculates time
			// between calling the constructor and render method -> in this case difference should be 0
			lastTime = now;

			g.drawString(String.format("x: %.2f", ship.x), 10, 30);
			g.drawString(String.format("y: %.2f", ship.y), 100, 30);
			g.drawString(String.format("Angle: %.2f", ship.angle), 10, 70);
			g.drawString(String.format("velX: %.2f", ship.velX), 10, 95);
			g.drawString(String.format("velY: %.2f", ship.velY), 10, 120);
			g.drawString(String.format("Velocity: %.2f", ship.velocity), 10, 145);
			g.drawString("Gravity: " + Environment.gravity, 10, 170);
			g.drawString("Terrain delta: " + ship.terrainDelta, 10, 205);
			g.drawString(String.format("Deviation: %.2f", ship.deviation), 10, 230);
			g.drawString(String.format("Gravity push: %.2f", Math.max(1, Math.abs(ship.velY)) * Environment.gravity),
					10, 255);

			g.drawString(String.format("Time: %.2f", timer), Game.WIDTH - 150, 30);
		}
		else
		{
			String gameOverText = Game.gameState == State.Won ? "You won!" : "You lost!";
			String restartText = "Press R to restart.";
			String timerText = Game.gameState == State.Won ? String.format("Time: %.2f", timer) : "";

			FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
			Rectangle gameOverTextBounds = font.createGlyphVector(frc, gameOverText).getVisualBounds().getBounds();
			Rectangle restartTextBounds = font.createGlyphVector(frc, restartText).getVisualBounds().getBounds();
			Rectangle timerTextBounds = font.createGlyphVector(frc, timerText).getVisualBounds().getBounds();

			g.setColor(Game.gameState == State.Won ? Color.green : Color.red);
			g.drawString(gameOverText, Game.WIDTH / 2 - gameOverTextBounds.width / 2,
					Game.HEIGHT / 2 - gameOverTextBounds.height / 2);

			g.setColor(Color.white);
			g.drawString(restartText, Game.WIDTH / 2 - restartTextBounds.width / 2,
					Game.HEIGHT / 2 - restartTextBounds.height / 2 + 30);

			g.drawString(timerText, Game.WIDTH / 2 - timerTextBounds.width / 2,
					Game.HEIGHT / 2 - timerTextBounds.height / 2 + 60);
		}
	}
}
