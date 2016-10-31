package lasting.travelassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

public class HotNewsPage extends Fragment {
    private static final int INITIAL_DELAY_MILLIS = 500;
    private ListView lv = null;
    private View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.card_list, null);
        lv = (ListView) view.findViewById(R.id.cardList);

        AlphaInAnimationAdapter aiaa = new AlphaInAnimationAdapter(new HotNewsAdapter(getActivity()));
        aiaa.setAbsListView(lv);

        assert aiaa.getViewAnimator() != null;
        aiaa.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

        lv.setAdapter(aiaa);

        return view;
    }
}
