import javax.swing.*;

public class Window extends JFrame
{
	public Window(String title, int width, int height, Game game)
	{
		new JFrame();
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(width, height);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		add(game);
	}
}
