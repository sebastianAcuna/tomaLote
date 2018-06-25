package cl.zcloud.www.inventariolotes.adapters.lotes;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.clases.Lotes;

public class AdaptadorListaPrimerNivel extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private List<String> _listSecondLevel;
    private HashMap<String, List<String>> _listThirdLevel;


    public AdaptadorListaPrimerNivel(Context context,
                                     List<String> listDataHeader,
                                     List<String> listChildData,
                                     HashMap<String, List<String>>_listThirdLevel) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listSecondLevel = listChildData;
        this._listThirdLevel = _listThirdLevel;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listSecondLevel.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final SecondLevelExpandableListView adapterLevel2 = new SecondLevelExpandableListView(_context);

//        List<String> dataTercerLevel = new ArrayList<>();
//        HashMap<String, List<String>> secondLevelData= _listThirdLevel.get(childPosition);

        adapterLevel2.setAdapter(new AdaptadorListaSegundoNivel(_context, _listSecondLevel , _listThirdLevel));
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



        /*final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_second, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListFecha);

        txtListChild.setText(childText);*/
        return  adapterLevel2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listSecondLevel.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_groups, null);
            }
        }

        TextView lblListHeader = null;
        if (convertView != null) {
            lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
        }
        if (lblListHeader != null) {
            lblListHeader.setTypeface(null, Typeface.BOLD);
        }
        if (lblListHeader != null) {
            lblListHeader.setText(headerTitle);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
