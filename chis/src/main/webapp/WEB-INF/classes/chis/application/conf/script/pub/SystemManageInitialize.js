$package("chis.application.conf.script.pub")
$import("app.desktop.Module", "util.Accredit")
chis.application.conf.script.pub.SystemManageInitialize = function(cfg) {
	cfg.fldDefaultWidth = 220;
	cfg.labelWidth = 120;
	cfg.autoFieldWidth = false;
	this.entryName = "SYS_CommonConfig"
	chis.application.conf.script.pub.SystemManageInitialize.superclass.constructor.apply(this,
			[cfg])
	this.colCount = 2;
	this.height = 400;
	this.title = "区域初始化";
	this.saveServiceId = "chis.systemCommonManageService";
	this.saveAction = "saveConfig";
	this.loadServiceId = "chis.systemCommonManageService";
	this.loadAction = "getConfig"
	this.c = null;
	this.p = null;
	this.on("save", this.onSave, this)
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.conf.script.pub.SystemManageInitialize, app.desktop.Module, {
	initPanel : function(sc) {
		var province = util.dictionary.SimpleDicFactory.createDic({
					id : 'areaGrid',
					parentKey : '0',
					sliceType : "3",
					width : 250,
					editable : false,
					defaultValue : '1'
				})
		province.name = 'province'
		province.fieldLabel = '省/自治区/直辖市'
		province.allowBlank = false
		if (province) {
			province.on("select", this.provinceSelect, this);
		}

		var city = util.dictionary.SimpleDicFactory.createDic({
					id : 'areaGrid',
					width : 250,
					editable : false,
					defaultValue : '1'
				})
		city.name = 'city'
		city.fieldLabel = '地级市/自治州/区'
		city.allowBlank = false

		if (city) {
			city.notfocusLoad = true;
			city.on("select", this.citySelect, this);
			city.store.on("beforeload", function() {
				var city = this.form.getForm().findField("city");
				var province = this.form.getForm().findField("province");
				var value = province.getValue();
				if (value) {
					city.store.proxy.conn.url = 'areaGrid.dic?src=SYS_CommomConfig.city&sliceType=3&parentKey='
							+ value;
				} else {
					return false;
				}
			}, this)
		}

		var region = util.dictionary.SimpleDicFactory.createDic({
					id : 'chis.dictionary.areaGrid',
					width : 250,
					editable : false,
					defaultValue : '1'
				})
		region.name = 'region'
		region.fieldLabel = '县级市/县/区'
		region.allowBlank = true

		if (region) {
			region.notfocusLoad = true;
			region.store.on("beforeload", function() {
				var region = this.form.getForm().findField("region");
				var city = this.form.getForm().findField("city");
				var value = city.getValue();
				if (value) {
					region.store.proxy.conn.url = 'areaGrid.dic?src=SYS_CommomConfig.region&sliceType=3&parentKey='
							+ value;
				} else {
					return false;
				}
			}, this)
		}

		var form = new Ext.FormPanel({
					tbar : [{
								text : "保存",
								iconCls : "save",
								handler : this.doSave,
								scope : this
							}],
					region : "north",
					bodyStyle : "padding:5px",
					height : 100,
					labelWidth : this.labelWidth || 80,
					border : true,
					frame : true,
					defaultType : 'textfield',
					defaults : {
						anchor : "100%"
					},
					shadow : true,
					items : [province, city, region]
				})
		this.form = form
		return form
	},

	provinceSelect : function(combo, record, index) {
		var form = this.form.getForm();
		var city = form.findField("city");
		var region = form.findField("region");
		if (region) {
			region.clearValue();
		}
		if (city) {
			city.enable();
			city.clearValue()
			var key = record.data.key
			var text = record.data.text
			if (key == "50" || key == "11" || key == "12" || key == "31") {
				city.setValue({
							key : key,
							text : text
						})
				city.disable();
			} else {
				city.focus()
			}
		}
	},

	citySelect : function(combo, record, index) {
		var form = this.form.getForm();
		var region = form.findField("region");
		if (region) {
			region.clearValue()
		}
	},

	doSave : function() {
		if (!this.form.getForm().isValid()) {
			return;
		}
		if (this.saving) {
			return
		}
		var values = this.getFormData();
		if (!values) {
			return;
		}
		Ext.apply(this.data, values);
		this.saveToServer(values);

	},

	getFormData : function() {
		var values = {};
		values.province = this.form.getForm().findField("province").getValue()
		values.city = this.form.getForm().findField("city").getValue()
		values.region = this.form.getForm().findField("region").getValue()
		values.province_text = this.form.getForm().findField("province")
				.getRawValue()
		values.city_text = this.form.getForm().findField("city").getRawValue()
		values.region_text = this.form.getForm().findField("region")
				.getRawValue()
		return values;
	},

	saveToServer : function(saveData) {
		this.form.el.mask("正在保存数据...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction || "",
					method:"execute",
					schema : this.entryName,
					op : this.op,
					body : saveData
				}, function(code, msg, json) {
					this.form.el.unmask();
					this.saving = false;
					var resBody = json.body;
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData], resBody);
						this.fireEvent("exception", code, msg, saveData);
						return;
					}
					Ext.apply(this.data, saveData);
					this.fireEvent("save", this.entryName, this.op, json,
							this.data);
					this.op = "update"
				}, this)
	},

	onSave : function(entryName, op, json, data) {
		Ext.Msg.show({
					title : '提示信息',
					msg : '配置完成,请重新登录以激活配置！',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {//预先加载一次用户，避免第一次登陆不成功
							this.form.getEl().mask("正在加载配置..")
							setTimeout(function(){
								var res = util.rmi.miniJsonRequestSync({
									serviceId : "chis.logonOut",
									method:"execute",
								});
								location.reload();
							},3000)
						}
					},
					scope : this
				})
	},

	onWinShow : function() {
		this.validate()
	},

	validate : function() {
		this.form.getForm().isValid();
	},

	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : 400,
						height : 170,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						items : this.initPanel(),
						constrainHeader : true,
						closable : false,
						// minimizable: true,
						// maximizable: true,
						shadow : false,
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
			var renderToEl = Ext.get('x-desktop')
			if (renderToEl) {
				win.render(renderToEl)
			}
			this.win = win
		}
		win.instance = this;
		return win;
	}
});