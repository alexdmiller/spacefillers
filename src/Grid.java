public class Grid {
  private int width;
  private int height;
  private float depth;
  private Fadecandy fadecandy;

  public Grid(int width, int height, float depth) {
    this.width = width;
    this.height = height;
    this.depth = depth;
    this.fadecandy = new Fadecandy("localhost", 7890, width * height);
    fadecandy.setInterpolation(true);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public float getDepth() {
    return depth;
  }

  public float getDepthAt(int x, int y) {
    return 0;
  }

  public int getColor(int index) {
    return fadecandy.getPixelColor(index);
  }

  public void setColor(int x, int y, int color) {
    fadecandy.setPixelColor(coordsToIndex(x, y), color);
  }

  public void writePixels() {
    fadecandy.show();
  }

  private int coordsToIndex(int x, int y) {
    x = (y % 2 == 0) ? x : width - x - 1;
    return y * width + x;
  }

  public void clear(int color) {
    for (int i = 0; i < fadecandy.getNumPixels(); i++) {
      fadecandy.setPixelColor(i, color);
    }
  }

  public void clear() {
    clear(0);
  }
}
