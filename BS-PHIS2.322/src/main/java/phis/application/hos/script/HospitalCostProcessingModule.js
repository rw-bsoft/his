$package("phis.application.hos.script")

$import("phis.script.SimpleModule")

phis.application.hos.script.HospitalCostProcessingModule = function(cfg) {
	phis.application.hos.script.HospitalCostProcessingModule.superclass.constructor
			.apply(this, [cfg])
	this.on("choose", this.choose, this);
}

Ext.extend(phis.application.hos.script.HospitalCostProcessingModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'center',

										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'east',
										width : 400,
										items : this.getForm()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {
				if (this.initDataId) {
					var form = this.form.form.getForm();
					var field = form.findField("ZYHM");
					field.setValue(this.initDataId);
					this.getItemInfo(field);
					form.findField("ZYHM").setDisabled(true);
					form.findField("BRCH").setDisabled(true);
					this.initDataId = null;
				}
			},
			getList : function() {
				var module = this.createModule("refCostProcessingList",
						this.refList);

				this.list = module;
				this.list.requestData.cnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.ZYH'], ['i', 0]],
								['eq', ['$', 'a.JGID'],
										['s', this.mainApp['phisApp'].deptId]]],
						['eq', ['$', 'a.XMLX'], ['i', 4]]];
				this.list.requestData.pageNo = 1;
				module.opener = this;
				return module.initPanel();
			},
			getForm : function() {
				var module = this.createModule("refCostProcessingForm",
						this.refForm);
				module.on("reSet", this.listClear, this);
				// module.on("loadData", this.listLoadData, this);
				this.formModule = module.initPanel();
				var form = module.form.getForm();
				var ZYHM = form.findField("ZYHM")
				var BRCH = form.findField("BRCH")
				var FYRQ = form.findField("FYRQ")
				var FYMC = form.findField("FYMC")
				var FYSL = form.findField("FYSL")
				var FYDJ = form.findField("FYDJ")
				ZYHM.un("specialkey", module.onFieldSpecialkey, module)
				BRCH.un("specialkey", module.onFieldSpecialkey, module)
				FYRQ.un("specialkey", module.onFieldSpecialkey, module)
				// FYMC.un("specialkey", module.onFieldSpecialkey, module)
				FYSL.un("specialkey", module.onFieldSpecialkey, module)
				FYRQ.on("specialkey", this.doSpecialkey, this);
				BRCH.on("specialkey", this.doSpecialkey, this);
				ZYHM.on("specialkey", this.doSpecialkey, this);
				// FYMC.on("specialkey", this.doSpecialkey, this);
				FYSL.on("specialkey", this.doSpecialkey, this);
				FYDJ.on("specialkey", this.doSpecialkey, this);

				FYDJ.on("blur", this.onblur, this);

				FYSL.on("focus", this.doFireEvents, this);
				BRCH.on("focus", this.doFireEvents, this);
				ZYHM.on("focus", this.doFireEvents, this);
				FYMC.on("focus", this.doFireEvents, this);

				ZYHM.focus(false, 200);
				this.form = module;
				module.opener = this;
				return this.formModule;
			},
			doFireEvents : function(field) {
				field.on("change", this.onblur, this);
			},
			onblur : function(field) {
				var form = this.form.form.getForm();
				var name = field.getName();
				var value = field.getValue();
				if (name == "FYDJ") {
					if (value == 0) {
						field.regex = /[^0]/;
						field.regexText = "不能为0";
					}
					field.setValue(this.number_TO_stringFormat(value, 4));
				}
				if ((name == "ZYHM" || name == "BRCH") && value) {
					this.getItemInfo(field);
				}
				if (name == "FYRQ") {
					this.doVerification(field);
				}
				if (name == "FYMC" && this.FYLR.FYMC) {
					field.setValue(this.FYLR.FYMC);
					if (this.flag == 1) {
						// form.findField("FYSL").focus(true, 200);
						this.flag = 0;
					}
				}
//				if (name == "FYSL") {
//					field.setValue(this.number_TO_stringFormat(value, 2));
//					var FYSL = form.findField("FYSL").getValue();
//					if (FYSL && FYSL < 0) {
//						if (this.FYLR.FYMC) {
//							this.FYSL = FYSL;
//							this.queryRefundInfo();
//						} else {
//							form.findField("ZXKS").focus(true, 200);
//						}
//					} else {
//						if (!FYSL) {
//							Ext.Msg.alert("提示", "请填写费用数量!", function() {
//										form.findField("FYSL")
//												.focus(false, 100);
//									});
//							return;
//						}
//						if (this.FYLR_BACKUP && FYSL > 0) {
//							this.FYLR = this.clone(this.FYLR_BACKUP);
////							form.findField("FYDJ").setValue(this.FYLR.FYDJ);
//							form.findField("ZXKS").setValue(this.FYLR.ZXKS);
//							form.findField("YSGH").setValue(this.FYLR.YSGH);
//							form.findField("ZXKS").setDisabled(false);
//						}
//						if (form.findField("FYDJ").getValue() == 0) {
//							form.findField("FYDJ").setDisabled(false);
//							form.findField("FYDJ").focus(false, 100);
//						} else {
//							form.findField("ZXKS").focus(false, 100);
//						}
//					}
//				}
			},
			getRefundList : function(FYLR) {
				var module = this.createModule("refRefundsDetailsList",
						this.refRefundList);
				module.on('loadData', this.doAfterLoadData, this);
				this.refundList = module;
				var list = module.initPanel();
				module.serverParams.FYLR = this.FYLR;
				Ext.apply(module.requestData, module.serverParams);
				// module.getStore(module.items);
				module.refresh();
				module.opener = this;
				return list;
			},
			doSpecialkey : function(field, e) {
				var form = this.form.form.getForm();
				if (e.getKey() == Ext.EventObject.ENTER) { // 触发了listener后，如果按回车，执行相应的方法
					field.un("change", this.onblur, this);
					var name = field.getName();
					var value = field.getValue();
					if (name == "FYDJ" && value == 0) {
						field.regex = /[^0]/;
						field.regexText = "不能为0";
					}
					if (name == "ZYHM" && !value) {
						form.findField("BRCH").focus(false, 200);
					}
					if ((name == "ZYHM" || name == "BRCH") && value) {
						this.getItemInfo(field);
					}
					if (name == "FYRQ") {
						this.doVerification(field);
					}
					// if (name == "FYMC" && this.FYLR.FYMC) {
					// field.setValue(this.FYLR.FYMC);
					// if(this.flag == 1){
					// form.findField("FYSL").focus(true, 200);
					// this.flag = 0;
					// }
					// }
					if (name == "FYSL") {
						field.setValue(this.number_TO_stringFormat(value, 2));
						var FYSL = form.findField("FYSL").getValue();
						if (FYSL && FYSL < 0) {
							if (this.FYLR.FYMC) {
								this.FYSL = FYSL;
								this.queryRefundInfo();
							} else {
								form.findField("ZXKS").focus(true, 200);
							}
						} else {
							if (!FYSL) {
								Ext.Msg.alert("提示", "请填写费用数量!", function() {
											form.findField("FYSL").focus(false,
													100);
										});
								return;
							}
							if (this.FYLR_BACKUP && FYSL > 0) {
								this.FYLR = this.clone(this.FYLR_BACKUP);
//								form.findField("FYDJ").setValue(this.FYLR.FYDJ);
								form.findField("ZXKS").setValue(this.FYLR.ZXKS);
								form.findField("YSGH").setValue(this.FYLR.YSGH);
								form.findField("ZXKS").setDisabled(false);
							}
							if (form.findField("FYDJ").getValue() == 0) {
								form.findField("FYDJ").setDisabled(false);
								form.findField("FYDJ").focus(true, 100);
							} else {
								form.findField("ZXKS").focus(false, 100);
							}
						}
					}
					// this.KSList.cndField.focus();
				}
			},
			getItemInfo : function(field) {
				this.form.form.getForm().findField("FYMC").setValue("");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalCostProcessingService",
							serviceAction : "queryItemInfo",
							cndName : field.getName(),
							cndValue : field.getValue()
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						if (field.getName() == "ZYHM") {
							Ext.Msg.alert("提示", "该住院号码不存在!", function() {
										field.focus(false, 100);
										field.setValue("");
									});
							return;
						}
						if (field.getName() == "BRCH") {
							Ext.Msg.alert("提示", "该床位号码不存在或无病人信息!", function() {
										field.focus(false, 100);
										field.setValue("");
									});
							return;
						}
					}
					var datas = r.json.body;
					if (!datas.BRBQ) {
						Ext.Msg.alert("提示", "该病人无临床病区，请先分配床位！", function() {
									field.focus(false, 100);
									field.setValue("");
								});
						return;
					}

					this.brry_Info = datas;
					var form = this.form.form.getForm();

					form.findField("FYSL").setDisabled(true);
					form.findField("FYDJ").setDisabled(true);
					form.findField("ZXKS").setDisabled(true);
					form.findField("YSGH").setDisabled(true);
					form.findField("FYRQ").setDisabled(true);

					this.FYLR = {};
					form.findField("ZYHM").setValue(datas.ZYHM);
					form.findField("BRXM").setValue(datas.BRXM);
					form.findField("BRXB").setValue(datas.BRXB);
					form.findField("BRXZ").setValue(datas.BRXZ);
					form.findField("BRKS").setValue(datas.BRKS);
					form.findField("RYRQ").setValue(datas.RYRQ);
					if (datas.CYRQ) {
						form.findField("CYRQ").setValue(datas.CYRQ);
					} else {
						form.findField("CYRQ").setValue("");
					}
					if (datas.BRCH) {
						form.findField("BRCH").setValue(datas.BRCH);
					} else {
						form.findField("BRCH").setValue("");
					}
					if (datas.ZSYS) {
						form.findField("YSGH").setValue(datas.ZSYS);
						this.FYLR.YSGH = datas.ZSYS
					} else {
						form.findField("YSGH").setValue("");
					}
					form.findField("ZXKS").setValue(datas.BRKS);
					form.findField("FYMC").setDisabled(false);
					// form.findField("FYSL").setDisabled(false);
					// form.findField("FYDJ").setDisabled(false);
					// form.findField("ZXKS").setDisabled(false);
					// form.findField("YSGH").setDisabled(false);
					// form.findField("FYRQ").setDisabled(false);

					this.FYLR.ZYH = datas.ZYH;
					this.FYLR.BRXZ = datas.BRXZ;
					this.FYLR.BRKS = datas.BRKS;
					this.FYLR.BRBQ = datas.BRBQ;
					this.FYLR.FYBQ = datas.BRBQ;
					this.FYLR.ZYHM = datas.ZYHM;
					this.FYLR.BRXM = datas.BRXM;
					this.FYLR.BRXB = datas.BRXB;
					this.FYLR.BRCH = datas.BRCH;
					this.FYLR.JSCS = datas.JSCS;
					this.listLoadData(datas.ZYH);
					this.flag = 0;

					var fymc = form.findField("FYMC");
					fymc.focus(false, 500);
				}
			},
			listLoadData : function(zyh) {
				this.list.requestData.cnd = ['and',
						['eq', ['$', 'a.ZYH'], ['i', zyh]],
						['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['eq', ['$', 'a.XMLX'], ['i', 4]],
						['eq', ['$', 'a.JSCS'], ['i', 0]]];
				this.list.requestData.pageNo = 1;
				this.list.refresh();
			},
			listClear : function() {
				this.list.requestData.cnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.ZYH'], ['i', 0]],
								['eq', ['$', 'a.JGID'],
										['s', this.mainApp['phisApp'].deptId]]],
						['eq', ['$', 'a.XMLX'], ['i', 4]]];
				this.list.requestData.pageNo = 1;
				this.list.refresh();
			},
			doVerification : function() {
				if (!this.form.validate()) {
					return
				}
				
				var form = this.form.form.getForm();
				var ls_fyrq = form.findField("FYRQ").getValue();
				var ls_ryrq = form.findField("RYRQ").getValue(); // string 类型
				var cyrq = form.findField("CYRQ").getValue();
				var fydj = form.findField("FYDJ").getValue();
				var fysl = form.findField("FYSL").getValue();
				if (this.FYLR.isZT == 1) {
					if(fysl<0){
						MyMessageTip.msg("提示", "组套不能退费，请按明细项目进行退费!", true)
						return;
					}
				}
				var zxks = form.findField("ZXKS").getValue();
				var ysgh = form.findField("YSGH").getValue();
				var FYMC = form.findField("FYMC").getValue();;
				if (!this.XMXX) {
					Ext.Msg.alert("提示", "请先输入费用！", function() {
								form.findField("FYMC").focus(false, 100);
							});
					return;
				}
				var arr = ls_ryrq.format('Y-m-d H:i:s').split(/-| |:/);
				var ryrq = new Date(arr[0], arr[1] - 1, arr[2]).getTime();
				if (!ls_fyrq) {
					Ext.Msg.alert("提示", "请先输入费用日期！", function() {
								form.findField("FYRQ").focus(false, 100);
							});
					return;
				}
				var fyrq = ls_fyrq.getTime();
				// alert("fyrq: "+ ls_fyrq+ " ryrq:"+ new
				// Date(arr[0],arr[1]-1,arr[2]))
				// alert("fyrq: "+ fyrq+ " ryrq:"+ ryrq)
				if (fyrq < ryrq) {
					Ext.Msg.alert("提示", "费用日期不能小于入院日期！", function() {
								form.findField("FYRQ").focus(false, 100);
							});
					return;
				}
				var dqrq = new Date().getTime();
				// alert("fyrq: "+ fyrq+ " dqrq:"+ dqrq)
				if (fyrq > dqrq) {
					Ext.Msg.alert("提示", "费用日期不能大于当前日期！", function() {
								form.findField("FYRQ").focus(false, 100);
							});
					return;
				}
				if (!fyrq) {
					Ext.Msg.alert("提示", "请填写费用日期！", function() {
								form.findField("FYRQ").focus(false, 100);
							});
					return;
				}
				if (cyrq && fyrq > cyrq) {
					Ext.Msg.alert("提示", "费用日期不能大于出院日期！", function() {
								form.findField("FYRQ").focus(false, 100);
							});
					return;
				}
				if (fysl == 0) {
					Ext.Msg.alert("提示", "费用数量不能为零！", function() {
								form.findField("FYSL").focus(false, 100);
							});
					return;
				}
				if (!fydj > 0) {
					Ext.Msg.alert("提示", "费用单价不能小于等于零！", function() {
								form.findField("FYDJ").focus(false, 100);
							});
					return;
				}
				var zjje = fysl * fydj;
				if (zjje > 9999999999.99) {
					Ext.Msg.alert("提示", "费用总额超过最高限定总额!", function() {
								form.findField("FYSL").focus(false, 100);
							});
					return;
				}
				if (zxks == null || zxks == "" || zxks == undefined) {
					Ext.Msg.alert("提示", "执行科室不能为空！", function() {
								form.findField("ZXKS").focus(false, 100);
							});
					return;
				}
				this.FYLR.FYSL = fysl;
				if (fysl > 0) {
					// if(!this.FYLR.FYKS){
					// this.FYLR.FYKS = form.findField("BRKS").getValue();
					// }
					this.FYLR.ZXKS = zxks;
					this.FYLR.FYDJ = fydj;
					this.FYLR.FYKS = form.findField("BRKS").getValue();
				}
				this.FYLR.YSGH = ysgh;
				this.FYLR.FYRQ = ls_fyrq;
				// this.FYLR.ZJJE = fydj*fysl;
				this.FYLR.XMLX = 4;
				Ext.MessageBox.confirm('确认保存', '是否保存当前费用记录',
						function(btn, text) {
							if (btn == "yes") {
								this.doSave();
							}
						}, this);
			},
			doSave : function() {
				var form = this.form.form.getForm();
				this.saving = true
				this.panel.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalCostProcessingService",
							serviceAction : "saveCost",
							body : this.FYLR
						}, function(code, msg, json) {
							this.panel.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (json.isCannotFindRecord == 1) {
								Ext.Msg.alert("提示",
										"未找到该费用正的记录!请确定费用名称和产地的正确性！");
								return;
							}
							if (json.isNotEnough == 1) {
								Ext.Msg.alert("提示",
										"费用的数量小于退回数量,请确定费用名称和产地的正确性");
								return;
							}
							this.XMXX = "";
							var form = this.form.form.getForm();
							var FYMC = form.findField("FYMC");
							var FYDJ = form.findField("FYDJ");
							form.findField("FYSL").setValue("1.00")
							form.findField("FYRQ").setValue(new Date())
							FYMC.setValue("");
							FYDJ.setValue("0.0000");
							FYMC.focus(false, 200);

							form.findField("FYSL").setDisabled(true);
							form.findField("ZXKS").setDisabled(true);
							form.findField("YSGH").setDisabled(true);
							form.findField("FYRQ").setDisabled(true);
							this.FYLR.FYMC = "";
							var field = form.findField("ZYHM");
							field.setValue(this.FYLR.ZYHM);
							this.list.refresh();
							this.getItemInfo(field);
						}, this)// jsonRequest
			},
			queryRefundInfo : function(field) {
				if (this.FYLR.isZT == 1) {
					MyMessageTip.msg("提示", "组套不能退费，请按明细项目进行退费!", true)
					return;
				}
				this.choiceItems(this.FYLR);
				// var form = this.form.form.getForm();
				// this.query = true
				// this.panel.el.mask("正在查询数据...", "x-mask-loading")
				// phis.script.rmi.jsonRequest({
				// serviceId : "hospitalCostProcessingService",
				// serviceAction : "queryRefundInfo",
				// body : this.FYLR
				// }, function(code, msg, json) {
				// this.panel.el.unmask()
				// this.query = false
				// if (code > 300) {
				// this.processReturnMsg(code, msg);
				// return
				// }
				// var datas = json.body;
				// if ((datas.length == 0)) {
				// Ext.Msg.alert("提示",
				// "未找到该费用正的记录!请确定费用名称和产地的正确性！",
				// function() {
				// form.findField("FYMC").setValue("");
				// form.findField("FYMC").focus(true,
				// 100);
				// });
				// return;
				// }
				// if ((datas.length == 1)) {
				// if (datas[0].FYSL < (0 - this.FYSL)) {
				// Ext.Msg.alert("提示", "退费数量大于计费数量！",
				// function() {
				// this.form.form.getForm()
				// .findField("FYSL")
				// .focus(true, 100);
				// });
				// return;
				// } else {
				// this.form.form.getForm().findField("FYDJ")
				// .setValue(datas[0].FYDJ);
				// var TFBL = this.FYSL / datas[0].FYSL;
				// this.FYLR.ZJJE = datas[0].ZJJE * TFBL;
				// this.FYLR.ZFJE = datas[0].ZFJE * TFBL;
				// this.FYLR.ZLJE = datas[0].ZLJE * TFBL;
				// this.form.form.getForm().findField("ZXKS")
				// .focus(false, 100);
				// }
				// return;
				// } else {
				// this.choiceItems(this.FYLR);
				//
				// }
				// }, this)// jsonRequest
			},
			choose : function(recored) {
				var form = this.form.form.getForm();
				if (!recored) {
					Ext.Msg.alert("提示", "未找到该费用正的记录!请确定费用名称和产地的正确性！",
							function() {
								form.findField("FYMC").setValue("");
								form.findField("FYSL").setValue("1.00");
								form.findField("FYDJ").setValue("0.0000");

								form.findField("FYMC").focus(true, 100);

								form.findField("FYSL").setDisabled(true);
								form.findField("FYDJ").setDisabled(true);
								form.findField("ZXKS").setDisabled(true);
								form.findField("YSGH").setDisabled(true);
								form.findField("FYRQ").setDisabled(true);
							});
					this.FYLR.FYMC = "";
					this.XMXX = "";
					return;
				}
				if (recored.data.FYSL < (0 - this.FYSL)) {
					Ext.Msg.alert("提示", "退费数量大于计费数量！", function() {
								form.findField("FYSL").focus(true, 100);
							});
					return;
				} else {
					form.findField("FYDJ").setValue(this
							.number_TO_stringFormat(recored.data.FYDJ, 4));
					form.findField("ZFBL").setValue(this
							.number_TO_stringFormat(recored.data.ZFBL, 3));
					var TFBL = this.FYSL / recored.data.FYSL;
					this.FYLR.FYDJ = recored.data.FYDJ;
					this.FYLR.FYMC = recored.data.FYMC;
					this.FYLR.ZFBL = recored.data.ZFBL;
					this.FYLR.ZJJE = recored.data.ZJJE * TFBL;
					this.FYLR.ZFJE = recored.data.ZFJE * TFBL;
					this.FYLR.ZLJE = recored.data.ZLJE * TFBL;
					this.FYLR.YZXH = recored.data.YZXH;
					this.FYLR.ZXKS = recored.data.ZXKS;
					this.FYLR.ZXYS = recored.data.ZXYS;
					this.FYLR.FYKS = recored.data.FYKS;
					if (form.findField("FYDJ").getValue() == 0) {
						form.findField("FYDJ").setDisabled(false);
						form.findField("FYDJ").focus(false, 100);
					} else {
						form.findField("FYRQ").focus(false, 100);
					}
					form.findField("ZXKS").setDisabled(true);
					form.findField("FYDJ").setDisabled(true);
				}

			},
			choiceItems : function(FYLR) {
				if (this.runderwinopening) {
					// this.refundList.refresh();
					return;
				}
				var win = new Ext.Window({
							width : 695,
							height : 400,
							// closeAction : "hide",
							items : this.getRefundList(FYLR),
							title : "退费明细选择",
							modal : true
						});
				this.runderwin = win;

			},
			beforerunderclose : function() {
				var form = this.form.form.getForm();
				this.runderwin.hide();
				this.runderwinopening = false;
				form.findField("FYMC").setValue("");
				this.FYLR.FYMC = "";
				this.XMXX = "";
				form.findField("FYSL").setValue("1.00");
				form.findField("FYDJ").setValue("0.0000");
				form.findField("FYMC").focus(true, 200);

				form.findField("FYSL").setDisabled(true);
				form.findField("FYDJ").setDisabled(true);
				form.findField("ZXKS").setDisabled(true);
				form.findField("YSGH").setDisabled(true);
				form.findField("FYRQ").setDisabled(true);
				return false;
			},
			doAfterLoadData : function() {
				if (!this.refundList.store.getAt(0)) {
					this.choose();
				}
				if (this.refundList.store.getAt(1) == null
						&& this.refundList.store.getAt(0)) {
					this.choose(this.refundList.store.getAt(0));
				}
				if (this.refundList.store.getAt(1)) {
					this.runderwin.show();
					this.runderwinopening = true;
					this.runderwin.on("beforeclose", this.beforerunderclose,
							this);
				}
			},
			number_TO_stringFormat : function(strNumber, count) {// 保留count位小数，不足补零，四舍五入、最高支持8位
				var para = {
					"strNumber" : strNumber,
					"count" : count
				}
				if (isNaN(strNumber)) {
					return strNumber;
				} else if (strNumber.length == 0 || strNumber == 0) {
					strNumber = '0';
				}
				strNumber = strNumber + "";
				if (strNumber.indexOf(".") != -1) {
					if (strNumber.charAt(strNumber.length - 1) == ".") {
						return strNumber + "00000000".substring(0, count);
					}
					var str = strNumber.substring(strNumber.indexOf(".") + 1);
					if (str.length > 0 && str.length <= count) {
						str = str + "00000000".substring(0, count - str.length);
						return strNumber.substring(0, strNumber.indexOf(".")
										+ 1)
								+ str;
					} else if (str.length > count) {
						var rest = phis.script.rmi.miniJsonRequestSync({
									serviceId : "hospitalCostProcessingService",
									serviceAction : "numberFormat",
									body : para
								});
						return this.number_TO_stringFormat(rest.json.rest,
								count)
					}
				} else {
					return strNumber + "." + "00000000".substring(0, count);
				}

			},
			clone : function(obj) {
				var objClone;
				if (obj.constructor == Object) {
					objClone = new obj.constructor();
				} else {
					objClone = new obj.constructor(obj.valueOf());
				}
				for (var key in obj) {
					if (objClone[key] != obj[key]) {
						if (typeof(obj[key]) == 'object') {
							objClone[key] = this.clone(obj[key]);
						} else {
							objClone[key] = obj[key];
						}
					}
				}
				objClone.toString = obj.toString;
				objClone.valueOf = obj.valueOf;
				return objClone;
			}
		});