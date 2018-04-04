package rl.jfx;

import rl.jfx.actor.Player;

public final class PlayerViewport {
  public static final class Point {
    final int x, y;

    public Point(final int x, final int y) {
      this.x = x;
      this.y = y;
    }
  }

  Player player;
  DisplayViewport viewport;

  public PlayerViewport(Player player, DisplayViewport viewport) {
    this.player = player;
    this.viewport = viewport;
  }

  public PlayerViewport.Point transform(World.Point point) {
    return new Point(point.x - player.at().x, point.y - player.at().y);
  }

  public boolean contains(PlayerViewport.Point point) {
    return viewport.contains(viewport.transform(point));
  }

  public void putChar(PlayerViewport.Point point, char ch) {
    viewport.putChar(viewport.transform(point), ch);
  }
}