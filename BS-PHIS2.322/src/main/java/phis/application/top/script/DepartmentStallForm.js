$package("com.bsoft.phis.pub")

/**
 * 员工维护form
 * 
 * @param {}
 *            cfg
 */

$import("com.bsoft.phis.TableForm")

com.bsoft.phis.pub.DepartmentStallForm = function(cfg) {
	cfg.colCount = 4;
	cfg.width = 900;
	cfg.saveServiceId = "departmentManageService";
	cfg.saveAction = "saveDepartmentStall";
	com.bsoft.phis.pub.DepartmentStallForm.superclass.constructor.apply(this,
			[cfg])
	this.on("loadData", this.onLoadData, this);

}
Ext.extend(com.bsoft.phis.pub.DepartmentStallForm, com.bsoft.phis.TableForm, {
	initPanel : function(sc) {
		if (this.form) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			return this.form;
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		var ac = util.Accredit;
		var defaultWidth = this.fldDefaultWidth || 200
		var items = schema.items
		if (!this.fireEvent("changeDic", items)) {
			return
		}
		var colCount = this.colCount;
		var table = {
			labelAlign : "right",
			labelWidth : this.labelWidth || 70,
			iconCls : 'bogus',
			border : false,
			items : []
		}
		var g = new Ext.form.FieldSet({
					title : '员工信息',
					animCollapse : true,
					defaultType : 'textfield',
					autoWidth : true,
					autoHeight : true,
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						},
						forceWidth : 800
					}
				})
		var size = items.length
		for (var i = 0; i < size; i++) {
			var it = items[i]
			if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
				continue;
			}
			var f = this.createField(it)
			f.labelSeparator = ":"
			f.index = i;
			f.anchor = it.anchor || "100%"
			delete f.width

			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)

			if (!this.fireEvent("addfield", f, it)) {
				continue;
			}
			g.add(f)
		}
		table.items.push(g);
		var g2 = new Ext.form.FieldSet({
					title : '同步到用户信息',
					id : "sys_user",
					animCollapse : true,
					defaultType : 'textfield',
					// autoHeight : true,
					// autoWidth : true,
					width : 870,
					height : 260,
					collapsed : true,
					titleCollapse : true,
					collapsible : true,
					closable : true,
					collapseFirst : true,
					layout : 'tableform',
					layoutConfig : {
						columns : 3,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : []
				})
		g2.on("expand",this.userExpand,this);
		var fzyySchema;
		var re = util.schema.loadSync(this.sysuser_entryName);
		if (re.code == 200) {
			fzyySchema = re.schema;
		} else {
			this.processReturnMsg(re.code, re.msg, this.initPanel)
			return;
		}
		var fzyyItems = fzyySchema.items;
		for (var i = 0; i < fzyyItems.length; i++) {
			var it = fzyyItems[i]
			if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
				continue;
			}
			var f = this.createField(it)
			f.labelSeparator = ":"
			f.index = i;
			f.anchor = it.anchor || "95%"
			delete f.width

			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)

			if (!this.fireEvent("addfield", f, it)) {
				continue;
			}
			g2.add(f)
		}
		g2.add(this.getUserPropList());

		table.items.push(g2);
		var cfg = {
			buttonAlign : 'center',
			labelAlign : this.labelAlign || "left",
			labelWidth : this.labelWidth || 80,
			frame : true,
			shadow : false,
			border : false,
			collapsible : false,
			autoWidth : true,
			autoHeight : true,
			floating : false
		}

		if (this.isCombined) {
			cfg.frame = true
			cfg.shadow = false
			cfg.width = this.width
			cfg.height = this.height
			// var xy=win.getPosition();
			// win.setPagePosition(xy[0],50);
		} else {
			cfg.autoWidth = true
			cfg.autoHeight = true
		}
		this.initBars(cfg);
		Ext.apply(table, cfg)
		this.form = new Ext.FormPanel(table)
		this.form.on("afterrender", this.onReady, this)

		this.schema = schema;
		this.setKeyReadOnly(true)
		if (!this.isCombined) {
			this.addPanelToWin();
		}
		return this.form
	},
	getUserPanel : function() {
		var panel = new Ext.Panel({
					border : false,
					// frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					height : 230,
					items : [{
								layout : "fit",
								border : false,
								region : 'north',
								height : 50,
								items : this.getUserForm()
							}, {
								layout : "fit",
								border : false,
								region : 'center',
								items : this.getUserPropList()
							}]
				});
		this.panel = panel;
		return panel;
	},
	expansion : function(cfg) {// 扩展
		cfg.frame = false;
	},
	getUserForm : function() {
		var form = this.createModule("userForm", this.refUserForm);
		this.userForm = form;
		return form.initPanel();
	},
	getUserPropList : function() {
		var listModule = this.createModule("userPropList", this.refUserList);
		this.userPropList = listModule;
		var list = listModule.initPanel();
		var index = list.getColumnModel().findColumnIndex('post');
		var cid = list.getColumnModel().getColumnId(index);
		var editor = list.getColumnModel().getColumnById(cid).editor;
		this.list = list;
		editor.tree.on("click", this.onTreeClick, this);
		return list;
	},
	userExpand : function() {
		this.userPropList.grid.doLayout();
	},
	doNew : function() {
		this.KSDM = this.data.KSDM;
		this.JGID = this.data.JGID;
		this.op = "create";
		// if (this.midiModules["userForm"]) {
		// this.midiModules["userForm"].doNew();
		// this.opener.afterOpenModule(this);
		// }
		if (this.midiModules["userPropList"]) {
			this.midiModules["userPropList"].store.removeAll();
		}
		com.bsoft.phis.pub.DepartmentStallForm.superclass.doNew.call(this)
		this.form.find("name", "password")[0].setValue("");
		this.form.find("name", "remark")[0].setValue("");
		this.data.KSDM = this.KSDM;
		this.data.JGID = this.JGID;
	},
	onTreeClick : function(node) {
		if (node.isLeaf()) {
			var parentNode = node.parentNode;
			var cell = this.userPropList.grid.getSelectionModel()
					.getSelectedCell();
			var row = cell[0];
			var store = this.userPropList.grid.store.data;
			var rowItem = store.itemAt(row);
			// var store = this.listModule.grid.getStore();
			var n = store.getCount()
			for (var i = 0; i < n; i++) {
				if (i == row)
					continue;
				var r = store.itemAt(i)
				if (r.get("post") == node.id) {
					MyMessageTip.msg("提示", "\"" + node.text + "\"已存在", true);
					rowItem.set('manaUnitId', "");
					rowItem.set('manaUnitId_text', "");
					return;
				}
			}
			rowItem.set('post', node.id);
			rowItem.set('post_text', node.text);
			rowItem.set('manaUnitId', parentNode.id);
			rowItem.set('manaUnitId_text', parentNode.text);
		}
	},
	loadUserData : function() {
		// var userid = this.initDataId;
		var record = this.exContext[this.entryName]
		var userid = record.get("YGDM");
		// 查询userId是否存在
		phis.script.rmi.jsonRequest({
					serviceId : "departmentManageService",
					serviceAction : "loadUserInfo",
					entryName : "SYS_USERS",
					body : {
						"pkey" : userid
					}
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg, this.loadData)
						return
					}
					// this.userForm.doNew()
					if (json.body) {
						// 手工设置密码和备注
						// this.userForm.initFormData(json.body)
						this.form.find("name", "password")[0]
								.setValue(json.body.password);
						this.form.find("name", "remark")[0]
								.setValue(json.body.remark);
						this.form.findById("sys_user").expand(false);
					} else {
						this.form.findById("sys_user").collapse(false);
					}
					// if (this.userForm.op == 'create') {
				// this.userForm.op = "update"
				// }

			}, this)// jsonRequest
		this.userPropList.requestData.cnd = ['eq', ['$', 'userId'],
				['s', userid]];
		this.userPropList.refresh();
	},
	getSaveRequest : function(saveData) {
		// 判断是否同步到用户表
		var syncToUser = this.form.findById("sys_user").collapsed;
		saveData.syncToUser = false;
		if (!syncToUser) {
			saveData.syncToUser = true;
			saveData.password = this.form.find("name", "password")[0]
					.getValue();
			saveData.REMARK = this.form.find("name", "remark")[0].getValue();
			var userProp = [];
			var store = this.userPropList.grid.getStore();
			var count = store.getCount();
			for (var i = 0; i < count; i++) {
				userProp.push(store.getAt(i).data)
			}
			saveData.userProp = userProp;
		}
		var values = saveData;
		return values;
	},
	doSave : function() {
		if (this.saving) {
			return
		}
		var values = this.getFormData();
		if (!values) {
			return;
		}
		Ext.apply(this.data, values);
		var syncToUser = this.form.findById("sys_user").collapsed;
		if (!syncToUser) {
			if (!this.form.find("name", "password")[0].getValue()) {
				MyMessageTip.msg("提示", "请输入密码!", true);
				return;
			}
			var store = this.userPropList.grid.getStore();
			var count = store.getCount();
			if (!count) {
				MyMessageTip.msg("提示", "请输入职位!", true);
				return;
			} else {
				for (var i = 0; i < count; i++) {
					if (store.getAt(i).data["post"] != ''
							&& store.getAt(i).data["post"] != '0'
							&& store.getAt(i).data["post"] != null
							&& store.getAt(i).data["manaUnitId"] != ''
							&& store.getAt(i).data["manaUnitId"] != '0'
							&& store.getAt(i).data["manaUnitId"] != null) {
					} else {
						MyMessageTip
								.msg("提示", "职位信息第" + (i + 1) + "行错误!", true);
						return;
					}
				}
			}
		}
		this.saveToServer(values);
	},
	onReady : function() {
		com.bsoft.phis.pub.DepartmentStallForm.superclass.onReady.call(this)
		var ZJPB = this.form.getForm().findField("ZJPB");
		if (ZJPB) {
			ZJPB.on("select", this.onSelect, this)
		}
	},
	// 专家判别选择'否'时,专家费应控制不能录入并还原为默认值
	onSelect : function(f) {
		var v = f.getValue();
		this.dealSelect(v);
	},

	dealSelect : function(v) {
		var form = this.form.getForm();
		var ZJFY = form.findField("ZJFY");
		if (ZJFY) {
			if (v == 0) {
				ZJFY.setValue(0);
				ZJFY.disable();
			} else {
				ZJFY.enable();
			}
		}
	},

	onLoadData : function(entryName, body) {
		var ZJPB = body.ZJPB;
		if (ZJPB) {
			this.dealSelect(ZJPB.key);
		}
		this.loadUserData();
	}
})