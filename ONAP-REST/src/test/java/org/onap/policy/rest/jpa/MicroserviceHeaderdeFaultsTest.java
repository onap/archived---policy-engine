package org.onap.policy.rest.jpa;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MicroserviceHeaderdeFaultsTest {
  @Test
  public void testHeader() {
    // Set up test data
    String value = "testVal";
    MicroserviceHeaderdeFaults header = new MicroserviceHeaderdeFaults();
    header.prePersist();
    header.preUpdate();

    // Set data
    header.setGuard(value);
    header.setId(1);
    header.setModelName(value);
    header.setOnapName(value);
    header.setPriority(value);
    header.setRiskLevel(value);
    header.setRiskType(value);

    // Test gets
    assertEquals(value, header.getGuard());
    assertEquals(1, header.getId());
    assertEquals(value, header.getModelName());
    assertEquals(value, header.getOnapName());
    assertEquals(value, header.getPriority());
    assertEquals(value, header.getRiskLevel());
    assertEquals(value, header.getRiskType());
  }
}
