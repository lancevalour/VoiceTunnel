package yicheng.android.app.voicetunnel;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class StartingViewActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.starting_view_activity_layout);
		
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	

		Handler handler = new Handler(); 
		handler.postDelayed(new Runnable() { 
			public void run() { 
				Intent main_view_intent = new Intent("yicheng.redder.LOGINACTIVITY");
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				startActivity(main_view_intent);

			} 
		}, 1500); 
		
		
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();

	}

}