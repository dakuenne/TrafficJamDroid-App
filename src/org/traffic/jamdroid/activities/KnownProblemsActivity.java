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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.Preferences;
import org.traffic.jamdroid.utils.IConstants;
import org.traffic.jamdroid.utils.Request;
import org.traffic.jamdroid.utils.Requester;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This activity shows the user a list of all recurring problems. Additionally a
 * functionality to visualize the belonging roads is provided in the {@link ShowProblemsActivity}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 151 $
 * @see ListActivity
 * @see ProgressDialog
 */
public class KnownProblemsActivity extends ListActivity {

	/** The dialog to show the users that the data is loading */
	private ProgressDialog pd = null;

	/** A map with the problems and a list of the belonging streets */
	private Map<String, ArrayList<Integer>> problems = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knownproblems);
		this.pd = ProgressDialog.show(this, "", getApplicationContext()
				.getResources().getString(R.string.popup_search_problems));
		new GetProblemsTask().execute();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent out = new Intent(this, ShowProblemsActivity.class);
		out.putIntegerArrayListExtra("data", problems.get(problems.keySet()
				.toArray(new String[0])[position]));
		startActivity(out);
	}

	/**
	 * Task to get all known problems from the server.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 151 $
	 */
	private class GetProblemsTask extends
			AsyncTask<Void, Void, Map<String, ArrayList<Integer>>> {

		@Override
		protected Map<String, ArrayList<Integer>> doInBackground(Void... args) {
			Map<String, ArrayList<Integer>> result = new HashMap<String, ArrayList<Integer>>();
			try {
				// sending the request to the server
				final Request reqProbs = new Request(
						IConstants.REQUEST_GET_PROBLEMS, Preferences
								.getInstance(getApplicationContext())
								.getString("session", null));
				Requester req = Requester.getInstance(getApplicationContext());
				final String response = req.contactServerForResult(reqProbs
						.toJson());

				// parsing the json-response
				final JSONObject jResponse = new JSONObject(response);
				final JSONArray jProblems = jResponse.getJSONArray("problems");

				// saving the data in a map
				for (int i = 0; i < jProblems.length(); i++) {
					String description = jProblems.getJSONObject(i).getString(
							"description");
					JSONArray jRegions = jProblems.getJSONObject(i)
							.getJSONArray("region");
					ArrayList<Integer> points = new ArrayList<Integer>();
					for (int j = 0; j < jRegions.length(); j++) {
						points.add(jRegions.getInt(j));
					}
					result.put(description, points);
				}
			} catch (Exception e) {
				Log.e("GetProblemsTask", e.getClass().getSimpleName()
						+ "@doInBackground: " + e.getMessage());
			}
			return result;
		}

		@Override
		protected void onPostExecute(Map<String, ArrayList<Integer>> result) {
			KnownProblemsActivity.this.problems = result;
			KnownProblemsActivity.this.setListAdapter(new ArrayAdapter<String>(
					KnownProblemsActivity.this, R.layout.row, R.id.problem,
					result.keySet().toArray(new String[0])));
			if (KnownProblemsActivity.this.pd != null) {
				KnownProblemsActivity.this.pd.dismiss();
			}
		}
	}
}
