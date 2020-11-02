$package("phis.application.emr.script");

$import("phis.script.SimpleModule");

phis.application.emr.script.EMRMedicalRecordsSignatureForm = function(cfg) {
	// this.showemrRootPage = true
	phis.application.emr.script.EMRMedicalRecordsSignatureForm.superclass.constructor
			.apply(this, [cfg]);
}
//var this_ = this;
Ext.extend(phis.application.emr.script.EMRMedicalRecordsSignatureForm,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var html = '<HTML>';
				html += '<BODY><table style="line-height:25px;left:20px;position:relative;width:815px;">';
				html += '<tr><td style="color:#0000FF;">其他信息:</td></tr>';
//				<dl style="line-height:25px;">';
//				html += '<dt style="color:#0000FF;left:20px;position:relative;">其他信息:</dt>';
//				html += '<dd style="color:#0000FF;left:40px;position:relative;"><table style="line-height:25px;"><tr><td><span style="color:#0088A8;">病理号</span></td><td><input id="YSQM_BLH" name="YSQM" class="InputBox" style="width:150px;" type="text" /></td></tr>';
//				html += '<tr><td><span style="color:#0088A8;">药物过敏 </span></td><td><input type="radio" name="GMYWBZ" value="1">1.无&nbsp;&nbsp;<input type="radio" name="GMYWBZ" value="2">2.有&nbsp;&nbsp;<span style="color:#0088A8;">过敏药物：</span><input id="YSQM_GMYWMC" name="YSQM" class="InputBox" style="width:230px;" type="text" /><span style="color:#0088A8;">死亡患者尸检：</span><input type="radio" name="SJBZ" value="1">1.是&nbsp;&nbsp;<input type="radio" name="SJBZ" value="2">2.否&nbsp;&nbsp;</td></tr>';
//				html += '<tr><td><span style="color:#0088A8;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rh&nbsp;&nbsp;&nbsp;&nbsp;</span></td><td><input type="radio" name="RHXXDM" value="1">1.阴&nbsp;&nbsp;<input type="radio" name="RHXXDM" value="2">2.阳&nbsp;&nbsp;<input type="radio" name="RHXXDM" value="3">3.不详<input type="radio" name="RHXXDM" value="4">4.未查</td></tr>';
//				html += '<tr><td><span style="color:#0088A8;">&nbsp;&nbsp;&nbsp;血型&nbsp;&nbsp;&nbsp;&nbsp;</span></td><td><input type="radio" name="ABOXXDM" value="1">1.A&nbsp;&nbsp;&nbsp;<input type="radio" name="ABOXXDM" value="2">2.B&nbsp;&nbsp;&nbsp;<input type="radio" name="ABOXXDM" value="3">3.O&nbsp;&nbsp;&nbsp;<input type="radio" name="ABOXXDM" value="4">4.AB&nbsp;&nbsp;<input type="radio" name="ABOXXDM" value="5">5.不详<input type="radio" name="ABOXXDM" value="6">6.未查</td></tr></table></dd>';
//				html += '<dd style="left:40px;position:relative;"><span style="color:#0088A8;">是否有出院31天内再住院计划</span><input type="radio" name="CY31ZYBZ" value="1">1.无&nbsp;&nbsp;<input type="radio" name="CY31ZYBZ" value="2">2.有&nbsp;&nbsp;<span style="color:#0088A8;">目的：</span><input id="YSQM_CY31ZYMD" name="YSQM" class="InputBox" style="width:340px;" type="text" /></dd>';
//				html += '<dd style="left:40px;position:relative;"><span style="color:#0088A8;">离院方式</span><select id="YSQM_LYFS" name="YSQM" class="InputBox" style="width:250px;"></select><span style="color:#0088A8;">拟接受医疗机构名称：</span><input id="YSQM_NJSYLJLMC" name="YSQM" class="InputBox" style="width:200px;" type="text" /></dd>';
//				html += '<dd style="left:40px;position:relative;"><span style="color:#0088A8;">颅脑损伤患者昏迷时间：入院前</span><input id="YSQM_RYQHMSJ" value="0" style="display:none;"/><input id="YSQM_RYQHMSJ_T" name="YSQM" maxLength="3" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">天</span><input id="YSQM_RYQHMSJ_S" maxLength="2" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">小时</span><input id="YSQM_RYQHMSJ_F" maxLength="2" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">分钟&nbsp;&nbsp;&nbsp;&nbsp;入院后</span><input id="YSQM_RYHHMSJ" value="0" style="display:none;"/><input id="YSQM_RYHHMSJ_T" maxLength="3" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">天</span><input id="YSQM_RYHHMSJ_S" maxLength="2" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">小时</span><input id="YSQM_RYHHMSJ_F" maxLength="2" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">分钟</span></dd>';
//				html += '<dt style="color:#0000FF;left:20px;position:relative;">签名栏:</dt>';
//				html += '<dd style="left:40px;position:relative;"><table style="line-height:25px;"><tr><td><span style="color:#0088A8;">科主任</span></td><td><select id="YSQM_KZRQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">主任(副主任)医师</span></td><td><select id="YSQM_ZRYSQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">主治医师</span></td><td><select id="YSQM_ZZYSQM" name="YSQM" style="width:100px;"></select></td><td><span style="color:#0088A8;">住院医师</span></td><td><select id="YSQM_ZYYSQM" name="YSQM" style="width:100px;"></select></td><tr>';
//				html += '<tr><td><span style="color:#0088A8;">责任护士</span></td><td><select id="YSQM_ZRHSQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">进修医生</span></td><td><select id="YSQM_JXYSQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">实习医师</span></td><td><select id="YSQM_SXYSQM" name="YSQM" style="width:100px;"></select></td><td><span style="color:#0088A8;">编码员</span></td><td><select id="YSQM_BABMYQM" name="YSQM" style="width:100px;"></select></td></tr></table></dd>';
//				html += '<dt style="color:#0000FF;left:20px;position:relative;">质量控制:</dt>'; 
//				html += '<dd style="color:#0000FF;left:40px;position:relative;"><table style="line-height:25px;"><tr><td><span style="color:#0088A8;">病案质量</span></td><td colspan = "2"><input type="radio" name="BAZL" value="1">1.甲<input type="radio" name="BAZL" value="1">2.乙<input type="radio" name="BAZL" value="1">3.丙<span style="color:#0088A8;"> 质控医师</span></td><td><select id="YSQM_ZKYSQM" name="YSQM" style="width:100px;"></select></td><td><span style="color:#0088A8;"> 质控护士</span></td><td><select id="YSQM_ZKHSQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">质控日期</span></td><td><div id="DIV_YSQM_ZKRQ" style="display:inline;"></div><input id="YSQM_ZKRQ" style="display:none;"/></td></tr></table></dd>';
//				html += '</dl></BODY></HTML>';
				html += '<tr><td style="width:80px;"><span style="left:20px;position:relative;"><span style="color:#0088A8">病理号</span></td><td><input id="YSQM_BLH" name="YSQM" class="InputBox" style="width:150px;" type="text" /></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">药物过敏 </span></td><td><input type="radio" name="GMYWBZ" value="1">1.无&nbsp;&nbsp;<input type="radio" name="GMYWBZ" value="2">2.有&nbsp;&nbsp;<span style="color:#0088A8;">过敏药物：</span><input id="YSQM_GMYWMC" name="YSQM" class="InputBox" style="width:230px;" type="text" /><span style="color:#0088A8;">死亡患者尸检：</span><input type="radio" name="SJBZ" value="1">1.是&nbsp;&nbsp;<input type="radio" name="SJBZ" value="2">2.否&nbsp;&nbsp;</span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rh&nbsp;&nbsp;&nbsp;&nbsp;</span></td><td><input type="radio" name="RHXXDM" value="1">1.阴&nbsp;&nbsp;<input type="radio" name="RHXXDM" value="2">2.阳&nbsp;&nbsp;<input type="radio" name="RHXXDM" value="3">3.不详<input type="radio" name="RHXXDM" value="4">4.未查</span></td></tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000;">&nbsp;&nbsp;&nbsp;血型&nbsp;&nbsp;&nbsp;&nbsp;</span></td><td ><input type="radio" name="ABOXXDM" value="1">1.A&nbsp;&nbsp;&nbsp;<input type="radio" name="ABOXXDM" value="2">2.B&nbsp;&nbsp;&nbsp;<input type="radio" name="ABOXXDM" value="3">3.O&nbsp;&nbsp;&nbsp;<input type="radio" name="ABOXXDM" value="4">4.AB&nbsp;&nbsp;<input type="radio" name="ABOXXDM" value="5">5.不详<input type="radio" name="ABOXXDM" value="6">6.未查</span></td></tr></table>';
				html += '<table style="line-height:25px;left:20px;position:relative;width:1015px;">';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8">是否有出院31天内再住院计划</span></td><td><input type="radio" name="CY31ZYBZ" value="1">1.无&nbsp;&nbsp;<input type="radio" name="CY31ZYBZ" value="2">2.有&nbsp;&nbsp;<span style="color:#0088A8;">目的：</span><input id="YSQM_CY31ZYMD" name="YSQM" class="InputBox" style="width:340px;" type="text" /></span></td></tr>';
				html += '</table><table style="line-height:25px;left:20px;position:relative;width:1015px;">';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#EA0000;">离院方式</span><select id="YSQM_LYFS" name="YSQM" class="InputBox" style="width:250px;"></select><span style="color:#0088A8;">拟接受医疗机构名称：</span><input id="YSQM_NJSYLJLMC" name="YSQM" class="InputBox" style="width:200px;" type="text" /></span></td></tr>';
				html += '</table><table style="line-height:25px;left:20px;position:relative;width:1015px;">';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">颅脑损伤患者昏迷时间：入院前</span><input id="YSQM_RYQHMSJ" value="0" style="display:none;"/><input id="YSQM_RYQHMSJ_T" name="YSQM" maxLength="3" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">天</span><input id="YSQM_RYQHMSJ_S" maxLength="2" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">小时</span><input id="YSQM_RYQHMSJ_F" maxLength="2" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">分钟&nbsp;&nbsp;&nbsp;&nbsp;入院后</span><input id="YSQM_RYHHMSJ" value="0" style="display:none;"/><input id="YSQM_RYHHMSJ_T" maxLength="3" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">天</span><input id="YSQM_RYHHMSJ_S" maxLength="2" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">小时</span><input id="YSQM_RYHHMSJ_F" maxLength="2" name="YSQM" class="InputBox" style="width:45px;" type="text" /><span style="color:#0088A8;">分钟</span></span></td></tr>';
				html += '<tr><td style="color:#0000FF;">签名栏:</td></tr></table>';
				html += '<table style="line-height:25px;left:20px;position:relative;width:815px;">';
				html += '<tr><td style="width:80px;"><span style="left:20px;position:relative;"><span style="color:#0088A8;">科主任</span></td><td><select id="YSQM_KZRQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">主任(副主任)医师</span></td><td><select id="YSQM_ZRYSQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">主治医师</span></td><td><select id="YSQM_ZZYSQM" name="YSQM" style="width:100px;"></select></td><td><span style="color:#0088A8;">住院医师</span></td><td><select id="YSQM_ZYYSQM" name="YSQM" style="width:100px;"></select></span></td><tr>';
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">责任护士</span></td><td><select id="YSQM_ZRHSQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">进修医生</span></td><td><select id="YSQM_JXYSQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">实习医师</span></td><td><select id="YSQM_SXYSQM" name="YSQM" style="width:100px;"></select></td><td><span style="color:#0088A8;">编码员</span></td><td><select id="YSQM_BABMYQM" name="YSQM" style="width:100px;"></select></span></td></tr></table>';
				html += '<table style="line-height:25px;left:20px;position:relative;width:815px;">';
				html += '<tr><td style="color:#0000FF;">质量控制:</td></tr>'; 
				html += '<tr><td><span style="left:20px;position:relative;"><span style="color:#0088A8;">病案质量</span></td><td colspan = "2"><input type="radio" name="BAZL" value="1">1.甲<input type="radio" name="BAZL" value="2">2.乙<input type="radio" name="BAZL" value="3">3.丙<span style="color:#0088A8;"> 质控医师</span></td><td><select id="YSQM_ZKYSQM" name="YSQM" style="width:100px;"></select></td><td><span style="color:#0088A8;"> 质控护士</span></td><td><select id="YSQM_ZKHSQM" name="YSQM" style="width:100px;" ></select></td><td><span style="color:#0088A8;">质控日期</span></td><td><div id="DIV_YSQM_ZKRQ" style="display:inline;"></div><input id="YSQM_ZKRQ" style="display:none;"/></span></td></tr></table>';
				html += '</BODY></HTML>';
				var panel = new Ext.Panel({
							frame : false,
							autoScroll : true,
							html : html
						});
				this.panel = panel
				this.panel.on("afterrender", this.onReady, this)
				this.qmys = {
					"KZRQM" : "科主任",
					"ZRYSQM" : "主任（副主任）医师",
					"ZZYSQM" : "主治医师",
					"ZYYSQM" : "住院医师",
					"ZRHSQM" : "责任护士",
					"JXYSQM" : "进修医师",
					"SXYSQM" : "实习医师",
					"BABMYQM" : "病案编码员",
					"ZKYSQM" : "质控医师",
					"ZKHSQM" : "质控护士"
				}
				return panel
			},
			showPSWWIN : function(id) {
				this.qmId = id;
				var obj = document.getElementById(this.qmId);
				this.YSNAME = obj.options[obj.selectedIndex].text;
				if (!this.form) {
					this.form = new Ext.FormPanel({
								frame : true,
								labelWidth : 75,
								labelAlign : 'top',
								defaults : {
									width : '95%'
								},
								defaultType : 'textfield',
								shadow : true,
								items : [{
											fieldLabel : '请输入'+this.qmys[this.qmId.substring(5)]+'【'+this.YSNAME+'】的登录密码',
											name : 'psw',
											inputType : 'password'
										}]
							})
				} else {
					this.psw.setValue();
					var pswField = this.psw.el.parent().parent()
										.first(); // 动态标签2
					pswField.dom.innerHTML = '请输入'+this.qmys[this.qmId.substring(5)]+'【'+this.YSNAME+'】的登录密码';
				}
				// this.Field.setValue();
				if (!this.chiswin) {
					var win = new Ext.Window({
								layout : "form",
								title : '请输入...',
								width : 300,
								height : 126,
								resizable : true,
								modal : true,
								iconCls : 'x-logon-win',
								constrainHeader : true,
								shim : true,
								// items:this.form,
								buttonAlign : 'center',
								closable : false,
								buttons : [{
											text : '确定',
											handler : this.doSignature,
											scope : this
										}, {
											text : '取消',
											handler : this.doCancel,
											scope : this
										}]
							})
					this.chiswin = win
					this.chiswin.add(this.form);
				}
				this.chiswin.show();
				var form = this.form.getForm()
				this.psw = form.findField("psw");
				this.psw.focus(false, 50);
			},
			doSignature : function() {
//				var form = this.form.getForm()
//				this.psw = form.findField("psw");
				var psw = this.psw.getValue();
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrMedicalRecordsService",
							serviceAction : "queryUser",
							uid : document.getElementById(this.qmId).value,
							psw : psw
						});
				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg,this.doSignature);
					return;
				}
				if(res.json.body){
					Ext.MessageBox.alert('提示', res.json.body,function(){
//						this.psw = form.findField("psw");
						this.psw.focus(true, 50);
					},this);
					return;
				}
//				document.getElementById(this.qmId).disabled = true;
				this.dochiswinhide();
				this.opener.doSave(this.qmId.substring(5));
			},
//			initSignature : function() {
//				var schema = this.getMySchema(this.entryName);
//				var items = schema.items
//				var size = items.length
//				for (var i = 0; i < size; i++) {
//					var it = items[i]
//					if (it.id == 'KZRQM' || it.id == 'ZRHSQM'
//							|| it.id == 'JXYSQM' || it.id == 'SXYSQM'
//							|| it.id == 'BABMYQM' || it.id == 'ZKYSQM'
//							|| it.id == 'ZKHSQM') {
//						if (it.dic) {
//							document.getElementById("YSQM_" + it.id).disabled = false;
//						}
//					} else if (it.id == 'BAZL') {
//						var radioObj = document.getElementsByName(it.id)
//						for (var j = 0; j < radioObj.length; j++) {
//							radioObj[j].disabled = false;
//						}
//					} else if (it.id == 'ZKRQ') {
//						this.ZKRQ.setDisabled(false);
//					}
//				}
//			},
			doCancel : function(){
				document.getElementById(this.qmId).value = "";
				this.dochiswinhide();
			},
			dochiswinhide : function() {
				this.chiswin.hide();
			},
			onReady : function() {
				var this_ = this;
				var zkrq = this.exContext.BRXX.ZKRQ;
				this.ZKRQ = new Ext.form.DateField({
							emptyText : "请选择日期",
							renderTo : 'DIV_YSQM_ZKRQ',
							format : 'Y-m-d',
							allowBlank : true,
							value : zkrq,
							width : 130
						})
				//document.getElementById('DIV_YSQM_ZKRQ').firstChild.style.display = "inline";
				if(window.attachEvent){
					document.getElementById('DIV_YSQM_ZKRQ').firstChild.lastChild.style.top = "-14px";
				}else{
					document.getElementById('DIV_YSQM_ZKRQ').firstChild.lastChild.style.top = "-2px";
				}
				if (!this.exContext.SXQX) {
					this.ZKRQ.setDisabled(true);
					document.getElementById("YSQM_RYQHMSJ_T").disabled = true;
					document.getElementById("YSQM_RYQHMSJ_S").disabled = true;
					document.getElementById("YSQM_RYQHMSJ_F").disabled = true;
					document.getElementById("YSQM_RYHHMSJ_T").disabled = true;
					document.getElementById("YSQM_RYHHMSJ_S").disabled = true;
					document.getElementById("YSQM_RYHHMSJ_F").disabled = true;
				} else {
					this.ZKRQ.on("change", function() {
								this.opener.XGYZ = true
							}, this)
				}
				this.initData(1);
				if (this.exContext.BRXX.RYQHMSJ_T) {
					document.getElementById("YSQM_RYQHMSJ_T").value = this.exContext.BRXX.RYQHMSJ_T;
					document.getElementById("YSQM_RYQHMSJ_S").value = this.exContext.BRXX.RYQHMSJ_S;
					document.getElementById("YSQM_RYQHMSJ_F").value = this.exContext.BRXX.RYQHMSJ_F;
					document.getElementById("YSQM_RYHHMSJ_T").value = this.exContext.BRXX.RYHHMSJ_T;
					document.getElementById("YSQM_RYHHMSJ_S").value = this.exContext.BRXX.RYHHMSJ_S;
					document.getElementById("YSQM_RYHHMSJ_F").value = this.exContext.BRXX.RYHHMSJ_F;
				}
				if (this.exContext.SXQX) {
					document.getElementById("YSQM_RYQHMSJ_T").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						this_.setSjData("YSQM_RYQHMSJ");
						this_.opener.XGYZ = true;
					}
					document.getElementById("YSQM_RYQHMSJ_S").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						this_.setSjData("YSQM_RYQHMSJ");
						this_.opener.XGYZ = true;
					}
					document.getElementById("YSQM_RYQHMSJ_F").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						this_.setSjData("YSQM_RYQHMSJ");
						this_.opener.XGYZ = true;
					}
					document.getElementById("YSQM_RYHHMSJ_T").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						this_.setSjData("YSQM_RYHHMSJ");
						this_.opener.XGYZ = true;
					}
					document.getElementById("YSQM_RYHHMSJ_S").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						this_.setSjData("YSQM_RYHHMSJ");
						this_.opener.XGYZ = true;
					}
					document.getElementById("YSQM_RYHHMSJ_F").onchange = function() {
						if(this.value){
							if(isNaN(this.value)){
								this.value = 0;
								MyMessageTip.msg("提示", "请正确输入！", true);
							}
						}
						this_.setSjData("YSQM_RYHHMSJ");
						this_.opener.XGYZ = true;
					}
					document.getElementById("YSQM_LYFS").onchange = function() {
						this_.opener.XGYZ = true;
						if (this.value == 2 || this.value == 3) {
							document.getElementById("YSQM_NJSYLJLMC").disabled = false;
						} else {
							document.getElementById("YSQM_NJSYLJLMC").disabled = true;
							document.getElementById("YSQM_NJSYLJLMC").value = "";;
						}
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
					if (it.layout && it.layout.indexOf("YSQM") >= 0) {
						if (this.exContext.SXQX) {
							if (this.exContext.SYQX) {
								if (it.id == 'ZRYSQM') {
									this.getStore(it.dic, "YSQM_" + it.id,
											it.defaultValue)
								}
							} else {
								if (it.id == 'BAZL') {
									var radioObj = document
											.getElementsByName(it.id)
									for (var j = 0; j < radioObj.length; j++) {
										radioObj[j].disabled = true;
									}
								} else if (it.id == 'ZKRQ') {
									this.ZKRQ.setDisabled(true);
								} else if (it.id == 'ZRYSQM') {
									document.getElementById("YSQM_" + it.id).disabled = true;
									this.getStore(it.dic, "YSQM_" + it.id,it.defaultValue)
									document.getElementById("YSQM_ZZYSQM").disabled = true;
									document.getElementById("YSQM_ZYYSQM").disabled = true;
									document.getElementById("YSQM_KZRQM").disabled = true;
									document.getElementById("YSQM_ZRHSQM").disabled = true;
									document.getElementById("YSQM_JXYSQM").disabled = true;
									document.getElementById("YSQM_SXYSQM").disabled = true;
									document.getElementById("YSQM_BABMYQM").disabled = true;
									document.getElementById("YSQM_ZKYSQM").disabled = true;
									document.getElementById("YSQM_ZKHSQM").disabled = true;
								}
							}
						} else {
							if (it.id == 'ZRYSQM') {
								this.getStore(it.dic, "YSQM_" + it.id,
										it.defaultValue)
							}
						}
						var obj = document.getElementById("YSQM_" + it.id);
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
								if(it.id!="ZRYSQM"&&it.id!="ZZYSQM"&&it.id!="ZYYSQM"&&it.id!="KZRQM"&&it.id!="ZRHSQM"&&it.id!="JXYSQM"&&it.id!="SXYSQM"&&it.id!="BABMYQM"&&it.id!="ZKYSQM"&&it.id!="ZKHSQM"){
									obj.onchange = function() {
										this_.opener.XGYZ = true;
									}
								}
							}
						}
						if (it.dic && dic) {
							if (it.id == 'LYFS') {
								this.getStore(it.dic, "YSQM_" + it.id,
										it.defaultValue)
							}
						}
						if (it.layout.indexOf("radio") >= 0) {
							this.setRadioValue(it.id,
									this.exContext.BRXX[it.id]);
						}
					}
				}
			},
			YSQMClick : function(id,this_){
				if(document.getElementById(id).value){
					if (this_.opener.midiModules["WAR2103"]) {
						var store = this_.opener.midiModules["WAR2103"].grid.getStore();
						var data = []
						var r = store.getAt(4)
						if (r.data.JBXH && r.data.JBBM && r.data.MSZD) {
							if (!r.data.ZXLB) {
								document.getElementById(id).value = ""
								this_.opener.changeTab(2)
								MyMessageTip.msg("提示", '出院主诊断的【中西类别】不能为空!',
										true);
							} else if (!r.data.ZDYS) {
								document.getElementById(id).value = ""
								this_.opener.changeTab(2)
								MyMessageTip.msg("提示", '出院主诊断的【诊断医生】不能为空!',
										true);
							} else if (!r.data.ZDRQ) {
								document.getElementById(id).value = ""
								this_.opener.changeTab(2)
								MyMessageTip.msg("提示", '出院主诊断的【诊断日期】不能为空!',
										true);
							} else if (!r.data.RYBQDM) {
								document.getElementById(id).value = ""
								this_.opener.changeTab(2)
								MyMessageTip.msg("提示", '出院主诊断的【入院病情诊断】不能为空!',
										true);
							} else if (!r.data.CYZGDM) {
								document.getElementById(id).value = ""
								this_.opener.changeTab(2)
								MyMessageTip.msg("提示", '出院主诊断的【出院转归情况】不能为空!',
										true);
							}else{
								this_.showPSWWIN(id)
							}
						} else {
							document.getElementById(id).value = ""
							this_.opener.changeTab(2)
							MyMessageTip.msg("提示", '出院主诊断不能为空!', true);
						}
					} else {
						if(this_.exContext.ZYZDJL){
							if(this_.exContext.ZYZDJL.length>0){
								this_.showPSWWIN(id)
							}else{
								document.getElementById(id).value = ""
								this_.opener.changeTab(2)
								MyMessageTip.msg("提示", '出院主诊断不能为空!', true);
							}
						}else{
							document.getElementById(id).value = ""
							this_.opener.changeTab(2)
							MyMessageTip.msg("提示", '出院主诊断不能为空!', true);
						}
					}
				}
			},
			getStore : function(dic, fieldId, value) {
				if(dic.id=="phis.dictionary.user06"){
					dic.filter = "['eq',['$','item.properties.manageUnit'],['s','"+this.mainApp['phisApp'].deptId+"']]";
//					dic.filter = null;
					dic.sliceType="1";
				}
				var this_ = this;
				var url = this.getUrl(dic)
				// add by yangl 请求统一加前缀
				if (this.requestData) {
					this.requestData.serviceId = 'phis.'
							+ this.requestData.serviceId
				}
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
					if (fieldId == "YSQM_ZRYSQM") {
						document.getElementById(fieldId).options
								.add(new Option("", ""));
						document.getElementById("YSQM_ZZYSQM").options
								.add(new Option("", ""));
						document.getElementById("YSQM_ZYYSQM").options
								.add(new Option("", ""));
						document.getElementById("YSQM_KZRQM").options
								.add(new Option("", ""));
						document.getElementById("YSQM_ZRHSQM").options
								.add(new Option("", ""));
						document.getElementById("YSQM_JXYSQM").options
								.add(new Option("", ""));
						document.getElementById("YSQM_SXYSQM").options
								.add(new Option("", ""));
						document.getElementById("YSQM_BABMYQM").options
								.add(new Option("", ""));
						document.getElementById("YSQM_ZKYSQM").options
								.add(new Option("", ""));
						document.getElementById("YSQM_ZKHSQM").options
								.add(new Option("", ""));
						for (var i = 0; i < this.reader.jsonData.items.length; i++) {

							var key = this.reader.jsonData.items[i].key;
							var text = this.reader.jsonData.items[i].text;
							document.getElementById(fieldId).options
									.add(new Option(text, key));
							document.getElementById("YSQM_ZZYSQM").options
									.add(new Option(text, key));
							document.getElementById("YSQM_ZYYSQM").options
									.add(new Option(text, key));
							document.getElementById("YSQM_KZRQM").options
									.add(new Option(text, key));
							document.getElementById("YSQM_ZRHSQM").options
									.add(new Option(text, key));
							document.getElementById("YSQM_JXYSQM").options
									.add(new Option(text, key));
							document.getElementById("YSQM_SXYSQM").options
									.add(new Option(text, key));
							document.getElementById("YSQM_BABMYQM").options
									.add(new Option(text, key));
							document.getElementById("YSQM_ZKYSQM").options
									.add(new Option(text, key));
							document.getElementById("YSQM_ZKHSQM").options
									.add(new Option(text, key));
						}
						document.getElementById(fieldId).onclick = function() {
							this_.YSQMClick(fieldId,this_);
						};
						if (this_.exContext.BRXX[fieldId.substring(5)]) {
							document.getElementById(fieldId).value = this_.exContext.BRXX[fieldId
									.substring(5)];
//							document.getElementById(fieldId).disabled = true
						}
						document.getElementById("YSQM_ZZYSQM").onclick = function() {
							this_.YSQMClick("YSQM_ZZYSQM",this_);
						};
						if (this_.exContext.BRXX["ZZYSQM"]) {
							document.getElementById("YSQM_ZZYSQM").value = this_.exContext.BRXX["ZZYSQM"];
//							document.getElementById("YSQM_ZZYSQM").disabled = true
						}
						document.getElementById("YSQM_ZYYSQM").onclick = function() {
							this_.YSQMClick("YSQM_ZYYSQM",this_);
						};
						if (this_.exContext.BRXX["ZYYSQM"]) {
							document.getElementById("YSQM_ZYYSQM").value = this_.exContext.BRXX["ZYYSQM"];
//							document.getElementById("YSQM_ZYYSQM").disabled = true
						}
						document.getElementById("YSQM_KZRQM").onclick = function() {
							this_.YSQMClick("YSQM_KZRQM",this_);
						};
						if (this_.exContext.BRXX["KZRQM"]) {
							document.getElementById("YSQM_KZRQM").value = this_.exContext.BRXX["KZRQM"];
//							document.getElementById("YSQM_KZRQM").disabled = true
						}
						document.getElementById("YSQM_ZRHSQM").onclick = function() {
							this_.YSQMClick("YSQM_ZRHSQM",this_);
						};
						if (this_.exContext.BRXX["ZRHSQM"]) {
							document.getElementById("YSQM_ZRHSQM").value = this_.exContext.BRXX["ZRHSQM"];
//							document.getElementById("YSQM_ZRHSQM").disabled = true
						}
						document.getElementById("YSQM_JXYSQM").onclick = function() {
							this_.YSQMClick("YSQM_JXYSQM",this_);
						};
						if (this_.exContext.BRXX["JXYSQM"]) {
							document.getElementById("YSQM_JXYSQM").value = this_.exContext.BRXX["JXYSQM"];
//							document.getElementById("YSQM_JXYSQM").disabled = true
						}
						document.getElementById("YSQM_SXYSQM").onclick = function() {
							this_.YSQMClick("YSQM_SXYSQM",this_);
						};
						if (this_.exContext.BRXX["SXYSQM"]) {
							document.getElementById("YSQM_SXYSQM").value = this_.exContext.BRXX["SXYSQM"];
//							document.getElementById("YSQM_SXYSQM").disabled = true
						}
						document.getElementById("YSQM_BABMYQM").onclick = function() {
							this_.YSQMClick("YSQM_BABMYQM",this_);
						};
						if (this_.exContext.BRXX["BABMYQM"]) {
							document.getElementById("YSQM_BABMYQM").value = this_.exContext.BRXX["BABMYQM"];
//							document.getElementById("YSQM_BABMYQM").disabled = true
						}
						document.getElementById("YSQM_ZKYSQM").onclick = function() {
							this_.YSQMClick("YSQM_ZKYSQM",this_);
						};
						if (this_.exContext.BRXX["ZKYSQM"]) {
							document.getElementById("YSQM_ZKYSQM").value = this_.exContext.BRXX["ZKYSQM"];
//							document.getElementById("YSQM_ZKYSQM").disabled = true
						}
						document.getElementById("YSQM_ZKHSQM").onclick = function() {
							this_.YSQMClick("YSQM_ZKHSQM",this_);
						};
						if (this_.exContext.BRXX["ZKHSQM"]) {
							document.getElementById("YSQM_ZKHSQM").value = this_.exContext.BRXX["ZKHSQM"];
//							document.getElementById("YSQM_ZKHSQM").disabled = true
						}
					} else if (fieldId == "YSQM_LYFS") {
						document.getElementById(fieldId).options
								.add(new Option("--请选择--", ""));
						for (var i = 0; i < this.reader.jsonData.items.length; i++) {

							var key = this.reader.jsonData.items[i].key;
							var text = this.reader.jsonData.items[i].text;
							document.getElementById(fieldId).options
									.add(new Option(text, key));
						}
						if (value) {
							document.getElementById(fieldId).value = value.key;
						}
						if (this_.exContext.BRXX[fieldId.substring(5)]) {
							document.getElementById(fieldId).value = this_.exContext.BRXX[fieldId
									.substring(5)];

						}
						if (document.getElementById(fieldId).value == 2
								|| document.getElementById(fieldId).value == 3) {
							if (this.exContext.SXQX) {
								document.getElementById("YSQM_NJSYLJLMC").disabled = false;
							}
						} else {
							document.getElementById("YSQM_NJSYLJLMC").disabled = true;
							document.getElementById("YSQM_NJSYLJLMC").value = "";;
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
				if (dic.sliceType) {
					if (url.indexOf("?") > -1) {
						url += "&sliceType=" + dic.sliceType
					} else {
						url += "?sliceType=" + dic.sliceType
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
			},
			setSjData : function(id) {
				var T = document.getElementById(id + "_T").value ? document
						.getElementById(id + "_T").value : 0;
				var S = document.getElementById(id + "_S").value ? document
						.getElementById(id + "_S").value : 0;
				var F = document.getElementById(id + "_F").value ? document
						.getElementById(id + "_F").value : 0;
				document.getElementById(id).value = parseInt(T * 24 * 60)
						+ parseInt(S * 60) + parseInt(F);
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
			}
		})