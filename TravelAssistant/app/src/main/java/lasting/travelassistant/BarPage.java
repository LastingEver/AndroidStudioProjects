package lasting.travelassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BarPage extends Fragment {
    private BarPage bp = this;
    private Fragment lp = new LoginPage();

    private Button login = null;
    private EditText username = null;
    private EditText password = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bar, container, false);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.bar, lp).commit();
        getActivity().getSupportFragmentManager().beginTransaction().hide(lp).commit();

        login = (Button) view.findViewById(R.id.loginButton);
        username = (EditText) view.findViewById(R.id.loginUsername);
        password = (EditText) view.findViewById(R.id.loginPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("admin") && password.getText().toString().equals("123456")) {
                    getActivity().getSupportFragmentManager().beginTransaction().hide(bp).commit();
                    getActivity().getSupportFragmentManager().beginTransaction().show(lp).commit();
                } else {
                    Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
