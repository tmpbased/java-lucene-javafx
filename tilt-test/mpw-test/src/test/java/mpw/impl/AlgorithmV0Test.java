package mpw.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import mpw.api.KeyPurpose;
import mpw.template.Abbrev;
import mpw.template.TemplateStr;

public class AlgorithmV0Test {
  @Test
  public void test() throws Exception {
    final Algorithm algo = new AlgorithmV0();
    final mpw.api.MasterKey masterKey =
        algo.masterKey(new MasterPassword("test".getBytes(StandardCharsets.US_ASCII)),
            new MasterKeySalt("Robert Lee Mitchell"));
    final mpw.api.SiteKey siteKey =
        algo.siteKey(masterKey, new SiteKeySalt("twitter.com", 1, KeyPurpose.Identification));
    assertEquals("qorzisebo", SiteKey.class.cast(siteKey)
        .format(new TemplateStr(Abbrev.CANONICAL, "ncvccvcvcv").toTemplate()).substring(1));
  }
}
