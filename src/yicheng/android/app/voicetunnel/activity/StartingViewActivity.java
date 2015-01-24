package yicheng.android.app.voicetunnel.activity;

import java.util.List;

import org.jivesoftware.smack.SmackException;

import yicheng.android.app.voicetunnel.R;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class StartingViewActivity extends Activity {
	private final static String APP_ID = "18658";
	private final static String AUTH_KEY = "z-FTGZRv-5kyxgv";
	private final static String AUTH_SECRET = "QzxNqCAkJ2KedEH";
	public static QBChatService chatService;

	private SharedPreferences local_user_information;
	private SharedPreferences.Editor local_user_editor;
	private String PREFS_NAME = "LocalUserInfo";

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.starting_view_activity_layout);

		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		handler = new Handler();
		autoLogin();
	}

	private void autoLogin() {
		local_user_information = this.getSharedPreferences(PREFS_NAME, 0);
		boolean isLoggedIn = local_user_information.getBoolean("isLoggedIn",
				false);
		if (!isLoggedIn) {

			handler.postDelayed(new Runnable() {
				public void run() {
					Intent main_view_intent = new Intent(
							"yicheng.android.app.voicetunnel.LOGINACTIVITY");
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

					startActivity(main_view_intent);

				}
			}, 1500);

		}
		else {

			QBChatService.setDebugEnabled(true);
			QBSettings.getInstance().fastConfigInit(
					StartingViewActivity.APP_ID, StartingViewActivity.AUTH_KEY,
					StartingViewActivity.AUTH_SECRET);

			if (!QBChatService.isInitialized()) {
				QBChatService.init(StartingViewActivity.this);
			}

			chatService = QBChatService.getInstance();

			final QBUser user = new QBUser(local_user_information.getString(
					"user_login", "default"), local_user_information.getString(
					"user_password", "default"));

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

				}
			});

		}

	}

	private void loginToChat(final QBUser user) {

		chatService.login(user, new QBEntityCallbackImpl() {
			@Override
			public void onSuccess() {

				try {
					chatService.startAutoSendPresence(60);
				}
				catch (SmackException.NotLoggedInException e) {
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
						StartingViewActivity.this);
				dialog.setMessage("chat login errors: " + errors).create()
						.show();
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();

	}

}
