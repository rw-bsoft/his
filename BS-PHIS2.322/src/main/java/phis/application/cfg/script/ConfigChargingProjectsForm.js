$package("phis.application.cfg.script")
/**
 * 收费项目维护的医疗项目 caijy 2012.05
 */
$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.cfg.script.ConfigChargingProjectsForm = function(cfg) {
	this.entryName = "phis.application.cfg.schemas.GY_SFXM_XG";
	this.schema = "phis.application.cfg.schemas.GY_SFXM_XG";
	phis.application.cfg.script.ConfigChargingProjectsForm.superclass.constructor.apply(
			this, [cfg]);
	this.on("beforeSave", this.onBeforeSave, this);

}
Ext.extend(phis.application.cfg.script.ConfigChargingProjectsForm,
		phis.script.SimpleForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.actions = [{
							id : "save",
							name : "保存"
						}]
				if (!this.isCombined)
					this.actions.push({
								id : "cancel",
								name : "关闭",
								iconCls : "common_cancel",
								notReadOnly : true
							})
				this.form = new Ext.FormPanel({
							labelWidth : 85, // label settings here cascade
							// unless overridden
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							width : 1024,
							autoHeight : true,
							items : [{
										xtype : 'fieldset',
										title : '基本信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 2,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										listeners : {
											"afterrender" : this.onReady,
											scope : this
										},
										defaultType : 'textfield',
										items : this.getItems('JBXX')
									}, {
										xtype : 'fieldset',
										title : '其他属性',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 2,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('QT')
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
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
						// alert(it.acValue);
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
			expand : function() {
				this.win.center();
			},
			saveToServer : function(saveData) {
				var saveRequest = this.getSaveRequest(saveData); // **
				// 获取保存条件数据
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveRequest)) {
					return;
				}

				this.saving = true;
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "configChargingProjectsSaveService",
							serviceAction : "saveChargingProjects",
							method : "execute",
							op : this.op,
							body : saveRequest
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return;
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.op = "update"
						}, this)
			},
			onBeforeSave : function(entryName, op, saveData) {
				var FYFL = this.exContext.FYFL;
				Ext.apply(saveData, {
							"FYFL" : FYFL
						})
				var data = {
					"SFMC" : saveData.SFMC
				};
				if (this.initDataId) {
					data["SFXM"] = this.initDataId;
				}
				data.ZBLB = saveData.ZBLB;
				data.PLSX = saveData.PLSX;
				data.ZYPL = saveData.ZYPL;
				data.MZPL = saveData.MZPL;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configChargingProjectsService",
							serviceAction : "ChargingProjectsVerification",
							schemaDetailsList : "GY_SFXM",
							method : "execute",
							op : this.op,
							body : data
						});
				if (r.code == 612) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 613) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
			}
		});
