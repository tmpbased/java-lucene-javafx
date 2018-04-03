package jfx;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lib.SearchRequest;
import lib.lucene.LuceneService;
import lib.receiver.ReceiverBuilder;
import lib.receiver.ReceiverThrottler;
import lib.receiver.ReplaceEventReceiver;

public class App extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  private LuceneService createLuceneService() {
    final LuceneService luceneService = new LuceneService(Paths.get("D:/db/"));
    final ExecutorService ex = Executors.newSingleThreadExecutor(r -> {
      final Thread thread = new Thread(r);
      thread.setName("Lucene-Service");
      thread.setDaemon(true);
      return thread;
    });
    ex.execute(luceneService);
    ex.shutdown();
    return luceneService;
  }

  private ReceiverThrottler createReceiverThrottler() {
    final ReceiverThrottler receiverThrottler = new ReceiverThrottler(Clock.systemDefaultZone());
    final ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);
    ex.setThreadFactory(r -> {
      final Thread thread = new Thread(r);
      thread.setName("Receiver-Throttler");
      thread.setDaemon(true);
      return thread;
    });
    ex.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
    ex.scheduleWithFixedDelay(receiverThrottler, 100, 100, TimeUnit.MILLISECONDS);
    ex.shutdown();
    return receiverThrottler;
  }

  @Override
  public void start(final Stage primaryStage) throws IOException {
    final LuceneService lucene = createLuceneService();
    final ReceiverThrottler throttler = createReceiverThrottler();
    final UiController controller = new UiController(new ReceiverBuilder()
      .mapCast(InitSearchRequest.class,
        new ReplaceEventReceiver<>(
          throttler.throttle(lucene, Duration.of(2, ChronoUnit.SECONDS)),
          event -> new SearchRequest(event.text, 0, event.howMany)
        ))
      .map(SearchRequest.class, lucene)
      .build());
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("app.fxml"));
    loader.setController(controller);
    final Parent root = loader.load();
    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }
}
