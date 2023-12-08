package codingdojo.sync;

import codingdojo.model.Customer;
import codingdojo.model.CustomerType;
import codingdojo.model.ShoppingList;

public class DefaultSynchronizationStrategy implements CustomerSynchronizationStrategy {
    public DefaultSynchronizationStrategy() {
    }

    @Override
    public Customer updateDuplicate(ExternalCustomer externalCustomer, Customer duplicate) {
        if (duplicate == null) {
            duplicate = new Customer();
            duplicate.setExternalId(externalCustomer.getExternalId());
            duplicate.setMasterExternalId(externalCustomer.getExternalId());
        }

        duplicate.setName(externalCustomer.getName());
        return duplicate;
    }

    @Override
    public Customer syncCustomer(ExternalCustomer externalCustomer, Customer customer) {
        if (customer == null) {
            customer = new Customer();
            customer.setExternalId(externalCustomer.getExternalId());
            customer.setMasterExternalId(externalCustomer.getExternalId());
        }
        customer.setName(externalCustomer.getName());
        if (externalCustomer.isCompany()) {
            customer.setCompanyNumber(externalCustomer.getCompanyNumber());
            customer.setCustomerType(CustomerType.COMPANY);
        } else {
            customer.setBonusPoints(externalCustomer.getBonusPoints());
            customer.setCustomerType(CustomerType.PERSON);
        }
        customer.setAddress(externalCustomer.getPostalAddress());
        customer.setPreferredStore(externalCustomer.getPreferredStore());
        for (ShoppingList shoppingList: externalCustomer.getShoppingLists()) {
            customer.addShoppingList(shoppingList);
        }
        return customer;
    }
}