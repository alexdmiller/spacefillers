import processing.core.PApplet;
import processing.opengl.PJOGL;

public class SpacefillersTitle extends PApplet {
  public static void main(String[] args) {
    PApplet.main("SpacefillersTitle");
  }

  public void settings() {
    fullScreen();
    PJOGL.profile = 1;
  }

  public void setup() {
  }

  public void draw() {
    background(0);

    fill(255);
    rect(100, 100, 300, 300);
  }
}
