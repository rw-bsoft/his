$package("phis.application.fsb.script")

$import("phis.script.TableForm")

phis.application.fsb.script.FamilySickBedPatientSelectionModule = function(cfg) {
	cfg.width = 630;
	cfg.modal = true;
	Ext.apply(this, app.modules.common)
	phis.application.fsb.script.FamilySickBedPatientSelectionModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedPatientSelectionModule,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.panel) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					this.listLoadData();
					return this.panel;
				}
				var table = {
					labelAlign : "right",
					labelWidth : this.labelWidth || 70,
					iconCls : 'bogus',
					border : false,
					items : []
				}
				var brlb = this.getList();
				var form = this.getForm();
				table.items.push(form);
				this.listLoadData();
				table.items.push(brlb);
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
				this.panel = new Ext.FormPanel(table)
//				this.panel.on("afterrender", this.onReady, this)
				this.schema = "ZY_JSGL_FORM";
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.panel
			},
			getList : function() {
				var module = this.createModule("refPatientSelectionList",
						this.refList);
				module.on("click", this.doClick, this);
				module.on("dblClick", this.doDblClick, this);
				this.list = module;
				module.opener = this;
				var cfg = {
					title : "病人列表",
					layout : "fit",
					border : false,
					split : true,
					region : 'south',
					height : 300,
					collapsible : true,
					// collapsed : true,
					items : module.initPanel()
				}
				return cfg
			},
			getForm : function() {
				var module = this.createModule("refPatientSelectionForm",
						this.refForm);
				this.form = module;
				var formModule = module.initPanel();
				var form = module.form.getForm();
				var ZYHM = form.findField("ZYHM")
				var BRCH = form.findField("BRCH")
				var JSRQ = form.findField("JSRQ")
				var JSLX = form.findField("JSLX")
				var this_ = this;
				ZYHM.un("specialkey", module.onFieldSpecialkey, this)
				ZYHM.on("specialkey", function(ZYHM, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (ZYHM.getValue()) {
									var data = {};
									data.key = ZYHM.getName();
									data.value = ZYHM.getValue();
									this_.list.doQuery(data)
								}
							}
						}, this);
				ZYHM.focus(false, 200);
				BRCH.un("specialkey", module.onFieldSpecialkey, this)
				BRCH.on("specialkey", function(BRCH, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (BRCH.getValue()) {
									var data = {};
									data.key = BRCH.getName();
									data.value = BRCH.getValue();
									this_.list.doQuery(data)
								}
							}
						}, this);
				JSRQ.un("specialkey", module.onFieldSpecialkey, this)
				JSRQ.on("specialkey", function(JSRQ, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (JSRQ.getValue()) {
									this.doCommit();
								}
							}
						}, this);
				JSLX.on("select", function() {
							if (JSLX.getValue() == 10) {
								this_.list.schema = 'ZY_BRRY_CYLB';
								this_.list.requestData.cnd = [
										'and',
										[
												'and',
												[
														'eq',
														['$', 'a.JGID'],
														[
																's',
																this_.mainApp.deptId]],
												['eq', ['$', 'b.ZFPB'],
														['i', 0]]],
										['ne', ['$', 'b.JSLX'], ['i', 0]]];
								this_.list.refresh();
							} else if (JSLX.getValue() == 5) {
								this_.listLoadData();
							}
						}, this)

				this.form = module;
				module.opener = this;
				return formModule
			},
			listLoadData : function(pharmarcyId) {
				this.list.schema = 'ZY_BRRY_CYLB_ZC';
				this.list.requestData.cnd = ['and',
						['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['eq', ['$', 'a.CYPB'], ['i', 1]]];
				this.list.refresh();
			},
			doNew : function() {
				this.form.doNew();
			},
			doClick : function(data) {
				var form = this.form.form.getForm();
				var JSLX = form.findField("JSLX")
				
				this.data = data;
				this.data.JSLX = JSLX.getValue();
				this.form.doChoose(data);
			},
			doDblClick : function(data) {
				var form = this.form.form.getForm();
				var JSLX = form.findField("JSLX")
				this.data = data;
				this.data.JSLX = JSLX.getValue();
				this.form.doChoose(data);
				this.doCommit();
			},
			doCommit : function() {
				r = this.list.grid.getSelectionModel().getSelected();
				if(r == null){
					MyMessageTip.msg("提示", "请选择病人后在确定", true);
					return;
				}
				var form = this.form.form.getForm();
				var JSLX = form.findField("JSLX");
				var JSRQ = form.findField("JSRQ");
				if (!JSLX.getValue()) {
					MyMessageTip.msg("提示", "请选择结算类型", true);
					return;
				}
				if (!this.data) {
					return;
				}
				this.data.JSLX = JSLX.getValue();
				if (JSRQ.getValue()) {
					this.data.JSRQ = JSRQ.getValue();
				}
				this.fireEvent("commit", this.data);
				this.doCancel()
			}
		});