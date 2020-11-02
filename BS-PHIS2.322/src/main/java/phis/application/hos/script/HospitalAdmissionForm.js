$package("phis.application.hos.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout", "util.Vtype",
		"phis.script.widgets.DatetimeField","phis.application.yb.script.MedicareCommonMethod",
		"phis.script.util.FileIoUtil","phis.script.Phisinterface","phis.script.DatamatrixReader",
		"phis.application.pay.script.PayCommon")
var flag = false;
phis.application.hos.script.HospitalAdmissionForm = function(cfg) {
	cfg.remoteUrl = 'YBDisease';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{JBMC}</td></td>';
	cfg.minListWidth = 250;
	Ext.apply(this,phis.application.yb.script.MedicareCommonMethod);
	Ext.apply(this, phis.script.Phisinterface);
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.hos.script.HospitalAdmissionForm.superclass.constructor
			.apply(this, [cfg])
	this.needFocus = true;
	this.YBHM = null;
}

Ext.extend(phis.application.hos.script.HospitalAdmissionForm,
		phis.script.SimpleForm, {
			getSQStoS : function(id) {
				var form = this.form.getForm();
				var sqs = form.findField(id + "_SQS");
				var s = form.findField(id + "_S");
				s.setValue("");
				this.setSOnFocus(sqs, s);
				if (id != "JGDM") {
					var x = form.findField(id + "_X");
					x.setValue("");
				}
			},
			getStoX : function(id) {
				var form = this.form.getForm();
				var s = form.findField(id + "_S");
				var x = form.findField(id + "_X");
				x.setValue("");
				this.setXOnFocus(s, x);
			},
			setSOnFocus : function(superObj, obj) {
				obj.onFocus = function() {
					util.widgets.MyCombox.superclass.onFocus.call(this);
					// if (this.store.getCount() == 0 || this.mode == 'local') {
					if (superObj.getValue()) {
						var id = "phis.dictionary.City";
						var store = util.dictionary.SimpleDicFactory.getStore({
							id : id,
							filter : "['eq',['$','item.properties.superkey'],['s','"
									+ superObj.getValue() + "']]"
						});
						store.load();
						obj.bindStore(store);
						this.focusLoad = true;
					}
				};
			},
			setXOnFocus : function(superObj, obj) {
				obj.onFocus = function() {
					util.widgets.MyCombox.superclass.onFocus.call(this);
					// if (this.store.getCount() == 0 || this.mode == 'local') {
					if (superObj.getValue()) {
						var id = "phis.dictionary.County";
						var store = util.dictionary.SimpleDicFactory.getStore({
							id : id,
							filter : "['eq',['$','item.properties.superkey'],['s','"
									+ superObj.getValue() + "']]"
						});
						store.load();
						obj.bindStore(store);
						this.focusLoad = true;
					}
				};
			},
			clearSQStoS : function(id) {
				var form = this.form.getForm();
				var sqs = form.findField(id + "_SQS");
				var s = form.findField(id + "_S");
				if (!sqs.getValue()) {
					s.setValue("");
					if (id != "JGDM") {
						var x = form.findField(id + "_X");
						x.setValue("");
					}
				}
			},
			clearStoX : function(id) {
				var form = this.form.getForm();
				var s = form.findField(id + "_S");
				var x = form.findField(id + "_X");
				if (!s.getValue()) {
					x.setValue("");
				}
			},
			onReady : function() {
				phis.application.hos.script.HospitalAdmissionForm.superclass.onReady
						.call(this);
				this.needQuery = true;
				var form = this.form.getForm();
				var JKFS = form.findField("JKFS");
				JKFS.on("select", this.JKFSselect, this)
				var brks = form.findField("BRKS");
				brks.on("select", this.getBRCH, this)
				brks.on("blur", this.clearBRCH, this)
				var brch = form.findField("BRCH");
				brch.on("focus", function() {
							if (!brks.getValue()) {
								MyMessageTip.msg("提示", "请先选择病人科室", true);
							}
						}, this)
				var csd_sqs = form.findField("CSD_SQS");
				var jgdm_sqs = form.findField("JGDM_SQS");
				var xzz_sqs = form.findField("XZZ_SQS");
				var hkdz_sqs = form.findField("HKDZ_SQS");
				csd_sqs.on("select", function() {this.getSQStoS("CSD")}, this)
				csd_sqs.on("blur", function() {this.clearSQStoS("CSD");}, this)
				jgdm_sqs.on("select", function() {this.getSQStoS("JGDM")}, this)
				jgdm_sqs.on("blur", function() {this.clearSQStoS("JGDM");}, this)
				xzz_sqs.on("select", function() {this.getSQStoS("XZZ")}, this)
				xzz_sqs.on("blur", function() {this.clearSQStoS("XZZ");}, this)
				hkdz_sqs.on("select", function() {
							this.getSQStoS("HKDZ")
						}, this)
				hkdz_sqs.on("blur", function() {
							this.clearSQStoS("HKDZ");
						}, this)
				var csd_s = form.findField("CSD_S");
				var xzz_s = form.findField("XZZ_S");
				var hkdz_s = form.findField("HKDZ_S");
				var jgdm_s = form.findField("JGDM_S");
				jgdm_s.on("focus", function() {
							if (!jgdm_sqs.getValue()) {
								MyMessageTip.msg("提示", "请先选择籍贯地址_省", true);
								return false;
							}
						})
				csd_s.on("select", function() {
							this.getStoX("CSD")
						}, this)
				csd_s.on("blur", function() {
							this.clearStoX("CSD");
						}, this)
				csd_s.on("focus", function() {
							if (!csd_sqs.getValue()) {
								MyMessageTip.msg("提示", "请先选择出生地_省", true);
								return false;
							}
						})
				xzz_s.on("select", function() {
							this.getStoX("XZZ")
						}, this)
				xzz_s.on("blur", function() {
							this.clearStoX("XZZ");
						}, this)
				xzz_s.on("focus", function() {
							if (!xzz_sqs.getValue()) {
								MyMessageTip.msg("提示", "请先选择现住址_省", true);
								return false;
							}
						})
				hkdz_s.on("select", function() {
							this.getStoX("HKDZ")
						}, this)
				hkdz_s.on("blur", function() {
							this.clearStoX("HKDZ");
						}, this)
				hkdz_s.on("focus", function() {
							if (!hkdz_sqs.getValue()) {
								MyMessageTip.msg("提示", "请先选择户口地址_省", true);
								return false;
							}
						})
				var csd_x = form.findField("CSD_X");
				var xzz_x = form.findField("XZZ_X");
				var hkdz_x = form.findField("HKDZ_X");
				csd_x.on("focus", function() {
							if (!csd_s.getValue()) {
								MyMessageTip.msg("提示", "请先选择出生地_市", true);
								return false;
							}
						})
				xzz_x.on("focus", function() {
							if (!xzz_s.getValue()) {
								MyMessageTip.msg("提示", "请先选择现住址_市", true);
								return false;
							}
						})
				hkdz_x.on("focus", function() {
							if (!hkdz_s.getValue()) {
								MyMessageTip.msg("提示", "请先选择户口地址_市", true);
								return false;
							}
						})
				var ybkh = form.findField("YBKH");
				ybkh.un("specialkey", this.onFieldSpecialkey, this)
				ybkh.on("specialkey", function(ybkh, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								this.doYbkhEnter(ybkh);
							}
						}, this)
				var bahm = form.findField("BAHM");
				bahm.un("specialkey", this.onFieldSpecialkey, this)
				bahm.on("specialkey", function(bahm, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								this.doBahmEnter(bahm);
							}
						}, this)
				/*****begin 挂号结算界面增加扫码支付 zhaojian 2019-11-12 *****/
				this.combo = form.findField("JKFS");
				this.combo.getStore().on('load', this.calYSKLoadEvent, this);
				var ewm = form.findField("ewm");
				ewm.on("focus", this.onEWMfocus, this);
				ewm.on("blur", this.onEWMblur, this);
				ewm.on("change", this.onEWMchange, this);
				/*****end 挂号结算界面增加扫码支付 zhaojian 2019-11-12 *****/
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalAdmissionService",
							serviceAction : "saveQueryZYHM"
						}, function(code, msg, json) {
							if (code > 300) {
								Ext.Msg.alert("提示", msg, function() {
											this.opener.closeCurrentTab();// 关闭收费模块
										}, this);
								return
							}
							if (json.body) {
								json.body.RYRQ = new Date()
										.format('Y-m-d H:i:s');
								this.initFormData(json.body)
							}
							if (json.fkfss) {
								this.fkfss = json.fkfss;
							}
						}, this)
				this.form.on("beforeclose", this.beforeclose, this);
			},
			JKFSselect : function(none) {
				this.form.getForm().findField("ewm").setValue("");
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
				if(this.combo.value==50){
					if(this.form.getForm().findField("JKJE").getValue()==""){
						this.form.getForm().findField("JKJE").focus(false, 300);
						return;
					}
					this.form.getForm().findField("ewm").focus(false, 100);
				}
			},
			// 回显付费方式事件
			calYSKLoadEvent : function(store, records) {
				var delcount = 0;
				//过滤付款方式下拉框选项，只保留 现金、扫码付
				store.each(function(item, index) {
					if(item.data.key!=6 && item.data.key!=50){
						store.removeAt(index+delcount);
						delcount --;
					}
				});
			},
			onEWMchange : function(field){
				if(field.getValue().length==18){
					if(this.combo.value != 50){
						Ext.Msg.alert("提示", "付款方式有误，请重新选择！",
								function() {
									field.setValue("");
									this.running = false;
								}, this);
						return;
					}
					QRCODE = field.getValue();
					this.form.el.unmask();
					this.running = false;
					this.doSave();
				}
			},
			onEWMfocus : function(){
				//判定付款方式是否为微信或支付宝
				if(this.combo.value==6){
					return;
				}
				this.form.el.mask("等待病人扫描付款码...", "x-mask-loading");	
				this.initHtmlElement();
			},
			onEWMblur : function(){
				if(this.combo.value==6){
					return;
				}
				this.form.el.unmask();
			},
			stopReadTask : function(){
				Ext.TaskMgr.stopAll();
			},
			initPanel : function(sc) {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalAdmissionService",
							serviceAction : "saveQueryZYHM"
						});
				if (r.code > 300) {
					if (r.msg == "NotLogon") {
						this.processReturnMsg(r.code, r.msg, this.initPanel)
					} else {
						MyMessageTip.msg("提示", r.msg, true);
					}
					return
				}
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}

				this.form = new Ext.FormPanel({
					labelWidth : 66, // label settings here cascade
					frame : true,
					autoScroll : true,
					items : [{
						xtype : "label",
						html : "<br/><div style='font-size:25px;font-weight:bold;text-align:center;letter-spacing:20px' >住院病人信息</div><br/>"
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
						title : '基本信息',
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
						title : '入院信息',
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
						items : this.getItems('part7')
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
						items : this.getItems('part8')
					}
					
					],
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
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					var f = "";
					if (it.mode == "remote") {
						f = this.createRemoteDicField(it);
						MyItems.push(f);
						continue
					}
					if (it.id == "BRCH") {
						var defaultWidth = this.fldDefaultWidth || 200
						var cfg = {
							name : it.id,
							fieldLabel : it.alias,
							xtype : it.xtype || "textfield",
							vtype : it.vtype,
							width : defaultWidth,
							value : it.defaultValue,
							enableKeyEvents : it.enableKeyEvents,
							validationEvent : it.validationEvent
						}
						cfg.listeners = {
							specialkey : this.onFieldSpecialkey,
							scope : this
						}
						if (it.inputType) {
							cfg.inputType = it.inputType
						}
						if (it.fixed || it.fixed) {
							cfg.disabled = true
						}
						this.BRCHStore = new Ext.data.JsonStore({
									fields : ['value', 'text'],
									data : []
								});
						var BRCHCombox = new Ext.form.ComboBox({
									store : this.BRCHStore,
									valueField : "value",
									displayField : "text",
									mode : 'local',
									triggerAction : 'all',
									emptyText : "请选择",
									selectOnFocus : true,
									// forceSelection : true,
									width : 150
								});
						Ext.apply(BRCHCombox, cfg)
						BRCHCombox.on("specialkey", this.onFieldSpecialkey,
								this)
						this.BRCHCombox = BRCHCombox;
						f = BRCHCombox;
					} else {
						f = this.createField(it)
					}
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width
					if (it.id == 'ZYHM' ||it.id == 'RYCS') {
						f.labelWidth = 80;
						f.style = 'color:red;background:none; border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;'
					} else {
						f.labelWidth = 60;
					}
					if (it.id == "ewm") {
						f.style = 'background:#E6E6E6;cursor:default;font-size:12px;color:gray;';
						f.emptyText = "请用鼠标点击此处激活扫码设备";
					}
					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				// alert(defaultWidth)
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent
				}
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
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
				if (it['not-null'] == '1') {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
				if (it['showRed']) {
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
				if (it.readOnly) {
					cfg.readOnly = true
					// cfg.unselectable = "on";
					cfg.style = "background:#E6E6E6;cursor:default;";
					cfg.listeners.focus = function(f) {
						f.blur();
					}
				}
				if (it.fixed || it.fixed) {
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
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}

				if (it.length) {
					cfg.maxLength = it.length;
				}

				if (it.xtype) {
					return cfg;
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						if (it.minValue) {
							cfg.minValue = it.minValue;
						} else {
							cfg.minValue = 0;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
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
						cfg.width = 700
						cfg.height = 450
						break;
				}
				return cfg;
			},
			doReset : function() {
				this.clearYbxx();
				this.needQuery = true;
				var form = this.form.getForm();
				form.findField("BAHM").enable();
				this.doNew();
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalAdmissionService",
							serviceAction : "saveQueryZYHM"
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								Ext.Msg.alert("提示", msg, function() {
											this.opener.closeCurrentTab();// 关闭收费模块
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
			doAdd : function(f, e, datas) {
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
				// var form = this.form.getForm();
				// var mzhm = form.findField("MZHM");
				if (!this.needQuery) {
					MyMessageTip.msg("提示", "请重置后再新建!", true);
					return;
				}
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("phis.application.pix.script.EMPIInfoModule");
					m = new phis.application.pix.script.EMPIInfoModule({
						/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
						//entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
						entryName : "phis.application.pix.schemas.MPI_DemographicInfo_SMQ",
						/*********************end*******************/
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
					m.on("onEmpiReturn", this.doWriteBrxx, this);
					this.midiModules["healthRecordModule"] = m;
				}
				if (datas != "" && datas != null) {
					m.brybxx = datas;
				} else {
					m.brybxx = null;
				}
				m.EMPIID = null;
				if(datas && datas.EMPIID!="undefined" && datas.EMPIID){
					m.EMPIID=datas.EMPIID;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
				//alert("222")
				var form = m.midiModules[m.entryName].form.getForm();
				// 卡号
				if (pdms.json.cardOrMZHM == 1) {
					form.findField("MZHM").setDisabled(true);
				}
				// 门诊号码
				if (pdms.json.cardOrMZHM == 2) {
					form.findField("cardNo").setValue(form.findField("MZHM")
							.getValue());
					form.findField("personName").focus(true, 200);
				}
				if (datas != "" && datas != null) {
					this.ybxx = datas;
//					m.on("saveCBXX", this.onSaveCBXX, this); //南京没有参保信息表
				}
			},
			onSaveCBXX : function(empiid) {
				if (this.ybxx && this.ybxx != null) {
					this.ybxx.EMPIID = empiid;
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicareService",
								serviceAction : "saveCBRYJBXX",
								body : this.ybxx
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return;
					} else {
						MyMessageTip.msg("提示", "保存成功!", true);
					}
				}
			},
			doWriteBrxx : function(data) {
				if(data.nhdk && data.nhdk=="1"){
					this.zynhdk="1";
				} 
				var form = this.form.getForm();
				var Data = {};
				Data.key = "MZHM";
				Data.value = data.MZHM;
				this.doQueryBrxx(Data);
			},
			doYbkhEnter : function(field, ybkxx) {
				if (field.getValue() == '') {
					this.needFocus = false;
					this.doReset();
					this.doAdd();
				} else {
					var Data = {};
					Data.key = field.getName();
					Data.value = field.getValue();
					this.doQueryBrxx(Data, ybkxx);
				}
			},
			doBahmEnter : function(field) {
				if (field.getValue() != '') {
					var Data = {};
					Data.key = field.getName();
					Data.value = field.getValue();
					this.doQueryBrxx(Data);
				}
			},
			doQueryBrxx : function(Data, ybkxx) {
				this.clearYbxx();// 清除医保缓存记录
				if (ybkxx && ybkxx != null) {
					this.ybkxx = ybkxx;
				}
				if (this.loading) {
					return
				}
				// var Data = {};
				// Data.key = field.getName();
				// Data.value = field.getValue();
				var message = "卡号";
				this.cxhm = {};
				if (Data.key == "BAHM") {
					this.cxhm.BAHM = Data.value;
					message = "病案号码";
				} else if (Data.key == "MZHM") {
					this.cxhm.MZHM = Data.value;
					message = "门诊号码";
				} else if (Data.key == "YBKH") {
					this.cxhm.YBKH = Data.value;
					message = "卡号";
				}
				var form = this.form.getForm();
				ZYHM = form.findField("ZYHM")
				BAHM = form.findField("BAHM")
				var data = {};
				data.BAHM = BAHM.getValue();
				data.ZYHM = ZYHM.getValue();
				this.form.el.mask("正在加载数据...", "x-mask-loading")
				this.loading = true;
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalAdmissionService",
							serviceAction : "queryBrxx",
							body : Data
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								if (code == 608) {
									Ext.Msg.confirm("请确认", msg, function(btn) {// 先提示是否删除
												if (btn == 'yes') {
													this.doImport();
												}
											}, this);
								} else {
									this.processReturnMsg(code, msg,
											this.loadData)
								}
								return
							}
							if (json.body) {
								if (json.body == 1) {
									var value = Data.value;
									if (json.BAHM) {
										value = json.BAHM;
									}
									Ext.Msg.alert("提示", "当前" + message + "("
													+ value
													+ ")病人已入院，不能重复入院，请修改!",
											function() {
												this.doReset();
											}, this);
									return;
								}else if(json.body == 2){
									Ext.Msg.alert("提示", "当前病人是家床病人，不能入院!",
											function() {
												this.doReset();
											}, this);
									return;
								}
								if (!json.body.ZYHM) {
									json.body.ZYHM = ZYHM.getValue();
								}
								if (!json.body.BAHM) {
									json.body.BAHM = BAHM.getValue();
								} else {
									this.cxhm.BAHM = json.body.BAHM
								}
								this.needQuery = false;
								var jkfs = form.findField('JKFS').getValue();
								this.doNew()
								if (json.body.CSNY) {
									json.body.CSNY = json.body.CSNY.substring(
											0, 10);
								}
								if (!json.body.JKFS) {
									json.body.JKFS = jkfs;
								}
								json.body.RYRQ = new Date()
										.format('Y-m-d H:i:s');
								json.body.YBHM = this.YBHM;//医保号码：对应莱斯医保住院序号XH
								this.initFormData(json.body)
								form.findField("BAHM").setDisabled(true);
								form.findField("BRKS").focus(false, 100);
								if (json.body.XXDM) {
									json.body.BRXX = json.body.XXDM
								} else {
									json.body.BRXX = 0;
								}
								this.data = json.body;
							} else {
								if (Data.key == 'BAHM') {
									MyMessageTip.msg("提示", "没有与当前病案号码匹配的病人信息!",
											true);
									form.findField("YBKH").focus(false, 100);
								} else if (Data.key == 'MZHM') {
									Ext.Msg.alert("提示", "对不起,该门诊号码不存在!",
											function() {
												this.doReset();
											}, this);
								} else if (Data.key == 'YBKH') {
									Ext.Msg.alert("提示", "对不起,当前卡号不存在!",
											function() {
												this.doReset();
												this.needFocus = false;
												this.doAdd();
											}, this);
								}
							}
						}, this)
			},
			saveToServer : function(saveData) {
				var str = saveData.RYRQ.replace(/-/g, "/");
				if ((new Date(str) - new Date()) > 0) {
					var this_ = this;
					Ext.Msg.alert("提示", "入院日期不能大于当前日期!", function() {
								var form = this_.form.getForm();
								RYRQ = form.findField("RYRQ")
								RYRQ.focus(true, 100);
							});
					return;
				}
				this.saveData = saveData;
				if (saveData.BRCH) {
					if (!this.dateForm) {
						var res = phis.script.rmi.miniJsonRequestSync({
									serviceId : "hospitalAdmissionService",
									serviceAction : "getDateTime"
								});
						var code = res.code;
						var msg = res.msg;
						if (code >= 300) {
							this.processReturnMsg(code, msg);
							return;
						}
						var dateTime = res.json.body;
						this.dateForm = new Ext.FormPanel({
									frame : true,
									labelAlign : 'right',
									defaultType : 'textfield',
									shadow : true,
									items : [new phis.script.widgets.DateTimeField(
											{
												id : 'inDateRYDJ',
												xtype : 'datetimefield',
												value : dateTime,
												width : 150,
												fieldLabel : '临床入院时间',
												allowBlank : false,
												altFormats : 'Y-m-d H:i:s',
												format : 'Y-m-d H:i:s'
											})]
								})
					}
					this.dateForm.getForm().findField("inDateRYDJ").on(
							"specialkey", function(f, e) {
								var key = e.getKey()
								if (key == e.ENTER) {
									this.saveRYDJ();
								}
							}, this)
					if (!this.chiswin) {
						this.chiswin = new Ext.Window({
									layout : "form",
									title : '临床入院时间确认',
									width : 300,
									resizable : true,
									iconCls : 'x-logon-win',
									shim : true,
									buttonAlign : 'center',
									closeAction : 'hide',
									modal : true,
									buttons : [{
												text : '确定',
												handler : this.saveRYDJ,
												scope : this
											}]
								})
						this.chiswin.add(this.dateForm);
						var res = phis.script.rmi.miniJsonRequestSync({
									serviceId : "hospitalAdmissionService",
									serviceAction : "getDateTime"
								});
						var code = res.code;
						var msg = res.msg;
						if (code >= 300) {
							this.processReturnMsg(code, msg);
							return;
						}
						var dateTime = res.json.body;
						this.dateForm.getForm().findField("inDateRYDJ")
								.setValue(dateTime)
						this.chiswin.on("hide", this.chiswinhide, this);
						this.chiswin.show();
					}
					this.dateForm.getForm().findField("inDateRYDJ").focus(
							false, 300);
				} else {
					this.saveRYDJ();
				}

			},
			chiswinhide : function() {

				if (this.chiswin) {
					this.chiswin.destroy();
					this.dateForm = null;
					this.chiswin = null;
				}
			},
			//保存入院信息
			saveRYDJ : function() {
				if (this.saving || this.runningTask) {
					return
				}
				
				if(this.zynhdk && this.zynhdk=="1"){
					this.data.zynhdk="1";
				}
				
				Ext.apply(this.data, this.saveData);
				this.saveData = this.data;
				var text;
				var form = this.form.getForm();
				if (this.saveData.JKJE) {
					if (!this.saveData.JKFS) {
						Ext.Msg.alert("提示", "请选择缴款方式!", function() {
									form.findField("JKFS").focus(false, 100);
								}, this);
						return;
					}
					/*****begin 入院登记预交款（扫码支付） zhaojian 2019-11-13 *****/
					if((this.combo.value==50) && (typeof(QRCODE)=='undefined' || QRCODE=="")){
						Ext.Msg.alert("提示", "未获取到付款码，请扫码支付！",
								function() {
									form.findField("ewm").setValue("");
									form.findField("ewm").focus(false,200);
									this.saving = false;
								}, this);
						return;
					}
					/*****end 入院登记预交款（扫码支付） zhaojian 2019-11-13 *****/	
				}
				if (this.saveData.BRCH) {
					var dateForm = this.dateForm.getForm();
					this.Field = dateForm.findField("inDateRYDJ");
					text = this.Field.getValue();
				} else {
					text = new Date();
					text = text.format('Y-m-d H:i:s')
				}
				Ext.apply(this.saveData, this.cxhm);
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						this.saveData)) {
					return;
				}			
				var brxz = this.form.getForm().findField("BRXZ").getValue();
				/**新医保*/
				if(brxz == '2000'){
					if(this.saveData.YBMC=="" || this.saveData.YBMC=="20"){
						MyMessageTip.msg("提示", "请选择医保病种!", true);
					}
					if( !this.ybxx){
						this.saving = false;
						MyMessageTip.msg("提示", "医保病人需要读卡", true);
						this.form.el.unmask();
						return;
					}
					var body = {}
					this.saveData.ZBBM = this.RYZD;
					body['DATA'] = this.saveData;
					body['YBXX'] = this.ybxx;
					var reg = "YLLB";
					body.USERID=this.mainApp.uid;
					//入院登记
					var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.NjjbService",
							serviceAction : "getywzqh",
							body:body
							});
					if (ret.code > 300) {
						MyMessageTip.msg("提示", "获取业务周期号失败", true);
						return;
					}
					// debugger;
					//撤销住院
					// var ywzqh=ret.json.YWZQH;
					// this.ybxx.NJJBLSH = "1869778";
					// this.ybxx.type = 2;
					// this.ybxx.JBBM = "";
					// this.ybxx.YSBM = "";
					// this.ybxx.YWZQH = ywzqh;
					// var oldybxx = this.ybxx;
					// //var oldNJJBLSHObj = (msg.split('、'));
					// //var oldNJJBLSH = oldNJJBLSHObj[2];
					// //oldNJJBLSH = oldNJJBLSH.slice(4);
					// // oldybxx.NJJBLSH = (msg.split('、'))[2].slice(4);
					// flag = true;
					// this.doNjjbqxrydj(oldybxx.YWZQH, oldybxx);
					//
					// drre=this.drinterfacebusinesshandle(str);
					// obj = drre.split('^');
					// int += 1;
					// if(int == 3){
					// 	this.form.el.unmask()
					// 	MyMessageTip.msg("提示", msg, true);
					// 	this.saving=false;
					// 	return
					// }
					// return;
					var ret1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.NjjbService",
							serviceAction : "getnjjblsh"
							});
					if (ret1.code > 300) {
						MyMessageTip.msg("提示", "获取流水号失败", true);
						return;
					}
					var ywzqh=ret.json.YWZQH;
					body['YBXX'].NJJBLSH = ret1.json.lsh.LSH;
					body['YBXX'].type = 2;
					body['YBXX'].JBBM = this.saveData.YBMC;
					body['YBXX'].YSBM = body['DATA'].SZYS;
					body['YBXX'].RYSJ=this.buildtimestr(new Date());
					body['YBXX'].BQMC="";
					if(this.saveData.ZSESHBZKH)
					body['YBXX'].ZSESHBZKH=this.saveData.ZSESHBZKH
					if(this.njjbyllb){
						body['YBXX'].YLLB=this.njjbyllb;
					}
					this.ybxx.NJJBLSH = ret1.json.lsh.LSH;
					this.ybxx.type = 2;
					this.ybxx.JBBM = this.saveData.YBMC;
					this.ybxx.YSBM = body['DATA'].SZYS;
					this.ybxx.YWZQH = ywzqh;
					this.addPKPHISOBJHtmlElement();			
					var str=this.buildstr("2210",ywzqh,body['YBXX']);
					//优化金保住院流水号获取方式,解决his流水号与医保平台不一致的问题--hj-2020-09-25
					debugger
					var datas = str.split('^');
					var data = datas[7].split('|');
					var lsh = data[0]+"";

					var drre=this.drinterfacebusinesshandle(str);
					var obj = drre.split('^');
					var int = 0;
					while(obj[0] == -2){
						var msg = obj[3]+"";
						if(msg.indexOf("在院状态下不允许医院就医") == -1){
							debugger;
							this.form.el.unmask()	
							MyMessageTip.msg("提示", obj[3], true);
							this.saving=false;
							return
						}else{
							debugger;
							var oldybxx = this.ybxx;
							//var oldNJJBLSHObj = (msg.split('、'));
							//var oldNJJBLSH = oldNJJBLSHObj[2];
							//oldNJJBLSH = oldNJJBLSH.slice(4);
							oldybxx.NJJBLSH = (msg.split('、'))[2].slice(4);
							flag = true;
							this.doNjjbqxrydj(oldybxx.YWZQH, oldybxx);
							drre=this.drinterfacebusinesshandle(str);
							obj = drre.split('^');
							int += 1;
							if(int == 3){
								this.form.el.unmask()	
								MyMessageTip.msg("提示", msg, true);
								this.saving=false;
								return
							}
						}
			
					}
					this.saveData.NJJBLSH = lsh;
					this.saveData.NJJBYLLB = body['YBXX'].YLLB;	
				}
//				this.form.el.mask("正在保存数据...", "x-mask-loading")
				var data={};
				if(this.form.getForm().findField("RYZD")==null||this.form.getForm().findField("RYZD").getValue()==''){
				}else{
					data.ZDXH=this.BRZD.json.JBXH;
					data.ZDLB=2;
					data.ZGQK=0;
					data.TXBZ=0;
					data.ZXLB=this.BRZD.json.ZXLB;
					data.ZDMC=this.BRZD.json.JBMC;
					data.ICD10=this.BRZD.json.ICD10;
					data.JBPB=this.BRZD.json.JBPB;
					this.saveData.ZYZD = this.BRZD.json.ICD10;
				}
				this.saving = true
				this.Text = text;
				/*****begin 入院登记预交款（扫码支付） zhaojian 2019-11-12 *****/	
				//微信或支付宝付款成功后再继续HIS中的结算流程
				if((this.combo.value==50) && typeof(QRCODE)!='undefined' && QRCODE!=""){
					this.saveData.JKFS = getFKFS(QRCODE,1);
					var paydata ={};
					paydata.PAYSERVICE = "3";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费，-3住院预交金退费'
					paydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP				
					paydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
					//paydata.IP = "192.168.9.71";//测试时使用
	 				//paydata.COMPUTERNAME = "DPSF01";//测试时使用
					paydata.HOSPNO = generateHospno("ZYYJK",this.saveData.SJHM);//医院流水号
					this.saveData.HOSPNO = paydata.HOSPNO;
					paydata.PAYMONEY = this.saveData.JKJE;//支付金额
					paydata.VOUCHERNO = this.saveData.SJHM;//就诊号码或发票号码
					paydata.ORGANIZATIONCODE = this.mainApp.deptId;//机构编码
					//paydata.ORGANIZATIONCODE = "320124003";//测试时使用
					paydata.PATIENTYPE = this.saveData.BRXZ;//病人性质
					paydata.PATIENTID = this.saveData.BRID;//病人id
					paydata.NAME = this.saveData.BRXM;//病人姓名
					paydata.SEX = this.saveData.BRXB;//性别
					paydata.IDCARD = this.saveData.SFZH;//身份证号
					paydata.BIRTHDAY = this.saveData.CSNY;//出生年月
					paydata.AUTH_CODE = QRCODE;//支付条码
					paydata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
					paydata.TERMINALNO = "";//支付终端号 如POS01、1号窗
					paydata.PAYNO = "";//支付账号
					paydata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
					paydata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名		
					//paydata.COLLECTFEESCODE = "0310581X";//测试时使用
					//paydata.COLLECTFEESNAME = "管理员";//测试时使用				
					paydata.STATUS = "0"//订单状态，0初始订单、1订单完成、2、订单关闭 3、订单失败	
					paydata.PAYTYPE = getPaytype(QRCODE);//1支付宝 2微信
					QRCODE="";
					SubmitOrder(paydata,this);
					return;
				}
				/*****end 入院登记预交款（扫码支付） zhaojian 2019-11-12 *****/	
				QRCODE="";
				this.doCommit2();
			},
			doCommit2: function(){
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalAdmissionService",
							serviceAction : "saveRYDJ",
							body : this.saveData,
							RYRQ : this.Text
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg);
								//如果医保成功,本地失败. 医保端取消登记
								if(brxz == '2000'){
									this.doNjjbqxrydj(this.ybxx.YWZQH, this.ybxx);
								}
								/*****begin 入院登记预交款（扫码支付退费） zhaojian 2019-11-12 *****/
								//如果本地失败，调用支付平台取消微信支付宝付款	
								if(this.combo.value==50){
									var refunddata ={};
									refunddata.PAYSERVICE = "-3";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费 -3住院预交金退费'
									refunddata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP							
									refunddata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称//
									//refunddata.IP = "192.168.9.71";//测试时使用
					 				//refunddata.COMPUTERNAME = "DPSF01";//测试时使用
									refunddata.HOSPNO = generateHospno("ZYYJKTF",this.saveData.SJHM);//医院流水号
									refunddata.PAYMONEY = this.saveData.JKJE;//支付金额
									refunddata.VOUCHERNO = this.saveData.SJHM;//就诊号码或发票号码
									refunddata.ORGANIZATIONCODE = this.mainApp.deptId;//机构编码
									//refunddata.ORGANIZATIONCODE = "320124003";//测试时使用
									refunddata.PATIENTYPE = this.saveData.BRXZ;//病人性质
									refunddata.PATIENTID = this.saveData.BRID;//病人id
									refunddata.NAME = this.saveData.BRXM;//病人姓名
									refunddata.SEX = this.saveData.BRXB;//性别
									refunddata.IDCARD = this.saveData.SFZH;//身份证号
									refunddata.BIRTHDAY = this.saveData.CSNY;//出生年月
									refunddata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
									//refunddata.TERMINALNO = "";//支付终端号 如POS01、1号窗
									refunddata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
									refunddata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名	
									//refunddata.COLLECTFEESCODE = "0310581X";//测试时使用
									//refunddata.COLLECTFEESNAME = "管理员";//测试时使用			
									refunddata.HOSPNO_ORG = this.saveData.HOSPNO;//退款交易时指向原HOSPNO
									refund(refunddata,this);
									return;
								}
								/*****end 入院登记预交款（扫码支付退费） zhaojian 2019-11-12 *****/
								return
							}
							if (this.chiswin) {
								this.chiswin.hide();
							}
							Ext.apply(this.data, this.saveData);
							MyMessageTip.msg("提示", "入院登记保存成功", true);
							this.doIsPrint(json.body.ZYH);
							this.zynhdk="";//重置农合读卡标记
							if (json.JKXH) {
								this.doPrint(json.JKXH);
							}
						}, this)// jsonRequest
			},
			doNjjbqxrydj : function(ywzqh,data){
				this.addPKPHISOBJHtmlElement();
				var str=this.buildstr("2240",ywzqh,data);
				var drre=this.drinterfacebusinesshandle(str);
				var obj = drre.split('^');
				if(obj[0] !="0"){
					MyMessageTip.msg("提示", obj[3], true);
					return;
				}else {
					if(!flag){
						MyMessageTip.msg("提示", "注销成功", true);
					}			
				}
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
			},
			getBRCH : function(none) {
				var body = {};
				body.KSDM = none.value;
				var form = this.form.getForm();
				var BRXB = form.findField("BRXB").getValue();
				body.BRXB = BRXB;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalAdmissionService",
							serviceAction : "getBRCH",
							body : body
						});
				var data = r.json.body;
				this.BRCHStore.loadData(data, false);
				this.BRCHCombox.setValue();
			},
			clearBRCH : function(none) {
				if (!none.value) {
					this.BRCHStore.loadData([], false);
					this.BRCHCombox.setValue();
				}
			},
			initFormData : function(data) {
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
							if (it.id == "JKFS" && f.getValue() != v) {
								f.counter = 1;
								this.setValueAgain(f, v, it);
							}
						}
						// alert(Ext.encode(it))
						// alert(it.id+":"+it.update)
						if (!this.needQuery) {
							// alert(it.updates == true)
							if (it.updates == "true") {
								f.setDisabled(false);
							}
						} else {
							if (it.updates == "true") {
								f.setDisabled(true);
							}
						}
					}
				}
				this.setKeyReadOnly(true)
				if (this.needFocus) {
					this.focusFieldAfter(-1, 200)
				} else {
					this.needFocus = true;
				}
				form.findField("ewm").setValue("");
			},
			beforeclose : function() {
				if (!this.needQuery) {
					Ext.MessageBox.confirm('提示', '当前病人还未保存,是否需要保存?', function(
									btn, text) {
								if (btn == "yes") {
									this.doSave();
								} else {
									this.chiswinhide()
									this.opener.closeCurrentTab();// 关闭收费模块
								}
							}, this);
					return false;
				}
				this.chiswinhide()
			},
			focusFieldAfter : function(index, delay) {
				var items = this.schema.items
				var form = this.form.getForm()
				for (var i = index + 1; i < items.length; i++) {
					var next = items[i]
					var id = next.id;
					if (id != "BAHM") {
						var field = form.findField(id)
						if (field && !field.disabled && !field.readOnly
								&& next.xtype != "imagefield") {// add
							// by
							// yangl
							// :跳过图片类型
							field.focus(false, 100)
							return;
						}
					}
				}
				var btns;
				if (this.showButtonOnTop) {
					btns = this.form.getTopToolbar().items
					if (btns) {
						var n = btns.getCount()
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i)
							if (btn.cmd == "save") {
								if (btn.rendered) {
									btn.focus()
								}
								return;
							}
						}
					}
				} else {
					btns = this.form.buttons;
					if (btns) {
						var n = btns.length
						for (var i = 0; i < n; i++) {
							var btn = btns[i]
							if (btn.cmd == "save") {
								if (btn.rendered) {
									btn.focus()
								}
								return;
							}
						}
					}
				}
			},
			doIsPrint : function(ZYH) { // 打印病案首页
				Ext.Msg.show({
							title : "提示",
							msg : "是否打印病案首页?",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var module = this.createModule(
											"hospMediRecordPrint",
											this.refHospMediRecordPrint)
									var form = this.form.getForm();
									var ZYHM = form.findField("ZYHM")
											.getValue();
									var BAHM = form.findField("BAHM")
											.getValue();
									var GJ = form.findField("GJDM")
											.getRawValue();
									var MZ = form.findField("MZDM")
											.getRawValue();
									if (ZYH == null) {
										MyMessageTip.msg("提示", "打印失败：无效的病人信息!",
												true);
										return;
									}
									module.ZYH = ZYH;
									module.ZYHM = ZYHM;
									module.BAHM = BAHM;
									module.GJ = encodeURIComponent(GJ);
									module.MZ = encodeURIComponent(MZ);
									module.initPanel();
									module.doPrint();
									this.doReset();
								} else {
									this.doReset();
									return true;
								}
							},
							scope : this
						});

			},
			// 保存
			doSave : function() {
				var from = this.form.getForm();
				var idcard = from.findField("SFZH").value;
				//下转，更新转诊类型
				util.rmi.jsonRequest({
					serviceId : "hai.hmsInterfaceService",
					serviceAction : "updateDownBizType",
					method : "execute",
	                body : {		                    
                    	"idcard":idcard,
                    	"bizType":"2"             
	                }
				});	
				//上转，更新转诊类型
				util.rmi.jsonRequest({
					serviceId : "hai.hmsInterfaceService",
					serviceAction : "updateUpBizType",
					method : "execute",
	                body : {		                    
                    	"idcard":idcard,
                    	"bizType":"2"             
	                }
				});	
				//下转，更新转诊状态
				debugger;
				var form = this.form.getForm();
				var brks = form.findField("BRKS").value;
				var szys = form.findField("SZYS").value;
				util.rmi.jsonRequest({
					serviceId : "hai.hmsInterfaceService",
					serviceAction : "updateDownStatus",
					method : "execute",
	                body : {		                    
                    	"idcard":idcard,
                    	"deptCode":brks,
                    	"doctorCode":szys,
                    	"status":"4"             
	                }
				});	
				//上转，更新转诊状态
				util.rmi.jsonRequest({
					serviceId : "hai.hmsInterfaceService",
					serviceAction : "updateUpStatus",
					method : "execute",
	                body : {		                    
                    	"idcard":idcard,
                    	"deptCode":brks,
                    	"doctorCode":szys,
                    	"status":"4"            
	                }
				});	
				phis.application.hos.script.HospitalAdmissionForm.superclass.doSave.call(this);
			},
			// 下面医保相关代码
			doYbdk : function() {
//				var ret = phis.script.rmi.miniJsonRequestSync({
//					serviceId : "yBService",
//					serviceAction : "queryXmlByPath"
//				});
//				if (ret.code > 300) {
//					this.processReturnMsg(ret.code, ret.msg);
//					return null;
//				}
				/*************以下是医保接口代码************/
//				var zydjxx = readXmlFile("c:\\njyb\\zydjxx.xml");
				var from = this.form.getForm();
//				if(zydjxx!=null && zydjxx!=""){
					try{
						/* DOMParser 对象解析 XML 文本并返回一个 XML Document 对象。
						* 要使用 DOMParser，使用不带参数的构造函数来实例化它，然后调用其 parseFromString() 方法
						* parseFromString(text, contentType) 参数text:要解析的 XML 标记 参数contentType文本的内容类型
						* 可能是 "text/xml" 、"application/xml" 或 "application/xhtml+xml" 中的一个。注意，不支持 "text/html"。
						*/
//						domParser = new DOMParser();
//						var xmlDoc = domParser.parseFromString(zydjxx, 'text/xml');
						var xmlDoc = loadXmlDoc("c:\\njyb\\zydjxx.xml");
						var elements = xmlDoc.getElementsByTagName("RECORD");
						var datas = {};
						for (var i = 0; i < elements.length; i++) {
							var tbr = (elements[i].getElementsByTagName("TBR")[0].firstChild)?(elements[i].getElementsByTagName("TBR")[0].firstChild.nodeValue):"";
							var xh = (elements[i].getElementsByTagName("XH")[0].firstChild)?(elements[i].getElementsByTagName("XH")[0].firstChild.nodeValue):"";
							var xm = (elements[i].getElementsByTagName("XM")[0].firstChild)?(elements[i].getElementsByTagName("XM")[0].firstChild.nodeValue):"";
							var xb = (elements[i].getElementsByTagName("XB")[0].firstChild)?(elements[i].getElementsByTagName("XB")[0].firstChild.nodeValue):"";
							var csny = (elements[i].getElementsByTagName("CSNY")[0].firstChild)?(elements[i].getElementsByTagName("CSNY")[0].firstChild.nodeValue):"";
							var sfzh = (elements[i].getElementsByTagName("SFZH")[0].firstChild)?(elements[i].getElementsByTagName("SFZH")[0].firstChild.nodeValue):"";
							var rysj = (elements[i].getElementsByTagName("RYSJ")[0].firstChild)?(elements[i].getElementsByTagName("RYSJ")[0].firstChild.nodeValue):"";
							var lxdh = (elements[i].getElementsByTagName("LXDH")[0].firstChild)?(elements[i].getElementsByTagName("LXDH")[0].firstChild.nodeValue):"";
							var ksm = (elements[i].getElementsByTagName("KSM")[0].firstChild)?(elements[i].getElementsByTagName("KSM")[0].firstChild.nodeValue):"";
							var zyh = (elements[i].getElementsByTagName("ZYH")[0].firstChild)?(elements[i].getElementsByTagName("ZYH")[0].firstChild.nodeValue):"";
							var cwh = (elements[i].getElementsByTagName("CWH")[0].firstChild)?(elements[i].getElementsByTagName("CWH")[0].firstChild.nodeValue):"";
							var ysm = (elements[i].getElementsByTagName("YSM")[0].firstChild)?(elements[i].getElementsByTagName("YSM")[0].firstChild.nodeValue):"";
							this.YBHM = xh;
							var m = this.midiModules["healthRecordModule"];
							if (!m) {
								$import("phis.application.pix.script.EMPIInfoModule");
								m = new phis.application.pix.script.EMPIInfoModule({
									/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
									//entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
									entryName : "phis.application.pix.schemas.MPI_DemographicInfo_SMQ",
									/*********************end*******************/
									title : "个人基本信息查询",
									height : 450,
									modal : true,
									mainApp : this.mainApp
								});
								m.on("onEmpiReturn", this.doWriteBrxx, this);
								this.midiModules["healthRecordModule"] = m;
							}
							datas.TBR=tbr;
							datas.XM=xm;
							datas.XB=xb=="男"?"1":"2";
							datas.CSNY=csny;
							datas.GRSFH=sfzh;
							datas.BRKS=ksm;
							datas.SZYS=ysm;
							datas.BRXZ=3000;
							m.brybxx = datas;
							var win = m.getWin();
							win.show();
							var form2 = m.midiModules[m.entryName].form.getForm();
							var cardNoField = form2.findField("cardNo");
							var mzhmField = form2.findField("MZHM");
							var personNameField = form2.findField("personName");
							var brxzField = form2.findField("BRXZ");
							cardNoField.setValue(tbr);
							mzhmField.setValue(tbr);
							personNameField.setValue(xm);
							brxzField.setValue({
								key : 3000,
								text : "市医保"
							});
							brxzField.disable();
						}
					}catch(e){
						MyMessageTip.msg("提示", "文件解析失败!", true);
					}
//				}
				/*************以下是医保接口代码结束************/
				
//				this.doYbrydjdk();
//				alert("医保功能完善中....");
//				return;
//				var ybModule = this.createModule("ybrydjModule",
//						"phis.application.yb.YB/YB/YB01");
//				ybModule.initPanel();
//				var win = ybModule.getWin();
//				win.show();
//				ybModule.on("qr", this.onQr_rydj, this);
//				ybModule.doNew();
//				this.ybModule = ybModule;
			},
			// 医保相关代码结束
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
				if (obj.getName() == "RYZD") {
					this.RYZD = record.get("ICD10");
				}
				obj.setValue(record.get("JBMC"));
			},
			doImport : function() {
				this.refImportList = this.createModule("refImportList",
						this.refImportList);
				this.refImportList.opener = this;
				this.refImportList.requestData.cnd = [
						'and',
						['like', ['$', 'JGID'],
								['s', this.mainApp['phisApp'].deptId + "%"]],
						['eq', ['$', 'JGBZ'], ['i', 0]]];
				this.refImportList.initCnd = [
						'and',
						['like', ['$', 'JGID'],
								['s', this.mainApp['phisApp'].deptId + "%"]],
						['eq', ['$', 'JGBZ'], ['i', 0]]];
				this.refImportList.loadData();
				// this.refImportList.on("save", this.onSave, this);
				// this.refImportList.on("winClose", this.onClose, this);
				var win = this.refImportList.getWin();
				win.add(this.refImportList.initPanel());
				win.setWidth(920);
				win.setHeight(400);
				win.show();
				win.center()
				if (!win.hidden) {
					// this.refImportList.op = "create";
					// this.refImportList.doNew();
				}
			},
			doCommitImport : function(r) {
				var cardNo = "";
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalAdmissionService",
							serviceAction : "queryJZKH",
							sbxh : r.SBXH
						});
				if (ret.json.cardNo) {
					cardNo = ret.json.cardNo;
				}
				if (ret.json.RYCS) {
					this.form.getForm().findField("RYCS").setValue(ret.json.RYCS);
				}
				if (new Date(r.YYRQ.replace(/-/g, "/")).format('Y-m-d') != new Date()
						.format('Y-m-d')) {
					Ext.Msg.confirm("请确认", "预约日期不是今天,确定入院登记吗?", function(btn) {// 先提示是否删除
								if (btn == 'yes') {
									if (r.BAHM) {
										this.form.getForm().findField("BAHM")
												.setValue(r.BAHM);
									}
									this.form.getForm().findField("YBKH")
											.setValue(cardNo);
									this.form.getForm().findField("BRXZ")
											.setValue(r.BRXZ ? r.BRXZ : "");
									this.form.getForm().findField("BRXM")
											.setValue(r.BRXM ? r.BRXM : "");
									this.form.getForm().findField("SFZH")
											.setValue(r.SFZH ? r.SFZH : "");
									this.form.getForm().findField("BRXB")
											.setValue(r.BRXB ? r.BRXB : "");
									this.form.getForm().findField("CSNY")
											.setValue(r.CSNY ? r.CSNY : "");
									this.form.getForm().findField("GJDM")
											.setValue(r.GJDM ? r.GJDM : "");
									this.form.getForm().findField("MZDM")
											.setValue(r.MZDM ? r.MZDM : "");
									this.form.getForm().findField("HYZK")
											.setValue(r.HYZK ? r.HYZK : "");
									this.form.getForm().findField("JTDH")
											.setValue(r.JTDH ? r.JTDH : "");
									this.form.getForm().findField("HKYB")
											.setValue(r.HKYB ? r.HKYB : "");
									this.form.getForm().findField("CSD_SQS")
											.setValue(r.CSD_SQS
													? r.CSD_SQS
													: "");
									this.form.getForm().findField("CSD_S")
											.setValue(r.CSD_S ? r.CSD_S : "");
									this.form.getForm().findField("CSD_X")
											.setValue(r.CSD_X ? r.CSD_X : "");
									this.form.getForm().findField("JGDM_SQS")
											.setValue(r.JGDM_SQS
													? r.JGDM_SQS
													: "");
									this.form.getForm().findField("JGDM_S")
											.setValue(r.JGDM_S ? r.JGDM_S : "");
									this.form.getForm().findField("ZYDM")
											.setValue(r.ZYDM ? r.ZYDM : "");
									this.form.getForm().findField("XZZ_SQS")
											.setValue(r.XZZ_SQS
													? r.XZZ_SQS
													: "");
									this.form.getForm().findField("XZZ_S")
											.setValue(r.XZZ_SQS
													? r.XZZ_SQS
													: "");
									this.form.getForm().findField("XZZ_X")
											.setValue(r.XZZ_X ? r.XZZ_X : "");
									this.form.getForm().findField("XZZ_QTDZ")
											.setValue(r.XZZ_QTDZ
													? r.XZZ_QTDZ
													: "");
									this.form.getForm().findField("XZZ_YB")
											.setValue(r.XZZ_YB ? r.XZZ_YB : "");
									this.form.getForm().findField("HKDZ_SQS")
											.setValue(r.HKDZ_SQS
													? r.HKDZ_SQS
													: "");
									this.form.getForm().findField("HKDZ_S")
											.setValue(r.HKDZ_S ? r.HKDZ_S : "");
									this.form.getForm().findField("HKDZ_X")
											.setValue(r.HKDZ_X ? r.HKDZ_X : "");
									this.form.getForm().findField("HKDZ_QTDZ")
											.setValue(r.HKDZ_QTDZ
													? r.HKDZ_QTDZ
													: "");
									this.form.getForm().findField("GZDW")
											.setValue(r.GZDW ? r.GZDW : "");
									this.form.getForm().findField("DWDH")
											.setValue(r.DWDH ? r.DWDH : "");
									this.form.getForm().findField("DWYB")
											.setValue(r.DWYB ? r.DWYB : "");
									this.form.getForm().findField("LXRM")
											.setValue(r.LXRM ? r.LXRM : "");
									this.form.getForm().findField("LXGX")
											.setValue(r.LXGX ? r.LXGX : "");
									this.form.getForm().findField("LXDH")
											.setValue(r.LXDH ? r.LXDH : "");
									this.form.getForm().findField("LXDZ")
											.setValue(r.LXDZ ? r.LXDZ : "");
									this.form.getForm().findField("BRKS")
											.setValue(r.YYKS ? r.YYKS : "");
									//add by lizhi 2017-11-22增加收治医生
									if(r.CZGH){
										this.form.getForm().findField("SZYS")
											.setValue(r.CZGH);
									}
									//add by lizhi 2017-12-12增加入院证带入病人床号
									if(r.BRCH){
										this.form.getForm().findField("BRCH")
											.setValue(r.BRCH ? r.BRCH : "");
									}
									this.data.BRID = r.BRID;
									this.data.MZHM = r.MZHM ? r.MZHM : "";
									this.data.DJRQ = r.DJRQ;
									if (r.CSNY && r.CSNY != "") {
										var Y = new Date().getFullYear();
										var briY = r.CSNY.substr(0, 4);
										this.form.getForm().findField("RYNL")
												.setValue(Y - briY);
									}

									this.form.getForm().findField("BRXZ")
											.enable();
									this.form.getForm().findField("BRXM")
											.enable();
									this.form.getForm().findField("GJDM")
											.enable();
									this.form.getForm().findField("MZDM")
											.enable();
									this.form.getForm().findField("HYZK")
											.enable();
									this.form.getForm().findField("JTDH")
											.enable();
									this.form.getForm().findField("CSD_SQS")
											.enable();
									this.form.getForm().findField("CSD_S")
											.enable();
									this.form.getForm().findField("CSD_X")
											.enable();
									this.form.getForm().findField("JGDM_SQS")
											.enable();
									this.form.getForm().findField("JGDM_S")
											.enable();
									this.form.getForm().findField("ZYDM")
											.enable();
									this.form.getForm().findField("XZZ_SQS")
											.enable();
									this.form.getForm().findField("XZZ_S")
											.enable();
									this.form.getForm().findField("XZZ_X")
											.enable();
									this.form.getForm().findField("XZZ_QTDZ")
											.enable();
									this.form.getForm().findField("XZZ_YB")
											.enable();
									this.form.getForm().findField("HKDZ_SQS")
											.enable();
									this.form.getForm().findField("HKDZ_S")
											.enable();
									this.form.getForm().findField("HKDZ_X")
											.enable();
									this.form.getForm().findField("HKDZ_QTDZ")
											.enable();
									this.form.getForm().findField("GZDW")
											.enable();
									this.form.getForm().findField("DWDH")
											.enable();
									this.form.getForm().findField("DWYB")
											.enable();
									this.form.getForm().findField("LXRM")
											.enable();
									this.form.getForm().findField("LXGX")
											.enable();
									this.form.getForm().findField("LXDH")
											.enable();
									this.form.getForm().findField("LXDZ")
											.enable();
									this.form.getForm().findField("BRKS")
											.enable();
									this.form.getForm().findField("BRCH")
											.enable();
									this.form.getForm().findField("RYRQ")
											.enable();
									this.form.getForm().findField("RYQK")
											.enable();
									this.form.getForm().findField("BRKS")
											.enable();
									this.form.getForm().findField("SZYS")
											.enable();
									this.form.getForm().findField("ZSYS")
											.enable();
									this.form.getForm().findField("YBMC")
											.enable();		
									this.refImportList.doCancel();
								}
							}, this);
					return
				}
				if (r.BAHM) {
					this.form.getForm().findField("BAHM").setValue(r.BAHM);
				}
				this.form.getForm().findField("YBKH").setValue(cardNo);
				this.form.getForm().findField("BRXZ").setValue(r.BRXZ
						? r.BRXZ
						: "");
				this.form.getForm().findField("BRXM").setValue(r.BRXM
						? r.BRXM
						: "");
				this.form.getForm().findField("SFZH").setValue(r.SFZH
						? r.SFZH
						: "");
				this.form.getForm().findField("BRXB").setValue(r.BRXB
						? r.BRXB
						: "");
				this.form.getForm().findField("CSNY").setValue(r.CSNY
						? r.CSNY
						: "");
				this.form.getForm().findField("GJDM").setValue(r.GJDM
						? r.GJDM
						: "");
				this.form.getForm().findField("MZDM").setValue(r.MZDM
						? r.MZDM
						: "");
				this.form.getForm().findField("HYZK").setValue(r.HYZK
						? r.HYZK
						: "");
				this.form.getForm().findField("JTDH").setValue(r.JTDH
						? r.JTDH
						: "");
				this.form.getForm().findField("HKYB").setValue(r.HKYB
						? r.HKYB
						: "");
				this.form.getForm().findField("CSD_SQS").setValue(r.CSD_SQS
						? r.CSD_SQS
						: "");
				this.form.getForm().findField("CSD_S").setValue(r.CSD_S
						? r.CSD_S
						: "");
				this.form.getForm().findField("CSD_X").setValue(r.CSD_X
						? r.CSD_X
						: "");
				this.form.getForm().findField("JGDM_SQS").setValue(r.JGDM_SQS
						? r.JGDM_SQS
						: "");
				this.form.getForm().findField("JGDM_S").setValue(r.JGDM_S
						? r.JGDM_S
						: "");
				this.form.getForm().findField("ZYDM").setValue(r.ZYDM
						? r.ZYDM
						: "");
				this.form.getForm().findField("XZZ_SQS").setValue(r.XZZ_SQS
						? r.XZZ_SQS
						: "");
				this.form.getForm().findField("XZZ_S").setValue(r.XZZ_SQS
						? r.XZZ_SQS
						: "");
				this.form.getForm().findField("XZZ_X").setValue(r.XZZ_X
						? r.XZZ_X
						: "");
				this.form.getForm().findField("XZZ_QTDZ").setValue(r.XZZ_QTDZ
						? r.XZZ_QTDZ
						: "");
				this.form.getForm().findField("XZZ_YB").setValue(r.XZZ_YB
						? r.XZZ_YB
						: "");
				this.form.getForm().findField("HKDZ_SQS").setValue(r.HKDZ_SQS
						? r.HKDZ_SQS
						: "");
				this.form.getForm().findField("HKDZ_S").setValue(r.HKDZ_S
						? r.HKDZ_S
						: "");
				this.form.getForm().findField("HKDZ_X").setValue(r.HKDZ_X
						? r.HKDZ_X
						: "");
				this.form.getForm().findField("HKDZ_QTDZ").setValue(r.HKDZ_QTDZ
						? r.HKDZ_QTDZ
						: "");
				this.form.getForm().findField("GZDW").setValue(r.GZDW
						? r.GZDW
						: "");
				this.form.getForm().findField("DWDH").setValue(r.DWDH
						? r.DWDH
						: "");
				this.form.getForm().findField("DWYB").setValue(r.DWYB
						? r.DWYB
						: "");
				this.form.getForm().findField("LXRM").setValue(r.LXRM
						? r.LXRM
						: "");
				this.form.getForm().findField("LXGX").setValue(r.LXGX
						? r.LXGX
						: "");
				this.form.getForm().findField("LXDH").setValue(r.LXDH
						? r.LXDH
						: "");
				this.form.getForm().findField("LXDZ").setValue(r.LXDZ
						? r.LXDZ
						: "");
				this.form.getForm().findField("BRKS").setValue(r.YYKS
						? r.YYKS
						: "");
				//add by lizhi 2017-11-22增加收治医生
				if(r.CZGH){
					this.form.getForm().findField("SZYS")
						.setValue(r.CZGH);
				}
				//add by lizhi 2017-12-12增加入院证带入病人床号
				if(r.BRCH){
					this.form.getForm().findField("BRCH")
						.setValue(r.BRCH ? r.BRCH : "");
				}
				this.data.BRID = r.BRID;
				this.data.MZHM = r.MZHM ? r.MZHM : "";
				this.data.DJRQ = r.DJRQ;
				this.initYBServer(r.BRXZ);  //初始化医保接口
				this.ybbhxx = r;	//病号信息
				if (r.CSNY && r.CSNY != "") {
					var Y = new Date().getFullYear();
					var briY = r.CSNY.substr(0, 4);
					this.form.getForm().findField("RYNL").setValue(Y - briY);
				}

				this.form.getForm().findField("BRXZ").enable();
				this.form.getForm().findField("BRXM").enable();
				this.form.getForm().findField("GJDM").enable();
				this.form.getForm().findField("MZDM").enable();
				this.form.getForm().findField("HYZK").enable();
				this.form.getForm().findField("JTDH").enable();
				this.form.getForm().findField("CSD_SQS").enable();
				this.form.getForm().findField("CSD_S").enable();
				this.form.getForm().findField("CSD_X").enable();
				this.form.getForm().findField("JGDM_SQS").enable();
				this.form.getForm().findField("JGDM_S").enable();
				this.form.getForm().findField("ZYDM").enable();
				this.form.getForm().findField("XZZ_SQS").enable();
				this.form.getForm().findField("XZZ_S").enable();
				this.form.getForm().findField("XZZ_X").enable();
				this.form.getForm().findField("XZZ_QTDZ").enable();
				this.form.getForm().findField("XZZ_YB").enable();
				this.form.getForm().findField("HKDZ_SQS").enable();
				this.form.getForm().findField("HKDZ_S").enable();
				this.form.getForm().findField("HKDZ_X").enable();
				this.form.getForm().findField("HKDZ_QTDZ").enable();
				this.form.getForm().findField("GZDW").enable();
				this.form.getForm().findField("DWDH").enable();
				this.form.getForm().findField("DWYB").enable();
				this.form.getForm().findField("LXRM").enable();
				this.form.getForm().findField("LXGX").enable();
				this.form.getForm().findField("LXDH").enable();
				this.form.getForm().findField("LXDZ").enable();
				this.form.getForm().findField("BRKS").enable();
				this.form.getForm().findField("BRCH").enable();
				this.form.getForm().findField("RYRQ").enable();
				this.form.getForm().findField("RYQK").enable();
				this.form.getForm().findField("BRKS").enable();
				this.form.getForm().findField("SZYS").enable();
				this.form.getForm().findField("ZSYS").enable();
				this.form.getForm().findField("YBMC").enable();	
				this.refImportList.doCancel();
			},
			//农合读卡
			doZynhdk : function() {
				this.doNhzydj();
			},
			//弹出刷卡窗口
			doNhzydj:function(){
				this.midiModules["nhzydjlist"]=null;
				var nhzydjmodule =this.createModule("nhzydjlist", "phis.application.hos.HOS/HOS/HOS0302");
					nhzydjmodule.on("zynhdkreturn", this.doWriteBrxx, this);
				var win = nhzydjmodule.getWin();
				win.add(nhzydjmodule.initPanel());
				win.show();
			},
			doNjjb : function() {
								this.addPKPHISOBJHtmlElement();
				this.drinterfaceinit();
				//先执行读卡程序
				var body={};
				body.USERID=this.mainApp.uid;
				//获取业务周期号
				var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getywzqh",
						body:body
						});
				if (ret.code <= 300) {
					var ywzqh=ret.json.YWZQH;
					var str=this.buildstr("2100",ywzqh,"");
					var drre=this.drinterfacebusinesshandle(str);
					var arr=drre.split("^");
					if(arr[0]=="0"){
						var canshu=arr[2].split("|")
						var data={};
						data.SHBZKH=canshu[0];//社会保障卡号
						data.DWBH=canshu[1];//单位编号
						data.DWMC=canshu[2];//单位名称
						data.SFZH=canshu[3];//身份证号
						data.XM=canshu[4];//姓名
						data.XB=canshu[5];//性别
						data.YLRYLB=canshu[6];//医疗人员类别
						data.YDRYBZ=canshu[7];//异地人员标志
						data.TCQH=canshu[8];//统筹区号
						data.DQZHYE=canshu[9];//当前帐户余额
						data.ZYZT=canshu[10];//在院状态
						data.BNZYCS=canshu[11];//本年住院次数
						data.DYXSBZ=canshu[12];//待遇享受标志
						data.DYBXSYY=canshu[13];//待遇不享受原因
						data.BZDJQK=canshu[14];//病种登记情况
						data.YBMMZG=canshu[15];//医保门慢资格
						data.YBMMBZ=canshu[16];//医保门慢病种
						data.YBMJZG=canshu[17];//医保门精资格
						data.YBMJBZ=canshu[18];//医保门精病种
						data.YBMAZG=canshu[19];//医保门艾资格
						data.YBMABZ=canshu[20];//医保门艾病种
						data.YBBGGRSZG=canshu[21];//医保丙肝干扰素资格
						data.YBBGGRSBZ=canshu[22];//医保丙肝干扰素病种
						data.YBMZXYBZG=canshu[23];//医保门诊血友病资格
						data.YBMZXYBBZ=canshu[24];//医保门诊血友病病种
						data.YBMTZG=canshu[25];//医保门特资格
						data.YBMTBZ=canshu[26];//医保门特病种
						data.YBTYZG=canshu[27];//医保特药资格
						data.YBTYBZ=canshu[28];//医保特药病种
						data.YBTYMCBM=canshu[29];//医保特药名称编码
						data.JMMDZG=canshu[30];//居民门大资格
						data.JMMDBZ=canshu[31];//居民门大病种
						data.JMMZXYBZG=canshu[32];//居民门诊血友病资格
						data.JMMZXYBBZ=canshu[33];//居民门诊血友病病种
						data.JMTYZG=canshu[34];//居民特药资格
						data.JMTYBZ=canshu[35];//居民特药病种
						data.JMTYMCBM=canshu[36];//居民特药名称编码
						data.NMGMDZG=canshu[37];//农民工门大资格
						data.NMGMDBZ=canshu[38];//农民工门大病种
						data.NMGTYZG=canshu[39];//农民工特药资格
						data.NMGTYBZ=canshu[40];//农民工特药病种
						data.NMGTYMCBM=canshu[41];//农民工特药名称编码
						data.NFXSZGMZTC=canshu[42];//能否享受职工门诊统筹
						data.SYSPLX=canshu[43];//生育审批类型
						data.FSYY=canshu[44];//封锁原因
						data.MMSYKBJE=canshu[45];//门慢剩余可报金额
						data.MTFZZLSYKBJE=canshu[46];//门特辅助治疗剩余可报金额
						data.GSDYZG=canshu[47];//工伤待遇资格
						data.GSDYBZ=canshu[48];//工伤待遇病种
						data.GSZDJL=canshu[49];//工伤诊断结论
						data.DKSYKBJE=canshu[50];//大卡剩余可报金额
						data.MTSYKBJE=canshu[51];//门统剩余可报金额
						data.YBJCZG=canshu[52];//医保家床资格
						/****增加建档立卡低收入人群参数  hj****/
						data.YBMZZXZG = canshu[53];//医保门诊专项资格
						data.YBMZZXYPTYMBM = canshu[54];//医保门诊专项药品通用名编码
						data.SFKNJDLK = canshu[55];//是否困难建档立卡人员
//						this.initFormData(data);
//						this.ybxx = data;
						var module = this.createModule("njjbForm", this.njjbForm);
						module.on("qr", this.onNjjbQr, this);
						var win = module.getWin();
						win.add(module.initPanel());
						module.doNew();
						module.initFormData(data);
						module.ybxx = data;
//			    		module.form.getForm().findField('YLLB').setValue("21");
						win.show();
					}else{
						MyMessageTip.msg("提示：",arr[3], true);
						return;	
					}
				}else {
					MyMessageTip.msg("提示：",ret.msg, true);
					return;
				}
			},
			onNjjbQr : function(data) {
				var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "savenjjbkxx",
						body:data
						});
				if (ret.code <=300) {
				} else {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				this.ybxx = data;
				this.njjbyllb=ret.json.body.YLLB;
				this.shiyb="2000";
				this.BRXZ = this.shiyb;
				data.BRXZ = this.shiyb;
				data.GRSFH = data.SFZH;
				this.data.GRBH = this.ybxx.GRBH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryBrxx",
							body : data
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				if (ret.json.Ll_return != "0") {
					if (ret.json.Ll_return != "-1") {
						data.EMPIID = ret.json.Ll_return;
						data.MZHM= ret.json.MZHM;
						this.doWriteBrxx(data)
					}
//					this.doAdd(null, null, data);
				} else {
//					if (ret.json.empiid) {
//						this.onSaveNJJBCBXX(ret.json.empiid);
//					}
//					var ret1 = phis.script.rmi.miniJsonRequestSync({
//								serviceId : "MedicareService",
//								serviceAction : "queryOutpatientAssociationNew",
//								SFZH : this.ybxx.SFZH
//							});
//					if (ret1.code > 300) {
//						this.processReturnMsg(ret1.code, ret1.msg);
//						return null;
//					}
//					var f = this.form.getForm().findField("MZHM");
//					f.setValue(ret1.json.MZGL);
//					this.doYbkhEnter(f);
//					this.form.getForm().findField("BRXZ").setValue(this.YBXZCS.SHIYB);
				}
			},
			onSaveNJJBCBXX : function(empiid) {
				if (this.ybxx && this.ybxx != null) {
					this.ybxx.EMPIID = empiid;
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "MedicareService",
								serviceAction : "saveNJJBBRXX",
								body : this.ybxx
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return;
					} else {
						MyMessageTip.msg("提示", "保存医保病人信息成功!", true);
					}
				}
			}
			
		});