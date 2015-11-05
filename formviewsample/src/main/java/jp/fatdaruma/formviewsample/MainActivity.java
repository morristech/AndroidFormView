package jp.fatdaruma.formviewsample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.fatdaruma.formview.FormView;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FormView formView = (FormView) findViewById(R.id.form_view);

        FloatingActionButton fab = new FloatingActionButton(this);
        formView.addView(fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuffer stringBuffer = new StringBuffer();
                for(Map.Entry<String, String> entry: formView.getParams().entrySet()) {
                    stringBuffer.append(entry.getKey());
                    stringBuffer.append(":");
                    stringBuffer.append(entry.getValue());
                    stringBuffer.append('\n');
                }

                Log.d("MainActivity", stringBuffer.toString());

                if (formView.isValid()) {
                    Snackbar.make(view, "Logged", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        // twitter profile form sample
        String[] keys = { "name", "bio", "location", "url" };
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.twitter_profile_form, null);
        List<View> children = new ArrayList<>();

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            children.add(viewGroup.getChildAt(i));
        }
        viewGroup.removeAllViews();

        formView.add(keys[0], children.get(0), new Function1<String, Boolean>() {
            @Override
            public Boolean invoke(String s) {
                return s != null && !s.isEmpty();
            }
        });

        formView.add(keys[1], children.get(1), new Function1<String, Boolean>() {
            @Override
            public Boolean invoke(String s) {
                return s != null && s.contains("hello");
            }
        });

        formView.add(keys[2], children.get(2), new Function1<String, Boolean>() {
            @Override
            public Boolean invoke(String s) {
                return s != null && s.length() > 5;
            }
        });

        formView.add(keys[3], children.get(3), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
