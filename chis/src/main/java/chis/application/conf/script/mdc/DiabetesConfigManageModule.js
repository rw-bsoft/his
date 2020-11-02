$package("chis.application.conf.script.mdc")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.mdc.DiabetesConfigManageModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.conf.script.mdc.DiabetesConfigManageModule.superclass.constructor
			.apply(this, [cfg])
	this.layOutRegion = "north"
	this.itemWidth = this.width
	this.itemHeight = 535
	this.itemCollapsible = false
	this.firstTitle = "糖尿病档案参数设置"
	this.secondTitle = "糖尿高危参数设置"
}
Ext.extend(chis.application.conf.script.mdc.DiabetesConfigManageModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.conf.script.mdc.DiabetesConfigManageModule.superclass.initPanel
						.call(this)

				var form = new Ext.Panel({
							frame : true,
							tbar : [{
										text : '确定',
										iconCls : 'save',
										handler : this.doSave,
										scope : this
									}],
							defaultType : 'textfield',
							items : panel
						});
				this.form = form;
				return form
				// this.form = form
				// return form;
				// this.panel = panel;
				// this.form = this.midiModules[this.actions[0].id];
				// this.form.on("save", this.onSave, this)
				// this.form.on("planModeChange", this.planModeChange, this)
				// this.list = this.midiModules[this.actions[1].id];
				// this.list.on("loadData", this.onLoadData, this)
				// this.grid = this.list.grid;
				// this.grid.setAutoScroll(true);
				// return panel;
			},

			loadData : function() {
				this.midiModules[this.actions[0].id].loadData();
				this.midiModules[this.actions[1].id].loadData();
				this.resetButtonsReadOnly();
			},
			

			resetButtonsReadOnly : function() {
				if (this.readOnly == null) {
					return;
				}
				if (this.form.getTopToolbar()) {
					var btns = this.form.getTopToolbar().items;
					if (!btns) {
						return;
					}
					var n = btns.getCount();
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i);
						if (btn.prop&&btn.prop.notReadOnly) {
							continue;
						}
						var status = this.readOnly;
						if (status == null) {
							return;
						}
						this.changeButtonStatus(btn, !status);
					}
				}
			},

			onSave : function() {
				// this.saveData();
			},

			doSave : function() {
				if (!this.midiModules[this.actions[0].id].validate()
						|| !this.midiModules[this.actions[1].id].validate()) {
					return
				}
				this.midiModules[this.actions[0].id].doSave()
				this.midiModules[this.actions[1].id].doSave()
				Ext.Msg.show({
							title : '提示信息',
							msg : '配置完成,请重新登录以激活配置！',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									location.reload();
								}
							},
							scope : this
						})
			}

		});
