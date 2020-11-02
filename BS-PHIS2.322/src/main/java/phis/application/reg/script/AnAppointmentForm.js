$package("phis.application.reg.script");

$import("phis.script.TableForm")

phis.application.reg.script.AnAppointmentForm = function(cfg) {
	cfg.showButtonOnTop = false;
	this.labelWidth = 55;
	phis.application.reg.script.AnAppointmentForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.reg.script.AnAppointmentForm, phis.script.TableForm, {
			doJZKHChange : function(f) {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "anAppointmentService",
							serviceAction : "queryPerson",
							JZKH : f.getValue()
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该病人不存在!", function() {
									f.focus(false, 100);
								});
						return;
					}
					var datas = r.json.body;
					this.MZXX = datas;
					this.doChangeAfter();
				}
				this.doCommit(this.MZXX.BRID);
			},
			doChangeAfter : function(){
				var form = this.form.getForm();
				var JZKH = form.findField("JZKH");
				var FPHM = form.findField("BRXM");
				var BRXB_text = form.findField("BRXB_text");
				var BRXM = form.findField("BRXM");
				var LXDZ = form.findField("LXDZ");
				var BRNL = form.findField("BRNL");
				var BRXZ = form.findField("BRXZ");
				if (this.MZXX.JZKH) {
					JZKH.setValue(this.MZXX.JZKH);
				}
				if (this.MZXX.BRXM) {
					BRXM.setValue(this.MZXX.BRXM);
				}
				if (this.MZXX.BRXB) {
					var brxb = "";
					if (this.MZXX.BRXB == 1) {
						brxb = "男";
					} else if (this.MZXX.BRXB == 2) {
						brxb = "女";
					} else if (this.MZXX.BRXB == 9) {
						brxb = "未说明的性别";
					} else if (this.MZXX.BRXB == 0) {
						brxb = "未知的性别";
					}
					BRXB_text.setValue(brxb);
				}
				if (this.MZXX.AGE) {
					BRNL.setValue(this.MZXX.AGE);
				}
				if (this.MZXX.BRXZ) {
					BRXZ.setValue(this.MZXX.BRXZ);
				} else {
					BRXZ.setValue(6);
				}
				if (this.MZXX.LXDZ) {
					LXDZ.setValue(this.MZXX.LXDZ);
				}
			},
			doMZHMChange : function(data) {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "anAppointmentService",
							serviceAction : "queryPerson",
							MZHM : data.MZHM
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该病人不存在!");
						return;
					}
					var datas = r.json.body;
					this.MZXX = datas;
					this.doChangeAfter();
				}
				this.doCommit(this.MZXX.BRID);
			},
			doENTER : function(field) {
				if (field.getValue() == '') {
					this.showModule();
				} else {
					// 去查询处方单
					this.doJZKHChange(field);
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
					m.on("onEmpiReturn", this.doMZHMChange, this);
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			doCommit : function(brid) {
				this.opener.showCFD(brid);
			}
		})