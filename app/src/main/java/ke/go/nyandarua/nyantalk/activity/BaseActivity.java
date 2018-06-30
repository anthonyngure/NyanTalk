package ke.go.nyandarua.nyantalk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.go.nyandarua.nyantalk.BuildConfig;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.tasks.SignOutTask;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;

@SuppressLint("Registered")
public class BaseActivity extends BaseAppActivity {
    @Override
    public boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean isLoggedIn() {
        return ((getUser() != null));
    }

    protected User getUser() {
        return PrefUtils.getInstance().getUser();
    }

    public void signOut() {

        new AlertDialog.Builder(this)
                .setMessage(R.string.guide_sign_out)
                .setNegativeButton(R.string.continue_, (dialog, which) -> doActualSignOut())
                .setPositiveButton(android.R.string.cancel, null)
                .create().show();
    }

    private void doActualSignOut() {

        new SignOutTask(new SignOutTask.Listener() {
            @Override
            public BaseActivity getActivity() {
                return BaseActivity.this;
            }

            @Override
            public void onFinish() {
                startNewTaskActivity(new Intent(getActivity(), WelcomeActivity.class));
                BaseActivity.this.finish();
            }
        }).execute();
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }
}
