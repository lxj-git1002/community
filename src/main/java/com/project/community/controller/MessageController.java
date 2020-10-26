package com.project.community.controller;

import com.project.community.entity.Message;
import com.project.community.entity.Page;
import com.project.community.entity.User;
import com.project.community.service.MessageService;
import com.project.community.service.UserService;
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
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /*
     * @Description 处理私信列表的请求
     * @Param
     * @return
     * @Author lxj
     * @Date 2020.10.24 22.27
     */
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {

        User user = hostHolder.getUser();

        //设置分页信息
        page.setLimit(1);
        page.setPath("/letter/list");
        //数据总数
        page.setRows(messageService.findConversationCount(user.getId()));

        //查询会话列表
        List<Message> list = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> map = new ArrayList<>();
        //将其他需要的信息存入到map中，例如未读消息数量等
        if (list != null)
        {
            for (Message message : list) {
                Map<String, Object> temp = new HashMap<>();
                //存入会话的最新消息
                temp.put("conversation",message);
                //存入会话的未读消息数量
                temp.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                //存入当前会话的所有消息数量
                temp.put("letterCount",messageService.findLetterCount(message.getConversationId()));

                //存入对方的头像信息
                //先获得对方的id
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                //获得对方user
                User targetUser = userService.findUserById(targetId);
                temp.put("target",targetUser);

                map.add(temp);
            }
        }

        model.addAttribute("conversations",map);

        //查到总的未读消息数量
        int total = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",total);

        return "/site/letter";
    }

    /*
     * @Description 显示私信的详情页面
     * @Param
     * @return
     * @Author lxj
     * @Date 2020.10.24 22.26
     */
    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String letterDetail(@PathVariable("conversationId")String conversationId, Page page, Model model)
    {
        //Integer.valueOf("abc");// 用于测试505错误
        //分页信息
        page.setLimit(2);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //分页查询
        //得到私信列表
        List<Message> letters = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());

        //对数据进行补充
        ArrayList<HashMap<String,Object>> map = new ArrayList<>();

        if (letters!=null)
        {
            for (Message letter : letters) {
                HashMap<String, Object> temp = new HashMap<>();
                temp.put("letter",letter);
                temp.put("fromUser",userService.findUserById(letter.getFromId()));

                map.add(temp);
            }
        }

        model.addAttribute("letters",map);

        //页面最顶显示和谁在交流
        model.addAttribute("target",getTargetUser(conversationId));

        //将未读消息设置未已读
        List<Integer> unreadLetterIds = getUnreadLetterIds(letters);
        if (!unreadLetterIds.isEmpty())
        {
            messageService.alterMessageStatus(unreadLetterIds,1);
        }

        return "/site/letter-detail";
    }

    //发送消息
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)//因为前端有数据提交到后台，所以用post方式
    @ResponseBody //发送消息是异步发送，不用刷新整个页面
    public String sendLetter(String content,String toName)
    {
       // Integer.valueOf("abd");

        //得到通信对方
        User toUser = userService.findUserByName(toName);
        if (toUser==null)
        {
            return CommunityUtil.getJsonString(1,"目标用户不存在");//code 1表示错误
        }

        User fromUser = hostHolder.getUser();

        //构造message对象
        Message message = new Message();
        message.setToId(toUser.getId());
        message.setFromId(fromUser.getId());

        StringBuilder sb = new StringBuilder();
        if (fromUser.getId()<toUser.getId())
        {
            sb=sb.append(fromUser.getId()).append("_").append(toUser.getId());
        }
        else
        {
            sb=sb.append(toUser.getId()).append("_").append(fromUser.getId());
        }

        message.setConversationId(sb.toString());
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return CommunityUtil.getJsonString(0);
    }



    /*
     * @Description 通过userid和 conversationId判断出当前user和那个人在交流。得到对方用户
     * @Param
     * @return
     * @Author lxj
     * @Date 2020.10.24 22.37
     */
    private User getTargetUser(String conversationId)
    {
        String[] s = conversationId.split("_");
        if (Integer.parseInt(s[0])==hostHolder.getUser().getId())
        {
            return userService.findUserById(Integer.parseInt(s[1]));
        }
        else
        {
            return userService.findUserById(Integer.parseInt(s[0]));
        }
    }

    /*
     * @Description 得到私信列表中所有未读的消息
     * @Param
     * @return
     * @Author lxj
     * @Date 2020.10.25 12.47
     */
    private List<Integer> getUnreadLetterIds(List<Message> letters)
    {
        List<Integer> res = new ArrayList<>();

        if (letters!=null)
        {
            for (Message letter : letters) {
                //判断当前用户是不是接受者，并且消息的状态是未读
                if (hostHolder.getUser().getId()==letter.getToId()&&letter.getStatus()==0)
                {
                    res.add(letter.getId());
                }
            }
        }

        return res;
    }
}
