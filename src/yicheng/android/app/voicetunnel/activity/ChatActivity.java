package yicheng.android.app.voicetunnel.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import yicheng.android.app.voicetunnel.R;
import yicheng.android.app.voicetunnel.adapter.ChatHistoryListViewAdapter;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.result.Result;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.users.result.QBUserPagedResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatActivity extends Activity {

	private Button send_message_button;
	private EditText send_message_editText;
	private ListView chat_history_listView;
	private RelativeLayout chat_activity_layout;

	private ArrayList<QBChatMessage> history;
	private ArrayList<QBDialog> dialogList;
	private ChatHistoryListViewAdapter listViewAdapter;

	private QBPrivateChat privateChat;
	private Integer occupantID;

	private SharedPreferences local_user_information;
	private SharedPreferences.Editor local_user_editor;
	private String PREFS_NAME = "LocalUserInfo";

	private Integer user_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity_layout);

		initiateComponents();

		loadChatHistory();

		setComponentControl();

	}

	private void initiateComponents() {
		send_message_button = (Button) findViewById(R.id.send_message_button);
		send_message_editText = (EditText) findViewById(R.id.send_message_editText);
		chat_history_listView = (ListView) findViewById(R.id.chat_history_listView);
		chat_activity_layout = (RelativeLayout) findViewById(R.id.chat_activity_layout);

		local_user_information = this.getSharedPreferences(PREFS_NAME, 0);
		user_id = local_user_information.getInt("user_id", 0);

	}

	private void setComponentControl() {
		setTouchHideKeyboardControl();
		setSendMessageButtonControl();
	}

	QBRequestGetBuilder customObjectRequestBuilder;

	private void initiatePrivateChat() {
		QBPrivateChatManager privateChatManager = QBChatService.getInstance()
				.getPrivateChatManager();

		privateChat = privateChatManager.getChat(occupantID);
		if (privateChat == null) {

			privateChat = privateChatManager.createChat(occupantID,
					new QBMessageListener<QBPrivateChat>() {

						@Override
						public void processError(QBPrivateChat arg0,
								QBChatException arg1, QBChatMessage arg2) {
							// TODO Auto-generated method stub
							Log.e("sending message", "process error");
						}

						@Override
						public void processMessage(QBPrivateChat arg0,
								QBChatMessage arg1) {
							// TODO Auto-generated method stub
							showMessage(arg1);
						}

						@Override
						public void processMessageDelivered(QBPrivateChat arg0,
								String arg1) {
							// TODO Auto-generated method stub
							Log.e("sending message", "delivered");
						}

						@Override
						public void processMessageRead(QBPrivateChat arg0,
								String arg1) {
							// TODO Auto-generated method stub
							Log.e("sending message", "read");
						}

					});
		}

	}

	private void loadChatHistory() {
		loadAllDialogs();
	}

	private void loadChatHistoryInDialog() {
		QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
		customObjectRequestBuilder.setPagesLimit(100);

		QBChatService.getDialogMessages(dialogList.get(0),
				customObjectRequestBuilder,
				new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
					@Override
					public void onSuccess(ArrayList<QBChatMessage> messages,
							Bundle args) {
						String s = "";
						for (QBChatMessage message : messages) {
							Log.e("chat history", message.getBody());
							s += message.getBody() + "\n";
						}
						listViewAdapter = new ChatHistoryListViewAdapter(
								ChatActivity.this,
								new ArrayList<QBChatMessage>());
						chat_history_listView.setAdapter(listViewAdapter);

						for (int i = messages.size() - 1; i >= 0; --i) {
							QBChatMessage msg = messages.get(i);
							showMessage(msg);
						}
						initiatePrivateChat();

					}

					@Override
					public void onError(List<String> errors) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								ChatActivity.this);
						dialog.setMessage("load chat history errors: " + errors)
								.create().show();
					}
				});
	}

	public void showMessage(QBChatMessage message) {
		listViewAdapter.add(message);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				listViewAdapter.notifyDataSetChanged();
				scrollDown();
			}
		});
	}

	private void scrollDown() {
		chat_history_listView
				.setSelection(chat_history_listView.getCount() - 1);
	}

	private void loadAllDialogs() {
		customObjectRequestBuilder = new QBRequestGetBuilder();
		customObjectRequestBuilder.setPagesLimit(100);

		QBChatService.getChatDialogs(null, customObjectRequestBuilder,
				new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
					@Override
					public void onSuccess(final ArrayList<QBDialog> dialogs,
							Bundle args) {
						dialogList = dialogs;

						occupantID = getOccupantID();

						Log.e("dialog", dialogList.get(0).getDialogId());
						loadChatHistoryInDialog();
					}

					@Override
					public void onError(List<String> errors) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								ChatActivity.this);
						dialog.setMessage("get dialogs errors: " + errors)
								.create().show();
					}
				});
	}

	private Integer getOccupantID() {

		for (Integer i : dialogList.get(0).getOccupants()) {
			if (i != user_id) {
				return i;
			}
		}

		return 0;
	}

	private void setSendMessageButtonControl() {
		send_message_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				sendMessage(send_message_editText.getText().toString());

				send_message_editText.setText("");

			}
		});
	}

	private void sendMessage(String message) {

		try {

			QBChatMessage chatMessage = new QBChatMessage();
			chatMessage.setBody(message);
			chatMessage.setProperty("save_to_history", "1");
			chatMessage.setDateSent(new Date().getTime() / 100);

			privateChat.sendMessage(chatMessage);

			showMessage(chatMessage);

		} catch (XMPPException e) {

		} catch (SmackException.NotConnectedException e) {

		}

	}

	private void setTouchHideKeyboardControl() {
		chat_activity_layout.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard();
				return false;
			}
		});

		chat_history_listView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard();
				return false;
			}
		});

		send_message_button.setOnTouchListener(new View.OnTouchListener() {

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
