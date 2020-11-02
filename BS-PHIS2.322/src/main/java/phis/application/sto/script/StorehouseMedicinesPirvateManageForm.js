$package("phis.application.sto.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.sto.script.StorehouseMedicinesPirvateManageForm = function(cfg) {
	cfg.fldDefaultWidth = 100;
	cfg.height=510;
	cfg.entryName="phis.application.sto.schemas.YK_YPXX";
	cfg.loadPirvateMedicinesServiceId="storehouseManageService";
	cfg.loadPirvateMedicinesActionId="loadPirvateMedicinesInformation";
	phis.application.sto.script.StorehouseMedicinesPirvateManageForm.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.sto.script.StorehouseMedicinesPirvateManageForm,
		phis.script.SimpleForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
							labelWidth : 85, // label settings here cascade
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							width : 1024,
							autoHeight : true,
							height:this.height,
							items : [{
										xtype : 'fieldset',
										title : '基本信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 5,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('JBXX')
									}, {
										xtype : 'fieldset',
										title : '药品包装',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 4,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('YPBZ')
									}, {
										xtype : 'fieldset',
										title : '其他',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 4,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('QT')
									}, {
										xtype : 'fieldset',
										title : '抗生素',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 4,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('KSS')
									}, {
										xtype : 'fieldset',
										title : '高、低储',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 5,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('GDC')
									}]
						});
						this.form.on("afterrender", this.onReady, this)
				if (!this.isCombined) {
					this.addPanelToWin();
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
				this.schema = schema;
				return this.form
			},
			onReady : function() {
				phis.application.sto.script.StorehouseMedicinesPirvateManageForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("GCSL").on("blur", this.onGDCChange, this);
				form.findField("DCSL").on("blur", this.onGDCChange, this);
			},
			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			// 由于未框架的simpleloads只实现了单主键的多表数据查询,故重写
			loadData : function() {
				if (this.loading) {
					return;
				}
				if (!this.schema) {
					return;
				}
				if (!this.initDataId && !this.initDataBody) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return;
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading");
				}
				this.loading = true;
				phis.script.rmi.jsonRequest({
							serviceId : this.loadPirvateMedicinesServiceId,
							serviceAction : this.loadPirvateMedicinesActionId,
							body : this.initDataBody
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask();
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData);
								return;
							}
							if (json.body) {
								this.initFormData(json.body);
								this.fireEvent("loadData", this.entryName,
										json.body);
							}
							if (this.op == 'create') {
								this.op = "update";
							}

						}, this)// jsonRequest
			},
			onGDCChange : function() {
				var form = this.form.getForm();
				var gcsl= form.findField("GCSL").getValue();
				var dcsl = form.findField("DCSL").getValue();
				if (parseFloat(dcsl) > parseFloat(gcsl)) {
					MyMessageTip.msg("提示", "低储数量不能高于高储数量!", true);
					return false;
				}
				return true;
			}
		});