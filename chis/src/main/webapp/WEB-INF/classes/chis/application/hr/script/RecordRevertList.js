$package("chis.application.hr.script")

$import("app.modules.list.SimpleListView")

chis.application.hr.script.RecordRevertList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.saveServiceId = "chis.revertRecordService";
	cfg.entryName = "chis.application.hr.schemas.ADMIN_RecordRevert";
	cfg.serviceAction = "loadAllLogoutedRecords";
	cfg.revertAction = "revertRecords";
	cfg.actions = [{
				id : "revert",
				name : "恢复",
				iconCls : "healthDoc_revert"
			}]
	chis.application.hr.script.RecordRevertList.superclass.constructor.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.hr.script.RecordRevertList, app.modules.list.SimpleListView, {

			getPagingToolbar : function(store) {
				this.requestData.serviceId = this.saveServiceId
				this.requestData.serviceAction = this.serviceAction
				this.requestData.body = {
					empiId : this.empiId,
					pageSize : this.pageSize || 25,
					pageNo : 1
				}

				var cfg = {
					pageSize : this.pageSize || 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}
				if (this.showButtonOnPT) {
					cfg.items = this.createButtons();
				}
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				return pagingToolbar
			},

			loadData : function() {
				this.requestData.empiId = this.empiId
				this.grid.el.mask("正在查询...");
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.serviceAction,
							method:"execute",
							body : this.requestData
						}, function(code, msg, json) {
							this.grid.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, this.requestData);
								return;
							}
							if (json.body) {
								this.store.removeAll();
								var result = json.body;
								for (var i = 0; i < result.length; i++) {
									var rec = result[i];
									var record = new Ext.data.Record(rec);
									this.store.add(record);
								}
								this.selectFirstRow();
								this.onRowClick();
							}
							this.fireEvent("loadData", this.store);
						}, this)
			},

			onWinShow : function() {
				this.win.setTitle(this.title)
				this.requestData.empiId = this.empiId
				this.loadData()
				this.win.doLayout()
			},

			doLogout : function() {
				var r = this.grid.getSelectionModel().getSelected()
				var reuslt = util.rmi.miniJsonRequestSync({
							serviceId : "chis.healthRecordService",
							serviceAction : "doRecordLogoutFormRevertList",
							method:"execute",
							body : r.data,
							schema : this.entryName
						})
				this.fireEvent("logout")
				this.refresh()
			},

			doRevert : function() {
				var r = this.grid.getSelectionModel().getSelected()
				var flag = false;
				if (r.get("type") == '4') {
					for (var i = 0; i < this.store.getCount(); i++) {
						var record = this.store.getAt(i)
						if (record.get("type") == '4'
								&& record.get("status") == '0') {
							flag = true
							break;
						}
					}
				}
				if (flag) {
					alert("存在未终止管理的孕产妇档案，不允许恢复")
					return
				}
				if (r.get("type") == '1') {
					if (this.store.getCount() > 1) {
						Ext.Msg.show({
									title : '消息提示',
									msg : '是否同时恢复子档案',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										this.process(r, btn)
									},
									scope : this
								})
					} else {
						this.process(r, 'no')
					}
				} else if (r.get("type") == '5') {

					var hasDebilityChildren
					for (var i = 0; i < this.store.data.length; i++) {
						var storeItem = this.store.getAt(i);
						if (storeItem.get("type") == '6') {
							hasDebilityChildren = true
						} else {
							hasDebilityChildren = false
						}
					}
					if (hasDebilityChildren) {
						Ext.Msg.show({
									title : '消息提示',
									msg : '是否同时恢复体弱儿档案',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										this.process(r, btn)
									},
									scope : this
								})
					} else {
						this.process(r, 'no');
					}
				} else {
					this.process(r, 'no');
				}
			},

			process : function(r, btn) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : this.revertAction,
							method:"execute",
							body : r.data,
							schema : this.entryName,
							revertFlag : btn
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				this.fireEvent("logout")
				this.refresh()
			},

			onStoreLoadData : function(store, records, ops) {
				chis.application.hr.script.RecordRevertList.superclass.onStoreLoadData.call(
						this, store, records, ops);
				this.onRowClick()
			},

			onRowClick : function() {
				var healthCardStatus
				for (var i = 0; i < this.store.data.length; i++) {
					var storeItem = this.store.getAt(i);
					if (storeItem.get("type") == '5') {
						healthCardStatus = storeItem.get("status")
					}
				}
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}

				var status = r.get("status");
				var bts = this.grid.getTopToolbar().items;
				if (status == 0) {
					bts.items[0].disable();
				} else {
					if (r.get("type") == "1") {
						bts.items[0].enable();
					} else {
						if (this.checkHealthRecordStatus() == "0") {
							bts.items[0].enable();
						} else {
							bts.items[0].disable();
						}
					}
					if (r.get("type") == "6" && healthCardStatus == '1') {
						bts.items[0].disable();
					}
				}

			},

			checkHealthRecordStatus : function() {
				var store = this.store
				for (var i = 0; i < store.getCount(); i++) {
					var record = store.getAt(i)
					if (record.data.type == "1") {
						return record.data.status;
					}
				}
			},

			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : 900,
								height : 570,
								iconCls : 'icon-form',
								constrain : true,
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								modal : true,
								plain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								buttonAlign : 'center',
								items : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			}
		})