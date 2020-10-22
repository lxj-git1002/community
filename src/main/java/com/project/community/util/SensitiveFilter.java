package com.project.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

//敏感词过滤器
@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    //定义一个常量用于替换敏感词 例如将 嫖娼 替换为 **
    private static final String REPLACEMENT = "***";

    //定义前缀树的节点，定义成内部类
    private class TrieNode{

        //当前节点是不是关键词的结尾节点
        private boolean isKeywordEnd = false;

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //当前节点的子节点，可以有多个节点，将多个节点放入到一个map
        //key 描述的是当前节点的子节点的对应字符。value 是子节点
        private Map<Character,TrieNode> subNode = new HashMap<>();

        //添加子节点
        public void addSubNode(Character key, TrieNode value)
        {
            subNode.put(key,value);
        }

        //获取子节点
        public TrieNode getSubNode(Character key)
        {
            return subNode.get(key);
        }
    }

    //初始化树
    //根节点
    private TrieNode root = new TrieNode();

    /*
     * @Description 每次spring容器通过@component创建这个SensitiveFilter bean的时候都会自动初始化这个前缀树。
     * @Param
     * @return
     * @Author lxj
     * @Date 2020.10.22 21.59
     */
    @PostConstruct
    //通过这个注解，spring容器加载SensitiveFilter这个bean的时候就会知道，init是一个初始化函数，然后进行树的初始化
    public void init()
    {
        //通过类加载器进行加载配置文件
        //程序在编译后在target下的class文件夹中
        try(
                //字节流
                InputStream sensitiveWords = this.getClass().getClassLoader().getResourceAsStream("sensitive-words");
                //先将字节流转化为字符流InputStreamReader
                //在将字符流转化为buffer流，方便高效的读取
                BufferedReader reader = new BufferedReader(new InputStreamReader(sensitiveWords))
                )
        {
            String keyWord;
            //从文件中一行一行读取
            while ((keyWord=reader.readLine())!=null)
            {
                //将敏感词添加到前缀树中
                this.addSensitiveWord(keyWord);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("加载文件敏感词文件错误"+e.getMessage());
        }
    }

    /*
     * @Description 将一个敏感词添加到前缀树中
     * @Param [keyWord]
     * @return void
     * @Author lxj
     * @Date 2020.10.22 21.56
     */
    private void addSensitiveWord(String keyWord)
    {
        TrieNode temp = root;
        //遍历keyword，将keyword中的字符添加到temp节点的子节点中。然后temp节点等于它的子节点
        for (int i = 0; i < keyWord.length(); i++) {
            char c = keyWord.charAt(i);

            //先查找当前字符是否已经添加到树中了
            TrieNode subNode = temp.getSubNode(c);
            if (subNode==null)
            {
                //如果temp的子节点中没有添加过当前字符就创建添加
                subNode = new TrieNode();
                temp.addSubNode(c,subNode);
            }

            //让temp等于子节点，进入下次循环
            temp =  subNode;

            //对于字符串的最后一个字符进行结束位的标志
            if (i==keyWord.length()-1)
            {
                temp.setKeywordEnd(true);
            }
        }
    }

    /*
     * @Description 从字符s中检测敏感词，然后将敏感词替换，返回替换的结果
     * @Param s
     * @return String
     * @Author lxj
     * @Date 2020.10.22 21.55
     */
    public String filter (String s)
    {
        if (StringUtils.isBlank(s))
        {
            return null;
        }

        //定义三个指针
        TrieNode cur = root;
        int begin = 0;
        int position = 0;

        StringBuilder sb = new StringBuilder();

        while (position<s.length())
        {
            char c = s.charAt(position);

            //为了算法的严谨，需要对 😭嫖😊娼 这个也定义为敏感词。所以需要进行符号判断
            if (isSymbol(c)) {

                //如果当前的cur指针是根节点。此时这个符号不需要过滤
                //例如 😭嫖😊娼 cur在root时。第一个😭不需要过滤，只需要让指针begin走下一位即可
                if (cur==root)
                {
                    sb.append(c);
                    begin++;
                }
                //无论 cur在那，position都要走
                position++;
                continue;
            }

            //检测下一级节点
            cur = cur.getSubNode(c);
            if (cur==null)
            {
                //此时表明以begin开头的字符串不是敏感词。
                sb.append(s.charAt(begin));
                begin++;
                position=begin;
                cur=root;
            }
            else if (cur.isKeywordEnd())
            {
                //敏感词结尾
                //将begin 到 position这一段的字符替换
                sb.append(REPLACEMENT);
                position++;
                begin=position;
                cur=root;
            }
            else
            {
                //继续循环查找
                position++;
            }
        }

        //可能存在position走完，但是没有记录begin到position之间的字符
        sb.append(s.substring(begin));

        return sb.toString();
    }

    /*
     * @Description
     * @Param
     * @return boolean
     * @Author lxj
     * @Date 2020.10.22 22.15
     */
    private boolean isSymbol(Character c)
    {
        //判断c是否为普通字符。
        //0x2E80到0x9FFF是东亚文字的范围。
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }
}
