package com.project.community.controller;

import com.project.community.entity.User;
import com.project.community.service.UserService;
import com.project.community.util.CommunityUtil;
import com.project.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    //持有用户的信息，代替session
    private HostHolder hostHolder;

    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage()
    {
        return "/site/setting";
    }

    //更新用户头像的请求
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImg, Model model)
    {
        if (headerImg==null)
        {
            //如果没有上传图像则返回当前设置页面
            model.addAttribute("error","请上传图像⏫");
            return "/site/setting";
        }

        //重命名
        String filename = headerImg.getOriginalFilename();
        //取得文件的格式名例如jpg png等
        String format = filename.substring(filename.lastIndexOf("."));

        if (StringUtils.isBlank(format))
        {
            model.addAttribute("error","文件格式错误");
            return "/site/setting";
        }

        //生成随机文件名
        filename = CommunityUtil.generaterUUID() + format;

        //确定文件的存放位置
        File dest = new File(uploadPath +"/"+ filename);
        try {
            //存储文件
            headerImg.transferTo(dest);
        }
        catch (Exception e)
        {
            LOGGER.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常"+e);
        }

        //存储成功后更新当前用户的头像路径 web访问路径http:localhost:8088/community/user/header/图片
        //更新当前用户需要先获得当前用户

        User user = hostHolder.getUser();
        //web路径
        String headerUrl = domain+contextPath+"/user/header/"+filename;

        //更新头像
        userService.updateHeader(user.getId(),headerUrl);

        //更新成功跳转到首页
        return "redirect:/index";
    }

    //外界获取头像的请求
    //该方法获得图像，然后将图返回给浏览器
    /*
    * 重点：
    * 在某个页面中都会加载头像：通过loginUser.headerUrl进行加载，得到的是路径：http:localhost:8088/community/user/header/图片
    * 然后浏览器会到这个路径下去加载图片。http:localhost:8088/community/user/header/图片 这个路径就会路由到当前的getHeader（）函数下
    * 然后完成图片的读取显示功能。
    * */
    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response)
    {

        //找到服务器存储文件的名字
        filename = uploadPath + "/" + filename;

        //获得向浏览器输出文件的格式
        String format = filename.substring(filename.lastIndexOf("."));

        //响应文件
        response.setContentType("image/"+format);
        //因为响应的图片是二进制文件，所以用字节流
        try(
                //创建输出流。输出到浏览器
                OutputStream os = response.getOutputStream();
                //创建一个文件的输入流。 将fis流中的数据输出到os流，然后通过os流输出到浏览器
                FileInputStream fis = new FileInputStream(filename);
                //fis流是自己创建的需要手动关闭
        )
        {
            //从这个fis流中输出文件
            //创建一个缓冲区
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1)//每次最多读取buffer这么多数据.b不等于-1表示读取到了数据
            {
                os.write(buffer, 0, b);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("读取头像失败"+e.getMessage());
        }
    }
}
