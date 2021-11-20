package com.team2.higallery.activities;

import static android.graphics.BitmapFactory.decodeFile;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.adapters.GridPhotosAdapter;
import com.team2.higallery.models.VaultImages;
import com.team2.higallery.utils.DataUtils;

import java.io.File;
import java.util.ArrayList;

public class VaultAlbumActivity extends AppCompatActivity {
    //Variable for activity
    private Toolbar appbar;

    //Variable for dialog
    private final int PIN_LENGTH = 6;

    GridPhotosAdapter gridPhotosAdapter;
    ArrayList<Integer> selectedIndices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_vault_album);

        setupAppBar();
        setupBody();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vault, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBack();
                return true;
            case R.id.vault_convert_album:
                convertToOriginalAlbum();
                return true;
            case R.id.vault_reset_password:
                openResetPassword();
                return true;
        }
        return false;
    }

    public void onBack() {
        finish();
    }

    private void openResetPassword() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_vault_reset_password);

        EditText passwordEt = (EditText) dialog.findViewById(R.id.dialog_reset_password);
        TextView passwordErrorTv = (TextView) dialog.findViewById(R.id.dialog_reset_password_error);

        EditText confirmPasswordEt = (EditText) dialog.findViewById(R.id.dialog_confirm_password);
        TextView confirmPasswordErrorTv = (TextView) dialog.findViewById(R.id.dialog_confirm_password_error);

        Button closeBtn = (Button) dialog.findViewById(R.id.dialog_vault_close);
        Button resetBtn = (Button) dialog.findViewById(R.id.dialog_vault_reset);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEt.getText().toString();
                String confirmPassword = confirmPasswordEt.getText().toString();

                if (password.length() != PIN_LENGTH) {
                    passwordErrorTv.setText(R.string.signup_vault_pin_too_short);
                    passwordErrorTv.setVisibility(View.VISIBLE);

                    return;
                } else {
                    passwordErrorTv.setVisibility(View.GONE);
                }

                if (!confirmPassword.equals(password)) {
                    confirmPasswordErrorTv.setText(R.string.signup_vault_pin_not_match);
                    confirmPasswordErrorTv.setVisibility(View.VISIBLE);

                    return;
                } else {
                    confirmPasswordErrorTv.setVisibility(View.GONE);
                }

                Toast.makeText(VaultAlbumActivity.this, "da doi thanh cong", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void convertToOriginalAlbum() {
        Toast.makeText(this, "convert to original album", Toast.LENGTH_SHORT).show();
    }

    private void setupAppBar() {
        appbar = (Toolbar) findViewById(R.id.appbar_vault);
        setSupportActionBar(appbar);
        ActionBar actionBar  = getSupportActionBar();

        // add X icon to appbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        appbar.setNavigationIcon(R.drawable.ic_baseline_close);
    }

    private void setupBody() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.body_vault);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        // TODO: Ảnh giả, cần thay bằng ảnh thật
        Bitmap dummyBitmap = decodeFile(DataUtils.allImages.get(0));
        VaultImages.list.add(dummyBitmap);

        gridPhotosAdapter = new GridPhotosAdapter(this, VaultImages.list, selectedIndices, new GridPhotosAdapter.ClickListener() {
            @Override
            public void onClick(int index) {
                // Nếu đang không ở chế độ Selection (có ảnh đang được chọn)
                // thì mở ảnh đó
                if (selectedIndices.isEmpty()) {
                    // TODO: Xem ảnh toàn màn hình, và tạo 2 nút left-right để xem ảnh 2 bên (không làm vuốt ảnh)
                    //       Có 2 cách làm:
                    //          + Tạo activity mới: truyền tham số là index của ảnh hiện tại, còn danh sách ảnh
                    //            thì là static (nằm lấy ở VaultImages)
                    //          + Ở activity này luôn: widget ImageView và 2 nút L-R ban đầu được INVISIBILE và khi
                    //            click ảnh thì INVISIBLE nó đồng thời set ảnh

                } else {
                    // Ngược lại, nếu đang ở chế độ Selection,
                    // và ta click lại 1 ảnh đang được select, thì bỏ select cho ảnh đó
                    if (selectedIndices.contains(index)) {
                        // TODO: Chưa cần làm

                    } else {
                        // TODO: Chưa cần làm

                    }
                }

            }

            @Override
            public void onLongClick(int index) {
                // Nếu long click một ảnh chưa có trong danh sách được select, thì thêm ảnh vào danh sách select
                if (!selectedIndices.contains(index)) {
                    // TODO: Chưa cần làm
                }
            }
        });
        recyclerView.setAdapter(gridPhotosAdapter);
    }
}