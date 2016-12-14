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

import java.util.Locale;

import org.traffic.jamdroid.R;
import org.traffic.jamdroid.utils.IConstants;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

/**
 * This activity shows the online-help in a {@link WebView}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 151 $
 */
public class HelpActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		setTitle(R.string.alert_help);

		Bundle extras = getIntent().getExtras();
		String anchor = extras.getString("param");

		// loading the help
		WebView webView = (WebView) findViewById(R.id.helpview);
		webView.loadUrl(IConstants.ONLINE_HELP_PATH + "?param=" + anchor
				+ "&lang=" + Locale.getDefault().getLanguage());
	}

}
