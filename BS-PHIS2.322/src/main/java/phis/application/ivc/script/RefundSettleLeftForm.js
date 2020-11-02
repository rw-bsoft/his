$package("phis.application.ivc.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.ivc.script.RefundSettleLeftForm = function(cfg) {
	cfg.colCount = 1;
	cfg.autoLoadData = false;
	cfg.height = 200;
	cfg.width = 500;
	phis.application.ivc.script.RefundSettleLeftForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.ivc.script.RefundSettleLeftForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					return this.form;
				}
				var form = new Ext.FormPanel({
					labelWidth : 85, // label settings here cascade
					frame : true,
					bodyStyle : 'padding:5px 5px 0',
					width : 280,
					height : 335,
					buttonAlign : 'center',
					items : [{
								xtype : 'fieldset',
								title : '退款情况',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 1,
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
								items : this.getItems('FPQK')
							}, {
								xtype : 'fieldset',
								title : '收款金额',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 1,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('SKJE')
							}]
				});
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
				this.form = form
				return form
			},
			onReady : function() {

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
					if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
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
			setData : function(data, MZXX){
				this.data = data;
				this.MZXX = MZXX;
			},
			afterShow : function() {
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "queryTFPayment",
					body : this.data,
					MZXX : this.MZXX
				});
				if (!r.json.body) {
					this.jsxx = '';
					this.showjs = '';
					return;
				} else {
					this.jsxx = r.json.body;
					this.showjs = r.json.showjs;
				}
				if (!this.jsxx) {
					Ext.Msg.alert("提示", "请先维护默认付款方式!", function() {
						this.doConcel();
							});
					return;
				}
				if (!this.jsxx.FPHM) {
					Ext.Msg.alert("提示", "请先维护发票号码!", function() {
						this.doConcel();
							});
					return;
				}
				if (!this.jsxx.lastFPHM) {
					Ext.Msg.alert("提示", "剩余发票已不够此次结算,请维护发票号码!", function() {
						this.doConcel();
							});
					return;
				}
				this.runing = false;
				this.MZXX.FKFS = this.jsxx.FKFS;
				this.MZXX.FPZS = this.jsxx.FPZS;
				var form = this.form.getForm();
//				var tfje = (parseFloat(this.MZXX.ZFJE) - parseFloat(this.jsxx.ZFJE)).toFixed(2);
				form.findField("TFJE").setValue(this.showjs.TFJE);
				form.findField("ZHTF").setValue("0.00");
				form.findField("YSK").setValue(this.showjs.YSK);
//				form.findField("FFFS").setValue(this.MZXX.FKFS);
				form.findField("FKMC").setValue(this.jsxx.FKMC);
				form.findField("JKJE").setValue("");
				form.findField("TZJE").setValue("");
				form.findField("JKJE").focus(false, 200);
			},
			focusFieldAfter : function(index, delay) {
				var form = this.form.getForm();
				var jkje = form.findField("JKJE");
				var ysk = form.findField("YSK");
				if (!jkje.getValue()) {
					jkje.setValue("0.00");
					form.findField("TZJE").setValue(-ysk.getValue());
				} else {
					if (parseFloat(jkje.getValue()) < parseFloat(ysk.getValue())) {
						jkje.setValue("0.00");
						form.findField("TZJE").setValue(-ysk.getValue());
					} else {
						var tzje = form.findField("TZJE");
						tzje.setValue(jkje.getValue() - ysk.getValue());
					}
				}
			}
		});