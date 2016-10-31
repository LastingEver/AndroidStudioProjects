package lasting.travelassistant;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.TriangularPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SceneryPage extends FragmentActivity {
    private static final String[] title = new String[]{"热点新闻", "景区通知", "相关图片"};
    private List<String> titleList = Arrays.asList(title);
    CardDetailAdapter cda = null;

    private MagicIndicator mi = null;
    private ViewPager vp = null;
    private CommonNavigator cn = null;

    private List<Fragment> fragmentList = null;
    private Fragment hnp = null;
    private Fragment snp = null;
    private Fragment rpp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail);
        ActivityManager.getInstance().addActivity(this);

        fragmentList = new ArrayList<Fragment>();
        hnp = new HotNewsPage();
        snp = new SceneryNoticePage();
        rpp = new RelativePicturePage();
        fragmentList.add(hnp);
        fragmentList.add(snp);
        fragmentList.add(rpp);

        cda = new CardDetailAdapter(getSupportFragmentManager(), fragmentList);

        vp = (ViewPager) findViewById(R.id.viewPager);
        vp.setAdapter(cda);

        mi = (MagicIndicator) findViewById(R.id.mi);
        mi.setBackgroundColor(Color.WHITE);

        cn = new CommonNavigator(this);
        cn.setAdjustMode(true);
        cn.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titleList == null ? 0 : titleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                SimplePagerTitleView sptv = new SimplePagerTitleView(context);
                sptv.setText(titleList.get(i));
                sptv.setNormalColor(Color.parseColor("#333333"));
                sptv.setSelectedColor(Color.parseColor("#e94220"));
                sptv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vp.setCurrentItem(i);
                    }
                });
                return sptv;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                TriangularPagerIndicator tpi = new TriangularPagerIndicator(context);
                tpi.setLineColor(Color.parseColor("#e94220"));
                return tpi;
            }
        });

        mi.setNavigator(cn);

        ViewPagerHelper.bind(mi, vp);
    }
}
