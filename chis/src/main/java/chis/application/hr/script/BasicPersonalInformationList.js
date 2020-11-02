$package("chis.application.hr.script")
$import("chis.script.BizSimpleListView", "chis.script.demographicView",
        "chis.script.EHRView",
        "chis.script.area")

chis.application.hr.script.BasicPersonalInformationList = function(cfg) {
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.hr.script.BasicPersonalInformationList.superclass.constructor
			.apply(this, [cfg]);
	
}

Ext.extend(chis.application.hr.script.BasicPersonalInformationList,
		chis.script.BizSimpleListView, {
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				};
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});
				comb.on("select", this.radioChanged, this);
				comb.setValue("01");
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				cfg.items = [lab, comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},

			radioChanged : function(r) {
				var status = r.getValue();
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var statusCnd = ["eq", ["$", "a.status"], ["s", status]];
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd);
					}
					if (queryCnd) {
						cnd.push(queryCnd);
					}
				}
				var bts = this.grid.getTopToolbar().items;
				var btn = bts.items[7];
				if (btn) {
					if (status != "0") {
						btn.disable();
					} else {
						btn.enable();
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},

			doAction : function(item, e) {
				var cmd = item.cmd;
				var ref = item.ref;
				if (ref) {
					this.loadRemote(ref, item);
					return;
				}
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1);
				var action = this["do" + cmd];
				if (action) {
					action.apply(this, [item, e]);
				}
			},
			onDblClick : function(grid, index, e) {
				this.doXg();
			},
			doNew : function(item, e) {
				var m = this.createModule("grjbxx",
						"chis.application.hr.HR/HR/B34101")
				 m.on("save", this.onSave, this)
				m.on("save2",this.fresh1,this);
				m.op = "create";
				var win = m.getWin();
				win.add(m.initPanel())
				win.setPosition(280, 60);
				win.show();
				m.doNew();
			},
			doXg : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var m = this.createModule("grjbxx",
						"chis.application.hr.HR/HR/B34101")
				m.on("save", this.onSave, this)
				m.on("save2", this.fresh1, this);// 刷新
				m.op = "update";
				var win = m.getWin();
				win.add(m.initPanel())
				var data = {};
				data["empiId"] = r.get("empiId");
				data["regionCode"] = r.get("regionCode");
				win.setPosition(289, 65);
				win.show();
				m.doNew();//
				m.loadData(data);
			},
			createModule : function(moduleName, moduleId, exCfg) {
				var item = this.midiModules[moduleName]
				if (!item) {
					var moduleCfg = this.loadModuleCfg(moduleId);
					var cfg = {
						showButtonOnTop : true,
						border : false,
						frame : false,
						autoLoadSchema : false,
						isCombined : true,
						exContext : {}
					};
					Ext.apply(cfg, exCfg);
					Ext.apply(cfg, moduleCfg);
					var cls = moduleCfg.script;
					if (!cls) {
						return;
					}
					if (!this.fireEvent("beforeLoadModule", moduleName, cfg)) {
						return;
					}
					$import(cls);
					item = eval("new " + cls + "(cfg)");
					item.setMainApp(this.mainApp);
					this.midiModules[moduleName] = item;
				}
				return item;
			},
			loadModuleCfg : function(id) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "moduleConfigLocator",
							id : id,
							method : "execute"
						})
				if (result.code != 200) {
					if (result.msg = "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				return result.json.body;
			},
			doWriteOff : function() {
				var r = this.getSelectedRecord();
				if (this.store.getCount() == 0) {
					return;
				}
                if(!(this.mainApp.jobId=='chis.14'|| this.mainApp.jobId=='chis.system')){
                	if(r.get("manaDoctorId")!=this.mainApp.uid){
                	alert("只有防保科长或现责任医生能注销档案！")
                	return;
                	}
                }
				var cfg = {
					title : "个人基本信息注销",
					phrId : r.get("phrId"),
					personName : r.get("personName"),
					empiId : r.get("empiId"),
					mainApp : this.mainApp
				};
				var module = this.midiModules["HealthRecordLogoutForm"];
				if (!module) {
					$import("chis.application.hr.script.HealthRecordLogoutForm");
					module = new chis.application.hr.script.HealthRecordLogoutForm(cfg);
					module.on("remove", this.onRemove, this);
					module.initPanel();
					this.midiModules["HealthRecordLogoutForm"] = module;
				} else {
					Ext.apply(module, cfg);
				}
				module.getWin().show();
			},
			onRemove : function() {
				this.loadData();
			},
			fresh1 : function() {
				this.refresh();
			},
			
			doZlls : function() {
				var r = this.grid.getSelectionModel().getSelected();
				if(!r){
					alert("请选择一条居民记录");
					return ;
				}
				var idcardnow=r.data.idCard;
				if( idcardnow.length != 18 ){
					alert("身份证号不是18位");
					return ;
				}
				//调用大数据健康档案浏览器接口服务，跳转html页面  zhaojian 2017-11-01
				var params_array= [{name:"idcard",value:idcardnow.replace(/(^\s*)|(\s*$)/g, "")},{name:"sys_organ_code",value:this.mainApp.deptId.replace(/(^\s*)|(\s*$)/g, "")},{name:"sys_code",value:"jkda"},{name : "opeCode",value : this.mainApp.uid},{name : "opeName",value : this.mainApp.uname}];
				util.rmi.jsonRequest({
					serviceId : "chis.desedeService",
					schema : "",
					serviceAction : "getDesInfo",
					method : "execute",
					params : JSON.stringify(params_array)
				}, function(code, msg, json) {
					if (msg == "Success") {
						this.openBHRView(json,"getPersonInfo");                  		
					} else {
						Ext.Msg.alert("提示", "操作失败");
						return false;
					}
				}, this)					
//			},
//				util.rmi.jsonRequest({
//					serviceId : "chis.empiService",
//					schema : "MPI_DemographicInfo",
//					serviceAction : "checkZlls",
//					method : "execute",
//					body : {
//						idcard : idcardnow
//					}
//				}, function(code, msg, json) {
//					if (code == 403) {
//						this.processReturnMsg(result.code, result.msg);
//						return;
//					}
//					var data = json["body"];
//					if (!data || data.length == 0)
//						return;
//					if(data=="yes"){
	                if(r.data.zlls=="1"){
					this.html="";
					Ext.apply(this,chis.script.area);
					this.area=this.getArea();
					
					 if(this.area=="ly"){
					 	this.html="<iframe id='cfinfo' src='http://172.31.167.105:8081/ehrview/EhrLogonService?user=system&pwd=123&idcard="+idcardnow+"' frameborder='0' width='100%' height='100%'></iframe>";
					 }else{
					 	this.html="<iframe id='cfinfo' src='http://32.33.1.75:9999/ehrview/EhrLogonService?user=system&pwd=123&idcard="+idcardnow+"' frameborder='0' width='100%' height='100%'></iframe>";
					 }
					var view=new Ext.Window({
						width:1200,
						height:680,
						closable : true, // 是否可关闭
						closeAction : 'hide', // 关闭策略
						bodyStyle : 'background-color:#FFFFFF',
						collapsible : true, // 是否可收缩
						maximizable : false, // 设置是否可以最大化
						animateTarget : Ext.getBody(),
						border : true, // 边框线设置
						pageY : 25, // 页面定位Y坐标
						pageX : document.body.clientWidth / 2 - 700 / 2, // 页面定位X坐标
						constrain : true,
						// 设置窗口是否可以溢出父容器
						buttonAlign : 'center',
						html : this.html,
						//items: cfmxgrid,
						buttons : [ {
							text : '关闭',
							iconCls : 'deleteIcon',
							handler : function() {
								view.hide();
							}
						}]
						
					})
					view.show();
					}else {
						Ext.Msg.alert('温馨提示','该居民还没有诊疗历史！')
					};
//				})
				
			},
			doTjb : function() {
				var r = this.grid.getSelectionModel().getSelected()
				this.showModule(r.data,"update")
			},
			showModule : function(data,op) {
				var empiId = data.empiId;
				var initModules = ['B_10'];
				if(this.mainApp.exContext.healthCheckType == 'paper'){
					initModules = ['B_10_HTML'];
				}
				var module = this.midiModules["HealthCheck_EHRView"]
				if (!module) {
					module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
							})
					this.midiModules["HealthCheck_EHRView"] = module
					//module.on("save", this.refreshData, this);
					module.exContext.args["healthCheck"] = "";
				} else {
					module.exContext.ids["empiId"] = empiId;
					module.exContext.args["healthCheck"] = "";
					module.refresh();
				}
				module.exContext.args["dataSources"] = "1";
				module.exContext.args["op"] = op;
				module.getWin().show();
			},
			
			doCross : function() {
				var r = this.grid.getSelectionModel().getSelected();
				if(!r){
					alert("请选择一条居民记录");
					return ;
				}
				var idcardnow=r.data.idCard;
				if( idcardnow.length != 18 ){
					alert("身份证号不是18位");
					return ;
				}
				this.html="<iframe id='cfinfo' src='http://192.168.20.17/hdpt/cross.ehr?idcard="+idcardnow+"&regdocid="+r.data.regionCode+"&manadocid="+r.data.manaDoctorId+"&manaunitid="+r.data.manaUnitId+"' frameborder='0' width='100%' height='100%'></iframe>";
					var Crossview=new Ext.Window({
						width:400,
						height:300,
						closable : true, // 是否可关闭
						closeAction : 'hide', // 关闭策略
						bodyStyle : 'background-color:#FFFFFF',
						collapsible : true, // 是否可收缩
						maximizable : false, // 设置是否可以最大化
						animateTarget : Ext.getBody(),
						border : true, // 边框线设置
						pageY : 25, // 页面定位Y坐标
						pageX : document.body.clientWidth / 2 - 700 / 2, // 页面定位X坐标
						constrain : true,
						// 设置窗口是否可以溢出父容器
						buttonAlign : 'center',
						html : this.html,
						//items: cfmxgrid,
						buttons : [ {
							text : '关闭',
							iconCls : 'deleteIcon',
							handler : function() {
								Crossview.hide();
							}
						}]
						
					})
					Crossview.show();
			}
		});