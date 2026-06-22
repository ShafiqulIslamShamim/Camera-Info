package com.pixplicity.sharp;

import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

public class Sharp {
  private final SVG svg;

  private Sharp(SVG svg) {
    this.svg = svg;
  }

  public static Sharp loadString(String svgString) {
    try {
      return new Sharp(SVG.getFromString(svgString));
    } catch (SVGParseException e) {
      throw new RuntimeException("Failed to parse SVG string", e);
    }
  }

  public Drawable getDrawable() {
    Picture picture = svg.renderToPicture();
    return new PictureDrawable(picture);
  }
}
