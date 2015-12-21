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
package org.traffic.jamdroid.views.overlays;

import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.traffic.jamdroid.R;
import org.traffic.jamdroid.model.LocalViewContainer;
import org.traffic.jamdroid.model.Preferences;
import org.traffic.jamdroid.model.RemoteData;
import org.traffic.jamdroid.utils.IConstants;
import org.traffic.jamdroid.utils.Request;
import org.traffic.jamdroid.utils.Requester;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * This class provides functionality to store a list of
 * {@link DrawableOverlayItem}. Furthermore it provides a listener to react on
 * single-tap and long-press-events.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 128 $
 * @param <Item>
 *            A subclass of {@link DrawableOverlayItem}
 */
public class RoadOverlay<Item extends DrawableOverlayItem> extends Overlay {

	/** A list of all items on this overlay */
	protected final List<Item> itemList;

	private final Point mItemPoint = new Point();

	/**
	 * Custom-Constructor with a list of items and the {@link Context} of the
	 * {@link MapView}.
	 * 
	 * @param pList
	 *            The new items
	 * @param context
	 *            The {@link Context} of the {@link MapView}
	 */
	public RoadOverlay(final List<Item> pList, final Context context) {
		super(new DefaultResourceProxyImpl(context));
		this.itemList = pList;
	}

	/**
	 * Updates the item at the given position.
	 * 
	 * @param item
	 *            The new item
	 * @param position
	 *            The position for this item
	 */
	public void updateItem(final Item item, final int position) {
		itemList.remove(position);
		itemList.add(position, item);
	}

	/**
	 * Adds an item.
	 * 
	 * @param item
	 *            The new item
	 */
	public void addItem(final Item item) {
		itemList.add(item);
	}

	/**
	 * Returns the item located at <code>position</code>.
	 * 
	 * @param position
	 *            The position
	 * @return The item
	 */
	public final Item getItem(final int position) {
		return itemList.get(position);
	}

	/**
	 * Returns the number of items.
	 * 
	 * @return The number of items
	 */
	public final int getSize() {
		return itemList.size();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (shadow) {
			return;
		}

		final int size = this.itemList.size() - 1;

		// drawing all items in the list
		for (int i = size; i >= 0; i--) {
			final Item item = itemList.get(i);
			item.draw(canvas, mapView, shadow);
		}
	}

	@Override
	public boolean onSingleTapUp(final MotionEvent event, final MapView mapView) {
		return (activateSelectedItems(event, mapView, new ActiveItem() {
			@Override
			public boolean run(final int index) {
				final RoadOverlay<Item> that = RoadOverlay.this;
				if (that.itemGestureListener == null) {
					return false;
				}
				return onSingleTapUpHelper(index, that.itemList.get(index),
						mapView);
			}
		})) ? true : super.onSingleTapUp(event, mapView);
	}

	/**
	 * Delivers a single-tap-event to the {@link OnItemGestureListener}.
	 * 
	 * @param index
	 *            The index of the item in the the <code>itemList</code>
	 * @param item
	 *            The item itself
	 * @param mapView
	 *            The underlying {@link MapView}
	 * @return True, if the event was handled, false otherwise
	 */
	protected boolean onSingleTapUpHelper(final int index, final Item item,
			final MapView mapView) {
		return this.itemGestureListener.onItemSingleTapUp(index, item);
	}

	@Override
	public boolean onLongPress(final MotionEvent event, final MapView mapView) {
		return (activateSelectedItems(event, mapView, new ActiveItem() {
			@Override
			public boolean run(final int index) {
				final RoadOverlay<Item> that = RoadOverlay.this;
				if (that.itemGestureListener == null) {
					return false;
				}
				return onLongPressHelper(index, that.itemList.get(index));
			}
		})) ? true : super.onLongPress(event, mapView);
	}

	/**
	 * Delivers a long-press-event to the {@link OnItemGestureListener}.
	 * 
	 * @param index
	 *            The index of the item in the the <code>itemList</code>
	 * @param item
	 *            The item itself
	 * @return True, if the event was handled, false otherwise
	 */
	protected boolean onLongPressHelper(final int index, final Item item) {
		return this.itemGestureListener.onItemLongPress(index, item);
	}

	/**
	 * Searches in the <code>itemList</code> if an item is activated by the
	 * given event.
	 * 
	 * @param event
	 *            The event
	 * @param mapView
	 *            The underlying {@link MapView}
	 * @param task
	 *            The current item in the wrapperclass {@link ActiveItem}
	 * @return True if an item is activated
	 */
	private boolean activateSelectedItems(final MotionEvent event,
			final MapView mapView, final ActiveItem task) {
		final Projection pj = mapView.getProjection();
		final int eventX = (int) event.getX();
		final int eventY = (int) event.getY();

		for (int i = 0; i < this.itemList.size(); ++i) {
			final Item item = getItem(i);
			pj.toPixels(item.getPoint(), mItemPoint);

			if (hitTest(item, eventX - mItemPoint.x, eventY - mItemPoint.y, mapView)) {
				if (task.run(i)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether the click on the {@link MapView} could be matched with the
	 * item.
	 * 
	 * @param item
	 *            The item to check
	 * @param hitX
	 *            The x-coordinate of the click
	 * @param hitY
	 *            The y-coordinate of the click
	 * @param mapView
	 *            The underlying {@link MapView}
	 * @return True if the item was clicked, false otherwise
	 */
	public boolean hitTest(final Item item, final int hitX, final int hitY,
			final MapView mapView) {
		final Projection pj = mapView.getProjection();

		for (GeoPoint p : item.getGeoPoints()) {
			Point tmpPoint = new Point();
			pj.toPixels(p, tmpPoint);
			if (hitX >= tmpPoint.x - MAX_DIFF && hitX <= tmpPoint.x + MAX_DIFF
					&& hitY >= tmpPoint.y - MAX_DIFF
					&& hitY <= tmpPoint.y + MAX_DIFF) {
				return true;
			}

		}
		return false;
	}

	/** The maximal difference between the click and the position of the overlay */
	private static final int MAX_DIFF = 25;

	/** A listener to react on single-tap and long-press-events */
	private OnItemGestureListener<Item> itemGestureListener = new OnItemGestureListener<Item>() {
		@Override
		public boolean onItemSingleTapUp(final int index, final Item item) {
			Toast.makeText(
					LocalViewContainer.getInstance().getMapView().getContext(),
					item.toString(), Toast.LENGTH_LONG).show();
			return true;
		}

		@Override
		public boolean onItemLongPress(final int index, final Item item) {
			if (item instanceof RouteOverlayItem) {
				final Context context = LocalViewContainer.getInstance()
						.getMapView().getContext();
				
				// dialog for deletion of a route
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LocalViewContainer.getInstance().getMapView()
								.getContext());
				builder.setMessage(
						context.getResources().getString(
								R.string.delete_route_question))
						.setCancelable(false)
						.setPositiveButton(
								context.getResources().getString(R.string.yes),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										Request r = new Request(
												IConstants.REQUEST_DELETE_ROUTE,
												Preferences
														.getInstance(
																context.getApplicationContext())
														.getString("session",
																null));
										Requester.getInstance(context)
												.contactServer(r.toJson());
										RemoteData.getInstance()
												.deleteRouteOverlay();
										LocalViewContainer.getInstance()
												.getMapView().postInvalidate();
										Toast.makeText(
												context,
												context.getResources()
														.getString(
																R.string.delete_route),
												Toast.LENGTH_LONG).show();
									}
								})
						.setNegativeButton(
								context.getResources().getString(R.string.no),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
			return true;
		}
	};

	/**
	 * Interface to provide a listener for single-tap and long-press-events.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 128 $
	 * @param <T>
	 *            The class to interact with
	 */
	private static interface OnItemGestureListener<T> {

		/**
		 * Performs an action if a single-tap-event occurs.
		 * 
		 * @param index
		 *            The index of the item in the the <code>itemList</code>
		 * @param item
		 *            The item itself
		 * @return True, if the event was handled, false otherwise
		 */
		public boolean onItemSingleTapUp(final int index, final T item);

		/**
		 * Performs an action if a long-press-event occurs.
		 * 
		 * @param index
		 *            The index of the item in the the <code>itemList</code>
		 * @param item
		 *            The item itself
		 * @return True, if the event was handled, false otherwise
		 */
		public boolean onItemLongPress(final int index, final T item);
	}

	/**
	 * Interface to define a process for an item.
	 * 
	 * @author Daniel Kuenne
	 * @version $LastChangedRevision: 128 $
	 */
	private static interface ActiveItem {

		/**
		 * Checks the item at position <code>aIndex</code>, whether it was
		 * clicked or not.
		 * 
		 * @param aIndex
		 *            The index of the item in the the <code>itemList</code>
		 * @return True if the item was clicked, false otherwise
		 */
		public boolean run(final int aIndex);
	}

}
