package lasting.travelassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainPage extends FragmentActivity {
    private static final int ALL_PERMISSIONS = 65535;

    public NavigationView nv = null;

    public Fragment mp = new MapPage();

    public Fragment np = new NewsPage();

    public Fragment ap = new AboutPage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        ActivityManager.getInstance().addActivity(this);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.mdContent, mp).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.mdContent, np).commit();
            getSupportFragmentManager().beginTransaction().hide(np).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.mdContent, ap).commit();
            getSupportFragmentManager().beginTransaction().hide(ap).commit();
        }

        initPerm();

        initLeftBar();
    }

    private void initPerm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            }, ALL_PERMISSIONS);
        }
    }

    private void initLeftBar() {
        nv = (NavigationView) findViewById(R.id.mdMenu);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()){
                    case R.id.navigationItem1:
                        Toast.makeText(getApplicationContext(), "首页", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().hide(getCurrentFragment()).show(mp).commit();
                        break;

                    case R.id.navigationItem2:
                        Toast.makeText(getApplicationContext(), "新闻", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().hide(getCurrentFragment()).show(np).commit();
                        break;

                    case R.id.navigationItem3:
                        Toast.makeText(getApplicationContext(), "关于", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().hide(getCurrentFragment()).show(ap).commit();
                        break;

                    case R.id.navigationItem4:
                        ActivityManager.getInstance().exit();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    public Fragment getCurrentFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible()){
                return fragment;
            }
        }
        return null;
    }
}
