package com.shamim.camerainfo.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.shamim.camerainfo.R;
import com.shamim.camerainfo.preference.SharedPrefValues;

public class StyleSettingsActivity extends BaseActivity {

  private ImageButton btnClose;
  private TextView tvActiveBadge;
  private LinearLayout cardDynamicTheme;
  private ImageView ivDynamicChecked;
  private ImageView ivStylePreview;
  private MaterialCardView cardActiveBadge;
  private ImageView ivActiveBadgeIcon;

  // Accent circles
  private LinearLayout btnAccentEmerald;
  private ImageView ivAccentEmeraldCheck;
  private LinearLayout btnAccentBlossom;
  private ImageView ivAccentBlossomCheck;
  private LinearLayout btnAccentOcean;
  private ImageView ivAccentOceanCheck;
  private LinearLayout btnAccentAmber;
  private ImageView ivAccentAmberCheck;
  private LinearLayout btnAccentCoral;
  private ImageView ivAccentCoralCheck;

  // Theme Segmented buttons
  private TextView btnThemeSystem;
  private TextView btnThemeLight;
  private TextView btnThemeDark;

  // AMOLED
  private MaterialCardView cardAmoledBlack;
  private MaterialSwitch switchAmoled;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_style_settings);

    // Bind views
    btnClose = findViewById(R.id.btn_close);
    tvActiveBadge = findViewById(R.id.tv_active_badge);
    cardDynamicTheme = findViewById(R.id.card_dynamic_theme);
    ivDynamicChecked = findViewById(R.id.iv_dynamic_checked);
    ivStylePreview = findViewById(R.id.iv_style_preview);
    cardActiveBadge = findViewById(R.id.card_active_badge);
    ivActiveBadgeIcon = findViewById(R.id.iv_active_badge_icon);

    btnAccentEmerald = findViewById(R.id.btn_accent_emerald);
    ivAccentEmeraldCheck = findViewById(R.id.iv_accent_emerald_check);
    btnAccentBlossom = findViewById(R.id.btn_accent_blossom);
    ivAccentBlossomCheck = findViewById(R.id.iv_accent_blossom_check);
    btnAccentOcean = findViewById(R.id.btn_accent_ocean);
    ivAccentOceanCheck = findViewById(R.id.iv_accent_ocean_check);
    btnAccentAmber = findViewById(R.id.btn_accent_amber);
    ivAccentAmberCheck = findViewById(R.id.iv_accent_amber_check);
    btnAccentCoral = findViewById(R.id.btn_accent_coral);
    ivAccentCoralCheck = findViewById(R.id.iv_accent_coral_check);

    btnThemeSystem = findViewById(R.id.btn_theme_system);
    btnThemeLight = findViewById(R.id.btn_theme_light);
    btnThemeDark = findViewById(R.id.btn_theme_dark);

    cardAmoledBlack = findViewById(R.id.card_amoled_black);
    switchAmoled = findViewById(R.id.switch_amoled);

    // Setup action listeners
    btnClose.setOnClickListener(v -> finish());

    setupAccentSelectors();
    setupThemeSelectors();
    setupAmoledSelector();

    updateUI();
  }

  private void setupAccentSelectors() {
    cardDynamicTheme.setOnClickListener(v -> setAccentPreference("0"));
    btnAccentEmerald.setOnClickListener(v -> setAccentPreference("1"));
    btnAccentBlossom.setOnClickListener(v -> setAccentPreference("2"));
    btnAccentOcean.setOnClickListener(v -> setAccentPreference("3"));
    btnAccentAmber.setOnClickListener(v -> setAccentPreference("4"));
    btnAccentCoral.setOnClickListener(v -> setAccentPreference("5"));
  }

  private void setAccentPreference(String value) {
    SharedPrefValues.putValue("app_theme_preference", value);
    recreateActivity();
  }

  private void setupThemeSelectors() {
    btnThemeSystem.setOnClickListener(v -> {
      SharedPrefValues.putValue("theme_preference", "0");
      recreateActivity();
    });
    btnThemeLight.setOnClickListener(v -> {
      SharedPrefValues.putValue("theme_preference", "3");
      recreateActivity();
    });
    btnThemeDark.setOnClickListener(v -> {
      SharedPrefValues.putValue("theme_preference", "2");
      recreateActivity();
    });
  }

  private void setupAmoledSelector() {
    boolean isAmoled = SharedPrefValues.getValue("amoled_black_mode", false);
    
    // Clear listener first to avoid premature triggering of recreateActivity during initialization
    switchAmoled.setOnCheckedChangeListener(null);
    switchAmoled.setChecked(isAmoled);

    switchAmoled.setOnCheckedChangeListener((buttonView, isChecked) -> {
      SharedPrefValues.putValue("amoled_black_mode", isChecked ? "1" : "0");
      recreateActivity();
    });

    cardAmoledBlack.setOnClickListener(v -> {
      boolean current = switchAmoled.isChecked();
      switchAmoled.setChecked(!current);
    });
  }

  private void recreateActivity() {
    runOnUiThread(() -> {
      applyLocalTheme();
      recreate();
    });
  }

  private void updateUI() {
    String currentAccent = SharedPrefValues.getValue("app_theme_preference", "0");
    String currentTheme = SharedPrefValues.getValue("theme_preference", "0");
    boolean isAmoled = SharedPrefValues.getValue("amoled_black_mode", false);
    boolean isLight = isLightThemeActive();

    // 1. Resolve Adaptive Colors based on current theme configuration
    int colorSurface = getColorFromAttr(com.google.android.material.R.attr.colorSurface);
    int colorSurfaceVariant = getColorFromAttr(com.google.android.material.R.attr.colorSurfaceVariant);
    int colorOutline = getColorFromAttr(com.google.android.material.R.attr.colorOutlineVariant);
    int colorOnSurfaceVariant = getColorFromAttr(com.google.android.material.R.attr.colorOnSurfaceVariant);

    int bgScreenColor;
    int bgCardColor;
    int strokeCardColor;
    int activeSegmentColor;
    int activeSegmentTextColor;
    int inactiveSegmentTextColor;

    if (isLight) {
      bgScreenColor = colorSurface;
      bgCardColor = getColorFromAttr(com.google.android.material.R.attr.colorSurfaceContainerLow);
      strokeCardColor = colorOutline;
      inactiveSegmentTextColor = colorOnSurfaceVariant;
    } else {
      // Dark Theme (Matches screenshot)
      bgScreenColor = isAmoled ? 0xFF000000 : getResources().getColor(R.color.style_dark_bg, getTheme());
      bgCardColor = getResources().getColor(R.color.style_card_bg, getTheme());
      strokeCardColor = getResources().getColor(R.color.style_card_stroke, getTheme());
      inactiveSegmentTextColor = getResources().getColor(R.color.style_text_mute, getTheme());
    }

    activeSegmentColor = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary);
    activeSegmentTextColor = getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary);

    // 2. Set backgrounds programmatically to adapt perfectly
    View styleRoot = findViewById(R.id.style_root);
    if (styleRoot != null) {
      styleRoot.setBackgroundColor(bgScreenColor);
    }

    if (ivStylePreview != null) {
      int previewColor = getPreviewTintColor(currentAccent);
      ivStylePreview.setColorFilter(previewColor, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    MaterialCardView cardAccentsContainer = findViewById(R.id.card_accents_container);
    if (cardAccentsContainer != null) {
      cardAccentsContainer.setCardBackgroundColor(ColorStateList.valueOf(bgCardColor));
      cardAccentsContainer.setStrokeColor(ColorStateList.valueOf(strokeCardColor));
    }

    MaterialCardView cardAmoledContainer = findViewById(R.id.card_amoled_black);
    if (cardAmoledContainer != null) {
      cardAmoledContainer.setCardBackgroundColor(ColorStateList.valueOf(bgCardColor));
      cardAmoledContainer.setStrokeColor(ColorStateList.valueOf(strokeCardColor));
    }

    View containerSegmented = findViewById(R.id.container_segmented);
    if (containerSegmented != null) {
      containerSegmented.setBackgroundTintList(ColorStateList.valueOf(bgCardColor));
    }

    // 3. Update Checkmarks for Accent Palette Options
    ivDynamicChecked.setVisibility(currentAccent.equals("0") ? View.VISIBLE : View.GONE);
    ivAccentEmeraldCheck.setVisibility(currentAccent.equals("1") ? View.VISIBLE : View.GONE);
    ivAccentBlossomCheck.setVisibility(currentAccent.equals("2") ? View.VISIBLE : View.GONE);
    ivAccentOceanCheck.setVisibility(currentAccent.equals("3") ? View.VISIBLE : View.GONE);
    ivAccentAmberCheck.setVisibility(currentAccent.equals("4") ? View.VISIBLE : View.GONE);
    ivAccentCoralCheck.setVisibility(currentAccent.equals("5") ? View.VISIBLE : View.GONE);

    // 4. Update Floating Badge Text and Dynamic Theme Colors for the badge
    switch (currentAccent) {
      case "0":
        tvActiveBadge.setText("Dynamic Active");
        break;
      case "1":
        tvActiveBadge.setText("Emerald Active");
        break;
      case "2":
        tvActiveBadge.setText("Blossom Active");
        break;
      case "3":
        tvActiveBadge.setText("Ocean Active");
        break;
      case "4":
        tvActiveBadge.setText("Amber Active");
        break;
      case "5":
        tvActiveBadge.setText("Coral Active");
        break;
      default:
        tvActiveBadge.setText("Default Active");
        break;
    }

    if (cardActiveBadge != null) {
      cardActiveBadge.setCardBackgroundColor(ColorStateList.valueOf(getBadgeContainerColor(currentAccent)));
    }
    int previewPrimaryColor = getPreviewTintColor(currentAccent);
    if (ivActiveBadgeIcon != null) {
      ivActiveBadgeIcon.setImageTintList(ColorStateList.valueOf(previewPrimaryColor));
    }
    if (tvActiveBadge != null) {
      tvActiveBadge.setTextColor(previewPrimaryColor);
    }

    // 5. Update Segmented Button Pills Concurrently
    btnThemeSystem.setBackgroundResource(0);
    btnThemeLight.setBackgroundResource(0);
    btnThemeDark.setBackgroundResource(0);

    btnThemeSystem.setTextColor(inactiveSegmentTextColor);
    btnThemeLight.setTextColor(inactiveSegmentTextColor);
    btnThemeDark.setTextColor(inactiveSegmentTextColor);

    // Highlight selected Theme segment
    TextView selectedSegment = null;
    switch (currentTheme) {
      case "3": // Light (Off)
        selectedSegment = btnThemeLight;
        break;
      case "2": // Dark (On)
        selectedSegment = btnThemeDark;
        break;
      default: // System (Follow)
        selectedSegment = btnThemeSystem;
        break;
    }

    if (selectedSegment != null) {
      android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
      gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
      gd.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
      gd.setColor(activeSegmentColor);
      selectedSegment.setBackground(gd);
      selectedSegment.setTextColor(activeSegmentTextColor);
    }

    switchAmoled.setChecked(isAmoled);
  }

  private int getPreviewTintColor(String currentAccent) {
    switch (currentAccent) {
      case "1": // Emerald
        return getResources().getColor(R.color.emerald_primary_dark, getTheme());
      case "2": // Blossom
        return getResources().getColor(R.color.blossom_primary_dark, getTheme());
      case "3": // Ocean
        return getResources().getColor(R.color.ocean_primary_dark, getTheme());
      case "4": // Amber
        return getResources().getColor(R.color.amber_primary_dark, getTheme());
      case "5": // Coral
        return getResources().getColor(R.color.coral_primary_dark, getTheme());
      case "0": // Dynamic/Default
      default:
        return getColorFromAttr(androidx.appcompat.R.attr.colorPrimary);
    }
  }

  private int getBadgeContainerColor(String currentAccent) {
    int color;
    switch (currentAccent) {
      case "1": // Emerald
        color = getResources().getColor(R.color.emerald_primaryContainer_dark, getTheme());
        break;
      case "2": // Blossom
        color = getResources().getColor(R.color.blossom_primaryContainer_dark, getTheme());
        break;
      case "3": // Ocean
        color = getResources().getColor(R.color.ocean_primaryContainer_dark, getTheme());
        break;
      case "4": // Amber
        color = getResources().getColor(R.color.amber_primaryContainer_dark, getTheme());
        break;
      case "5": // Coral
        color = getResources().getColor(R.color.coral_primaryContainer_dark, getTheme());
        break;
      case "0": // Dynamic/Default
      default:
        color = getColorFromAttr(com.google.android.material.R.attr.colorPrimaryContainer);
        break;
    }
    // Set alpha to ~75% (0xBF)
    return (color & 0x00FFFFFF) | 0xBF000000;
  }

  private int getColorFromAttr(int attribute) {
    TypedValue typedValue = new TypedValue();
    getTheme().resolveAttribute(attribute, typedValue, true);
    return typedValue.data;
  }
}
