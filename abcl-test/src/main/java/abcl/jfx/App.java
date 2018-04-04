package abcl.jfx;

import java.io.IOException;
import org.armedbear.lisp.Function;
import org.armedbear.lisp.Interpreter;
import org.armedbear.lisp.JavaObject;
import org.armedbear.lisp.LispObject;
import org.armedbear.lisp.Packages;
import org.armedbear.lisp.Symbol;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {
  @Override
  public void start(final Stage primaryStage) throws IOException {
    primaryStage.setTitle("Drawing Operations Test");
    Group root = new Group();
    Canvas canvas = new Canvas(300, 250);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(Color.BLUE);
    gc.fillRect(75,75,100,100);
    root.getChildren().add(canvas);
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }
  
  public int addTwoNumbers(int a, int b)
  {
    return a + b;
  }

  public static void main(String[] args) throws Exception {
    try
    {
      Interpreter interpreter = Interpreter.createInstance();
      interpreter.eval("(load \"abclrc\")");
      interpreter.eval("(load \"demo.lisp\")");
      LispObject myInterface = interpreter.eval("(lispfunction)");
      org.armedbear.lisp.Package defaultPackage = 
          Packages.findPackage("CL-USER");
      Symbol voidsym = 
          defaultPackage.findAccessibleSymbol("VOID-FUNCTION");
      Function voidFunction = (Function) voidsym.getSymbolFunction();
      voidFunction.execute(new JavaObject(new App()));
    } catch (Throwable t) {
      System.out.println("exception!");
      t.printStackTrace();
    }
    App.launch(args);
  }
}
