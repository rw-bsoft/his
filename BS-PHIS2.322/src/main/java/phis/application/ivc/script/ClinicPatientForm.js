$package("phis.application.ivc.script")

$import("phis.script.TableForm")

phis.application.ivc.script.ClinicPatientForm = function(cfg) {
	this.hide = false;
	phis.application.ivc.script.ClinicPatientForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ivc.script.ClinicPatientForm, phis.script.TableForm, {
			doMZHMChange : function(f) {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryPerson",
							MZHM : f.getValue()
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该门诊号码不存在!");
						f.focus(false, 100);
						return;
					}
					var dates = r.json.body;
					var form = this.form.getForm();
					var MZHM = form.findField("MZHM");
					var BRXZ = form.findField("BRXZ");
					var BRXM = form.findField("BRXM");
					if (dates.BRXZ) {
						BRXZ.setValue(dates.BRXZ);
					} else {
						BRXZ.setValue(6);
					}
					BRXM.setValue(dates.BRXM);
					this.BRID = dates.BRID;
					//return this.BRID;
				}
				this.doCommit();
			},
			doENTER : function(field) {
				if (field.getValue() == '') {
					this.showModule();
				} else {
					// 去查询处方单
					this.doMZHMChange(field);
					//this.showghList(this.BRID);
				}
			},
			showModule : function() {
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("phis.script.pix.EMPIInfoModule");
					m = new phis.script.pix.EMPIInfoModule({
								entryName : "MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							});
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			doCommit : function() {
				var from = this.form.getForm();
				var BRXM = from.findField("BRXM");
				var MZHM = from.findField("MZHM");
				var BRXZ = from.findField("BRXZ");
				if (!MZHM.getValue()) {
					MyMessageTip.msg("提示", "请输入门诊号码!", true);
					MZHM.focus(false, 200);
					return;
				}
				var parentFrom = this.opener.formModule.form
						.getForm();
				var KSDM = from.findField("KSDM");
				parentFrom.findField("MZGL").setValue(MZHM.getValue());
				parentFrom.findField("BRXM").setValue(BRXM.getValue());
				parentFrom.findField("BRXZ").setValue(BRXZ.getValue());
				this.hide = true;
				this.getWin().hide();
				var body = {
					"BRID" : this.BRID
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryCF",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (r.json.count > 0) {
						this.opener.showCFD(body);
					}
				}
			},
			beforeClose : function() {
				var md = this;
				if (!this.hide) {
					Ext.MessageBox.confirm('提示', '是否退出收费模块?', function(but){
						if(but=='yes'){
							md.opener.opener.closeCurrentTab();//关闭收费模块
							return;
						}else{
							md.getWin().show();
						}
					});
				}
				this.hide = false;
				return false;
			},
			doConcel : function() {
				var from = this.form.getForm();
				var MZHM = from.findField("MZHM");
				MZHM.setValue("");
				var BRXZ = from.findField("BRXZ");
				BRXZ.setValue("");
				var BRXM = from.findField("BRXM");
				BRXM.setValue("");
				var KSDM = from.findField("KSDM");
				KSDM.setValue("");
				MZHM.focus(false, 200);
			},
			getWin : function() {
				var module = this;
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : false,
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true,
								buttons : [{
											text : '确定',
											handler : module.doCommit,
											scope : module
										}, {
											text : '取消',
											handler : module.doConcel,
											scope : module
										}]
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("beforehide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				return win;
			}
		})