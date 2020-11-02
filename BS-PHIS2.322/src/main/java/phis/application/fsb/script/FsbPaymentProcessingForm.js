$package("phis.application.hos.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.fsb.script.FsbPaymentProcessingForm = function(cfg) {
	// cfg.colCount = 2;
	cfg.autoLoadData = false;
	cfg.labelWidth = 65;
	// cfg.showButtonOnTop = true;
	phis.application.fsb.script.FsbPaymentProcessingForm.superclass.constructor
			.apply(this, [cfg])
	this.showButtonOnTop = true;
}

Ext.extend(phis.application.fsb.script.FsbPaymentProcessingForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombiyined) {
						this.addPanelToWin();
					}
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
				table.items.push({
					xtype : "label",
					html : "<br/><div style='font-size:20px;font-weight:bold;text-align:center;letter-spacing:3px' >家床病人预缴医药费收据</div>"
				})
				table.items.push({
					xtype : "label",
					html : "<br/><div style='font-weight:bolder;text-align:center' >"
							+ this.mainApp.dept + "</div><br/><br/>"
				})
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
					if (it.id == 'SJHM') {
						f.style += 'color:red';
					}
					if (it.id == 'JKJE') {
						f.style = "font-size:16px;font-weight:bold;color:red;";
					} else if (it.id == 'JKHJ' || it.id == 'ZFHJ'
							|| it.id == 'SYHJ') {
						f.style += "font-size:16px;font-weight:bold;color:blue;";
					}
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
					autoScroll : true,
					// collapsible : false,
					// autoWidth : true,
					// autoHeight : true,
					floating : false
				}

				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					// cfg.width = this.width
					// cfg.height = this.height
				} else {
					// cfg.autoWidth = true
					// cfg.autoHeight = true
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
				var form = this.form.getForm();
				var JKFS = form.findField("JKFS");
				JKFS.on("select", this.JKFSselect, this)
				var JKRQ = form.findField("JKRQ");
				JKRQ.un("specialkey", this.onFieldSpecialkey, this)
				JKRQ.on("specialkey", function(JKRQ, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (JKRQ.getValue()) {
									this.doBeforeSave();
								}
							}
						}, this);
			},
			doBeforeSave : function() {
//				var res = phis.script.rmi.miniJsonRequestSync({
//							serviceId : "hospitalAdmissionService",
//							serviceAction : "getDateTime"
//						});
//				var code = res.code;
//				var msg = res.msg;
//				if (code >= 300) {
//					this.processReturnMsg(code, msg);
//					return;
//				}
				var dateTime = Date.getServerDateTime();
				var sysdate = new Date(dateTime.replace(/-/g, "/"));
				var form = this.form.getForm();
				var JKRQ = form.findField("JKRQ");
				if (form.findField("JKFS").getValue()
						&& form.findField("ZYHM").getValue()
						&& form.findField("JKJE").getValue()) {
					var RYRQStr = this.redata.RYRQ.replace(/-/g, "/");
					var str = JKRQ.getValue().replace(/-/g, "/");
					if ((new Date(RYRQStr) - new Date(str)) > 0) {
						Ext.Msg.alert("提示", "缴款日期不能早于入院日期(" + this.redata.RYRQ
										+ ")!", function() {
									JKRQ.focus(true, 100);
								}, this);
						return;
					}
					if ((new Date(str) - sysdate) > 0) {
						Ext.Msg.alert("提示", "缴款日期不能大于当前日期!", function() {
									JKRQ.focus(true, 100);
								}, this);
						return;
					}
					Ext.MessageBox.confirm('确认保存', '是否保存当前缴款记录', function(btn,
									text) {
								if (btn == "yes") {
									this.doSave();
								}
							}, this);
				}
			},
			JKFSselect : function(none) {
				if (this.fkfss) {
					for (var i = 0; i < this.fkfss.length; i++) {
						var form = this.form.getForm();
						var ZPHM = form.findField("ZPHM");
						if (this.fkfss[i].FKFS == none.value) {
							ZPHM.setDisabled(false);
							if (this.fkfss[i].HMCD) {
								ZPHM.maxLength = this.fkfss[i].HMCD;
							}
							return;
						} else {
							ZPHM.setDisabled(true);
						}
					}
				}

			},
			doEnter : function(field) {
				this.needQuery = true;
				if (this.loading) {
					return
				}
				var Data = {};
				Data.key = field.getName();
				Data.value = field.getValue();
				this.form.el.mask("正在加载数据...", "x-mask-loading")
				this.loading = true;
				phis.script.rmi.jsonRequest({
							serviceId : "fsbPaymentProcessingService",
							serviceAction : "queryBrxx",
							body : Data
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								Ext.Msg.alert("提示", msg, function() {
											this.opener.opener.tab
													.getItem("paymentprocessing")
													.setDisabled(true);
											this.opener.opener.tab.activate(1)
										}, this);
								return
							}
							if (json.body) {
								this.needQuery = false;
								this.doNew()
								if (json.msg) {
									Ext.Msg.alert("提示", json.msg, function() {
												this.opener.opener.tab
														.getItem("paymentprocessing")
														.setDisabled(true);
												this.opener.opener.tab
														.activate(1)
												if (this.redata) {
													json.body.JKFS = this.redata.JKFS;
												} else {
													this.loadData();
												}
												// json.body.JKRQ = new Date()
												// .format('Y-m-d H:i:s');
												this.redata = json.body
												this.initFormData(json.body)
												var form = this.form.getForm();
												form.findField("JKJE").focus(
														false, 300);
												this.fireEvent("loadData",
														json.body.ZYH);
											}, this);
									return;
								}
								if (this.redata) {
									if (!json.body.SJHM) {
										json.body.SJHM = this.redata.SJHM;
									}
									json.body.JKFS = this.redata.JKFS;
								} else {
									// alert(1)
									this.loadData();
								}
								// json.body.JKRQ = new Date()
								// .format('Y-m-d H:i:s');
								this.redata = json.body
								this.initFormData(json.body)
								var form = this.form.getForm();
								form.findField("JKJE").focus(false, 300);
								this.fireEvent("loadData", json.body.ZYH);
							} else {
								var msg = "";
								if (Data.key == 'ZYHM') {
									msg = '家床号码';
								} else {
									msg = '床位号';
								}
								Ext.Msg.alert("提示", "该" + msg + "不存在!",
										function() {
											this.doNew();
											this.loadData();
											field.focus(false, 300);
										}, this);
							}
						}, this)
			},
			initFormData : function(data) {
				if (data.JKHJ) {
					data.JKHJ = parseFloat(data.JKHJ).toFixed(2);
				} else {
					data.JKHJ = '0.00'
				}
				if (data.ZFHJ) {
					data.ZFHJ = parseFloat(data.ZFHJ).toFixed(2);
				} else {
					data.ZFHJ = '0.00'
				}
				if (data.SYHJ) {
					data.SYHJ = parseFloat(data.SYHJ).toFixed(2);
				} else {
					data.SYHJ = '0.00'
				}
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							f.setValue(v)
						}
						if (this.needQuery) {
							if (it.update == "true") {
								f.disable();
							}
						} else {
							if (it.update == "true") {
								f.setDisabled(false);
							}
						}
					}
				}
			},
			saveToServer : function(saveData) {
				if (!saveData.SJHM) {
					Ext.Msg.alert("提示", "请先维护收据号码!");
					return
				}
				if (!this.redata.ZYH) {
					Ext.Msg.alert("提示", "请先查询病人信息后再缴款!", function() {
								var form = this.form.getForm();
								var ZYHM = form.findField("ZYHM");
								ZYHM.focus(false, 300);
							}, this);
					return;
				}
				saveData.ZYH = this.redata.ZYH;
				saveData.ZYHM = this.redata.ZYHM;
//				saveData.BRCH = this.redata.BRCH;
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "fsbPaymentProcessingService",
							serviceAction : "savePayment",
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							var form = this.form.getForm();
							var ZYHM = form.findField("ZYHM");
							ZYHM.setValue(saveData.ZYHM);
							this.doEnter(ZYHM);
							this.doPrint(json.body.JKXH);
						}, this)// jsonRequest
			},
			doReSet : function() {
				this.loadData();
				this.fireEvent("reSet", this);
			},
			loadData : function() {
				this.needQuery = true;
				if (this.loading) {
					return
				}
				if (this.form.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "fsbPaymentProcessingService",
							serviceAction : "getReceiptNumber"
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								Ext.Msg.alert("提示", msg, function() {
											this.opener.opener.tab
													.getItem("paymentprocessing")
													.setDisabled(true);
											this.opener.opener.tab.activate(1)
										}, this);
								return
							}
							if (json.fkfss) {
								this.fkfss = json.fkfss;
							}
							if (json.body) {
								// json.body.JKRQ = new Date()
								// .format('Y-m-d H:i:s');
								this.redata = json.body;
								this.doNew();
								this.initFormData(json.body);
							} else {
								Ext.Msg.alert("提示", "请先维护收据号码后再缴款!",
										function() {
											this.opener.opener.tab
													.getItem("paymentprocessing")
													.setDisabled(true);
											this.opener.opener.tab.activate(1)
										}, this);
							}
						}, this)// jsonRequest
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}

						if (it.type == "date") { // ** add by yzh 20100919 **
							if (it.minValue)
								f.setMinValue(it.minValue)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex)
									.get('key'));
						}
					}
				}
				this.setKeyReadOnly(false)
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew")
				this.validate()
			},
			doPrint : function(jkxh) {
				var module = this.createModule("paymentprint", this.refPayment)
				var form = this.form.getForm();
				if (form) {
					module.jkxh = jkxh;
					module.initPanel();
					module.doPrint();
				} else {
					MyMessageTip.msg("提示", "打印失败：无效的缴款信息!", true);
				}
			}
		});