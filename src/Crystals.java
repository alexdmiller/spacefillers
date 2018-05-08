import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Crystals {
  public static final int NUM_SOUNDS = 36;
  public static final int[][] CHORDS = new int[][] { {0, 3, 7, 10}, {0, 3, 5, 8} };
  public static final int NUM_CRYSTALS = 4;
  public static final int PIXELS_PER_CRYSTAL = 12;

  private static int[] pulses = new int[NUM_SOUNDS];

  private static int chordIndex = 1;
  private static long updateSpeed = 100;
  private static int cadence = 1;
  private static int noteCountdown = 0;
  private static int chordCadence = 20;
  private static int chordCountdown = 0;
  private static int t = 0;
  private static SoundManager soundManager;

  public static void main(String[] args) throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
    soundManager = new SoundManager();
    for (int i = 0; i < NUM_SOUNDS; i++) {
      soundManager.addClip("sounds/" + (i + 1) + ".wav");
    }

    Fadecandy fadecandy = new Fadecandy("localhost", 7890, PIXELS_PER_CRYSTAL * NUM_CRYSTALS);
    fadecandy.setInterpolation(true);
    fadecandy.setPixelColor(0, 0xFFFFFF);
    fadecandy.show();

    while (true) {
      Thread.sleep(updateSpeed);

      if (chordCountdown == chordCadence) {
        chordCountdown = 0;
        chordIndex = (int) Math.floor(Math.random() * CHORDS.length);
      }

      if (Math.random() < 0.3) {
        noteCountdown = 0;
        cadence = (int) ((Math.sin((float) t / 100) + 1) / 2 * 15 + 4);

        int soundIndex = playRandomSound();
        int crystalIndex = soundIndex % NUM_CRYSTALS;

        for (int i = 0; i < PIXELS_PER_CRYSTAL; i++) {
          fadecandy.setPixelColor(i + crystalIndex * PIXELS_PER_CRYSTAL, 0xFFFFFF);
        }
      }

      fadecandy.dimPixels(40);
      fadecandy.show();

      noteCountdown++;
      chordCountdown++;
      t++;
//    }

//    while (true) {
//      double waitTime = Math.random() * 30  00;
//      Thread.sleep((long) waitTime);
//      playRandomSound();
//    }
    }
  }

  private static int playRandomSound() throws LineUnavailableException, UnsupportedAudioFileException {
    int[] currentChord = CHORDS[chordIndex];
    int soundIndex = currentChord[(int) Math.floor(Math.random() * currentChord.length)];
    soundIndex += Math.floor(Math.random() * 3) * 12;
    soundManager.playSound(soundIndex);

    return soundIndex;
  }
}