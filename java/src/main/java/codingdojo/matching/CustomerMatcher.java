package codingdojo.matching;

import codingdojo.sync.ExternalCustomer;
import codingdojo.model.Customer;
import codingdojo.model.CustomerRepository;
import codingdojo.model.CustomerType;

public class CustomerMatcher {

    private final CustomerRepository customerRepository;

    public CustomerMatcher(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerMatches getCustomerMatches(ExternalCustomer externalCustomer) {
        CustomerMatches customerMatches;
        if (externalCustomer.isCompany()) {
            customerMatches = findCompanyMatches(externalCustomer);
        } else {
            customerMatches = findPersonMatches(externalCustomer);
        }
        return customerMatches;
    }

    private CustomerMatches findCompanyMatches(ExternalCustomer externalCustomer) {

        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();

        CustomerMatches customerMatches = findMatchBy(externalId, companyNumber);

        if (customerMatches.getCustomer() != null && !CustomerType.COMPANY.equals(customerMatches.getCustomer().getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }

        if ("ExternalId".equals(customerMatches.getMatchTerm())) {
            String customerCompanyNumber = customerMatches.getCustomer().getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                customerMatches.getCustomer().setMasterExternalId(null);
                customerMatches.addDuplicate(customerMatches.getCustomer());
                customerMatches.setCustomer(null);
                customerMatches.setMatchTerm(null);
            }
        } else if ("CompanyNumber".equals(customerMatches.getMatchTerm())) {
            String customerExternalId = customerMatches.getCustomer().getExternalId();
            if (customerExternalId != null && !externalId.equals(customerExternalId)) {
                throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
            }
            Customer customer = customerMatches.getCustomer();
            customer.setExternalId(externalId);
            customer.setMasterExternalId(externalId);
        }

        return customerMatches;
    }

    private CustomerMatches findMatchBy(String externalId, String companyNumber) {
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

    private CustomerMatches findPersonMatches(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();

        CustomerMatches customerMatches = findMatchBy(externalId);

        if (customerMatches.getCustomer() != null) {
            if (!CustomerType.PERSON.equals(customerMatches.getCustomer().getCustomerType())) {
                throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
            }

            if (!"ExternalId".equals(customerMatches.getMatchTerm())) {
                Customer customer = customerMatches.getCustomer();
                customer.setExternalId(externalId);
                customer.setMasterExternalId(externalId);
            }
        }

        return customerMatches;
    }

    private CustomerMatches findMatchBy(String externalId) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerRepository.findByExternalId(externalId);
        matches.setCustomer(matchByPersonalNumber);
        if (matchByPersonalNumber != null) matches.setMatchTerm("ExternalId");
        return matches;
    }
}
