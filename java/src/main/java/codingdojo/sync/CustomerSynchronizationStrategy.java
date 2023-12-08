package codingdojo.sync;

import codingdojo.model.Customer;

public interface CustomerSynchronizationStrategy {

    Customer updateDuplicate(ExternalCustomer externalCustomer, Customer duplicate);

    Customer syncCustomer(ExternalCustomer externalCustomer, Customer customer);
}
