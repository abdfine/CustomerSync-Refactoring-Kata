package codingdojo;

public class CustomerMatcher {

    private final CustomerRepository customerRepository;

    public CustomerMatcher(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerMatches findMatchBy(String externalId, String companyNumber) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByExternalId = this.customerRepository.findByExternalId(externalId);
        if (matchByExternalId != null) {
            matches.setCustomer(matchByExternalId);
            matches.setMatchTerm("ExternalId");
            Customer matchByMasterId = this.customerRepository.findByMasterExternalId(externalId);
            if (matchByMasterId != null) matches.addDuplicate(matchByMasterId);
        } else {
            Customer matchByCompanyNumber = this.customerRepository.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                matches.setCustomer(matchByCompanyNumber);
                matches.setMatchTerm("CompanyNumber");
            }
        }

        return matches;
    }

    public CustomerMatches findMatchBy(String externalId) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerRepository.findByExternalId(externalId);
        matches.setCustomer(matchByPersonalNumber);
        if (matchByPersonalNumber != null) matches.setMatchTerm("ExternalId");
        return matches;
    }

    public Customer updateCustomerRecord(Customer customer) {
        return customerRepository.updateCustomerRecord(customer);
    }

    public Customer createCustomerRecord(Customer customer) {
        return customerRepository.createCustomerRecord(customer);
    }

    public void updateShoppingList(Customer customer, ShoppingList consumerShoppingList) {
        customer.addShoppingList(consumerShoppingList);
        customerRepository.updateShoppingList(consumerShoppingList);
        customerRepository.updateCustomerRecord(customer);
    }
}
