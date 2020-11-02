$package("phis.application.emr.script");

$import("phis.script.SimpleModule","util.widgets.DateTimeField");

phis.application.emr.script.EMRMedicalRecordsBasicForm = function(cfg) {
	// this.showemrRootPage = true
	phis.application.emr.script.EMRMedicalRecordsBasicForm.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRMedicalRecordsBasicForm,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.test22 = "1";
				if (this.panel) {
					return this.panel;
				}

				var html = '<HTML>';
				html += '<HEAD><TITLE>基本信息</TITLE></HEAD>';
				html += '<BODY><table style="line-height:25px;left:20px;position:relative;width:840px;">';
				html += '<tr><td style="color:#0000FF;">个人信息:</td></tr>';
				html += '<tr><td style="width:80px;"><span style="left:20px;position:relative;"><span style="color:#0088A8">姓名</span></td><td><div style="float:left;"><input id="JBXX_BRXM" name="JBXX" style="width:50px;" disabled="true" type="text" />&nbsp;<span style="color:#0088A8">性别</span>&nbsp;&nbsp;<input type="radio" name="BRXB" disabled="true" value="0">0.未知的性别<input type="radio" name="BRXB" disabled="true" value="1">1.男<input type="radio" name="BRXB" disabled="true" value="2">2.女<input type="radio" name="BRXB" disabled="true" value="9">9.未说明的性别<span style="color:#0088A8">&nbsp;出生日期</span></div>&nbsp;<span><div style="float:left;" id="DIV_JBXX_CSNY"></div></span><input style="display:none;" id="JBXX_CSNY" />&nbsp;&nbsp;<span style="color:#0088A8">年龄</span>&nbsp;<input id="JBXX_BRNL" name="JBXX" style="width:30px;" type="text" /></td><td><span style="color:#0088A8">国籍</span><select id="JBXX_GJDM" name="JBXX" style="width:100px;" ></select></span></td></tr>';
			    html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000">证件类型</span></td><td><input type="radio" name="SFZJLB"  value="0">0.居民身份证<input type="radio" name="SFZJLB"  value="1">1.护照<input type="radio" name="SFZJLB"  value="2">2.港澳台居民身份证/通行证<input type="radio" name="SFZJLB"  value="4">4.旅行证据<input type="radio" name="SFZJLB"  value="9">9.其他</span></td></tr>';
     			html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000">身份证号</span></td><td><input id="JBXX_SFZJHM" name="JBXX" type="text" style="width:150px;"/>&nbsp;<span style="color:#EA0000">不详原因</span>&nbsp;<input type="radio" name="ZJBXYY" value="1">1.三无人员<input type="radio" name="ZJBXYY" value="2">2.新生儿未办理<input type="radio" name="ZJBXYY" value="3">3.无完全民事行为能力<input type="radio" name="ZJBXYY" value="4">4.意识障碍<input type="radio" name="ZJBXYY" value="9">9.其他</span></td></tr>';
 				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8">婚姻</span></td><td><input type="radio" name="HYDM" value="1">1.未婚<input type="radio" name="HYDM" value="2">2.已婚<input type="radio" name="HYDM" value="3">3.丧偶<input type="radio" name="HYDM" value="4">4.离婚<input type="radio" name="HYDM" value="9">9.其他 </td><td><span style="color:#0088A8">民族</span><select id="JBXX_MZDM" name="JBXX" style="width:100px;" ></select></span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000">出生地</span></td><td><select id="JBXX_CSD_SQS" name="JBXX" class="InputBox2" style="width:110px;" type="text" ></select>&nbsp;<select id="JBXX_CSD_S" name="JBXX" class="InputBox2" style="width:110px;" type="text" ></select>&nbsp;<select id="JBXX_CSD_X" name="JBXX" class="InputBox2" style="width:110px;" type="text"></select>&nbsp;&nbsp;<span style="color:#EA0000">籍贯</span><select id="JBXX_JGDM_SQS" name="JBXX" class="InputBox2" style="width:110px;" type="text"></select>&nbsp;<select id="JBXX_JGDM_S" name="JBXX" class="InputBox2" style="width:110px;" type="text" ></select>&nbsp;&nbsp;</td><td><span style="color:#0088A8">职业</span><select id="JBXX_ZYDM" name="JBXX" style="width:100px;" ></select></span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000">现住址</span></td><td><select id="JBXX_XZZ_SQS" name="JBXX" class="InputBox2" style="width:110px;" type="text" ></select>&nbsp;<select id="JBXX_XZZ_S" name="JBXX" class="InputBox2" style="width:110px;" type="text" /></select>&nbsp;<select id="JBXX_XZZ_X" name="JBXX" class="InputBox2" style="width:110px;" type="text"></select>&nbsp;<input id="JBXX_XZZ_DZ" name="JBXX" style="width:128px;" type="text" /><span style="color:#0088A8">电话</span><input id="JBXX_XZZ_DH" name="JBXX" style="width:100px;" type="text" /></td><td><span style="color:#0088A8">邮编</span><input id="JBXX_XZZ_YB" name="JBXX" style="width:100px;" type="text" /></span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000">户口地址</span></td><td><select id="JBXX_HKDZ_SQS" name="JBXX" class="InputBox2" style="width:110px;" type="text" ></select>&nbsp;<select id="JBXX_HKDZ_S" name="JBXX" class="InputBox2" style="width:110px;" type="text" /></select>&nbsp;<select id="JBXX_HKDZ_X" name="JBXX" class="InputBox2" style="width:110px;" type="text"></select>&nbsp;<input id="JBXX_HKDZ_DZ" name="JBXX" style="width:251px;" type="text" /></td><td><span style="color:#0088A8">邮编</span><input id="JBXX_HKDZ_YB" name="JBXX" style="width:100px;" type="text" /></span></td></tr>';
				html += '<tr><td colspan = "2"><span style="left:20px;position:relative;"><span style="color:#0088A8">工作单位及地址</span><input id="JBXX_DWDZ" name="JBXX" style="width:421px;" type="text" /><span style="color:#0088A8">单位电话</span><input id="JBXX_DWDH" name="JBXX" style="width:100px;" type="text" /></td><td><span style="color:#0088A8">邮编</span><input id="JBXX_DWYB" name="JBXX" style="width:100px;" type="text" /></span></td></tr>';
				html += '<tr><td colspan = "2"><span style="left:20px;position:relative;"><span style="color:#0088A8">联系人姓名</span><input id="JBXX_LXRXM" name="JBXX" style="width:100px;" type="text" /><span style="color:#0088A8">关系</span><select id="JBXX_LXRGX" name="JBXX" class="InputBox2" style="width:140px;" type="text" ></select><span style="color:#0088A8">地址</span><input id="JBXX_LXRDZ" name="JBXX" style="width:305px;" type="text" /></td><td><span style="color:#0088A8">电话</span><input id="JBXX_LXRDH" name="JBXX" style="width:100px;" type="text" /></span></td></tr>';
				html += '</table><table style="line-height:25px;left:20px;position:relative;width:815px;">'
				html += '<tr><td style="color:#0000FF;">入院信息:</td></tr>';
				html += '<tr><td style="width:100px"><span style="left:20px;position:relative;"><span style="color:#0088A8">医疗付费方式</span></td><td><select id="JBXX_YLFYDM" name="JBXX" class="InputBox2" style="width:200px;" type="text" /></select><span style="color:#0088A8">健康卡号</span><input id="JBXX_JMJKKH" name="JBXX" style="width:180px;" type="text" /><span style="color:#0088A8">第</span><input id="JBXX_ZYCS" name="JBXX" style="width:27px;" type="text" /><span style="color:#0088A8">次住院 </span><span style="color:#0088A8"> &nbsp;&nbsp;病案号</span><input id="JBXX_BAHM" name="JBXX" style="width:145px;" type="text" /></span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8">入院途径</span></td><td><select id="JBXX_RYTJ" name="JBXX" class="InputBox2" style="width:200px;" type="text" ></select></span></td></tr>';
				html += '</table><table style="line-height:25px;left:20px;position:relative;width:815px;">';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8">入院时间</span><div style="display:inline;" id="DIV_JBXX_RYRQ"></div><input style="display:none;" id="JBXX_RYRQ" name="JBXX" />&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:#0088A8">入院科别</span><select id="JBXX_RYKS" name="JBXX" style="width:130px;" type="text" ></select><span style="color:#0088A8">病房</span><select id="JBXX_RYBF" name="JBXX" style="width:100px;" type="text" ></select><span style="color:#0088A8">转科科别</span><input id="JBXX_ZKKSMC" name="JBXX" style="width:158px;" type="text" /></span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000">出院时间</span><div style="display:inline;" id="DIV_JBXX_CYRQ"></div><input style="display:none;" id="JBXX_CYRQ" name="JBXX" />&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:#0088A8">出院科别</span><select id="JBXX_CYKS" name="JBXX" style="width:130px;" type="text" ></select><span style="color:#0088A8">病房</span><select id="JBXX_CYBQ" name="JBXX" style="width:100px;" type="text" ></select><span style="color:#EA0000">实际住院</span><input id="JBXX_SJZYYS" name="JBXX" style="width:70px;" type="text" /><span style="color:#0088A8">天</span></span></td></tr>';
				html += '<tr><td style="color:#0000FF;">新生儿:</td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8">年龄</span><input id="JBXX_YL" name="JBXX" style="display:none;" type="text" /><input id="JBXX_YL_Y" maxLength="2" name="JBXX" style="width:30px" /><input id="JBXX_YL_FZ" maxLength="2" name="JBXX" style="width:30px" />/<input id="JBXX_YL_FM" maxLength="2" name="JBXX" style="width:30px" /><span style="color:#0088A8">月 &nbsp;&nbsp;新生儿出生体重（一孩</span><input id="JBXX_XSECSTZ" name="JBXX" style="width:50px;" type="text" /><span style="color:#0088A8">克&nbsp;&nbsp;二孩</span><input id="JBXX_XSEEHCSTZ" name="JBXX" style="width:50px;" type="text" /><span style="color:#0088A8">克） &nbsp;&nbsp;新生儿入院体重</span><input id="JBXX_XSERYTZ" name="JBXX" style="width:50px;" type="text" /><span style="color:#0088A8">克</span></span></td></tr>';
				html += '<tr><td style="color:#0000FF;">备注信息:</td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;">(一)：医疗费用方式：1.城镇职工基本医疗保险 2.城镇居民基本医疗保险 3.新型农村合作医疗 4.贫困救</span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;">&nbsp;助 5.商业医疗保险 6.全公费 7.全公费 8.其他社会保险 9.其他</span></td></tr></table></BODY></HTML>';
				html += '<tr><td><span style="left:20px;position:relative;color:#EA0000;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(二)：红色字体为必填</span></td></tr></table></BODY></HTML>';
				var panel = new Ext.Panel({
							// border : false,
							// hideBorders : true,
							frame : false,
							autoScroll : true,
							// bodyStyle : 'span{color:#0088A8}',
							html : html
						});

				this.panel = panel
				this.panel.on("afterrender", this.onReady, this)
				// 取EMRView系统参数
				// this.loadSystemParams();
				// alert(Ext.get("BRXM"));
				return panel
			},
			setRadioValue : function(name, value) {
				var radioObj = document.getElementsByName(name);
				for (var i = 0; i < radioObj.length; i++) {
					if (radioObj[i].value == value) {
						radioObj[i].checked = true;
					}
					if (!this.exContext.SXQX) {
						radioObj[i].disabled = true;
					} else {
						var this_ = this;
						radioObj[i].onchange = function() {
							this_.opener.XGYZ = true;
						}
					}
				}
			},
			getRadioValue : function(name) {
				var radioObj = document.getElementsByName(name);
				for (var i = 0; i < radioObj.length; i++) {
					if (radioObj[i].checked) {
						return radioObj[i].value;
					}
				}
			},
			onReady : function() {
				this.initData(1)
				var csny = this.exContext.BRXX.CSNY;
				this.CSNY = new Ext.form.DateField({
							emptyText : "请选择日期",
							renderTo : 'DIV_JBXX_CSNY',
							format : 'Y-m-d',
							allowBlank : true,
							value : csny,
							width : 130
						})
				if(window.attachEvent){
					document.getElementById('DIV_JBXX_CSNY').firstChild.lastChild.style.top = "1px";
				}else{
					document.getElementById('DIV_JBXX_CSNY').firstChild.lastChild.style.top = "-2px";
				}
				var ryrq = this.exContext.BRXX.RYRQ;
				this.RYRQ = new util.widgets.DateTimeField({
							id : 'EXT_RYRQ',
							xtype : 'datetimefield',
							value : ryrq,
							width : 200,
							renderTo : 'DIV_JBXX_RYRQ',
							allowBlank : true,
							altFormats : 'Y-m-d H:i:s',
							format : 'Y-m-d H:i:s'
						})
				document.getElementById('DIV_JBXX_RYRQ').firstChild.style.display = "inline";
				if(window.attachEvent){
					document.getElementById('DIV_JBXX_RYRQ').firstChild.lastChild.style.top = "1px";
				}else{
					document.getElementById('DIV_JBXX_RYRQ').firstChild.lastChild.style.top = "-2px";
				}
				var cyrq = this.exContext.BRXX.CYRQ;
				this.CYRQ = new util.widgets.DateTimeField({
							id : 'EXT_CYRQ',
							xtype : 'datetimefield',
							value : cyrq,
							width : 200,
							renderTo : 'DIV_JBXX_CYRQ',
							allowBlank : true,
							altFormats : 'Y-m-d H:i:s',
							format : 'Y-m-d H:i:s'
						})
				document.getElementById('DIV_JBXX_CYRQ').firstChild.style.display = "inline";
				if(window.attachEvent){
					document.getElementById('DIV_JBXX_CYRQ').firstChild.lastChild.style.top = "1px";
				}else{
					document.getElementById('DIV_JBXX_CYRQ').firstChild.lastChild.style.top = "-2px";
				}
				if (!this.exContext.SXQX) {
					this.CSNY.setDisabled(true);
					this.RYRQ.setDisabled(true);
					this.CYRQ.setDisabled(true);
					document.getElementById("JBXX_YL_Y").disabled = true;
					document.getElementById("JBXX_YL_FZ").disabled = true;
					document.getElementById("JBXX_YL_FM").disabled = true;
				} else {
					this.SFZJHM = document.getElementById("JBXX_SFZJHM");
					var this_ = this;
					this.SFZJHM.onblur=function(){
						var res = this_.opener.getInfo(this.value);
						if (res.length != 0) {
							this_.setRadioValue("BRXB",res[1]);
							this_.CSNY.setValue(res[0]);
							this_.CSNY.disable()
						}else{
							this_.CSNY.enable();
						}
					}
					this.CSNY.on("change", function() {
								this.opener.XGYZ = true
							}, this)
					this.RYRQ.on("change", function() {
								this.opener.XGYZ = true
								var RYRQ = this.RYRQ.getValue();
								var CYRQ = this.CYRQ.getValue();
								if(RYRQ!=""&&this.RYRQ.validateValue()&&this.CYRQ.validateValue()&&RYRQ>CYRQ){
									MyMessageTip.msg("提示", "入院日期不能大于出院日期！", true);
								}
							}, this)
					this.CYRQ.on("change", function() {
								this.opener.XGYZ = true
								var RYRQ = this.RYRQ.getValue();
								var CYRQ = this.CYRQ.getValue();
								if(CYRQ!=""&&this.RYRQ.validateValue()&&this.CYRQ.validateValue()&&RYRQ>CYRQ){
									MyMessageTip.msg("提示", "出院日期不能小于入院日期！", true);
								}
								document.getElementById("JBXX_SJZYYS").value=parseInt(Math.abs(this.RYRQ.getValue()-this.CYRQ.getValue())/1000/60/60/24);
							}, this)
				}
				if(document.getElementById("JBXX_SJZYYS").value==""){
					document.getElementById("JBXX_SJZYYS").value=parseInt(Math.abs(this.RYRQ.getValue()-this.CYRQ.getValue())/1000/60/60/24);
				}
				if (this.exContext.BRXX.YL_Y) {
					document.getElementById("JBXX_YL_Y").value = this.exContext.BRXX.YL_Y;
					document.getElementById("JBXX_YL_FZ").value = this.exContext.BRXX.YL_FZ;
					document.getElementById("JBXX_YL_FM").value = this.exContext.BRXX.YL_FM;
				}
				if (this.exContext.SXQX) {
					document.getElementById("JBXX_YL_Y").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						var Y = document.getElementById("JBXX_YL_Y").value;
						var FZ = document.getElementById("JBXX_YL_FZ").value;
						var FM = document.getElementById("JBXX_YL_FM").value;
						if(!Y){
							Y = " ";
						}
						if(!FZ){
							FZ = " ";
						}
						if(!FM){
							FM = " ";
						}
						document.getElementById("JBXX_YL").value = Y + "," + FZ + "/"
								+ FM
					}
					document.getElementById("JBXX_YL_FZ").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						var Y = document.getElementById("JBXX_YL_Y").value;
						var FZ = document.getElementById("JBXX_YL_FZ").value;
						var FM = document.getElementById("JBXX_YL_FM").value;
						if(!Y){
							Y = " ";
						}
						if(!FZ){
							FZ = " ";
						}
						if(!FM){
							FM = " ";
						}
						document.getElementById("JBXX_YL").value = Y + "," + FZ + "/"
								+ FM
					}
					document.getElementById("JBXX_YL_FM").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						var Y = document.getElementById("JBXX_YL_Y").value;
						var FZ = document.getElementById("JBXX_YL_FZ").value;
						var FM = document.getElementById("JBXX_YL_FM").value;
						if(!Y){
							Y = " ";
						}
						if(!FZ){
							FZ = " ";
						}
						if(!FM){
							FM = " ";
						}
						document.getElementById("JBXX_YL").value = Y + "," + FZ + "/"
								+ FM
					}
				}
			},
			getMySchema : function(entryName) {
				var schema = this.opener.schema[entryName]
				if (!schema) {
					var re = util.schema.loadSync(entryName)
					if (re.code == 200) {
						schema = re.schema;
						if (this.opener.schema) {
							this.opener.schema[entryName] = schema;
						} else {
							this.opener.schema = {}
							this.opener.schema[entryName] = schema;
						}
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				return schema;
			},
			initData : function(dic) {
				var this_ = this;
				var schema = this.getMySchema(this.entryName);
				var items = schema.items
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if (it.layout && it.layout.indexOf("JBXX") >= 0) {
						var obj = document.getElementById("JBXX_" + it.id);
						if (obj) {

							if (this.exContext.BRXX[it.id]
									|| this.exContext.BRXX[it.id] == "0") {
								obj.value = this.exContext.BRXX[it.id];
							}
							if (it.length) {
								obj.maxLength = it.length;
							}
							if (!this.exContext.SXQX) {
								obj.disabled = true;
							} else {
								if (it.type == "int") {
									obj.onchange = function() {
										this_.opener.XGYZ = true;
										if (this.value) {
											if (isNaN(this.value)) {
												this.value = 0;
												MyMessageTip.msg("提示",
														"请正确输入！", true);
											}
										}
									}
								} else {
									obj.onchange = function() {
										this_.opener.XGYZ = true;
									}
								}
							}
						}
						if (it.dic && dic) {
							if (it.id != "HKDZ_X" && it.id != "XZZ_X"
									&& it.id != "CSD_X" && it.id != "HKDZ_S"
									&& it.id != "XZZ_S" && it.id != "JGDM_S"
									&& it.id != "CSD_S") {
								this.getStore(it.dic, "JBXX_" + it.id,
										it.defaultValue)// 关系
							}
						}
						if (it.layout.indexOf("radio") >= 0) {
							this.setRadioValue(it.id,
									this.exContext.BRXX[it.id]);
						}
					}
				}
			},
			ssxChange : function(id, dicId, superkey) {
				if (!superkey) {
					this.getSSXStore({
								id : dicId,
								filter : "['eq',['$','item.properties.superkey'],['s','0']]"
							}, id)
				} else {
					this.getSSXStore({
								id : dicId,
								filter : "['eq',['$','item.properties.superkey'],['s',"
										+ superkey + "]]"
							}, id)// 省
				}
			},
			getSSXStore : function(dic, fieldId) {
				var this_ = this;
				var url = this.getUrl(dic)
				var fields = null;
				if (dic.fields) {
					dic.fields = dic.fields + "";
					fields = dic.fields.split(",")
				}
				var store = new Ext.data.JsonStore({
							totalProperty : 'result',
							url : url,
							root : 'items',
							fields : fields
									|| ['key', 'text',
											dic.searchField || "mCode"]
						})
				store.on('load', function() {
					if (this.getCount() == 0) {
						return;
					}
					document.getElementById(fieldId).options.length = 0;
					document.getElementById(fieldId).options.add(new Option(
							"--请选择--", 0));
					for (var i = 0; i < this.reader.jsonData.items.length; i++) {
						var key = this.reader.jsonData.items[i].key;
						var text = this.reader.jsonData.items[i].text;
						document.getElementById(fieldId).options
								.add(new Option(text, key));
					}
					if (this_.exContext.BRXX[fieldId.substring(5)]) {
						document.getElementById(fieldId).value = this_.exContext.BRXX[fieldId
								.substring(5)];
						if (fieldId.indexOf('_S') > 0) {
							this_.ssxChange(fieldId.replace(/_S/, "_X"),
									'phis.dictionary.County',
									document.getElementById(fieldId).value);
						}
					}
					if (fieldId.indexOf('_S') > 0) {
						if (document
								.getElementById(fieldId.replace(/_S/, "_X"))) {
							document
									.getElementById(fieldId.replace(/_S/, "_X")).options.length = 0;
							document
									.getElementById(fieldId.replace(/_S/, "_X")).options
									.add(new Option("--请选择--", 0));
						}
						document.getElementById(fieldId).onchange = function() {
							if (document.getElementById(fieldId.replace(/_S/,
									"_X"))) {
								this_.ssxChange(fieldId.replace(/_S/, "_X"),
										'phis.dictionary.County', document
												.getElementById(fieldId).value);
							}
							this_.opener.XGYZ = true;
						}
					}
				})
				store.load();
			},
			getStore : function(dic, fieldId, value) {
				var this_ = this;
				var url = this.getUrl(dic)
				// add by yangl 请求统一加前缀
				if (this.requestData) {
					this.requestData.serviceId = 'phis.'
							+ this.requestData.serviceId
				}
//				var fields = null;
//				if (dic.fields) {
//					dic.fields = dic.fields + "";
//					fields = dic.fields.split(",")
//				}
				var proxy = new util.dictionary.HttpProxy({   //new Ext.data.HttpProxy
					method:"GET",	//make cache,upper
					url:url
				});
				var store = new Ext.data.JsonStore({
					proxy:proxy,
					totalProperty:'result',
					root:'items',
					fields: dic.fields || ['key','text',dic.searchField || "mCode"]
				})
//				var store = new Ext.data.JsonStore({
//							totalProperty : 'result',
//							url : url,
//							root : 'items',
//							fields : fields
//									|| ['key', 'text',
//											dic.searchField || "mCode"]
//						})
				store.on('load', function() {
					if (this.getCount() == 0) {
						return;
					}
					document.getElementById(fieldId).options.add(new Option(
							"--请选择--", 0));
					for (var i = 0; i < this.reader.jsonData.items.length; i++) {

						var key = this.reader.jsonData.items[i].key;
						var text = this.reader.jsonData.items[i].text;
						document.getElementById(fieldId).options
								.add(new Option(text, key));
					}
					
					if (fieldId.indexOf('_SQS') > 0) {
						document.getElementById(fieldId).onchange = function() {
							this_.ssxChange(fieldId.replace(/_SQS/, "_S"),
									'phis.dictionary.City',
									document.getElementById(fieldId).value);
							this_.opener.XGYZ = true;
						}
					}
					if (value) {
						document.getElementById(fieldId).value = value.key;
					}
					if (this_.exContext.BRXX[fieldId.substring(5)]) {
						document.getElementById(fieldId).value = this_.exContext.BRXX[fieldId
								.substring(5)];
						if (fieldId.indexOf('_SQS') > 0) {
							this_.ssxChange(fieldId.replace(/_SQS/, "_S"),
									'phis.dictionary.City',
									document.getElementById(fieldId).value);
						}
					}
				})
				store.load();
			},
			getUrl : function(dic) {
				var url = ClassLoader.serverAppUrl || ""
				url += dic.id + ".dic"
				if (dic.parentKey) {
					url += "?parentKey=" + dic.parentKey;
				}
				if (dic.slice) {
					if (url.indexOf("?") > -1) {
						url += "&sliceType=" + dic.slice
					} else {
						url += "?sliceType=" + dic.slice
					}
				}
				if (dic.src) {
					if (url.indexOf("?") > -1) {
						url += "&src=" + dic.src
					} else {
						url += "?src=" + dic.src
					}
				}
				if (dic.filter) {
					if (url.indexOf("?") > -1) {
						url += "&filter=" + dic.filter;
					} else {
						url += "?filter=" + dic.filter;
					}
				}
				url = encodeURI(url);
				return url
			}
		})