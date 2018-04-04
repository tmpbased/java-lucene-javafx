package mpw.jfx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mpw.api.Algorithm;
import mpw.impl.AlgorithmV1;

public class App extends Application {
  @Override
  public void start(final Stage primaryStage) throws IOException {
    final Algorithm algorithm = new AlgorithmV1();
    final UiController controller = new UiController(algorithm);
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/app.fxml"));
    loader.setController(controller);
    final Parent root = loader.load();
    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }

  public static void main(String[] args) throws Exception {
    App.launch(args);
  }
}
