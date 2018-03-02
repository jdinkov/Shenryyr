package com.wordpress.dnvsoft.android.shenryyr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetChannelId;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.menus.MissingServiceMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button navSignIn;
    private Button navSignOut;
    private ImageView navGoogleImage;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LatestVideosFragment videosFragment = new LatestVideosFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, videosFragment);
        fragmentTransaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        navSignIn = (Button) findViewById(R.id.navSignIn);
        navSignOut = (Button) findViewById(R.id.navSignOut);
        navGoogleImage = (ImageView) findViewById(R.id.navGoogleImage);
        navSignIn.setOnClickListener(onClickListener);
        navSignOut.setOnClickListener(onClickListener);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = isSignedIn();
        updateSignInUI(account);
    }

    private GoogleSignInAccount isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navSignIn:
                    signIn();
                    break;
                case R.id.navSignOut:
                    onSignOutMenu();
                    break;
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer != null) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    };

    private void signIn() {
        if (isSignedIn() == null) {
            if (Network.IsDeviceOnline(MainActivity.this)) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            } else {
                Toast.makeText(MainActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateSignInUI(null);
                    }
                });

        SharedPreferences.Editor editor =
                getSharedPreferences("CHANNEL_ID_PREFERENCES", MODE_PRIVATE).edit();
        editor.remove("CHANNEL_ID");
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void onSignOutMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.sign_out_menu_title);
        builder.setMessage(R.string.sign_out_menu_message);

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOut();
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        if (id == R.id.nav_youtube) {
            fragment = new LatestVideosFragment();
        } else if (id == R.id.nav_library) {
            fragment = new PlayListsFragment();
        } else if (id == R.id.nav_search) {
            fragment = new SearchFragment();
        } else if (id == R.id.nav_twitch) {
            if (isPackageInstalled()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YoutubeInfo.TWITCH_URI)));
            } else {
                MissingServiceMenu serviceMenu = new MissingServiceMenu(MainActivity.this,
                        getString(R.string.get_twitch_dialog), YoutubeInfo.TWITCH_PACKAGE);
                serviceMenu.ShowDialog();
            }
        } else if (id == R.id.nav_about) {
            fragment = new AboutFragment();
        }

        if (fragment != null) {
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private boolean isPackageInstalled() {
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            pm.getPackageInfo(YoutubeInfo.TWITCH_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void updateSignInUI(GoogleSignInAccount account) {
        if (account != null) {
            navSignIn.setText(account.getDisplayName());
            navSignOut.setVisibility(View.VISIBLE);
            if (account.getPhotoUrl() != null) {
                Picasso.with(this).load(account.getPhotoUrl()).into(navGoogleImage);
            } else {
                navGoogleImage.setImageDrawable(getResources().getDrawable(R.mipmap.profile_pic));
            }
        } else {
            navSignIn.setText(R.string.sign_in_drawer);
            navSignOut.setVisibility(View.INVISIBLE);
            navGoogleImage.setImageResource(R.mipmap.ic_google_log_in_image);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            getChannelId().execute();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateSignInUI(account);
        } catch (ApiException e) {
            updateSignInUI(null);
        }
    }

    private AsyncGetChannelId getChannelId() {
        return new AsyncGetChannelId(MainActivity.this,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        SharedPreferences.Editor editor =
                                getSharedPreferences("CHANNEL_ID_PREFERENCES", MODE_PRIVATE).edit();
                        editor.putString("CHANNEL_ID", result.getChannelId());
                        editor.apply();
                    }
                });
    }
}
