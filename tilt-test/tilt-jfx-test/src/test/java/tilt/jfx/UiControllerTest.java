package tilt.jfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import tilt.api.search.ApiRqSearch;
import tilt.api.search.ApiRsSearch;
import tilt.lib.receiver.RcvDynamicBindings;
import tilt.lib.receiver.RcvGeneric;
import tilt.lib.receiver.RcvGenericBinding;
import tilt.lib.receiver.Receiver;

public class UiControllerTest extends ApplicationTest {
  private static final boolean HEADLESS = true;
  private RcvDynamicBindings bindings;

  static {
    if (HEADLESS) {
      System.setProperty("java.awt.headless", "true");
      System.setProperty("testfx.robot", "glass");
      System.setProperty("glass.platform", "Monocle");
      System.setProperty("monocle.platform", "Headless");
      System.setProperty("headless.geometry", "1920x1080-32");
      System.setProperty("prism.order", "sw");
      System.setProperty("prism.text", "t2k");
      System.setProperty("testfx.setup.timeout", "2500");
    }
  }

  @Override
  public void start(Stage stage) throws Exception {
    final UiController controller = new UiController(this.bindings = new RcvDynamicBindings());
    this.bindings.add(new RcvGenericBinding(ApiRqSearch.class, RcvGeneric.consumeEvent(e -> {
      controller.onEvent(Receiver.none(), new ApiRsSearch(e, new IOException()));
    })));
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/app.fxml"));
    loader.setController(controller);
    final Parent root = loader.load();
    stage.setTitle("Hello World!");
    stage.setScene(new Scene(root));
    stage.show();
  }

  @Test
  void myFirstTest() throws Exception {
    boolean[] wasRequest = new boolean[] {false};
    this.bindings.add(new RcvGenericBinding(ApiRqSearch.class, RcvGeneric.consumeEvent(e -> {
      if (e.text.length() < 4) {
        return;
      }
      wasRequest[0] = true;
      assertEquals("test", e.text);
    })));
    clickOn("#searchText").write("test").push(KeyCode.ENTER);
    verifyThat("#searchText", TextInputControlMatchers.hasText("test"));
    assertTrue(wasRequest[0]);
  }
}
