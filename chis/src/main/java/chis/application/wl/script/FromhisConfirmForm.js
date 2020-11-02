$package("chis.application.wl.script");

$import("chis.script.BizTableFormView", "util.widgets.LookUpField");
$import("util.Accredit", "chis.script.util.helper.Helper")
$import("app.modules.form.TableFormView")

chis.application.wl.script.FromhisConfirmForm = function(cfg) {
	cfg.autoLoadSchema = false
	chis.application.wl.script.FromhisConfirmForm.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.wl.script.FromhisConfirmForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.wl.script.FromhisConfirmForm.superclass.onReady
						.call(this);
				var targetDoctor = this.form.getForm()
						.findField("targetDoctor");
				if (targetDoctor) {
					targetDoctor.on("select", this.changeManaUnit, this);
				}
			},
			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method:"execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				this.setManaUnit(result.json.manageUnit)
			},
				setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("targetUnit");
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}
				combox.setValue(manageUnit)
				combox.disable();
			},
			doConfirm : function() {

				this.data["affirmType"] = "1";
				this.op = "update";
				if (this.data["status"].key == "4") {
					Ext.Msg.alert("提示：", "已确认！")
					return;
				}
				if (this.data["status"].key == "9") {
					Ext.Msg.alert("提示：", "已退回！")
					return;
				}
				var sfzh = this.form.getForm().findField('sfzh').getValue();
                
				if (sfzh.length < 18) {
					Ext.Msg.alert("提示：", "请核实身份证号：" + sfzh)
					return;
				}
				
				var checkcardnum = this.checkIdcard(sfzh)
				if (checkcardnum.indexOf("身份证") > 0) {
					Ext.Msg.alert("提示：", "请核实身份证号：" + sfzh)
					return;
				}
				
				var areagrid = this.form.getForm().findField('areagrid').getValue();
				if (!areagrid || areagrid.length < 7) {
					Ext.Msg.alert("提示：", "请选择网格地址！")
					return;
				}
				var targetDoctor = this.form.getForm().findField('targetDoctor').getValue();
				if (!targetDoctor || targetDoctor.length < 1) {
					Ext.Msg.alert("提示：", "请选择责任医生！")
					return;
				}
				var targetUnit = this.form.getForm().findField('targetUnit').getValue();
				if (!targetUnit || targetUnit.length < 4) {
					Ext.Msg.alert("提示：", "请选择责任医生！")
					return;
				}
				this.data["areagrid_text"]=this.form.getForm().findField('areagrid').getRawValue();
				this.saveServiceId = "chis.managemdcfromhis";
				this.saveAction = "saveConfirmfromhis";
				this.doSave();
				var r=this.data;
				var empiId =this.data.empiId;
				if(empiId){
				this.onSelectEMPI(r);
				}
				this.getWin().hide();
			},
						
			onSelectEMPI : function(r) {
				var empiId = r.empiId;
				var jdlx = r.jdlx;
				var module = this.midiModules["HealthRecord_EHRView"]	
					$import("chis.script.EHRView");
					
					if (jdlx == "1") {
						var initModules = ['C_01'];
						module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
								
							});
					this.midiModules["HealthRecord_EHRView"] = module;						
				     module.getWin().show();
					} else if (jdlx == "2") {
						var initModules = ['D_01'];
						module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
								
							});
					this.midiModules["HealthRecord_EHRView"] = module;						
				    module.getWin().show();
					}	else  {
						var initModules = ['B_011'];
						module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
								
							});
					this.midiModules["HealthRecord_EHRView"] = module;						
				    module.getWin().show();
					}					
			},
			doReject : function() {
				this.data["affirmType"] = "2";
				if (this.data["status"].key == "4") {
					Ext.Msg.alert("提示：", "已确认不能退回！")
					return;
				}
				if (this.data["status"].key == "9") {
					Ext.Msg.alert("提示：", "已退回！")
					return;
				}
				this.op = "update";
				this.saveServiceId = "chis.managemdcfromhis";
				this.saveAction = "saveRejectfromhis";
				this.doSave();
				this.getWin().hide();
			},
			checkIdcard : function(pId) {
				var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 15 && pId.length != 18) {
					return "身份证号共有 15 码或18位";
				}
				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai)) {
					return "身份证除最后一位外，必须为数字！";
				}
				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						this.mainApp.serverDate, "Y-m-d");
				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !this.isValidDate(dd, mm, yyyy)) {
					return "身份证输入错误！";
				}
				for (var i = 0, ret = 0; i < 17; i++) {
					ret += Ai.charAt(i) * Wi[i];
				}
				Ai += arrVerifyCode[ret %= 11];
				return pId.length == 18 && pId.toLowerCase() != Ai
						? "身份证输入错误！"
						: Ai;
			},// 判断时间是否合法
			isValidDate : function(day, month, year) {
				if (month == 2) {
					var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
					if (day > 29 || (day == 29 && !leap)) {
						return false;
					}
				}
				return true;
			}
			//跳转到健康档案浏览器
			,doJumpEHR:function(){
				var sfzh = this.form.getForm().findField('sfzh').getValue();
				if(sfzh==""||sfzh.length!=18){
					Ext.Msg.alert("提示","身份证号不正确，不能查看健康档案浏览器");
					return;
				}
				var params_array = [{
					name : "idcard",
					value :sfzh
				},{name:"sys_organ_code",value:this.mainApp.deptId.replace(/(^\s*)|(\s*$)/g, "")},{name:"sys_code",value:"jkda"},{name : "opeCode",value : this.mainApp.uid},{name : "opeName",value : this.mainApp.uname}];
				//调用大数据健康档案浏览器接口服务，跳转html页面  zhaojian 2018-06-26
				util.rmi.jsonRequest({
					serviceId : "chis.desedeService",
					schema : "",
					serviceAction : "getDesInfo",
					method : "execute",
					params : JSON.stringify(params_array)
				}, function(code, msg, json) {
					if (msg == "Success") {
						this.openBHRView( json, "getPersonInfo",this.panel);             		
					} else {
						Ext.Msg.alert("提示", "操作失败");
						return false;
					}
				}, this)
			}
		});