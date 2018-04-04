package rl.jfx.actor;

import rl.jfx.PlayerViewport;
import rl.jfx.World;

public final class Wall implements Occupant, Terrain {
  private World.Point[] points;

  public Wall(World.Point... spawnPoints) {
    points = spawnPoints;
  }

  @Override
  public void draw(PlayerViewport viewport) {
    for (final World.Point point : points) {
      viewport.putChar(viewport.transform(point), '#');
    }
  }
}