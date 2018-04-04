package tilt.jfx;

import static javafx.beans.binding.Bindings.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import tilt.api.Request;
import tilt.api.search.*;
import tilt.jfx.util.FloorBinding;
import tilt.jfx.util.ToIntExactBinding;
import tilt.jfx.util.ToLongBinding;
import tilt.lib.receiver.RcvGeneric;
import tilt.lib.receiver.RcvGenericBinding;
import tilt.lib.receiver.RcvIncomingLogging;
import tilt.lib.receiver.RcvNamed;
import tilt.lib.receiver.RcvOutgoingLogging;
import tilt.lib.receiver.RcvStaticBindings;
import tilt.lib.receiver.Receiver;

public class UiController implements Initializable, Receiver {
  private final static Logger LOGGER = Logger.getLogger(UiController.class.getName());

  private final Receiver localReceiver, remoteReceiver;

  public UiController(final Receiver receiver) {
    this.remoteReceiver = new RcvOutgoingLogging(receiver, LOGGER);
    this.localReceiver = new RcvIncomingLogging(new RcvNamed("UI",
        new RcvStaticBindings()
            .add(new RcvGenericBinding(ApiRsSearch.class, RcvGeneric.consumeEvent(this::setToken)))
            .add(new RcvGenericBinding(ApiRsScrollUp.class,
                RcvGeneric.consumeEvent(this::displaySearchResult)))
            .add(new RcvGenericBinding(ApiRsScrollDown.class,
                RcvGeneric.consumeEvent(this::displaySearchResult)))
            .build()),
        LOGGER);
  }

  @FXML
  private TextField searchText;
  @FXML
  private ListView<String> docList;
  @FXML
  private Pane navigationPane, navigationPreviousPane, navigationNextPane;
  private ObservableIntegerValue pageSize;
  private BooleanProperty navigationInProgress, navigationFirstVisible, navigationLastVisible;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.pageSize = observePageSize();
    this.navigationInProgress = new SimpleBooleanProperty(false);
    this.navigationFirstVisible = new SimpleBooleanProperty(true);
    this.navigationLastVisible = new SimpleBooleanProperty(true);
    this.navigationPane.disableProperty().bind(this.navigationInProgress);
    this.navigationPreviousPane.disableProperty().bind(this.navigationFirstVisible);
    this.navigationNextPane.disableProperty().bind(this.navigationLastVisible);
    // this.searchText.textProperty().addListener((observable, oldValue, newValue) ->
    // navigateFirstPage());
    this.searchText.setOnKeyPressed(evt -> {
      switch (evt.getCode()) {
        case ENTER:
          navigateFirstPage();
          break;
        default:
          break;
      }
    });
    this.pageSize
        .addListener((observable, oldValue, newValue) -> refreshCurrentPage(oldValue.intValue()));
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    this.localReceiver.onEvent(source, event);
  }

  private ObservableIntegerValue observePageSize() {
    final ObservableDoubleValue height = this.docList.heightProperty(),
        fixedCellSize = this.docList.fixedCellSizeProperty();
    return toIntExact(toLong(max(floor(divide(height, fixedCellSize).subtract(.5)), 1)));
  }

  private static ObservableNumberValue floor(final ObservableNumberValue op) {
    return new FloorBinding(op);
  }

  private static ObservableLongValue toLong(final ObservableNumberValue op) {
    return new ToLongBinding(op);
  }

  private static ObservableIntegerValue toIntExact(final ObservableLongValue op) {
    return new ToIntExactBinding(op);
  }

  private enum Direction {
    UPWARD, DOWNWARD;
  }

  private Direction currentDirection = Direction.DOWNWARD;

  private void sendEvent(final Request request) {
    this.remoteReceiver.onEvent(new RcvNamed("UI", this), request);
  }

  private void refreshCurrentPage(final int oldPageSize) {
    if (this.navigationInProgress.get()) {
      return;
    } else if (this.token == null) {
      navigateFirstPage();
    } else {
      this.navigationInProgress.set(true);
      clearSearchResult();
      switch (this.currentDirection) {
        case DOWNWARD:
          sendEvent(new ApiRqSkipUp(this.token, oldPageSize));
          sendEvent(new ApiRqScrollDown(this.token, this.pageSize.get()));
          break;
        case UPWARD:
          sendEvent(new ApiRqSkipDown(this.token, oldPageSize));
          sendEvent(new ApiRqScrollUp(this.token, this.pageSize.get()));
          break;
      }
    }
  }

  @FXML
  public void navigateFirstPage() {
    if (this.navigationInProgress.get()) {
      return;
    } else {
      this.navigationInProgress.set(true);
      this.currentDirection = Direction.DOWNWARD;
      sendEvent(new ApiRqSearch(this.searchText.getText()));
    }
  }

  @FXML
  public void navigateLastPage() {
    if (this.navigationInProgress.get()) {
      return;
    } else {
      this.navigationInProgress.set(true);
      this.currentDirection = Direction.UPWARD;
      sendEvent(new ApiRqSearch(this.searchText.getText()));
    }
  }

  private void changeCurrentDirection(final Direction newDirection) {
    if (this.currentDirection != newDirection) {
      switch (newDirection) {
        case UPWARD:
          sendEvent(new ApiRqSkipUp(this.token, this.pageSize.get()));
          break;
        case DOWNWARD:
          sendEvent(new ApiRqSkipDown(this.token, this.pageSize.get()));
          break;
      }
      this.currentDirection = newDirection;
    }
  }

  @FXML
  public void navigatePreviousPage() {
    if (this.navigationInProgress.get()) {
      return;
    } else if (this.token == null) {
      navigateFirstPage();
    } else {
      this.navigationInProgress.set(true);
      changeCurrentDirection(Direction.UPWARD);
      sendEvent(new ApiRqScrollUp(this.token, this.pageSize.get()));
    }
  }

  @FXML
  public void navigateNextPage() {
    if (this.navigationInProgress.get()) {
      return;
    }
    if (this.token == null) {
      navigateLastPage();
    } else {
      this.navigationInProgress.set(true);
      changeCurrentDirection(Direction.DOWNWARD);
      sendEvent(new ApiRqScrollDown(this.token, this.pageSize.get()));
    }
  }

  private Notifications createNotification() {
    return Notifications.create().owner(this.searchText.getScene().getWindow())
        .hideAfter(Duration.seconds(2));
  }

  private ApiToken token;

  private void setToken(final ApiRsSearch response) {
    if (response.throwable != null) {
      clearSearchResult();
      createNotification().title("Error").text(response.throwable.getMessage()).showError();
      this.navigationInProgress.set(false);
      this.navigationFirstVisible.set(true);
      this.navigationLastVisible.set(true);
    } else {
      this.token = response.token;
      switch (this.currentDirection) {
        case UPWARD:
          sendEvent(new ApiRqScrollUp(this.token, this.pageSize.get()));
          break;
        case DOWNWARD:
          sendEvent(new ApiRqScrollDown(this.token, this.pageSize.get()));
          break;
      }
    }
  }

  private void displaySearchResult(final ApiRsScroll<?> response) {
    if (response.throwable != null) {
      clearSearchResult();
      createNotification().title("Error").text(response.throwable.getMessage()).showError();
    } else if (response.docs.docs.isEmpty()) {
      clearSearchResult();
      createNotification().title("Well...").text("No documents found").showWarning();
    } else {
      final List<String> newItems = new ArrayList<>(response.docs.docs.size());
      for (final ApiDoc doc : response.docs.docs) {
        newItems.add(doc.getRelevance() + " => " + doc.getPath());
      }
      final List<String> items = new ArrayList<>(this.docList.getItems().size() + newItems.size());
      switch (this.currentDirection) {
        case UPWARD:
          items.addAll(newItems);
          items.addAll(this.docList.getItems());
          this.docList.getItems()
              .setAll(items.subList(0, Math.min(items.size(), this.pageSize.get())));
          break;
        case DOWNWARD:
          items.addAll(this.docList.getItems());
          items.addAll(newItems);
          this.docList.getItems()
              .setAll(items.subList(Math.max(items.size() - this.pageSize.get(), 0), items.size()));
          break;
      }
    }
    this.navigationInProgress.set(false);
    this.navigationFirstVisible.set(!response.hasMoreUp);
    this.navigationLastVisible.set(!response.hasMoreDown);
  }

  private void clearSearchResult() {
    this.docList.getItems().clear();
  }
}
