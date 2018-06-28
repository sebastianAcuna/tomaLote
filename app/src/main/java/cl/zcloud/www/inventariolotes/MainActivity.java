package cl.zcloud.www.inventariolotes;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cl.zcloud.www.inventariolotes.bd.MyAppDB;
import cl.zcloud.www.inventariolotes.fragments.adminFragment;
import cl.zcloud.www.inventariolotes.fragments.homeFragment;
import cl.zcloud.www.inventariolotes.fragments.listarRegistrosFragment;
import cl.zcloud.www.inventariolotes.fragments.mantenedorFragment;
import cl.zcloud.www.inventariolotes.fragments.tomaExistenciaFragment;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TextView lblHeader;

    public static MyAppDB myAppDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        myAppDB = Room.databaseBuilder(getApplicationContext(), MyAppDB.class, "llasa_lote.db").allowMainThreadQueries().build();


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_l_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.left_menu_main);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        lblHeader = navigationView.getHeaderView(0).findViewById(R.id.titulo_img_header);

        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date dts = new Date();
        String[] fechTotal = TextUtils.split(dt.format(dts), "-");

        String fechaFinal = fechTotal[0] + "-" + fechTotal[1] + "-" +fechTotal[2];

        lblHeader.setText(fechaFinal);


        navigationView.setCheckedItem(R.id.l_menu_inicio);


        setupDrawerContent(navigationView);

        cambiarFragment(homeFragment.class);

    }


    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }


    private void selectDrawerItem(MenuItem item){

        Class fragmentClass;
        switch (item.getItemId()){
            case R.id.l_menu_inicio:
                fragmentClass = homeFragment.class;
                break;
            case R.id.l_menu_toma_existencia:
                fragmentClass = tomaExistenciaFragment.class;
                break;

            case R.id.l_menu_list_existencia:
                fragmentClass = listarRegistrosFragment.class;
                break;

            case R.id.l_menu_mant_ubic:
                fragmentClass = mantenedorFragment.class;
                break;

            case R.id.l_menu_herr_admin:
                fragmentClass = adminFragment.class;
                break;

            default:
                fragmentClass = homeFragment.class;
                break;

        }

        cambiarFragment(fragmentClass);

        item.setChecked(true);
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();

    }

    public void cambiarFragment( Class fragmentClass){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();
//        .addToBackStack(null)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
