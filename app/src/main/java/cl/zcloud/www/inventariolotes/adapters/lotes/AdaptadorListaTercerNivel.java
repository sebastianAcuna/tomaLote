package cl.zcloud.www.inventariolotes.adapters.lotes;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cl.zcloud.www.inventariolotes.R;

public class AdaptadorListaTercerNivel extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _headers; //ubicaciones
    private HashMap<String, List<String>> _child; //lotes


    public AdaptadorListaTercerNivel(Context _context, List<String> _headers, HashMap<String, List<String>> _child) {
        this._context = _context;
        this._headers = _headers;
        this._child = _child;
    }

    @Override
    public int getGroupCount() {
        return this._headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._child.get(this._headers.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._child.get(this._headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            convertView = inflater.inflate(R.layout.list_third_level, null);
        }
        TextView text = convertView.findViewById(R.id.lblListCalle);
        String[] groupText = TextUtils.split((String) getGroup(groupPosition), "_");
        text.setText("calle : " + groupText[1]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.list_fourth_level, null);
            }

            TextView textView =  convertView.findViewById(R.id.lblListUbicacion);

            String childArray = (String) getChild(groupPosition,childPosition);

            textView.setText(childArray);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
