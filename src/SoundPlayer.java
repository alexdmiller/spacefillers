import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer implements Runnable {
  private String filename;

  public SoundPlayer(String filename) {
    this.filename = filename;
  }

  @Override
  public void run() {
    AudioInputStream audioIn;
    try {
      audioIn = AudioSystem.getAudioInputStream(new File(filename));
      Clip clip = AudioSystem.getClip();
      clip.open(audioIn);
      clip.start();
      Thread.sleep(clip.getMicrosecondLength()/1000);
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException  e1) {
      e1.printStackTrace();
    }
  }
}
