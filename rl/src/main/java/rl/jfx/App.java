package rl.jfx;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import rl.jfx.actor.Player;
import rl.jfx.actor.Wall;

public class App extends Application {
  static final double TILE_SIZE = 16;

  private final World world = new World();

  @Override
  public void start(final Stage primaryStage) throws IOException {
    primaryStage.setTitle("Drawing Operations Test");
    Group root = new Group();
    Canvas canvas = new Canvas(300, 250);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    final Font font = Font.loadFont(getClass().getResourceAsStream("square.ttf"), TILE_SIZE);
    gc.setFont(font);
    final DisplayViewport dv = new JavaFXViewport(gc);
    final Player pc = new Player(new World.Point(0, 0));
    this.world.actors.add(pc);
    this.world.actors.add(new Wall(pc.at().left(), pc.at().right()));
    final PlayerViewport pv = new PlayerViewport(pc, dv);
    root.getChildren().add(canvas);
    primaryStage.setScene(new Scene(root));
    primaryStage.getScene().setOnKeyPressed(event -> {
      switch (event.getCode()) {
      case END:
      case NUMPAD1:
        pc.moveTo(pc.at().down().left());
        break;
      case DOWN:
      case NUMPAD2:
        pc.moveTo(pc.at().down());
        break;
      case PAGE_DOWN:
      case NUMPAD3:
        pc.moveTo(pc.at().down().right());
        break;
      case LEFT:
      case NUMPAD4:
        pc.moveTo(pc.at().left());
        break;
      case RIGHT:
      case NUMPAD6:
        pc.moveTo(pc.at().right());
        break;
      case HOME:
      case NUMPAD7:
        pc.moveTo(pc.at().up().left());
        break;
      case UP:
      case NUMPAD8:
        pc.moveTo(pc.at().up());
        break;
      case PAGE_UP:
      case NUMPAD9:
        pc.moveTo(pc.at().up().right());
        break;
      }
      this.world.draw(pv);
    });
    primaryStage.getScene().widthProperty().addListener((observable, oldValue, newValue) -> {
      canvas.setWidth(newValue.doubleValue());
      dv.init();
      this.world.draw(pv);
    });
    primaryStage.getScene().heightProperty().addListener((observable, oldValue, newValue) -> {
      canvas.setHeight(newValue.doubleValue());
      dv.init();
      this.world.draw(pv);
    });
    primaryStage.show();
    this.world.draw(pv);
  }

  public static void main(String[] args) throws Exception {
    App.launch(args);
  }
}
