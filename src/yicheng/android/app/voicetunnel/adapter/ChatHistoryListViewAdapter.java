package yicheng.android.app.voicetunnel.adapter;

import java.util.Date;
import java.util.List;

import yicheng.android.app.voicetunnel.R;

import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatHistoryListViewAdapter extends BaseAdapter {
	private final List<QBChatMessage> chatMessages;
	private Context context;

	private SharedPreferences local_user_information;
	private SharedPreferences.Editor local_user_editor;
	private String PREFS_NAME = "LocalUserInfo";

	public ChatHistoryListViewAdapter(Context context,
			List<QBChatMessage> chatMessages) {
		this.context = context;
		this.chatMessages = chatMessages;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (this.chatMessages != null) {
			return this.chatMessages.size();
		}
		else {
			return 0;
		}
	}

	@Override
	public QBChatMessage getItem(int position) {
		// TODO Auto-generated method stub
		if (this.chatMessages != null) {
			return this.chatMessages.get(position);
		}
		else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void add(QBChatMessage message) {
		chatMessages.add(message);
	}

	public void add(List<QBChatMessage> messages) {
		chatMessages.addAll(messages);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MessageViewHolder holder;
		QBChatMessage chatMessage = getItem(position);
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = vi.inflate(
					R.layout.chat_history_listview_listitem_layout, null);
			holder = createViewHolder(convertView);
			convertView.setTag(holder);

		

		}
		else {
			holder = (MessageViewHolder) convertView.getTag();
		}

		local_user_information = this.context.getSharedPreferences(PREFS_NAME,
				0);
		Integer user_id = local_user_information.getInt("user_id", 0);

		boolean isOutgoing = chatMessage.getSenderId() == null
				|| chatMessage.getSenderId().equals(user_id);

		setMessageLayoutAlignment(holder, isOutgoing);

		// TODO Auto-generated method stub

		holder.message_textView.setText(chatMessage.getBody());
		if (chatMessage.getSenderId() != null) {
			holder.message_time_textView.setText(chatMessage.getSenderId()
					+ ": " + getMessageTimeText(chatMessage));
		}
		else {
			holder.message_time_textView
					.setText(getMessageTimeText(chatMessage));
		}

		return convertView;
	}

	private void setMessageLayoutAlignment(MessageViewHolder holder,
			boolean isOutgoing) {
		if (!isOutgoing) {
			holder.message_content_background_layout
					.setBackgroundResource(R.drawable.incoming_message_background);

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.message_content_background_layout
					.getLayoutParams();
			layoutParams.gravity = Gravity.RIGHT;
			holder.message_content_background_layout
					.setLayoutParams(layoutParams);

			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.message_background_layout
					.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp.setMargins(0, 0, 20, 0);
			holder.message_background_layout.setLayoutParams(lp);
			layoutParams = (LinearLayout.LayoutParams) holder.message_textView
					.getLayoutParams();
			
			layoutParams.gravity = Gravity.RIGHT;
			holder.message_textView.setLayoutParams(layoutParams);

			layoutParams = (LinearLayout.LayoutParams) holder.message_time_textView
					.getLayoutParams();
			layoutParams.gravity = Gravity.RIGHT;
			holder.message_time_textView.setLayoutParams(layoutParams);
		}
		else {
			holder.message_content_background_layout
					.setBackgroundResource(R.drawable.outgoing_message_background);

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.message_content_background_layout
					.getLayoutParams();
			layoutParams.gravity = Gravity.LEFT;
			holder.message_content_background_layout
					.setLayoutParams(layoutParams);
			

			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.message_background_layout
					.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			lp.setMargins(20, 0, 0, 0);
			holder.message_background_layout.setLayoutParams(lp);
			layoutParams = (LinearLayout.LayoutParams) holder.message_textView
					.getLayoutParams();
			layoutParams.gravity = Gravity.LEFT;
			holder.message_textView.setLayoutParams(layoutParams);

			layoutParams = (LinearLayout.LayoutParams) holder.message_time_textView
					.getLayoutParams();
			layoutParams.gravity = Gravity.LEFT;
			holder.message_time_textView.setLayoutParams(layoutParams);
		}
	}

	private MessageViewHolder createViewHolder(View v) {
		MessageViewHolder holder = new MessageViewHolder();
		holder.message_textView = (TextView) v
				.findViewById(R.id.message_textView);
		holder.message_time_textView = (TextView) v
				.findViewById(R.id.message_time_textView);
		holder.message_background_layout = (LinearLayout) v
				.findViewById(R.id.message_background_layout);
		holder.message_content_background_layout = (LinearLayout) v
				.findViewById(R.id.message_content_background_layout);

		return holder;
	}

	private String getMessageTimeText(QBChatMessage message) {
		return millisToLongDHMS(message.getDateSent() * 1000);
	}

	private static String millisToLongDHMS(long duration) {
		if (duration > 0) {
			duration = new Date().getTime() - duration;
		}
		if (duration < 0) {
			duration = 0;
		}

		StringBuffer res = new StringBuffer();
		long temp = 0;
		if (duration >= MessageViewHolder.ONE_SECOND) {
			temp = duration / MessageViewHolder.ONE_DAY;
			if (temp > 0) {
				duration -= temp * MessageViewHolder.ONE_DAY;
				res.append(temp)
						.append(" day")
						.append(temp > 1 ? "s" : "")
						.append(duration >= MessageViewHolder.ONE_MINUTE ? ", "
								: "");
			}

			temp = duration / MessageViewHolder.ONE_HOUR;
			if (temp > 0) {
				duration -= temp * MessageViewHolder.ONE_HOUR;
				res.append(temp)
						.append(" hour")
						.append(temp > 1 ? "s" : "")
						.append(duration >= MessageViewHolder.ONE_MINUTE ? ", "
								: "");
			}

			temp = duration / MessageViewHolder.ONE_MINUTE;
			if (temp > 0) {
				duration -= temp * MessageViewHolder.ONE_MINUTE;
				res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
			}

			if (!res.toString().equals("")
					&& duration >= MessageViewHolder.ONE_SECOND) {
				res.append(" and ");
			}

			temp = duration / MessageViewHolder.ONE_SECOND;
			if (temp > 0) {
				res.append(temp).append(" second").append(temp > 1 ? "s" : "");
			}
			res.append(" ago");
			return res.toString();
		}
		else {
			return "0 second ago";
		}
	}

	private static class MessageViewHolder {
		public final static long ONE_SECOND = 1000;
		public final static long SECONDS = 60;

		public final static long ONE_MINUTE = ONE_SECOND * 60;
		public final static long MINUTES = 60;

		public final static long ONE_HOUR = ONE_MINUTE * 60;
		public final static long HOURS = 24;

		public final static long ONE_DAY = ONE_HOUR * 24;

		public TextView message_textView;
		public TextView message_time_textView;
		public LinearLayout message_background_layout;
		public LinearLayout message_content_background_layout;
	}

}
