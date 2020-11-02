$package("chis.application.mhc.script.deliveryHtml")
$import(
		"chis.script.BizHtmlFormView",
		"chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlformTemplate",
		"chis.script.HtmlCommonMethod")
$styleSheet("chis.css.DebilityChildrenVisitHtmlform");
chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlform = function(
		cfg) {
	chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlform.superclass.constructor
			.apply(this, [cfg]);
	Ext
			.apply(
					this,
					chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlformTemplate)

	Ext.apply(this, chis.script.HtmlCommonMethod)
	this.fldTofld = [{
				"name" : "face",
				"value" : "9",
				"id" : [{
							"contro" : "yes",
							"id" : "faceOther"
						}]
			}, {
				"name" : "bregmaStatus",
				"value" : "4",
				"id" : [{
							"contro" : "yes",
							"id" : "otherStatus"
						}]
			}, {
				"name" : "eye",
				"value" : "10",
				"id" : [{
							"contro" : "yes",
							"id" : "eyeAbnormal"
						}]
			}, {
				"name" : "ear",
				"value" : "7",
				"id" : [{
							"contro" : "yes",
							"id" : "earAbnormal"
						}]
			}, {
				"name" : "neck",
				"value" : "y",
				"id" : [{
							"contro" : "yes",
							"id" : "neck1"
						}]
			}, {
				"name" : "nose",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "noseAbnormal"
						}]
			}, {
				"name" : "skin",
				"value" : "99",
				"id" : [{
							"contro" : "yes",
							"id" : "skinAbnormal"
						}]
			}, {
				"name" : "mouse",
				"value" : "14",
				"id" : [{
							"contro" : "yes",
							"id" : "mouseAbnormal"
						}]
			}, {
				"name" : "anal",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "analAbnormal"
						}]
			}, {
				"name" : "heartlung",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "heartLungAbnormal"
						}]
			}, {
				"name" : "umbilical",
				"value" : "9",
				"id" : [{
							"contro" : "yes",
							"id" : "umbilicalOther"
						}]
			}, {
				"name" : "spine",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "spineAbnormal"
						}]
			}, {
				"name" : "genitalia",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "genitaliaAbnormal"
						}]
			}, {
				"name" : "abdominal",
				"value" : "5",
				"id" : [{
							"contro" : "yes",
							"id" : "abdominalabnormal"
						}]
			}, {
				"name" : "referral",
				"value" : "y",
				"id" : [{
							"contro" : "yes",
							"id" : "referralReason"
						}, {
							"contro" : "yes",
							"id" : "referralUnit"
						}]
			}, {
				"name" : "limbs",
				"value" : "2",
				"id" : [{
							"contro" : "yes",
							"id" : "limbsAbnormal"
						}]
			}];
	this.disableFlds = ["faceOther", "otherStatus", "eyeAbnormal",
			"earAbnormal", "neck1", "noseAbnormal", "skinAbnormal",
			"mouseAbnormal", "analAbnormal", "heartLungAbnormal",
			"genitaliaAbnormal", "abdominalabnormal", "spineAbnormal",
			"umbilicalOther", "referralReason", "referralUnit", "limbsAbnormal"];// 初始化不可用
	this.createFields = ["visitDate", "nextVisitDate"];

}

Ext.extend(
		chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlform,
		chis.script.BizHtmlFormView, {
			getHTMLTemplate : function() {
				return this.getDebilityChildrenVisitHtmlformTemplate();
			},
			addFieldAfterRender : function() {
				// 随访日期
				var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				this.visitDate = new Ext.form.DateField({
							name : 'visitDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							maxValue : curDate,
							allowBlank : false,
							invalidText : "必填字段",
							fieldLabel : "本次随访日期",
							value : curDate,
							disabled : true,// 禁止该控件
							renderTo : Ext.get("visitDate" + this.idPostfix)
						});
				this.nextVisitDate = new Ext.form.DateField({
							name : 'nextVisitDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							minValue : curDate,
							// allowBlank : false,
							// invalidText : "必填字段",
							fieldLabel : "下次随访日期",
							renderTo : Ext
									.get("nextVisitDate" + this.idPostfix)
						});
				// 随访医生签名
				this.visitDoctor = this.createDicField({
							"width" : 150,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.user01",
							"render" : "Tree",
							onlySelectLeaf : true,
							"selectOnFocus" : true,
							parentKey : this.mainApp.uid

						});

				Ext.apply(this.visitDoctor, {
							name : "visitDoctor"
						})
				this.visitDoctor.fieldLabel = "随访医生";
				this.visitDoctor.render("visitDoctor" + this.idPostfix);
				this.visitDoctor.setValue(this.mainApp.uid)
				this.visitDoctor.on("select", this.changeManaUnit, this);
				this.visitDoctor.disable();// 方法
				//
				this.radioToInput(this.fldTofld);
				this.addFieldDataValidateFun(this.schema);

			},
			// 保存
			doSave : function() {
				if (!this.htmlFormSaveValidate(this.schema)) {
					return;
				}
				var values = this.getFormData();
				this.saveToServer(values);
				

			},
			doCreate : function() {
				this.data = {};// 清缓存
				this.doNew(this.idPostfix);

			},
			// 获取整个页面数据
			getFormData : function() {
				var ac = util.Accredit;
				var form = this.form.getForm();
				if (!this.schema) {
					return
				}
				var values = {};// 数据集合
				var items = this.schema.items;
				if (items) {
					var n = items.length;
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id] // ** modify by yzh
						// 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue();
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
								var today = new Date().format('Y-m-d');
								if (v > today) {
									return;
								}
							}
						} else {// 不是ext创建的控件
							if (it.tag == "text") {
								v = this.getValueById(it.id);
							} else if (it.tag == "radioGroup") {
								v = this.getRadioValueMy(it.id);
								// 修改前台传0过去，造成无法保存的问题
								if (v == 0) {
									v = "0";
								}
							} else if (it.tag == "checkBox") {
								v = this.getCheckBoxValuesMy(it.id);

							}
						}
						values[it.id] = v;
						// if (v == null || v === "" || v == 0) {
						// if (!it.pkey
						// && (it['not-null'] == "1" || it['not-null'] ==
						// "true")
						// && !it.ref) {
						// return;
						// }
						//
						// }
						// fgx
					}
				}

				values["pregnantId"] = this.exContext.ids.pregnantId;
				values["babyId"] = this.exContext.args.babyId
				if (this.data["visitId"]) {
					values["visitId"] = this.data["visitId"];
				}
				if (this.nextVisitDate.getValue()) {
					values["nextVisitDate"] = this.nextVisitDate.getValue()
							.format('Y-m-d');
				}
				if (this.visitDoctor.getValue()) {
					values["visitDoctor"] = this.visitDoctor.getValue();
				}
				if (this.visitDate.getValue()) {
					values["visitDate"] = this.visitDate.getValue()
							.format('Y-m-d');
				}
				return values;
			},
			initFormData : function(data) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length;
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined || this.flag == true) {
							if (it.dic && v !== "" && v === 0) {// add by
								v = "0";
							}
							f.setValue(v)
							if (it.dic && v != "0" && f.getValue() != v) {
								f.counter = 1;
							}
						}
					} else {
						var v = data[it.id]
						if (v != undefined || this.flag == true) {
							if (undefined == v) {
								v = ""
							}
							if (it.tag == "text") {
								this.setValueById(it.id, v)
							} else if (it.tag == "radioGroup") {
								if (it.controValue == v) {
									var strId = it.controId;
									var s = strId.indexOf(",");
									if (s == -1) {
										this.getObj(strId).disabled = false;
									} else {
										this.getObj(strId.substring(0, s)).disabled = false;// s处不要
										this.getObj(strId.substring(s + 1,
												strId.length)).disabled = false;
									}
								}
								this.setRadioValueMy(it.id, v)
							} else if (it.tag == "checkBox") {
								this.setCheckBoxValuesMy(it.id, v)
							} else if (it.tag == "selectgroup") {
								this.setSelectValues(it.id, v)
							}
						}
					}
					this.setKeyReadOnly(true)
				}
				this.visitDate.setValue(data["visitDate"]);
				this.nextVisitDate.setValue(data["nextVisitDate"]);
				this.visitDoctor.setValue(data["visitDoctor"]);
				// 体重和身高
				this.getObj("weightx").value = this.exContext.args.weightx;
				this.getObj("weightx").disabled = true;
				this.getObj("lengthx").value = this.exContext.args.lengthx;
				this.getObj("lengthx").disabled = true;
				var obj=document.getElementById("eyeAbnormal"+this.idPostfix);
				if(obj&&data["eyeAbnormal"]){
					obj.disabled=false
				}
				var obj=document.getElementById("earAbnormal"+this.idPostfix);
				if(obj&&data["earAbnormal"]){
					obj.disabled=false
				}
			},
			// 初始化页面
			doNew : function(Id) {
				var dom = document.getElementsByTagName("input");
				var l = dom.length;
				for (var i = 0; i < l; i++) {
					var d = dom[i];
					if (d.type == "text") {
						var arrt = dom[i].getAttribute("id");
						if (arrt.indexOf(Id) > 0) {
							d.value = "";
							document.getElementById(arrt).disabled = false;
						}
					} else if (d.type == "radio") {
						if (arrt.indexOf(Id) > 0) {
							d.checked = false;
						}
					} else if (d.type == "checkbox") {
						if (arrt.indexOf(Id) > 0) {
							d.checked = false;
						}
					}
				}
				this.initFieldDisable(this.disableFlds);
				this.nextVisitDate.setValue("");
				var v = {};
				v["key"] = this.mainApp.uid
				v["text"] = this.mainApp.uname
				this.visitDoctor.setValue(v);
				this.visitDoctor.disabled = true, // 禁止该控件
				this.visitDate.setValue(Date.parseDate(this.mainApp.serverDate,
						"Y-m-d"));
				// 体重和身高
				this.getObj("weightx").value = this.exContext.args.weightx;
				this.getObj("weightx").disabled = true;
				this.getObj("lengthx").value = this.exContext.args.lengthx;
				this.getObj("lengthx").disabled = true;
				// this.isNotDisable(this.isDisabled);
				this.resetButtons();
			},
			// **************************其他页面的处理方法**************************
			changeManaUnit : function(combo, node) {
				this.visitDoctor.text = node.text;
				this.visitDoctor.key = node.id;
			},
			saveToServer : function(saveRequest) {
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveRequest)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.pregnantRecordService",
							serviceAction : "saveBabyVisitRecordToHtml",
							op : this.op,
							body : saveRequest,
							method : "execute"
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveRequest],
										json.body);
								return
							}
							Ext.apply(this.data, saveRequest);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data);
							}
							this.op = "update"
						}, this)// jsonRequest
			},
			doPrintVisitRecord : function() {
				this.visitId = this.data["visitId"];
				if (!this.visitId) {
					this.visitId = "23333333";// 当this.visitId 为空的时候，随便设置值
				}
				var url = "resources/chis.prints.template.PrenatalChildrenRecord.print?type="
						+ 1 + "&visitId=" + this.visitId
				url += "&temp=" + new Date().getTime()
				var win = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				if (Ext.isIE6) {
					win.print()
				} else {
					win.onload = function() {
						win.print()
					}
				}
			}
		});