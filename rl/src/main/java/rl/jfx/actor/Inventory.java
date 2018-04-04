package rl.jfx.actor;

import java.util.Collection;

public interface Inventory {
  interface Index {
  }

  interface Loot extends Actor {
  }

  Collection<Index> index();

  Index put(Loot loot);

  Loot take(Index index);
}
