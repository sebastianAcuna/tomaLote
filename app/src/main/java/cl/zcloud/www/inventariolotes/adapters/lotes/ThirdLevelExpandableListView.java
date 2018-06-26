package cl.zcloud.www.inventariolotes.adapters.lotes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class ThirdLevelExpandableListView extends ExpandableListView {

    public ThirdLevelExpandableListView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * Adjust height
         */
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(1500, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
