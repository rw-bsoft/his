/**
 * 修改各档责任医生列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.manage")
$import("chis.script.BizSimpleListView")
chis.application.mov.script.manage.ManaInfoChangeList = function(cfg) {
	chis.application.mov.script.manage.ManaInfoChangeList.superclass.constructor.apply(this,
			[cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("firstRowSelected", this.rowSelected, this)
};
Ext.extend(chis.application.mov.script.manage.ManaInfoChangeList, chis.script.BizSimpleListView, {

	doRequest : function() {
		var moduleName = "ManageApply";
		var module = this.createCombinedModule(moduleName, this.refModule);
		module.moduleName = moduleName;
		module.actionName = "apply";
		module.on("save", this.onSave, this);
		module.initDataId = null;
		this.showWin(module);
	},

	doConfirm : function() {
		var r = this.getSelectedRecord()
		if (!r) {
			return;
		}
		var jgid=r.data.manaUnitId;
		var affirmUnit=this.mainApp.deptId;
		var jgid1="";
		var affirmUnit1="";
				if(jgid.length>=9){
					 jgid1= jgid.substring(0, 9);
				}else{
					jgid1=jgid;
				}
				if(affirmUnit.length>=9){
					affirmUnit1= affirmUnit.substring(0, 9);
				}else{
					affirmUnit1=affirmUnit;
				}
		var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.ehrMoveService",
								serviceAction : "getManageUnit",
								method : "execute",
								body : jgid1
							});
					if (result.code > 300) {
						MyMessageTip.msg("提示",
					       "获取居民管辖机构失败", true);
					        return;
				   }
					var jgidname=result.json.body.ORGANIZNAME;
			  	   if(jgid1!=affirmUnit1){
					MyMessageTip.msg("提示",
					       "当前居民档案不属于本单位管理！请联系"+jgidname+"防保科长审核确认", true);
					        return;
			  	   }
		var moduleName = "ManageConfirm";
		var module = this.createSimpleModule(moduleName, this.refModule);
		module.moduleName = moduleName;
		module.actionName = "confirm";
		module.on("save", this.onSave, this);
		module.initDataId = r.id;
		this.showWin(module);
		module.loadData();
	},

	doModify : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		var status = r.get("status");
		if (status == "1") {
			this.doUpdateRequest();
		} else {
			this.doConfirm();
		}
	},

	doUpdateRequest : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		var moduleName = "ManageApply";
		var module = this.createSimpleModule(moduleName, this.refModule);
		module.moduleName = moduleName;
		module.actionName = "apply";
		module.on("save", this.onSave, this);
		module.initDataId = r.id;
		this.showWin(module);
		module.loadData();
	},

	onDblClick : function() {
		this.doModify();
	},

	onSave : function(module) {
		module.doCancel();
		this.refresh();
	},

	onLoadData : function(store) {
		if (store.getCount() == 0) {
			this.rowSelected();
		}
	},

	onRowClick : function(grid, index, e) {
		chis.application.mov.script.manage.ManaInfoChangeList.superclass.onRowClick.call(this,
				grid, index, e);
		this.rowSelected();
	},

	rowSelected : function() {
		var r = this.getSelectedRecord();
		var allowUpdate = true;
		var allowDelete = true;
		var allowConfirm = true;
		if (r == null) {
			allowUpdate = false;
			allowConfirm = false;
			allowDelete = false;
		} else {
			var status = r.get("status");
			if (status != "1") {
				allowConfirm = false;
				allowDelete = false;
			} else {
				var deptId = this.mainApp.deptId;
				var applyUnit = r.get("applyUnit").substring(0, 9);
				if (applyUnit == deptId) {
					allowConfirm = true;
				} else {
					allowConfirm = false;
				}
				var applyUser = r.get("applyUser");
				if (applyUser != this.mainApp.uid) {
					allowDelete = false;
				}
			}
		}
		var btns = this.grid.getTopToolbar().items;
		if (!btns) {
			return;
		}
		var n = btns.getCount();
		var upBtn = btns.item(6);
		this.changeButtonStatus(upBtn, allowUpdate);
		if (n == 9) {
			var confBtn = btns.item(7);
			this.changeButtonStatus(confBtn, allowConfirm)
			var delBtn = btns.item(8);
			this.changeButtonStatus(delBtn, allowDelete)
		} else {
			var delBtn = btns.item(7);
			this.changeButtonStatus(delBtn, allowDelete)
		}
	},

	getPagingToolbar : function(store) {
		var pagingToolbar = chis.application.mov.script.manage.ManaInfoChangeList.superclass.getPagingToolbar
				.call(this, store);
		var items = pagingToolbar.items;
		var lab = new Ext.form.Label({
					html : "&nbsp;&nbsp;状态:"
				});
		items.insert(13, "lab", lab);
		var comb = util.dictionary.SimpleDicFactory.createDic({
					id : "chis.dictionary.archiveMoveStatusForRadio",
					forceSelection : true
				})
		comb.on("select", this.radioChanged, this);
		comb.setValue("01");
		comb.setWidth(80);
		items.insert(14, "comb", comb);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},

	radioChanged : function(r) {
		var status = r.getValue();
		var navCnd = this.navCnd;
		var queryCnd = this.queryCnd;
		var statusCnd;
		if (status == "0") {
			statusCnd = ['eq', ['s', '1'], ['s', '1']];
		} else {
			statusCnd = ['eq', ['$', 'a.status'], ['s', status]];
		}
		this.initCnd = statusCnd;
		var cnd = statusCnd;
		if (navCnd || queryCnd) {
			cnd = ['and', cnd];
			if (navCnd) {
				cnd.push(navCnd);
			}
			if (queryCnd) {
				cnd.push(queryCnd);
			}
		}
		this.requestData.cnd = cnd;
		this.requestData.pageNo = 1;
		this.refresh();
	}

})