import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundClip
{
	// TODO: Fix clicking sound
	private Clip clip = null;
	private FloatControl gainControl; // Operates in decibels (not floats)!
	private long endMicroseconds;
	private boolean enabled;

	public SoundClip(String path, boolean isEnd, float defaultVolume)
	{
		try
		{
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
			AudioFormat baseFormat = ais.getFormat();

			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(),
					16,
					baseFormat.getChannels(),
					baseFormat.getChannels() * 2,
					baseFormat.getSampleRate(),
					false);

			AudioInputStream dAis = AudioSystem.getAudioInputStream(decodeFormat, ais);

			clip = AudioSystem.getClip();
			clip.open(dAis);

			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			changeVolume(defaultVolume);
			enabled = true;

			if(isEnd) // Add LineListener to stop automatically
			{
				endMicroseconds = 8 * clip.getMicrosecondLength() / 10;

				LineListener listener = event ->
				{
					if(event.getType() == LineEvent.Type.STOP)
					{
						clip.stop();
					}
				};
				clip.addLineListener(listener);
			}
		}
		catch(UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void loop()
	{
		if(enabled)
		{
			// KeyInput stops the loop
			clip.setLoopPoints(0, 90000); // 114727 - max
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	public void playAndStop()
	{
		if(enabled)
		{
			// Stops automatically because of LineListener
			clip.setMicrosecondPosition(endMicroseconds);
			clip.start();
		}
	}

	public void stop()
	{
		if(clip.isRunning())
		{
			clip.stop();
		}
	}

	public void close()
	{
		stop();
		clip.drain();
		clip.close();
	}

	public void changeVolume(float decibels)
	{
		gainControl.setValue(decibels);
	}
}
