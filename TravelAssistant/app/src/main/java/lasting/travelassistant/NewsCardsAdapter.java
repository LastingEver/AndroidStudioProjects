package lasting.travelassistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

public class NewsCardsAdapter extends ArrayAdapter<Integer> {
    private Context context = null;
    private BitmapCache bc = null;
    private ViewHolder vh = null;

    public NewsCardsAdapter(Context c) {
        context = c;
        bc = new BitmapCache();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        vh = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.news_card, null);
            vh.textView = (TextView) convertView.findViewById(R.id.cardText);
            convertView.setTag(vh);
            vh.imageView = (ImageView) convertView.findViewById(R.id.cardImage);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.textView.setText("景点" + position);
        setImageView(vh, position);

        return convertView;
    }

    private void setImageView(ViewHolder vh, int position) {
        int imageResId;
        switch (getItem(position) % 5) {
            case 0:
                imageResId = R.drawable.img_nature1;
                break;
            case 1:
                imageResId = R.drawable.img_nature2;
                break;
            case 2:
                imageResId = R.drawable.img_nature3;
                break;
            case 3:
                imageResId = R.drawable.img_nature4;
                break;
            default:
                imageResId = R.drawable.img_nature5;
        }

        Bitmap bitmap = bc.get(imageResId);

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), imageResId);
            if (bc.get(imageResId) == null) {
                bc.put(imageResId, bitmap);
            }
        }

        vh.imageView.setImageBitmap(bitmap);
    }

    private static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
