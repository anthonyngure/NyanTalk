package ke.go.nyandarua.nyantalk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.go.nyandarua.nyantalk.fragment.BlankFragment;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.fragment.TicketsFragment;
import ke.go.nyandarua.nyantalk.fragment.TopicsFragment;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private DrawerManager mDrawerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewPager.setAdapter(new SectionsAdapter());
        tabLayout.setupWithViewPager(viewPager);

        mDrawerManager = DrawerManager.init(this, getToolbar());
    }

    private class SectionsAdapter extends FragmentStatePagerAdapter {

        private SectionsAdapter() {
            super(MainActivity.this.getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TicketsFragment.newInstance();
                case 1:
                    return TopicsFragment.newInstance();
                default:
                    return BlankFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tickets";
                case 1:
                    return "Forum";
                default:
                    return super.getPageTitle(position);
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (mDrawerManager.getDrawer().isDrawerOpen()) {
            mDrawerManager.getDrawer().closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        if (viewPager.getCurrentItem() == 0){
            EditTicketActivity.start(this);
        } else  {
            EditTopicActivity.start(this);
        }
    }
}
