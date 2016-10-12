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

        getSupportFragmentManager().beginTransaction().replace(R.id.mdContent, mp).commit();

        initPer();

        initNav();
    }

    private void initPer() {
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

    private void initNav() {
        nv = (NavigationView) findViewById(R.id.mdMenu);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()){
                    case R.id.navigationItem1:
                        Toast.makeText(getApplicationContext(), "首页", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.mdContent, mp).commit();
                        break;

                    case R.id.navigationItem2:
                        Toast.makeText(getApplicationContext(), "新闻", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.mdContent, np).commit();
                        break;

                    case R.id.navigationItem3:
                        Toast.makeText(getApplicationContext(), "关于", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.mdContent, ap).commit();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }
}
