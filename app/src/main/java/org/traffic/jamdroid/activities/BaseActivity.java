/*
 * Copyright (c) 2011, Daniel Kuenne
 * 
 * This file is part of TrafficJamDroid.
 *
 * TrafficJamDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TrafficJamDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TrafficJamDroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.traffic.jamdroid.activities;

import org.traffic.jamdroid.R;
import org.traffic.jamdroid.TrafficJamDroidActivity;
import org.traffic.jamdroid.model.LocalData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This activity provides the menu-functionality for all other activities.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 224 $
 * @see TrafficJamDroidActivity
 * @see RoutingActivity
 * @see JammingActivity
 */
public class BaseActivity extends AppCompatActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// starting the selected activity
		switch (item.getItemId()) {
		case R.id.menu_jam:
			startActivity(new Intent(this, JammingActivity.class));
			break;
		case R.id.menu_route:
			if (LocalData.getInstance().getRoute().size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(
						BaseActivity.this.getApplicationContext()
								.getResources()
								.getString(R.string.popup_new_route))
						.setCancelable(false)
						.setPositiveButton(
								BaseActivity.this.getApplicationContext()
										.getResources().getString(R.string.yes),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										startActivity(new Intent(
												BaseActivity.this,
												RoutingActivity.class));
									}
								})
						.setNegativeButton(
								BaseActivity.this.getApplicationContext()
										.getResources().getString(R.string.no),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				builder.create().show();
			} else {
				startActivity(new Intent(this, RoutingActivity.class));
			}
			break;
		case R.id.menu_problems:
			startActivity(new Intent(this, KnownProblemsActivity.class));
			break;
		case R.id.menu_prefs:
			startActivityForResult(new Intent(getBaseContext(),
					PreferencesActivity.class), 0);
			break;
		case R.id.menu_help:
			Intent intent = new Intent(this, HelpActivity.class);
			intent.putExtra("param", this.getClass().getSimpleName());
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
