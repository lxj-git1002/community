package com.project.community.entity;

import org.junit.runners.model.FrameworkMember;

/**
 * 封装分页的相关信息
 * */
public class Page {
    //当前页码
    private int current=1;
    //每一页的数量
    private int limit=3;
    //数据总量，用于计算页面总数
    private int rows;
    //查询路径（点击页面会跳转，所以其实就是一个url路径）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        //限定一页最多显示20条数据
        if (limit>=1&&limit<=20)
            this.limit = limit;
        else
            this.limit=20;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows>=0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset()
    {
        //数据库操作需要的是当前页的起始的行号
        //通过当前的页码算出其实的行号
        return (current - 1) * limit;
    }

    public int getTotal()
    {
        //显示总的页数
        if (rows%limit==0)
            return rows/limit;
        else
            return rows/limit +1;
    }

    public int getFrom()
    {
        //显示的起始页（如果有100页不能全部显示，只显示当前页的前面和后面几页即可）
        int from=current-2;
        return from<1?1:from;
    }

    public int getTo()
    {
        int to = current+2;
        return to>getTotal()?getTotal():to;
    }
}
