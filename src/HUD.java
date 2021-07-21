import java.awt.*;

public class HUD
{
	private final Ship ship;
	private final Font font;

	public HUD(Ship ship)
	{
		this.ship = ship;
		font = new Font("Yu Gothic Regular", Font.BOLD, 18);
	}

	public void render(Graphics g)
	{
		g.setColor(Color.white);
		g.setFont(font);

		g.drawString(String.format("x: %.2f", ship.x), 10, 30);
		g.drawString(String.format("y: %.2f", ship.y), 100, 30);
		g.drawString(String.format("Angle: %.2f", ship.angle), 10, 70);
		g.drawString(String.format("velX: %.2f", ship.velX), 10, 95);
		g.drawString(String.format("velY: %.2f", ship.velY), 10, 120);
		g.drawString(String.format("Speed: %.2f", ship.velocity), 10, 145);
		g.drawString("Gravity: " +  Environment.gravity, 10, 170);
	}
}
