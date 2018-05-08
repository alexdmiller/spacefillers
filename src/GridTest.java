public class GridTest {
  private static final int WIDTH = 12;
  private static final int HEIGHT = 12;

  private static Grid grid;

  public static void main(String[] args) throws InterruptedException {
    System.out.println("running grid test");
    grid = new Grid(12, 12, 12);

    grid.clear(0);
    grid.writePixels();
    Thread.sleep(200);
    grid.clear(0xFFFFFF);
    grid.writePixels();
    Thread.sleep(200);

    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        System.out.println(x + ", " + y);
        grid.clear();
        grid.setColor(x, y, Color.makeColor(255, 255, 255));
        grid.writePixels();
        Thread.sleep(200);
      }
    }
  }
}
