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
import com.team2.higallery.providers.VaultManager;

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
        selectedIndices.sort(Collections.reverseOrder());
        for (Integer index : selectedIndices) {
            vaultManager.revertToGallery(index);
            decryptedBitmaps.remove(index.intValue());
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
//                // TODO: C???n truy???n pass c?? m???i c???p nh???t ???????c pass m???i, n??n c???n th??m field cho dialog
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
                // N???u ??ang kh??ng ??? ch??? ????? Selection (c?? ???nh ??ang ???????c ch???n)
                // th?? m??? ???nh ????
                if (selectedIndices.isEmpty()) {
                    // TODO: Xem ???nh to??n m??n h??nh, v?? t???o 2 n??t left-right ????? xem ???nh 2 b??n (kh??ng l??m vu???t ???nh)
                    //       C?? 2 c??ch l??m:
                    //          + T???o activity m???i: truy???n tham s??? l?? index c???a ???nh hi???n t???i, c??n danh s??ch ???nh
                    //            th?? l?? static (n???m l???y ??? VaultImages)
                    //          + ??? activity n??y lu??n: widget ImageView v?? 2 n??t L-R ban ?????u ???????c INVISIBILE v?? khi
                    //            click ???nh th?? INVISIBLE n?? ?????ng th???i set ???nh

                    fullImageView.setImageBitmap(decryptedBitmaps.get(index));
                    fullImageView.setVisibility(View.VISIBLE);

                } else {
                    // Ng?????c l???i, n???u ??ang ??? ch??? ????? Selection,
                    // v?? ta click l???i 1 ???nh ??ang ???????c select, th?? b??? select cho ???nh ????
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
                        // n???u click 1 ???nh m???i, th?? th??m ???nh ???? v??o danh s??ch ???nh ???????c select
                        gridPhotosAdapter.select(index);
                        int selectedCount = selectedIndices.size();
                        int allCount = decryptedBitmaps.size();
                        appbar.setTitle(selectedCount + "/" + allCount);
                    }
                }
            }

            @Override
            public void onLongClick(int index) {
                // N???u long click m???t ???nh ch??a c?? trong danh s??ch ???????c select, th?? th??m ???nh v??o danh s??ch select
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