package de.foodshippers.foodship.FoodFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.foodshippers.foodship.R;
import de.foodshippers.foodship.api.model.Product;

import java.util.List;

/**
 * Created by hannes on 09.11.16.
 */
public class GridViewAdapter extends BaseAdapter implements FoodViewReFresher.OnFoodChangesListener {
    private Context context;
    private int layoutResourceId;
    private List<Product> data;

    public GridViewAdapter(Context context, int layoutResourceId) {
        super();
        FoodViewReFresher.getInstance(context).add(this);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = FoodViewReFresher.getInstance(context).getFoodList();
    }

    @Override
    public int getCount() {
        if (data.size() > 0) {
            return data.size();
        }
        return 1;
    }

    @Override
    public Object getItem(int i) {
        if (data.size() == 0) {
            return null;
        }
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
//        return Long.decode(data.get(i).getEan());
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        if (data.size() == 0) {
            holder.imageTitle.setText("No Element");
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
            holder.image.setImageBitmap(bmp);
            return row;
        } else {
            Product item = data.get(position);
            holder.imageTitle.setText(item.getType());
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
            holder.image.setImageBitmap(bmp);
            return row;
        }
    }

    @Override
    public void onFoodChangesNotyfi() {
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}