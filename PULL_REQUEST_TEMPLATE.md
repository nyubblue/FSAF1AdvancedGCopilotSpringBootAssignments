# Question to Answer Session

## In Task 1 (Refactoring placeOrder), what specific "code smell" did the original method have? How does the "Extract Method" refactoring improve the code's maintainability?

### Current Issues in `createOrder` Method

The `createOrder` method is currently handling multiple responsibilities within a single function:

- Find and validate the user by `userId`.
- Iterate through each item in the request:
  - Find and validate the product.
  - Check product stock.
  - Deduct the stock and save the updated product.
  - Create an `OrderItem` object.
- Create the `Order` object and assign the `OrderItems`.
- Establish the relationship between `Order` and `OrderItem`.
- Save both `Order` and `OrderItem` to the database.
- Return the created `Order`.

### Problem

Combining all these responsibilities into a single method leads to the following issues:

- The function becomes long and difficult to read.
- It is hard to write unit tests for such a large method.
- Maintenance becomes more complicated.
- The code is difficult to extend or modify in the future.

### Benefits of This Refactor

- **Improved readability and maintainability**: Each method handles a single responsibility, making the code easier to understand and modify.
- **Better testability**: Individual pieces of logic can be tested independently, leading to more reliable unit tests.
- **Easier to extend**: If new logic needs to be added in the future (e.g., applying promotions, calculating shipping fees, etc.), it can be implemented as separate helper methods without affecting existing functionality.

## In Task 2 (Debugging), which of the three bugs was the most difficult for you to create a prompt for? Why do you think that is? What makes a good prompt for debugging?

As a developer using AI to help debug these issues, the most difficult bug to create a good prompt for is Bug C (Data Integrity) — the missing @Transactional annotation.

### Why Bug `C` is the hardest to prompt for
1. It's non-obvious and non-local:
- The issue doesn't throw an immediate error or exception.
- It causes subtle data inconsistencies, like stock not being updated, only noticed after running the app in real-world scenarios.
- Unlike Bugs A and B, the code might seem to work in isolated unit tests.
2. Hard to describe symptoms:
- When prompting AI, it’s difficult to give a precise, reproducible symptom.
- You might only be able to say something vague like “stock sometimes doesn’t update properly after an order.” That’s harder for AI to trace than “I get a NullPointerException” or “my total price is wrong.”

### What makes a good debugging prompt for AI?
1. Contextual Clarity:
- Mention the method/class name and what it’s supposed to do.
- Ex: “In OrderServiceImpl, after placing an order, the stock isn’t reduced.”
2. Symptoms or Errors Observed:
- Logs, error messages, or unexpected behavior.
- Ex: “The order goes through, but the inventory quantity remains the same.”
3. Snippets of Code (Not Too Much):
- Show only the relevant method or transaction block.
- Ex: public void placeOrder(Order order) { ... } // missing @Transactional
4. Environment or Framework Details:
- Mention you're using Spring, JPA, etc., so the AI knows @Transactional applies.

## In Task 3 (Security Analysis), besides the issues Copilot found, can you think of one other potential security risk in a typical e-commerce application?

One common and critical security risk in a typical e-commerce application is Insecure Direct Object Reference (IDOR).

### What is IDOR?
IDOR occurs when an application exposes a reference to an internal object (like a user ID, order ID, or file name) and does not properly check whether the currently authenticated user is authorized to access or modify that object.

### Example in E-commerce:
Suppose you have an endpoint:
```
GET /api/v1/orders/12345
```
If the backend only checks that the user is authenticated, but does not verify that the order with ID 12345 actually belongs to the current user, then a malicious user could change the ID in the URL and access or modify someone else’s order.

### Potential Impact:
- Data leakage: Users can view other users’ orders, addresses, or payment info.
- Unauthorized actions: Users can cancel, modify, or pay for orders that are not theirs.
- Privacy violation: Exposure of sensitive customer data.
### How to Prevent:
- Always check that the authenticated user is the owner of the resource before returning or modifying data.
- Use server-side authorization checks, not just client-side.
- Avoid using predictable IDs in URLs; consider using UUIDs or other non-sequential identifiers.

