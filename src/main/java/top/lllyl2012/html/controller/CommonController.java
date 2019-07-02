package top.lllyl2012.html.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.lllyl2012.html.service.ResourceService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @Author: volume
 * @Description:
 * @CreateDate: 2019/6/15 11:25
 */
@Controller
public class CommonController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ResourceService resourceService;

    /**
     *各种文件在线预览
     */
    @RequestMapping("/toHtml")
    public void toHtml(HttpServletResponse response) throws Throwable {
        resourceService.toHtml(response);
    }

    /**
     * 下载图片
     * @param response
     * @param photo
     * @return
     */
    @RequestMapping(value = {"/{photo}","/image/word/media/{photo}"},produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<?> getImage2(HttpServletResponse response, @PathVariable String photo) {
        return resourceService.showPhotoDoc(response,photo);
    }

    /**
     * 下载视频
     */
    @RequestMapping(value = "/video")
    @ResponseBody
    public ResponseEntity<?> getVideo(HttpServletResponse response,  String file) throws FileNotFoundException {
        Resource resource = resourceLoader.getResource("classpath:static/" +file);
        return ResponseEntity.ok(resource);
    }
}
