package am.imwallet.payment.sample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;

public class MainActivity extends Activity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private static final int RESULT_DATA_INVALID = 1;

	/**
	 * Name of the receiver that will be shot to the user
	 */
	private static final String RECEIVER_NAME = "Test Receiver";

	/**
	 * Idram account id of the receiver
	 */
	private static final String RECEIVER_ID = "123546";

	/**
	 * Unique id for payment identification of merchant billing. (EDP_BILL_NO)
	 */
	private static final String TITLE = "Unique id for payment identification of merchant billing. (EDP_BILL_NO)";

	private EditText amountField;
	private EditText tipField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		amountField = (EditText) findViewById(R.id.amount);
		tipField = (EditText) findViewById(R.id.tip);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Log.i(LOG_TAG, "Payment done.");
		} else if (resultCode == Activity.RESULT_CANCELED) {
			Log.i(LOG_TAG, "The user canceled.");
		} else if (resultCode == RESULT_DATA_INVALID) {
			Log.i(LOG_TAG, "An invalid Uri was submitted. Please check URI.");
		}
	}

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
}
