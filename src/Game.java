import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable
{
	// TODO: Feel of the game: Vse se mora dogajat bolj pocasi! https://youtu.be/LrEvoKI07Ww?t=436
	public static final int WIDTH = 1024, HEIGHT = 768;
	private final Color backgroundColor = new Color(9, 9, 42);

	private KeyInput keyboard;
	private Ship ship; // Player
	private Camera camera;
	private HUD hud;

	private Thread thread;

	public static void main(String[] args)
	{
		new Game();
	}

	public Game()
	{
		new Window("Lunar Lander", WIDTH, HEIGHT, this);
		this.setFocusable(true);
		this.requestFocus();

		ship = new Ship(WIDTH / 2, HEIGHT / 5, 50, 46);
		camera = new Camera(ship, WIDTH, HEIGHT);
		hud = new HUD(ship);

		keyboard = new KeyInput(ship);
		this.addKeyListener(keyboard);

		thread = new Thread(this);
		thread.start();
	}

	private void tick()
	{
		camera.tick();
		ship.tick();
	}

	private void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null)
		{
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		Graphics2D g2d = (Graphics2D) g;

		g.setColor(backgroundColor);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g2d.translate(-camera.getX(), -camera.getY());

		ship.render(g);
		Environment.render(g);

		g2d.translate(camera.getX(), camera.getY()); // Reset camera position for the HUD
		hud.render(g);

		g.dispose();
		bs.show();
	}

	@Override
	public void run()
	{
		// Notch's Game Loop
		long lastTime = System.nanoTime(); // nanoseconds since start of program / start of JVM
		double targetFPS = 60;
		double nsPerTick = 1000000000 / targetFPS; // 10^9 (biillion) nanoseconds = 1 second -> divided by targetFPS
		// -> how many nanoseconds in one tick -> sets how many (min) ticks before rendering
		double delta = 0;

		// FPS Debug stuff
		long timer = System.currentTimeMillis(); // how many milliseconds since 1.1.1970
		int frames = 0;

		while(true)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			while(delta >= 1) // If a lot of time has passed, program has to tick(), rendering comes later
			{
				tick();
				delta--;
			}
			render();
			frames++;

//			if(System.currentTimeMillis() - timer > 1000) // If 1 second passed
//			{
//				timer += 1000; // Set timer to next second
//				System.out.println("FPS: " + frames);
//				frames = 0;
//			}

			// Yikes
			try
			{
				Thread.sleep(7);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
