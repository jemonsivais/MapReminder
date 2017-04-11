package com.example.mapmanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Special Dialog used when the user clicks on a 
 * marker to confirm the removal of the ToDoItem.
 * 
 * @author jesusmolina
 *
 */
public class MarkerDialog extends DialogFragment {
	/**
	 * Returns an instances of MarkerDialog with the
	 * given title.
	 * 
	 * @param title the title of the dialog.
	 * @return an instance of MarkerDialog.
	 */
	static MarkerDialog newInstance(String title){
		MarkerDialog md = new MarkerDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		md.setArguments(args);
		return md;
	}
	
	/**
	 * Creates a dialog, and sets the positive and negative button to call the doPositiveRemoverClick
	 * and doNegativeRemoverClick respectively.
	 * 
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		String title = getArguments().getString("title");
		AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
		ad.setIcon(R.drawable.ic_launcher);
		ad.setTitle(title);
		ad.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((MainActivity) getActivity()).doPositiveRemoverClick();
			}
		});
		ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).doNegativeRemoverClick();

			}
		});
		return ad;
	}
}
