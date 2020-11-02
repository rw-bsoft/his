$package("phis.application.emr.script")

$import("phis.script.TableForm")
phis.application.emr.script.EMRTemperatureForm = function(cfg) {
	cfg.colCount = 2;
	cfg.showButtonOnTop = true;
	cfg.disAutoHeight = true;
	cfg.fldDefaultWidth = 163;
	phis.application.emr.script.EMRTemperatureForm.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRTemperatureForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				phis.application.emr.script.EMRTemperatureForm.superclass.initPanel
						.call(this, sc);
				var CJSJField = this.form.getForm().findField("CJSJ");
				CJSJField.on("focus", function() {
					var ryrq, cyrq;
					//zhaojian 2017-10-09 医生站打开病人体温单，无法获取到ZHY问题（this.opener.opener.info未定义）
					if (this.opener.opener.info == undefined) {
						ryrq = Date.parseDate(this.opener.opener.exContext.brxx.data.RYRQ,"Y-m-d");
						ryrq = Date.parseDate(this.opener.opener.exContext.brxx.data.RYRQ,"Y-m-d");
					} else {
						ryrq = Date.parseDate(this.opener.opener.info.CYRQ,"Y-m-d");
						ryrq = Date.parseDate(this.opener.opener.info.CYRQ,"Y-m-d");
					}
					if (!cyrq) {
						cyrq = new Date();
					}
					cyrq = cyrq.add(Date.DAY, 1)
					CJSJField.setMinValue(ryrq)
					CJSJField.setMaxValue(cyrq)
				}, this);

				var FCField = new Ext.form.Checkbox({
					name : 'FC',
					boxLabel : "复测:"
				})
				FCField.on("check", function(radio, chcked) {
					var FCSJField = this.form.getForm().findField("FCSJ");
					var CJSJField = this.form.getForm().findField("CJSJ");
					var XMXBField = this.form.getForm().findField("XMXB");
					chcked == true ? FCSJField.enable() : FCSJField.disable()
					chcked == true ? CJSJField.disable() : CJSJField.enable()
					chcked == true ? XMXBField.disable() : XMXBField.enable()
					if (!chcked)
						this.doNew();
					FCSJField.clearValue()

				}, this);
				var combo = this.cretateCombo();
				combo.on("beforequery", function(qe) {
					delete qe.combo.lastQuery
				});
				combo.on("select", function(qe, record, index) {
					var CJSJField = this.form.getForm().findField("CJSJ");
					var XMXBField = this.form.getForm().findField("XMXB");
					CJSJField.setValue(record.data.CJSJ);
					XMXBField.setValue(record.data.XMXB);
				}, this);
				combo.getStore().on("beforeload", function(s, options) {
					//zhaojian 2017-10-09 医生站打开病人体温单，无法获取到ZHY问题（this.opener.opener.info未定义）
					if (this.opener.opener.info == undefined) {
						options.params.zhy = this.opener.opener.exContext.brxx.data.ZYH;
					} else {
						options.params.zhy = this.opener.opener.info.ZYH;
					}
					//options.params.zhy = this.opener.opener.info.ZYH;
					var CJSJField = this.form.getForm().findField("CJSJ");
					options.params.date = Date.parseDate(CJSJField.value,
							CJSJField.format).format("Y-m-d");
					Ext.apply(this.comboJsonData, options.params);
				}, this)

				FCField.setWidth(85);
				var panel = new Ext.Container({
					layout : "hbox",
					colspan : 2,
					style : "padding-bottom:3px;",
					items : [FCField, combo]
				})
				this.form.insert(3, panel);
				this.form.insert(6, this.getAlias());
				var XMXBField = this.form.getForm().findField("XMXB");
				return this.form;
			},
			// 获取备注信息字段
			getAlias : function() {
				var label = new Ext.form.Label({
					text : "备注(选择):",
					width : 85
				});
				var combo = this.createField({
					id : "BZLX",
					dic : {
						id : "phis.dictionary.remarkType",
						autoLoad : true
					}
				})
				var text = this.createField({
					id : "BZXX"
				})
				text.width = 258;
				// this.form.getForm().add(text,combo);
				return new Ext.Container({
					layout : "hbox",
					colspan : 2,
					style : "padding-bottom:3px;",
					items : [label, combo, text]
				});
			},
			onReady : function() {
				phis.application.emr.script.EMRTemperatureForm.superclass.onReady
						.call(this);
				this.form.getForm().findField("FCSJ").disable()
				var d = Date.parseDate(Date.getServerDateTime(), 'Y-m-d H:i:s');
				// var hours = d.getHours();
				// this.form.getForm().findField("SJ").setValue(this
				// .setDefaultHours(hours));
				var cjsj = this.form.getForm().findField("CJSJ");
				cjsj.on("select", function(f, date) {
					// this.changeDate(date)
				}, this);
				cjsj.setValue(d);
				var sj = this.form.getForm().findField("SJ"); // 时间快捷输入
				sj.on("change", this.onCheck, this);

			},
			onCheck : function(radio, item) {
				if (!item)
					return;
				var cjsj = this.form.getForm().findField("CJSJ");
				var curCjsj = cjsj.getValue();
				var d = Date.parseDate(
						(curCjsj.split(" ")[0] + " " + item.inputValue),
						'Y-m-d H:i');
				cjsj.setValue(d);
				// this.changeDate(d);
			},
			changeDate : function(date) { // 时分转化中文
				var hours = date.format("H:i").split(":");
				var formatTime = (hours[0].substr(0, 1) > 1 ? this
						.parseTime(hours[0].substr(0, 1)) : "")
						+ (hours[0] >= 10 ? "十" : "")
						+ this.parseTime(hours[0].substr(1, 2))
						+ "时"
						+ (hours[1].substr(0, 1) > 1 ? this.parseTime(hours[1]
								.substr(0, 1)) : "")
						+ (hours[1] >= 10 ? "十" : "")
						+ this.parseTime(hours[1].substr(1, 2))
						+ (hours[1] == "00" ? "整" : "分")
				return formatTime;
			},
			parseTime : function(n) { // 只针对时间
				switch (n) {
					case "0" :
						return "";
					case "1" :
						return "一";
					case "2" :
						return "二";
					case "3" :
						return "三";
					case "4" :
						return "四";
					case "5" :
						return "五";
					case "6" :
						return "六";
					case "7" :
						return "七";
					case "8" :
						return "八";
					case "9" :
						return "九";
				}
			},
			doXz : function() {
				this.doNew();
				this.form.getForm().CJH = "";
				var d = Date.parseDate(Date.getServerDateTime(), 'Y-m-d H:i:s');
				// var hours = d.getHours();
				// this.form.getForm().findField("SJ").setValue(this
				// .setDefaultHours(hours));
				this.form.getForm().findField("CJSJ").setValue(d);
			},
			setDefaultHours : function(hours) {
				if (hours < 6) {
					return "02:00";
				} else if (hours < 10) {
					return "06:00"
				} else if (hours < 14) {
					return "10:00"
				} else if (hours < 18) {
					return "14:00"
				} else if (hours < 22) {
					return "18:00"
				} else if (hours < 24) {
					return "22:00"
				}
				return "10:00"
			},
			doCreate : function() {
				if (!this.validate()) {
					return;
				}
				var FCField = this.form.getForm().findField("FC");
				var FCSJField = this.form.getForm().findField("FCSJ");
				var TWField = this.form.getForm().findField("TW");
				if (FCField.getValue() == true
						&& Ext.isEmpty(FCSJField.getValue())) {
					MyMessageTip.msg("提示", "请选择复测时间!", true);
					return;
				}
				if (FCField.getValue() == true
						&& Ext.isEmpty(TWField.getValue())) {
					MyMessageTip.msg("提示", "请填写复测的体温!", true);
					return;
				}

				var body = {};
				body["BQ_TZXM"] = this.getFormData();
				body["BQ_TZXM"].DB = this.form.getForm().findField("DB")
						.getRawValue()
				// 格式化CJSJ 加上时间
				if (!FCField.getValue()) {
					// body["BQ_TZXM"].CJSJ = Date.parseDate(
					// body["BQ_TZXM"].CJSJ + " " + body["BQ_TZXM"].SJ,
					// "Y-m-d H:i").format('Y-m-d H:i:s');
				} else {
					body["BQ_TZXM"].FCSJ = FCSJField.getRawValue()
				}
				body.cjsj = body["BQ_TZXM"].CJSJ;
				body.xmxb = body["BQ_TZXM"].XMXB;
				if (!Ext.isEmpty(body["BQ_TZXM"].FC))
					body.FCGL = FCSJField.getValue();
				if (this.form.getForm().CJH) {
					body.CJH = this.form.getForm().CJH;
				}
				//zhaojian 2017-10-09 医生站打开病人体温单，无法获取到ZHY问题（this.opener.opener.info未定义）
				if (this.opener.opener.info == undefined) {
					body.zyh = this.opener.opener.exContext.brxx.data.ZYH;
				} else {
					body.zyh = this.opener.opener.info.ZYH;
				}
				//body.zyh = this.opener.opener.info.ZYH;
				body.sqxh = 300;
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "emrTemperatureService",
					serviceAction : "saveSMTZ",
					body : body
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					Ext.MessageBox.alert("信息", r.msg)
					return;
				} else {
					MyMessageTip.msg("提示", "保存成功", true);
					this.doXz();
					this.fireEvent("save", this);
				}
			},
			cretateCombo : function() {
				var remoteTpl = '<td>{[values.CJSJ.substr(5)]}&nbsp;&nbsp;{XMXB}</td>';
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr>' + remoteTpl + '<tr>', '</table>', '</div>',
						'</tpl>', {
							getHour : function(CJSJ) {
								return Date.parseDate(CJSJ, "Y-m-d H:i:s")
										.format("Y-m-d");

							}
						});

				var url = ClassLoader.serverAppUrl || "";
				this.comboJsonData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : "Temperature"
				}
				var proxy = new Ext.data.HttpProxy({
					url : url + '*.jsonRequest',
					method : 'POST',
					jsonData : this.comboJsonData
				});
				var mds_reader = new Ext.data.JsonReader({
					root : 'mds',
					totalProperty : 'count',
					id : 'mdssearch'
				}, [{
					name : 'CJH'
				}, {
					name : 'CJSJ'
				}, {
					name : 'XMXB'
				}])
				var mdsstore = new Ext.data.Store({
					proxy : proxy,
					reader : mds_reader
				});
				var combo = new Ext.form.ComboBox({
					fieldLabel : "复测时间",
					name : "FCSJ",
					valueField : "CJH",
					displayField : "CJSJ",
					editable : false,
					width : 166,
					triggerAction : "all",
					emptyText : '请选择',
					store : mdsstore,
					mode : 'remote',
					selectOnFocus : true,
					typeAhead : true,
					loadingText : '搜索中...',
					hideTrigger : false,
					tpl : resultTpl,
					lazyInit : true,
					itemSelector : 'div.search-item'
				});
				return combo
			}

		});