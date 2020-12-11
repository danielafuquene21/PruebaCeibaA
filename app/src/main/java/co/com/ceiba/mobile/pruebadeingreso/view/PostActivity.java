package co.com.ceiba.mobile.pruebadeingreso.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import co.com.ceiba.mobile.pruebadeingreso.R;
import co.com.ceiba.mobile.pruebadeingreso.adapter.PostAdapterList;
import co.com.ceiba.mobile.pruebadeingreso.dataObject.Post;
import co.com.ceiba.mobile.pruebadeingreso.dataObject.User;
import co.com.ceiba.mobile.pruebadeingreso.rest.Const;
import co.com.ceiba.mobile.pruebadeingreso.rest.Endpoints;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostActivity extends Activity {
    private TextView name;
    private TextView phone;
    private TextView email;
    User user;
    private RecyclerView recyclerViewPostsResults;
    private ArrayList<Post> postList;
    private GridLayoutManager glm;
    private PostAdapterList adapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getExtras();
        loadView();
        loadPost();
    }
    private void loadPost(){
        progressDialog = ProgressDialog.show(PostActivity.this, Const.VACIO,
                Const.PROGRESS_DIALOG, false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Endpoints.GET_POST_USER_ID.concat(String.valueOf(user.getId()));
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
                    public void onResponse(Call call, Response response) throws IOException {
                        final String myResponse = response.body().string();
                        PostActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONArray jsonarray = null;
                                try {
                                    jsonarray = new JSONArray(myResponse);
                                    postList = new ArrayList<Post>();
                                    for(int i=0;i<jsonarray.length();i++) {
                                        Post p = new Post();
                                        JSONObject item = (JSONObject) jsonarray.get(i);
                                        p.setId(item.getString(Const.OBJ_POST_ID));
                                        p.setUserId(item.getString(Const.OBJ_POST_USERID));
                                        p.setTitle(item.getString(Const.OBJ_POST_TITLE));
                                        p.setBody(item.getString(Const.OBJ_POST_BODY));
                                        postList.add(p);
                                    }
                                    fillListView(postList);
                                    if (progressDialog != null)
                                        progressDialog.cancel();
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

    private void fillListView(ArrayList<Post> listaResponse) {
        adapter = new PostAdapterList(listaResponse, getApplicationContext());
        recyclerViewPostsResults.setAdapter(adapter);
    }


    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = new User();
            user = (User) bundle.getSerializable(Const.KEY_INTENT_USERPOST);
        }
    }

    private void loadView() {
        name =  (TextView) findViewById(R.id.name);
        phone =  (TextView) findViewById(R.id.phone);
        email =  (TextView) findViewById(R.id.email);
        name.setText(user.getName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
        recyclerViewPostsResults = (RecyclerView) findViewById(R.id.recyclerViewPostsResults);
        glm = new GridLayoutManager(this, 1);
        recyclerViewPostsResults.setLayoutManager(glm);
        recyclerViewPostsResults.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
}
