package ke.go.nyandarua.nyantalk.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.loopj.android.http.RequestParams;
import com.synnapps.carouselview.CarouselView;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.toshngure.basecode.app.ReusableFragmentActivity;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.logging.BeeLog;
import ke.go.nyandarua.nyantalk.BuildConfig;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.fragment.SignInFragment;
import ke.go.nyandarua.nyantalk.model.FacebookUser;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;
import ke.go.nyandarua.nyantalk.utils.Utils;

public class WelcomeActivity extends BaseActivity implements
        FacebookCallback<LoginResult> {

    private static final String TAG = "WelcomeActivity";
    @BindView(R.id.carouselView)
    CarouselView carouselView;
    private CallbackManager mCallbackManager;
    private static final String[] readPermissions = new String[]{"email", "public_profile"};
    LoginManager mLoginManager;
    AccessTokenTracker mAccessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isLoggedIn()){
            Utils.startMainActivity(this);
        } else  {
         init();
        }
    }


    public void init(){
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        Utils.initSlidersCarousel(carouselView);

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //updateFacebookButtonUI();
            }
        };

        mLoginManager = LoginManager.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager.registerCallback(mCallbackManager, this);

        //generateHashKey();
    }

    private void generateHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                BeeLog.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        toastDebug("Facebook Login onSuccess");
        readUserFBData();
    }

    @Override
    public void onCancel() {
        toastDebug("Facebook Login onCancel");
        hideProgressDialog();
    }

    @Override
    public void onError(FacebookException error) {
        BeeLog.e(TAG, error);
        toastDebug("Facebook Login onError " + String.valueOf(error));
        hideProgressDialog();
    }

    private void signInFacebookUser() {
        FacebookUser user = PrefUtils.getInstance().getFacebookUser();
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.EMAIL, user.getEmail());
        params.put(BackEnd.Params.FACEBOOK_ID, user.getId());
        params.put(BackEnd.Params.PICTURE_URL, user.getPicture().getData().getUrl());
        params.put(BackEnd.Params.NAME, user.getName());
        params.put(BackEnd.Params.FIRST_NAME, user.getFirstName());
        params.put(BackEnd.Params.LAST_NAME, user.getLastName());
        params.put(BackEnd.Params.GENDER, user.getGender());

        Client.getInstance().getClient()
                .post(Client.absoluteUrl(BackEnd.EndPoints.AUTH_FACEBOOK),
                        params, new ResponseHandler(new FacebookLoginCallback()));
    }


    private class FacebookLoginCallback extends Callback<User> {

        private FacebookLoginCallback() {
            super(WelcomeActivity.this, User.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            signInFacebookUser();
        }

        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            PrefUtils.getInstance().saveUser(item);
            Utils.startMainActivity(WelcomeActivity.this);
        }
    }

    private void readUserFBData() {
        Bundle params = new Bundle();
        params.putString("fields", "email,id,name,first_name,last_name,picture,age_range,gender");
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (object, response) -> {
                    BeeLog.i(TAG, String.valueOf(response));
                    FacebookUser user = BaseUtils.getSafeGson().fromJson(response.getJSONObject().toString(), FacebookUser.class);
                    BeeLog.i(TAG, String.valueOf(user));
                    PrefUtils.getInstance().saveFacebookUser(user);
                    hideProgressDialog();
                    //Connect to sign in to the api if the facebook user data exists
                    signInFacebookUser();
                });

        request.setParameters(params);
        request.executeAsync();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, WelcomeActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.facebookLoginBtn)
    public void onFacebookLoginBtnClicked() {
        mAccessTokenTracker.startTracking();
        showProgressDialog();
        mLoginManager.logInWithReadPermissions(this, Arrays.asList(readPermissions));
    }

    @OnClick(R.id.phoneLoginBtn)
    public void onPhoneLoginBtnClicked() {
        ReusableFragmentActivity.start(this, SignInFragment.newInstance(), getString(R.string.sign_in));
    }
}
