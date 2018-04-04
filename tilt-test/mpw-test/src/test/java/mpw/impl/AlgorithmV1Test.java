package mpw.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import mpw.api.KeyPurpose;
import mpw.template.Abbrev;
import mpw.template.TemplateStr;

public class AlgorithmV1Test {
  @Test
  @Disabled
  void test() throws Exception {
    final Algorithm algo = new AlgorithmV1();
    final mpw.api.MasterKey masterKey =
        algo.masterKey(new MasterPassword("test".getBytes(StandardCharsets.US_ASCII)),
            new MasterKeySalt("Robert Lee Mitchell"));
    final mpw.api.SiteKey siteKey =
        algo.siteKey(masterKey, new SiteKeySalt("twitter.com", 1, KeyPurpose.Identification));
    assertEquals("soskotadi",
        siteKey.format(new TemplateStr(Abbrev.CANONICAL, "cvccvcvcv").toTemplate()));
  }
}
