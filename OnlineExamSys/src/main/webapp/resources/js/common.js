// 对Date的扩展，将 Date 转化为指定格式的String 
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
// 例子： 
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function(fmt) {
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

var setting = {
	priceScale : "2",
	priceRoundType : "roundHalfUp",
	uploadImageExtension : "jpg,jpeg,bmp,gif,png",
	uploadFlashExtension : "swf,flv",
	uploadMediaExtension : "swf,flv,mp3,wav,avi,rm,rmvb",
	uploadFileExtension : "zip,rar,7z,doc,docx,xls,xlsx,ppt,pptx"
};

/**
 * 公共提示信息 response==success
 * 
 * @param msgStr
 */
function showSuccessMsg(msgStr) {
	$.messager.show({
		title : message("sy.common.prompt"),
		msg : msgStr,
		timeout : 3000,
		showType : 'slide'
	});
}

/**
 * 公共提示信息 error
 */
function alertErrorMsg() {
	$.messager.alert(message("sy.common.fail"),
			message("sy.common.unknow.error"), 'error');
}

// 添加Cookie
function addCookie(name, value, options) {
	if (arguments.length > 1 && name != null) {
		if (options == null) {
			options = {};
		}
		if (value == null) {
			options.expires = -1;
		}
		if (typeof options.expires == "number") {
			var time = options.expires;
			var expires = options.expires = new Date();
			expires.setTime(expires.getTime() + time * 1000);
		}
		document.cookie = encodeURIComponent(String(name))
				+ "="
				+ encodeURIComponent(String(value))
				+ (options.expires ? "; expires="
						+ options.expires.toUTCString() : "")
				+ (options.path ? "; path=" + options.path : "")
				+ (options.domain ? "; domain=" + options.domain : ""),
				(options.secure ? "; secure" : "");
	}
}

// 获取Cookie
function getCookie(name) {
	if (name != null) {
		var value = new RegExp("(?:^|; )" + encodeURIComponent(String(name))
				+ "=([^;]*)").exec(document.cookie);
		return value ? decodeURIComponent(value[1]) : null;
	}
}

// 移除Cookie
function removeCookie(name, options) {
	addCookie(name, null, options);
}
//导出excel数据
function exportData(control, form) {
	// 建议一次导出excel数据的最大值为500
	var maxSize = 500;
	$.ajax({
				url : "../" + control + "/count.jhtml",
				type : "post",
				data : $("#" + form).serialize(),
				success : function(result, response, status) {
					if (result.count != null) {
						var text = "";
						if (result.count == 0) {
							// "当前条件无可导出的数据。"
							text = message("yly.common.notice.current_condition_no_export_data");
							$.messager.alert(message("yly.common.notice"),
									text, 'warning');
						} else if (result.count <= maxSize) {
							// "确定导出 {0}条记录？"
							text = message(
									"yly.common.notice.comfirm_export_data",
									result.count);
							$.messager
									.confirm(
											message("yly.common.confirm"),
											text,
											function(r) {
												if (r) {
													$("#" + form)
															.attr(
																	"action",
																	"../"
																			+ control
																			+ "/exportData.jhtml");
													$("#" + form).attr(
															"target", "_blank");
													$("#" + form).submit();
												}

											});
						} else {
							// "导出数据超过 "+maxSize+" 条数据，建议搜索查询条件以缩小查询范围，再导出。";
							text = message(
									"yly.common.notice.export_data_too_much_advice_use_filter",
									maxSize);
							$.messager
									.confirm(
											message("yly.common.notice"),
											text,
											function(r) {
												if (!r) {
													// "导出共有"+ result.count
													// +"条数据，导出超过 "+maxSize+"
													// 条数据可能需要您耐心等待，仍需操作请确定继续。";
													text = message(
															"yly.common.notice.need_wait_export_too_much_data",
															result.count,
															maxSize);
													$.messager
															.confirm(
																	message("yly.common.confirm"),
																	text,
																	function(
																			yes) {
																		if (yes) {
																			$(
																					"#"
																							+ form)
																					.attr(
																							"action",
																							"../"
																									+ control
																									+ "/exportData.jhtml");
																			$(
																					"#"
																							+ form)
																					.attr(
																							"target",
																							"_blank");
																			$(
																					"#"
																							+ form)
																					.submit();
																		}
																	});
												}
											})
						}
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("error");
					$.messager.progress('close');
					alertErrorMsg();
				}
			});
}
function exportExcel(control,form,totalRecord){
	var total = $('#'+totalRecord).val();
	var max = 500;
	if(total > max){
	$.messager
	.confirm(
			message("yly.common.notice"),
			text,
			function(r) {
				if (!r) {
					// "导出共有"+ result.count
					// +"条数据，导出超过 "+maxSize+"
					// 条数据可能需要您耐心等待，仍需操作请确定继续。";
					text = message(
							"yly.common.notice.need_wait_export_too_much_data",
							result.count,
							maxSize);
				}else{
					$("#"+ form).attr("action","../"+ control+ "/exportData.jhtml");
					$("#"+ form).attr("target","_blank");
					$("#"+ form).submit();
				}
			});
	}else if (total == 0 ){
		text = message("yly.common.notice.current_condition_no_export_data");
		$.messager.alert(message("yly.common.notice"),
				text, 'warning');
	}else{
		$("#"+ form).attr("action","../"+ control+ "/exportData.jhtml");
		$("#"+ form).attr("target","_blank");
		$("#"+ form).submit();
	}
	
}

/**
 * 选中datagrid记录进行编辑，检查是否有选中记录，而且只允许选择一行进行编辑
 */
function editRowValidate(datagrid_id){
	var _id = datagrid_id; //datagrid的id
	if($("#" + _id)){
		var _edit_row = $("#" + _id).datagrid('getSelected');
		if( _edit_row == null ){
			$.messager.alert(message("jlr.common.notice"), "至少选择一条记录");  
			return false;
		}
		var _checked_row =$("#" + _id).datagrid('getChecked');
		if( _checked_row != null && _checked_row.length > 1){
			$.messager.alert(message("jlr.common.notice"), "只允许选择一条记录");  
			return false;
		}
		return true;
	}
	return false;
}

/**
 * 删除公用方法 id table_list id url 删除是向后台发送的链接
 */
function listRemove(id, url) {
	var _id = id;
	var _url = url
	var _rows = $("#" + _id).datagrid('getSelections');
	if (_rows.length == 0) {
		$.messager.alert(message("sy.common.prompt"),
				message("sy.common.select.deleteRow"), 'warning');
	} else {
		var _ids = [];
		for (var i = 0; i < _rows.length; i++) {
			_ids.push(_rows[i].id);
		}
		if (_ids.length > 0) {
			$.messager.confirm(message("sy.common.confirm"),
					message("sy.common.delete.confirm"), function(r) {
						if (r) {
							$.ajax({
								url : _url,
								type : "post",
								traditional : true,
								data : {
									"ids" : _ids
								},
								beforeSend : function() {
									$.messager.progress({
										text : message("sy.common.progress")
									});
								},
								success : function(result, response, status) {
									$.messager.progress('close');
									var resultMsg = result.content;
									if (response == "success") {
										showSuccessMsg(resultMsg);
										$("#" + _id).datagrid('reload');
									} else {
										alertErrorMsg();
									}
								}
							});
						}
					})
		}

	}
}


// 查询用户(TenantUser)
function searchTenantUser(id) {
	$('#searchTenantUser').dialog({
						title : message("ov.tenantUser.search"),
						width : 1000,
						height : 500,
						modal : true,
						cache : false,
						href : '../tenantUser/commonTenantUserSearch.jhtml',
						buttons : [ {
							text : message("ov.common.cancel"),
							iconCls : 'icon-cancel',
							handler : function() {
								$('#searchTenantUser').dialog("close");
							}
						} ],
						onLoad : function() {
							/**
							 * 此datagrid 用户展示用户(TenantUser)数据,并且提供查询功能
							 */
							$("#common-tenantUser-table-list").datagrid({
												title : message("ov.tenantAccount.list"),
												fitColumns : true,
												url : '../tenantUser/list.jhtml',
												pagination : true,
												loadMsg : message("ov.common.loading"),
												striped : true,
												onDblClickRow : function(rowIndex, rowData) {
													$("#" + id + "ID").val(rowData.id);
													$("#" + id).textbox('setValue',rowData.realName);
													$('#searchTenantUser').dialog("close");
												},
												columns : [ [
												             {title : message("ov.common.name"),field : "realName",width : 100,sortable : true},
												             {title : message("ov.common.age"),field : "age",width : 100,sortable : true},
												             {title : message("ov.tenantUser.staffID"),field : "staffID",width : 100,sortable : true},
												             {title : message("ov.tenantUser.staffStatus"),field : "staffStatus",width : 100,sortable : true,formatter : function(value, row,index) {
																	if (value == "INSERVICE") {
																		return message("ov.tenantUser.staffStatus.inService");
																	} else if (value = "OUTSERVICE") {
																		return message("ov.tenantUser.staffStatus.outService");
																	}
															}},
															{title : message("ov.tenantUser.department"),field : "department",width : 100,sortable : true,formatter : function(value, row,index) {
																	if (value) {
																		return value.name;
																	} else {
																		return value;
																	}
															}},
															{title : message("ov.tenantUser.position"),field : "position",width : 100,sortable : true,formatter : function(value, row,index) {
																	if (value) {
																		return value.name;
																	} else {
																		return value;
																	}
															}},
															{title : message("ov.tenantUser.hireDate"),field : "hireDate",width : 100,sortable : true,formatter : function(value, row,index) {
																	return new Date(value).Format("yyyy-MM-dd");
															}}, 
											 ] ]
							});

							$("#common-tenantUser-search-btn").click(function() {
												var _queryParams = $("#common-tenantUser-search-form").serializeJSON();
												$('#common-tenantUser-table-list').datagrid('options').queryParams = _queryParams;
												$("#common-tenantUser-table-list").datagrid('reload');
							});
						}
					});
}
//查询角色(Role)
function searchRoles(id) {
	$('#searchRole').dialog({
						title : message("ov.role.search"),
						width : 1000,
						height : 500,
						modal : true,
						cache : false,
						href : '../role/commonRolesSearch.jhtml',
						buttons : [ {
							text : message("ov.common.cancel"),
							iconCls : 'icon-cancel',
							handler : function() {
								$('#searchRole').dialog("close");
							}
						} ],
						onLoad : function() {
							/**
							 * 此datagrid 用户展示角色数据,并且提供查询功能
							 */
							$("#common-roles-table-list").datagrid({
												title : message("ov.role.list"),
												fitColumns : true,
												url : '../role/list.jhtml',
												pagination : true,
												loadMsg : message("ov.common.loading"),
												striped : true,
												onDblClickRow : function(rowIndex, rowData) {
													$("#" + id + "ID").val(rowData.id);
													$("#" + id).textbox('setValue',rowData.name);
													$('#searchRole').dialog("close");
												},
												columns : [ [
												             {title : message("ov.role.name"),field : "name",width : 20,align : 'center',formatter : function(value, row,index) {
												            	 	return row.name;
															 }},
															 {title : message("ov.role.description"),field : "description",width : 80,align : 'center',formatter : function(value, row,index) {
																 	return row.description;
															}} 
												] ]
							});
							$("#common-role-search-btn").click(function() {
								var _queryParams = $("#common-role-search-form").serializeJSON();
								$('#common-roles-table-list').datagrid('options').queryParams = _queryParams;
								$("#common-roles-table-list").datagrid('reload');
							});
						}
					});
}
//查询车辆(Vehicle)
function searchVehicle(id,unbind) {
	
	$('#searchVehicle').dialog({
						title : message("ov.role.search"),
						width : 550,
						height : 400,
						modal : true, 
						cache : false,
						href : '../vehicle/commonVehiclesSearch.jhtml',
						buttons : [ {
							text : message("ov.common.cancel"),
							iconCls : 'icon-cancel',
							handler : function() {
								$('#searchVehicle').dialog("close");
							}
						} ],
						onLoad : function() {
							var url ='../vehicle/list.jhtml';
							if(unbind){
								url = '../vehicle/listUnBuindVehicle.jhtml';
							}
							/**
							 * 此datagrid 用户展示车辆数据,并且提供查询功能
							 */
							$("#common-vehicles-table-list").datagrid({
								url:url,  
								pagination:true,
								loadMsg:message("ov.common.loading"),
								striped:true,
								singleSelect:true,
								onDblClickRow : function (rowIndex, rowData){
									$("#" + id + "ID").val(rowData.id);
									$("#" + id).textbox('setValue',rowData.plate);
									$('#searchVehicle').dialog("close");
								},
								columns:[[
									{field : 'ck',checkbox : true},
									{title : "车牌号",field : "plate",width :"47%",align : 'center',sortable : true},
									{title : "品牌图标",field : "brandIcon",width :"47%",align : 'center',sortable : true},					
								]]
							});
							$("#common_vehicle_search_btn").click(function() {
								var _queryParams = $("#common_vehicle_search_form").serializeJSON();
								$('#common-vehicles-table-list').datagrid('options').queryParams = _queryParams;
								$("#common-vehicles-table-list").datagrid('reload');
							});
						}
					});
}

//查询终端用户用户
function searchEndUser(id) {
	$('#commonMainDialog')
			.dialog(
					{
						title : message("ov.endUser.search"),
						width : 1000,
						height : 500,
						modal : true,
						cache : false,
						href : '../endUser/commonEndUserSearch.jhtml',
						buttons : [ {
							text : message("yly.common.cancel"),
							iconCls : 'icon-cancel',
							handler : function() {
								$('#commonMainDialog').dialog("close");
							}
						} ],
						onLoad : function() {
							$("#common-endUser-table-list")
									.datagrid(
											{
												title : message("ov.endUser.list"),
												fitColumns : true,
												url : '../endUser/list.jhtml',
												pagination : true,
												loadMsg : message("ov.common.loading"),
												striped : true,
												onDblClickRow : function(
														rowIndex, rowData) {
													$("#" + id + "ID").val(
															rowData.id);
													$("#" + id).textbox(
															'setValue',
															rowData.realName);
													if($("#vehicleMaintainMobileNum")!=undefined){
														
														$("#vehicleMaintainMobileNum").textbox('setValue',rowData.mobileNum);
													};
													if($("#vehicleMaintain_plate")!=undefined){
														$("#vehicleMaintain_plate").combobox({
															url:"../vehicle/findVehicleUnderUser.jhtml?userId="+rowData.id,
														    valueField:'id',
														    method:"get",
														    textField:'plate',
														    editable : false,
														    required:true,
														    prompt:message("ov.common.please.select"),
														    onSelect:function(record){
														    	$("#dashboardMileage").textbox('setValue',record.dashboardMileage);
														    	$("#lastMaintainMileage").textbox('setValue',record.dashboardMileage);
														    }
														});
													};
													$('#commonMainDialog')
															.dialog("close");
												},
												columns : [ [
														      {field:'ck',checkbox:true},
														      {title:message("ov.endUser.userName"),field:"userName",width:100,sortable:true},
														      {title:message("ov.endUser.realName"),field:"realName",width:100,sortable:true   },
														      {title:message("ov.endUser.mobileNum"),field:"mobileNum",width:100,sortable:true},
														      {title:message("ov.endUser.qq"),field:"qq",width:100,sortable:true},
														      {title:message("ov.endUser.accoutStatus"),field:"accoutStatus",width:100,sortable:true,
														    	  formatter: function(value,row,index){
															    	  if(value == "ACTIVED"){
															    		  return  message("ov.endUser.active");
															    	  }else if (value == "LOCKED"){
															    		  return  message("ov.endUser.locked");
															    	  }
														      	  }  
														      },
														      {title:message("ov.endUser.loginDate"),field:"loginDate",width:100,sortable:true,formatter: function(value,row,index){
																	return new Date(value).Format("yyyy-MM-dd:hh:mm:ss");
																}
														      },
														   ] ]

											});

							$("#common_endUser_search_btn")
									.click(
											function() {
												var _queryParams = $(
														"#common_endUser_search_form")
														.serializeJSON();
												$(
														'#common-endUser-table-list')
														.datagrid('options').queryParams = _queryParams;
												$(
														"#common-endUser-table-list")
														.datagrid('reload');
											})
						}
					});

}
//查询设备信息
function searchDevice(id) {
	$('#commonMainDialog')
			.dialog(
					{
						title : message("yly.deviceInfo.search"),
						width : 1000,
						height : 500,
						modal : true,
						cache : false,
						href : '../deviceInfo/commonDeviceInfoSearch.jhtml',
						buttons : [ {
							text : message("yly.common.cancel"),
							iconCls : 'icon-cancel',
							handler : function() {
								$('#commonMainDialog').dialog("close");
							}
						} ],
						onLoad : function() {
							/**
							 * 此datagrid 用户展示老人数据,并且提供查询功能
							 */
							$("#common-deviceInfo-table-list")
									.datagrid(
											{
												title : message("yly.deviceInfo"),
												fitColumns : true,
												url : '../deviceInfo/list.jhtml',
												pagination : true,
												loadMsg : message("yly.common.loading"),
												striped : true,
												onDblClickRow : function(
														rowIndex, rowData) {
													$("#" + id + "ID").val(
															rowData.id);
													$("#" + id).textbox(
															'setValue',
															rowData.deviceNo);
													
													$('#commonMainDialog')
															.dialog("close");
												},
												onBeforeLoad:function(param){
													param.deviceStatusSearch = "STORAGEOUT";
												},
												columns : [ [
														      {field:'ck',checkbox:true},
														      {title:message("ov.deviceInfo.deviceNO"),field:"deviceNo",width:100,sortable:true},
														      {title:message("ov.deviceInfo.deviceType"),field:"type",width:100,sortable:true,
														    	  formatter: function(value,row,index){
															    	  if(value != null){
															    		  return  value.name;
															    	  }
														      	  }},
														      {title:message("ov.deviceInfo.deviceStatus"),field:"deviceStatus",width:100,sortable:true,
														    	  formatter: function(value,row,index){
															    	  if(value == "INITED"){
															    		  return  message("ov.deviceInfo.deviceStatus.INITED");
															    	  }else if (value = "SENDOUT"){
															    		  return  message("ov.deviceInfo.deviceStatus.SENDOUT");
															    	  }else if (value = "STORAGEOUT"){
															    		  return  message("ov.deviceInfo.deviceStatus.STORAGEOUT");
															    	  }else if (value = "BINDED"){
															    		  return  message("ov.deviceInfo.deviceStatus.BINDED");
															    	  }else if (value = "REFUNDED"){
															    		  return  message("ov.deviceInfo.deviceStatus.REFUNDED");
															    	  }
														      	  }  
														      },
														      {title:message("ov.deviceInfo.bindTime"),field:"bindTime",width:100,sortable:true,formatter: function(value,row,index){
																	return new Date(value).Format("yyyy-MM-dd:hh:mm:ss");
																}
														      },
														   ] ]

											});

							$("#common_deviceInfo_search_btn")
									.click(
											function() {
												var _queryParams = $(
														"#common_deviceInfo_search_form")
														.serializeJSON();
												$(
														'#common-deviceInfo-table-list')
														.datagrid('options').queryParams = _queryParams;
												$(
														"#common-deviceInfo-table-list")
														.datagrid('reload');
											})
						}
					});

}

function formReset(formId, tableId) {
	$('#' + formId)[0].reset();
	var _queryParams = {}
	$('#' + tableId).datagrid('options').queryParams = _queryParams;
}

// 返回指定长度字符串截取,超出部分不显示,以...作为后缀显示
function formatLongString(str, len) {
	if (str != null && str != "" && len > 0) {
		if (str.length > len) {
			return '<span title="' + str + '">' + str.substring(0, len) + "..."
					+ '<span>'
		} else {
			return str;
		}
	}
	return "";
}
// 导出excel数据
function exportData(control, form) {
	// 建议一次导出excel数据的最大值为500
	var maxSize = 500;
	$
			.ajax({
				url : "../" + control + "/count.jhtml",
				type : "post",
				data : $("#" + form).serialize(),
				success : function(result, response, status) {
					if (result.count != null) {
						var text = "";
						if (result.count == 0) {
							// "当前条件无可导出的数据。"
							text = message("ov.common.notice.current_condition_no_export_data");
							$.messager.alert(message("ov.common.notice"),
									text, 'warning');
						} else if (result.count <= maxSize) {
							// "确定导出 {0}条记录？"
							text = message(
									"ov.common.notice.comfirm_export_data",
									result.count);
							$.messager
									.confirm(
											message("ov.common.confirm"),
											text,
											function(r) {
												if (r) {
													$("#" + form)
															.attr(
																	"action",
																	"../"
																			+ control
																			+ "/exportData.jhtml");
													$("#" + form).attr(
															"target", "_blank");
													$("#" + form).submit();
												}

											});
						} else {
							// "导出数据超过 "+maxSize+" 条数据，建议搜索查询条件以缩小查询范围，再导出。";
							text = message(
									"ov.common.notice.export_data_too_much_advice_use_filter",
									maxSize);
							$.messager
									.confirm(
											message("ov.common.notice"),
											text,
											function(r) {
												if (!r) {
													// "导出共有"+ result.count
													// +"条数据，导出超过 "+maxSize+"
													// 条数据可能需要您耐心等待，仍需操作请确定继续。";
													text = message(
															"ov.common.notice.need_wait_export_too_much_data",
															result.count,
															maxSize);
													$.messager
															.confirm(
																	message("ov.common.confirm"),
																	text,
																	function(
																			yes) {
																		if (yes) {
																			$(
																					"#"
																							+ form)
																					.attr(
																							"action",
																							"../"
																									+ control
																									+ "/exportData.jhtml");
																			$(
																					"#"
																							+ form)
																					.attr(
																							"target",
																							"_blank");
																			$(
																					"#"
																							+ form)
																					.submit();
																		}
																	});
												}
											})
						}
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("error");
					$.messager.progress('close');
					alertErrorMsg();
				}
			});
}

