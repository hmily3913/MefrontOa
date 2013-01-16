package com.Zbh.mefrontoa;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.Zbh.mefrontoa.Util.PreferencesUtil;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MSService extends Service {

	private static final String TAG = "MSService";
	private NotificationManager _nm;
//	private Handler mHandler;
	private String curdate;
	/**执行Timer进度**/
	public final static int LOAD_PROGRESS = 0; 

	/**关闭Timer进度**/
	public final static int CLOSE_PROGRESS = 1; 
	/**Timer对象**/
	Timer mTimer = null;

	/**TimerTask对象**/
	TimerTask mTimerTask = null;

//	/**记录TimerID**/
//	int mTimerID = 0;

	@Override
	public IBinder onBind(Intent i) {
		Log.e(TAG, "============> MSService.onBind");
		return null;
	}

	public class LocalBinder extends Binder {
		MSService getService() {
			return MSService.this;
		}
	}

	@Override
	public boolean onUnbind(Intent i) {
		Log.e(TAG, "============> MSService.onUnbind");
		return false;
	}

	@Override
	public void onRebind(Intent i) {
		Log.e(TAG, "============> MSService.onRebind");
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "============> MSService.onCreate");
		_nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		StartTimer();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.e(TAG, "============> MSService.onStart");
	}

	@Override
	public void onDestroy() {
		//		_nm.cancel(R.string.service_started);
		Log.e(TAG, "============> MSService.onDestroy");
	}
	private int Notification_ID_BASE = 110;
	private void showNotification() {
		//
		SharedPreferences settings = getSharedPreferences(PreferencesUtil.SETTING_INFOS, 0);
		String UserID = settings.getString("UserID", "");
		if(UserID.equals("")||UserID.equals("0")){
//			CloseTimer();
		}else{
			postUrl posts=new postUrl();
			String ret=posts.posturl(PreferencesUtil.baseUrl+PreferencesUtil.OAUrl+"/z_Mlogin/getMessage.jsp?UserID="+UserID+"&curdate="+URLEncoder.encode(curdate));
			try {
			JSONTokener jsonParser = new JSONTokener(ret);
			JSONObject jsonObj= (JSONObject) jsonParser.nextValue();
			int totals=jsonObj.getInt("totals"); 
			JSONArray datas = jsonObj.getJSONArray("datas");
			if(totals>0){
				for(int i = 0; i < datas.length() ; i++){
			        JSONObject onedata = ((JSONObject)datas.opt(i));
					Map<String, Object> item;
					item = new HashMap<String, Object>();
					item.put("标题", onedata.getString("title"));
					curdate=onedata.getString("arrive_date");
					Notification notification = new Notification(android.R.drawable.stat_notify_sync,
							"收到一条"+onedata.getString("title")+"需处理", System.currentTimeMillis());
					
					Intent intent=new Intent(this, UsDataActivity.class);
					intent.putExtra("UserID", UserID);
					intent.putExtra("fileid", onedata.getString("fileid"));

					PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
							intent, PendingIntent.FLAG_UPDATE_CURRENT);

					// must set this for content view, or will throw a exception
					notification.setLatestEventInfo(this, "MFOA消息提醒",
							"收到一条"+onedata.getString("title")+"需处理！", contentIntent);
					//添加声音和震动
					notification.defaults = Notification.DEFAULT_ALL;

					notification.flags |=Notification.FLAG_AUTO_CANCEL; 
					_nm.notify(Notification_ID_BASE, notification);
					Notification_ID_BASE++;
				}	
			}
			
			
			} catch (JSONException ex) {  
				System.out.println("Jsons parse error !");
				ex.printStackTrace();
			}  
		}
	}

	public void StartTimer() {

		if (mTimer == null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(System.currentTimeMillis()));
			calendar.add(Calendar.HOUR_OF_DAY,8);
			curdate=formatter.format(calendar.getTime());
			mTimerTask = new TimerTask() {
				public void run() {
					//mTimerTask与mTimer执行的前提下每过10秒进一次这里
					showNotification();
//					mTimerID ++;
//					Message msg = new Message();
//					msg.what = LOAD_PROGRESS;
//					msg.arg1 = (int) (mTimerID);
//					mHandler.sendMessage(msg);
				}
			};
			mTimer = new Timer();

			//第一个参数为执行的mTimerTask
			//第二个参数为延迟的时间 这里写10000的意思是mTimerTask将延迟10秒执行
			//第三个参数为多久执行一次 这里写10000表示每10秒执行一次mTimerTask的Run方法
			mTimer.schedule(mTimerTask, 10000, 10000);
		}

	}

	public void CloseTimer() {

		//在这里关闭mTimer 与 mTimerTask
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask = null;
		}

//		/**ID重置**/
//		mTimerID = 0;

		//这里发送一条只带what空的消息
//		mHandler.sendEmptyMessage(CLOSE_PROGRESS);
	}
}
