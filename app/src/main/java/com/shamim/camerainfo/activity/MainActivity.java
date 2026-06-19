package com.shamim.camerainfo.activity;

import android.app.Activity;
import android.content.*;
import android.content.Context;
import android.hardware.camera2.*;
import android.net.Uri;
import android.os.Bundle;
import android.text.*;
import android.util.*;
import android.util.TypedValue;
import android.view.*;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.text.style.BackgroundColorSpan;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.core.text.PrecomputedTextCompat;
import androidx.core.view.*;
import androidx.core.widget.TextViewCompat;
import androidx.preference.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.shamim.camerainfo.R;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {

  private static final String TAG = "MainActivity";
  private ActivityResultLauncher<Intent> folderPickerLauncher;

  public static MainActivity ActivityContext;
  private final String[] infoOptions = {"Basic info", "All info", "Camcorder profile info"};
  TextView textView;
  FloatingActionButton shareFab;
  private CircularProgressIndicator progressIndicator;
  CameraManager cameraManager;

  private View searchBar;
  private EditText searchInput;
  private TextView searchCount;
  private ImageButton searchPrev;
  private ImageButton searchNext;
  private ImageButton searchClose;
  private LinearProgressIndicator searchProgressIndicator;

  private SpannableStringBuilder cachedSpannableText = null;
  private androidx.core.text.PrecomputedTextCompat cachedPrecomputedText = null;
  private String cachedPlainText = "";
  private String cachedLowerPlainText = "";
  private String lastSearchQuery = "";
  private final java.util.concurrent.ThreadPoolExecutor searchExecutor =
      new java.util.concurrent.ThreadPoolExecutor(
          2, 8, 5L, java.util.concurrent.TimeUnit.SECONDS,
          new java.util.concurrent.LinkedBlockingQueue<Runnable>());
  private java.util.concurrent.Future<?> currentSearchFuture = null;
  private int searchGeneration = 0;
  private final java.util.ArrayList<Integer> matchOffsets = new java.util.ArrayList<>();
  private int currentMatchIndex = -1;
  private String currentSearchQuery = "";
  private final android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
  private Runnable searchRunnable;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
    boolean isFirstLaunch = prefs.getBoolean("intro_shown", false);

    if (!isFirstLaunch) {
      startActivity(new Intent(this, IntroActivity.class));
      finish();
      return;
    }

    ActivityContext = this;

    setContentView(R.layout.activity_main);

    OTAUpdateHelper.checkForUpdatesIfDue(this);

    boolean logcat = SharedPrefValues.getValue("enable_logcat", false);

    if (logcat) {

      if (StoragePermissionHelper.isPermissionGranted(this)) {
        LogcatSaver.RunLog(this);
      }

      folderPickerLauncher =
          registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
              result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                  Intent data = result.getData();
                  StoragePermissionHelper.handleFolderPickerResult(this, data);
                  if (StoragePermissionHelper.isPermissionGranted(this)) {
                    LogcatSaver.RunLog(this);
                  }
                }
              });

      StoragePermissionHelper.checkAndRequestStoragePermission(this, folderPickerLauncher);
    }

    cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    textView = findViewById(R.id.logView); // আপনার TextView এর আইডি বসান
    progressIndicator = findViewById(R.id.progress_indicator);
    progressIndicator.setIndeterminate(false);

    MaterialToolbar toolbar = findViewById(R.id.topAppBar);
    setSupportActionBar(toolbar);

    // Get views
    ChipGroup chipGroup = findViewById(R.id.LogChipGroup);
    Chip chipBasicMode = findViewById(R.id.chipBasicMode);
    Chip chipAllMode = findViewById(R.id.chipAllMode);
    Chip chipCamcorderProfile = findViewById(R.id.chipCamcorderProfile);

    // Load saved selection (default = 0)
    int savedIndex = SharedPrefValues.getValue("pref_log_mode", 0);

    // Pre-select the correct chip based on saved value
    switch (savedIndex) {
      case 0:
        chipBasicMode.setChecked(true);
        break;
      case 1:
        chipAllMode.setChecked(true);
        break;
      case 2:
        chipCamcorderProfile.setChecked(true);
        break;
    }

    // Listen for chip selection changes
    chipGroup.setOnCheckedStateChangeListener(
        (group, checkedIds) -> {
          if (checkedIds.isEmpty()) return; // no selection

          int checkedId = checkedIds.get(0);
          int selectedIndex = 0;

          if (checkedId == R.id.chipBasicMode) selectedIndex = 0;
          else if (checkedId == R.id.chipAllMode) selectedIndex = 1;
          else if (checkedId == R.id.chipCamcorderProfile) selectedIndex = 2;

          // Save user selection in SharedPreferences
          PreferenceManager.getDefaultSharedPreferences(this)
              .edit()
              .putString("pref_log_mode", String.valueOf(selectedIndex))
              .apply();

          // Update data or UI
          setInfoToTextView();
        });

    shareFab = findViewById(R.id.sharetext);

    shareFab.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            shareTextAsFile();
          }
        });

    // Initialize Search UI Components
    searchBar = findViewById(R.id.search_bar);
    searchInput = findViewById(R.id.search_input);
    searchInput.setFilters(new android.text.InputFilter[] {
      new android.text.InputFilter() {
        @Override
        public java.lang.CharSequence filter(
            java.lang.CharSequence source, int start, int end,
            android.text.Spanned dest, int dstart, int dend) {
          if (source == null) return null;
          boolean hasLinebreakerOrTab = false;
          for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (c == '\n' || c == '\r' || c == '\t') {
              hasLinebreakerOrTab = true;
              break;
            }
          }
          if (hasLinebreakerOrTab) {
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < end; i++) {
              char c = source.charAt(i);
              if (c == '\n' || c == '\r' || c == '\t') {
                sb.append(' ');
              } else {
                sb.append(c);
              }
            }
            return sb.toString();
          }
          return null;
        }
      }
    });
    searchCount = findViewById(R.id.search_count);
    searchPrev = findViewById(R.id.search_prev);
    searchNext = findViewById(R.id.search_next);
    searchClose = findViewById(R.id.search_close);
    searchProgressIndicator = findViewById(R.id.search_progress_indicator);

    searchInput.addTextChangedListener(new android.text.TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {}

      @Override
      public void afterTextChanged(android.text.Editable s) {
        currentSearchQuery = s.toString();
        currentMatchIndex = 0;
        if (searchRunnable != null) {
          searchHandler.removeCallbacks(searchRunnable);
        }
        if (currentSearchQuery.isEmpty()) {
          if (searchProgressIndicator != null) {
            searchProgressIndicator.setVisibility(View.GONE);
          }
          updateSearchHighlights(true);
        } else {
          searchRunnable = () -> updateSearchHighlights(true);
          searchHandler.postDelayed(searchRunnable, 1100);
        }
      }
    });

    searchInput.setOnEditorActionListener((v, actionId, event) -> {
      if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
        if (searchRunnable != null) {
          searchHandler.removeCallbacks(searchRunnable);
        }
        updateSearchHighlights(true);
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
          imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
        return true;
      }
      return false;
    });

    searchPrev.setOnClickListener(v -> {
      if (!matchOffsets.isEmpty()) {
        currentMatchIndex = (currentMatchIndex - 1 + matchOffsets.size()) % matchOffsets.size();
        updateSearchHighlights(false);
      }
    });

    searchNext.setOnClickListener(v -> {
      if (!matchOffsets.isEmpty()) {
        currentMatchIndex = (currentMatchIndex + 1) % matchOffsets.size();
        updateSearchHighlights(false);
      }
    });

    searchClose.setOnClickListener(v -> closeSearch());

    setInfoToTextView();
  }

  // থিম থেকে রঙ আনার জন্য হেল্পার মেথড
  private int getColorFromAttr(Context context, int attr) {
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(attr, typedValue, true);
    return typedValue.data;
  }

  private void shareTextAsFile() {
    String text = textView.getText().toString();

    try {
      // cache ডিরেক্টরিতে টেম্প ফাইল বানানো
      File file = new File(getCacheDir(), "camera Info.txt");
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(text.getBytes());
      fos.close();

      // FileProvider দিয়ে Uri তৈরি
      Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

      // শেয়ার করার Intent
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("text/plain");

      // ফাইল অ্যাটাচ করা
      intent.putExtra(Intent.EXTRA_STREAM, uri);

      // মেসেজ যুক্ত করা
      intent.putExtra(Intent.EXTRA_TEXT, DeviceInfo.getShortDeviceInfoMassage());

      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      startActivity(Intent.createChooser(intent, "Share text file via"));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setInfoToTextView() {
    cachedSpannableText = null;
    cachedPrecomputedText = null;
    cachedPlainText = "";
    cachedLowerPlainText = "";
    lastSearchQuery = "";
    // 1. Show the indicator immediately
    if (progressIndicator != null) {
      progressIndicator.setIndeterminate(false);
      progressIndicator.setVisibility(View.VISIBLE);
      progressIndicator.setProgress(8, true);
    }
    textView.setText("");

    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(
        () -> {
          progressIndicator.setProgress(15, true);
          // Prepare text in background
          String deviceInfo = DeviceInfo.getDeviceInfoText(this);
          String cameraInformation = CameraInfoHelper.getAllCameraInfo(cameraManager);

          progressIndicator.setProgress(20, true);

          StringBuilder combinedInfo = new StringBuilder();
          combinedInfo.append(deviceInfo);
          if (!deviceInfo.isEmpty() && !cameraInformation.isEmpty()) {
            combinedInfo.append("\n");
          }
          combinedInfo.append(cameraInformation);

          progressIndicator.setProgress(30, true);

          String plainText = combinedInfo.toString();
          String lowerPlainText = plainText.toLowerCase(java.util.Locale.US);

          int keyColor = getColorFromAttr(this, androidx.appcompat.R.attr.colorPrimary);
          int separatorColor =
              getColorFromAttr(this, com.google.android.material.R.attr.colorTertiary);
          int valueColor =
              getColorFromAttr(this, com.google.android.material.R.attr.colorSecondary);

          SpannableStringBuilder spannableText =
              ColoredTextHelper.setColoredText(
                  plainText, keyColor, separatorColor, valueColor);

          progressIndicator.setProgress(45, true);

          // 3. Prepare text layout off the main thread (async + safe)
          PrecomputedTextCompat.Params params = TextViewCompat.getTextMetricsParams(textView);
          PrecomputedTextCompat precomputedText =
              PrecomputedTextCompat.create(spannableText, params);

          // 4. Update UI once text layout is ready
          runOnUiThread(
              () -> {
                progressIndicator.setProgress(80, true);

                // Ensure matching parameters (avoids IllegalArgumentException)
                TextViewCompat.setTextMetricsParams(textView, precomputedText.getParams());
                TextViewCompat.setPrecomputedText(textView, precomputedText);

                cachedSpannableText = spannableText;
                cachedPrecomputedText = precomputedText;
                cachedPlainText = plainText;
                cachedLowerPlainText = lowerPlainText;

                if (!currentSearchQuery.isEmpty()) {
                  updateSearchHighlights(true);
                }

                progressIndicator.setProgress(100, true);
                textView.postDelayed(() -> progressIndicator.setVisibility(View.GONE), 300);
              });

          executor.shutdown();
        });
  }

  private void openSearch() {
    searchBar.setVisibility(View.VISIBLE);
    searchInput.requestFocus();
    searchInput.postDelayed(() -> {
      if (!isDestroyed() && !isFinishing()) {
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
          imm.showSoftInput(searchInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        }
      }
    }, 100);
  }

  private void closeSearch() {
    currentSearchQuery = "";
    searchInput.setText("");
    searchBar.setVisibility(View.GONE);
    if (searchProgressIndicator != null) {
      searchProgressIndicator.setVisibility(View.GONE);
    }
    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }
    updateSearchHighlights(true);
  }

  private void updateSearchHighlights() {
    updateSearchHighlights(false);
  }

  private void updateSearchHighlights(boolean forceRequery) {
    if (searchProgressIndicator != null) {
      searchProgressIndicator.setVisibility(View.VISIBLE);
    }

    if (currentSearchFuture != null) {
      currentSearchFuture.cancel(true);
    }
    try {
      searchExecutor.getQueue().clear();
    } catch (Exception e) {
      e.printStackTrace();
    }

    final String query = currentSearchQuery;
    final int targetIndex = currentMatchIndex;
    final int generation = ++searchGeneration;

    if (query.trim().isEmpty()) {
      lastSearchQuery = "";
      matchOffsets.clear();
      currentMatchIndex = -1;
      searchCount.setText("0/0");
      if (searchProgressIndicator != null) {
        searchProgressIndicator.setVisibility(View.GONE);
      }
      if (cachedPrecomputedText != null) {
        androidx.core.widget.TextViewCompat.setPrecomputedText(textView, cachedPrecomputedText);
      } else {
        textView.setText(cachedSpannableText != null ? cachedSpannableText : "");
      }
      return;
    }

    // Ensure cache is available
    if (cachedSpannableText == null) {
      CharSequence text = textView.getText();
      cachedSpannableText = new SpannableStringBuilder(text != null ? text : "");
      cachedPlainText = cachedSpannableText.toString();
      cachedLowerPlainText = cachedPlainText.toLowerCase(java.util.Locale.US);
    } else if (cachedLowerPlainText == null || cachedLowerPlainText.isEmpty()) {
      cachedLowerPlainText = cachedPlainText.toLowerCase(java.util.Locale.US);
    }

    final String searchTargetText = cachedPlainText;
    final SpannableStringBuilder baseSpannable = cachedSpannableText;

    // Fast-path: If query hasn't changed, we don't need to re-find matches!
    boolean needRequery = forceRequery || !query.equals(lastSearchQuery) || matchOffsets.isEmpty();

    // Fetch text metrics parameters on the main thread
    final PrecomputedTextCompat.Params params = TextViewCompat.getTextMetricsParams(textView);

    currentSearchFuture = searchExecutor.submit(() -> {
      if (generation != searchGeneration || Thread.currentThread().isInterrupted()) return;

      final java.util.ArrayList<Integer> localMatches;
      if (needRequery) {
        final String queryLower = query.toLowerCase(java.util.Locale.US);
        final String textStrLower = cachedLowerPlainText;
        localMatches = new java.util.ArrayList<>();

        int index = textStrLower.indexOf(queryLower);
        while (index >= 0) {
          if (generation != searchGeneration || Thread.currentThread().isInterrupted()) return;
          localMatches.add(index);
          if (localMatches.size() >= 1000) {
            break;
          }
          index = textStrLower.indexOf(queryLower, index + 1);
        }
      } else {
        // Reuse precalculated matches
        localMatches = new java.util.ArrayList<>(matchOffsets);
      }

      if (generation != searchGeneration || Thread.currentThread().isInterrupted()) return;

      final SpannableStringBuilder spannable = new SpannableStringBuilder(baseSpannable);
      final int matchCount = localMatches.size();
      final int finalMatchIndex;
      if (matchCount > 0) {
        if (targetIndex < 0 || targetIndex >= matchCount) {
          finalMatchIndex = 0;
        } else {
          finalMatchIndex = targetIndex;
        }

        int highlightColor = 0x66FFEB3B; // Yellow with 40% opacity
        int currentHighlightColor = 0xFFFF9800; // Orange or bright yellow for current match
        
        int windowSize = 50;
        int startHighlight = Math.max(0, finalMatchIndex - windowSize);
        int endHighlight = Math.min(matchCount - 1, finalMatchIndex + windowSize);
        
        for (int i = startHighlight; i <= endHighlight; i++) {
          if (generation != searchGeneration || Thread.currentThread().isInterrupted()) return;
          int start = localMatches.get(i);
          int end = start + query.length();
          int color = (i == finalMatchIndex) ? currentHighlightColor : highlightColor;
          
          spannable.setSpan(
              new BackgroundColorSpan(color),
              start,
              end,
              Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
      } else {
        finalMatchIndex = -1;
      }

      if (generation != searchGeneration || Thread.currentThread().isInterrupted()) return;

      // Precompute layout off the main thread
      final PrecomputedTextCompat precomputedText = PrecomputedTextCompat.create(spannable, params);

      if (generation != searchGeneration || Thread.currentThread().isInterrupted()) return;

      searchHandler.post(() -> {
        if (generation != searchGeneration) return;

        matchOffsets.clear();
        matchOffsets.addAll(localMatches);
        currentMatchIndex = finalMatchIndex;
        lastSearchQuery = query;

        // Apply precomputed text layout instantly to prevent main-thread block
        TextViewCompat.setPrecomputedText(textView, precomputedText);

        if (matchCount > 0) {
          searchCount.setText((currentMatchIndex + 1) + "/" + matchCount);
          scrollToOffset(matchOffsets.get(currentMatchIndex));
        } else {
          searchCount.setText("0/0");
        }

        if (searchProgressIndicator != null) {
          searchProgressIndicator.setVisibility(View.GONE);
        }
      });
    });
  }

  private void scrollToOffset(int offset) {
    textView.post(() -> {
      android.text.Layout layout = textView.getLayout();
      if (layout != null) {
        int line = layout.getLineForOffset(offset);
        int lineTop = layout.getLineTop(line);
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.scrollView);
        if (scrollView != null) {
          scrollView.smoothScrollTo(0, lineTop);
        }
      }
    });
  }

  // === Menu Code (Unchanged) ===
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    int menuId = getResources().getIdentifier("main_menu", "menu", getPackageName());
    if (menuId == 0) {
      Log.e(TAG, "Menu resource 'main_menu' not found");
      return false;
    }
    getMenuInflater().inflate(menuId, menu);

    // Search
    MenuItem searchItem = menu.findItem(R.id.action_search);
    View searchViewView = searchItem.getActionView();
    View searchIcon = searchViewView.findViewById(R.id.icon_image);
    searchIcon.setOnClickListener(
        v -> {
          if (searchBar.getVisibility() == View.VISIBLE) {
            closeSearch();
          } else {
            openSearch();
          }
        });

    // Reset
    MenuItem resetItem = menu.findItem(R.id.action_reset);
    View resetView = resetItem.getActionView();
    View resetIcon = resetView.findViewById(R.id.icon_image);
    resetIcon.setOnClickListener(
        v -> {
          Intent intent = new Intent(this, MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
          finish();
          startActivity(intent);
        });

    // Settings
    MenuItem settingsItem = menu.findItem(R.id.settings);
    View settingsView = settingsItem.getActionView();
    View settingsIcon = settingsView.findViewById(R.id.icon_image);
    settingsIcon.setOnClickListener(
        v -> {
          startActivity(new Intent(this, SettingsActivity.class));
        });

    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    OTAUpdateHelper.checkForUpdatesIfDue(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      searchExecutor.shutdownNow();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
