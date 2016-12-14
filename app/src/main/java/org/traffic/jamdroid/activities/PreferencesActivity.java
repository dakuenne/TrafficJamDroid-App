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

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.traffic.jamdroid.R;

/**
 * This activity gives the user the opportunity to change a few preferences.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 224 $
 */
public class PreferencesActivity extends BaseActivity {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.prefs);
	}
}
