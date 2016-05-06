package com.td.tdabslidingmenu;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity implements OnClickListener {

	private DrawerLayout mDrawerLayout;
	private RelativeLayout mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	// SLIDING MENU OPTIONS
	RelativeLayout rlProfile;
	RelativeLayout rlHome;
	RelativeLayout rlUploads;
	RelativeLayout rlSubscriptions;
	RelativeLayout rlPlaylists;
	RelativeLayout rlHistory;
	RelativeLayout rlFav;
	ImageView ivMenuFacebook;
	ImageView ivMenuTwitter;
	ImageView ivMenuShare;

	public static String CUR_PAGE_TITLE = "Title";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		initMenu();

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (RelativeLayout) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, // host Activity
				mDrawerLayout, // DrawerLayout object
				R.drawable.ic_drawer, // nav drawer image to replace 'Up' image
				R.string.drawer_open, // "open drawer" description for accessibility
				R.string.drawer_close // "close drawer" description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			switchFragment(new Category());
			setSelected(rlHome);
			mDrawerLayout.openDrawer(mDrawerList); // Keep drawer open by default
		}

	}

	private void initMenu() {

		rlProfile = (RelativeLayout) findViewById(R.id.rlProfile);
		rlHome = (RelativeLayout) findViewById(R.id.rlHome);
		rlUploads = (RelativeLayout) findViewById(R.id.rlUploads);
		rlSubscriptions = (RelativeLayout) findViewById(R.id.rlSubscriptions);
		rlFav = (RelativeLayout) findViewById(R.id.rlFav);
		rlPlaylists = (RelativeLayout) findViewById(R.id.rlPlaylists);
		rlHistory = (RelativeLayout) findViewById(R.id.rlHistory);

		ivMenuFacebook = (ImageView) findViewById(R.id.ivMenuFacebook);
		ivMenuTwitter = (ImageView) findViewById(R.id.ivMenuTwitter);
		ivMenuShare = (ImageView) findViewById(R.id.ivMenuShare);

		rlProfile.setOnClickListener(this);
		rlHome.setOnClickListener(this);
		rlUploads.setOnClickListener(this);
		rlSubscriptions.setOnClickListener(this);
		rlFav.setOnClickListener(this);
		rlPlaylists.setOnClickListener(this);
		rlHistory.setOnClickListener(this);

		ivMenuFacebook.setOnClickListener(this);
		ivMenuTwitter.setOnClickListener(this);
		ivMenuShare.setOnClickListener(this);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}
		return true;

	}

	@Override
	public void onClick(View v) {

		// update the main content by replacing fragments
		Fragment newContent = new Category();
		Bundle bundle = new Bundle();

		if (v.getId() == R.id.rlProfile) {
			// PROFILE
			newContent = new UserProfile();
			bundle.putString(CUR_PAGE_TITLE, "Profile");
			setTitle("Profile");
			setSelected(rlProfile);
		} else if (v.getId() == R.id.rlHome) {
			// HOME
			setTitle("Home");
			bundle.putString(CUR_PAGE_TITLE, "Home");
			setSelected(rlHome);
		} else if (v.getId() == R.id.rlUploads) {
			// UPLOADS
			setTitle("Uploads");
			bundle.putString(CUR_PAGE_TITLE, "Uploads");
			setSelected(rlUploads);
		} else if (v.getId() == R.id.rlSubscriptions) {
			// Subscriptions
			setTitle("My Subscriptions");
			bundle.putString(CUR_PAGE_TITLE, "My Subscriptions");
			setSelected(rlSubscriptions);
		} else if (v.getId() == R.id.rlPlaylists) {
			// PLAYLISTS
			setTitle("Playlists");
			bundle.putString(CUR_PAGE_TITLE, "Playlists");
			setSelected(rlPlaylists);
		} else if (v.getId() == R.id.rlHistory) {
			// HISTORY
			setTitle("History");
			bundle.putString(CUR_PAGE_TITLE, "History");
			setSelected(rlHistory);
		} else if (v.getId() == R.id.rlFav) {
			// FAVOURITES
			setTitle("Favourites");
			bundle.putString(CUR_PAGE_TITLE, "Favourites");
			setSelected(rlFav);
		}

		// SHARE
		else if (v.getId() == R.id.ivMenuFacebook) {
			// FACEBOOK
			Toast.makeText(this, "Facebook pressed!", Toast.LENGTH_SHORT).show();
		} else if (v.getId() == R.id.ivMenuTwitter) {
			// TWITTER
			Toast.makeText(this, "Twitter pressed!", Toast.LENGTH_SHORT).show();
		} else if (v.getId() == R.id.ivMenuShare) {
			// SHARE
			Toast.makeText(this, "Share pressed!", Toast.LENGTH_SHORT).show();
		}

		if (newContent != null) {
			newContent.setArguments(bundle);
			switchFragment(newContent);
		}

	}

	// switching fragment
	private void switchFragment(Fragment fragment) {

		mDrawerLayout.closeDrawer(mDrawerList);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	// set the selected option as enabled
	private void setSelected(RelativeLayout rl) {

		// reset all selections
		rlProfile.setSelected(false);
		rlHome.setSelected(false);
		rlUploads.setSelected(false);
		rlSubscriptions.setSelected(false);
		rlPlaylists.setSelected(false);
		rlHistory.setSelected(false);
		rlFav.setSelected(false);

		rl.setSelected(true); // set current selection

	}

	// When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}