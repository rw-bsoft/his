/**
 * 药品公用信息模版
 * 
 * @author tianj
 */
$package("com.bsoft.phis.pub");

$import("com.bsoft.phis.SimpleModule")

com.bsoft.phis.pub.UserModule = function(cfg) {
	this.width = 600;
	this.height = 350;
	this.serviceId = "userManageService";
	this.actionId = "saveUserManage";
	this.modal = true
	com.bsoft.phis.pub.UserModule.superclass.constructor.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
}

Ext.extend(com.bsoft.phis.pub.UserModule, com.bsoft.phis.SimpleModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		var panel = new Ext.Panel({
					border : false,
					height : this.height,
					frame : true,
					layout : 'border',
					items : [{
								layout : "fit",
								border : false,
								split : true,
								region : 'north',
								width : 600,
								height : 104,
								items : this.getForm()
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								width : 600,
								region : 'center',
								items : this.getList()
							}],
					tbar : (this.tbar || []).concat(this.createButtons())
				});
		this.panel = panel;
		return panel;
	},
	getForm : function() {
		var formModule = this.createModule("sysUserForm", this.sysUserForm);
		//formModule.ygbhs = this.ygbhs;
		var from = formModule.initPanel();
		return from;
	},
	getList : function() {
		var listModule = this.createModule("sysUserEditorList",
				this.sysUserEditorList);
		this.listModule = listModule;
		var list = listModule.initPanel();
		var index = list.getColumnModel().findColumnIndex('post');
		var cid = list.getColumnModel().getColumnId(index);
		var editor = list.getColumnModel().getColumnById(cid).editor;
		this.list = list;
		editor.tree.on("click", this.onTreeClick, this);
		return list;
	},
	onTreeClick : function(node) {
		if (node.isLeaf()) {
			var parentNode = node.parentNode;
			var cell = this.listModule.grid.getSelectionModel()
					.getSelectedCell();
			var row = cell[0];
			var store = this.listModule.grid.store.data;
			var rowItem = store.itemAt(row);
			//var store = this.listModule.grid.getStore();
			var n = store.getCount()
			for (var i = 0; i < n; i++) {
				if (i == row)
					continue;
				var r = store.itemAt(i)
				if (r.get("post") == node.id) {
					MyMessageTip.msg("提示", "\"" + node.text
									+ "\"已存在", true);
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
	doSave : function() {
		var values = {};
		var formModule = this.midiModules["sysUserForm"];
		if (formModule) {
			formModule.acid = "sysUserForm";
			var k = this.moduleOperation("save", formModule, values);
			if (k == 0) {
				return;
			}
		}
		var listModule = this.midiModules["sysUserEditorList"];
		if (listModule) {
			listModule.acid = "sysUserEditorList";
			var k = this.moduleOperation("save", listModule, values);
			if (k == 0) {
				return;
			}
		}
		if (!values["sysUserEditorList"]
				|| values["sysUserEditorList"].length == 0) {
			MyMessageTip.msg("提示", "用户职位不能为空！", true);
			return;
		}
		/*
		 * if ((!values["cfgaliastab"] || values["cfgaliastab"].length == 0) &&
		 * this.op == "create") { if (this.midiModules["cfgproptab"]) { var fymc =
		 * this.midiModules["cfgproptab"].form.getForm()
		 * .findField("FYMC").getValue(); values["cfgaliastab"] = []; var data = {
		 * '_opStatus' : 'create' }; data["FYXH"] = null; data["FYMC"] = fymc;
		 * data["PYDM"] = null; data["WBDM"] = null; data["JXDM"] = null;
		 * data["QTDM"] = null; data["BMFL"] = 1;
		 * values["cfgaliastab"].push(data); } }
		 */
		this.panel.el.mask("正在执行操作...");
		phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : this.actionId,
					op : this.op,
					body : values
				}, function(code, msg, json) {
					this.panel.el.unmask();
					if (code >= 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					this.fireEvent("save");
					this.getWin().hide();
					// this.op = "update";
			}, this);
	},
	moduleOperation : function(op, module, values) {
		var acid = module.acid;
		if (op == "save") {
			if (acid == "sysUserForm") {
				if (module.initDataId == null) {
					MyMessageTip.msg("提示", "用户信息请从员工表选择", true);
					return 0;
				}
				values["userId"] = module.initDataId;
				values["pyCode"] = module.data["pyCode"];
				if (module.data["status"]) {
					values["status"] = module.data["status"]["key"];
				} else {
					values["status"] = "0";
				}
				var re = util.schema.loadSync(module.entryName);
				var form = module.form.getForm();
				for (var i = 0; i < re.schema.items.length; i++) {
					var item = re.schema.items[i];
					var field = form.findField(item.id);
					if (field) {
						var value = field.getValue();
						if (!value) {
							if (!item.pkey && item["not-null"]) {
								Ext.Msg.alert("提示", item.alias + "不能为空");
								return 0;
							}
						}
						if (value && typeof(value) == "string") {
							value = value.trim();
						}
						values[item.id] = value;
					} else if (item.defaultValue && !item.virtual) {
						var dvalue = item.defaultValue;
						if (typeof dvalue == "object") {
							values[item.id] = dvalue.key;
						} else {
							values[item.id] = dvalue;
						}
					}
				}
			} else if (acid == "sysUserEditorList") {
				values[module.acid] = [];
				count = module.store.getCount();
				for (var i = 0; i < count; i++) {
					if (module.store.getAt(i).data["post"] != ''
							&& module.store.getAt(i).data["post"] != '0'
							&& module.store.getAt(i).data["post"] != null
							&& module.store.getAt(i).data["manaUnitId"] != ''
							&& module.store.getAt(i).data["manaUnitId"] != '0'
							&& module.store.getAt(i).data["manaUnitId"] != null) {
						values[module.acid].push(module.store.getAt(i).data);
					}else{
						MyMessageTip.msg("提示", "职位信息第"+(i+1)+"行错误!", true);
						return 0;
					}
				}
			}
		}
		return 1;
	},
	doCancel : function() {
		var win = this.getWin();
		if (win)
			win.hide();
	},
//	createButtons : function() {
//		if (this.op == 'read') {
//			return []
//		}
//		var actions = this.actions;
//		var buttons = [];
//		if (!actions) {
//			return buttons;
//		}
//		var f1 = 112;
//		for (var i = 0; i < actions.length; i++) {
//			var action = actions[i];
//			var btn = {};
//			btn.accessKey = f1 + i, btn.cmd = action.id;
//			btn.text = action.name + "(F" + (i + 1) + ")", btn.iconCls = action.iconCls
//					|| action.id
//			btn.script = action.script;
//			btn.handler = this.doAction;
//			// ** add by yzh **
//			btn.notReadOnly = action.notReadOnly;
//			if (action.notReadOnly) {
//				btn.disabled = false
//			} else {
//				btn.disabled = this.exContext.readOnly || false
//			}
//			btn.scope = this;
//			btn.scale = "large";
//			btn.iconAlign = "top";
//			buttons.push(btn, '-');
//		}
//		return buttons
//	},
	doAction : function(item, e) {
		var cmd = item.cmd
		var script = item.script
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
	},
	doNew : function() {
		this.op = "create";
		if (this.midiModules["sysUserForm"]) {
			this.midiModules["sysUserForm"].doNew();
		}
		if (this.midiModules["sysUserEditorList"]) {
			this.midiModules["sysUserEditorList"].doNew();
		}
	},
	loadData : function() {
		if (this.midiModules["sysUserForm"]) {
			this.midiModules["sysUserForm"].initDataId = this.initDataId;
			this.midiModules["sysUserForm"].loadData();
		}
		if (this.midiModules["sysUserEditorList"]) {
			this.midiModules["sysUserEditorList"].initDataId = this.initDataId;
			this.midiModules["sysUserEditorList"].requestData.cnd = ['eq',
					['$', 'userId'], ['s', this.initDataId]]
			this.midiModules["sysUserEditorList"].loadData();
		}
	}
});