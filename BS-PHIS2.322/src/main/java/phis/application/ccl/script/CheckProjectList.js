$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckProjectList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.ccl.script.CheckProjectList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.ccl.script.CheckProjectList, phis.script.SimpleList, {
	loadData : function() {
				this.requestData.cnd = ['and',['and',['eq',['$','a.JGID'],["s",this.mainApp['phis'].phisApp.deptId]],['eq', ['$', 'a.LBID'],['i', this.requestData.lbid]]],['eq', ['$', 'a.BWID'],['i', this.requestData.bwid]]];
				phis.application.ccl.script.CheckProjectList.superclass.loadData
						.call(this)
			},
	doRemove : function() {
		var record = this.getSelectedRecord();
		if (record == undefined) {
			return;
		}
		var lbid = record.data.LBID;// 类别ID
		var bwid = record.data.BWID;// 部位ID
		var xmid = record.data.XMID;// 项目ID
		Ext.Msg.show({
					title : '删除确认',
					msg : '删除项目将删除①选中类别,部位,项目对应的该条关系②选中类别,部位,项目对应关系的费用绑定信息，是否继续？',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							// 根据lbid,bwid,xmid删除关系
							var res = phis.script.rmi.miniJsonRequestSync({
										serviceId : "checkApplyService",
										serviceAction : "removeCheckApplyRelation",
										body : {
											lbid : lbid,
											bwid : bwid,
											xmid : xmid
										}
									});
							if (res.code >= 300) {
								this.processReturnMsg(res.code, res.msg);
								return;
							}
							this.opener.midiModules["checkTypeList"].refresh();
							this.opener.midiModules["checkPointList"].refresh();
							this.refresh();
						} else {
							return;
						}
					},
					scope : this
				});
	},
	doAdd : function() {
		var checkPointList = this.opener.midiModules["checkPointList"];
		if (checkPointList.getSelectedRecord() == undefined) {
			Ext.Msg.alert("提示", "请先选择部位");
			return;
		}
//		// 首先刷新项目列表，显示对应类别存在的项目
//		var lbid = checkPointList.getSelectedRecord().data.LBID;
//		var bwid = checkPointList.getSelectedRecord().data.BWID;
//		this.requestData.cnd = ['and', ['eq', ['$', 'lbid'], ['i', lbid]],
//				['eq', ['$', 'bwid'], ['i', bwid]]];
//		this.refresh();
		var module = this.createModule("projectModule", this.refProjectModule);
		if (module) {
			var win = module.getWin();
			win.setWidth(500);
			win.setHeight(400);
			win.show();
			module.opener = this;
			module.projectList.refresh();
		}
	},
	doSave : function() {
		var store = this.grid.getStore();
		if(store.getCount()==0){
			Ext.Msg.alert("提示","列表没有数据,无法保存");
			return;
		}
		var list=[];
		for(var i=0;i<store.getCount();i++){
			list.push(store.data.items[i].data)
		}
		
		var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : "checkApplyService",
					serviceAction : "saveCheckApplyRelation",
					body : {
						list:list
					}
				});
		if (res.code >= 300) {
			this.processReturnMsg(res.code, res.msg);
			return;
		}
		MyMessageTip.msg("提示", "保存成功!", true);
	}
});