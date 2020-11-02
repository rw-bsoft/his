$package("chis.application.ohr.script")
$import("chis.script.BizTableFormView", "chis.script.util.widgets.MyMessageTip")
$styleSheet("chis.css.MedicineManageFormA");

chis.application.ohr.script.MedicineManageFormA = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.ohr.script.MedicineManageFormA.superclass.constructor
			.apply(this, [cfg]);
	this.saveServiceId = "chis.chineseMedicineManageService";
	this.saveAction = "saveChineseMedicineManage";
	this.loadServiceId = "chis.chineseMedicineManageService";
	this.loadAction = "loadChineseMedicineManage";
}
var formA_ctx = null;
function doHtmlFieldClick(type, index) {
	formA_ctx.exeHtmlFieldClick(type, index);
}
Ext.extend(chis.application.ohr.script.MedicineManageFormA,
		chis.script.BizTableFormView, {
			initPanel : function(sc) {
				this.id = this.generateMixed(7);
				if (this.form) {
					return this.form;
				}
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
							autoScroll : true,
							html : this.getHtml().apply({})
						})
				this.form = form;
				form.on("afterrender", this.onReady, this);
				return form;
			},
			exeHtmlFieldClick : function(type, index) {
				if (type == "physiqueIdentify") {
					this.doDetermine(this.dataQ);
				} else if (type == "healthGuide") {
					var healthGuideF = document.getElementsByName("healthGuide"
							+ index + this.id);
					var otherF = document.getElementById("other" + index
							+ this.id);
					otherF.value = "";
					if (healthGuideF[5].checked == true
							|| healthGuideF[5].checked == "true") {
						otherF.disabled = false;
					} else {
						otherF.disabled = true;
					}
				}
			},
			getHtml : function() {
				var html = new Ext.Template('<div class="answer">'
						+ '<table width="100%" border="0" cellpadding="0" cellspacing="0" class="tables">'
						+ '<tr><th>体质类型</th><th>体质辨识</th><th>中医药保健指导</th></tr><tr><td align="center">气虚质</td><td>1.得分'
						+ '<input type="text" class="underline with100" id="score1'
						+ this.id
						+ '" disabled="disabled"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify1'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify1'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>倾向是</td><td>'
						+ '<input type="checkbox" name="healthGuide1'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide1'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide1'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide1'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide1'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide1'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',1)"/>6．其他：'
						+ '<input type="text" class="underline"  disabled="disabled" id="other1'
						+ this.id
						+ '"/></td></tr><tr><td align="center">阳虚质</td><td>1.得分'
						+ '<input type="text" class="underline with100"  disabled="disabled" id="score2'
						+ this.id
						+ '"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify2'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify2'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>倾向是</td><td>'
						+ '<input type="checkbox" name="healthGuide2'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide2'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide2'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide2'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide2'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide2'
						+ this.id
						+ '"  onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',2)"/>6．其他：'
						+ '<input type="text" class="underline" disabled="disabled" id="other2'
						+ this.id
						+ '"/></td></tr><tr><td align="center">阴虚质</td><td>1.得分'
						+ '<input type="text" class="underline with100" disabled="disabled" id="score3'
						+ this.id
						+ '"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify3'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify3'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>倾向是</td><td>'
						+ '<input type="checkbox" name="healthGuide3'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide3'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide3'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide3'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide3'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide3'
						+ this.id
						+ '"  onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',3)"/>6．其他：'
						+ '<input type="text" class="underline" disabled="disabled" id="other3'
						+ this.id
						+ '"/></td></tr><tr><td align="center">痰湿质</td><td>1.得分'
						+ '<input type="text" class="underline with100" disabled="disabled" id="score4'
						+ this.id
						+ '"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify4'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify4'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>倾向是</td><td>'
						+ '<input type="checkbox" name="healthGuide4'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide4'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide4'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide4'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide4'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide4'
						+ this.id
						+ '"  onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',4)"/>6．其他：'
						+ '<input type="text" class="underline" disabled="disabled" id="other4'
						+ this.id
						+ '"/></td></tr><tr><td align="center">湿热质</td><td>1.得分'
						+ '<input type="text" class="underline with100" disabled="disabled" id="score5'
						+ this.id
						+ '"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify5'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify5'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>倾向是</td><td>'
						+ '<input type="checkbox" name="healthGuide5'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide5'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide5'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide5'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide5'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide5'
						+ this.id
						+ '"  onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',5)"/>6．其他：'
						+ '<input type="text" class="underline" disabled="disabled" id="other5'
						+ this.id
						+ '"/></td></tr><tr><td align="center">血瘀质</td><td>1.得分'
						+ '<input type="text" class="underline with100" disabled="disabled" id="score6'
						+ this.id
						+ '"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify6'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify6'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>倾向是</td><td>'
						+ '<input type="checkbox" name="healthGuide6'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide6'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide6'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide6'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide6'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide6'
						+ this.id
						+ '"  onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',6)"/>6．其他：'
						+ '<input type="text" class="underline" disabled="disabled" id="other6'
						+ this.id
						+ '"/></td></tr><tr><td align="center">气郁质</td><td>1.得分'
						+ '<input type="text" class="underline with100" disabled="disabled" id="score7'
						+ this.id
						+ '"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify7'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify7'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>倾向是</td><td>'
						+ '<input type="checkbox" name="healthGuide7'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide7'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide7'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide7'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide7'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide7'
						+ this.id
						+ '"  onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',7)"/>6．其他：'
						+ '<input type="text" class="underline" disabled="disabled" id="other7'
						+ this.id
						+ '"/></td></tr><tr><td align="center">特禀质</td><td>1.得分'
						+ '<input type="text" class="underline with100" disabled="disabled" id="score8'
						+ this.id
						+ '"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify8'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify8'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>倾向是</td><td>'
						+ '<input type="checkbox" name="healthGuide8'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide8'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide8'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide8'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide8'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide8'
						+ this.id
						+ '"  onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',8)"/>6．其他：'
						+ '<input type="text" class="underline" disabled="disabled" id="other8'
						+ this.id
						+ '"/></td></tr><tr><td align="center">平和质</td><td>1.得分'
						+ '<input type="text" class="underline with100" disabled="disabled" id="score9'
						+ this.id
						+ '"/><br />2.'
						+ '<input type="radio" name="physiqueIdentify9'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>是  3.'
						+ '<input type="radio" name="physiqueIdentify9'
						+ this.id
						+ '" onclick="doHtmlFieldClick('
						+ "'physiqueIdentify'"
						+ ')"/>基本是</td><td>'
						+ '<input type="checkbox" name="healthGuide9'
						+ this.id
						+ '" />1．情志调摄'
						+ '<input type="checkbox" name="healthGuide9'
						+ this.id
						+ '" />2．饮食调养'
						+ '<input type="checkbox" name="healthGuide9'
						+ this.id
						+ '" />3．起居调摄'
						+ '<input type="checkbox" name="healthGuide9'
						+ this.id
						+ '" />4．运动保健'
						+ '<input type="checkbox" name="healthGuide9'
						+ this.id
						+ '" />5．穴位保健'
						+ '<input type="checkbox" name="healthGuide9'
						+ this.id
						+ '"  onclick="doHtmlFieldClick('
						+ "'healthGuide'"
						+ ',9)"/>6．其他：'
						+ '<input type="text" class="underline" disabled="disabled" id="other9'
						+ this.id
						+ '"/></td></tr></table></div>'
						+ '<table width="800" border="0"  style="margin-left:10px;"><tr><td width="100" style="margin-left:10px;">填表日期：</td><td colspan="3"><div id="reportDate'
						+ this.id
						+ '"/></td>'
						+ '<td width="100" style="margin-left:10px;">医生签名：</td><td colspan="3">'
						+ '<div id="reportUser'
						+ this.id
						+ '"/></td></tr></table></div>');
				this.html = html;
				return html;
			},
			onReady : function() {
				formA_ctx = this;
				chis.application.ohr.script.MedicineManageFormA.superclass.onReady
						.call(this);
				var items = this.schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (it.id == "reportUser") {
						this.reportUser = this.createDicField({
									"width" : 280,
									"defaultIndex" : 0,
									"id" : "chis.dictionary.user01",
									"render" : "Tree",
									"selectOnFocus" : true,
									"onlySelectLeaf" : true,
									"parentKey" : "%user.manageUnit.id",
									"defaultValue" : {
										'key' : this.mainApp.uid,
										'text' : this.mainApp.uname + "--"
												+ this.mainApp.jobtitle
									}
								});
						this.reportUser.render(it.id + this.id);
						this.form.add(this.reportUser);
					}
					if (it.id == "reportDate") {
						this.reportDate = new Ext.form.DateField({
									width : 280,
									name : it.id,
									fieldLabel : it.alias,
									value : it.defaultValue,
									emptyText : "请选择日期",
									format : 'Y-m-d',
									maxValue : new Date()
								});
						this.reportDate.render(it.id + this.id);
						this.form.add(this.reportDate);
					}
				}
			},
			loadData : function(){
				//不需要发送数据加载请求
			},
			doDetermine : function(dataQ) {
				this.dataQ = dataQ;
				if (!this.schema) {
					return
				}
				var items = this.schema.items;
				var tzlx1 = 0;
				var tzlx2 = 0;
				var tzlx3 = 0;
				var tzlx4 = 0;
				var tzlx5 = 0;
				var tzlx6 = 0;
				var tzlx7 = 0;
				var tzlx8 = 0;
				var tzlx9 = 0;
				var j = 1;
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (it.boxType != "radio") {
						continue;
					}
					var itemValue = parseInt(dataQ[it.id]);
					switch (j) {
						case 1 :
							tzlx9 += itemValue;
							break;
						case 2 :
							tzlx9 += 6 - itemValue;
							tzlx1 += itemValue;
							break;
						case 4 :
							tzlx9 += 6 - itemValue;
							tzlx1 += itemValue;
							break;
						case 5 :
							tzlx9 += 6 - itemValue;
							tzlx7 += itemValue;
							break;
						case 13 :
							tzlx9 += 6 - itemValue;
							tzlx2 += itemValue;
							break;
						case 3 :
						case 14 :
							tzlx1 += itemValue;
							break;
						case 11 :
						case 12 :
						case 29 :
							tzlx2 += itemValue;
							break;
						case 10 :
						case 21 :
						case 26 :
						case 31 :
							tzlx3 += itemValue;
							break;
						case 9 :
						case 16 :
						case 28 :
						case 32 :
							tzlx4 += itemValue;
							break;
						case 23 :
						case 25 :
						case 27 :
						case 30 :
							tzlx5 += itemValue;
							break;
						case 19 :
						case 22 :
						case 24 :
						case 33 :
							tzlx6 += itemValue;
							break;
						case 6 :
						case 7 :
						case 8 :
							tzlx7 += itemValue;
							break;
						case 15 :
						case 17 :
						case 18 :
						case 20 :
							tzlx8 += itemValue;
							break;
					}
					j++;
				}
				var flag1 = false;
				var flag2 = false;
				for (var i = 1; i < 9; i++) {
					var score = eval("tzlx" + i);
					if (score >10) {
						flag2 = true;
					}
					if (score > 8) {
						flag1 = true;
					}
					var scoreDoc = document.getElementById("score" + i
							+ this.id);
					var physiqueIdentifyDoc = document
							.getElementsByName("physiqueIdentify" + i + this.id);
					scoreDoc.value = score;
					if (score >= 11) {
						physiqueIdentifyDoc[0].checked = true;
					} else if (score > 8) {
						physiqueIdentifyDoc[1].checked = true;
					} else {
						physiqueIdentifyDoc[0].checked = false;
						physiqueIdentifyDoc[1].checked = false;
					}
				}
				var score9Doc = document.getElementById("score9" + this.id);
				var physiqueIdentify9Doc = document
						.getElementsByName("physiqueIdentify9" + this.id);
				score9Doc.value = tzlx9;
				if (tzlx9 >= 17 && flag1 == false) {
					physiqueIdentify9Doc[0].checked = true;
				} else if (tzlx9 >= 17 && flag2 == false) {
					physiqueIdentify9Doc[1].checked = true;
				} else {
					physiqueIdentify9Doc[0].checked = false;
					physiqueIdentify9Doc[1].checked = false;
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
				Ext.apply(this.data, values);
				Ext.apply(values, this.dataQ);
				this.saveToServer(values);
			},
			getFormData : function() {
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items;
				Ext.apply(this.data, this.exContext.empiData);
				Ext.apply(this.data, this.exContext.ids);
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					var v = this.data[it.id]
					if (v == undefined) {
						v = it.defaultValue
					}
					if (it.id == "reportUser") {
						v = this.reportUser.getValue();
					}
					if (it.id == "reportDate") {
						v = this.reportDate.getValue();
					}
					values[it.id] = v;
				}
				var bodyType = "";
				for (var i = 1; i < 10; i++) {
					var scoreDoc = document.getElementById("score" + i
							+ this.id);
					var otherDoc = document.getElementById("other" + i
							+ this.id);
					var physiqueIdentifyDoc = document
							.getElementsByName("physiqueIdentify" + i + this.id);
					var healthGuideDoc = document
							.getElementsByName("healthGuide" + i + this.id);
					values["score" + i] = scoreDoc.value;
					values["other" + i] = otherDoc.value;
					for (var j = 0; j < physiqueIdentifyDoc.length; j++) {
						if (physiqueIdentifyDoc[j].checked == true
								|| physiqueIdentifyDoc[j].checked == "true") {
							var v = j + 1;
							values["physiqueIdentify" + i] = v;
							bodyType = bodyType + i + ",";
							break;
						}
					}
					var hValue = "";
					for (var j = 0; j < healthGuideDoc.length; j++) {
						if (healthGuideDoc[j].checked == true
								|| healthGuideDoc[j].checked == "true") {
							var v = j + 1;
							hValue = hValue + v + ",";
						}
					}
					values["healthGuide" + i] = hValue.substring(0,
							hValue.length - 1);
				}
				values["bodyType"] = bodyType.substring(0, bodyType.length - 1);
				return values;
			},
			initFormData : function(data) {
				this.beforeInitFormData(data);
				Ext.apply(this.data, data);
				this.op = "update";
				this.initDataId = this.data[this.schema.pkey];
				var items = this.schema.items;
				var n = items.length;
				for (var i = 0; i < n; i++) {
					var it = items[i];
					var v = this.data[it.id];
					if (v == undefined) {
						continue;
					}
					if (it.id == "reportUser") {
						this.reportUser.setValue(v);
					}
					if (it.id == "reportDate") {
						this.reportDate.setValue((v + "").substring(0, 10));
					}
				}
				for (var i = 1; i < 10; i++) {
					var healthGuideDoc = document
							.getElementsByName("healthGuide" + i + this.id);
					var physiqueIdentifyDoc = document
							.getElementsByName("physiqueIdentify" + i + this.id);
					physiqueIdentifyDoc[0].checked = false;
					physiqueIdentifyDoc[1].checked = false;
					for (var j = 0; j < healthGuideDoc.length; j++) {
						healthGuideDoc[j].checked = false;
					}
					var otherF = document.getElementById("other" + i + this.id);
					otherF.value = "";
					otherF.disabled = true;
				}

				for (var i = 1; i < 10; i++) {
					var scoreDoc = document.getElementById("score" + i
							+ this.id);
					var otherDoc = document.getElementById("other" + i
							+ this.id);
					var physiqueIdentifyDoc = document
							.getElementsByName("physiqueIdentify" + i + this.id);
					var healthGuideDoc = document
							.getElementsByName("healthGuide" + i + this.id);
					scoreDoc.value = this.data["score" + i];
					otherDoc.value = this.data["other" + i];
					var pv = this.data["physiqueIdentify" + i];
					var hv = this.data["healthGuide" + i];
					if (typeof pv == "object") {
						pv = pv.key
					}
					if (typeof hv == "object") {
						hv = hv.key
					}
					if (pv && pv != "") {
						physiqueIdentifyDoc[(parseInt(pv) - 1) + ""].checked = true;
					}
					if (hv && hv != "") {
						var hvList = (hv + "").split(",");
						for (var j = 0; j < hvList.length; j++) {
							var v = hvList[j]
							healthGuideDoc[(parseInt(v) - 1) + ""].checked = true;
							if (parseInt(v) == 6) {
								var otherF = document.getElementById("other"
										+ i + this.id);
								otherF.disabled = false;
							}
						}
					}
				}
			},

			doCreate : function() {
				this.initDataId = null;
				this.op = "create"
				if (!this.schema) {
					return
				}
				if (this.data) {
					this.data = {}
				}
				var items = this.schema.items;
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (it.id == "reportUser") {
						this.reportUser.setValue({
										'key' : this.mainApp.uid,
										'text' : this.mainApp.uname + "--"
												+ this.mainApp.jobtitle
									});
					}
					if (it.id == "reportDate") {
						this.reportDate.setValue(new Date());
					}
				}
				for (var i = 1; i < 10; i++) {
					var scoreDoc = document.getElementById("score" + i
							+ this.id);
					var otherDoc = document.getElementById("other" + i
							+ this.id);
					var physiqueIdentifyDoc = document
							.getElementsByName("physiqueIdentify" + i + this.id);
					var healthGuideDoc = document
							.getElementsByName("healthGuide" + i + this.id);
					scoreDoc.value = "";
					otherDoc.value = "";
					scoreDoc.disabled=true;
					otherDoc.disabled=true;
					for (var j = 0; j < physiqueIdentifyDoc.length; j++) {
						physiqueIdentifyDoc[j].checked = false;
					}
					for (var j = 0; j < healthGuideDoc.length; j++) {
						healthGuideDoc[j].checked = false;
					}
				}
			},
			doPrintManage : function() {
				if (!this.initDataId) {
					return
				}
				this.empiId = this.exContext.ids.empiId;
				this.phrId = this.exContext.ids.phrId;
				var url = "resources/chis.prints.template.chineseMedicineManage.print?empiId="
						+ this.empiId
						+ "&id="
						+ this.initDataId
						+ "&phrId="
						+ this.phrId;
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