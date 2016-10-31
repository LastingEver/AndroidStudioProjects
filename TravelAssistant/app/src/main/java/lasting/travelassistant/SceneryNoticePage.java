package lasting.travelassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;

public class SceneryNoticePage extends Fragment {
    private ScaleInAnimationAdapter siaa = null;
    private ListView lv = null;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.card_list, null);
        lv = (ListView) view.findViewById(R.id.cardList);

        siaa = new ScaleInAnimationAdapter(new SceneryNoticeAdapter(getActivity()));
        siaa.setAbsListView(lv);

        lv.setAdapter(siaa);

        return view;
    }
}
