package org.gampiot.robok.feature.terminal;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalViewClient;

import org.gampiot.robok.feature.terminal.databinding.ActivityTerminalBinding;
import org.gampiot.robok.feature.util.KeyboardUtils;
import org.gampiot.robok.feature.util.base.RobokActivity;
import org.gampiot.robok.feature.component.dialog.DialogEditText;

public class TerminalActivity extends RobokActivity implements TerminalSessionClient, TerminalViewClient {

     private ActivityTerminalBinding binding;
     private String cwd;
     private TerminalSession session;
     
     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          binding = ActivityTerminalBinding.inflate(getLayoutInflater());
          setContentView(binding.getRoot());
          if (getIntent().hasExtra("path")) {
              cwd = getIntent().getStringExtra("path");
          } else {
              cwd = Environment.getExternalStorageDirectory().getAbsolutePath();
          }
          binding.terminalView.setTextSize(28);
          String[] env = {};
          String[] argsList = {};
          session = new TerminalSession(
                "/system/bin/sh",
                cwd,
                env,
                argsList,
                TerminalEmulator.DEFAULT_TERMINAL_CURSOR_STYLE,
          this);
          binding.terminalView.attachSession(session);
          binding.terminalView.setTerminalViewClient(this);
          configureFabs();
     }
     
     public void configureFabs() {
          setOptionsVisibility(true);
          binding.terminalOptionsButton.setOnClickListener(view -> setOptionsVisibility(false));
          binding.closeButton.setOnClickListener(view -> setOptionsVisibility(true));
          binding.installPackageButton.setOnClickListener(v -> {
               // TO-DO : Logic to install package
               setOptionsVisibility(true);
          });
          binding.updatePackagesButton.setOnClickListener(v -> {
               // TO-DO : Logic to update packages
               setOptionsVisibility(true);
          });
     }
     
     public void setOptionsVisibility(boolean isHide) {
          binding.terminalOptionsLayout.animate()
                  .translationY(isHide ? 300 : 0)
                  .alpha(isHide ? 0 : 1)
                  .setInterpolator(new OvershootInterpolator());
                  
          binding.terminalOptionsButton.animate()
                  .translationY(isHide ? 0 : 300)
                  .alpha(isHide ? 1 : 0)
                  .setInterpolator(new OvershootInterpolator());
     }
     
     public void showInstallPackageDialog () {
          DialogEditText installPackageDialog = DialogEditText.Builder(this)
                .setIconResId(org.gampiot.robok.feature.component.R.drawable.ic_dot_24)
                .setText(getString(org.gampiot.robok.feature.res.R.terminal_install_package))
                .setTextFieldHint(getString(org.gampiot.robok.feature.res.R.terminal_install_package_hint))
                .setTextFieldCornerRadius(10f)
                .setAllowClickListener(v -> {
                      // TO-DO: install package logic
                })
                .setDenyClickListener(v -> {
                      installPackageDialog.dismiss();
                })
                .build();
          installPackageDialog.show();
     }
     
     @Override
     protected void onDestroy() {
         super.onDestroy();
         binding = null;
     }
     
     @Override
     public float onScale(float scale) {
          return 1F;
     }
    
     @Override
     public void onSingleTapUp(MotionEvent e) {
          KeyboardUtils.showSoftInput(binding.terminalView);
     }
     
     @Override
     public boolean shouldBackButtonBeMappedToEscape() {
          return false;
     }
     
     @Override
     public boolean shouldEnforceCharBasedInput() {
          return false;
     }
     
     @Override
     public boolean shouldUseCtrlSpaceWorkaround() {
          return false;
     }
     
     @Override
     public boolean isTerminalViewSelected () {
          return false;
     }
     
     @Override
     public void copyModeChanged(boolean copyMode) {}
     
     @Override
     public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session) {
          if (!session.isRunning()) {
              if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                   finish();
              }
          }
          return false;
     }
     
     @Override
     public boolean onLongPress(MotionEvent event) {
          return true;
     }
     
     @Override
     public boolean readControlKey() {
          return false;
     }
     
     @Override
     public boolean readAltKey() {
          return false;
     }
     
     @Override 
     public boolean readFnKey() {
          return false;
     }
     
     @Override
     public boolean readShiftKey() {
          return false;
     }

     @Override
     public boolean onCodePoint(int codePoint, boolean ctrlDown, TerminalSession session) {
          return false;
     }
     
     @Override
     public void onEmulatorSet() {}
     
     @Override
     public void logError(String tag, String message) {}
     
     @Override
     public void logWarn(String tag, String message) {}
     
     @Override
     public void logInfo(String tag, String message) {}
     
     @Override
     public void logDebug(String tag, String message) {}
     
     @Override
     public void logVerbose(String tag, String message) {}
     
     @Override
     public void logStackTraceWithMessage(String tag, String message, Exception e) {}
     
     @Override
     public void logStackTrace(String tag, Exception e) {}
     
     @Override
     public void onTextChanged(@NonNull TerminalSession changedSession) {
          binding.terminalView.onScreenUpdated();
     }
     
     @Override
     public void onTitleChanged(@NonNull TerminalSession changedSession) {}
     
     @Override
     public void onSessionFinished(@NonNull TerminalSession finishedSession) {}
     
     @Override
     public void onBell(@NonNull TerminalSession session) {}
     
     @Override
     public void onColorsChanged(@NonNull TerminalSession session) {}
     
     @Override
     public void onTerminalCursorStateChange(boolean state) {}
     
     @Override
     public Integer getTerminalCursorStyle() {
          return TerminalEmulator.DEFAULT_TERMINAL_CURSOR_STYLE;
     }
     
     @Override
     public void onCopyTextToClipboard(@NonNull TerminalSession arg0, String arg1) {}
     
     @Override 
     public void onPasteTextFromClipboard(@Nullable TerminalSession session) {
     
     }
     @Override
     @MainThread
     public void onBackPressed() {
          if (!session.isRunning()) {
              super.onBackPressed();
          }
     }
}
