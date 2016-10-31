package lasting.travelassistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nhaarman.listviewanimations.ArrayAdapter;

public class RelativePictureAdapter extends ArrayAdapter<Integer> {
    private Context context = null;
    private BitmapCache bc = null;

    public RelativePictureAdapter(Context c) {
        context = c;
        bc = new BitmapCache();

        for (int i = 0; i < 100; i++) {
            add(i);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
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
