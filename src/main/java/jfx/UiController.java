package jfx;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lib.FoundDoc;
import lib.SearchRequest;
import lib.SearchResponse;
import lib.receiver.Receiver;
import lib.receiver.ReceiverBuilder;

public class UiController implements Initializable, Receiver {
  private final Receiver localReceiver, remoteReceiver;

  public UiController(final Receiver receiver) {
    this.remoteReceiver = receiver;
    this.localReceiver = new ReceiverBuilder()
      .mapCast(SearchResponse.class, (source, event) -> Platform.runLater(() -> displaySearchResult(event)))
      .build();
  }

  @FXML
  private TextField searchText;
  @FXML
  private ListView<String> foundDocs;
  @FXML
  private Pagination pagination;
  private IntegerProperty pageSize, totalHits;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.totalHits = new SimpleIntegerProperty(0);
    this.totalHits.addListener((observable, oldValue, newValue) -> repaginate());
    this.pageSize = new SimpleIntegerProperty(calculatePageSize());
    this.pageSize.addListener((observable, oldValue, newValue) -> repaginate());
    this.pagination.visibleProperty().bind(this.totalHits.greaterThan(this.pageSize));
    this.pagination.managedProperty().bind(this.totalHits.greaterThan(this.pageSize));
    this.pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> searchRequest(false));
    this.searchText.textProperty().addListener((observable, oldValue, newValue) -> searchRequest(true));
    this.foundDocs.fixedCellSizeProperty().addListener((observable, oldValue, newValue) -> updatePageSize());
    this.foundDocs.heightProperty().addListener((observable, oldValue, newValue) -> updatePageSize());
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    this.localReceiver.onEvent(source, event);
  }

  private int calculatePageSize() {
    if (this.foundDocs.getFixedCellSize() > 0) {
      return Math.max((int) Math.floor(this.foundDocs.getHeight() / this.foundDocs.getFixedCellSize() - .5), 1);
    } else {
      return 25;
    }
  }

  private void updatePageSize() {
    final int newPageSize = calculatePageSize();
    if (this.pageSize.get() != newPageSize) {
      this.pageSize.set(newPageSize);
      searchRequest(false);
    }
  }

  private void searchRequest(final boolean init) {
    final String text = this.searchText.getText();
    if (text.isEmpty()) {
      clearSearchResult();
    } else if (this.totalHits.get() == 0 && text.equals(this.appliedSearchText)) {
      // FIXME updatePageSize
    } else {
      final int pageSize = this.pageSize.get();
      if (init) {
        this.remoteReceiver.onEvent(this, new InitSearchRequest(text, pageSize));
      } else {
        this.remoteReceiver.onEvent(this, new SearchRequest(text, pageSize * this.pagination.getCurrentPageIndex(), pageSize));
      }
    }
  }

  private Notifications createNotification() {
    return Notifications.create()
      .owner(this.searchText.getScene().getWindow())
      .hideAfter(Duration.seconds(2));
  }

  private String appliedSearchText = "";

  private void displaySearchResult(final SearchResponse response) {
    this.foundDocs.getItems().clear();
    for (final FoundDoc foundDoc : response.foundDocs) {
      this.foundDocs.getItems().add(foundDoc.path);
    }
    if (response.throwable != null) {
      createNotification()
        .title("Error")
        .text(response.throwable.getMessage())
        .showError();
    } else if (!Objects.equals(this.appliedSearchText, response.request.text)) {
      if (response.totalHits > 0) {
        createNotification()
          .title("Success")
          .text(String.format("Found %s document(s)", response.totalHits))
          .showInformation();
      } else {
        createNotification()
          .title("Well...")
          .text(String.format("No documents found", response.totalHits))
          .showWarning();
      }
    }
    this.totalHits.set(response.totalHits);
    this.appliedSearchText = response.request.text;
  }

  private void clearSearchResult() {
    this.foundDocs.getItems().clear();
    this.totalHits.set(0);
  }

  private void repaginate() {
    final int pageSize = this.pageSize.get();
    int pageCount = this.totalHits.get() / pageSize;
    if (this.totalHits.get() % pageSize > 0) {
      pageCount++;
    }
    this.pagination.setMaxPageIndicatorCount(Math.min(pageCount, 5));
    this.pagination.setPageCount(pageCount);
  }
}
