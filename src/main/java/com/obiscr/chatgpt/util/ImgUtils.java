package com.obiscr.chatgpt.util;

import cn.hutool.core.img.ImgUtil;
import com.intellij.util.ui.ImageUtil;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * @author wuzi
 */
public class ImgUtils extends ImgUtil {

    public static Image iconToImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon)icon).getImage();
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }

    public static BufferedImage scaleImage(int width, int height, ImageIcon imageIcon) {
        BufferedImage bi = null;
        try {
            bi = ImageUtil.createImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) bi.createGraphics();
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY));
            g2d.drawImage(imageIcon.getImage(), 0, 0, width, height, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bi;
    }
}
