package rl.jfx;

import java.util.*;

import rl.jfx.actor.Actor;
import rl.jfx.actor.Occupant;
import rl.jfx.actor.Inventory;
import rl.jfx.actor.Terrain;

public final class World {
  public static final class Point {
    public final int x, y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public Point up() {
      return new Point(x, y + 1);
    }

    public Point down() {
      return new Point(x, y - 1);
    }

    public Point right() {
      return new Point(x + 1, y);
    }

    public Point left() {
      return new Point(x - 1, y);
    }

    @Override
    public int hashCode() {
      return Objects.hash(World.class, x, y);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Point) {
        final Point o = (Point) obj;
        return x == o.x && y == o.y;
      }
      return false;
    }
  }

  private static final class Cell {
    private Occupant occupant;
    private Inventory inventory;
    private Terrain terrain;
  }

  private final Map<Point, Cell> cells = new HashMap<>();
  final Collection<Actor> actors = new ArrayList<>();

  public void draw(final PlayerViewport viewport) {
    viewport.viewport.clear();
    for (final Actor actor : this.actors) {
      actor.draw(viewport);
    }
  }
}