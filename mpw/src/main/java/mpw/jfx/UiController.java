package mpw.jfx;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.Pane;
import mpw.api.Algorithm;
import mpw.api.KeyPurpose;
import mpw.api.MasterKey;
import mpw.api.SiteKey;
import mpw.impl.MasterKeySalt;
import mpw.impl.MasterPassword;
import mpw.impl.SiteKeySalt;
import mpw.letter.Alphabetic;
import mpw.letter.ConsonantsLowerCase;
import mpw.letter.Numeric;
import mpw.letter.Other;
import mpw.letter.UnionSet;
import mpw.letter.VowelsLowerCase;
import mpw.template.Word;
import mpw.template.limit.Limit;

public class UiController implements Initializable {
  private final Algorithm algorithm;

  @FXML
  private Pane masterKeyPane, siteKeyPane;
  @FXML
  private TextField fullName, siteName;
  @FXML
  private PasswordField masterPassword;
  @FXML
  private ChoiceBox<Integer> loginLength, passwordLength;
  @FXML
  private Spinner<Integer> siteCounter;
  @FXML
  private ToggleButton passwordShowHide;

  private ObjectProperty<MasterKey> masterKey;
  private String password;

  public UiController(final Algorithm algorithm) {
    this.algorithm = algorithm;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.masterKey = new SimpleObjectProperty<>();
    this.masterKey.addListener(new ChangeListener<MasterKey>() {
      @Override
      public void changed(ObservableValue<? extends MasterKey> observable, MasterKey oldValue,
          MasterKey newValue) {
        masterKeyPane.setDisable(newValue != null);
        siteKeyPane.setDisable(newValue == null);
      }
    });
    this.passwordShowHide.focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
          Boolean newValue) {
        passwordShowHide.setSelected(false);
      }
    });
    this.passwordShowHide.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
          Boolean newValue) {
        if (newValue) {
          password = masterPassword.getText();
          masterPassword.clear();
          masterPassword.setPromptText(password);
        } else {
          masterPassword.setText(password);
          masterPassword.setPromptText("");
          password = null;
        }
      }
    });
    this.loginLength.getItems().addAll(5, 7, 13);
    this.loginLength.setValue(7);
    this.passwordLength.getItems().addAll(8, 16, 24, 32);
    this.passwordLength.setValue(24);
    this.siteCounter.setValueFactory(new IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
  }

  @FXML
  private void createMasterKey() {
    final MasterKey masterKey = this.algorithm.masterKey(
        new MasterPassword(this.masterPassword.getText().getBytes(StandardCharsets.UTF_8)),
        new MasterKeySalt(this.fullName.getText()));
    this.masterKey.set(masterKey);
    this.masterPassword.clear();
  }

  @FXML
  private void changeMasterKey() {
    this.masterKey.set(null);
  }

  private Word createLoginTemplate() {
    return new Word.Builder().setLength(this.loginLength.getValue())
        .setCharLimit(VowelsLowerCase.INSTANCE, new Limit.AtLeast(1))
        .setCharLimit(ConsonantsLowerCase.INSTANCE, new Limit.AtLeast(1)).build();
  }

  private Word createPasswordTemplate() {
    return new Word.Builder().setLength(this.passwordLength.getValue())
        .setCharLimit(UnionSet.INSTANCE, new Limit.AtLeast(1))
        .setCharLimit(Alphabetic.INSTANCE, new Limit.Exactly(1))
        .setCharLimit(Numeric.INSTANCE, new Limit.Exactly(1))
        .setCharLimit(Other.INSTANCE, new Limit.Exactly(1)).build();
  }

  @FXML
  private void clipSiteLogin() {
    final Clipboard systemClipboard = Clipboard.getSystemClipboard();
    final SiteKey siteKey =
        this.algorithm.siteKey(this.masterKey.get(), new SiteKeySalt(this.siteName.getText(),
            this.siteCounter.getValue(), KeyPurpose.Identification));
    systemClipboard
        .setContent(Map.of(DataFormat.PLAIN_TEXT, siteKey.format(createLoginTemplate())));
  }

  @FXML
  private void clipSitePassword() {
    final Clipboard systemClipboard = Clipboard.getSystemClipboard();
    final SiteKey siteKey =
        this.algorithm.siteKey(this.masterKey.get(), new SiteKeySalt(this.siteName.getText(),
            this.siteCounter.getValue(), KeyPurpose.Authentication));
    systemClipboard
        .setContent(Map.of(DataFormat.PLAIN_TEXT, siteKey.format(createPasswordTemplate())));
  }
}
