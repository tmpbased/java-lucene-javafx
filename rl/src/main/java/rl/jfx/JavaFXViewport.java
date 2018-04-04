package rl.jfx;

import javafx.scene.canvas.GraphicsContext;

final class JavaFXViewport implements DisplayViewport {
  private final GraphicsContext gc;
  int width, height;

  public JavaFXViewport(final GraphicsContext gc) {
    this.gc = gc;
    init();
  }

  @Override
  public void init() {
    width = (int) (gc.getCanvas().getWidth() / App.TILE_SIZE);
    height = (int) (gc.getCanvas().getHeight() / App.TILE_SIZE);
  }

  @Override
  public Point transform(PlayerViewport.Point point) {
    return new Point(width / 2 + point.x, height / 2 - point.y);
  }

  @Override
  public boolean contains(Point point) {
    return point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height;
  }

  @Override
  public void clear() {
    gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
  }

  @Override
  public void putChar(Point point, char ch) {
    gc.fillText(Character.toString(ch), point.x * App.TILE_SIZE, App.TILE_SIZE + point.y * App.TILE_SIZE);
  }
}