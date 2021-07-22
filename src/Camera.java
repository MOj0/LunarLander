public class Camera
{
	private Ship ship;
	private double x, y;
	private int windowWidth, windowHeight;
	private int maxX;

	public Camera(Ship ship, int width, int height)
	{
		this.ship = ship;
		windowWidth = width;
		windowHeight = height;
		x = ship.x + ship.width / 2 - width / 2;
		y = ship.y + ship.height / 2 - height / 2;
		maxX = (Environment.WIDTH_MULTIPLIER - 1) * Game.WIDTH;
	}

	public void tick()
	{
		// Tween the values
		x += (ship.x + ship.width / 2 - x - windowWidth / 2) * 0.05;
		x = clamp(x, 0, maxX);
		y += (ship.y + ship.height / 2 - y - windowHeight / 2) * 0.05;
	}

	private double clamp(double x, int min, int max)
	{
		return Math.max(min, Math.min(x, max));
	}

	public int getX()
	{
		return (int) x;
	}

	public int getY()
	{
		return (int) y;
	}
}
