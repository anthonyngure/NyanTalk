package ke.go.nyandarua.nyantalk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.fragment.password.PasswordRecoveryFragment;
import ke.go.nyandarua.nyantalk.fragment.password.PasswordResetFragment;

public class RecoverPasswordActivity extends BaseActivity
        implements StepperLayout.StepperListener, PasswordResetFragment.Listener {

    @BindView(R.id.stepperLayout)
    StepperLayout stepperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);
        ButterKnife.bind(this);

        stepperLayout.setAdapter(new MyStepperAdapter());
        stepperLayout.setListener(this);
    }


    public static void start(Context context) {
        Intent starter = new Intent(context, RecoverPasswordActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCompleted(View completeButton) {
        toastDebug("Password recovery completed!");
    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {

    }

    @Override
    public void onReturn() {

    }

    @Override
    public void onNotMyNumberClicked() {
        stepperLayout.setCurrentStepPosition(0);
    }

    private class MyStepperAdapter extends AbstractFragmentStepAdapter {

        MyStepperAdapter() {
            super(RecoverPasswordActivity.this.getSupportFragmentManager(), RecoverPasswordActivity.this);
        }

        @Override
        public Step createStep(int position) {
            switch (position){
                case 0:
                    return PasswordRecoveryFragment.newInstance();
                case 1:
                    return PasswordResetFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
