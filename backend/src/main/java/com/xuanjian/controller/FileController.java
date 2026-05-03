package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private static final java.util.Set<String> ALLOWED_TYPES = java.util.Set.of(
            "image/png", "image/jpeg", "image/jpg", "image/gif", "image/webp"
    );
    private final FileService fileService;

    @PostMapping("/avatar/{characterId}")
    public Result<?> uploadAvatar(@PathVariable Long characterId, @RequestParam("file") MultipartFile file) {
        // 文件类型校验
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            return Result.error("只允许上传 PNG / JPG / GIF / WebP 格式的图片");
        }
        // 文件大小校验（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("图片大小不能超过 10MB");
        }
        try {
            String filename = fileService.uploadAvatar(file, characterId);
            return Result.ok(Map.of(
                    "filename", filename,
                    "url", fileService.getAvatarUrl(characterId),
                    "thumbUrl", fileService.getAvatarThumbUrl(characterId)
            ));
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}