/**
 * 儿童档案户籍地址迁移管理列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.cdh")
$import("chis.script.BizSimpleListView");

chis.application.mov.script.cdh.CDHMoveList = function(cfg) {
	chis.application.mov.script.cdh.CDHMoveList.superclass.constructor.apply(this, [cfg]);
	this.removeServiceId = "chis.cdhMoveService";
	this.removeAction = "removeCDHMoveRecord";
	this.on("loadData", this.onLoadData, this);
	this.on("firstRowSelected", this.rowSelected, this)
};
Ext.extend(chis.application.mov.script.cdh.CDHMoveList, chis.script.BizSimpleListView, {

	doRequest : function() {
		var module = this.createSimpleModule("CDHApply", this.refApplyModule);
		module.initPanel();
		module.on("afterSave", this.afterSave, this);
		module.initDataId = null;
		module.exContext.control = {};
		this.showWin(module);
		module.doNew();
	},

	doConfirm : function() {
	
		var r = this.getSelectedRecord()
		if (!r) {
			return;
		}
		var module = this.createSimpleModule("CDHConfirm",
				this.refConfirmModule);
		module.initPanel();
		module.on("afterSave", this.afterSave, this);
		module.initDataId = r.id;
		this.showWin(module);
		module.loadData();
	},

	doModify : function() {
		var r = this.getSelectedRecord()
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
		var r = this.getSelectedRecord()
		if (!r) {
			return;
		}
		var module = this.createSimpleModule("CDHApply", this.refApplyModule);
		module.initPanel();
		module.on("afterSave", this.afterSave, this);
		module.initDataId = r.id;
		this.showWin(module);
		module.loadData();
	},

	onDblClick : function() {
		this.doModify();
	},

	afterSave : function() {
		this.refresh();
	},

	onLoadData : function(store) {
		if (store.getCount() == 0) {
			this.rowSelected();
		}
	},

	onRowClick : function(grid, index, e) {
		chis.application.mov.script.cdh.CDHMoveList.superclass.onRowClick.call(this, grid,
				index, e);
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
				var sourceUnit = r.get("sourceManaUnitId").substring(0, 9);
				var targetUnit = r.get("targetManaUnitId").substring(0, 9);
				var moveType = r.get("moveType");
				if (moveType == "1" && sourceUnit == deptId) {
					allowConfirm = true;
				} else if (moveType == "2" && targetUnit == deptId) {
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
		var upBtn = btns.item(6);
		this.changeButtonStatus(upBtn, allowUpdate);
		var n = btns.getCount();
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
		var pagingToolbar = chis.application.mov.script.cdh.CDHMoveList.superclass.getPagingToolbar
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