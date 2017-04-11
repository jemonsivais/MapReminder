package com.example.mapmanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Special Dialog that is used to create a ToDoItem
 * by specifying the location using an address.
 * 
 * It contains an EditText used by the user to 
 * write down the address of the ToDoItem.
 * 
 * @author jesusmolina
 *
 */
public class SpecifyAddressDialog extends DialogFragment {
	/** The text where the user will specify the address. */
	EditText et;

	/** Returns an instance of SpecifyAddressDialog. */
	static SpecifyAddressDialog newInstance(String title){
		SpecifyAddressDialog md = new SpecifyAddressDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		md.setArguments(args);
		return md;
	}
	
	/**
	 * Sets the positive and negative buttons to call the doPositiveSpecifyClick and
	 * doNegativeSpecifyClick respectevely.
	 * 
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		et = new EditText(getActivity().getBaseContext());

		String title = getArguments().getString("title");
		AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
		ad.setIcon(R.drawable.ic_launcher);
		ad.setTitle(title);
		ad.setView(et);
		ad.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((MainActivity) getActivity()).doPositiveSpecifyClick(et.getText().toString());
			}
		});
		ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).doNegativeSpecifyClick();

			}
		});
		return ad;
	}
}
