package ke.go.nyandarua.nyantalk.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkb.vcedittext.VerificationCodeEditText;
import com.loopj.android.http.RequestParams;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.app.ReusableFragmentActivity;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.basecode.utils.Spanny;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.activity.BaseActivity;
import ke.go.nyandarua.nyantalk.activity.MainActivity;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;
import ke.go.nyandarua.nyantalk.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class CodeVerificationFragment extends Fragment implements Step {


    @BindView(R.id.infoTV)
    TextView infoTV;
    @BindView(R.id.codeET)
    VerificationCodeEditText codeET;
    @BindView(R.id.notMyNumberBtn)
    Button notMyNumberBtn;
    @BindView(R.id.resendBtn)
    Button resendBtn;
    Unbinder unbinder;

    //private static final long RESEND_DELAY = 30 * 1000; //30 seconds
    private static final long RESEND_DELAY = 60 * 1000; //1 minute
    //private static final long RESEND_DELAY = 60 * 5 * 1000; //5 minutes
    @BindView(R.id.rootView)
    RelativeLayout rootView;
    private SmsVerifyCatcher smsVerifyCatcher;
    private CountDownTimer mCountDownTimer;

    public CodeVerificationFragment() {
        // Required empty public constructor
    }


    public static CodeVerificationFragment newInstance() {

        Bundle args = new Bundle();

        CodeVerificationFragment fragment = new CodeVerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_code_verification, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(codeET, InputMethodManager.SHOW_FORCED);
        }

        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), message -> {
            String code = parseCode(message);//Parse verification code
            codeET.setText(code);//set code in edit text
            //then you can send verification code to server
            onSubmitBtnClicked();
        });

        //smsVerifyCatcher.setFilter(getString(R.string.app_name));
        smsVerifyCatcher.setPhoneNumberFilter(getString(R.string.app_name));

        if (infoTV != null && notMyNumberBtn != null){
            String phone = PrefUtils.getInstance().getString(R.string.pref_phone);
            Spanny guide = new Spanny(getString(R.string.guide_enter_verification_code))
                    .append(" ")
                    .append(phone, new StyleSpan(Typeface.BOLD), new UnderlineSpan());
            infoTV.setText(guide);
            int accentColor = BaseUtils.getColor(Objects.requireNonNull(getActivity()), R.attr.colorAccent);
            Spanny notMyNumber = new Spanny(phone, new ForegroundColorSpan(accentColor))
                    .append(" ").append(" is not my number!");
            notMyNumberBtn.setText(notMyNumber);

        }

        showResendCounter();

    }



    private String parseCode(String message) {
        String code = "";
        String[] words = message.split("\\s+");
        for (String word1 : words) {
            // You may want to check for a non-word character before blindly
            // performing a replacement
            // It may also be necessary to adjust the character class
            String word = word1.replaceAll("[^\\w]", "");
            if (word.length() == 4 && TextUtils.isDigitsOnly(word)) {
                code = word;
                break;
            }
        }
        return code;
    }

    @Override
    public void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCountDownTimer.cancel();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showResendCounter() {

        resendBtn.setEnabled(false);

        mCountDownTimer = new CountDownTimer(RESEND_DELAY, 1000) {

            /**
             * Callback fired on regular interval.
             *
             * @param millisUntilFinished The amount of time until finished.
             */
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                long remainingSeconds = millisUntilFinished / 1000;
                if (remainingSeconds > 60) {
                    int minutes = (int) (remainingSeconds / 60);
                    int seconds = (int) (remainingSeconds - (60 * minutes));
                    resendBtn.setText("Resend code in " + minutes + " min " + seconds + " secs");
                } else {
                    resendBtn.setText("Resend code in " + remainingSeconds + " secs");
                }
            }

            /**
             * Callback fired when the time is up.
             */
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                resendBtn.setText(new Spanny("Did\'t a receive code ? Tap resend"));
                resendBtn.setEnabled(true);
            }
        }.start();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCountDownTimer.cancel();
        unbinder.unbind();
    }

    @OnClick(R.id.notMyNumberBtn)
    public void onNotMyNumberBtnClicked() {
        mCountDownTimer.cancel();
        PrefUtils.getInstance().remove(R.string.pref_phone);
        PrefUtils.getInstance().remove(R.string.pref_pending_phone_verification);
        ReusableFragmentActivity.start((AppCompatActivity) Objects.requireNonNull(getActivity()),
                SignInFragment.newInstance(), getString(R.string.sign_in));
    }

    @OnClick(R.id.resendBtn)
    public void onResendBtnClicked() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.AUTH_CODE);
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.PHONE, PrefUtils.getInstance().getString(R.string.pref_phone));
        Client.getInstance().getClient().get(url,params, new ResponseHandler(new ResendCodeCallback()));
    }

    private class ResendCodeCallback extends Callback<User> {

        private ResendCodeCallback() {
            super((BaseAppActivity) getActivity(), User.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            onResendBtnClicked();
        }


        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            PrefUtils.getInstance().writeString(R.string.pref_phone, item.getPhone());
            PrefUtils.getInstance().writeBoolean(R.string.pref_pending_phone_verification, true);
            showResendCounter();
        }
    }

    @OnClick(R.id.submitBtn)
    public void onSubmitBtnClicked() {
        if (codeET.getText().toString().length() == 4){
            String url = Client.absoluteUrl(BackEnd.EndPoints.AUTH_VERIFICATION);
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.CODE, codeET.getText().toString());
            params.put(BackEnd.Params.PHONE, PrefUtils.getInstance().getString(R.string.pref_phone));
            Client.getInstance().getClient().post(url, params, new ResponseHandler(new VerificationCallback()));
        } else {
            Snackbar.make(codeET, "Please enter a your code to proceed!", Snackbar.LENGTH_LONG).show();
        }
    }

    private class VerificationCallback extends Callback<User> {

        VerificationCallback() {
            super((BaseAppActivity) getActivity(), User.class);
        }

        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            PrefUtils.getInstance().writeBoolean(R.string.pref_pending_phone_verification, false);
            PrefUtils.getInstance().saveUser(item);
            Utils.startMainActivity(getActivity());
        }
    }
}
