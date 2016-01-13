package us.petrolog.nexus;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;


/**
 * Created by Cesar on 7/22/13.
 */
public class wellFillage_post {

    MainActivity myAct;
    TextSwitcher currentFillage;
    TextView fillageSetting;
    TextSwitcher pumpOffDistance;

    public wellFillage_post(MainActivity myActivity) {

        myAct = myActivity;

        currentFillage = (TextSwitcher) myAct.findViewById(R.id.fillage_v1TV);
        fillageSetting = (TextView) myAct.findViewById(R.id.fillage_v2TV);
        pumpOffDistance = (TextSwitcher) myAct.findViewById(R.id.fillage_v3TV);

        Animation in = AnimationUtils.loadAnimation(myAct, R.anim.push_down_in);
        Animation out = AnimationUtils.loadAnimation(myAct, R.anim.push_down_out);

        Animation inPD = AnimationUtils.loadAnimation(myAct, R.anim.push_down_in);
        Animation outPD = AnimationUtils.loadAnimation(myAct, R.anim.push_down_out);

        currentFillage.setInAnimation(in);
        currentFillage.setOutAnimation(out);

        pumpOffDistance.setInAnimation(inPD);
        pumpOffDistance.setOutAnimation(outPD);
    }

    public void post() {
        int fillSetting = MainActivity.PetrologSerialCom.getFillageSetting();
        int currentFill = MainActivity.PetrologSerialCom.getCurrentFillage();

        if ((fillSetting < 0) || (fillSetting > 90)) {
            fillageSetting.setText(StringFormatValue.format(myAct, "", myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
        } else {
            fillageSetting.setText(StringFormatValue.format(myAct, "" + fillSetting + "%", myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
        }

        if ((currentFill > 100) || (currentFill <= 0)) {
            String data = myAct.getString(R.string.n_a);

            TextView TempTV = (TextView) currentFillage.getCurrentView();
            if (!TempTV.getText().toString().equals(data)) {
                currentFillage.setText(StringFormatValue.format(myAct, "", myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
            }
            TempTV = (TextView) pumpOffDistance.getCurrentView();
            if (!TempTV.getText().toString().equals(data)) {
                pumpOffDistance.setText(StringFormatValue.format(myAct, "", myAct.getResources().getColor(R.color.mainGray), 1.2f, true));
            }
        } else {
            TextView TempTV = (TextView) currentFillage.getCurrentView();
            if (!TempTV.getText().toString().equals(String.valueOf(currentFill) + "%")) {
                currentFillage.setText(StringFormatValue.format(myAct, "" + currentFill + "%", myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
            }

            int pumpOffDis = currentFill - fillSetting;
            if (pumpOffDis < 0) {
                TempTV = (TextView) pumpOffDistance.getCurrentView();
                if (!TempTV.getText().toString().equals(String.valueOf(pumpOffDis) + "%")) {
                    pumpOffDistance.setText(StringFormatValue.format(myAct, "" + pumpOffDis + "%", myAct.getResources().getColor(R.color.mainRed), 1.2f, false));
                }

            } else {
                TempTV = (TextView) pumpOffDistance.getCurrentView();
                if (!TempTV.getText().toString().equals(String.valueOf(pumpOffDis) + "%")) {
                    pumpOffDistance.setText(StringFormatValue.format(myAct, "" + pumpOffDis + "%", myAct.getResources().getColor(R.color.mainBlue), 1.2f, false));
                }
            }
        }
    }
}
