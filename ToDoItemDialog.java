package com.example.mapmanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Special dialog used when the user wants to create a new ToDoItem.
 * It uses an EditText for the user to name his ToDoItems.
 * 
 * @author jesusmolina
 *
 */
public class ToDoItemDialog extends DialogFragment {
	/** Where the user will give a name to the ToDoItem. */
	EditText et;
	
	/**
	 * Returns an instance of ToDoItemDialog with the given title.
	 *  
	 * @param title the title of the ToDoItemDialog.
	 * @return an instance of ToDoItemDialog.
	 */
	static ToDoItemDialog newInstance(String title){
		ToDoItemDialog tdid = new ToDoItemDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		tdid.setArguments(args);
		return tdid;
	}
	
	/**
	 * Creates a dialog and sets its positive and negative buttons to call doPositiveClick and doNegativeClick
	 * respectively.
	 * 
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		et = new EditText(getActivity().getBaseContext());
		
		String title = getArguments().getString("title");
		AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
		ad.setView(et);
		ad.setIcon(R.drawable.ic_launcher);
		ad.setTitle(title);
		ad.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((MainActivity) getActivity()).doPositiveClick(et.getText().toString());
			}
		});
		ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).doNegativeClick();

			}
		});
		return ad;
	}
}
