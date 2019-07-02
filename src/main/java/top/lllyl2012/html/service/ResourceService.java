package top.lllyl2012.html.service;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: volume
 * @Description:
 * @CreateDate: 2019/6/15 11:26
 */
public interface ResourceService {
    void toHtml(HttpServletResponse response);

    ResponseEntity<?> showPhotoDoc(HttpServletResponse response, String photo);
}
