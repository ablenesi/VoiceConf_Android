package com.voiceconf.voiceconf.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.voiceconf.voiceconf.storage.models.User;
import com.voiceconf.voiceconf.ui.authentification.LoginActivity;
import com.voiceconf.voiceconf.ui.conference.setup.ConferenceDetailActivity;
import com.voiceconf.voiceconf.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is the main activity witch greets the user contains a tabbed layout with recent user
 * activity and friend management.
 * <p/>
 * Created by Attila Blenesi 20 Dec 2015
 * Edited by Tamas-Csaba Kadar
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    //region VARIABLES
    private static final String ADD_FRIEND_DIALOG_TAG = "add_friend";
    private FloatingActionButton mFloatingActionButton;
    private ViewPager mViewPager;
    //endregion

    //region LIFE CYCLE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar setup
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Menu drawer layout setup
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation setup
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ParseUser currentUser = User.getCurrentUser();

        // Navigation view header setup
        View header = navigationView.getHeaderView(0);
        // Load user avatar
        Glide.with(this).load(User.getAvatar(currentUser)).into((CircleImageView) header.findViewById(R.id.user_avatar));
        // Set name and email
        ((TextView) header.findViewById(R.id.user_name)).setText(currentUser.getUsername());
        ((TextView) header.findViewById(R.id.user_email)).setText(currentUser.getEmail());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_search:
                        Snackbar.make(navigationView, R.string.under_development, Snackbar.LENGTH_LONG).show();
                        return true;
                    case R.id.nav_add_friend:
                        new AddFriendDialog().show(getSupportFragmentManager(), ADD_FRIEND_DIALOG_TAG);
                        return true;
                    case R.id.nav_settings:
                        return openSettings();
                    case R.id.nav_logout:
                        return logOut();
                    default:
                        return false;
                }
            }
        });

        // Floating Action Button setup
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mViewPager.getCurrentItem()) {
                    case MainPagerAdapter.HISTORY_TAB:
                        startActivity(new Intent(MainActivity.this, ConferenceDetailActivity.class));
                        break;
                    case MainPagerAdapter.FRIENDS_TAB:
                        new AddFriendDialog().show(getSupportFragmentManager(), ADD_FRIEND_DIALOG_TAG);
                }
            }
        });

        // Tab layout and ViewPager setup
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                return openSettings();
            case R.id.action_log_out:
                return logOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region VIEW PAGER ON CHANGE
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case MainPagerAdapter.FRIENDS_TAB:
                mFloatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_person_add_white_24dp));
                break;
            case MainPagerAdapter.HISTORY_TAB:
                mFloatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_start_conference_white_24dp));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //endregion

    //region HELPER
    private boolean logOut() {
        ParseUser.logOutInBackground();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
        return true;
    }

    private boolean openSettings() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        return true;
    }
    //endregion
}
