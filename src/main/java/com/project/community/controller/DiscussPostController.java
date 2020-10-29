package com.project.community.controller;

import com.project.community.entity.Comment;
import com.project.community.entity.DiscussPost;
import com.project.community.entity.Page;
import com.project.community.entity.User;
import com.project.community.service.CommentService;
import com.project.community.service.DiscussPostService;
import com.project.community.service.LikeService;
import com.project.community.service.UserService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.CommunityUtil;
import com.project.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;//帖子发布的数据中包含当前用户的信息

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    //因为是增加数据，浏览器会提交数据给服务器所以用post方法
    //返回一个字符串
    @ResponseBody
    public String addDiscussPost(String title,String content,String tag)
    {
        //发帖子之前先登录，所以首先判断当前是否登录
        //从hostholder中取user，如果没有取到则表示没有登录
        User user = hostHolder.getUser();
        if (user==null)
        {
            //此时返回给前端用户一个异步的消息
            //403表示没有权限
            return CommunityUtil.getJsonString(403,"你还没有登录，快去登录吧");
        }

        //如果当前对象登录了，则需要添加帖子，先创建一个discusspost对象
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setTag(tag);
        post.setContent(content);
        post.setCreateTime(new Date());

        //调用service的方法假如数据库
        discussPostService.addDiscussPost(post);

        /********/
        //返回异步消息
        return CommunityUtil.getJsonString(0,"发布成功，和大家一起讨论吧");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page)
    {
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);

        //根据post中的userid查询作者的名称
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //帖子的赞数量
        long num = likeService.findEntityLikeNum(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount",num);
        //点赞状态
        //如果没有登录则返回的是0，0表示没有点赞。因为没有登录不存在点赞
        int status = hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus",status);

        //评论分页信息
        page.setLimit(8);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());//当前帖子的评论数量

        //得到当前帖子的所有评论，并分页
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //对评论中userid等进行替换
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList!=null)
        {
            for (Comment comment : commentList) {
                //一个评论的显示
                Map<String, Object> commentVO = new HashMap<>();
                commentVO.put("comment",comment);
                //评论的作者
                commentVO.put("commentuser",userService.findUserById(comment.getUserId()));
                //评论的赞数量
                num = likeService.findEntityLikeNum(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount",num);
                //点赞状态
                //如果没有登录则返回的是0，0表示没有点赞。因为没有登录不存在点赞
                status = hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeStatus",status);


                //查询评论的评论 即回复
                //回复不需要做分页
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //对回复中的userid进行替换
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                if (replyList!=null)
                {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVO = new HashMap<>();

                        //回复的消息
                        replyVO.put("reply",reply);
                        replyVO.put("replyuser",userService.findUserById(reply.getUserId()));

                        //回复的赞数量
                        num = likeService.findEntityLikeNum(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeCount",num);
                        //点赞状态
                        //如果没有登录则返回的是0，0表示没有点赞。因为没有登录不存在点赞
                        status = hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeStatus",status);

                        //回复某人消息，某人的id就是target
                        //例如 A和B在互相回复，然后C看到后回复了B，此时的target就是B的id
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVO.put("target",target);

                        //将回复放入到list中
                        replyVOList.add(replyVO);
                    }
                }
                commentVO.put("replys",replyVOList);

                //回复的数量
                int count = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replycount",count);

                commentVOList.add(commentVO);
            }
        }

        model.addAttribute("comments",commentVOList);

        //帖子的回复功能


        return "/site/discuss-detail";
    }
}
