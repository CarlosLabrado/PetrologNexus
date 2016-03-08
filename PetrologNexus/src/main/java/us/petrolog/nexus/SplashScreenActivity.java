package us.petrolog.nexus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.petrolog.nexus.rest.model.User;
import us.petrolog.nexus.rest.model.UserBody;
import us.petrolog.nexus.ui.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    @Bind(R.id.editTextEmail)
    EditText mEditTextEmail;
    @Bind(R.id.editTextPassword)
    EditText mEditTextPassword;
    @Bind(R.id.buttonLogIn)
    Button mButtonLogIn;
    @Bind(R.id.linearLayoutLoginContainer)
    LinearLayout mLinearLayoutLoginContainer;

    @Bind(R.id.progressBarSplash)
    ProgressBar mProgressBarSplash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        mLinearLayoutLoginContainer.setVisibility(View.GONE);
        mButtonLogIn.setEnabled(false);

        mProgressBarSplash.setVisibility(View.GONE);

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean isUserLoggedIn = settings.getBoolean(Constants.SP_IS_USER_LOGGED, false);
        if (isUserLoggedIn) {
            String userName = settings.getString(Constants.SP_USER_NAME, "");
            String userEmail = settings.getString(Constants.SP_USER_EMAIL, "");
            startMainActivity(userName, userEmail);
        } else {
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLinearLayoutLoginContainer.setVisibility(View.VISIBLE);
                    mButtonLogIn.setEnabled(true);
                }
            }, 500);

        }
    }

    /**
     * if there is not an user logged in then we start the process of making that, then pass the name
     * and the email to the main activity
     */
    private void startLogging() {

        String email = mEditTextEmail.getText().toString();
        final String password = mEditTextPassword.getText().toString();

        UserBody userBody = new UserBody(email, password);

        mProgressBarSplash.setVisibility(View.VISIBLE);
        mLinearLayoutLoginContainer.setVisibility(View.GONE);

        Call<User> call = FirstApp.getRestClient().getApiService().userLogin(userBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mProgressBarSplash.setVisibility(View.GONE);
                if (response.body() != null) {
                    User user = response.body();

                    String userName = user.getFirstName() + " " + user.getLastName();
                    // put the info in the shared preferences
                    SharedPreferences settings =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.SP_USER_EMAIL, user.getEmail());
                    editor.putString(Constants.SP_USER_PASSWORD, password);
                    editor.putString(Constants.SP_USER_NAME, userName);
                    editor.putBoolean(Constants.SP_IS_USER_LOGGED, true);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_NAME, userName);
                    intent.putExtra(Constants.EXTRA_USER_EMAIL, user.getEmail());
                    startActivity(intent);
                    finish();

                    Log.d(TAG, "Callback for user login successfully returned");
                } else {
                    mLinearLayoutLoginContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Callback for user login failed body is null");
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mLinearLayoutLoginContainer.setVisibility(View.VISIBLE);
                mProgressBarSplash.setVisibility(View.GONE);
                Log.e(TAG, "Callback for user login failed");
            }
        });

    }

    private void startMainActivity(final String userName, final String userEmail) {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(Constants.EXTRA_USER_NAME, userName);
                intent.putExtra(Constants.EXTRA_USER_EMAIL, userEmail);
                startActivity(intent);
                finish();
            }
        }, 1500);

    }

    @OnClick(R.id.buttonLogIn)
    public void onClick() {
        if (!mEditTextEmail.getText().toString().isEmpty() && !mEditTextPassword.getText().toString().isEmpty()) {

        } else {
            Toast.makeText(this, R.string.login_empty, Toast.LENGTH_SHORT).show();
        }
        startLogging();
    }
}
