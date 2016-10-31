package lasting.travelassistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;

public class HotNewsAdapter extends ExpandableListItemAdapter<Integer> {
    private Context context = null;
    private BitmapCache bc = null;

    public HotNewsAdapter(Context c) {
        super(c, R.layout.hotnews_card, R.id.hotNewsTitle, R.id.hotNewsContent);
        context = c;
        bc = new BitmapCache();

        for (int i = 0; i < 100; i++) {
            add(i);
        }
    }

    @NonNull
    @Override
    public View getTitleView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        TextView tv = (TextView) view;
        if (tv == null) {
            tv = new TextView(context);
        }
        tv.setText(context.getString(R.string.hot_news, getItem(i) + 1));
        return tv;
    }

    @NonNull
    @Override
    public View getContentView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        ImageView iv = (ImageView) view;
        if (iv == null) {
            iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        int imageResId;
        switch (getItem(i) % 5) {
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

        Bitmap bitmap = getBitmapFromMemCache(imageResId);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), imageResId);
            addBitmapToMemoryCache(imageResId, bitmap);
        }

        iv.setImageBitmap(bitmap);

        return iv;
    }

    private void addBitmapToMemoryCache(final int key, final Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            bc.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(final int key) {
        return bc.get(key);
    }
}
