package ke.go.nyandarua.nyantalk.fragment.password;


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
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jkb.vcedittext.VerificationCodeEditText;
import com.loopj.android.http.RequestParams;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.basecode.utils.Spanny;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.activity.BaseActivity;
import ke.go.nyandarua.nyantalk.fragment.BaseFragment;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;
import ke.go.nyandarua.nyantalk.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordResetFragment extends BaseFragment implements BlockingStep {


    @BindView(R.id.phoneInfoTV)
    TextView phoneInfoTV;
    @BindView(R.id.codeET)
    VerificationCodeEditText codeET;
    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.confirmPasswordET)
    EditText confirmPasswordET;
    Unbinder unbinder;


    //private static final long RESEND_DELAY = 30 * 1000; //30 seconds
    //private static final long RESEND_DELAY = 60 * 1000; //1 minute
    private static final long RESEND_DELAY = 60 * 5 * 1000; //5 minutes
    @BindView(R.id.notMyNumberBtn)
    Button notMyNumberBtn;
    @BindView(R.id.resendBtn)
    Button resendBtn;
    private SmsVerifyCatcher smsVerifyCatcher;
    private CountDownTimer mCountDownTimer;
    private Listener mListener;

    public PasswordResetFragment() {
        // Required empty public constructor
    }

    public static PasswordResetFragment newInstance() {

        Bundle args = new Bundle();

        PasswordResetFragment fragment = new PasswordResetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = ((Listener) context);
        } else {
            throw new IllegalArgumentException("Activity must implement this fragments listener");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_reset, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //BaseUtils.cacheInput(passwordET, R.string.password, PrefUtils.getInstance());

        setUpPhoneInfoTV();

        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                codeET.setText(code);//set code in edit text
                //then you can send verification code to server
                //onSubmitBtnClicked();
            }
        });

        //smsVerifyCatcher.setFilter(getString(R.string.app_name));
        smsVerifyCatcher.setPhoneNumberFilter(getString(R.string.app_name));

        showResendCounter();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible()){
            setUpPhoneInfoTV();
        }
    }

    private void setUpPhoneInfoTV() {
        if (phoneInfoTV != null && notMyNumberBtn != null){
            String phone = PrefUtils.getInstance().getString(R.string.pref_phone);
            Spanny guide = new Spanny(getString(R.string.guide_enter_verification_code))
                    .append(" ")
                    .append(phone, new StyleSpan(Typeface.BOLD), new UnderlineSpan());
            phoneInfoTV.setText(guide);
            int accentColor = BaseUtils.getColor(Objects.requireNonNull(getActivity()), R.attr.colorAccent);
            Spanny notMyNumber = new Spanny(phone, new ForegroundColorSpan(accentColor))
                    .append(" ").append(" is not my number!");
            notMyNumberBtn.setText(notMyNumber);
        }
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
        String password = passwordET.getText().toString();
        String confirmedPassword = confirmPasswordET.getText().toString();
        String errorMessage;

        if (TextUtils.isEmpty(password)) {
            errorMessage = getString(R.string.error_new_password);
        } else if (TextUtils.isEmpty(confirmedPassword)) {
            errorMessage = getString(R.string.error_confirm_password);
        } else if (!password.equals(confirmedPassword)) {
            errorMessage = getString(R.string.error_password_match);
        } else {
            errorMessage = null;
        }

        if (TextUtils.isEmpty(errorMessage)) {
            return null;
        } else {

            return new VerificationError(errorMessage);


        }
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Snackbar.make(passwordET, error.getErrorMessage(), Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, v -> {

                }).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        if (verifyStep() != null) {
            onError(verifyStep());
        } else {

            String url = Client.absoluteUrl(BackEnd.EndPoints.AUTH_RESET_PASSWORD);
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.CODE, codeET.getText().toString());
            params.put(BackEnd.Params.PHONE, PrefUtils.getInstance().getString(R.string.pref_phone));
            params.put(BackEnd.Params.PASSWORD, passwordET.getText().toString());
            params.put(BackEnd.Params.PASSWORD_CONFIRMATION, confirmPasswordET.getText().toString());
            Client.getInstance().getClient().post(url, params, new ResponseHandler(new VerificationCallback(callback)));
        }
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        //callback.goToPrevStep();
    }

    private class VerificationCallback extends Callback<User> {

        private StepperLayout.OnCompleteClickedCallback callback;

        VerificationCallback(StepperLayout.OnCompleteClickedCallback callback) {
            super((BaseAppActivity) getActivity(), User.class);
            this.callback = callback;

        }

        @Override
        protected void onRetry() {
            super.onRetry();
            onCompleteClicked(callback);
        }

        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            PrefUtils.getInstance().writeBoolean(R.string.pref_pending_phone_verification, false);
            PrefUtils.getInstance().saveUser(item);
            Utils.startMainActivity(getActivity());
        }
    }

    @OnClick(R.id.notMyNumberBtn)
    public void onNotMyNumberBtnClicked() {
        mCountDownTimer.cancel();
        mListener.onNotMyNumberClicked();
    }

    @OnClick(R.id.resendBtn)
    public void onResendBtnClicked() {

    }

    public interface Listener {
        void onNotMyNumberClicked();
    }
}
