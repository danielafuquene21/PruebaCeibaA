package co.com.ceiba.mobile.pruebadeingreso.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

public class MainActivity extends Activity {
    private EditText editTextSearch;
    private RecyclerView recyclerViewSearchResults;
    private ArrayList<User> listUser = new ArrayList<User>();
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
        loadView();
        validateData();
    }

    private void validateData() {
        json= "";
        json = sharedPreferences.getString(Const.KEY_JSON_USERLIST, null);
        if(json == null){
            loadUserList();
        }else{
            loadUserListPreferences();
        }
    }

    private void loadUserListPreferences() {
        try {
            JSONArray newJArray = new JSONArray(json);
            json = newJArray.toString();
            loadJsonArray(newJArray);
            llenarListView(listUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadJsonArray(JSONArray newJArray) {

        try {
            for (int i = 0; i < newJArray.length(); i++) {
                User u = new User();
                JSONObject item = (JSONObject) newJArray.get(i);
                u.setName(item.getString(Const.OBJ_USER_NAME));
                u.setId((Integer.parseInt(item.getString(Const.OBJ_USER_ID)))) ;
                u.setUsername(item.getString(Const.OBJ_USER_USERNAME));
                u.setEmail(item.getString(Const.OBJ_USER_EMAIL));
                u.setPhone(item.getString(Const.OBJ_USER_PHONE));
                u.setWebsite(item.getString(Const.OBJ_USER_WEBSITE));
                listUser.add(u);
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

    private void loadUserList() {

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
                                json = "";
                                try {
                                    jsonarray = new JSONArray(myResponse);
                                    json = jsonarray.toString();
                                    loadJsonArray(jsonarray);
                                    llenarListView(listUser);
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

    private void loadView() {
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
        for(User s : listUser)
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