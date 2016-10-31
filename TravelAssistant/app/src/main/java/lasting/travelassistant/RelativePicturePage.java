package lasting.travelassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class RelativePicturePage extends Fragment {
    private static final int INITIAL_DELAY_MILLIS = 300;
    private GridView gv = null;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.card_picture, null);
        gv = (GridView) view.findViewById(R.id.cardPicture);

        SwingBottomInAnimationAdapter sbiaa = new SwingBottomInAnimationAdapter(new RelativePictureAdapter(getActivity()));
        sbiaa.setAbsListView(gv);

        assert sbiaa.getViewAnimator() != null;
        sbiaa.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

        gv.setAdapter(sbiaa);

        return view;
    }
}
