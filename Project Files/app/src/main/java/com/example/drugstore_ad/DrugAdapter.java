package com.example.drugstore_ad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DrugAdapter extends BaseAdapter{
    /*药品图标列表*/
    private int[] icons = {R.drawable.drug1,R.drawable.drug2,
            R.drawable.drug3,R.drawable.drug4,R.drawable.drug5};
    private List<DrugInfo> list;
    private LayoutInflater layoutInflater;
    public DrugAdapter(Context context, List<DrugInfo> list){
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }
    /*得到Item条目的总数*/
    @Override
    public int getCount() {
        Log.e("CartAdapter","list.size()--"+list.size());
        return list.size();
    }
    /*根据position得到某个Item的对象*/
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    /*根据position得到某个Item的id*/
    @Override
    public long getItemId(int position) {
        return 0;
    }
    /*得到相应position对应的Item视图，
     -position是当前Item的位置，
     -convertView用于复用旧视图，
     -parent用于加载XML布局
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView=layoutInflater.inflate(R.layout.list_item,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        DrugInfo drugInfo = list.get(position);
        viewHolder.tv_id.setText("No."+drugInfo.getId());
        viewHolder.tv_name.setText(""+drugInfo.getName());
        viewHolder.tv_kind.setText(""+drugInfo.getKind());
        viewHolder.tv_num.setText(""+drugInfo.getNum());
        viewHolder.tv_price.setText(""+drugInfo.getPrice());
        int NoIcon = (drugInfo.getId()%5);
        viewHolder.imageView.setBackgroundResource(icons[NoIcon]);
        Log.e("DrugInfo","drugInfo.getName()-"+drugInfo.getName()+"  "+
                drugInfo.getKind()+"  "+drugInfo.getNum()+"  "+drugInfo.getPrice());
        return convertView;
    }
    class ViewHolder{
        TextView tv_id;
        TextView tv_name;
        TextView tv_kind;
        TextView tv_num;
        TextView tv_price;
        ImageView imageView;
        public ViewHolder(View view){
            tv_id =  view.findViewById(R.id.tv_ID);
            tv_name =  view.findViewById(R.id.tv_name);
            tv_kind =  view.findViewById(R.id.tv_kind);
            tv_num = view.findViewById(R.id.tv_num);
            tv_price =  view.findViewById(R.id.tv_price);
            imageView = view.findViewById(R.id.lv_icon);
        }
    }
}
