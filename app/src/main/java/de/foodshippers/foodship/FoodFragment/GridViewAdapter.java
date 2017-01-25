package de.foodshippers.foodship.FoodFragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.foodshippers.foodship.Bilder.FoodImageManager;
import de.foodshippers.foodship.R;
import de.foodshippers.foodship.api.model.Product;
import de.foodshippers.foodship.api.model.Type;

import java.util.List;

/**
 * Created by hannes on 09.11.16.
 */
public class GridViewAdapter extends BaseAdapter implements FoodViewDataBase.OnFoodChangesListener {
    private Activity activity;
    private int layoutResourceId;
    private List<Product> data;
    private FoodImageManager imagman;

    GridViewAdapter(Activity activity, int layoutResourceId) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.activity = activity;
        this.data = FoodViewDataBase.getInstance(this.activity).getFoodList();
        this.imagman = FoodImageManager.getInstance(this.activity);
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
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        if (data.size() == 0) {
            holder.imageTitle.setText(R.string.empty_products);
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
            holder.image.setImageBitmap(bmp);
            return row;
        } else {
            Product item = data.get(position);
            Type t = Type.getTypeFromId(activity, item.getType());
            holder.imageTitle.setText(String.valueOf(t.getName()));
            holder.image.setImageBitmap(imagman.loadImage(Type.getTypeFromId(activity, item.getType()).getImageUrl()));
            return row;
        }
    }

    @Override
    public void onFoodChangesNotify() {
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public ImageView image;
        TextView imageTitle;
    }
}