package rl.jfx.actor;

import rl.jfx.PlayerViewport;
import rl.jfx.World;
import rl.jfx.World.Point;

public final class Player implements Occupant {
  private World.Point point;
  private Inventory inventory;

  public Player(World.Point spawnPoint) {
    point = spawnPoint;
  }

  @Override
  public void draw(PlayerViewport viewport) {
    viewport.putChar(viewport.transform(point), '@');
  }

  public Point at() {
    return this.point;
  }

  public void moveTo(World.Point point) {
    this.point = point;
  }
}