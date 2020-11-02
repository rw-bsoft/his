$package("chis.application.wl.script");

$import("chis.script.BizSimpleListView",
		"chis.script.util.widgets.MyMessageTip"
		);
chis.application.wl.script.FromhisList = function(cfg) {
	if(cfg.actions==undefined)
	{
		cfg.actions = [{
				id : "modify",
				name : "查看",
				iconCls : "update"
			},
			{
				id : "confirm",
				name : "确认",
				iconCls : "archiveMove_commit"
			},
			{
				id : "remove",
				name : "删除"
			},
			{
				id : "print",
				name : "打印"
			}]
	}
	cfg.selectFirst = false;
	cfg.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "1"]];
	chis.application.wl.script.FromhisList.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.wl.script.FromhisList,
		chis.script.BizSimpleListView, {
			onReady : function() {
				chis.application.wl.script.FromhisList.superclass.onReady
						.call(this);
			},

			loadData : function() {
				if (this.cnds) {
					this.requestData.cnd = this.cnds
				}
				chis.application.wl.script.FromhisList.superclass.loadData
						.call(this);
				var recordNum = this.store.getCount();
				if (recordNum == 0) {
					var bts = this.grid.getTopToolbar().items;
					for (var i = 0; i < bts.getCount(); i++) {
						if (i > 5) {
							bts.items[i].disable();
						}
					}
				}
			},
//
//			doRequest : function() {
//				var module = this.createSimpleModule("EHRApplyForm",
//						this.refApplyModule);
//				module.initPanel();
//				module.on("save", this.onSave, this);
//				module.initDataId = null;
//				module.op = "create";
//				module.exContext.control = {
//					"update" : true
//				};
//				this.showWin(module);
//				module.doNew();
//			},

			doConfirm : function() {

				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var module = this.createSimpleModule("EHRConfirmForm",
						this.refConfirmModule);
				module.initPanel();
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
				var module = this.createSimpleModule("EHRApplyForm",
						this.refApplyModule);
				module.initPanel();
				module.on("save", this.onSave, this);
				module.initDataId = r.id;
				module.op1 = "update";
				this.showWin(module);
				module.loadData();
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("ehrjgdm").substr(0,this.mainApp.deptId.length) != this.mainApp.deptId) {
					MyMessageTip.msg("提示", "只有本机构或上级机构才能删除申请记录！", true);
					return;
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.keys.length; i++) {
						title += r.get(this.schema.keys[i])
					}
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},

			onDblClick : function(grid, index, e) {
				this.doModify();
			},
            
			onSave : function() {
				this.refresh();
			},

			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				};
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.archiveMoveStatusForRadio",
							forceSelection : true
						});
				comb.on("select", this.radioChanged, this);
				comb.setValue("0");
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				cfg.items = [lab, comb];

				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},

			radioChanged : function(r) {
				var status = r.getValue();
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var statusCnd;
				if (status == 0) {
					statusCnd = ['eq', ['s', '1'], ['s', '1']];
				} else {
					statusCnd = ['eq', ['$', 'status'], ['s', status]];
				}
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd || this.cnds) {
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
			},

			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				var uid = this.mainApp.uid;
				var role = this.mainApp.jobtitleId;
				var status = r.get("status");
				var applyUser = r.get("applyUser");
				var deptId = this.mainApp.deptId;
				var ehrjgdm = r.get("ehrjgdm").substring(0, 9);
				var moveType = r.get("moveType");

				var bts = this.grid.getTopToolbar().items;
				if (!bts) {
					return;
				}
				// 控制 -删除-
				if (status == "1"
						&& (applyUser == uid || role == "chis.14"
								|| role == "gp.101" || role == 'chis.system' || role == "chis.01" ||role == "chis.20")) {
					if (role == "chis.14" || role == 'chis.system' || role == "chis.01" ||role == "chis.20" ) {// 防保长时多一“确认”
						bts.items[7].enable();
					} else {
						bts.items[6].enable();
					}
				} else {
					if (role == "chis.14") {
						bts.items[7].disable();
					} else {
						bts.items[6].disable();
					}
				}
				// 控制 -确认-
				if (status == "1") {
					if (role == 'chis.system' || role == "chis.01" ||role == "chis.20" ) {
						bts.items[6].enable();
					} else if (role == "chis.14"  ) {// 防保长
						 if ( ehrjgdm == deptId) {
							bts.items[6].enable();
						} else {
							bts.items[6].disable();
						}
					}
				} else {
					if (role == 'chis.system' || role == "chis.14" || role == "chis.01" || role == "chis.20" ) {
						bts.items[6].disable();
					}
				}
				// 查看
				bts.items[5].enable();
				bts.items[8].enable();
			}
		});