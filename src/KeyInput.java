import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter
{
	private Ship ship;

	public KeyInput(Ship ship)
	{
		this.ship = ship;
	}


	@Override
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if(keyCode == 87) // W
		{
			ship.accelerate();
		}
		if(keyCode == 65 || keyCode == 68) // A, D
		{
			ship.steer(keyCode == 65 ? -1 : 1);
		}
//		if(keyCode == 83) // S
//		{
//
//		}
		if(keyCode == 27) // Esc
		{
			System.exit(0);
		}
	}
}
