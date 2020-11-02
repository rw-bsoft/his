$package("phis.application.hos.script")

$import("phis.script.SimpleForm")

phis.application.hos.script.HospitalAntibacterialApplyForm = function(cfg) {
	cfg.remoteUrl = 'MedicineAmqc';
	cfg.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="160px" title="{YPMC}">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td>';
	cfg.minListWidth = 480;
	cfg.loadServiceId = "amqcLoad";
	this.render = "apply"
	phis.application.hos.script.HospitalAntibacterialApplyForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("loadData", this.afterLoadData, this);
}

Ext.extend(phis.application.hos.script.HospitalAntibacterialApplyForm,
		phis.script.SimpleForm, {
			onWinShow : function() {
				delete this.isSave;// 保存标志
				// 新申请
				if (!this.initDataId) {
					this.doNew();
					// 已提交记录不允许修改
					var btns = this.form.getTopToolbar().items;
					if (btns) {
						btns.item(0).setDisabled(false);
						btns.item(1).setDisabled(false);
					}
					this.form.getForm().findField("JYBGH").setDisabled(true);
					this.form.getForm().findField("QTYYXS").setDisabled(true);
					this.form.getForm().findField("HZKS").setDisabled(true);
					document.getElementById(this.render + "_status").innerHTML = "新增"
					this.initBrxx();
				} else {
					// 修改申请
					this.loadData();
					this.initBrxx();
				}
				this.remoteDicStore.baseParams.KSBZ = 1;
				this.remoteDicStore.baseParams.wardId = this.brxx.BRBQ;
				this.remoteDicStore.baseParams.openBy = this.openBy;
			},
			// {"JGID":"","BRID":17287,"EMPIID":"9ef542eaef434591b06f5833cfb774da","BRBQ":471,"BAHM":"","ZYHM":"0000000228","BRCH":"03","FYZH":"","BRCH_SHOW":"03","BRXM":"王星宁","BRXB":1,"BRXB_text":"男","BRKS":471,"BRKS_text":"住院全科1","BRXZ":6031,"BRXZ_text":"城乡居保","ZSYS":"183","ZSYS_text":"钟维维","ZZYS":"183","HLJB":"2","ZKZT":0,"BRQK":3,"YSDM":"1","CYPB":0,"RYRQ":"2013-11-25","CSNY":"2002-07-15","AGE":"11","JBMC":"","FJHM":"","CWKS":471,"KSDM":471,"CWXB":3,"CWFY":0,"ICU":0,"JCPB":0,"ZYH":187,"YEWYH":0,"ZDYCW":0,"XKYZ":"0","XTYZ":"0","CYZ":"0","GMYW":"","GMYW_SIGN":"0","CYRQ":"","RYRQS":"2013.11.25","RYNL":"11岁","JSCS":"0"}
			initBrxx : function() {
				// alert(Ext.encode(this.brxx))
				this.initFormData(this.brxx)
				this.data.JZXH = this.brxx.ZYH;
				this.data.JGID = this.mainApp.deptId;
				this.form.getForm().findField("YPMC").focus(true, 200);
			},
			initPanel : function(sc) {
				// 根据initDataId判断是新增还是修改
				if (!this.data) {
					this.data = {};
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
				this.schema = schema;
				var form = new Ext.form.FormPanel({
							tbar : this.createButtons(),
							html : this.getApplyFormHtml().apply({})
						})
				this.form = form;
				form.on("afterrender", this.onReady, this);
				return this.form;
			},
			onReady : function() {
				phis.application.hos.script.HospitalAntibacterialApplyForm.superclass.onReady
						.call(this)
				var items = this.schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1)) {
						// alert(it.acValue);
						continue;
					}
					var f = this.createField(it)
					f.index = i;
					f.render(this.render + "_" + it.id);
					this.form.add(f);
					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					if (it.id == "RZYL" || it.id == "YYLC") {
						f.on("change", this.countTotal, this)
					}
					if (it.id == "SQYWMG" || it.id == "QTYY") {
						f.on("check", this.doCheck, this)
					}
				}
				if (this.render == 'aduit') {
					var radioGroup = new Ext.form.RadioGroup({
								width : 120,
								disabled : true,
								renderTo : this.render + "_SPJG",
								items : [{
											name : "SPJG",
											boxLabel : "同意",
											inputValue : "1"
										}, {
											name : "SPJG",
											boxLabel : "不同意",
											inputValue : "9"
										}]

							})
					this.radioGroup = radioGroup;
					radioGroup.on("change", this.aduitChange, this)
					this.form.add(radioGroup);
				} else {
					document.getElementById(this.render + "_SPJG").innerHTML = '<input name="sqjg" type="radio" disabled value="1">同意</input>&nbsp;&nbsp;&nbsp;&nbsp;<input name="sqjg" type="radio" disabled value="2">不同意</input>'
				}
			},
			setBackInfo : function(obj, record) {
				var form = this.form.getForm();
				obj.setValue(record.get("YPMC"));
				// 隐藏字段
				this.data.YPXH = record.get("YPXH");
				this.data.YFBZ = record.get("YFBZ");
				this.data.YPJL = record.get("YPJL");
				form.findField("YFGG").setValue(record.get("YFGG"));
				obj.collapse();
				form.findField("RZYL").focus(true, 200);
			},
			doSave : function() {
				var values = this.getFormData();
				if (!values)
					return;

				values.JZXH = this.brxx.ZYH;
				values.JGID = this.mainApp.deptId;
				this.saveToServer(values);
			},
			doCommit : function() {
				var values = this.getFormData();
				if (!values)
					return;

				values.JZXH = this.brxx.ZYH;
				values.JGID = this.mainApp.deptId;
				values.DJZT = 1;
				this.saveToServer(values);
			},
			afterLoadData : function() {
				this.countTotal();
				if (this.data.DJZT > 0) {
					document.getElementById(this.render + "_status").innerHTML = this.data.DJZT > 1
							? "已审核"
							: "已提交";
				} else {
					document.getElementById(this.render + "_status").innerHTML = "新增"
				}
				// 已提交记录不允许修改
				var btns = this.form.getTopToolbar().items;
				if (btns) {
					btns.item(0).setDisabled(this.data.DJZT > 0);
					btns.item(1).setDisabled(this.data.DJZT > 0);
				}
				if (this.radioGroup) {
					this.radioGroup.setDisabled(true);
				} else {
					document.getElementById(this.render + "_SPJG").innerHTML = '<input name="sqjg" '
							+ (this.data.SPJG == 1 ? "checked" : '')
							+ ' type="radio" disabled value="1">同意</input>&nbsp;&nbsp;&nbsp;&nbsp;<input name="sqjg" type="radio" '
							+ (this.data.SPJG == 9 ? "checked" : '')
							+ ' disabled value="2">不同意</input>'
				}
			},
			countTotal : function(f, v) {
				var form = this.form.getForm()
				var rzyl = form.findField("RZYL").getValue();
				var yylc = form.findField("YYLC").getValue();
				if (rzyl && this.data.YFBZ && this.data.YPJL) {
					form.findField("MRYL").setValue(parseFloat(rzyl
									/ this.data.YFBZ / this.data.YPJL, 2))
					if (yylc) {
						form.findField("HJYL").setValue(parseFloat(rzyl * yylc,
								2))
					}
				}
			},
			doCheck : function(f, checked) {
				var id = f.getName();
				if (id == "SQYWMG") {
					var f = this.form.getForm().findField("JYBGH");
					f.setValue("");
					f.setDisabled(!checked);
				} else {
					var qtyy = this.form.getForm().findField("QTYYXS");
					qtyy.setValue("");
					qtyy.setDisabled(!checked);
					var hzks = this.form.getForm().findField("HZKS");
					hzks.setValue("");
					hzks.setDisabled(!checked);
				}
			},
			beforeSearchQuery : function() {
				// 判断当前行是否满足同组输入
				var s = this.remoteDic.getValue();
				if (s == null || s == "" || s.length == 0)
					return true;
				this.remoteDicStore.baseParams.TYPE = 1;// 药品
				if (s.substr(0, 1) == '*') {
					return false;
				} else if (s.substr(0, 1) == '.') {
					return false;
				}
				return true;
			},
			// add by caijy for checkbox
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
						if(it.id=="AGE"){
							v =  data["RYNL"]
						}
						if(it.id=="ZSYS"){
							v = data["ZRYS"]
						}
						if(it.id=="JBMC"){
							v = data["JCZD"]
						}
						if (v != undefined) {
							if (f.getXType() == "checkbox") {
								var setValue = "";
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
									if (v == checkValue) {
										setValue = true;
									} else if (v == unCheckValue) {
										setValue = false;
									}
								}
								if (setValue == "") {
									if (v == 1) {
										setValue = true;
									} else {
										setValue = false;
									}
								}
								f.setValue(setValue);
							} else {
								if (it.dic && v !== "" && v === 0) {// add by
									// yangl
									// 解决字典类型值为0(int)时无法设置的BUG
									v = "0";
								}
								f.setValue(v)
								if (it.dic && v != "0" && f.getValue() != v) {
									f.counter = 1;
									this.setValueAgain(f, v, it);
								}

							}
						}
						if (it.update == "false") {
							f.disable();
						}
					}
					this.setKeyReadOnly(true)
				}
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (!saveData.GRBQYZZ && !saveData.MYGNDX && !saveData.SQYWMG
						&& !saveData.QTYY) {
					MyMessageTip.msg("提示", "请先填写抗菌药物申请原因!", true);
					return;
				}
				if (saveData.QTYYXS && saveData.QTYYXS.realLength() > 255) {
					MyMessageTip.msg("提示", "[其它原因详述]长度超过允许最大长度255!", true);
					return;
				}
				if (saveData.JYBGH && saveData.JYBGH.realLength() > 50) {
					MyMessageTip.msg("提示", "[检验报告单]长度超过允许最大长度50!", true);
					return;
				}
				if (this.openBy == "fsb") {
					saveData.JZLX = 6;//家床就诊类型为6
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							op : this.op,
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							MyMessageTip.msg("提示", "保存成功!", true);
							if (json.body.SQDH) {
								this.initDataId = json.body.SQDH;
							}
							this.onWinShow();
							this.op = "update"
							this.fireEvent("save");
						}, this)// jsonRequest
			},
			getFormData : function() {
				var ac = util.Accredit;
				var form = this.form.getForm()
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items
				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var v = this.data[it.id] // ** modify by yzh
						if (v == undefined) {
							v = it.defaultValue
							if (it.type == "timestamp" && this.op == "create") {// update
								// by
								// caijy
								// 2013-3-21
								// for
								// 新增页面的时间动态生成
								v = Date.getServerDateTime();
							}
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							// add by caijy from checkbox
							if (f.getXType() == "checkbox") {
								var checkValue = 1;
								var unCheckValue = 0;
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
								}
								if (v == true) {
									v = checkValue;
								} else {
									v = unCheckValue;
								}
							}
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}
						if (v == null || v === "") {
							if (!it.pkey && it["not-null"] && !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空")
								return;
							}
						}
						if (it.type && it.type == "int") {
							v = (v == "0" || v == "" || v == undefined)
									? 0
									: parseInt(v);
						}
						values[it.id] = v;
					}
				}
				return values;
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPXH'
								}, {
									name : 'YPMC'
								}, {
									name : 'YFGG'
								}, {
									name : 'YPDW'
								}, {
									name : 'YPSL'
								}, {
									name : 'JLDW'
								}, {
									name : 'YPJL'
								}, {
									name : 'PSPB'
								},// 判断是否皮试药品
								{
									name : 'YFBZ'
								}, {
									name : 'GYFF'
								},// 药品用法
								{
									name : 'LSJG'
								}, {
									name : 'YPCD'
								}, {
									name : 'CDMC'
								}, {
									name : 'TYPE'
								}, {
									name : 'TSYP'
								}, {
									name : 'YFDW'
								}, {
									name : 'YBFL'
								}, {
									name : 'YBFL_text'
								}, {
									name : 'GYFF_text'
								}, {
									name : 'JYLX'
								}, {
									name : 'KCSL'
								}]);
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = it.width || 100
				// alert(this.defaultHeight || it.height)
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					height : this.defaultHeight || it.height,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent
				}
				if (it.xtype == "checkbox") {
					cfg.fieldLabel = null;
					cfg.boxLabel = it.alias;
				}

				// if (it.renderTo) {
				// cfg.renderTo = it.renderTo
				// }

				if (it.xtype == "uxspinner") {
					cfg.strategy = {}
					cfg.strategy.xtype = it.spin_xtype;
					// alert(Ext.encode(it));
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
					cfg.editable = true
				}
				if (it['not-null'] && !it.fixed) {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
				if (it['showRed']) {
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
				if (it.fixed || it.fixed) {
					cfg.disabled = true
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true
				}
				if (it.emptyText) {
					cfg.emptyText = it.emptyText;
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
				if (this.hideTrigger && cfg.disabled == true) {
					cfg.hideTrigger = true;
					cfg.emptyText = null;
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
					it.dic.width = 50
					if (it.dic.fields) {
						if (typeof(it.dic.fields) == 'string') {
							var fieldsArray = it.dic.fields.split(",")
							it.dic.fields = fieldsArray;
						}
					}
					var dic = it.dic;
					var cmbCfg = {
						store : util.dictionary.SimpleDicFactory.getStore(dic),
						valueField : "key",
						displayField : "text",
						searchField : dic.searchField || "mCode",
						editable : (dic.editable != undefined)
								? dic.editable
								: true,
						minChars : 2,
						selectOnFocus : true,
						triggerAction : dic.remote ? "query" : "all",
						pageSize : dic.pageSize,
						hideTrigger : it.fixed,
						width : dic.width || 200,
						listWidth : dic.listWidth,
						value : dic.defaultValue
					}
					if (!it.fixed) {
						cmbCfg.emptyText = dic.emptyText || '请选择';
					} else {
						cmbCfg.style = "border-style:none;background-image:none;";
					}
					var combox = new util.widgets.MyCombox(cmbCfg)
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					if (dic.autoLoad) {
						combox.store.load()
					}
					return combox;
				}
				if (it.fixed) {
					cfg.style = "border-style:none;background-image:none;";
				} else {
					if (it.xtype != "checkbox") {
						cfg.style = "border-style:none;background-image:none;border-bottom-style:dashed;";
					}
				}
				if (it.length) {
					cfg.maxLength = it.length;
				}
				if (it.xtype) {
					if (it.xtype == 'checkbox') {
						return new Ext.form.Checkbox(cfg);
					}
					return new Ext.form.Field(cfg);
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						cfg.style = "color:#00AA00;font-weight:bold;text-align:right;border-style:none;background-image:none;border-bottom-style:dashed;";
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield';
						cfg.emptyText = "请选择日期";
						cfg.format = 'Y-m-d';
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = it.width || 700
						cfg.height = it.height || 300
						if (this.plugins)
							this.getPlugins(cfg);
						break;
					case 'label' :
						cfg.id = it.id;
						cfg.xtype = "label"
						cfg.width = it.width || 700
						cfg.height = it.height || 300
						// cfg.html = "<span style='color:red'>测试</span>"
						break;
				}
				if (cfg.xtype == 'numberfield') {
					return new Ext.form.NumberField(cfg);
				}
				return new Ext.form.Field(cfg);
			},
			createRemoteDicField : function(it) {
				var mds_reader = this.getRemoteDicReader();
				// store远程url
				var url = ClassLoader.serverAppUrl || "";
				this.comboJsonData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : this.remoteUrl
					// ,pageSize : this.pageSize || 25,
					// pageNo : 1
				}
				var proxy = new Ext.data.HttpProxy({
							url : url + '*.jsonRequest',
							method : 'POST',
							jsonData : this.comboJsonData
						});
				var mdsstore = new Ext.data.Store({
							proxy : proxy,
							reader : mds_reader
						});
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")")
								if (json) {
									var code = json["code"]
									var msg = json["msg"]
									this.processReturnMsg(code, msg,
											this.refresh)
								}
							} else {
								this.processReturnMsg(404, 'ConnectionError',
										this.refresh)
							}
						}, this)
				this.remoteDicStore = mdsstore;
				Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr>' + this.remoteTpl + '<tr>', '</table>', '</div>',
						'</tpl>');
				var _ctx = this;
				// update for label颜色变红 by caijy
				var fieldLabel = it.alias;
				if (it['showRed']) {
					fieldLabel = "<span style='color:red'>" + it.alias
							+ "</span>"
				}
				var width = 280;
				if (it.properties.width) {
					width = parseInt(it.properties.width);
				}
				var remoteField = new Ext.form.ComboBox({
					name : it.id,
					fieldLabel : fieldLabel,
					width : width,
					store : mdsstore,
					selectOnFocus : true,
					typeAhead : false,
					loadingText : '搜索中...',
					pageSize : 10,
					hideTrigger : true,
					style : "border-style:none;background-image:none;border-bottom-style:dashed;",
					minListWidth : this.minListWidth || 280,
					tpl : resultTpl,
					minChars : 2,
					enableKeyEvents : true,
					lazyInit : false,
					itemSelector : 'div.search-item',
					onSelect : function(record) { // override default
						// onSelect
						// to do
						_ctx.setBackInfo(this, record);
						this.collapse();
					}
				});
				remoteField.on("focus", function() {
							remoteField.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
				remoteField.on("keyup", function(obj, e) {// 实现数字键导航
							var key = e.getKey();
							if (key == e.ENTER && !obj.isExpanded()) {
								// 是否是字母
								if (key == e.ENTER) {
									if (!obj.isExpanded()) {
										// 是否是字母
										var patrn = /^[a-zA-Z.]+$/;
										if (patrn.exec(obj.getValue())) {
											// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
											obj.getStore().removeAll();
											obj.lastQuery = "";
											if (obj.doQuery(obj.getValue(),
													true) !== false) {
												e.stopEvent();
												return;
											}
										}
									}
									_ctx.focusFieldAfter(obj.index);
									return;
								}
								var patrn = /^[a-zA-Z.]+$/;
								if (patrn.exec(obj.getValue())) {
									// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
									obj.getStore().removeAll();
									obj.lastQuery = "";
									if (obj.doQuery(obj.getValue(), true) !== false) {
										e.stopEvent();
										return;
									}
								}
							}
							if ((key >= 48 && key <= 57)
									|| (key >= 96 && key <= 105)) {
								if (obj.isExpanded()) {
									if (key == 48 || key == 96)
										key = key + 10;
									key = key < 59 ? key - 49 : key - 97;
									var record = this.getStore().getAt(key);
									_ctx.setBackInfo(obj, record);
								}
							}
							// 支持翻页
							if (key == 37) {
								obj.pageTb.movePrevious();
							} else if (key == 39) {
								obj.pageTb.moveNext();
							}
							// 删除事件 8
							if (key == 8) {
								if (obj.getValue().trim().length == 0) {
									if (obj.isExpanded()) {
										obj.collapse();
									}
								}
							}
						})
				if (remoteField.store) {
					remoteField.store.load = function(options) {
						Ext.apply(_ctx.comboJsonData, options.params);
						Ext.apply(_ctx.comboJsonData, mdsstore.baseParams);
						options = Ext.apply({}, options);
						this.storeOptions(options);
						if (this.sortInfo && this.remoteSort) {
							var pn = this.paramNames;
							options.params = Ext.apply({}, options.params);
							options.params[pn.sort] = this.sortInfo.field;
							options.params[pn.dir] = this.sortInfo.direction;
						}
						try {
							return this.execute('read', null, options); // <--
							// null
							// represents
							// rs. No rs for
							// load actions.
						} catch (e) {
							this.handleException(e);
							return false;
						}
					}
				}
				remoteField.on("beforequery", function(qe) {
							this.comboJsonData.query = qe.query;
							return true;
						}, this);
				remoteField.isSearchField = true;
				this.remoteDic = remoteField;
				return remoteField
			},
			getApplyFormHtml : function() {
				var tpl = new Ext.Template(
						'<table width="620" height="433" style="BORDER-COLLAPSE: collapse;font-size:14px;text-indent:2px;margin-left:30px;" borderColor="#000000" cellPadding="1"  align="center" border="1">',
						'<caption style="text-align:right;margin-bottom:2px;">',
						'<span  style="font-family:宋体;font-weight: bold;font-size: 24px;margin-right:50px;">抗菌药物使用申请表</span>',
						'<span id="'
								+ this.render
								+ '_status" style="font-family:宋体;font-weight: bold;font-size: 18px; color:#FF0000; margin-left:100px;">新增</span>',
						'</caption>',
						'<tr>',
						'<td width="66" height="29">科&nbsp;&nbsp;室</td>',
						'<td width="118"><div id="' + this.render
								+ '_BRKS" /></td>',
						'<td width="63">病&nbsp;&nbsp;区</td>',
						'<td colspan="2"><div id="' + this.render
								+ '_BRBQ"></div></td>',
						'<td width="78">住院号</td>',
						'<td colspan="2"><div id="' + this.render
								+ '_ZYHM">{ZYHM}</div></td>',
						'</tr>',
						'<tr>',
						'<td height="28">姓&nbsp;&nbsp;名</td>',
						'<td><div id="' + this.render + '_BRXM"></div></td>',
						'<td>性&nbsp;&nbsp;别</td>',
						'<td colspan="2"><div id="' + this.render
								+ '_BRXB"></div></td>',
						'<td>年&nbsp;&nbsp;龄</td>',
						'<td colspan="2"><div id="' + this.render
								+ '_AGE"></div></td>',
						'</tr>',
						'<tr>',
						'<td height="29">床位号</td>',
						// 床位号码是不是可以输入，并通过此调出病人
						'<td><div id="' + this.render + '_BRCH"></div></td>',
						'<td>主治医生</td>',
						'<td colspan="5"><div id="' + this.render
								+ '_ZSYS"></td>',
						'</tr>',
						'<tr>',
						'<td height="30">诊&nbsp;&nbsp;断</td>',
						'<td colspan="7"><div id="' + this.render
								+ '_JBMC"></td>',
						'</tr>',
						'<tr>',
						'<td rowspan="2">申请使用抗菌药物</td>',
						'<td height="30" colspan="2">药品名称</td>',
						'<td width="61">规格</td>',
						'<td width="60">日用量</td>',
						'<td>日剂量</td>',
						'<td width="69">用药天数</td>',
						'<td width="53">总剂量</td>',
						'</tr>',
						'<tr>',
						'<td height="30" colspan="2"><div id="' + this.render
								+ '_YPMC" /></td>',
						'<td><div id="' + this.render + '_YFGG"></div></td>',
						'<td><div id="' + this.render + '_MRYL"></div></td>',
						'<td><div id="' + this.render + '_RZYL"></div></td>',
						'<td><div id="' + this.render + '_YYLC"></div></td>',
						'<td><div id="' + this.render + '_HJYL"></div></td>',
						'</tr>',
						'<tr>',
						'<td rowspan="3">申请原因</td>',
						'<td height="50" colspan="7"><div style="float:left"><span id="'
								+ this.render
								+ '_GRBQYZZ"/></div><div style="float:left"><span id="'
								+ this.render + '_MYGNDX"/></div></td>',
						'</tr>',
						'<tr>',
						'<td colspan="7"><div style="float:left"><div id="'
								+ this.render
								+ '_SQYWMG" ></div><span style="float:left">（检验报告单：</span><div style="float:left" id="'
								+ this.render
								+ '_JYBGH"></div>）</div></div></td>',
						'</tr>',
						'<tr>',
						'<td height="54" colspan="7"><div style="float:left"><span id="'
								+ this.render
								+ '_QTYY" /></div><div style="float:left">（请</div><div style="float:left" id="'
								+ this.render
								+ '_HZKS"></div><div>专科会诊意见）：</div>',
						'<div style="clear:both;" id="' + this.render
								+ '_QTYYXS" /></td>', '</tr>', '<tr>',
						'<td height="36">申请医师</td>',
						'<td colspan="3"><div id="' + this.render
								+ '_SQYS"></div></td>', '<td>申请日期</td>',
						'<td colspan="3"><div id="' + this.render
								+ '_SQRQ"></td>', '</tr>', '<tr>',
						'<td height="32">审批结果</td>',
						'<td colspan="3"><div id="' + this.render
								+ '_SPJG" /></td>', '<td>审批用量</td>',
						'<td colspan="3"><div id="' + this.render
								+ '_SPYL" /></td>', '</tr>', '</table><br />')
				return tpl;
			}

		});