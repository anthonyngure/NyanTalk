package ke.go.nyandarua.nyantalk.tasks;

import android.app.Activity;
import android.text.TextUtils;

import ke.co.toshngure.basecode.app.DialogAsyncTask;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.activity.BaseActivity;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;
import ke.go.nyandarua.nyantalk.utils.Utils;

/**
 * Created by Anthony Ngure on 11/04/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class SignOutTask extends DialogAsyncTask<Void, Void, Void> {

    private Listener listener;

    public interface Listener {
        BaseActivity getActivity();

        void onFinish();
    }

    public SignOutTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected Activity getActivity() {
        return listener.getActivity();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        /*try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        User user = PrefUtils.getInstance().getUser();


        /*Cached values*/
        String name = PrefUtils.getInstance().getString(R.string.pref_name);
        String phone = PrefUtils.getInstance().getString(R.string.pref_phone);
        String email = PrefUtils.getInstance().getString(R.string.pref_email);

        String password = PrefUtils.getInstance().getString(R.string.pref_password);

        PrefUtils.getInstance().signOut();
        //Database.getInstance().clean();

        PrefUtils.getInstance().writeString(R.string.pref_email, TextUtils.isEmpty(user.getEmail()) ? email : user.getEmail());
        PrefUtils.getInstance().writeString(R.string.pref_phone, TextUtils.isEmpty(user.getPhone()) ? phone : Utils.removePhoneCode(user.getPhone()));
        PrefUtils.getInstance().writeString(R.string.pref_name, TextUtils.isEmpty(user.getName()) ? name : user.getName());
        PrefUtils.getInstance().writeString(R.string.pref_password, password);
        PrefUtils.getInstance().writeBoolean(R.string.pref_pending_phone_verification, false);
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onFinish();
    }
}
