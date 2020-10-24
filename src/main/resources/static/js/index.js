$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//发送异步请求
	//获取标题内容和标签
	var title = $("#recipient-name").val();
	var tag = $("#tag-name").val();
	var content = $("#message-text").val();

	//发送请求
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{
			"title":title,
			"tag":tag,
			"content":content
		},
		function (data)
		{
			data = $.parseJSON(data);

			//在提示框中显示返回的消息
			$("#hintBody").text(data.msg);

			//显示提示框
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");

				//如果添加成功则刷新当前页面
				if (data.code==0)
				{
					window.location.reload();
				}
			}, 2000);
			//两秒之后提示框消失
		}
	)
}