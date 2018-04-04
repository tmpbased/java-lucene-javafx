package tilt.jfx;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tilt.api.ApiRqOpenIndex;
import tilt.api.Tilt;
import tilt.api.TiltService;
import tilt.jfx.util.PlatformReceiver;
import tilt.lib.receiver.RcvBlockingQueue;
import tilt.lib.receiver.RcvChannel;
import tilt.lib.receiver.Receiver;
import tilt.lib.util.Closeables;

public class App extends Application {
  public static void main(String[] args) {
    launch(App.class, args);
  }
  
  private final Closeables closeables;

  public App() {
    this.closeables = new Closeables();
  }

  private CompletableFuture<Void> runAsync(final String name, final RcvBlockingQueue action) {
    final ExecutorService ex = Executors.newSingleThreadExecutor(r -> {
      final Thread thread = new Thread(r);
      thread.setName(name);
      return thread;
    });
    final CompletableFuture<Void> future = CompletableFuture.runAsync(action, ex);
    ex.shutdown();
    return future;
  }

  @Override
  public void start(final Stage primaryStage) throws IOException {
    final RcvBlockingQueue queue = new RcvBlockingQueue(new LinkedBlockingQueue<>());
    final PlatformReceiver jfx = new PlatformReceiver();
    final Tilt tilt = ServiceLoader.load(TiltService.class)
        .stream().map(ServiceLoader.Provider::get).iterator().next()
        .tilt(new RcvChannel(queue, jfx));
    queue.onEvent(tilt, new RcvBlockingQueue.Subscribe());
    final UiController controller = new UiController(new RcvChannel(jfx, queue));
    jfx.onEvent(controller, new PlatformReceiver.Subscribe());
    runAsync("Tilt", queue).whenComplete((ok, err) -> tilt.close());
    this.closeables.add(() -> queue.onEvent(Receiver.none(), new RcvBlockingQueue.Close()));
    queue.onEvent(Receiver.none(), new ApiRqOpenIndex(Paths.get("D:/db/")));
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/app.fxml"));
    loader.setController(controller);
    final Parent root = loader.load();
    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    this.closeables.close();
    super.stop();
  }
}
