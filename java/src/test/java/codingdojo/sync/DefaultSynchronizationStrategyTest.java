package codingdojo.sync;

import codingdojo.model.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSynchronizationStrategyTest {

    private final DefaultSynchronizationStrategy sut = new DefaultSynchronizationStrategy();

    @Test
    void returnsNewDuplicateCustomer() {

        final ExternalCustomer externalCustomer = new ExternalCustomer();
        String externalId = "ext1234";
        externalCustomer.setExternalId(externalId);
        externalCustomer.setName("John");

        final Customer duplicate = sut.updateDuplicate(externalCustomer, null);

        assertNotNull(duplicate);
        assertEquals(duplicate.getExternalId(), externalId);
        assertEquals(duplicate.getMasterExternalId(), externalId);
        assertEquals(duplicate.getName(), externalCustomer.getName());
    }

    // and so on

}