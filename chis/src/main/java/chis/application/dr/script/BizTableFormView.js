$package("app.biz")
$import("util.Accredit", "app.modules.form.SimpleFormView",
		"org.ext.ux.layout.TableFormLayout")
app.biz.BizTableFormView = function(cfg) {
	this.msgBoxWidth=300;
	this.colCount = 3;
	this.autoFieldWidth = true;
	this.closeAfterSave = true;
//	cfg.labelWidth = 70;
	cfg.width = 750
	app.biz.BizTableFormView.superclass.constructor.apply(this, [cfg])
}
Ext.extend(app.biz.BizTableFormView, app.modules.form.SimpleFormView, {
	initPanel : function(sc) {
		if (this.form) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			return this.form;
		}
		var schema = sc
		Ext.apply(this,this.properties);
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
		var colCount = this.colCount;

		var table = {
			layout : 'tableform',
			layoutConfig : {
				columns : colCount,
				tableAttrs : {
					border : 0,
					cellpadding : '2',
					cellspacing : "2"
				}
			},
			items : []
		}
		if (!this.autoFieldWidth) {
			var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
					* colCount
			table.layoutConfig.forceWidth = forceViewWidth
		}
		var size = items.length
		for (var i = 0; i < size; i++) {
			var it = items[i]
			if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
				continue;
			}
			var f = this.createField(it)
			f.index = i;
			f.anchor = it.anchor || "100%"
			delete f.width

			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)

			if (!this.fireEvent("addfield", f, it)) {
				continue;
			}
			table.items.push(f)
		}

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
	saveToServer : function(saveData) {
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveData)) {
			return;
		}
		if (this.initDataId == null) {
			this.op = "create";
		}
		this.saving = true
		this.form.el.mask("在正保存数据...", "x-mask-loading")
		util.rmi.jsonRequest({
					method:"execute",
					serviceId : this.saveServiceId,
					action : this.saveAction || "save",
					op : this.op,
					schema : this.entryName,
					deptId:this.mainApp.deptId,
					body : saveData
				}, function(code, msg, json) {
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData], json.body);
						return
					}
					Ext.apply(this.data, saveData);
					if (json.body) {
						this.initFormData(json.body)
						this.fireEvent("save", this.entryName, this.op, json,
								this.data)
					}
					if (this.closeAfterSave) {
						if (this.win) {
							this.win.hide();
						}
					}
					this.op = "update"
					this.fireEvent("afterSave");
				}, this)
	},
	loadData : function() {
		if (this.loading) {
			return
		}

		if (!this.schema) {
			return
		}
		if (!this.initDataId) {
			return;
		}
		if (!this.fireEvent("beforeLoadData", this.entryName, this.initDataId)) {
			return
		}
		if (this.form && this.form.el) {
			this.form.el.mask("正在载入数据...", "x-mask-loading")
		}
		this.loading = true
		util.rmi.jsonRequest({
					method:"execute",
					serviceId : this.loadServiceId,
					schema : this.entryName,
					action : this.loadAction,
					pkey : this.initDataId
				}, function(code, msg, json) {
					if (this.form && this.form.el) {
						this.form.el.unmask()
					}
					this.loading = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.loadData)
						return
					}
					if (json.body) {
						this.doNew()
						this.initFormData(json.body)
						this.fireEvent("loadData", this.entryName, json.body);
					}
					if (this.op == 'create') {
						this.op = "update"
					}

				}, this)// jsonRequest
	},
	getFormData : function(){
		if (this.saving) {
			return
		}
		var ac = util.Accredit;
		var form = this.form.getForm();
		if (!this.form.getForm().isValid()) {
			return
		}
		if (!this.schema) {
			return
		}
		var values = {};
		var items = this.schema.items

		Ext.apply(this.data, this.exContext)

		if (items) {
			var n = items.length
			for (var i = 0; i < n; i++) {
				var it = items[i]
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					continue;
				}
				var v = it.defaultValue || this.data[it.id]

				if (v != null && typeof v == "object") {
					v = v.key
				}
				var f = form.findField(it.id)
				if (f) {
					v = f.getValue()
					// add by huangpf
					if (f.getXType() == "treeField") {
						var rawVal = f.getRawValue();
						if (rawVal == null || rawVal == "")
							v = "";
					}
					// end
				}

				if (v == null || v === "") {
					if (!(it.pkey == "true")
							&& (it["not-null"] == "1" || it['not-null'] == "true")
							&& !it.ref) {
						if (it.id != 'personName' || !this.personNameEmptry) {
							alert(it.alias + "不能为空")
							return;
						}
					}
				}
				values[it.id] = v;
			}
		}

		Ext.apply(this.data, values);
		return values;
	},
	
	setInitDataId : function(initDataId){
		this.initDataId = initDataId;
	},
	
	createButtons:function(){
		var actions = this.actions
		var buttons = []
		if(!actions){
			return buttons
		}
		var f1 = 112

		for(var i = 0; i < actions.length; i ++){
			var action = actions[i];
			var btn = {}
			btn.accessKey = f1 + i,
			btn.cmd = action.id
			btn.text = action.name + "(F" + (i + 1) + ")",
			btn.iconCls = action.iconCls || action.id
			btn.script =  action.script
			btn.handler = this.doAction;
			btn.scope = this;
			buttons.push(btn)
		}
		return buttons
	}
});