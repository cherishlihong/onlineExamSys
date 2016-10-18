
/**
 *绑定右侧点击事件 
 */
function clickNotificationNews(event) {
	var _this = $(event.target);
	var _url = _this.attr("data-url");
	if($('#manager-tabs').tabs("exists","通知消息")){
		$('#manager-tabs').tabs("select","通知消息");

		// 调用 'refresh' 方法更新选项卡面板的内容
		var tab = $('#manager-tabs').tabs('getSelected');  // 获取选择的面板
		tab.panel('refresh', _url);
	}else{
		$('#manager-tabs').tabs('add',{    
		    title:"通知消息",    
		    href:_url,    
		    closable:true      
		}); 
	}
};

/**
 *绑定流程点击事件 
 */
function shortcutNavigation(title,data_url){
	if(title){
		if($('#manager-tabs').tabs("exists",title)){
			$('#manager-tabs').tabs("select",title)
		}else{
			$('#manager-tabs').tabs('add',{    
			    title:title,    
			    href:data_url,    
			    closable:true      
			}); 
		}
	}
};
function changePassword(){
	$('#changePassword').dialog({
	    title: "修改密码",  
	    width: 500,    
	    height: 380,
	    iconCls:'icon-mini-add',
	    href:'../common/changePassword.jhtml',
	    cache: false, 
	    buttons:[{
	    	text:message("jlr.common.save"),
	    	iconCls:'icon-save',
			handler:function(){
				debugger;
				var validate = $('#changePassword_form').form('validate');
				if(validate){
					$.ajax({
						url:"../common/savePassword.jhtml",
						type:"post",
						data:$("#changePassword_form").serialize(),
						beforeSend:function(){
							$.messager.progress({
								text:message("jlr.common.saving")
							});
						},
						success:function(result,response,status){
							$.messager.progress('close');
							if(response == "success"){
								showSuccessMsg(result.content);
								$('#changePassword').dialog("close");
							}else{
								alertErrorMsg();
							}
						}
					});
				};
			}
		},{
			text:message("jlr.common.cancel"),
			iconCls:'icon-cancel',
			handler:function(){
				 $('#changePassword').dialog("close");
			}
	    }],
	    onOpen:function(){
	    	$('#changePassword').show();
	    },
	
	});
}
$(function(){
	
	if($(".nav-wrap li").length > 5 ){//如果菜单栏只有一行，就不显示“更多”
		$("#nav-switcher").show();
	}
	
	/**
	 *初始化右侧的选项卡
	 */
	$("#manager-tabs").tabs({
		fit:true,
		border:false
	});

	$("#nav-switcher").mouseover(function(){
		$(".nav-wrap").addClass("nav-silde");
		//$("#nav-switcherset").show();
	})
	$("#nav-wrap ul").mouseleave(function(){
		$("#nav-wrap").removeClass("nav-silde");
		//$("#nav-switcherset").hide();
	})
	
	$("#dropdownMenu1").dropdown();
	//初始化显示首页，隐藏菜单栏
	//$('.easyui-layout').layout('collapse','west');
	
	var westLayour = $('.easyui-layout').layout('panel','west');
	$("#nav-wrap > ul >li >a").click(function(){
		
		var $this = $(this);
		$(".left-content > ul").hide();
		if($this.text()=="首页"){
			$('.easyui-layout').layout('expand','west');
			$('#manager-tabs').tabs("select",'首页');
			
			westLayour.show();
		}else{
			
			$('.easyui-layout').layout('expand','west');
			westLayour.show();
		}
		
		$($this.attr("href")).show();
	})
	
	/**
	 *绑定左侧导航条的点击事件 
	 */
	$(".left-content a").click(function(){
		var _this = $(this);
		var _url = _this.attr("data-url");
		if(_this.text()){
			if($('#manager-tabs').tabs("exists",_this.text())){
				$('#manager-tabs').tabs("select",_this.text())
			}else{
				$('#manager-tabs').tabs('add',{    
				    title:_this.text(),    
				    href:_url,    
				    closable:true      
				}); 
			}
		}
	});

		if($("#useCarRequestMain-table-list")){
		$("#useCarRequestMain-table-list").datagrid({
			title:message("jlr.useCarRequest.list"),
			fitColumns:true,
			toolbar:"#useCarRequest_manager_tool",
			url:'../vehicleScheduling/listRequest.jhtml?childrenOrParent=children',  
			pagination:true,
			loadMsg:message("jlr.common.loading"),
			striped:true,
			columns:[
			   [
			      {field:'ck',checkbox:true},
			      {title:message("jlr.useCarRequest.title"),field:"title",width:80,sortable:true},
			      {title:message("jlr.useCarRequest.startDate"),field:"startDate",width:50,sortable:true, 
			    	  formatter:function(value,row,index){
			    		  return new Date(value).Format("yyyy-MM-dd hh:mm:ss");
			    	  }
			      },
			      {title:message("jlr.useCarRequest.startPositionDetails"),field:"startPositionDetails",width:100},
			      {title:message("jlr.useCarRequest.endPositionDetails"),field:"endPositionDetails",width:100},
			      {title:message("jlr.useCarRequest.status"),field:"status",width:30,sortable:true,
			    	  formatter: function(value,row,index){
				    	  if(value == "TO_CONFIRM"){
				    		  return  message("jlr.useCarRequest.to_confirm");
				    	  }else if (value == "DISTRIBUTED"){
				    		  return  message("jlr.useCarRequest.distributed");
				    	  }else if (value == "FINISHED"){
				    		  return  message("jlr.useCarRequest.finished");
				    	  }else if (value == "CANCELLED"){
				    		  return  message("jlr.useCarRequest.cancelled");
				    	  }else if (value == "REJECTED"){
				    		  return  message("jlr.useCarRequest.rejected");
				    	  }else if (value == "BREAK_CONTRACT"){
				    		  return  message("jlr.useCarRequest.break_contract");
				    	  }else if (value == "CLEARED"){
				    		  return  message("jlr.useCarRequest.cleared");
				    	  }
			      	  }  
			      },
			   ]
			]
		});
		}
})

