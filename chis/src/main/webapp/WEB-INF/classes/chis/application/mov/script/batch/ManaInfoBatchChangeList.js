/**
 * 批量修改管理医生列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.batch")
$import("chis.script.BizSimpleListView")
chis.application.mov.script.batch.ManaInfoBatchChangeList = function(cfg) {
	chis.application.mov.script.batch.ManaInfoBatchChangeList.superclass.constructor.apply(
			this, [cfg]);
	this.on("loadData", this.onLoadData, this)
	this.on("firstRowSelected", this.rowSelected, this)
}
Ext.extend(chis.application.mov.script.batch.ManaInfoBatchChangeList,
		chis.script.BizSimpleListView, {

			doEHRRequest : function() {
				
				var moduleName = "EHRApply";
				var module = this.createCombinedModule(moduleName,
						this.refModule);
				module.moduleName = moduleName;
				module.actionName = "apply";
				module.on("save", this.onSave, this);
				module.initDataId = null;
				this.showWin(module);
			},

			doCDHRequest : function() {
				var moduleName = "CDHApply";
				var module = this.createCombinedModule(moduleName,
						this.refModule);
				module.moduleName = moduleName;
				module.actionName = "apply";
				module.on("save", this.onSave, this);
				module.initDataId = null;
				this.showWin(module);
			},

			doMHCRequest : function() {
				var moduleName = "MHCApply";
				var module = this.createCombinedModule(moduleName,
						this.refModule);
				module.on("save", this.onSave, this);
				module.initDataId = null;
				module.moduleName = moduleName;
				module.actionName = "apply";
				this.showWin(module);
			},

			doConfirm : function() {
				var r = this.getSelectedRecord()
				if (!r) {
					return;
				}
				var moduleName;
				var archiveType = r.get("archiveType");
				if (archiveType == "1") {
					moduleName = "EHRConfirm";
				} else if (archiveType == "5") {
					moduleName = "CDHConfirm";
				} else if (archiveType == "6") {
					moduleName = "MHCConfirm";
				}
				var module = this
						.createSimpleModule(moduleName, this.refModule);
				module.moduleName = moduleName;
				module.actionName = "confirm";
				module.on("save", this.onSave, this);
				module.initDataId = r.data.archiveMoveId
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
				var moduleName;
				var archiveType = r.get("archiveType");
				if (archiveType == "1") {
					moduleName = "EHRApply";
				} else if (archiveType == "5") {
					moduleName = "CDHApply";
				} else if (archiveType == "6") {
					moduleName = "MHCApply";
				}
				var module = this
						.createSimpleModule(moduleName, this.refModule);
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
				chis.application.mov.script.batch.ManaInfoBatchChangeList.superclass.onRowClick
						.call(this, grid, index, e);
				this.rowSelected();
			},

			rowSelected : function() {
				var r = this.getSelectedRecord();
				var allowDelete = true;
				var allowConfirm = true;
				var allowUpdate = true;
				if (r == null) {
					allowConfirm = false;
					allowDelete = false;
					allowUpdate = false;
				} else {
					var status = r.get("status");
					var applyUser = r.get("applyUser");
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
				if (n == 11) {
					var upBtn = btns.item(8);
					this.changeButtonStatus(upBtn, allowUpdate);
					var confBtn = btns.item(9);
					this.changeButtonStatus(confBtn, allowConfirm)
					var delBtn = btns.item(10);
					this.changeButtonStatus(delBtn, allowDelete)
				} else {
					var upBtn = btns.item(6);
					this.changeButtonStatus(upBtn, allowUpdate);
					var delBtn = btns.item(7);
					this.changeButtonStatus(delBtn, allowDelete)
				}
			},

			getPagingToolbar : function(store) {
				var pagingToolbar = chis.application.mov.script.batch.ManaInfoBatchChangeList.superclass.getPagingToolbar
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