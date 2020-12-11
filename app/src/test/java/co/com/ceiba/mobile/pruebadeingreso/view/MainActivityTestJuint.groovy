package co.com.ceiba.mobile.pruebadeingreso.view

import co.com.ceiba.mobile.pruebadeingreso.dataObject.User
import co.com.ceiba.mobile.pruebadeingreso.rest.Const
import co.com.ceiba.mobile.pruebadeingreso.rest.Endpoints
import io.realm.ClientResetRequiredError
import okhttp3.OkHttpClient
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoRule
import org.mockito.runners.MockitoJUnitRunner

@RunWith (MockitoJUnitRunner.class )
class MainActivityTestJuint {
    @Mock
    ArrayList<User> listMock;
    @Mock
    MainActivity mainActivity;

    public  void methodeCall(){
        Mockito.verify(mainActivity).loadUserList();
    }
}
