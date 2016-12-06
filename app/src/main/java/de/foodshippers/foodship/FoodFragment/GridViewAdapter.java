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
import de.foodshippers.foodship.Bilder.FoodImageManager;
import de.foodshippers.foodship.R;
import de.foodshippers.foodship.api.model.Product;
import de.foodshippers.foodship.api.model.Type;

import java.util.List;

/**
 * Created by hannes on 09.11.16.
 */
public class GridViewAdapter extends BaseAdapter implements FoodViewDataBase.OnFoodChangesListener {
    private Context context;
    private int layoutResourceId;
    private List<Product> data;
    private FoodImageManager imagman;

    public GridViewAdapter(Context context, int layoutResourceId) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = FoodViewDataBase.getInstance(context).getFoodList();
        this.imagman = FoodImageManager.getInstance(context);
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
            Type t = Type.getTypeFromId(context, item.getType());
            holder.imageTitle.setText(String.valueOf(t.getName()));
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
            holder.image.setImageBitmap(imagman.loadImageFromStorage(Type.getTypeFromId(context, item.getType())));
            return row;
        }
    }

    @Override
    public void onFoodChangesNotyfi() {
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public TextView imageTitle;
        public ImageView image;
    }
}