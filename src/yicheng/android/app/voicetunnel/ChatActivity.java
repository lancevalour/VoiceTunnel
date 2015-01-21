package yicheng.android.app.voicetunnel;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatHistoryMessage;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBMessage;
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
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends Activity {

	private Button send_message_button;
	private EditText send_message_editText;
	private TextView textView;
	
	private ArrayList<QBChatHistoryMessage> history;
	private ArrayList<QBDialog> dialogList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity_layout);

		initiateComponents();

		setComponentControl();

		loadChatHistory();
	}

	private void initiateComponents() {
		send_message_button = (Button) findViewById(R.id.send_message_button);
		send_message_editText = (EditText) findViewById(R.id.send_message_editText);
		textView = (TextView) findViewById(R.id.textView);
	}

	private void setComponentControl() {
		setSendMessageButtonControl();
	}

	QBRequestGetBuilder customObjectRequestBuilder;

	private void loadChatHistory() {
		loadAllDialogs();

		
	}
	
	private void loadChatHistoryInDialog(){
		QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
		customObjectRequestBuilder.setPagesLimit(100);

		QBChatService.getDialogMessages(dialogList.get(0),
				customObjectRequestBuilder,
				new QBEntityCallbackImpl<ArrayList<QBChatHistoryMessage>>() {
					@Override
					public void onSuccess(
							ArrayList<QBChatHistoryMessage> messages,
							Bundle args) {
						String s = "";
						for (QBChatHistoryMessage message : messages) {
							Log.e("chat history", message.getBody());
							s += message.getBody() + "\n";
						}
						textView.setText(s);
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

	private void loadAllDialogs() {
		customObjectRequestBuilder = new QBRequestGetBuilder();
		customObjectRequestBuilder.setPagesLimit(100);

		QBChatService.getChatDialogs(null,
				customObjectRequestBuilder,
				new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
					@Override
					public void onSuccess(final ArrayList<QBDialog> dialogs,
							Bundle args) {
						dialogList = dialogs;
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

	private void setSendMessageButtonControl() {
		send_message_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				sendMessage(2214943, send_message_editText.getText().toString());
			}
		});
	}

	private void sendMessage(int id, String message) {

		QBPrivateChatManager privateChatManager = QBChatService.getInstance()
				.getPrivateChatManager();

		QBPrivateChat privateChat = privateChatManager.getChat(id);
		if (privateChat == null) {

			privateChat = privateChatManager.createChat(id,
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
							Log.e("sending message", "process message");
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

		try {

			QBChatMessage chatMessage = new QBChatMessage();
			chatMessage.setBody(message);
			chatMessage.setProperty("save_to_history", "1");
			privateChat.sendMessage(chatMessage);

		} catch (XMPPException e) {

		} catch (SmackException.NotConnectedException e) {

		}

	}

}
