package codingdojo.model;

import java.util.List;

public interface CustomerRepository {

    Customer updateCustomerRecord(Customer customer);

    Customer createCustomerRecord(Customer customer);

    void updateShoppingList(ShoppingList consumerShoppingList);

    default void updateShoppingLists(List<ShoppingList> shoppingLists) {
      for (ShoppingList shoppingList: shoppingLists) {
          updateShoppingList(shoppingList);
      }
    }

    Customer findByExternalId(String externalId);

    Customer findByMasterExternalId(String externalId);

    Customer findByCompanyNumber(String companyNumber);
}
