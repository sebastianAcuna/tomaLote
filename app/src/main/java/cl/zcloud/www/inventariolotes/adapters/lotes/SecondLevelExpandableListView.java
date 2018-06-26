package cl.zcloud.www.inventariolotes.adapters.lotes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class SecondLevelExpandableListView extends ExpandableListView {
    public SecondLevelExpandableListView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * Adjust height
         */
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(500, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
