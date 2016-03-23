# IdramMerchantPayment-Android

Idram app uses custom scheme to accept payment from other apps.
URI example : idramApp://payment?receiverName=TestName&receiverId=123456789&title=title&amount=1500&tip=15

The following query parameters should be declared.

1. receiverName (required) - e.g "Test Account Name"
2. receiverId (required) = Receiver's Idram account id. Money will be transferred to this account.
4. title(required) - Unique id for payment identification of merchant billing. (EDP_BILL_NO)
4. amount (required)
5. tip (optional)

Sample Code

1. Create the payment and launch the payment intent, for example, when a button is pressed:

```java
  /**
	 * Create the payment and launch the payment intent, for example, when a button is pressed:
	 */
	public void onPayButtonClick(View view) {
		// Check required fields
		if (amountField.getText().length() == 0) {
			Toast.makeText(this, "Amount field is required", Toast.LENGTH_LONG).show();
			return;
		}

		BigDecimal amount = new BigDecimal(amountField.getText().toString());
		BigDecimal tip = null;

		if (tipField.getText().length() > 0) {
			tip = new BigDecimal(tipField.getText().toString());
		}

		// Start payment
		Intent intent = getPaymentIntent(amount, tip);

		try {
			startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException ex) {
			Log.e(LOG_TAG, "Idram app is not installed on the user device, " +
					"or external payments are not supported by the current app version");
		}
	}
	
	private Intent getPaymentIntent(BigDecimal amount, BigDecimal tip) {
		// e.g idramApp://payment/?receiverName=ggTaxy&receiverId=1234567890&amount=1500&tip=150

		Uri.Builder uriBuilder = new Uri.Builder()
				.scheme("idramApp")
				.authority("payment")
				.appendQueryParameter("receiverName", RECEIVER_NAME)
				.appendQueryParameter("receiverId", RECEIVER_ID)
				.appendQueryParameter("title", TITLE)
				.appendQueryParameter("amount", amount.toPlainString());

		if (tip != null) {
			uriBuilder.appendQueryParameter("tip", tip.toPlainString());
		}

		return new Intent(Intent.ACTION_VIEW, uriBuilder.build());
	}
```

2. Implement onActivityResult()

```java
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Log.i(LOG_TAG, "Payment done.");
		} else if (resultCode == Activity.RESULT_CANCELED) {
			Log.i(LOG_TAG, "The user canceled.");
		} else if (resultCode == RESULT_DATA_INVALID) {
			Log.i(LOG_TAG, "An invalid Uri was submitted. Please check URI.");
		}
	}
````

**Note.** RESULT_DATA_INVALID is declared by Idram app. Value of result code is 1.
