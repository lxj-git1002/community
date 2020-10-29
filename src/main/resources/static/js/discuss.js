function like(button,entityType,entityId,entityUserId)
{
    $.post(
        //路径
        CONTEXT_PATH+"/like",
        {
            "entityType":entityType,
            "entityId":entityId,
            "entityUserId":entityUserId
        },
        function (data)
        {
            data = $.parseJSON(data);
            if (data.code==0)
            {
                //如果点赞成功则修改数据
                $(button).children("i").text(data.likeCount);
                $(button).children("b").text(data.likeStatus==1?'已赞':'赞');

            }
            else
            {
                alert(data.msg)
            }
        }
    );
}