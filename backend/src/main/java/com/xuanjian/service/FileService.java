package com.xuanjian.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 按人物ID存储头像 —— 永远只有一张，覆盖旧图
     */
    public String uploadAvatar(MultipartFile file, Long characterId) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String filename = characterId + "." + ext;

        Path avatarDir = Paths.get(uploadDir, "avatar");
        Path thumbDir = Paths.get(uploadDir, "avatar", "thumb");
        Files.createDirectories(avatarDir);
        Files.createDirectories(thumbDir);

        // 删除旧文件（所有扩展名）
        String[] exts = {"png", "jpg", "jpeg", "gif", "webp"};
        for (String e : exts) {
            try {
                Files.deleteIfExists(avatarDir.resolve(characterId + "." + e));
            } catch (Exception ignored) {
            }
            try {
                Files.deleteIfExists(thumbDir.resolve(characterId + "." + e));
            } catch (Exception ignored) {
            }
        }

        // 保存原图
        Path originalPath = avatarDir.resolve(filename);
        Files.copy(file.getInputStream(), originalPath, StandardCopyOption.REPLACE_EXISTING);

        // 生成缩略图 150×150
        try {
            BufferedImage original = ImageIO.read(originalPath.toFile());
            if (original != null) {
                BufferedImage thumb = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = thumb.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int minDim = Math.min(original.getWidth(), original.getHeight());
                int x = (original.getWidth() - minDim) / 2;
                int y = (original.getHeight() - minDim) / 2;
                g.drawImage(original, 0, 0, 150, 150, x, y, x + minDim, y + minDim, null);
                g.dispose();
                ImageIO.write(thumb, "png", thumbDir.resolve(characterId + ".png").toFile());
            }
        } catch (Exception e) {
            log.warn("缩略图生成失败: {}", e.getMessage());
        }

        return filename;
    }

    private String getExtension(String name) {
        if (name == null || !name.contains(".")) return "png";
        String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        return (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("png") || ext.equals("gif") || ext.equals("webp")) ? ext : "png";
    }

    public String getAvatarUrl(Long characterId) {
        return "/api/file/image/avatar/" + characterId + ".png";
    }

    public String getAvatarThumbUrl(Long characterId) {
        return "/api/file/image/avatar/thumb/" + characterId + ".png";
    }
}