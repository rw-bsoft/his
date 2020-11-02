$package("phis.application.fsb.script")

$import("phis.script.TableForm")

phis.application.fsb.script.FsbApplicationForm = function(cfg) {
	cfg.remoteUrl = 'MedicalDiagnosisZdlr';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{MSZD}</td></td>';
	cfg.queryParams = {
		"ZXLB" : 1
	};
	phis.application.fsb.script.FsbApplicationForm.superclass.constructor
			.apply(this, [cfg])
	this.colCount = 8;
	this.labelWidth = 55;
	this.width = 900;
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadData", this.afterLoadData, this);
}
Ext.extend(phis.application.fsb.script.FsbApplicationForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.fsb.script.FsbApplicationForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var mzhm = form.findField("MZHM");
				mzhm.un("specialkey", this.onFieldSpecialkey, this);
				mzhm.on("specialkey", this.doSpecialkey, this);

				// 住院号码 查询
				var zyhm = form.findField("ZYHM");
				zyhm.un("specialkey", this.onFieldSpecialkey, this);
				zyhm.on("specialkey", this.doSpecialkey, this);

				var SQZT = form.findField("SQZT");
				var JGID = form.findField("JGID");
				var SQFS = form.findField("SQFS");
				var BRID = form.findField("BRID");
				var CSNY = form.findField("CSNY");
				SQZT.hide()
				BRID.hide()
				JGID.hide()
				SQFS.hide()
				CSNY.hide()
				var SQRQ = form.findField("SQRQ");
				var ZDRQ = form.findField("ZDRQ");
				if (SQRQ.getValue() == null || SQRQ.getValue() == "") {
					SQRQ.setValue(new Date());
				}
				if (ZDRQ.getValue() == null || ZDRQ.getValue() == "") {
					ZDRQ.setValue(new Date());
				}
			},
			afterLoadData : function() {
				var form = this.form.getForm();
				var BRNL = form.findField("BRNL");
				var Y = new Date().getFullYear();
				var briY = this.data.CSNY.substr(0, 4);
				BRNL.setValue(Y - briY);
			},
			doSpecialkey : function(field, e) {
				if (!Ext.isIE) {
					e.stopEvent();
				}
				if (e.getKey() == Ext.EventObject.ENTER) { // 触发了listener后，如果按回车，执行相应的方法
					var name = field.getName();
					var value = field.getValue();
					//门诊号码
					if (name == "MZHM" && !value ) {
						// 为空时，默认打开新建档案功能
						this.showModule();
					} else if (name == "MZHM" && value) {
						this.getBRXX(name,value);
					}
					// 住院号码
					if (name == "ZYHM" && !value) {
						// 为空时，默认打开新建档案功能
						this.showModule();
					} else if (name == "ZYHM" && value) {
						this.getBRXX(name,value);
					}
				}
				if (e.getKey() == Ext.EventObject.BACKSPACE
						&& field.getValue().length > 0) { // 触发了listener后，如果按退格，执行相应的方法
					var value = field.getValue();
					field.setValue(value.substring(0, value.length - 1));
				}
				if (e.getKey() == Ext.EventObject.TAB) { // 触发了listener后，如果按tab，执行相应的方法
					if (field.getName() == 'MZHM') {
						this.form.getForm().findField("ZYHM").focus();
					}
					if (field.getName() == 'ZYHM') {
						this.form.getForm().findField("JCZD").focus();
					}
				}
			},
			showModule : function() {
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
				var form = m.midiModules[m.entryName].form.getForm();
				form.findField("MZHM").setDisabled(true);
//				// 1卡号
//				var form = m.midiModules[m.entryName].form.getForm();
//				if (pdms.json.cardOrMZHM == 1) {
//					form.findField("MZHM").setDisabled(true);
//				}
				// 2门诊号码
//				if (pdms.json.cardOrMZHM == 2) {
//					form.findField("cardNo").setValue(form.findField("MZHM")
//									.getValue());
//					form.findField("personName").focus(true, 200);
//				}
			},
			checkRecordExist : function(data) {
				this.getBRXX("MZHM",data.MZHM);
			},
			getBRXX : function(name, value) {
				var form = this.form.getForm();
				this.doCreate();
				var obj = {
					name : name,
					value : value
				}
				if (name == "MZHM") {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "jczxManageService",
								serviceAction : "selectBrdaByMzhm",
								body : obj
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
						return;
					} else {
						if (r.json.MZBR) {
							this.BRXX = r.json.MZBR;
							this.initFormData(this.BRXX)
							var BRNL = form.findField("BRNL");
							//var Y = new Date().getFullYear();
							//var briY = this.BRXX.CSNY.substr(0, 4);
							BRNL.setValue(this.BRXX.RYNL);

							var JCZD = form.findField("JCZD"); // 建床诊断
							var ICD = form.findField("ICD10"); // icd
							var ZDRQ = form.findField("ZDRQ"); // 诊断日期
							var BQZY = form.findField("BQZY"); // 病情摘要
							var JCYJ = form.findField("JCYJ"); // 建床意见

							if (r.json.BRZD) {
								var brzd = r.json.BRZD;
								JCZD.setValue(brzd.ZDMC); // 建床诊断
								ICD.setValue(brzd.ICD10); // ICD
								if (brzd.ZDSJ != null) {
									var zdsj = brzd.ZDSJ;
									zdsj = zdsj.substring(0, zdsj.indexOf(' '));
									ZDRQ.setValue(zdsj) // 诊断日期
								}
							}
							if (r.json.BCJL) {
								var bcjl = r.json.BCJL;
								BQZY.setValue(bcjl.ZSXX);
								JCYJ.setValue(bcjl.CZYS);
							}
						}
					}

				} else if (name == "ZYHM") {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "jczxManageService",
								serviceAction : "selectZyzdjlByField",
								body : obj
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
						return;
					} else {
						if (r.json.BRRY) {
							this.BRXX = r.json.BRRY;
							this.initFormData(this.BRXX)
							var BRNL = form.findField("BRNL");
							var Y = new Date().getFullYear();
							var briY = this.BRXX.CSNY.substr(0, 4);
							BRNL.setValue(Y - briY);

							var JCZD = form.findField("JCZD"); // 建床诊断
							var ICD = form.findField("ICD10"); // icd
							var ZDRQ = form.findField("ZDRQ"); // 诊断日期
							var SQFS = form.findField("SQFS");
							SQFS.setValue("2");
							if (r.json.ZYZD) {
								var zyzd = r.json.ZYZD;
								JCZD.setValue(zyzd.MSZD); // 建床诊断
								ICD.setValue(zyzd.JBBM); // ICD
								ZDRQ.setValue(zyzd.ZDRQ) // 诊断日期
							}

						}
					}
				}
				//获取光标焦点
				this.form.getForm().findField("JCZD").focus(false,800);
			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				if ((this.mainApp.jobId == 'phis.50'
						|| this.mainApp.jobId == 'phis.51'
						|| this.mainApp.jobId == 'phis.53' || this.mainApp.jobId == 'phis.55')
						& this.op != 'update') {
					values.SQZT = 1;
				}
				values.SQFS = values.SQFS || "3";
				values.JGID = this.mainApp.deptId;
				Ext.apply(this.data, values);
				Ext.Msg.show({
							title : '',
							msg : '确认保存家床申请?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.saveToServer(values);
									this.doCancel();
								}
							},
							scope : this
						})
			},
			onBeforeSave : function(entryName, op, saveData) {
				if (this.op == "create") {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "jczxManageService",
								serviceAction : "checkRepetition",
								cnds : [
										'and',
										['eq', ['$', 'BRID'],
												['i', this.BRXX.BRID]],
										['ne', ['$', 'SQZT'], ['i', 4]]],
								entryName : entryName
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return false;
					} else {
						if (r.json.body) {
							Ext.Msg.alert("提示", "该病人已申请，请勿重复申请!");
							return false;
						}
					}
				} else {
					if (this.data.SQZT == "2") {
						Ext.Msg.alert("提示", "请先退回申请!");
						return false;
					}
				}
			},
			doCreate : function() {
				// alert(1);
				// this.fireEvent("reSet", this);
				this.doNew();
				var form = this.form.getForm();
				var BRXM = form.findField("BRXM"); // 姓名
				var BRXB = form.findField("BRXB"); // 性别
				var BRNL = form.findField("BRNL"); // 年龄
				var SFZ = form.findField("SFZH"); // 身份证
				var BRXZ = form.findField("BRXZ");// 病人性质
				var DZ = form.findField("LXDZ"); // 联系地址
				var LXR = form.findField("LXR"); // 联系人
				var YHGX = form.findField("YHGX"); // 与患关系
				var LXDH = form.findField("LXDH"); // 联系电话
				var JCZD = form.findField("JCZD"); // 建床诊断
				var ICD = form.findField("ICD10"); // icd
				var ZDRQ = form.findField("ZDRQ"); // 诊断日期
				var BQZY = form.findField("BQZY"); // 病情摘要
				var JCYJ = form.findField("JCYJ"); // 建床意见
				var BRID = form.findField("BRID");
				var CSNY = form.findField("CSNY");

				BRXM.setValue(''); // 姓名
				BRXB.setValue(''); // 性别
				BRNL.setValue(''); // 年龄
				SFZ.setValue(''); // 身份证
				BRXZ.setValue(''); // 病人性质
				DZ.setValue(''); // 联系地址
				LXR.setValue(''); // 联系人
				YHGX.setValue(''); // 与患关系
				LXDH.setValue(''); // 联系电话
				JCZD.setValue(''); // 建床诊断
				ICD.setValue(''); // icd
				ZDRQ.setValue(''); // 诊断日期
				BQZY.setValue(''); // 病情摘要
				JCYJ.setValue(''); // 建床意见
				BRID.setValue('');
				CSNY.setValue('');
			},
			doBlyy : function() {

			},
			//新建个人基本信息
			doCreateInfo : function(){
				this.showModule();
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
									name : 'MSZD'

								}, {
									name : 'JBBM'

								}, {
									name : 'JBPB'

								}, {
									name : 'JBPB_text'

								}]);
			},
			setBackInfo : function(obj, record) {
				obj.collapse();
				this.form.getForm().findField("JCZD").setValue(record
						.get("MSZD"));
				this.form.getForm().findField("ICD10").setValue(record
						.get("JBBM"));
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
				if (it['not-null'] == "1" || it['not-null']) {
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
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					this.setButtonsState([action.id], false);
				}
				if (state == "editable") {
					this.setButtonsState(["create", "save", "cancel","createInfo"], true);
				}
				if (state == "uneditable") {
					this.setButtonsState(["cancel"], true);
				}
			},
			// 设置按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.form.getTopToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			},
			// 重新saveToServer，增加保存完 刷新申请单管理界面
			saveToServer : function(saveData) {
				var saveRequest = this.getSaveRequest(saveData); // **
				// 获取保存条件数据
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveRequest)) {
					return;
				}

				this.saving = true;

				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction || "",
							method : "execute",
							op : this.op,
							schema : this.entryName,
							module : this._mId, // 增加module的id
							body : saveRequest
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData],
										json.body);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
							MyMessageTip.msg("提示", "保存成功!", true)
							if (this.opener) {
								this.opener.refresh()
							}
						}, this)// jsonRequest
			}
		});