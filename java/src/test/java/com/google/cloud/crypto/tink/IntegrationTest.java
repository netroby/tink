// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.google.cloud.crypto.tink;

import static com.google.common.io.BaseEncoding.base64;
import static org.junit.Assert.assertArrayEquals;

import com.google.cloud.crypto.tink.hybrid.HybridDecryptFactory;
import com.google.cloud.crypto.tink.hybrid.HybridEncryptFactory;
import com.google.cloud.crypto.tink.subtle.Random;
import java.security.GeneralSecurityException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for KeysetManager.
 */
@RunWith(JUnit4.class)
public class IntegrationTest {
  private static final int AES_KEY_SIZE = 16;
  private static final int HMAC_KEY_SIZE = 20;

  @Before
  public void setUp() throws GeneralSecurityException {
    HybridEncryptFactory.registerStandardKeyTypes();
    HybridDecryptFactory.registerStandardKeyTypes();
  }

  /**
   * Tests with EciesAesGcmHkdf keys generated by Tinkey.
   */
  @Test
  public void testWithTinkeyEciesAesGcmHkdf() throws Exception {
    // Generated with
    // tinkey create \
    // --key-template ECIES_P256_HKDFHMACSHA256_AES128GCM.proto \
    // --out private_key.cfg
    final String privateKeyset =
        "CKqtvK8HEo0CCoACCkR0eXBlLmdvb2dsZWFwaXMuY29tL2dvb2dsZS5jbG91ZC5j"
        + "cnlwdG8udGluay5FY2llc0FlYWRIa2RmUHJpdmF0ZUtleRK1ARKQARJICgQIAhAD"
        + "Ej4SPAo2dHlwZS5nb29nbGVhcGlzLmNvbS9nb29nbGUuY2xvdWQuY3J5cHRvLnRp"
        + "bmsuQWVzR2NtS2V5EgIQEBgBGiEAshFW965cYU3B6wwQ9OnqiICPoccQk1J7s19e"
        + "AR1PZk0iIQDQZRrtrcrj+PH7cGAiXjAtG/jLPYC46AQFOuZAfuitqxogWbIJkYNY"
        + "WiJ7D4NMku5zLgH5e6Fcy5vXEuYeHSbgvPQYAhABGKqtvK8HIAE=";

    // Generated with
    // tinkey create-public-keyset --in private_key.cfg --out public_key.cfg
    final String publicKeyset =
        "CKqtvK8HEucBCtoBCkN0eXBlLmdvb2dsZWFwaXMuY29tL2dvb2dsZS5jbG91ZC5j"
        + "cnlwdG8udGluay5FY2llc0FlYWRIa2RmUHVibGljS2V5EpABEkgKBAgCEAMSPhI8"
        + "CjZ0eXBlLmdvb2dsZWFwaXMuY29tL2dvb2dsZS5jbG91ZC5jcnlwdG8udGluay5B"
        + "ZXNHY21LZXkSAhAQGAEaIQCyEVb3rlxhTcHrDBD06eqIgI+hxxCTUnuzX14BHU9m"
        + "TSIhANBlGu2tyuP48ftwYCJeMC0b+Ms9gLjoBAU65kB+6K2rGAMQARiqrbyvByAB";


    HybridDecrypt hybridDecrypt = HybridDecryptFactory.getPrimitive(
        CleartextKeysetHandle.parseFrom(base64().decode(privateKeyset)));

    HybridEncrypt hybridEncrypt = HybridEncryptFactory.getPrimitive(
        CleartextKeysetHandle.parseFrom(base64().decode(publicKeyset)));

    byte[] plaintext = Random.randBytes(20);
    byte[] contextInfo = Random.randBytes(20);
    byte[] ciphertext = hybridEncrypt.encrypt(plaintext, contextInfo);
    assertArrayEquals(plaintext, hybridDecrypt.decrypt(ciphertext, contextInfo));
  }

  /**
   * Tests with EciesAesCtrHmacAead keys generated by Tinkey.
   */
  @Test
  public void testWithTinkeyEciesAesCtrHmacAead() throws Exception {
    // tinkey create \
    // --key-template ECIES_P256_HKDFHMACSHA256_AES256CTR_128BITIV_HMACSHA256_256BITTAG.proto \
    // --out private_key.cfg
    final String privateKeyset =
        "CJG2oOEFEqUCCpgCCkR0eXBlLmdvb2dsZWFwaXMuY29tL2dvb2dsZS5jbG91ZC5j"
        + "cnlwdG8udGluay5FY2llc0FlYWRIa2RmUHJpdmF0ZUtleRLNARKoARJgCgQIAhAD"
        + "ElYSVAo+dHlwZS5nb29nbGVhcGlzLmNvbS9nb29nbGUuY2xvdWQuY3J5cHRvLnRp"
        + "bmsuQWVzQ3RySG1hY0FlYWRLZXkSEgoGCgIIDBAQEggKBAgDEBAQIBgBGiEA8s6U"
        + "82GYEwihgH/YVTyG1iXwITX6vJk/toIdCf5K9HEiIQDacZIVqjdCZIhsNYxAPO4R"
        + "Y0Ns0WkLRX3wo6IAEszPahogWYL4SpaHlwYZaBYNVTibJueOdaPbeaLHiyCVBAkn"
        + "YAwYAhABGJG2oOEFIAE=";

    final String publicKeyset =
        "CJG2oOEFEv8BCvIBCkN0eXBlLmdvb2dsZWFwaXMuY29tL2dvb2dsZS5jbG91ZC5j"
        + "cnlwdG8udGluay5FY2llc0FlYWRIa2RmUHVibGljS2V5EqgBEmAKBAgCEAMSVhJU"
        + "Cj50eXBlLmdvb2dsZWFwaXMuY29tL2dvb2dsZS5jbG91ZC5jcnlwdG8udGluay5B"
        + "ZXNDdHJIbWFjQWVhZEtleRISCgYKAggMEBASCAoECAMQEBAgGAEaIQDyzpTzYZgT"
        + "CKGAf9hVPIbWJfAhNfq8mT+2gh0J/kr0cSIhANpxkhWqN0JkiGw1jEA87hFjQ2zR"
        + "aQtFffCjogASzM9qGAMQARiRtqDhBSAB";


    HybridDecrypt hybridDecrypt = HybridDecryptFactory.getPrimitive(
        CleartextKeysetHandle.parseFrom(base64().decode(privateKeyset)));

    HybridEncrypt hybridEncrypt = HybridEncryptFactory.getPrimitive(
        CleartextKeysetHandle.parseFrom(base64().decode(publicKeyset)));

    byte[] plaintext = Random.randBytes(20);
    byte[] contextInfo = Random.randBytes(20);
    byte[] ciphertext = hybridEncrypt.encrypt(plaintext, contextInfo);
    assertArrayEquals(plaintext, hybridDecrypt.decrypt(ciphertext, contextInfo));
  }
}
