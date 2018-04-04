package tilt.jfx.util;

import java.util.ArrayList;
import java.util.Collection;

import javafx.application.Platform;
import tilt.lib.receiver.Receiver;

public class PlatformReceiver implements Receiver {
  public static final class Subscribe {
  }

  public static final class RemoveSubscription {
  }

  private final Collection<Receiver> receivers;

  public PlatformReceiver() {
    this.receivers = new ArrayList<>();
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    Platform.runLater(() -> {
      if (event instanceof Subscribe) {
        this.receivers.add(source);
        return;
      }
      if (event instanceof RemoveSubscription) {
        this.receivers.remove(source);
        return;
      }
      for (final Receiver receiver : this.receivers) {
        receiver.onEvent(source, event);
      }
    });
  }
}
