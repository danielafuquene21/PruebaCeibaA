package co.com.ceiba.mobile.pruebadeingreso.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.com.ceiba.mobile.pruebadeingreso.adapter.EmptyListAdapter;
import co.com.ceiba.mobile.pruebadeingreso.adapter.UserAdapterList;
import co.com.ceiba.mobile.pruebadeingreso.dataObject.User;
import co.com.ceiba.mobile.pruebadeingreso.R;
import co.com.ceiba.mobile.pruebadeingreso.rest.Const;
import co.com.ceiba.mobile.pruebadeingreso.rest.Endpoints;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private EditText editTextSearch;
    private RecyclerView recyclerViewSearchResults;
    private ArrayList<User> listaUser = new ArrayList<User>();
    private GridLayoutManager glm;
    private UserAdapterList adapter;
    private EmptyListAdapter emptyAdapter;
    ArrayList<String> a = new ArrayList<String>();
    boolean load = false;
    public String json = "";
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cargarVista();
        validarDatos();
    }

    private void validarDatos() {
        json= "";
        json = sharedPreferences.getString(Const.KEY_JSON_USERLIST, null);
        if(json == null){
            cargarListaUsuarios();
        }else{
            cargarListaUsuariosPreferences();
        }
    }

    private void cargarListaUsuariosPreferences() {
        try {
            JSONArray newJArray = new JSONArray(json);
            json = newJArray.toString();
            cargarJsonArray(newJArray);
            llenarListView(listaUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cargarJsonArray(JSONArray newJArray) {

        try {
            for (int i = 0; i < newJArray.length(); i++) {
                User u = new User();
                JSONObject item = (JSONObject) newJArray.get(i);
                Object name = item.getString(Const.OBJ_USER_NAME);
                Object id = item.getString(Const.OBJ_USER_ID);
                Object username = item.getString(Const.OBJ_USER_USERNAME);
                Object email = item.getString(Const.OBJ_USER_EMAIL);
                Object phone = item.getString(Const.OBJ_USER_PHONE);
                Object website = item.getString(Const.OBJ_USER_WEBSITE);

                u.setId((Integer.parseInt(id.toString())));
                u.setName(name.toString());
                u.setUsername(username.toString());
                u.setEmail(email.toString());
                u.setPhone(phone.toString());
                u.setWebsite(website.toString());

                listaUser.add(u);
                a.add(name.toString());
            }
            load = true;
        }catch (JSONException e) {
        e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void cargarListaUsuarios() {

        progressDialog = ProgressDialog.show(MainActivity.this, Const.VACIO,
                Const.PROGRESS_DIALOG, false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = Endpoints.URL_BASE+Endpoints.GET_USERS;
                OkHttpClient peticion = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                peticion.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        final String myResponse = response.body().string();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONArray jsonarray = null;
                                User user = null;
                                json = "";
                                try {
                                    jsonarray = new JSONArray(myResponse);
                                    json = jsonarray.toString();
                                    for(int i=0;i<jsonarray.length();i++) {
                                        User u = new User();
                                        JSONObject item = (JSONObject) jsonarray.get(i);
                                        u.setName(item.getString(Const.OBJ_USER_NAME));
                                        u.setId((Integer.parseInt(item.getString(Const.OBJ_USER_ID)))) ;
                                        u.setUsername(item.getString(Const.OBJ_USER_USERNAME));
                                        u.setEmail(item.getString(Const.OBJ_USER_EMAIL));
                                        u.setPhone(item.getString(Const.OBJ_USER_PHONE));
                                        u.setWebsite(item.getString(Const.OBJ_USER_WEBSITE));
                                        listaUser.add(u);
                                    }
                                    llenarListView(listaUser);
                                    if (progressDialog != null)
                                        progressDialog.cancel();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(Const.KEY_JSON_USERLIST, json);
                                    editor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void llenarListView(ArrayList<User> listaResponse) {
        adapter = new UserAdapterList(listaResponse, getApplicationContext());
        recyclerViewSearchResults.setAdapter(adapter);
    }

    private void cargarVista() {
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        recyclerViewSearchResults = (RecyclerView) findViewById(R.id.recyclerViewSearchResults);
        glm = new GridLayoutManager(this, 1);
        recyclerViewSearchResults.setLayoutManager(glm);
        recyclerViewSearchResults.setAdapter(adapter);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                filterList(e.toString());
            }
        });
        sharedPreferences  = getSharedPreferences("Json_user", MODE_PRIVATE);
    }

    private  void filterList (String text){
        ArrayList<User> filterNames= new ArrayList<>();
        for(User s : listaUser)
            if(s.getName().toLowerCase().contains(text.toLowerCase()))
                filterNames.add(s);

        if(filterNames.size() == 0){
            emptyAdapter = new EmptyListAdapter(getApplicationContext());
            recyclerViewSearchResults.setAdapter(emptyAdapter);
        }else{
            recyclerViewSearchResults.setAdapter(adapter);
            adapter.filterList(filterNames);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }


}