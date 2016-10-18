var messages = {
		/**
		 * common
		 */
		"sy.common.createDate" : "创建时间",
		"sy.common.modifyDate" : "修改时间",
		"sy.common.title" : "标题",
		"sy.common.loading" : "加载中...",
		"sy.common.save" : "保存",
		"sy.common.saving" : "保存中...",
		"sy.common.add" : "添加",
		"sy.common.edit" : "编辑",
		"sy.common.remove" : "删除",
		"sy.common.cancel" : "取消",
	    "sy.common.close" : "关闭",
		"sy.common.progress" : "正在处理中...",
		"sy.common.prompt" : "操作提示",
		"sy.common.success" : "操作成功",
		"sy.common.fail" : "操作失败",
		"sy.common.unknow.error" : "未知错误",
		"sy.common.select.editRow" : "请选择要编辑的记录",
		"sy.common.select.editRow.unique" : "只能选择一条记录修改",
		"sy.common.select.deleteRow" : "请选择要删除的内容",
		"sy.common.select.ship":"请选择要显示历史轨迹的船只",
		"sy.common.confirm" : "确认",
		"sy.common.delete.confirm" : "您确认想要删除记录吗？",
		"sy.common.gender": "性别",
		"sy.common.name":"姓名",
		"sy.common.age":"年龄",
		"sy.common.birthday":"出生日期",
		"sy.common.infoChannel":"信息来源",
		"sy.common.remark":"备注",
		"sy.common.phonenumber":"电话号码",
		"sy.common.male":"男",
		"sy.common.female":"女",
		"sy.common.other":"其它",
		"sy.common.yes":"是",
		"sy.common.no":"否",
		"sy.common.notice":"提示",
		"sy.common.notice.selectDate":"请先选择日期",
		"sy.common.detail":"详情",
		"sy.common.idcard":"身份证号码",
		"sy.common.disable":"禁用",
		"sy.common.enable":"启用",
		"sy.common.bedRoom":"房间",
		"sy.common.please.select":"请选择...",
		"sy.common.insertDate": "录入时间",
		"sy.common.notice.pathEmptyValue":"没有该时间段的历史轨迹",
		"sy.common.notice.current_condition_no_export_data":"当前条件无可导出的数据。",
		"sy.common.notice.comfirm_export_data":"确定导出 {0} 条记录？",
		"sy.common.notice.export_data_too_much_advice_use_filter":"导出数据超过 {0} 条数据，建议搜索查询条件以缩小查询范围，再导出。",
		"sy.common.notice.need_wait_export_too_much_data":"导出共有 {0} 条数据，导出超过 {1} 条数据可能需要您耐心等待，仍需操作请确定继续。",
		"sy.common.address":"地址",
		"sy.common.print":"打印",
		"sy.common.action":"操作",
		"sy.common.dateFormatChina":"yyyy年MM月dd日",
		"sy.common.assign":"指派",
		"sy.common.unitPrice":"/元",
		"sy.common.unitVolume":"/L",
		"sy.date.China.format.yyyyMMdd":"yyyy年MM月dd日",
		"sy.date.China.format.yyyyMM":"yyyy年MM",
		"sy.date.China.format.yyyy":"yyyy年",
		
		
		"sy.common.delete.confirm":"确认删除?",
		"sy.common.confirm":"确认",
		"sy.common.unknow.erroe":"未知错误",
		"sy.common.unknow.error":"未知错误",
		"sy.common.prompt":"系统提示",
		"sy.common.progress":"操作中",
		"admin.message.success":"操作成功",
		"sy.common.network.slow":"网络缓慢,请稍后试",
		
		//菜单
		"systemManage":"系统管理",
		"admin:user":"后台用户",
		"admin:role":"角色管理",
		"admin:roleAuth":"授权管理",
		"admin:params":"参数设置",
		
		"endUserManage":"用户管理",
		"admin:endUser":"终端用户",
		"admin:feedback":"意见反馈",
		
		"shipInfoManage":"船只管理",
		"admin:shipInfo":"船只信息",
		"admin:shipGroup":"船只分组",
		
		"shipOwnerManage":"船主管理",
		"admin:shipOwner":"船主信息",
		
		"alertAreaManage":"警戒区管理",
		"admin:alertArea":"警戒区",
		
		"shipPortManage":"海港管理",
		"admin:shipPort":"海港信息",
		
		"terminalManage":"设备管理",
		"admin:terminalInfo":"设备信息",
		
		"shipUploadManage":"上报记录",
		"admin:shipUploadInfo":"上报信息",
		
		"shipViolationManage":"违规记录",
		"admin:shipViolationInfo":"违规信息",
		
		"fishingProhibitManage":"禁渔期管理",
		"admin:fishingInfo":"禁渔期",
		
		//百度地图ak
		"sy.baiduMap.ak.shipRange" : "",
		"sy.baiduMap.area.noAlertAreaShip":"该区域内没有船只",
		"sy.baiduMap.area.shipNum":"在区域内区的船只为:",
		"sy.baiduMap.noDrawing":"请在地图上选择区域",
		"sy.baiduMap.noSetArea":"请先画出警戒区域再进行提交",
		"sy.baiduMap.setAlertArea.success":"警戒区域设置成功",
		"sy.baiduMap.noAlertArea":"没有警戒区",
		
};
//多语言
function message(code) {
	if (code != null) {
		var content = messages[code] != null ? messages[code] : code;
		if (arguments.length == 1) {
			return content;
		} else {
			if ($.isArray(arguments[1])) {
				$.each(arguments[1], function(i, n) {
					content = content.replace(
							new RegExp("\\{" + i + "\\}", "g"), n);
				});
				return content;
			} else {
				$.each(Array.prototype.slice.apply(arguments).slice(1),
						function(i, n) {
							content = content.replace(new RegExp("\\{" + i
									+ "\\}", "g"), n);
						});
				return content;
			}
		}
	}
}

