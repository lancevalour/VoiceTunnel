package yicheng.android.app.voicetunnel.activity;

import java.util.List;

import org.jivesoftware.smack.SmackException;

import yicheng.android.app.voicetunnel.R;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.result.Result;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.users.result.QBUserResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private final static String APP_ID = "18658";
	private final static String AUTH_KEY = "z-FTGZRv-5kyxgv";
	private final static String AUTH_SECRET = "QzxNqCAkJ2KedEH";
	public static QBChatService chatService;
	private Handler handler;

	private Button login_button, forget_password_button, register_button;
	private EditText login_username_editText, login_password_editText;
	private LinearLayout login_loading_layout;
	private RelativeLayout login_activity_layout;

	private String user_login, user_password, user_email;
	private Integer user_id;

	private SharedPreferences local_user_information;
	private SharedPreferences.Editor local_user_editor;
	private String PREFS_NAME = "LocalUserInfo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		setContentView(R.layout.login_activity_layout);

		autoLogin();

	}

	private void autoLogin() {
		local_user_information = this.getSharedPreferences(PREFS_NAME, 0);
		boolean isLoggedIn = local_user_information.getBoolean("isLoggedIn",
				false);
		if (!isLoggedIn) {

			initiateComponents();

			setComponentControl();

			setHandlerControl();

			logInToXMPPServer();

		} else {
			user_login = local_user_information.getString("user_login",
					"default");
			user_password = local_user_information.getString("user_password",
					"default");

			QBChatService.setDebugEnabled(true);
			QBSettings.getInstance().fastConfigInit(
					LoginActivity.APP_ID, LoginActivity.AUTH_KEY,
					LoginActivity.AUTH_SECRET);

			if (!QBChatService.isInitialized()) {
				QBChatService.init(LoginActivity.this);
			}

			chatService = QBChatService.getInstance();
			
			final QBUser user = new QBUser(user_login, user_password);

			QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
				@Override
				public void onSuccess(QBSession session, Bundle args) {

					Log.e("Signing in", "Sign in success");
					Toast.makeText(getBaseContext(), "Login Success",
							Toast.LENGTH_SHORT).show();
					user.setId(session.getUserId());

					loginToChat(user);
				}

				@Override
				public void onError(List<String> errors) {
					/*AlertDialog.Builder dialog = new AlertDialog.Builder(
							LoginActivity.this);
					dialog.setMessage("create session errors: " + errors)
							.create().show();*/
				}
			});

		}

	}

	private void initiateComponents() {
		login_username_editText = (EditText) findViewById(R.id.login_username_editText);
		login_password_editText = (EditText) findViewById(R.id.login_password_editText);
		login_button = (Button) findViewById(R.id.login_button);
		forget_password_button = (Button) findViewById(R.id.forget_password_button);
		register_button = (Button) findViewById(R.id.register_button);
		login_loading_layout = (LinearLayout) findViewById(R.id.login_loading_layout);
		login_activity_layout = (RelativeLayout) findViewById(R.id.login_activity_layout);

	}

	private void setTouchHideKeyboardControl() {
		login_activity_layout.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard();
				return false;
			}
		});

		login_button.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard();
				return false;
			}
		});

		forget_password_button.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard();
				return false;
			}
		});

		register_button.setOnTouchListener(new View.OnTouchListener() {

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

	private void setComponentControl() {
		setTouchHideKeyboardControl();
		setLoginButtonControl();
		setForgetPasswordButtonControl();
		setRegisterButtonControl();

	}

	private void setLoginButtonControl() {
		login_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final QBUser user = new QBUser(login_username_editText
						.getText().toString(), login_password_editText
						.getText().toString());

				QBAuth.createSession(user,
						new QBEntityCallbackImpl<QBSession>() {
							@Override
							public void onSuccess(QBSession session, Bundle args) {

								Log.e("Signing in", "Sign in success");
								Toast.makeText(getBaseContext(),
										"Login Success", Toast.LENGTH_SHORT)
										.show();
								user.setId(session.getUserId());

								local_user_editor = local_user_information
										.edit();
								local_user_editor.putString("user_login",
										user.getLogin());
								local_user_editor.putString("user_password",
										user.getPassword());
								local_user_editor.putInt("user_id",
										user.getId());
								// local_user_editor.putInt("user_avatar_id",
								// user.getFileId());

								local_user_editor
										.putBoolean("isLoggedIn", true);
								local_user_editor.commit();

								loginToChat(user);
							}

							@Override
							public void onError(List<String> errors) {
								AlertDialog.Builder dialog = new AlertDialog.Builder(
										LoginActivity.this);
								dialog.setMessage(
										"create session errors: " + errors)
										.create().show();
							}
						});
			}
		});
	}

	private void setForgetPasswordButtonControl() {
		forget_password_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void setRegisterButtonControl() {
		register_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						"yicheng.android.app.voicetunnel.REGISTERACTIVITY");
				startActivity(intent);
				finish();
			}
		});
	}

	private void setHandlerControl() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					login_loading_layout.setVisibility(View.INVISIBLE);
				}
			}
		};
	}

	private void loginToChat(final QBUser user) {

		chatService.login(user, new QBEntityCallbackImpl() {
			@Override
			public void onSuccess() {

				try {
					chatService.startAutoSendPresence(60);
				} catch (SmackException.NotLoggedInException e) {
					e.printStackTrace();
				}

				Log.e("Signing in", "Go to chat view");

				Intent intent = new Intent(
						"yicheng.android.app.voicetunnel.CHATACTIVITY");
				startActivity(intent);
				finish();

			}

			@Override
			public void onError(List errors) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						LoginActivity.this);
				dialog.setMessage("chat login errors: " + errors).create()
						.show();
			}
		});
	}

	private void logInToXMPPServer() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						QBChatService.setDebugEnabled(true);
						QBSettings.getInstance().fastConfigInit(
								LoginActivity.APP_ID, LoginActivity.AUTH_KEY,
								LoginActivity.AUTH_SECRET);

						if (!QBChatService.isInitialized()) {
							QBChatService.init(LoginActivity.this);
						}

						chatService = QBChatService.getInstance();

						Message msg = Message.obtain();
						msg.what = 1;
						handler.sendMessage(msg);
					}

				}).start();
			}

		});
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
