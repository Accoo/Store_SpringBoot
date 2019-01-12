package cn.bluebubbles.store.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author yibo
 * @date 2019-01-12 16:36
 * @description
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
