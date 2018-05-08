import java.io.*;
import java.net.URL;
import java.util.*;
import javax.sound.sampled.*;

public class SoundManager
{
  private javax.sound.sampled.Line.Info lineInfo;

  private Vector afs;
  private Vector sizes;
  private Vector infos;
  private Vector audios;
  private int num=0;

  public SoundManager()
  {
    afs=new Vector();
    sizes=new Vector();
    infos=new Vector();
    audios=new Vector();
  }

  public void addClip(String s)
      throws IOException, UnsupportedAudioFileException, LineUnavailableException
  {
    URL url = getClass().getResource(s);
    //InputStream inputstream = url.openStream();
    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(s));
    AudioFormat af = audioInputStream.getFormat();
    int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength());
    byte[] audio = new byte[size];
    DataLine.Info info = new DataLine.Info(Clip.class, af, size);
    audioInputStream.read(audio, 0, size);

    afs.add(af);
    sizes.add(new Integer(size));
    infos.add(info);
    audios.add(audio);

    num++;


  }

  private ByteArrayInputStream loadStream(InputStream inputstream)
      throws IOException
  {
    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
    byte data[] = new byte[1024];
    for(int i = inputstream.read(data); i != -1; i = inputstream.read(data))
      bytearrayoutputstream.write(data, 0, i);

    inputstream.close();
    bytearrayoutputstream.close();
    data = bytearrayoutputstream.toByteArray();
    return new ByteArrayInputStream(data);
  }

  public void playSound(int x)
      throws UnsupportedAudioFileException, LineUnavailableException
  {
    if(x>num)
    {
      System.out.println("playSound: sample nr["+x+"] is not available");
    }
    else
    {
      Clip clip = (Clip) AudioSystem.getLine((DataLine.Info)infos.elementAt(x));
      clip.open((AudioFormat)afs.elementAt(x), (byte[])audios.elementAt(x), 0, ((Integer)sizes.elementAt(x)).intValue());
      clip.start();
    }
  }
}