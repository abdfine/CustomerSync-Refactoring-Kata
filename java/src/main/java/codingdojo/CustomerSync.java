package codingdojo;

import codingdojo.matching.CustomerMatcher;
import codingdojo.matching.CustomerMatches;
import codingdojo.model.Customer;
import codingdojo.model.CustomerRepository;
import codingdojo.model.CustomerType;
import codingdojo.model.ShoppingList;

import java.util.List;

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

        Customer customer = customerMatches.getCustomer();

        if (customer == null) {
            customer = new Customer();
            customer.setExternalId(externalCustomer.getExternalId());
            customer.setMasterExternalId(externalCustomer.getExternalId());
        }

        populateNameTypeCompanyNumberBonusPoints(externalCustomer, customer);

        boolean created = false;
        if (customer.getInternalId() == null) {
            customer = createCustomer(customer);
            created = true;
        } else {
            this.customerRepository.updateCustomerRecord(customer);
        }
        updateContactInfo(externalCustomer, customer);

        if (customerMatches.hasDuplicates()) {
            for (Customer duplicate : customerMatches.getDuplicates()) {
                updateDuplicate(externalCustomer, duplicate);
            }
        }

        updateRelations(externalCustomer, customer);
        updatePreferredStore(externalCustomer, customer);

        return created;
    }


    private void updateRelations(ExternalCustomer externalCustomer, Customer customer) {
        List<ShoppingList> consumerShoppingLists = externalCustomer.getShoppingLists();
        for (ShoppingList consumerShoppingList : consumerShoppingLists) {
            customer.addShoppingList(consumerShoppingList);
            customerRepository.updateShoppingList(consumerShoppingList);
            customerRepository.updateCustomerRecord(customer);
        }
    }

    private void updateDuplicate(ExternalCustomer externalCustomer, Customer duplicate) {
        if (duplicate == null) {
            duplicate = new Customer();
            duplicate.setExternalId(externalCustomer.getExternalId());
            duplicate.setMasterExternalId(externalCustomer.getExternalId());
        }

        duplicate.setName(externalCustomer.getName());

        if (duplicate.getInternalId() == null) {
            createCustomer(duplicate);
        } else {
            this.customerRepository.updateCustomerRecord(duplicate);
        }
    }

    private void updatePreferredStore(ExternalCustomer externalCustomer, Customer customer) {
        customer.setPreferredStore(externalCustomer.getPreferredStore());
    }

    private Customer createCustomer(Customer customer) {
        return this.customerRepository.createCustomerRecord(customer);
    }

    private void populateNameTypeCompanyNumberBonusPoints(ExternalCustomer externalCustomer, Customer customer) {
        customer.setName(externalCustomer.getName());
        if (externalCustomer.isCompany()) {
            customer.setCompanyNumber(externalCustomer.getCompanyNumber());
            customer.setCustomerType(CustomerType.COMPANY);
        } else {
            customer.setBonusPoints(externalCustomer.getBonusPoints());
            customer.setCustomerType(CustomerType.PERSON);
        }
    }

    private void updateContactInfo(ExternalCustomer externalCustomer, Customer customer) {
        customer.setAddress(externalCustomer.getPostalAddress());
    }

}
