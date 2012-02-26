package com.swatiee;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class AddressOverlay extends ItemizedOverlay<OverlayItem> {
	private List<OverlayItem> items=new ArrayList<OverlayItem>();
	private Drawable marker=null;
	private Context mContext; 

	public AddressOverlay(Drawable marker, Context context) {
		super(marker);
		mContext = context;
		this.marker=marker;
		boundCenterBottom(marker);
	}

	public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
	}

	public AddressOverlay(Drawable marker) {
		super(marker);
		this.marker=marker;
		boundCenterBottom(marker);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return(items.get(i));
	}

	//	@Override
	//	protected boolean onTap(int i) {
	//		Toast.makeText(mContext,items.get(i).getSnippet(),Toast.LENGTH_SHORT).show();
	//		return(true);
	//	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = items.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		//dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	@Override
	public int size() {
		return(items.size());
	}

	public void addOverlay(OverlayItem overlay) {
		items.add(overlay);
		populate();
	}
}