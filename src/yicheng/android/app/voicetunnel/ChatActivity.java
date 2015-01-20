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
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChatActivity extends Activity {
	private String[] mPlanetTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	public QBChatService chatService;
	private final static String APP_ID = "18658";
	private final static String AUTH_KEY = "z-FTGZRv-5kyxgv";
	private final static String AUTH_SECRET = "QzxNqCAkJ2KedEH";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity_layout);

		QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
		pagedRequestBuilder.setPage(1);
		pagedRequestBuilder.setPerPage(50);

		QBUsers.getUsers(pagedRequestBuilder, new QBCallbackImpl() {
			@Override
			public void onComplete(Result result) {
				if (result.isSuccess()) {
					QBUserPagedResult usersResult = (QBUserPagedResult) result;
					ArrayList<QBUser> users = usersResult.getUsers();
					Log.e("Users: ", users.toString());

					Log.e("currentPage:", "" + usersResult.getCurrentPage());
					Log.e("totalEntries:", "" + usersResult.getTotalEntries());
					Log.e("perPage:", "" + usersResult.getPerPage());
					Log.e("totalPages:", "" + usersResult.getTotalPages());
				}
				else {
					Log.e("Errors", result.getErrors().toString());
				}
			}
		});

		QBChatService.setDebugEnabled(true);
		QBSettings.getInstance().fastConfigInit(
				ChatActivity.APP_ID, ChatActivity.AUTH_KEY,
				ChatActivity.AUTH_SECRET);

		if (!QBChatService.isInitialized()) {
			QBChatService.init(ChatActivity.this);
		}

		chatService = QBChatService.getInstance();
		
		try {
			chatService.startAutoSendPresence(60);
		}
		catch (SmackException.NotLoggedInException e) {
			e.printStackTrace();
		}
		
		
		QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();

		QBPrivateChat privateChat = privateChatManager.getChat(35);
		if (privateChat == null) {

			privateChat = privateChatManager.createChat(35,
					new QBMessageListener() {

						@Override
						public void processError(QBChat arg0,
								QBChatException arg1, QBChatMessage arg2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void processMessage(QBChat arg0,
								QBChatMessage arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void processMessageDelivered(QBChat arg0,
								String arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void processMessageRead(QBChat arg0, String arg1) {
							// TODO Auto-generated method stub

						}

					});
		}

		try {
			privateChat.sendMessage("Hi there!");

		}
		catch (XMPPException e) {

		}
		catch (SmackException.NotConnectedException e) {

		}

		initiateComponents();

		setComponentControl();
	}

	private void initiateComponents() {

	}

	private void setComponentControl() {

	}

}
