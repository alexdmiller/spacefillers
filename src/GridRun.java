import toxi.sim.automata.*;
import toxi.math.*;

import java.lang.reflect.WildcardType;

public class GridRun {
  private static final int WIDTH = 12;
  private static final int HEIGHT = 12;

  private Grid grid;
  private CAMatrix ca;
  private CAWolfram1D wolfram;

//  int y = 0;
//
//  public void setup() {
//    ca = new CAMatrix(WIDTH);
//    wolfram = new CAWolfram1D(1, 2, true).setRuleID(30);
//    ca.setRule(wolfram);
//    ca.setStateAt(WIDTH/2,0,1);
//    grid = new Grid(WIDTH, HEIGHT, 10);
//  }
//
//  public void draw() throws InterruptedException {
//    if (y >= grid.getHeight()) {
//      grid.clear();
//
//      int rule = (int) (Math.random() * 255);
//      wolfram.setRuleID(rule);
//      ca.setStateAt(WIDTH / 2,0,1);
//      y = 0;
//    }
//
//    ca.update();
//    int[] m = ca.getMatrix();
//    for (int x = 0; x < ca.getWidth(); x++) {
//      grid.setColor(x, y, Color.makeColor(m[x] * 100, m[x] * 100, m[x] * 100));
//    }
//
//    y++;
//    System.out.println(y);
//
//    grid.writePixels();
//  }

  public void setup() {
    ca = new CAMatrix(WIDTH, HEIGHT);

    byte[] birthRules=new byte[] { 3 };
    // survival rules specify the possible numbers of required
    // ACTIVE neighbour cells in order for a cell to stay alive
    byte[] survivalRules=new byte[] { 2,3 };
    CARule rule=new CARule2D(birthRules,survivalRules,2,true);
    // assign the rules to the CAMatrix
    ca.setRule(rule);
    ca.addNoise(0.5f, WIDTH, HEIGHT);

    grid = new Grid(WIDTH, HEIGHT, 10);
  }

  public void draw() throws InterruptedException {
    ca.update();
    grid.clear();
    int[] m = ca.getMatrix();
    for (int x = 0; x < ca.getWidth(); x++) {
      for (int y = 0; y < ca.getHeight(); y++) {
        System.out.println(x + ", " + y);
        int s = m[y * grid.getWidth() + x];
        grid.setColor(x, y, Color.makeColor(s * 100, s * 100, s * 100));
      }
    }

    grid.writePixels();
  }

  public static void main(String[] args) throws InterruptedException {
    GridRun gridRun = new GridRun();
    gridRun.setup();
    while (true) {
      gridRun.draw();
      Thread.sleep(50);
    }
  }
}
