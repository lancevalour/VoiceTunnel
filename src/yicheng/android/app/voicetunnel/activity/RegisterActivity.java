package yicheng.android.app.voicetunnel.activity;

import java.util.List;

import yicheng.android.app.voicetunnel.R;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText register_email_editText, register_username_editText,
			register_password_editText, register_password_again_editText;
	private RelativeLayout register_activity_layout;
	private Button register_back_button, register_confirm_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		setContentView(R.layout.register_activity_layout);

		initiateComponents();
		setComponentControl();
	}

	private void initiateComponents() {
		register_email_editText = (EditText) findViewById(R.id.register_email_editText);
		register_username_editText = (EditText) findViewById(R.id.register_username_editText);
		register_password_editText = (EditText) findViewById(R.id.register_password_editText);
		register_password_again_editText = (EditText) findViewById(R.id.register_password_again_editText);
		register_activity_layout = (RelativeLayout) findViewById(R.id.register_activity_layout);
		register_back_button = (Button) findViewById(R.id.register_back_button);
		register_confirm_button = (Button) findViewById(R.id.register_confirm_button);
	}

	private void setComponentControl() {
		setTouchHideKeyboardControl();
		setRegisterConfirmButtonControl();
		setRegisterBackButtonControl();
	}

	private void setRegisterConfirmButtonControl() {
		register_confirm_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!register_password_editText
						.getText()
						.toString()
						.equals(register_password_again_editText.getText()
								.toString())) {
					Toast.makeText(getBaseContext(),
							"Passwords are not identical", Toast.LENGTH_SHORT)
							.show();
				}
				else {
					QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
						@Override
						public void onSuccess(QBSession session, Bundle args) {

							final QBUser user = new QBUser(
									register_username_editText.getText()
											.toString(),
									register_password_editText.getText()
											.toString());
							user.setEmail(register_email_editText.getText()
									.toString());

							QBUsers.signUp(user,
									new QBEntityCallbackImpl<QBUser>() {
										@Override
										public void onSuccess(QBUser user,
												Bundle args) {
											// success
											Log.e("Signing up",
													"Sign up success");
											Toast.makeText(getBaseContext(),
													"Sign up Success",
													Toast.LENGTH_SHORT).show();
										}

										@Override
										public void onError(List<String> errors) {
											// error

											for (String s : errors) {
												Log.e("Signing up", s);
												Toast.makeText(
														getBaseContext(), s,
														Toast.LENGTH_SHORT)
														.show();
											}
										}
									});
						}

						@Override
						public void onError(List<String> errors) {
							AlertDialog.Builder dialog = new AlertDialog.Builder(
									RegisterActivity.this);
							dialog.setMessage(
									"create session errors: " + errors)
									.create().show();
						}
					});
				}
			}
		});

	}

	private void setRegisterBackButtonControl() {
		register_back_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						"yicheng.android.app.voicetunnel.LOGINACTIVITY");
				startActivity(intent);
				finish();
			}
		});

	}

	private void setTouchHideKeyboardControl() {
		register_activity_layout.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard();
				return false;
			}
		});

		register_back_button.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard();
				return false;
			}
		});

		register_confirm_button.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard();
				return false;
			}
		});

	}

	private void hideKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		moveTaskToBack(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			moveTaskToBack(true);

			return false;
		}
		return super.onKeyDown(keyCode, event);

	}

}
