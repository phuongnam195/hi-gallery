package com.team2.higallery.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.adapters.GridPhotosAdapter;
import com.team2.higallery.models.VaultManager;

import java.util.ArrayList;
import java.util.Collections;

public class VaultAlbumActivity extends AppCompatActivity {
    //Variable for activity
    private Toolbar appbar;

    ArrayList<Bitmap> decryptedBitmaps;
    GridPhotosAdapter gridPhotosAdapter;
    ArrayList<Integer> selectedIndices = new ArrayList<>();

    ImageView fullImageView;
    VaultManager vaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_vault_album);

        fullImageView = (ImageView) findViewById(R.id.full_image_view);
        vaultManager = VaultManager.getInstance(this);

        setupAppBar();
        setupBody();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vaultManager.clearBitmaps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!selectedIndices.isEmpty()) {
            getMenuInflater().inflate(R.menu.menu_vault_select_mode, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onClose();
                return true;
            case R.id.deselect_all_menu_vault:
                onDeselectAll();
                return true;
            case R.id.select_all_menu_vault:
                onSelectAll();
                return true;
            case R.id.revert_selected_menu_vault:
                onRevertSelected();
                return true;

        }
        return false;
    }

    private void onClose() {
        finish();
    }

    private void onDeselectAll() {
        gridPhotosAdapter.deselectAll();
        invalidateOptionsMenu();
        appbar.setTitle(getResources().getString(R.string.vault_title));
    }

    private void onSelectAll() {
        gridPhotosAdapter.selectAll();
        int allCount = decryptedBitmaps.size();
        appbar.setTitle(allCount + "/" + allCount);
    }

    private void onRevertSelected() {
        Collections.sort(selectedIndices);
        for (int i = selectedIndices.size() - 1; i >= 0; i--) {
            vaultManager.revertToGallery(i);
            decryptedBitmaps.remove(selectedIndices.get(i).intValue());
        }
        gridPhotosAdapter.setImageBitmaps(decryptedBitmaps);
        onDeselectAll();
    }

//    private void openResetPIN() {
//        Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_vault_reset_password);
//
//        EditText passwordEt = (EditText) dialog.findViewById(R.id.dialog_reset_password);
//        TextView passwordErrorTv = (TextView) dialog.findViewById(R.id.dialog_reset_password_error);
//
//        EditText confirmPasswordEt = (EditText) dialog.findViewById(R.id.dialog_confirm_password);
//        TextView confirmPasswordErrorTv = (TextView) dialog.findViewById(R.id.dialog_confirm_password_error);
//
//        Button closeBtn = (Button) dialog.findViewById(R.id.dialog_vault_close);
//        Button resetBtn = (Button) dialog.findViewById(R.id.dialog_vault_reset);
//
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//            }
//        });
//
//        resetBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String password = passwordEt.getText().toString();
//                String confirmPassword = confirmPasswordEt.getText().toString();
//
//                if (password.length() != Account.PIN_LENGTH) {
//                    passwordErrorTv.setText(R.string.signup_vault_pin_too_short);
//                    passwordErrorTv.setVisibility(View.VISIBLE);
//                    return;
//                } else {
//                    passwordErrorTv.setVisibility(View.GONE);
//                }
//
//                if (!confirmPassword.equals(password)) {
//                    confirmPasswordErrorTv.setText(R.string.signup_vault_pin_not_match);
//                    confirmPasswordErrorTv.setVisibility(View.VISIBLE);
//                    return;
//                } else {
//                    confirmPasswordErrorTv.setVisibility(View.GONE);
//                }
//
//                // TODO: Cần truyền pass cũ mới cập nhật được pass mới, nên cần thêm field cho dialog
//                AuthCredential credential = EmailAuthProvider.getCredential(Account.email, );
//                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            user.
//                        }
//                    }
//                })
//            }
//        });
//
//        dialog.show();
//    }

    private void setupAppBar() {
        appbar = (Toolbar) findViewById(R.id.appbar_vault);
        setSupportActionBar(appbar);
        ActionBar actionBar = getSupportActionBar();

        // add X icon to appbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        appbar.setNavigationIcon(R.drawable.ic_baseline_close);
    }

    private void setupBody() {
        fullImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullImageView.setVisibility(View.GONE);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.body_vault);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        decryptedBitmaps = VaultManager.getInstance(this).getAllDecryptedBitmaps();

        gridPhotosAdapter = new GridPhotosAdapter(this, decryptedBitmaps, selectedIndices, new GridPhotosAdapter.ClickListener() {
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

                    fullImageView.setImageBitmap(decryptedBitmaps.get(index));
                    fullImageView.setVisibility(View.VISIBLE);

                } else {
                    // Ngược lại, nếu đang ở chế độ Selection,
                    // và ta click lại 1 ảnh đang được select, thì bỏ select cho ảnh đó
                    if (selectedIndices.contains(index)) {
                        gridPhotosAdapter.deselect(index);
//                        selectedIndices.remove(Integer.valueOf(index));
                        if (selectedIndices.isEmpty()) {
                            appbar.setTitle(getResources().getString(R.string.vault_title));
                            invalidateOptionsMenu();
                        } else {
                            int selectedCount = selectedIndices.size();
                            int allCount = decryptedBitmaps.size();
                            appbar.setTitle(selectedCount + "/" + allCount);
                        }
                    } else {
                        // nếu click 1 ảnh mới, thì thêm ảnh đó vào danh sách ảnh được select
                        gridPhotosAdapter.select(index);
                        int selectedCount = selectedIndices.size();
                        int allCount = decryptedBitmaps.size();
                        appbar.setTitle(selectedCount + "/" + allCount);
                    }
                }
            }

            @Override
            public void onLongClick(int index) {
                // Nếu long click một ảnh chưa có trong danh sách được select, thì thêm ảnh vào danh sách select
                if (selectedIndices.isEmpty()) {
                    invalidateOptionsMenu();
                }
                if (!selectedIndices.contains(index)) {
//                    selectedIndices.add(index);
                    gridPhotosAdapter.select(index);
                    int selectedCount = selectedIndices.size();
                    int allCount = decryptedBitmaps.size();
                    appbar.setTitle(selectedCount + "/" + allCount);
                }
            }
        });
        recyclerView.setAdapter(gridPhotosAdapter);
    }
}