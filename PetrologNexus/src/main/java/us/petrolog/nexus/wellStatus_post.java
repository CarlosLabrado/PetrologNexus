package us.petrolog.nexus;

import android.graphics.Color;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;


/**
 * Created by Cesar on 7/22/13.
 */
public class wellStatus_post {

    DetailActivity myAct;
    TextView current_t1TV;
    TextSwitcher current_v1TV;
    TextView current_t2TV;
    TextSwitcher current_v2TV;
    TextView current_t3TV;
    TextSwitcher current_v3TV;

    public wellStatus_post(DetailActivity myActivity) {

        myAct = myActivity;
        current_t1TV = (TextView) myAct.findViewById(R.id.current_t1TV);
        current_v1TV = (TextSwitcher) myAct.findViewById(R.id.current_v1TV);
        current_t2TV = (TextView) myAct.findViewById(R.id.current_t2TV);
        current_v2TV = (TextSwitcher) myAct.findViewById(R.id.current_v2TV);
        current_t3TV = (TextView) myAct.findViewById(R.id.current_t3TV);
        current_v3TV = (TextSwitcher) myAct.findViewById(R.id.current_v3TV);

        // Declare the in and out animations and initialize them
        Animation in = AnimationUtils.loadAnimation(myAct, R.anim.push_down_in);
        Animation out = AnimationUtils.loadAnimation(myAct, R.anim.push_down_out);
        Animation inCC = AnimationUtils.loadAnimation(myAct, R.anim.push_down_in);
        Animation outCC = AnimationUtils.loadAnimation(myAct, R.anim.push_down_out);
        Animation in1 = AnimationUtils.loadAnimation(myAct, android.R.anim.fade_in);
        Animation out1 = AnimationUtils.loadAnimation(myAct, android.R.anim.fade_out);

        current_v1TV.setInAnimation(in);
        current_v1TV.setOutAnimation(out);

        current_v2TV.setInAnimation(in1);
        current_v2TV.setOutAnimation(out1);

        current_v3TV.setInAnimation(inCC);
        current_v3TV.setOutAnimation(outCC);

    }

    public void post() {

    /* Format Well Status */
        String data = DetailActivity.PetrologSerialCom.getWellStatus();
        String title = myAct.getString(R.string.well_status);
        current_t1TV.setText(StringFormatTitle.format(title, Color.BLACK, 1f));
        if (data.contains("Running")) {
            /* Start Well button invisible*/
            DetailActivity.MyMenu.getItem(0).setVisible(false);
            DetailActivity.Help.setConnectedRunning();

            TextView TempTV = (TextView) current_v1TV.getCurrentView();
            if (!TempTV.getText().toString().equals(data)) {
                current_v1TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
            }

        /* Format Pump Off */
            title = myAct.getString(R.string.pump_off);
            current_t2TV.setText(StringFormatTitle.format(title, Color.BLACK, 1f));
            data = DetailActivity.PetrologSerialCom.getPumpOffStatus();
            if (data.contains("No")) {
                current_v2TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
            } else if (data.contains("Yes")) {
                current_v2TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainRed), 1.2f, false));
            } else {
                current_v2TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
            }

        /* Format Strokes this Cycle */
            int dataInt = DetailActivity.PetrologSerialCom.getStrokesThis();
            data = String.valueOf(dataInt) + " strokes";
            title = myAct.getString(R.string.strokes_this);
            current_t3TV.setText(StringFormatTitle.format(title, Color.BLACK, 1f));
            if (dataInt > 0) {
                TextView TempTVCC = (TextView) current_v3TV.getCurrentView();
                if (!TempTVCC.getText().toString().equals(data)) {
                    current_v3TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
                }
            } else {
                TextView TempTVCC = (TextView) current_v3TV.getCurrentView();
                if (!TempTVCC.getText().toString().equals(myAct.getString(R.string.n_a))) {
                    current_v3TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainBlue), 1.2f, true));
                }
            }
        } else if (data.contains("Stopped")) {
            /* Start Well button visible*/
            if (DetailActivity.Connected) {
                DetailActivity.MyMenu.getItem(0).setVisible(true);
                DetailActivity.Help.setConnectedStopped();
            }

            TextView TempTV = (TextView) current_v1TV.getCurrentView();
            if (!TempTV.getText().toString().equals(data)) {
                current_v1TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
            }

        /* Format Next Start */
            title = myAct.getString(R.string.next_start);
            current_t2TV.setText(StringFormatTitle.format(title, Color.BLACK, 1f));
            int min = DetailActivity.PetrologSerialCom.getMinNextStart();
            int sec = DetailActivity.PetrologSerialCom.getSecNextStart();
            if ((min >= 0 && min != 255) && (sec >= 0 && sec != 255)) {
                data = String.format("%02d", min) + ":" + String.format("%02d", sec);
                current_v2TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
            } else {
                current_v2TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
            }

        /* Format Strokes last Cycle */
            title = myAct.getString(R.string.strokes_last);
            current_t3TV.setText(StringFormatTitle.format(title, Color.BLACK, 1f));
            int dataInt = DetailActivity.PetrologSerialCom.getStrokesLast();
            String data1 = String.valueOf(dataInt) + " strokes";
            if (dataInt > 0) {
                TextView TempTVCC = (TextView) current_v3TV.getCurrentView();
                if (!TempTVCC.getText().toString().equals(data1)) {
                    current_v3TV.setText(StringFormatValue.format(myAct, data1, myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
                }
            } else {
                TextView TempTVCC = (TextView) current_v3TV.getCurrentView();
                if (!TempTVCC.getText().toString().equals(myAct.getString(R.string.n_a))) {
                    current_v3TV.setText(StringFormatValue.format(myAct, data1, myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
                }
            }
        } else {
            data = myAct.getString(R.string.n_a);
            TextView TempTV = (TextView) current_v1TV.getCurrentView();
            if (!TempTV.getText().toString().equals(data)) {
                current_v1TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
            }
            TempTV = (TextView) current_v2TV.getCurrentView();
            if (!TempTV.getText().toString().equals(data)) {
                current_v2TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
            }
            TempTV = (TextView) current_v3TV.getCurrentView();
            if (!TempTV.getText().toString().equals(data)) {
                current_v3TV.setText(StringFormatValue.format(myAct, data, myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
            }
        }
    }
}
