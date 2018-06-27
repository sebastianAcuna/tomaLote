package cl.zcloud.www.inventariolotes.adapters.lotes;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cl.zcloud.www.inventariolotes.R;

public class AdaptadorListaPrimerNivel extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles //fechas
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listSecondLevel; //ubicaciones
    private HashMap<String, List<String>> _listThirdLevel; //calles
//    private HashMap<String, List<String>> _listFourthLevel; //lotes


    public AdaptadorListaPrimerNivel(Context context,
                                     List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData,
                                     HashMap<String, List<String>>_listThirdLevel
                                     /*,HashMap<String, List<String>>_listFourthLevel*/) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listSecondLevel = listChildData;
        this._listThirdLevel = _listThirdLevel;
//        this._listFourthLevel = _listFourthLevel;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listSecondLevel.get(_listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final SecondLevelExpandableListView adapterLevel2 = new SecondLevelExpandableListView(_context);

            ArrayList<String> chilldd = new ArrayList<>();
            chilldd.add(getChild(groupPosition,childPosition).toString());

            adapterLevel2.setAdapter(new AdaptadorListaSegundoNivel(_context, chilldd , _listThirdLevel/*, _listFourthLevel*/));
            adapterLevel2.setGroupIndicator(null);

            adapterLevel2.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    if(groupPosition != previousGroup)
                        adapterLevel2.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
                }
            });
        System.out.println("LLAMANDO A HIJO");

        return  adapterLevel2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return  _listSecondLevel.get(_listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    //cantidad de padres
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String[] headerTitle =  TextUtils.split((String) getGroup(groupPosition),"_");
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_first_level, null);

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
                lblListHeader.setTypeface(null, Typeface.BOLD);
                lblListHeader.setText(headerTitle[0] + " " + headerTitle[1]);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
