package com.Zbh.mefrontoa;

import com.Zbh.mefrontoa.Util.PreferencesUtil;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
@SuppressLint("ShowToast")
public class LoginActivity extends Activity {
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUserName;
	private String mPassword;

	// UI references.
	private EditText mUserNameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		SharedPreferences settings = getSharedPreferences(PreferencesUtil.SETTING_INFOS, 0);
		String name = settings.getString("NAME", "");
		String password = settings.getString("PASSWORD", "");
		boolean SPWD=settings.getBoolean("SPWD", true);
		String UserID = settings.getString("UserID", "");
		if(!UserID.equals("")&&!UserID.equals("0")){
			LoginProcess(UserID);
		}else{
			// Set up the login form.
			mUserNameView = (EditText) findViewById(R.id.username);

			mPasswordView = (EditText) findViewById(R.id.password);
			mPasswordView
			.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView textView, int id,
						KeyEvent keyEvent) {
					if (id == R.id.login || id == EditorInfo.IME_NULL) {
						attemptLogin();
						return true;
					}
					return false;
				}
			});

			mLoginFormView = findViewById(R.id.login_form);
			mLoginStatusView = findViewById(R.id.login_status);
			mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

			//Set value
			mUserNameView.setText(name);
			mPasswordView.setText(password);
			CheckBox c_savepwd=(CheckBox) findViewById(R.id.c_savepwd);
			c_savepwd.setChecked(SPWD);
			findViewById(R.id.sign_in_button).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							attemptLogin();
						}
					});

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUserNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserName = mUserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} /*else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}*/

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserName)) {
			mUserNameView.setError(getString(R.string.error_field_required));
			focusView = mUserNameView;
			cancel = true;
		} 

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return null;
			}
			postUrl posts=new postUrl();
			String ret=posts.posturl(PreferencesUtil.baseUrl+PreferencesUtil.OAUrl+"/z_Mlogin/check.jsp?username="+mUserName+"&password="+mPassword);
			return ret;
		}

		@Override
		protected void onPostExecute(final String success) {
			mAuthTask = null;
			showProgress(false);

			if (!success.equals("0")) {
				Toast.makeText(LoginActivity.this, "登录成功，正在获取用户数据……", 0).show();
				//保存帐号密码
				CheckBox c_savepwd=(CheckBox) findViewById(R.id.c_savepwd);
				SharedPreferences settings = getSharedPreferences(PreferencesUtil.SETTING_INFOS, 0);
				if (c_savepwd.isChecked()){
					settings.edit()
					.putString("NAME", mUserName)
					.putString("PASSWORD", mPassword)
					.putBoolean("SPWD", true)
					.putString("UserID", success)
					.commit();
				}else
					settings.edit().clear().commit();
				LoginProcess(success);
			} else {
				mPasswordView
				.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}
		//创建服务连接
		private MSService _boundService;
		private ServiceConnection _connection = new ServiceConnection() {  
			public void onServiceConnected(ComponentName className, IBinder service) {           
				_boundService = ((MSService.LocalBinder)service).getService();  

				Toast.makeText(LoginActivity.this, "Service connected",  
						Toast.LENGTH_SHORT).show();  
			}  

			public void onServiceDisconnected(ComponentName className) {  
				// unexpectedly disconnected,we should never see this happen.  
				_boundService = null;  
				Toast.makeText(LoginActivity.this, "Service connected",  
						Toast.LENGTH_SHORT).show();  
			}  
		};  

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	private void LoginProcess(String Userid){
		Intent intent = new Intent(LoginActivity.this, UsDataActivity.class);
		intent.putExtra("UserID", Userid);
		startActivity(intent);
		Intent intentS = new Intent(LoginActivity.this, MSService.class);
		//		intentS.putExtra("UserID", success);
		startService(intentS);
		finish();
	}
}
