$package("phis.application.cfg.script");

$import("phis.script.SimpleList");
phis.application.cfg.script.MedicalExpressionList = function(cfg) {
	cfg.autoLoadData = true;
	cfg.autoLoadSchema = false;
	// cfg.disablePagingTbr = true;
	// cfg.enableCnd=false;
	cfg.listServiceId = "medicalExpMaintainService";
	cfg.serverParams = {
		serviceAction : "listMedicalExpRecords"
	};
	cfg.cnds = ['eq', ['$', 'ZXBZ'], ['s', '0']];
	phis.application.cfg.script.MedicalExpressionList.superclass.constructor
			.apply(this, [cfg]);
},

Ext.extend(phis.application.cfg.script.MedicalExpressionList,
		phis.script.SimpleList, {
			doLogout : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var ZXBZ = r.get("ZXBZ");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicalExpMaintainService",
							serviceAction : "logoutMedicalExp",
							method : "execute",
							pkey : r.id,
							ZXBZ : ZXBZ
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doLogout);
				} else {
					this.refresh();

				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if(this.checkData){
					var id = this.checkData.DYBDSBH;
					var record = this.store.getById(id);
					var index = this.store.indexOf(record);
					this.selectedIndex = index;
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				if (this.isOnSave) {
					this.isOnSave = false;
				} else {
					this.grid.fireEvent("rowClick")
				}
			},
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin();
					win.on("hide", this.formWinHide, this);
					win.setPosition(300, 200);
					win.setTitle(module.title);
					this.formWin = win;
					win.show()
					var formData = this.castListDataToForm(r.data, this.schema);
					this.fireEvent("openModule", module)
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								this.checkData = null;
								module.doNew()
								break;
							case "read" :
							case "update" :
								module.op = "update"
								module.initFormData(formData)
						}
					}
				}
			},
			formWinHide : function() {
				this.fireEvent("formWinHide", this)
			},
			castListDataToForm : function(data, schema) {
				var formData = {};
				var items = schema.items;
				var n = items.length;
				for (var i = 0; i < n; i++) {
					var it = items[i];
					var key = it.id;
					if (it.dic) {
						var dicData = {
							"key" : data[key],
							"text" : data[key + "_text"]
						};
						formData[key] = dicData;
					} else {
						formData[key] = data[key];
					}
				}
				Ext.applyIf(formData, data)
				return formData;
			},
			onSave : function(entryName, op, json, data) {
				this.checkData = data;
				this.isOnSave = true;
				this.refresh();
				if (this.formWin) {
					this.formWin.hide();
				}
				this.fireEvent("save", entryName, op, json, data);
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "logout");
				btn = btn[0];
				if (r.get("ZXBZ") == "1") {
					btn.setText("取消注销(F4)");
				} else {
					btn.setText("注销(F4)");
				}
				if (this.recordId == r.id) {
					return;
				}
				this.recordId = r.id;
				this.checkData = r.data;
				this.fireEvent("listRowClick");
			},
			onContextMenu : function(grid, rowIndex, e) {
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
				this.grid.getSelectionModel().selectRow(rowIndex)
				var r = this.getSelectedRecord();
				var cmenu = this.midiMenus['gridContextMenu']
				if (!cmenu) {
					var items = [];
					var actions = this.actions
					if (!actions) {
						return;
					}
					for (var i = 0; i < actions.length; i++) {
						var action = actions[i];
						var it = {}
						it.cmd = action.id
						it.ref = action.ref
						it.iconCls = action.iconCls || action.id
						it.script = action.script
						it.text = action.name
						if (action.id == "logout") {
							if (r.data.ZXBZ == "1") {
								it.text = "取消注销";
							}
						}
						it.handler = this.doAction
						it.scope = this
						items.push(it)
					}
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['gridContextMenu'] = cmenu
				} else {
					for (var i = 0; i < cmenu.items.length; i++) {
						if (cmenu.items.itemAt(i).cmd == "logout") {
							if (r.data.ZXBZ == "1") {
								cmenu.items.itemAt(i).setText("取消注销");
							} else {
								cmenu.items.itemAt(i).setText("注销");
							}
						}
					}

				}
				// @@ to set menuItem disable or enable according to buttons of
				// toptoolbar.
				var toolBar = this.grid.getTopToolbar();
				if (toolBar) {
					for (var i = 0; i < this.actions.length; i++) {
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (!btn || btn.length == 0) {
							continue;
						}
						if (btn[0].disabled) {
							cmenu.items.itemAt(i).disable();
						} else {
							cmenu.items.itemAt(i).enable();
						}

					}
				}
				cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5])
			},
			doEditExp : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.checkData = r.data;
				this.fireEvent("save");
			},
			getCheckData : function() {
				return this.checkData;
			}

		});