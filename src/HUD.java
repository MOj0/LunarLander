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

		g.drawString("x: " + ship.x, 10, 30);
		g.drawString("y: " + ship.y, 80, 30);
		g.drawString(String.format("velX: %.2f", ship.velX), 10, 80);
		g.drawString(String.format("velY: %.2f", ship.velY), 10, 110);
	}
}
