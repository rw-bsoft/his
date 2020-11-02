$package("phis.application.fsb.script")

$import("phis.script.TableForm")

phis.application.fsb.script.FamilySickBedCheckForm = function(cfg) {
	cfg.colCount = 1;
	cfg.labelWidth = 60;
	cfg.autoLoadData = false;
	this.serviceId = "familySickBedManageService";
	phis.application.fsb.script.FamilySickBedCheckForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedCheckForm,
		phis.script.TableForm, {
			// 去除form光标跳转
			focusFieldAfter : function() {

			},
			editDiagnose : function() {
				if (this.op == 'create') {
					Ext.Msg.alert("提示", "请先保存查床信息");
					return;
				}
				var m = this.createModule("refDiagnoseEditorList",
						this.refDiagnoseEditorList);
				m.ZYH = this.opener.initDataId;
				m.CCXH = this.initDataId;
				m.exContext = this.exContext;
				m.requestData.cnd = ['eq', ['$', 'ZYH'],
						['l', this.opener.initDataId]];
				if (!this.editDiagnoseWin) {
					var win = m.getWin();
					win.add(m.initPanel())
					this.editDiagnoseWin = win;
					m.on("doRefresh", this.listRefresh, this);
				}
				this.editDiagnoseWin.show();
			},
			onReady : function() {
				// 添加诊断记录
				var ctx = this;
				var label = new Ext.form.Label({
							name : "ZDXX",
							text : "疾病诊断:",
							width : 65
						});
				var button = new Ext.Button({
							text : "编  辑",
							handler : this.editDiagnose,
							scope : this
						});
				this.diaBtn = button;
				var vbox = new Ext.Container({
							// colspan : 2,
							height : 50,
							layout : "vbox",
							hideBorders : true,
							items : [label, button]
						});
				var m = this.createModule("refDiagnoseList",
						this.refDiagnoseList, {
							height : this.opener.complexPanel.getHeight() - 350
						});
				m.showRowNumber = false;
				m.requestData.cnd = ['eq', ['$', 'ZYH'],
						['l', this.opener.initDataId]];
				m.expansion = function(cfg) {
					cfg.frame = false;
				}
				m.showZd = function(v, params, r) {
					if (r.get("CCXH") != ctx.initDataId) {
						params.css = "x-grid-record-pink";
						var tips = "开具：" + r.get("ZDSJ") + " "
								+ r.get("ZDYS_text")
						tips = '<div class=\'ext-cell-tip\'>' + tips + '</div>';
						params.attr = 'ext:qtip="' + tips + '"';
						if (v == 1) {
							return "<font>家</font>"
						} else if (v == 2) {
							return "<font>撤</font>"
						}
						return;
					}
					if (v == 1) {
						return "<font>家</font>"
					} else if (v == 2) {
						return "<font>撤</font>"
					}
				}
				this.list = m;
				var grid = m.initPanel();
				var panel = new Ext.Container({
							// colspan : 2,
							width : 230,
							layout : "hbox",
							hideBorders : true,
							items : [vbox, grid]
						})
				this.form.add(panel);
				this.doLoadCcjl();
			},
			listRefresh : function() {
				this.list.loadData();
			},
			doLoadCcjl : function() {
				if (this.initDataId) {
					this.loadData();
					this.diaBtn.setDisabled(false);
					this.list.requestData.cnd = ['eq', ['$', 'ZYH'],
							['l', this.opener.initDataId]];
					this.list.loadData();
				} else {
					// 初始化时，设置默认插床日期
					this.form.getForm().findField('CCSJ').setValue(Date
							.getServerDateTime());
					this.diaBtn.setDisabled(true);
				}
			},
			getSaveRequest : function(saveData) {
				saveData.ZYH = this.exContext.ids.clinicId;
				return saveData;
			},
			// 删除查床记录
			doRemove : function() {
				var f = this.form.getForm();
				if (this.op == 'create') {
					this.doNew();
					return;
				}
				var ccsj = f.findField("CCSJ").getValue();
				Ext.Msg.show({
							title : '提醒',
							msg : '确认删除[' + ccsj + ']的查床记录吗?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},
			processRemove : function() {
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "removeFsbCheck",
							body : {
								ZYH : this.opener.initDataId,
								CCXH : this.initDataId
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg)
								return false;
							}
							this.fireEvent("save")
						}, this);
			}
		});
