package mpw.api;

public enum KeyPurpose {
  Authentication("com.lyndir.masterpassword"),
  /** login */
  Identification("com.lyndir.masterpassword.login"),
  /** answer */
  Recovery("com.lyndir.masterpassword.answer");

  private String scope;

  private KeyPurpose(final String scope) {
    this.scope = scope;
  }

  public String getScope() {
    return scope;
  }
}
