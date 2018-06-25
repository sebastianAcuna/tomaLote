package cl.zcloud.www.inventariolotes.adapters.lotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cl.zcloud.www.inventariolotes.R;

public class AdaptadorListaSegundoNivel extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _headers;
    private HashMap<String, List<String>> _child;

    public AdaptadorListaSegundoNivel(Context context, List<String> headers, HashMap<String, List<String>> child){
        this._context = context;
        this._headers = headers;
        this._child = child;

    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._headers.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._headers.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            convertView = inflater.inflate(R.layout.row_second, null);
        }
        TextView text = convertView.findViewById(R.id.lblListFecha);
        String groupText = _headers.get(groupPosition);
        text.setText(groupText);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _child.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            convertView = inflater.inflate(R.layout.row_third, null);
        }

        TextView textView =  convertView.findViewById(R.id.lblListUbicacion);

        String childArray =  _child.values().toString();

        textView.setText(childArray);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._child.size();
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
