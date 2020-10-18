package com.project.community.dao;

import com.project.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    //登录成功给数据表中插入ticket凭证
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")//设置sql自动生成主键，并且将生成的主键注入给id。
    int insertTicket(LoginTicket loginTicket);

    //查询
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改凭证状态，退出登录的时候用到
    @Update({
            "update login_ticket set status=#{status} ",
            "where ticket=#{ticket}"
    })
    //注解写sql也支持动态sql
//    @Update({
//            "<script>",
//            "update login_ticket set status=#{status} ",
//            "where ticket=#{ticket} ",
//            "<if test=\"ticket!=null\" > ",
//            "and 1=1",
//            "</if>",
//            "</script>"
//    })
    int updateStatus(String ticket,int status);//传入ticket，找到用户，将数据表中的用户的status修改为传入的status
}
