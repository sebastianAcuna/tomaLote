package cl.zcloud.www.inventariolotes.adapters.lotes;

import android.content.Context;
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

public class AdaptadorListaSegundoNivel extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _headers; //ubicaciones
//    private HashMap<String, List<String>> _parent; //calles
    private HashMap<String, List<String>> _child; //lotes

    public AdaptadorListaSegundoNivel(Context context, List<String> headers,/*HashMap<String, List<String>> parent,*/ HashMap<String, List<String>> child){
        this._context = context;
        this._headers = headers;
//        this._parent = parent;
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
        String[] groupText = TextUtils.split((String) getGroup(groupPosition), "_");
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_second_level, null);
            TextView text = convertView.findViewById(R.id.lblListFecha);
            text.setText(groupText[0] + " " + groupText[1] + " " + groupText[2]);
            System.out.println(groupText[0] + " " + groupText[1] + " " + groupText[2]);
        }
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _child.get(_headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String childArray = (String) getChild(groupPosition,childPosition);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_fourth_level, null);
            TextView textView =  convertView.findViewById(R.id.lblListUbicacion);
            textView.setText(childArray);
        }

        return convertView;

//    final ThirdLevelExpandableListView adapterLevel3 = new ThirdLevelExpandableListView(_context);

        /*ArrayList<String> chilldd = new ArrayList<>();
        chilldd.add(getChild(groupPosition,childPosition).toString());
//        adapterLevel3.measure(parent.getWidth(),parent.getHeight());
        adapterLevel3.setAdapter(new AdaptadorListaTercerNivel(_context, chilldd , _child));
        adapterLevel3.setGroupIndicator(null);*/




       /* adapterLevel3.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    adapterLevel3.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });*/

//        return adapterLevel3;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._child.get(_headers.get(groupPosition)).size();
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
