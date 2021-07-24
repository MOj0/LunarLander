import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class KeyInput extends KeyAdapter
{
	private Ship ship;
	private Game game;
	private SoundClip soundClipBoost, soundClipEnd;
	private final boolean[] pressedDirectional;

	public KeyInput(Ship ship, Game game, SoundClip soundClip, SoundClip soundClipEnd)
	{
		this.ship = ship;
		this.game = game;
		this.soundClipBoost = soundClip;
		this.soundClipEnd = soundClipEnd;
		pressedDirectional = new boolean[2]; // A, D
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();

		if(keyCode == 27) // Esc
		{
			System.exit(0);
			soundClipBoost.close();
			soundClipEnd.close();
		}
		else if(keyCode == 87) // W
		{
			ship.setAcceleration(true);
			soundClipBoost.loop();
		}
		else if(keyCode == 83) // S
		{
			ship.setReverse(true);
		}
		else if(keyCode == 65 || keyCode == 68) // A, D
		{
			boolean pressedA = keyCode == 65;
			ship.setSteer(pressedA ? -1 : 1);
			pressedDirectional[pressedA ? 0 : 1] = true;
		}
		else if(keyCode == 82) // R
		{
			game.restart();
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
			soundClipBoost.stop();
			soundClipEnd.playAndStop();
		}
		else if(keyCode == 83) // S
		{
			ship.setReverse(false);
		}
		else if(keyCode == 65 || keyCode == 68) // A, D
		{
			pressedDirectional[keyCode == 65 ? 0 : 1] = false;
		}

		// Reset steer only if no directional button is pressed
		if(!pressedDirectional[0] && !pressedDirectional[1])
		{
			ship.setSteer(0);
		}
	}
}
