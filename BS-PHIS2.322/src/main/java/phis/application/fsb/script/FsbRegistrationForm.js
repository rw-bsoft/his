$package("phis.application.fsb.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout", "util.Vtype",
		"phis.script.widgets.DatetimeField")

phis.application.fsb.script.FsbRegistrationForm = function(cfg) {
	phis.application.fsb.script.FsbRegistrationForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FsbRegistrationForm,
		phis.script.SimpleForm, {
			onReady : function() {
				phis.application.fsb.script.FsbRegistrationForm.superclass.onReady
						.call(this);
				Ext.apply(this, this.loadSystemParams({
									privates : ['JCEDTS']
								}));
				var form = this.form.getForm();
				var mzhm = form.findField("MZHM");
				mzhm.on("specialkey", this.doMzhmEnter, this);
				var jcbh = form.findField("JCBH");
				jcbh.on("specialkey", this.doJCBHEnter, this);
				var JKFS = form.findField("JKFS");
				JKFS.on("select", this.JKFSselect, this)
				phis.script.rmi.jsonRequest({
							serviceId : "jczxManageService",
							serviceAction : "saveQueryJCHM"
						}, function(code, msg, json) {
							if (code > 300) {
								Ext.Msg.alert("提示", msg);
								return
							}
							if (json.body) {
								// 根据参数设置开始时间和结束时间
								this.doReset();//调用重置方法 
								this.initFormData(json.body)
								//mzhm.focus();
							}
							if (json.fkfss) {
								this.fkfss = json.fkfss;
							}
						}, this)
			},
			initPanel : function(sc) {
				this.form = new Ext.FormPanel({
					labelWidth : 66,
					frame : true,
					autoScroll : true,
					items : [{
						xtype : "label",
						html : "<br/><div style='font-size:25px;font-weight:bold;text-align:center;letter-spacing:20px' >家庭病床登记</div><br/>"
					}, {
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part1')
					}, {
						xtype : 'fieldset',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part2')
					}, {
						xtype : 'fieldset',
						title : '病人信息',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part3')
					}, {
						xtype : 'fieldset',
						title : '家床信息',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part4')
					}, {
						xtype : 'fieldset',
						title : '缴款信息',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 4,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part5')
					}],
					tbar : (this.tbar || []).concat(this.createButton())
				});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				this.form.on("afterrender", this.onReady, this)
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
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				var f1 = 112;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
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
					var it = items[i];
					var f = "";
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					if (it.mode == "remote") {
						f = this.createRemoteDicField(it);
						MyItems.push(f);
						continue
					}
					f = this.createField(it);
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					if (it.id == 'JCHM') {
						f.labelWidth = 80;
						f.style = 'color:red;background:none; border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;'
					} else {
						f.labelWidth = 60;
					}
					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			doAdd : function() {
				this.fsbApplicationForm = this.createModule(
						"fsbApplicationForm", this.refForm);
				this.fsbApplicationForm.opener = this;
				// this.fsbApplicationForm.on("save", this.onSave, this);
				var win = this.fsbApplicationForm.getWin();
				win.add(this.fsbApplicationForm.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.fsbApplicationForm.op = "create";
					this.fsbApplicationForm.doNew();
				}
			},
			doMzhmEnter : function(field, e) {
				if (!Ext.isIE) {
					e.stopEvent();
				}
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
						// cardOrMZHM : data.cardOrMZHM
					});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.cardOrMZHM) {
						Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
						f.focus(false, 100);
						return;
					}
				}
				var key = e.getKey()
				if (key == e.ENTER) {
					if (field.getValue() == '') {
						// this.needFocus = false;
						this.doReset();
						this.doAdd();
					} else {
						this.doQueryBrsq(field.getValue(), r.json.cardOrMZHM);
					}
				}
			},
			doQueryBrsq : function(value, cardOrMZHM) {
				if (this.loading) {
					return
				}
				var form = this.form.getForm();
				var data = {};
				this.form.el.mask("正在加载数据...", "x-mask-loading")
				this.loading = true;
				if (cardOrMZHM == 1) {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "jczxManageService",
								serviceAction : "selectBrsqByField",
								JZKH : value
							});
				} else if (cardOrMZHM == 2) {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "jczxManageService",
								serviceAction : "selectBrsqByField",
								cnds : ['eq', ['$', 'MZHM'],
										['l', value]]
							});
				}

				if (this.form && this.form.el) {
					this.form.el.unmask()
				}
				this.loading = false
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return;
				} else {
					if (!r.json.body) {
						if (r.json.message) {
							Ext.Msg.alert("提示", r.json.message, function() {
										field.focus(false, 100);
									});
							return;
						}
						Ext.Msg.alert("提示", "该号码不存在!", function() {
									field.focus(false, 100);
								});
						return;
					}
					this.doCommitImport(r.json.body);
				}
			},
			doJCBHEnter : function(field, e) {
				var key = e.getKey()
				if (key == e.ENTER) {
					if (field.getValue() == '') {
						this.doReset();
						this.doAdd();
					} else {

					}
				}
			},
			getBrsq : function(BRID) {
				if (BRID == null) {
					return;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "jczxManageService",
							serviceAction : "selectBrsqByField",
							cnds : ['eq', ['$', 'BRID'], ['l', BRID]]
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, "系统错误!");
					return null;
				} else {
					return r.json.body;
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
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				var obj = {
					BRID : this.BRXX.BRID,
					SQFS : this.BRXX.SQFS
				}
				Ext.apply(values, obj);
				Ext.apply(this.data, values);
				var text;
				var form = this.form.getForm();
				if (this.dateDiff(values.KSRQ, values.JSRQ) >= this.JCEDTS) {
					MyMessageTip.msg("提示", "家床天数超过额定值", true);
					return;
				}
				//'家床登记：结束日期早于开始时间也能保存成功
				if(values.KSRQ>values.JSRQ){
					MyMessageTip.msg("提示", "结束时间不能早于开始时间", true);
					return;
				}
				if (values.JKJE) {
					if (!values.JKFS) {
						Ext.Msg.alert("提示", "请选择缴款方式!", function() {
									form.findField("JKFS").focus(false, 100);
								}, this);
						return;
					}
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
					serviceId : "jczxManageService",
					serviceAction : "saveBrdj",
					body : values
						// RYRQ : text
					}, function(code, msg, json) {
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					MyMessageTip.msg("提示", "家床登记保存成功", true);
					this.doIsPrint(json.body.ZYH);
					if (json.JKXH) {
						this.doPrint(json.JKXH);
					}
				}, this)
			},
			doPrint : function(jkxh) {
				var module = this.createModule("fsbPaymentprint",
						this.refPayment)
				var form = this.form.getForm();
				if (form) {
					module.jkxh = jkxh;
					module.initPanel();
					module.doPrint();
				} else {
					MyMessageTip.msg("提示", "打印失败：无效的缴款信息!", true);
				}
			},
			doIsPrint : function(ZYH) { // 打印病案首页
				// Ext.Msg.show({
				// title : "提示",
				// msg : "是否打印病案首页?",
				// modal : true,
				// width : 300,
				// buttons : Ext.MessageBox.OKCANCEL,
				// multiline : false,
				// fn : function(btn, text) {
				// if (btn == "ok") {
				var module = this.createModule("fsbMediRecordPrint",
						this.refFsbMediRecordPrint)
				var form = this.form.getForm();
				// var ZYHM = form.findField("ZYHM").getValue();
				// var BAHM = form.findField("BAHM").getValue();
				// var GJ = form.findField("GJDM").getRawValue();
				// var MZ = form.findField("MZDM").getRawValue();
				if (ZYH == null) {
					MyMessageTip.msg("提示", "打印失败：无效的病人信息!", true);
					return;
				}
				module.ZYH = ZYH;
				// module.ZYHM = ZYHM;
				// module.BAHM = BAHM;
				// module.GJ = encodeURIComponent(GJ);
				// module.MZ = encodeURIComponent(MZ);
				module.initPanel();
				module.doPrint();
				this.doReset();
				// } else {
				// this.doReset();
				// return true;
				// }
				// },
				// scope : this
				// });

			},
			doReset : function() {
				// this.clearYbxx();// 清楚医保缓存
				// this.needQuery = true;
				var form = this.form.getForm();
				// form.findField("BAHM").enable();
				this.doNew();
				phis.script.rmi.jsonRequest({
							serviceId : "jczxManageService",
							serviceAction : "saveQueryJCHM"
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								Ext.Msg.alert("提示", msg, function() {
											// this.opener.closeCurrentTab();//
											// 关闭收费模块
										}, this);
								return
							}
							if (json.body) {
								json.body.RYRQ = new Date()
										.format('Y-m-d H:i:s');
								this.initFormData(json.body)
							}
						}, this)
			},
			doImport : function() {
				this.refImportList = this.createModule("refImportList",
						this.refImportList);
				this.refImportList.opener = this;
				this.refImportList.loadData();
				var win = this.refImportList.getWin();
				win.add(this.refImportList.initPanel());
				win.setWidth(920);
				win.setHeight(400);
				win.show();
				win.center()
			},
			doCommitImport : function(r) {
				var form = this.form.getForm();
				this.BRXX = r;
				var Y = new Date().getFullYear();
				var briY = this.BRXX.CSNY.substr(0, 4);

				var JCBH = form.findField("JCBH"); // 家床编号
				JCBH.setDisabled(true);
				var MZHM = form.findField("MZHM"); // 门诊号码
				MZHM.setDisabled(true);
				var BRXZ = form.findField("BRXZ"); // 病人性质
				var BRXM = form.findField("BRXM"); // 病人姓名
				var BRXB = form.findField("BRXB"); // 病人性别
				var BRNL = form.findField("RYNL"); // 病人年龄
				var SFZ = form.findField("SFZH"); // 身份证
				var DZ = form.findField("LXDZ"); // 地址
				var LXR = form.findField("LXRM"); // 联系人
				var LXGX = form.findField("LXGX"); // 与患关系
				var LXDH = form.findField("LXDH"); // 联系电话
				var JCZD = form.findField("JCZD"); // 建床诊断
				var ICD = form.findField("ICD10"); // ICD码
				var ZDRQ = form.findField("ZDRQ"); // 诊断日期
				var BQZY = form.findField("BQZY"); // 病情摘要
				var JCYJ = form.findField("JCYJ"); // 建床意见

				this.data.BRID = r.BRID;
				this.data.MZHM = r.MZHM ? r.MZHM : "";
				this.data.CSNY = r.CSNY;
				if (this.BRXX.JCBH) {
					JCBH.setValue(this.BRXX.JCBH); // 家床编号
				}
				MZHM.setValue(this.BRXX.MZHM != null ? this.BRXX.MZHM : ""); // 门诊号码
				BRXZ.setValue(this.BRXX.BRXZ != null ? this.BRXX.BRXZ : ""); // 病人性质
				BRXM.setValue(this.BRXX.BRXM != null ? this.BRXX.BRXM : ""); // 病人姓名
				BRXB.setValue(this.BRXX.BRXB != null ? this.BRXX.BRXB : ""); // 病人性别
				BRNL.setValue(Y - briY); // 病人年龄
				SFZ.setValue(this.BRXX.SFZH != null ? this.BRXX.SFZH : ""); // 身份证
				DZ.setValue(this.BRXX.LXDZ != null ? this.BRXX.LXDZ : ""); // 地址
				LXR.setValue(this.BRXX.LXR != null ? this.BRXX.LXR : ""); // 联系人
				LXGX.setValue(this.BRXX.YHGX != null ? this.BRXX.YHGX : ""); // 与患关系
				LXDH.setValue(this.BRXX.LXDH != null ? this.BRXX.LXDH : ""); // 联系电话
				JCZD.setValue(this.BRXX.JCZD != null ? this.BRXX.JCZD : ""); // 建床诊断
				ICD.setValue(this.BRXX.ICD10 != null ? this.BRXX.ICD10 : ""); // ICD码
				ZDRQ.setValue(this.BRXX.ZDRQ != null ? this.BRXX.ZDRQ.substr(0,
						10) : ""); // 诊断日期
				BQZY.setValue(this.BRXX.BQZY != null ? this.BRXX.BQZY : ""); // 病情摘要
				JCYJ.setValue(this.BRXX.JCYJ != null ? this.BRXX.JCYJ : ""); // 建床意见
			},
			doCreate : function() {
				var refCreateForm = this.createModule("createForm",
						this.refCreateForm);
				refCreateForm.opener = this;
				var win = refCreateForm.getWin();
				win.add(refCreateForm.initPanel());
				win.setWidth(920);
				win.setHeight(400);
				win.show();
				win.center()
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent,
					labelSeparator : ":"
				}
				if (it.hideLabel) {
					delete cfg.fieldLabel;
					cfg.hideLabel = true;
				}
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
					// add by liyl 2012-06-17 去掉输入字符串首位空格
					blur : function(e) {
						if (typeof(e.getValue()) == 'string') {
							e.setValue(e.getValue().trim())
						}
					},
					scope : this
				}
				if (it.inputType) {
					cfg.inputType = it.inputType
				}
				if (it.editable) {
					cfg.editable = (it.editable == "true") ? true : false
				}
				if (it['not-null'] == "1") {
					// cfg.allowBlank = false
					// cfg.invalidText = "必填字段"
					// cfg.regex = /(^\S+)/
					// cfg.regexText = "前面不能有空格字符"
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
				// add by yangl 增加readOnly属性
				if (it.readOnly) {
					cfg.readOnly = true
					// cfg.unselectable = "on";
					cfg.style = "background:#E6E6E6;cursor:default;";
					cfg.listeners.focus = function(f) {
						f.blur();
					}
				}
				if (it.fixed) {
					cfg.disabled = true
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true
				}
				if (it.evalOnServer && ac.canRead(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "update" && !ac.canUpdate(it.acValue)) {
					cfg.disabled = true
				}
				// add by yangl,modify simple code Generation methods
				if (it.codeType) {
					if (!this.CodeFieldSet)
						this.CodeFieldSet = [];
					this.CodeFieldSet.push([it.target, it.codeType, it.id]);
				}
				if (it.properties && it.properties.mode == "remote") {
					// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
					return this.createRemoteDicField(it);
				} else if (it.dic) {
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}

					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth
					if (it.dic.fields) {
						if (typeof(it.dic.fields) == 'string') {
							var fieldsArray = it.dic.fields.split(",")
							it.dic.fields = fieldsArray;
						}
					}
					var combox = this.createDicField(it.dic)
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}
				if (it.dic) {
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}
					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth
					var combox = this.createDicField(it.dic)
					this.changeFieldCfg(it, cfg);
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}
				if (it.length) {
					cfg.maxLength = it.length;
				}
				if (it.maxValue) {
					cfg.maxValue = it.maxValue;
				}
				// update by caijy for minValue=0时无效的BUG
				if (it.minValue || it.minValue == 0) {
					cfg.minValue = it.minValue;
				}
				if (it.xtype) {
					if (it.xtype == "htmleditor") {
						cfg.height = it.height || 200;
					}
					if (it.xtype == "textarea") {
						cfg.height = it.height || 65
					}
					if (it.xtype == "datefield"
							&& (it.type == "datetime" || it.type == "timestamp")) {
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
					}
					this.changeFieldCfg(it, cfg);
					return cfg;
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield";
						cfg.style = "color:#00AA00;font-weight:bold;text-align:right";
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
						if (it.maxValue && typeof it.maxValue == 'string'
								&& it.maxValue.length > 10) {
							cfg.maxValue = it.maxValue.substring(0, 10);
						}
						if (it.minValue && typeof it.minValue == 'string'
								&& it.minValue.length > 10) {
							cfg.minValue = it.minValue.substring(0, 10);
						}
						break;
					case 'datetime' :
						cfg.xtype = 'datetimefieldEx'
						cfg.emptyText = "请选择日期时间"
						cfg.format = 'Y-m-d H:i:s'
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = 300
						cfg.height = 180
						break;
				}
				this.changeFieldCfg(it, cfg);
				return cfg;
			},
			dateDiff : function(sDate1, sDate2) { // sDate1和sDate2是2006-12-18格式
				var aDate, oDate1, oDate2, iDays
				aDate = sDate1.split("-")
				oDate1 = new Date(aDate[0], aDate[1] - 1, aDate[2]) // 转换为12-18-2006格式
				aDate = sDate2.split("-")
				oDate2 = new Date(aDate[0], aDate[1] - 1, aDate[2])
				iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60
						/ 24) // 把相差的毫秒数转换为天数
				return iDays
			}

		});