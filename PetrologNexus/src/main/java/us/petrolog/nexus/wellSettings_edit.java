package us.petrolog.nexus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * Created by Cesar on 11/25/13.
 */
public class wellSettings_edit {

    private DetailActivity myAct;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private View tempView;
    private EditText settings_v0ET;
    private EditText settings_v1ET;
    private EditText settings_v2ET;
    private EditText settings_v3ET;
    private EditText settings_v4ET;
    private Spinner autoTimeOut;

    public wellSettings_edit(DetailActivity myActivity) {

        myAct = myActivity;

        LayoutInflater inflater = myAct.getLayoutInflater();

        tempView = inflater.inflate(R.layout.settings, null);
        settings_v0ET = (EditText) tempView.findViewById(R.id.settings_v0ET);
        settings_v1ET = (EditText) tempView.findViewById(R.id.settings_v1ET);
        settings_v2ET = (EditText) tempView.findViewById(R.id.settings_v2ET);
        settings_v3ET = (EditText) tempView.findViewById(R.id.settings_v3ET);
        settings_v4ET = (EditText) tempView.findViewById(R.id.settings_v4ET);

        dialogBuilder = new AlertDialog.Builder(myAct);
        dialogBuilder.setCancelable(true);
    }

    public void popup() {

        /* Mex date format -> US date format */
        String date = DetailActivity.PetrologSerialCom.getPetrologClock();
        String time = date.substring(0, 9);
        String day = date.substring(9, 12);
        String month = date.substring(12, 15);
        String year = date.substring(15);
        settings_v0ET.setHint(time + month + day + year);
        settings_v0ET.setText("");
        settings_v1ET.setHint(String.valueOf(DetailActivity.PetrologSerialCom.getPumpUpSetting()));
        settings_v1ET.setText("");
        settings_v2ET.setHint(String.valueOf(DetailActivity.PetrologSerialCom.getPumpOffStrokesSetting()));
        settings_v2ET.setText("");
        settings_v3ET.setHint(String.valueOf(DetailActivity.PetrologSerialCom.getFillageSetting()));
        settings_v3ET.setText("");
        settings_v4ET.setHint(String.valueOf(DetailActivity.PetrologSerialCom.getCurrentTimeoutSetting()));
        settings_v4ET.setText("");

        autoTimeOut = (Spinner) tempView.findViewById(R.id.automatic_time_out);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                myAct,
                R.array.auto_timeout,
                R.layout.my_spinner
        );

        autoTimeOut.setAdapter(adapter);

        if (DetailActivity.PetrologSerialCom.getAutomaticTOSetting().equals("Yes")) {
            autoTimeOut.setSelection(0);
        } else {
            autoTimeOut.setSelection(1);
        }

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String tempV0;
                int tempV1;
                int tempV2;
                int tempV3;
                int tempV4;
                boolean temp;

                /* Clock */
                tempV0 = settings_v0ET.getText().toString();
                /* Pump Up */
                if (settings_v1ET.getText().toString().equals("")) {
                    tempV1 = Integer.valueOf(settings_v1ET.getHint().toString());
                } else {
                    tempV1 = Integer.valueOf(settings_v1ET.getText().toString());
                }
                /* Pump Off */
                if (settings_v2ET.getText().toString().equals("")) {
                    tempV2 = Integer.valueOf(settings_v2ET.getHint().toString());
                } else {
                    tempV2 = Integer.valueOf(settings_v2ET.getText().toString());
                }
                /* Fillage */
                if (settings_v3ET.getText().toString().equals("")) {
                    tempV3 = Integer.valueOf(settings_v3ET.getHint().toString());
                } else {
                    tempV3 = Integer.valueOf(settings_v3ET.getText().toString());
                }
                /* Time Out */
                if (settings_v4ET.getText().toString().equals("")) {
                    tempV4 = Integer.valueOf(settings_v4ET.getHint().toString());
                } else {
                    tempV4 = Integer.valueOf(settings_v4ET.getText().toString());
                }

                if (autoTimeOut.getSelectedItemPosition() == 0) {
                    temp = true;
                } else {
                    temp = false;
                }
                /* Write values to Petrolog */
                try {
                    removeDialog();
                    Toast.makeText(myAct, "Writing new settings to POC", Toast.LENGTH_LONG)
                            .show();
                    DetailActivity.PetrologSerialCom.setSettings(
                            tempV0,
                            tempV1,
                            tempV2,
                            tempV3,
                            tempV4,
                            temp
                    );
                } catch (NumberFormatException e) {
                    Log.e("PN - Settings Dialog", "Number error!");
                    removeDialog();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("PN - Settings Dialog", "onClick - Negative Button called");
                removeDialog();
            }
        });

        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.i("PN - Settings Dialog", "onCancel called");
                removeDialog();
            }
        });

        dialogBuilder.setView(tempView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void removeDialog() {
        Log.i("PN - Settings Dialog", "removeDialog called");
        ((ViewGroup) tempView.getParent()).removeView(tempView);
        dialog.dismiss();
        // Remove notification bar
        myAct.getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
        // Clean Dyna
        DetailActivity.wellDynagraphPost.clean();
        // Remove View

    }

}
