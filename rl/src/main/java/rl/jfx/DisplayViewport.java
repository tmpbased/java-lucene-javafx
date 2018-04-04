package rl.jfx;

public interface DisplayViewport {
  static final class Point {
    final int x, y;

    public Point(final int x, final int y) {
      this.x = x;
      this.y = y;
    }
  }

  void init();

  Point transform(PlayerViewport.Point point);

  boolean contains(Point point);

  void clear();

  void putChar(Point point, char ch);
}