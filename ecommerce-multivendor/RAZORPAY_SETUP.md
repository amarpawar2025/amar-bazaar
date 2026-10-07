# Razorpay Test Setup

The project now uses Razorpay Standard Checkout.

## 1. Create Razorpay test keys

Create/test keys in the Razorpay Dashboard. Keep the **Key Secret only on the backend**.

Official documentation:
https://razorpay.com/docs/

## 2. Configure backend

Do NOT put the secret in React.

Windows PowerShell:

```powershell
$env:RAZORPAY_KEY_ID="rzp_test_xxxxxxxxxx"
$env:RAZORPAY_KEY_SECRET="your_test_secret"
```

Then start Spring Boot:

```powershell
cd ecommerce-multivendor
.\mvnw.cmd spring-boot:run
```

The backend runs on:

```text
http://localhost:5454
```

You can also place the values in your local environment/configuration, but do not commit real secrets to GitHub.

## 3. Start frontend

```powershell
cd ecommerce-react1
npm install
npm start
```

Frontend:

```text
http://localhost:3000
```

## 4. Payment flow

1. Customer logs in.
2. Customer adds products to cart.
3. Customer opens Checkout.
4. Customer enters/selects a delivery address.
5. Customer clicks **Pay Securely**.
6. Backend calculates the amount from the authenticated user's cart.
7. Backend creates a Razorpay Order.
8. Razorpay Checkout opens.
9. Customer can use the payment methods enabled in Razorpay Checkout (for example UPI/card/netbanking, subject to the account/dashboard configuration).
10. Razorpay returns payment id, order id and signature.
11. Backend verifies the signature using the secret.
12. Backend fetches the payment and confirms it is captured.
13. Application marks the payment/order as completed and creates the seller transaction/report records.
14. Customer is taken to Account > Orders.

## Security

- The Razorpay secret is never sent to React.
- The amount is calculated on the server from the user's cart.
- The Razorpay order id is created by the server.
- The returned Razorpay signature is verified on the server.
- Payment verification is protected by the authenticated user and internal payment-order id.

For production, also configure HTTPS, webhook verification, proper secret management, and production Razorpay keys.
