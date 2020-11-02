$package("phis.application.med.script")

$import("phis.script.TableForm")

phis.application.med.script.MZListForm = function(cfg) {
	cfg.showButtonOnTop = false;
	cfg.colCount = 4;
	cfg.autoLoadData = false;
	cfg.loadServiceId = "medicalTechnicalSectionService";
	cfg.loadAction = "getMzForm";
	phis.application.med.script.MZListForm.superclass.constructor.apply(this, [cfg])
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(phis.application.med.script.MZListForm, phis.script.TableForm, {
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
					if(it.id=="ZXRQ"){
						f.setValue(v.substring(0,v.indexOf(' ')));
					}else{
						f.setValue(v)
					}
				}
				if (it.update == "false") {
					f.disable();
				}
			}
		}
		this.setKeyReadOnly(true)
		this.focusFieldAfter(-1, 800)
	},
	loadData : function() {
		
		if (this.loading) {
			return
		}
//		if (!this.schema) {
//			return
//		}
//		if (!this.initDataId && !this.initDataBody) {
//			return;
//		}
		if (!this.fireEvent("beforeLoadData", this.entryName, this.initDataId,
				this.initDataBody)) {
			return
		}
		if (this.form && this.form.el) {
			this.form.el.mask("正在载入数据...", "x-mask-loading")
		}
		this.loading = true
		phis.script.rmi.jsonRequest(Ext.apply({
			serviceId : this.loadServiceId,
			serviceAction : this.loadAction 
			},this.exContext.args["formRequestData"]  ), function(code, msg, json) {
			if (this.form && this.form.el) {
				this.form.el.unmask()
			}
			this.loading = false
			if (code > 300) {
				this.processReturnMsg(code, msg, this.loadData)
				return
			}
			if (json.body) {
				this.doNew()
				this.initFormData(json.body[0])
				var form = this.form.getForm();
				var jzkh = form.findField("JZKH");
				jzkh.disable();
				this.fireEvent("loadData", this.entryName, json.body[0]);
			}
			if (this.op == 'create') {
				this.op = "update"
			}

		}, this)// jsonRequest
	},
			onReady : function(){
				phis.application.med.script.MZListForm.superclass.onReady.call(this);
				var form = this.form.getForm();
				var dic_ks;
				var items = this.schema.items;
				for ( var i = 0; i < items.length; i++) {
					var it = items[i]
					if (it.id == 'KSDM') {
						dic_ks = it.dic;
					}
				}
				var jzkh = form.findField("JZKH");
				jzkh.un("specialkey", this.onFieldSpecialkey, this)
				jzkh.on("specialkey", function(jzkh, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (jzkh.getValue()) {
									this.doJzkhEnter(jzkh);
								}else{
									this.showModule();
								}
							}
						}, this)
				var ksdm = form.findField("KSDM");
				var ysdm = form.findField("YSDM");
				ysdm.on("select", function() {
					var ysId = ysdm.getValue();
					phis.script.rmi
							.jsonRequest(
									{
										serviceId : "changeDoctorOrDepartmentService",
										serviceAction : "findKsdmByYsdm",
										body : {
											ysId : ysId
										}
									},
									function(code, msg,
											json) {
										var arr1 = json.ksdm;
										ksdm.setValue("");
										ksdm.store.removeAll();
										ksdm.clearValue();
										var filters1 = "['and',['in',['$','item.properties.ID','i'],"
												+ arr1
												+ "],['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]]";
										dic_ks.id = "phis.dictionary.department_leaf";
										dic_ks.filter = filters1;
										ksdm.store.proxy = new util.dictionary.HttpProxy(
												{
													method : "GET",
													url : util.dictionary.SimpleDicFactory
															.getUrl(dic_ks)
												})
										ksdm.store.load();
				}, this);
					this.fireEvent("startEditing");
				}, this)
				
			},
			focusZXYS : function(){
				var form = this.form.getForm();
				var zxys = form.findField("ZXYS");
				zxys.focus();
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
					m.on("onEmpiReturn", this.doMZHMQuery, this);
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
				// 1卡号
				var form = m.midiModules[m.entryName].form.getForm();
				if (pdms.json.cardOrMZHM == 1) {
					form.findField("MZHM").setDisabled(true);
				}
				// 2门诊号码
				if (pdms.json.cardOrMZHM == 2) {
					form.findField("cardNo").setValue(form.findField("MZHM")
							.getValue());
					form.findField("personName").focus(true, 200);
				}
			},
//			doMzhmEnter : function(field) {
//				if (field.getValue()) {
//					this.doQueryBrxx(field);
//				}
//			},
//			doQueryBrxx : function(field) {
////				alert(1)
//			},
			getSaveData : function(){
				if (this.saving) {
					return
				}
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items
				Ext.apply(this.data, this.exContext)
				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var v = this.data[it.id] // ** modify by yzh 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null && v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}

						if (v == null || v === "") {
							if (!it.pkey && it["not-null"] && !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空")
								return null;
							}
						}
						values[it.id] = v;
					}
				}
				return values ;
			},
			onLoadData : function(e,r){
				if(!r||!r.YJXH){
					Ext.Msg.alert('提示','您选取的医技单无效,可能是病人信息已被删除!');
					this.opener.getWin().hide()
					return;
				}
			},
//			doNew : function() {
//				this.op = "create"
//				if (this.data) {
//					this.data = {}
//				}
//				this.initDataId = null;
//				if (!this.schema) {
//					return;
//				}
//				var form = this.form.getForm()
//				var f = form.findField(this.loginname)
//
//				if (f) {
//
//					if (!f.hasListener("lookup")) {
//						f.on("lookup", this.doLookup, this)
//						f.un("specialkey", this.onFieldSpecialkey, this)
//						f.on("specialkey", function(f, e) {
//									var key = e.getKey()
//									if (key == e.ENTER) {
//										this.doENTER(f)
//									}
//								}, this)
//					}
//					f.enable()
//				}
//				var items = this.schema.items
//				var n = items.length
//				for (var i = 0; i < n; i++) {
//					var it = items[i]
//					var f = form.findField(it.id)
//					if (f) {
//						f.setValue(it.defaultValue)
//						// @@ 2010-01-07 modified by chinnsii, changed the
//						// condition
//						// "it.update" to "!=false"
//						if (!it.fixed && !it.evalOnServer) {
//							f.enable();
//						} else {
//							f.disable();
//						}
//						if (it.type == "date") { // ** add by yzh 20100919 **
//							if (it.minValue)
//								f.setMinValue(it.minValue)
//							if (it.maxValue)
//								f.setMaxValue(it.maxValue)
//						}
//					}
//				}
//				this.setKeyReadOnly(false)
//				this.resetButtons(); // ** add by yzh **
//				this.fireEvent("doNew")
//				this.focusFieldAfter(-1, 800)
//				this.validate()
//			},
//			doLookup : function(field) {
//				var v = field.getValue()
//				this.loadPersonList(v)
//			},
			doMZHMQuery : function(data) {
				this.form.el.mask("正在查询...");
				phis.script.rmi.jsonRequest({
							serviceId : "medicalTechnicalSectionService",
							serviceAction : "queryByMZHM",
							MZHM : data.MZHM
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
//							if (json.body.size != 1) {
//								this.loadPersonList(v)
//							} else {
							if(!json.body){
								MyMessageTip.msg("提示", "没有找到该病人信息!",
										true);
								return ; 
							}
							json.body.KSDM = this.mainApp['phis'].MedicalId
							json.body.YSDM = this.mainApp.userId
							this.queryData = true;
								this.initFormData(json.body)
								field.disable()
								this.opener.BRXZ = json.body.BRXZ;
								this.fireEvent("setBake");
//								this.Backfill(json.body.data);
//							}
						}, this);
			},
			doJzkhEnter : function(field) {
				var v = field.getValue();
				this.form.el.mask("正在查询...");
				phis.script.rmi.jsonRequest({
							serviceId : "medicalTechnicalSectionService",
							serviceAction : "queryByMZHM",
							JZKH : v
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
//							if (json.body.size != 1) {
//								this.loadPersonList(v)
//							} else {
							if(!json.body){
								MyMessageTip.msg("提示", "没有找到该病人信息!",
										true);
								return ; 
							}
//							json.body.KSDM = this.mainApp['phis'].MedicalId
							json.body.YSDM = this.mainApp.userId
							this.queryData = true;
								this.initFormData(json.body)
								field.disable()
								this.opener.BRXZ = json.body.BRXZ;
								this.fireEvent("setBake");
//								this.Backfill(json.body.data);
//							}
						}, this);
			},
			loadPersonList : function(v) {
				var cnd = null
				//var ygbhsstr = "'";
//				for(var i = 0 ; i <this.ygbhs.length ; i++){
//					if(i == 0){
//						ygbhsstr += this.ygbhs[i];
//					}else{
//						ygbhsstr += "','"+this.ygbhs[i];
//					}
//				}
//				ygbhsstr += "'";
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "userManageService",
							serviceAction : "getYgbh"
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				var ygbhs  = result.json.ygbh;
				var ygbhsstr = "'";
				for(var i = 0 ; i <ygbhs.length ; i++){
					if(i == 0){
						ygbhsstr += ygbhs[i];
					}else{
						ygbhsstr += "','"+ygbhs[i];
					}
				}
				ygbhsstr += "'";
				this.ygbhsstr = ygbhsstr;
				if(ygbhs.length>0){
					if (v) {
						cnd = ['and',['like', ['$', this.YGBH], ['s', '%' + v + '%']],['not in',['$','YGBH'],[ygbhsstr]]];
					}else{
						cnd = ['not in',['$','YGBH'],[ygbhsstr]];
					}
				}else{
					if (v) {
						cnd = ['like', ['$', this.YGBH], ['s', '%' + v + '%']];
					}
				}
				var cmd = "gridlist";
				var cfg = {
					title : "员工信息",
					entryName :this.FEntryName, //"SYS_Personnel",
					autoLoadSchema : false,
					autoLoadData : true,
					showButtonOnTop : true,
					createCls : "phis.application.pub.script.DepartmentStallForm",
					updateCls : "phis.application.pub.script.DepartmentStallForm",
					initCnd : cnd,
					actions : [
						{
								id : "save",
								name : "确定"
							},
							{
								id : "canceled",
								name : "取消",
								iconCls : "common_cancel"
							}]
				}
				var cls = "phis.application.pub.script.PersonQuery"
				var m = this.midiModules[cmd]
				if (!m) {
					$require(cls, [function() {
										var module = eval("new " + cls
												+ "(cfg)")
										module.opener = this
										module.setMainApp(this.mainApp)
										module.on("Backfill", this.Backfill,
												this)
										module.requestData.cnd = cnd;
										this.midiModules[cmd] = module;
										var lp = module.initPanel()
										this.openWin(cmd, 100, 50)
									}, this])
				} else {
					m.requestData.cnd = cnd
					m.refresh()
					this.openWin(cmd, 100, 50)
				}
			},
			openWin : function(cmd, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					if (xy) {
						win.setPosition(xy[0], xy[1])
					}
					win.setTitle(module.title)
					win.show()
					var form = this.form.getForm();
					var YGBH = form.findField(this.loginname);
				}
			},
			Backfill : function(data, win) {
				this.form.el.mask("正在查询...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "userAuthentication",
							body : data["YGBH"]
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							var form = this.form.getForm();
							var YGBH = form.findField("loginName");
							YGBH.setValue(data["YGBH"]);
							YGBH.disable();
							var YGXM = form.findField("userName");
							YGXM.setValue(data["YGXM"]);
							YGXM.disable();
							var YGXB = form.findField("gender");
							YGXB.setValue(data["YGXB"]);
							YGXB.disable();
							this.initDataId = data["YGDM"];
							this.data["pyCode"] = data["PYDM"];
							//焦点定位到密码输入框
							var password = form.findField("password");
							if (win) {
								win.hide()
							}
						}, this);

			}
		})