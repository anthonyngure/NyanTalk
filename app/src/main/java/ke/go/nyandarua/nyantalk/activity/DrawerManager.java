package ke.go.nyandarua.nyantalk.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import ke.co.toshngure.basecode.app.ReusableFragmentActivity;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.fragment.ProfileEditFragment;
import ke.go.nyandarua.nyantalk.fragment.password.ChangePasswordFragment;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.Utils;


/**
 * Created by Anthony Ngure on 06/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class DrawerManager implements Drawer.OnDrawerItemClickListener {

    private BaseActivity mContext;
    private Drawer mDrawer;

    private DrawerManager(BaseActivity activity, Toolbar toolbar) {
        this.mContext = activity;
        init(toolbar);
    }

    public static DrawerManager init(BaseActivity activity, Toolbar toolbar) {
        return new DrawerManager(activity, toolbar);
    }

    private void init(Toolbar toolbar) {

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                super.set(imageView, uri, placeholder);
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                super.cancel(imageView);
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText("").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()
                return super.placeholder(ctx, tag);
            }
        });


        User user = mContext.getUser();
        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
                //.withHeaderBackground(new ImageHolder(BackEnd.coverUrl(user.getDp())))
                .withCloseDrawerOnProfileListClick(false)
                .withNameTypeface(Typeface.DEFAULT_BOLD)
                .withHeaderBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorPrimary)))
                .withTextColor(Color.WHITE)
                .withActivity(mContext)
                .withThreeSmallProfileImages(true)
                .withAlternativeProfileHeaderSwitching(true)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .withPaddingBelowHeader(true)
                .withProfileImagesClickable(true)
                .withSelectionListEnabled(true)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        //UserActivity.start(mContext, mContext.getUser());
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
                .withCloseDrawerOnProfileListClick(false);

        //Set up user
        ProfileDrawerItem userProfileDrawerItem = new ProfileDrawerItem()
                .withTextColorRes(R.color.colorAccent)
                .withNameShown(true)
                .withName(user.getName());

        //Setup avatar to show
        if (!TextUtils.isEmpty(user.getFacebookPictureUrl())) {
            userProfileDrawerItem.withIcon(user.getFacebookPictureUrl());
        } else {
            userProfileDrawerItem.withIcon(BackEnd.image(user.getAvatar()));
        }


        /*Set up email to show for user*/
        if (!TextUtils.isEmpty(user.getEmail())) {
            userProfileDrawerItem.withEmail(user.getEmail());
        } else if (!TextUtils.isEmpty(user.getPhone())) {
            userProfileDrawerItem.withEmail(user.getPhone());
        } else if (!TextUtils.isEmpty(user.getFacebookId())) {
            userProfileDrawerItem.withEmail(user.getFacebookPictureUrl());
        } else {
            userProfileDrawerItem.withEmail(String.valueOf(user.getId()));
        }

        accountHeaderBuilder.addProfiles(userProfileDrawerItem);

        DrawerBuilder drawerBuilder = new DrawerBuilder().withActivity(mContext)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(true)
                .withAccountHeader(accountHeaderBuilder.build())
                .withHeaderPadding(true)
                .withCloseOnClick(true)
                .withActionBarDrawerToggle(true)
                .withOnDrawerItemClickListener(this)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home).withIcon(R.drawable.ic_home_black_24dp),
                        new PrimaryDrawerItem().withName(R.string.update_profile)
                                .withIcon(R.drawable.ic_account_circle_black_24dp)
                                .withIdentifier(R.id.update_profile)
                );

        //If user did not sign in with facebook, the user can change password
        if (TextUtils.isEmpty(user.getFacebookId())) {
            drawerBuilder.addDrawerItems(
                    new PrimaryDrawerItem().withName(R.string.change_password)
                            .withIcon(R.drawable.ic_vpn_key_black_24dp)
                            .withIdentifier(R.id.change_password)
            );
        }
        drawerBuilder.addDrawerItems(
                new PrimaryDrawerItem().withName(R.string.privacy_policy)
                        .withIcon(R.drawable.ic_security_black_24dp)
                        .withIdentifier(R.id.menu_privacy_policy),
                new PrimaryDrawerItem().withName(R.string.terms_of_use)
                        .withIcon(R.drawable.ic_verified_user_black_24dp)
                        .withIdentifier(R.id.menu_terms_of_use),
                new PrimaryDrawerItem().withName(R.string.title_activity_about)
                        .withIcon(R.drawable.ic_info_black_24dp)
                        .withIdentifier(R.id.menu_about),
                new PrimaryDrawerItem().withName(R.string.share)
                        .withIcon(R.drawable.ic_share_black_24dp)
                        .withIdentifier(R.id.action_share)
        );

        drawerBuilder.addStickyDrawerItems(
                new PrimaryDrawerItem().withName(R.string.sign_out)
                        .withIcon(R.drawable.ic_sign_out_black_24dp)
                        .withIdentifier(R.id.menu_sign_out),
                new DividerDrawerItem(),
                new SecondaryDrawerItem().withName(R.string.copyright)
                        .withIcon(R.drawable.ic_copyright_black_24dp)
                        .withIdentifier(R.id.menu_copyright)
        );
        mDrawer = drawerBuilder.build();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mDrawer.getHeader().setPadding(0, 24, 0, 0);
        }
    }

    public Drawer getDrawer() {
        return mDrawer;
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        if (drawerItem.getIdentifier() == R.id.menu_sign_out) {
            mContext.signOut();
            return true;
        } else if (drawerItem.getIdentifier() == R.id.update_profile) {
            ReusableFragmentActivity.start(mContext, ProfileEditFragment.newInstance(),
                    mContext.getString(R.string.update_profile));
            return true;
        } else if (drawerItem.getIdentifier() == R.id.menu_about) {
            AboutActivity.start(mContext);
            return true;
        } else if (drawerItem.getIdentifier() == R.id.menu_privacy_policy) {
            Utils.openWebPage(mContext, mContext.getString(R.string.county_privacy_policy_url));
            return true;
        } else if (drawerItem.getIdentifier() == R.id.menu_terms_of_use) {
            Utils.openWebPage(mContext, mContext.getString(R.string.county_terms_of_use_url));
            return true;
        }
        else if (drawerItem.getIdentifier() == R.id.menu_copyright) {
            Utils.openWebPage(mContext, mContext.getString(R.string.county_website_url));
            return true;
        }
        else if (drawerItem.getIdentifier() == R.id.change_password) {
            ReusableFragmentActivity.start(mContext, ChangePasswordFragment.newInstance(),
                    mContext.getString(R.string.change_password));
        } else if (drawerItem.getIdentifier() == R.id.action_share) {
            BaseUtils.shareText(mContext, mContext.getString(R.string.share_text));
        }

        return false;
    }
}
