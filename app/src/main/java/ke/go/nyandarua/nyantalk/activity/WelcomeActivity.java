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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.RequestParams;
import com.synnapps.carouselview.CarouselView;

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
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 100;
    @BindView(R.id.carouselView)
    CarouselView carouselView;
    @BindView(R.id.googleLoginBtn)
    SignInButton googleLoginBtn;
    private CallbackManager mCallbackManager;
    private static final String[] readPermissions = new String[]{"email", "public_profile"};
    LoginManager mLoginManager;
    AccessTokenTracker mAccessTokenTracker;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isLoggedIn()) {
            Utils.startMainActivity(this);
        } else {
            init();
        }
    }


    public void init() {
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


        googleLoginBtn.setSize(SignInButton.SIZE_STANDARD);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        /*GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        authenticateWithGoogle(account);*/

        //generateHashKey();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            authenticateWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            BeeLog.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            authenticateWithGoogle(null);
        }
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

    private void authenticateWithGoogle(GoogleSignInAccount account) {
        BeeLog.i(TAG, account);
        if (account != null){
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.GOOGLE_ID, account.getId());
            params.put(BackEnd.Params.EMAIL, account.getEmail());
            params.put(BackEnd.Params.NAME, account.getDisplayName());
            params.put(BackEnd.Params.PHOTO_URL, account.getPhotoUrl());
            BeeLog.i(TAG, params);
            String url = Client.absoluteUrl(BackEnd.EndPoints.AUTH_GOOGLE);
            Client.getInstance().getClient().post(url,params, new ResponseHandler(new GoogleLoginCallback(account)));
        }

        //account.getDisplayName();
    }

    private class GoogleLoginCallback extends Callback<User> {

        private GoogleSignInAccount account;

        private GoogleLoginCallback(GoogleSignInAccount account) {
            super(WelcomeActivity.this, User.class);
            this.account = account;
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            authenticateWithGoogle(this.account);
        }

        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            PrefUtils.getInstance().saveUser(item);
            Utils.startMainActivity(WelcomeActivity.this);
        }
    }

    @OnClick(R.id.googleLoginBtn)
    public void onGoogleLoginBtnClicked() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
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
