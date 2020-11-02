$package("phis.application.emr.script");

$import("phis.script.SimpleModule");

phis.application.emr.script.EMRMedicalRecordsAdditionalForm = function(cfg) {
	// this.showemrRootPage = true
	phis.application.emr.script.EMRMedicalRecordsAdditionalForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.emr.script.EMRMedicalRecordsAdditionalForm,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var html = '<HTML>';
				html += '<BODY><table style="line-height:25px;left:20px;position:relative;width:815px;">';
				html += '<tr><td style="color:#0000FF;">附加项目:</td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">诊断符合情况-门诊和出院：</span><input type="radio" name="ZDFH_MZZY" value="0">0.未做&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_MZZY" value="1">1.符合&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_MZZY" value="2">2.不符合&nbsp;&nbsp;<input type="radio" name="ZDFH_MZZY" value="3">3.不确定&nbsp;&nbsp;</span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">诊断符合情况-入院和出院：</span><input type="radio" name="ZDFH_RYCY" value="0">0.未做&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_RYCY" value="1">1.符合&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_RYCY" value="2">2.不符合&nbsp;&nbsp;<input type="radio" name="ZDFH_RYCY" value="3">3.不确定&nbsp;&nbsp;</span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">诊断符合情况-术前和术后：</span><input type="radio" name="ZDFH_SQSH" value="0">0.未做&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_SQSH" value="1">1.符合&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_SQSH" value="2">2.不符合&nbsp;&nbsp;<input type="radio" name="ZDFH_SQSH" value="3">3.不确定&nbsp;&nbsp;</span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">诊断符合情况-临床和病理：</span><input type="radio" name="ZDFH_LCBL" value="0">0.未做&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_LCBL" value="1">1.符合&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_LCBL" value="2">2.不符合&nbsp;&nbsp;<input type="radio" name="ZDFH_LCBL" value="3">3.不确定&nbsp;&nbsp;</span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">诊断符合情况-放射和病理：</span><input type="radio" name="ZDFH_FSBL" value="0">0.未做&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_FSBL" value="1">1.符合&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="ZDFH_FSBL" value="2">2.不符合&nbsp;&nbsp;<input type="radio" name="ZDFH_FSBL" value="3">3.不确定&nbsp;&nbsp;</span></td></tr>';
				html += '</table><table style="line-height:25px;left:20px;position:relative;width:815px;">';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000;">抢救情况-抢救次数：</span><input id="FJXM_QJCS" class="InputBox" style="width:100px;" type="text" /></span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000;">抢救情况-成功次数：</span><input id="FJXM_CGCS" class="InputBox" style="width:100px;" type="text" /></span></td></tr>';
				html += '</table><table style="line-height:25px;left:20px;position:relative;width:815px;">';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">临床路径管理：</span><input type="radio" name="LCLJBZ" value="1">1.完成&nbsp;&nbsp;<input type="radio" name="LCLJBZ" value="2">2.变异' + 
						'&nbsp;&nbsp;<input type="radio" name="LCLJBZ" value="3">3.退出&nbsp;&nbsp;<input type="radio" name="LCLJBZ" value="4">4.未入</span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">危重病例</span>&nbsp;&nbsp;&nbsp;<input type="radio" name="WZBL" value="0">0.否&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="WZBL" value="1">1.是&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:#0088A8">疑难病例&nbsp;&nbsp;</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="YNBL" value="0">0.否&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="YNBL" value="1">1.是&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:#0088A8">MDT病例&nbsp;&nbsp;</span>&nbsp;&nbsp;&nbsp;<input type="radio" name="MDTBL" value="0">0.否&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="MDTBL" value="1">1.是&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">单病种病例</span><input type="radio" name="DBZBL" value="0">0.否&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="DBZBL" value="1">1.是&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:#0088A8">日间手术病例&nbsp;&nbsp;</span><input type="radio" name="RJSSBL" value="0">0.否&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="RJSSBL" value="1">1.是&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:#0088A8">教学查房病例&nbsp;&nbsp;</span><input type="radio" name="JXBL" value="0">0.否&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="JXBL" value="1">1.是&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">手术类别：</span><input type="radio" name="SSLB" value="1">1.择期手术&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="SSLB" value="2">2.急诊手术</span></td></tr>';
			//	html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8">重症监护名称</span><select id="FJXM_ZZJH" name="FJXM"  class="InputBox2" style="width:130px;" type="text" ></select><span style="color:#0088A8">进入重症监护时间</span><div style="display:inline;" id="DIV_FJXM_JRZZJHSJ"></div><input style="display:none;" id="FJXM_JRZZJHSJ" name="FJXM" />&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:#0088A8">转出重症监护时间</span><div style="display:inline;" id="DIV_FJXM_ZCZZJHSJ"></div><input style="display:none;" id="FJXM_ZCZZJHSJ" name="FJXM" /></span></td></tr>';
			//	html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8">重症监护名称</span><select id="FJXM_ZZJH" name="FJXM" class="InputBox2" style="width:200px;" type="text" /></select></span></td></tr>';
				html += '</table></BODY></HTML>';
				var panel = new Ext.Panel({
							frame : false,
							autoScroll : true,
							html : html
						});
				this.panel = panel
				this.panel.on("afterrender", this.onReady, this)
				return panel
			},
			onReady : function(){
				this.initData(1);
			},
			getMySchema : function(entryName){
				var schema = this.opener.schema[entryName]
				if (!schema) {
					var re = util.schema.loadSync(entryName)
					if (re.code == 200) {
						schema = re.schema;
						if(this.opener.schema){
							this.opener.schema[entryName] = schema;
						}else{
							this.opener.schema={}
							this.opener.schema[entryName] = schema;
						}
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				return schema;
			},
			
			initData : function(dic){
				var this_ = this;
				var schema = this.getMySchema(this.entryName);
				var items = schema.items
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if (it.layout&&it.layout.indexOf("FJXM")>=0){
						var obj = document.getElementById("FJXM_" + it.id);
						if (obj) {
							if (this.exContext.BRXX[it.id]||this.exContext.BRXX[it.id]=="0") {
								obj.value = this.exContext.BRXX[it.id];
							}
							if(it.length){
								obj.maxLength=it.length;
							}
							if(!this.exContext.SXQX){
								obj.disabled = true;
							}else{
								if(it.type=="int"){
									obj.onchange = function() {
										this_.opener.XGYZ = true;
										if(this.value){
											if(isNaN(this.value)){
												this.value = 0;
												MyMessageTip.msg("提示", "请正确输入！", true);
											}
										}
									}
								}else{
									obj.onchange = function() {
										this_.opener.XGYZ = true;
									}
								}
							}
						}
						if (it.dic&&dic) {
							
							if (it.id != "ZZJH" 
									) {
								this.getStore(it.dic, "FJXM_" + it.id,
										it.defaultValue)// 关系
							}
						
						//	this.getStore(it.dic, "FJXM_" + it.id,
									//	it.defaultValue)// 关系
						}
						
						if(it.layout.indexOf("radio")>=0){
							this.setRadioValue(it.id, this.exContext.BRXX[it.id]);
						}
					}
				}
			},
			
			setRadioValue : function(name, value) {
				var radioObj = document.getElementsByName(name);
				for (var i = 0; i < radioObj.length; i++) {
					if (radioObj[i].value == value) {
						radioObj[i].checked = true;
					}
					if(!this.exContext.SXQX){
						radioObj[i].disabled = true;
					}else{
						var this_ = this;
						radioObj[i].onchange = function() {
							this_.opener.XGYZ = true;
						}
					}
				}
			}
	 /**onReady : function() {
				this.initData(1)
				var JRZZJHSJ = this.exContext.BRXX.RYRQ;
				this.JRZZJHSJ = new util.widgets.DateTimeField({
							id : 'EXT_JRZZJHSJ',
							xtype : 'datetimefield',
							value : JRZZJHSJ,
							width : 200,
							renderTo : 'DIV_FJXM_JRZZJHSJ',
							allowBlank : true,
							altFormats : 'Y-m-d H:i:s',
							format : 'Y-m-d H:i:s'
						})
				document.getElementById('DIV_FJXM_JRZZJHSJ').firstChild.style.display = "inline";
				if(window.attachEvent){
					document.getElementById('DIV_FJXM_JRZZJHSJ').firstChild.lastChild.style.top = "1px";
				}else{
					document.getElementById('DIV_FJXM_JRZZJHSJ').firstChild.lastChild.style.top = "-2px";
				}
				var zczzjhsj = this.exContext.BRXX.CYRQ;
				this.ZCZZJHSJ = new util.widgets.DateTimeField({
							id : 'EXT_ZCZZJHSJ',
							xtype : 'datetimefield',
							value : zczzjhsj,
							width : 200,
							renderTo : 'DIV_FJXM_ZCZZJHSJ',
							allowBlank : true,
							altFormats : 'Y-m-d H:i:s',
							format : 'Y-m-d H:i:s'
						})
				document.getElementById('DIV_FJXM_ZCZZJHSJ').firstChild.style.display = "inline";
				if(window.attachEvent){
					document.getElementById('DIV_FJXM_ZCZZJHSJ').firstChild.lastChild.style.top = "1px";
				}else{
					document.getElementById('DIV_FJXM_ZCZZJHSJ').firstChild.lastChild.style.top = "-2px";
				}
				if (!this.exContext.SXQX) {
					
					this.JRZZJHSJ.setDisabled(true);
					this.ZCZZJHSJ.setDisabled(true);
				} else {
					var this_ = this;
					}
					this.JRZZJHSJ.on("change", function() {
								this.opener.XGYZ = true
								var JRZZJHSJ = this.JRZZJHSJ.getValue();
								var ZCZZJHSJ = this.ZCZZJHSJ.getValue();
								if(JRZZJHSJ!=""&&this.JRZZJHSJ.validateValue()&&this.ZCZZJHSJ.validateValue()&&JRZZJHSJ>ZCZZJHSJ){
									MyMessageTip.msg("提示", "进入时间日期不能大于转出时间日期！", true);
								}
							}, this)
					this.ZCZZJHSJ.on("change", function() {
								this.opener.XGYZ = true
								var JRZZJHSJ = this.JRZZJHSJ.getValue();
								var ZCZZJHSJ = this.ZCZZJHSJ.getValue();
								if(ZCZZJHSJ!=""&&this.JRZZJHSJ.validateValue()&&this.ZCZZJHSJ.validateValue()&&JRZZJHSJ>ZCZZJHSJ){
									MyMessageTip.msg("提示", "转出时间日期不能小于进入时间日期！", true);
								}
								//document.getElementById("FJXM_SJZYYS").value=parseInt(Math.abs(this.JRZZJHSJ.getValue()-this.ZCZZJHSJ.getValue())/1000/60/60/24);
							}, this)
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
			}**/
	} )