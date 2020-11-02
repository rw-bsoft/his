$package("phis.application.cic.script")

$import("phis.script.TableForm")

phis.application.cic.script.ReferralAppointmentForm = function(cfg) {
	phis.application.cic.script.ReferralAppointmentForm.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.cic.script.ReferralAppointmentForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
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
					width : this.width,
					height : this.height,
					floating : false
				}
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.expansion(table);// add by yangl
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)

				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			onReady : function() {
				phis.application.cic.script.ReferralAppointmentForm.superclass.onReady
						.call(this);
				var now = this.form.getForm().findField("YYRQ").getValue();// 拿到预约日期
				var newdate = new Date();
				if (newdate.getHours() < 12) {
					this.form.getForm().findField("ZBLB").setValue(1);
				} else {
					this.form.getForm().findField("ZBLB").setValue(2);
				}
				var newtimems = now.getTime()
						+ (this.YYJGTS * 24 * 60 * 60 * 1000);
				newdate.setTime(newtimems);
				var maxDat = newdate.dateFormat('Y-m-d');// 根据天数算出最大日期
				var DaysToAdd = this.form.getForm().findField("YYRQ")
						.setMaxValue(maxDat);
				this.form.getForm().findField("YYRQ").setValue(maxDat);
			},
			doSave : function() {
				var GHFZV = 0;
				if (this.form.getForm().findField("GHFZ").getValue() == true) {
					GHFZV = 1;
				} else {
					GHFZV = 0;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "verificationReservationInfo",
							op : this.op,
							body : {
								BRID : this.exContext.ids.brid,
								clinicId : this.exContext.ids.clinicId,
								FZRQ : this.form.getForm().findField("YYRQ")
										.getValue().format('Y-m-d'),
								GHFZ : GHFZV,
								KSDM : this.mainApp['phis'].reg_departmentId,
								YSDM : this.mainApp.uid,
								ZBLB : this.form.getForm().findField("ZBLB")
										.getValue()
							}
						});
				if (GHFZV == 1) {
					var yyrq = this.form.getForm().findField("YYRQ").getValue()
							.format('Y-m-d');
					var zblb = this.form.getForm().findField("ZBLB").getValue();
					var dqzblb = 0;
					if (new Date().getHours() < 12) {
						dqzblb = 1;
					} else {
						dqzblb = 2;
					}
					if (yyrq == new Date().format('Y-m-d') && zblb == dqzblb) {
						MyMessageTip.msg("提示", "当天当前班次不能预约!", true);
						return;
					} else if (yyrq == new Date().format('Y-m-d') && zblb == 1
							&& dqzblb == 2) {
						MyMessageTip.msg("提示", "当天下午不能预约上午的号!", true);
						return;
					}
					if (r.json.yspb) {// 如果医生已经排班
						if (parseInt(r.json.yspb.YYXE) <= parseInt(r.json.yspb.YYRS)
								&& r.json.yspb.YYXE != 0) {
							Ext.Msg.confirm("提示", "当天医生预约人数超限,是否继续?", function(
									btn) {// 先提示是否删除
										if (btn == 'yes') {
											this.publicSave();
										}
										return;
									}, this);
						} else {
							this.publicSave();
						}
					} else {
						if (r.json.kspb) {// 如果科室已经排班,医生未排班
							if (parseInt(r.json.kspb.YYXE) <= parseInt(r.json.kspb.YYRS)
									&& r.json.kspb.YYXE != 0) {
								Ext.Msg.confirm("提示", "当天医生未排班,科室预约人数超限,是否继续?",
										function(btn) {// 先提示是否删除
											if (btn == 'yes') {
												this.publicSave();
											}
											return;
										}, this);
							} else {
								Ext.Msg.confirm("提示", "当天本科室已排班,医生未排班，是否继续?",
										function(btn) {// 先提示是否删除
											if (btn == 'yes') {
												this.publicSave();
											}
											return;
										}, this);
							}
						} else if (!r.json.kspb) {// 如果科室和医生没排班
							Ext.Msg.confirm("提示", "当天本科室,本医生都未排班，是否继续?",
									function(btn) {// 先提示是否删除
										if (btn == 'yes') {
											this.publicSave();
										}
										return;
									}, this);
						}
					}
				} else {
					MyMessageTip.msg("提示", "挂号预约操作成功!", true);
				}
			},
			publicSave : function() {
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
					serviceId : "clinicManageService",
					serviceAction : "saveReservationInfo",
					body : {
						BRID : this.exContext.ids.brid,
						clinicId : this.exContext.ids.clinicId,
						FZRQ : this.form.getForm().findField("YYRQ").getValue()
								.format('Y-m-d'),
						GHRQ : new Date().format('Y-m-d'),
						KSDM : this.mainApp['phis'].reg_departmentId,
						YSDM : this.mainApp.uid,
						GHBZ : 0,
						ZCID : 0,
						YYMM : 0,
						JZXH : 0,
						JGID : this.mainApp['phisApp'].deptId,
						ZBLB : this.form.getForm().findField("ZBLB").getValue(),
						YYLB : 1
					}
				}, function(code, msg, json) {
					this.form.el.unmask();
					if (code > 300) {
						if (code == 701) {
							MyMessageTip.msg("提示", msg, true);
							return false;
						}
						this.processReturnMsg(code, msg);
						return;
					}
					MyMessageTip.msg("提示", "挂号预约操作成功!", true);
				}, this)
			}
		});
