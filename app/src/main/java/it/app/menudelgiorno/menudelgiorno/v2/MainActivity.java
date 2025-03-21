package it.app.menudelgiorno.menudelgiorno.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import it.app.menudelgiorno.menudelgiorno.v2.facebooklogin.FacebookLogin;
import it.app.menudelgiorno.menudelgiorno.v2.facebooklogin.FacebookLoginModern;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentDetailLocali;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentDetailMenu;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentLogin;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentMenuDelGiorno;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentOffertaGiorno;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentOrganizzaPranzo;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentPreferiti;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentSearch;
import it.app.menudelgiorno.menudelgiorno.v2.googlelogin.GoogleLogin;
import it.app.menudelgiorno.menudelgiorno.v2.googlemaps.FragmentMaps;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private Fragment fragment = null;
    private FragmentManager fragmentManager;

    private Activity context;

    private final Handler handler = new MainHandler(this);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookLoginModern.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        TypedArray navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        // navDrawerItems.add(new NavDrawerItem(getResources().getString(
        // R.string.offerta), navMenuIcons.getResourceId(3, -1), true,
        // "22"));
        navDrawerItems.add(new NavDrawerItem(getResources().getString(
                R.string.offerta), navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(getResources().getString(
                R.string.mappa), navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(getResources().getString(
                R.string.preferiti), navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(getResources().getString(
                R.string.organizza), navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(getResources().getString(
                R.string.login), navMenuIcons.getResourceId(4, -1)));

        navMenuIcons.recycle();
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open
        ) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        fragment = new FragmentOffertaGiorno();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack(null)
                .commit();

        // setta il titolo dei fragment
        fragmentManager
                .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

                    @Override
                    public void onBackStackChanged() {
                        Fragment f = fragmentManager
                                .findFragmentById(R.id.content_frame);

                        if (f != null) {
                            String fragClassName = f.getClass().getName();
                            if (fragClassName.equals(FragmentLogin.class
                                    .getName())) {
                                setTitle(R.string.login);
                            }

                            if (fragClassName
                                    .equals(FragmentOffertaGiorno.class
                                            .getName())) {
                                setTitle(R.string.offerta);
                            }

                            if (fragClassName
                                    .equals(FragmentOrganizzaPranzo.class
                                            .getName())) {
                                setTitle(R.string.organizza);
                            }

                            if (fragClassName.equals(FragmentPreferiti.class
                                    .getName())) {
                                setTitle(R.string.preferiti);
                            }

                            if (fragClassName.equals(FragmentSearch.class
                                    .getName())) {
                                setTitle(R.string.cerca);
                            }

                            if (fragClassName.equals(FragmentMaps.class
                                    .getName())) {
                                setTitle(R.string.mappa);
                            }

                            if (fragClassName
                                    .equals(FragmentMenuDelGiorno.class
                                            .getName())) {
                                setTitle(R.string.app_name);
                            }

                            if (fragClassName.equals(FragmentDetailLocali.class
                                    .getName())) {
                                setTitle(R.string.dettaglio_locale);
                            }

                            if (fragClassName.equals(FragmentDetailMenu.class
                                    .getName())) {
                                setTitle(R.string.dettaglio_menu);
                            }

                        }
                    }
                });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        if (Utility.isThereUser(this))
            menu.findItem(R.id.action_login).setIcon(R.drawable.logout);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Fragment f = fragmentManager.findFragmentById(R.id.content_frame);
            String fragClassName = f.getClass().getName();
            if (f != null) {
                if ((fragClassName.equals(FragmentLogin.class.getName())
                        || fragClassName.equals(FacebookLogin.class.getName()) || fragClassName
                        .equals(GoogleLogin.class.getName()))
                        && position == 4) {

                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    mDrawerLayout.closeDrawer(mDrawerList);

                    return;
                }
            }

            displayView(position);
        }
    }

    private void displayView(int position) {

        switch (position) {
            case 0:
                fragment = new FragmentOffertaGiorno();

                break;
            case 1:
                fragment = new FragmentMaps();
                break;
            case 2:
                fragment = new FragmentPreferiti();
                break;
            case 3:
                fragment = new FragmentOrganizzaPranzo();
                break;
            case 4:
                fragment = new FragmentLogin();
                break;

            default:
                break;
        }

        if (fragment != null) {

            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).addToBackStack(null)
                    .commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_websearch) {
            fragment = new FragmentSearch();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).addToBackStack(null)
                    .commit();

        } else if (item.getItemId() == R.id.action_login) {

            if (Utility.isThereUser(this)) {

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.AVVISO_LOGOUT)
                        .setMessage(R.string.VERIFICA_LOGOUT)
                        .setPositiveButton(R.string.OK,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        SharedPreferences settings = getApplicationContext()
                                                .getSharedPreferences("login",
                                                        0);
                                        SharedPreferences.Editor editor = settings
                                                .edit();
                                        editor.clear().commit();

                                        fragmentManager
                                                .beginTransaction()
                                                .replace(R.id.content_frame,
                                                        new FragmentLogin())
                                                .addToBackStack(null).commit();

                                        invalidateOptionsMenu();
                                    }

                                }).setNegativeButton(R.string.cancel, null)
                        .show();

            } else {

                // final CharSequence[] items = { "Facebook", "Google+" };
                final CharSequence[] items = {"Facebook"};
                AlertDialog.Builder builder3 = new AlertDialog.Builder(
                        MainActivity.this);
                builder3.setTitle(R.string.login_choice).setItems(items,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (which == 0) {
                                    // FACEBOOK LOGIN
                                    FacebookLoginModern.handler = handler;
                                    FacebookLoginModern.login(context);

                                } else if (which == 1) {
                                    // GOOGLE+ LOGIN
                                    GoogleLogin.handler = handler;
                                    fragmentManager
                                            .beginTransaction()
                                            .replace(R.id.content_frame,
                                                    new GoogleLogin())
                                            .addToBackStack(null).commit();
                                }
                            }
                        });

                builder3.show();
            }
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            invalidateOptionsMenu();

        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(R.string.menudelgiornoExit);
            alert.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            finish();

                        }
                    });
            alert.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            dialog.cancel();
                        }
                    });

            alert.show();
        }
    }

    private class MainHandler extends Handler {
        private final WeakReference<MainActivity> activityRef;

        public MainHandler(MainActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = activityRef.get();
            if (activity != null) {
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new FragmentLogin())
                        .addToBackStack(null).commit();
                activity.invalidateOptionsMenu();
            }
        }
    }

}

