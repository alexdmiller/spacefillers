import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Fadecandy implements AutoCloseable {

  public static final int BLACK = 0x000000;

  private Socket socket;
  private OutputStream output;
  private final String host;
  private final int port;
  private final int channel = 0;
  private byte firmwareConfig = 0;
  private int numPixels = 0;
  private boolean initialized = false;
  private byte[] packetData;
  private boolean verbose = true;

  protected boolean interpolation = false;

  protected boolean dithering = true;

  /**
   * Construct a new OPC Client.
   *
   * @param hostname Host name or IP address.
   * @param portNumber Port number
   */
  public Fadecandy(String hostname, int portNumber, int numPixels) {
    this.host = hostname;
    this.port = portNumber;
    this.firmwareConfig |= 0x02;
    this.numPixels = numPixels;
  }

  @Override
  public void close() {
    try {
      if (this.output != null) {
        this.output.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      this.output = null;
    }

    try {
      if (this.socket != null && (!this.socket.isClosed())) {
        this.socket.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      this.socket = null;
    }
  }

  /**
   * Retrieve a pixel color within the global pixel map of this client.
   *
   * @param number absolute number of the pixel within the server.
   * @return color represented as an integer.
   */
  protected int getPixelColor(int number) {
    if (!initialized) {
      init();
    }
    int offset = 4 + number * 3;
    return (packetData[offset] << 16) | (packetData[offset + 1] << 8)
        | packetData[offset + 2];
  }

  /**
   * Reset the packet data buffer.
   */
  protected void init() {
    System.out.println("initializing data buffer");
    if (!initialized) {
      int numBytes = 3 * this.numPixels;
      int packetLen = 4 + numBytes;
      packetData = new byte[packetLen];
      packetData[0] = (byte) this.channel;
      packetData[1] = 0; // Command (Set pixel colors)
      packetData[2] = (byte) (numBytes >> 8);
      packetData[3] = (byte) (numBytes & 0xFF);
    }
    initialized = true;
  }

  /**
   * Print a message out to the console.
   */
  protected void log(String msg, Exception e) {
    if (! verbose) { return; }
    System.out.println(msg);
    if (e != null) {
      e.printStackTrace();
    }
  }

  /**
   * Open a socket connection to the Fadecandy server.
   */
  protected void open() {
    if (this.output == null) {
      try {
        System.out.println("opening connection to " + this.host + ":" + this.port);
        socket = new Socket(this.host, this.port);
        socket.setTcpNoDelay(true);
        output = socket.getOutputStream();
        sendFirmwareConfigPacket();
      } catch (Exception e) {
        log("open: error: " + e, e);
        this.close();
      }
    }
  }

  /**
   * Send a control message to the Fadecandy server setting up proper
   * interpolation and dithering.
   */
  protected void sendFirmwareConfigPacket() {
    if (output == null) {
      log("sendFirmwareConfigPacket: no socket", null);
      return;
    }

    byte[] packet = new byte[9];
    packet[0] = (byte) this.channel; // Channel (reserved)
    packet[1] = (byte) 0xFF; // Command (System Exclusive)
    packet[2] = 0; // Length high byte
    packet[3] = 5; // Length low byte
    packet[4] = 0x00; // System ID high byte
    packet[5] = 0x01; // System ID low byte
    packet[6] = 0x00; // Command ID high byte
    packet[7] = 0x02; // Command ID low byte
    packet[8] = firmwareConfig;

    writePixels(packet);
  }

  /**
   * Turn on/off the temporal dithering.
   *
   * @param enabled whether to do temporal dithering.
   */
  public void setDithering(boolean enabled) {
    this.dithering = enabled;
    if (enabled) {
      firmwareConfig &= ~0x01;
    } else {
      firmwareConfig |= 0x01;
    }
    sendFirmwareConfigPacket();
  }

  /**
   * Turn on/off the inter-frame. If this is turned off, pixels will respond
   * instantly.
   *
   * @param enabled whether to interpolate.
   */
  public void setInterpolation(boolean enabled) {
    this.interpolation = enabled;
    if (enabled) {
      firmwareConfig &= ~0x02;
    } else {
      firmwareConfig |= 0x02;
    }
    sendFirmwareConfigPacket();
  }

  /**
   * Set a pixel color within the global pixel map of this client.
   *
   * @param opcPixel number of the pixel within the server.
   * @param color color represented as an integer.
   */
  protected void setPixelColor(int opcPixel, int color) {
    if (!initialized) {
      init();
    }
    int offset = 4 + opcPixel * 3;
    packetData[offset] = (byte) (color >> 16);
    packetData[offset + 1] = (byte) (color >> 8);
    packetData[offset + 2] = (byte) color;
  }

  public int getNumPixels() {
    return numPixels;
  }

  /**
   * @param b Whether to do verbose logging.
   */
  public void setVerbose(boolean b) {
    this.verbose = b;
  }

  /**
   * Push all pixel changes to the strip.
   */
  public void show() {
    if (!initialized) {
      init();
    }
    if (this.output == null) {
      this.open();
    }
    writePixels(packetData);
  }

  @Override
  public String toString() {
    return "OpcClient(" + this.host + "," + this.port + ")";
  }

  public void dimPixels(int amount) {
    for (int i = 0; i < numPixels; i++) {
      int offset = 4 + i * 3;
      packetData[offset] = (byte) Math.max((packetData[offset] & 0xFF) - amount, 0);
      packetData[offset + 1] = (byte) Math.max((packetData[offset] & 0xFF) - amount, 0);
      packetData[offset + 2] = (byte) Math.max((packetData[offset] & 0xFF) - amount, 0);
    }
  }

  /**
   * Push a pixel buffer out the socket to the Fadecandy.
   */
  protected void writePixels(byte[] packetData) {
    if (packetData == null || packetData.length == 0) {
      log("writePixels: no packet data", null);
      return;
    }
    if (output == null) {
      open();
    }
    if (output == null) {
      log("writePixels: no socket", null);
      return;
    }

    try {
      output.write(packetData);
      output.flush();
    } catch (Exception e) {
      log("writePixels: error : " + e, e);
      close();
    }
  }

  /**
   * Input a value 0 to 255 to get a color value.
   * The colors are a transition r - g - b - back to r.
   */
  private static int colorWheel(int c) {
    byte n = (byte)c;
    if (n < 85)  {
      return Color.makeColor(n*3, 255 - n*3, 0);
    } else if (n < 170) {
      return Color.makeColor(255 - n*3, 0, n*3);
    } else {
      return Color.makeColor(0, n*3, 255 - n*3);
    }
  }
}
