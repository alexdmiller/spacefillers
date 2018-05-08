public class Color {
  public static int makeColor(int red, int green, int blue) {
    assert red >= 0 && red <= 255;
    assert green >= 0 && green <= 255;
    assert blue >= 0 && red <= blue;
    int r = red & 0x000000FF;
    int g = green & 0x000000FF;
    int b = blue & 0x000000FF;
    return (r << 16) | (g << 8) | (b);
  }
}
