$package("phis.application.ivc.script")

$import("phis.script.TableForm")

phis.application.ivc.script.ClinicChargesPatientForm = function(cfg) {
	// cfg.autoLoadData = false;
	cfg.showButtonOnTop = false
	cfg.remoteUrl = 'Disease';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{JBMC}</td></td>';
	cfg.ZD1 = "";
	cfg.ZD2 = "";
	cfg.ZD3 = "";
	phis.application.ivc.script.ClinicChargesPatientForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.ivc.script.ClinicChargesPatientForm,
		phis.script.TableForm, {
			doJZKHChange : function(f) {
				var body = {
					serviceId : "clinicChargesProcessingService",
					serviceAction : "queryPerson",
					logonName : this.mainApp.logonName,
					useBy : 'charges'// 收费处调用
					// add by yangl 疫苗建档用
				}
				if (!f.MZHM) {
					body.JZKH = f.getValue();
				} else {
					body.MZHM = f.MZHM
				}
				var r = phis.script.rmi.miniJsonRequestSync(body);
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该卡号不存在!", function() {
									f.focus(false, 100);
								});
						return;
					}
					this.MZXX = r.json.body;
					this.BLLX = r.json.body.BLLX;
//					if (this.opener.ybxx) {
//						this.opener.ybxx.BLLX = r.json.body.BLLX;
//					} else {
//						this.opener.ybxx = {};
//						this.opener.ybxx.BLLX = r.json.body.BLLX;
//					}
					this.opener.clearYbxx();
//					this.MZXX.pdgdbz = r.json.PDGDBZ;// 规定病种
					if (!r.json.body.GHGL) {
						var this_ = this;
						if (r.json.body.chooseGhks) {
							this.showKsxzWin();
							return;
						}
						// add by yangl 挂号模式没有挂号直接终止调入
						Ext.Msg.alert("提示", "未挂号病人不能收费，请先挂号!");
						return;
						// Ext.MessageBox.confirm('提示', '该患者没有挂号记录,是否继续?',
						// function(btn) {
						// if (btn == 'yes') {
						// var form = this_.form.getForm();
						// var MZGL = form.findField("MZGL");
						// var FPHM = form.findField("FPHM");
						// var BRXZ = form.findField("BRXZ");
						// var BRXM = form.findField("BRXM");
						// var BRXB = form.findField("BRXB");
						// var AGE = form.findField("AGE");
						// if (this_.MZXX.BRXZ) {
						// BRXZ.setValue(this_.MZXX.BRXZ);
						// }
						// if (this_.MZXX.AGE) {
						// AGE.setValue(this_.MZXX.AGE);
						// }
						// if (this_.MZXX.JZKH) {
						// MZGL.setValue(this_.MZXX.JZKH);
						// } else {
						// MZGL.setValue();
						// }
						// if (this_.initDataId) {
						// FPHM.setValue(this_.initDataId);
						// }
						// BRXB.setValue(this_.MZXX.BRXB);
						// BRXM.setValue(this_.MZXX.BRXM);
						// this_.doCommit();
						// form.findField("MZGL").disable();
						// } else {
						// this_.opener.doQx();
						// }
						// });
					} else {
						var form = this.form.getForm();
						var MZGL = form.findField("MZGL");
						var FPHM = form.findField("FPHM");
						var BRXZ = form.findField("BRXZ");
						var BRXM = form.findField("BRXM");
						var BRXB = form.findField("BRXB");
						var AGE = form.findField("AGE");
						if (this.MZXX.BRXZ) {
							BRXZ.setValue(this.MZXX.BRXZ);
						} else {
							BRXZ.setValue(6);
						}
						if (this.MZXX.AGE) {
							AGE.setValue(this.MZXX.AGE);
						}
						if (this.MZXX.JZKH) {
							MZGL.setValue(this.MZXX.JZKH);
						} else {
							MZGL.setValue();
						}
						if (this.initDataId) {
							FPHM.setValue(this.initDataId);
						}
						BRXB.setValue(this.MZXX.BRXB);
						BRXM.setValue(this.MZXX.BRXM);
						this.doCommit();
						this.form.getForm().findField("MZGL").disable();
					}

				}

			},
			loadData : function() {
				if (!this.MZXX)
					return;
				var form = this.form.getForm();
				var MZGL = form.findField("MZGL");
				var FPHM = form.findField("FPHM");
				var BRXZ = form.findField("BRXZ");
				var BRXM = form.findField("BRXM");
				var BRXB = form.findField("BRXB");
				var AGE = form.findField("AGE");
				if (this.MZXX.BRXZ) {
					BRXZ.setValue(this.MZXX.BRXZ);
				} else {
					BRXZ.setValue(6);
				}
				if (this.MZXX.AGE) {
					AGE.setValue(this.MZXX.AGE);
				}
				if (this.MZXX.JZKH) {
					MZGL.setValue(this.MZXX.JZKH);
				} else {
					MZGL.setValue();
				}
				if (this.initDataId) {
					FPHM.setValue(this.initDataId);
				}
				BRXB.setValue(this.MZXX.BRXB);
				BRXM.setValue(this.MZXX.BRXM);
				this.ZD1 = "";
				this.ZD2 = "";
				this.ZD3 = "";
			},
			showKsxzWin : function() {
				if (!this.reg_form) {
					var GHKS = this.createDicField({
						id : "phis.dictionary.department_reg",
						filter : "['eq',['$','item.properties.JGID']],['$','%user.manageUnit.id']]"
					})
					this.Field = GHKS;
					this.reg_form = new Ext.FormPanel({
								frame : true,
								labelWidth : 75,
								defaults : {
									width : '95%'
								},
								shadow : true,
								items : [{
											fieldLabel : "挂号科室",
											name : "GHKS",
											items : GHKS
										}]
							})
				} else {
					// var reg_form = this.reg_form.getForm()
					// this.Field = reg_form.findField("GHKS");
					this.Field.setValue();
				}
				// this.Field.setValue();
				if (!this.chiswin) {
					var win = new Ext.Window({
						layout : "form",
						title : '请选择挂号科室',
						width : 330,
						height : 110,
						resizable : true,
						modal : true,
						constrainHeader : true,
						shim : true,
						// items:this.form,
						buttonAlign : 'center',
						closable : false,
						buttons : [{
							text : '确定',
							handler : function() {
								var text = this.Field.getValue();
								// 挂号科室信息
								this.chiswin.hide();
								this.MZXX.GHKS = text;
								var res = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicChargesProcessingService",
									serviceAction : "saveGhmx",
									body : this.MZXX
								});
								if (res.code > 300) {
									Ext.Msg.alert("提示", res.msg);
									return;
								}
								this.MZXX.GHGL = res.json.GHGL
								var form = this.form.getForm();
								var MZGL = form.findField("MZGL");
								var FPHM = form.findField("FPHM");
								var BRXZ = form.findField("BRXZ");
								var BRXM = form.findField("BRXM");
								var BRXB = form.findField("BRXB");
								var AGE = form.findField("AGE");
								if (this.MZXX.BRXZ) {
									BRXZ.setValue(this.MZXX.BRXZ);
								} else {
									BRXZ.setValue(6);
								}
								if (this.MZXX.AGE) {
									AGE.setValue(this.MZXX.AGE);
								}
								if (this.MZXX.JZKH) {
									MZGL.setValue(this.MZXX.JZKH);
								} else {
									MZGL.setValue();
								}
								if (this.initDataId) {
									FPHM.setValue(this.initDataId);
								}
								BRXB.setValue(this.MZXX.BRXB);
								BRXM.setValue(this.MZXX.BRXM);
								this.doCommit();
								this.form.getForm().findField("MZGL").disable();
								// if (typeof callback == "function") {
								// callback
								// .apply(this, [text])
								// }
							},
							scope : this
						}, {
							text : '取消',
							handler : function() {
								this.chiswin.hide();
							},
							scope : this
						}]
					})
					this.chiswin = win
					this.chiswin.add(this.reg_form);
				}
				this.chiswin.show();
				// var reg_form = this.reg_form.getForm()
				// this.Field = reg_form.findField("GHKS");
				this.Field.focus(false, 50);
			},
			doENTER : function(field,isDk,ybkxx) {
				this.opener.clearYbxx();
				// 判断启用模式 1卡号,2门诊号码
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
						// cardOrMZHM : data.cardOrMZHM
					});
				if (pdms.code > 300) {
					this.processReturnMsg(pdms.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!pdms.json.cardOrMZHM) {
						Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
						return;
					}
				}
				if (field.getValue() == '') {
					if (pdms.json.cardOrMZHM == 1) {
						this.showModule(pdms.json.cardOrMZHM);
					}
					if (pdms.json.cardOrMZHM == 2) {
						this.showModule(pdms.json.cardOrMZHM);
					}
					this.showModule();
				} else {
					// 去查询处方单
					this.doJZKHChange(field);
					if(isDk){
					this.opener.ybkxx=ybkxx;}
				}
			},
			showModule : function() {
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("phis.application.pix.script.EMPIInfoModule");
					m = new phis.application.pix.script.EMPIInfoModule({
								entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							});
					m.on("onEmpiReturn", this.checkRecordExist, this);
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.show();
			},
//			showModule : function(cardOrMZHM) {
//				var m = this.midiModules["healthRecordModule"];
//				if (!m) {
//					$import("phis.script.pix.EMPIInfoModule");
//					m = new phis.script.pix.EMPIInfoModule({
//								entryName : "MPI_DemographicInfo",
//								title : "个人基本信息查询",
//								height : 450,
//								modal : true,
//								mainApp : this.mainApp
//							});
//					m.on("onEmpiReturn", this.doJZKHChange, this);
//					this.midiModules["healthRecordModule"] = m;
//				}
//				var win = m.getWin();
//				win.setPosition(250, 100);
//				win.show();
//				var form = m.midiModules[m.entryName].form.getForm();
//				// 卡号
//				if (cardOrMZHM == 1) {
//					form.findField("MZHM").setDisabled(true);
//				}
//				// 门诊号码
//				if (cardOrMZHM == 2) {
//					form.findField("cardNo").setValue(form.findField("MZHM")
//							.getValue());
//					form.findField("personName").focus(true, 200);
//				}
//			},
			doCommit : function() {
				var from = this.form.getForm();
				var BRXM = from.findField("BRXM");
				var MZGL = from.findField("MZGL");
				var BRXZ = from.findField("BRXZ");
				var parentFrom = this.opener.formModule.form.getForm();
				var KSDM = from.findField("KSDM");
				parentFrom.findField("MZGL").setValue(MZGL.getValue());
				parentFrom.findField("BRXM").setValue(BRXM.getValue());
				parentFrom.findField("BRXZ").setValue(BRXZ.getValue());
				this.getWin().hide();
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryDJ",
							body : this.MZXX
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (r.json.count > 0) {
						this.opener.cfsb = [];
						this.opener.setCFD([]);
						this.opener.showCFD(this.MZXX);
					} else {
						this.opener.cfsb = [];
						this.opener.MZXX = this.MZXX;
						this.opener.setCFD([]);
					}
				}
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'disease',
							totalProperty : 'count',
							id : 'mdssearch_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'JBXH'
								}, {
									name : 'JBMC'

								}, {
									name : 'ICD10'

								}]);
			},
			setBackInfo : function(obj, record) {
				obj.collapse();
				if (obj.getName() == "ZD1") {
					this.ZD1 = record.get("ICD10");
				}
				if (obj.getName() == "ZD2") {
					this.ZD2 = record.get("ICD10");
				}
				if (obj.getName() == "ZD3") {
					this.ZD3 = record.get("ICD10");
				}
				obj.setValue(record.get("JBMC"));
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : "审批编号录入",
						width : 800,
						height : 300,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			}
		})