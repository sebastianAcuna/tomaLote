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
import java.util.List;
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
    NavigationView navigationView;

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
        navigationView = findViewById(R.id.navigation_view);

        TextView lblHeader = navigationView.getHeaderView(0).findViewById(R.id.titulo_img_header);

        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date dts = new Date();
        String[] fechTotal = TextUtils.split(dt.format(dts), "-");

        String fechaFinal = fechTotal[0] + "-" + fechTotal[1] + "-" +fechTotal[2];

        lblHeader.setText(fechaFinal);


        navigationView.setCheckedItem(R.id.l_menu_inicio);


        setupDrawerContent(navigationView);

        cambiarFragment(homeFragment.class, "home");
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
        String tag;
        switch (item.getItemId()){
            case R.id.l_menu_inicio:
                fragmentClass = homeFragment.class;
                tag = "home";
                break;
            case R.id.l_menu_toma_existencia:
                fragmentClass = tomaExistenciaFragment.class;
                tag = "tomaExistencia";
                break;

            case R.id.l_menu_list_existencia:
                fragmentClass = listarRegistrosFragment.class;
                tag = "listarRegistros";
                break;

            case R.id.l_menu_mant_ubic:
                fragmentClass = mantenedorFragment.class;
                tag = "mantenedor";
                break;

            case R.id.l_menu_herr_admin:
                fragmentClass = adminFragment.class;
                tag = "admin";
                break;

            default:
                fragmentClass = homeFragment.class;
                tag = "home";
                break;

        }

        cambiarFragment(fragmentClass, tag);

        item.setChecked(true);
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();

    }

    @Override
    public void onBackPressed() {

        Fragment frg = getVisibleFragment();
        switch (Objects.requireNonNull(frg.getTag())){
            case "home":
                super.onBackPressed();
                break;
            case "tomaExistencia":
                navigationView.setCheckedItem(R.id.l_menu_inicio);
                cambiarFragment(homeFragment.class, "home");
                break;
            case "listarRegistros":
                navigationView.setCheckedItem(R.id.l_menu_inicio);
                cambiarFragment(homeFragment.class, "home");
                break;
            case "mantenedor":
                navigationView.setCheckedItem(R.id.l_menu_inicio);
                cambiarFragment(homeFragment.class, "home");
                break;
            case "admin":
                navigationView.setCheckedItem(R.id.l_menu_inicio);
                cambiarFragment(homeFragment.class, "home");
                break;
            default:
                super.onBackPressed();
                break;
        }

    }


    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void cambiarFragment(Class fragmentClass, String tag){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment, tag).commit();
//        .addToBackStack(null)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                hideKeyboard(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        Objects.requireNonNull(inputManager).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
