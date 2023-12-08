package codingdojo;

import codingdojo.matching.CustomerMatcher;
import codingdojo.matching.CustomerMatches;
import codingdojo.model.Customer;
import codingdojo.model.CustomerRepository;

public class CustomerSync {

    private final CustomerMatcher customerMatcher;
    private final CustomerRepository customerRepository;


    public CustomerSync(CustomerMatcher matcher, CustomerRepository customerRepository) {
        this.customerMatcher = matcher;
        this.customerRepository = customerRepository;
    }

    /**
     * Syncs external customer with customer base.
     *
     * @param externalCustomer the external customer to be synced
     * @return true if a new customer was created
     */
    public boolean syncExternalCustomer(ExternalCustomer externalCustomer) {

        CustomerMatches customerMatches = customerMatcher.getCustomerMatches(externalCustomer);

        boolean created = handleCustomer(externalCustomer, customerMatches.getCustomer());

        if (customerMatches.hasDuplicates()) {
            handleDuplicates(externalCustomer, customerMatches);
        }

        return created;
    }

    private boolean handleCustomer(ExternalCustomer externalCustomer, Customer customer) {
        Customer syncedCustomer = CustomerSynchronizer.syncCustomer(externalCustomer, customer);

        customerRepository.updateShoppingLists(externalCustomer.getShoppingLists());

        return createOrUpdateCustomer(syncedCustomer);
    }


    private void handleDuplicates(ExternalCustomer externalCustomer, CustomerMatches customerMatches) {
        for (Customer duplicate : customerMatches.getDuplicates()) {
            duplicate = CustomerSynchronizer.updateDuplicate(externalCustomer, duplicate);

            createOrUpdateCustomer(duplicate);
        }
    }

    private boolean createOrUpdateCustomer(Customer customer) {
        boolean created = false;
        if (customer.getInternalId() == null) {
            this.customerRepository.createCustomerRecord(customer);
            created = true;
        } else {
            this.customerRepository.updateCustomerRecord(customer);
        }
        return created;
    }
}
