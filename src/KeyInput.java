import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter
{
	private final Ship ship;
	private final boolean[] pressedLeftRight;

	public KeyInput(Ship ship)
	{
		this.ship = ship;
		pressedLeftRight = new boolean[2]; // A, D
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();

		if(keyCode == 27) // Esc
		{
			System.exit(0);
		}
		else if(keyCode == 87) // W
		{
			ship.setAcceleration(true);
		}
		else if(keyCode == 65 || keyCode == 68) // A, D
		{
			boolean pressedA = keyCode == 65;
			ship.setSteer(pressedA ? -1 : 1);
			pressedLeftRight[pressedA ? 0 : 1] = true;
		}
		else if(keyCode == 82) // R
		{
			ship.reset(Game.WIDTH / 2, Game.HEIGHT / 2); // Should be handled by Game class!
			Environment.createTerrainAndStars();
		}
		else if(keyCode == 71)
		{
			Environment.setGravity(Environment.gravity == 0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if(keyCode == 87) // W
		{
			ship.setAcceleration(false);
		}
		else if(keyCode == 65 || keyCode == 68) // A, D
		{
			pressedLeftRight[keyCode == 65 ? 0 : 1] = false;
		}

		// Reset steer only if no directional button is pressed -> less clunky movement
		if(!pressedLeftRight[0] && !pressedLeftRight[1])
		{
			ship.setSteer(0);
		}
	}
}
