$package("chis.application.tr.script.phq")

$import("chis.script.BizSimpleListView");

chis.application.tr.script.phq.TumourHealthQuestionList = function(cfg) {
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.tr.script.phq.TumourHealthQuestionList.superclass.constructor
			.apply(this, [cfg]);
	this.querySchema = "chis.application.mpm.schemas.MPM_MasterplateMaintain";
	this.on("beforeRemove",this.onBeforeRemove,this);
}

Ext.extend(chis.application.tr.script.phq.TumourHealthQuestionList,
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
				var logoutBtn = bts.items[6];
				var deleteBtn = bts.items[7];
				if(logoutBtn){
					if (status != "0") {
						logoutBtn.disable();
					} else {
						logoutBtn.enable();
					}
				}
				if(deleteBtn){
					if (status != "0") {
						deleteBtn.disable();
					} else {
						deleteBtn.enable();
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				var masterplateId = r.get("masterplateId");
				var gcId = r.get("gcId");
				var masterplateType = r.get("masterplateType");
				var JZXH = r.get("JZXH");
				var status = r.get("status");
				this.showTHQViewWin(masterplateId,gcId,masterplateType,JZXH,status);
			},
			onDblClick : function() {
				this.doModify();
			},
			showTHQViewWin : function(masterplateId,gcId,masterplateType,JZXH,status) {
				var control = {"create":true,"update":true};
				var uid = this.mainApp.uid
				if(status != 0 && uid != 'system'){
					control = {"create":false,"update":false};
				}
				var module = this.createSimpleModule("TPHQModule",this.TPHQModule);
				module.exContext.args = {};
				module.exContext.args.masterplateId=masterplateId;
				module.exContext.args.empiId = this.empiId;
				module.exContext.args.gcId = gcId;
				module.exContext.args.masterplateType = masterplateType;
				module.exContext.args.source = "2";
				module.exContext.args.JZXH = JZXH;
				module.initPanel();
				module.initDataId = null;
				module.exContext.control = control;
				module.on("saveTHQ",this.THQViewSave,this);
				this.showWin(module);
				module.loadData();
			},
			THQViewSave : function(){
				this.refresh();
			},
			doHealthQuestion : function(){
				this.fireEvent("createTPHQ");
			},
			doWriteOff : function() {
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				Ext.Msg.show({
				   title: '确认注销记录',
				   msg: '该记录注销后将不能进行该记录的其他业务操作，是否继续?',
				   modal:true,
				   width: 300,
				   buttons: Ext.MessageBox.OKCANCEL,
				   multiline: false,
				   fn: function(btn, text){
				   	 if(btn == "ok"){
				   	 	this.processLogout();
				   	 }
				   },
				   scope:this
				})
			},
			processLogout : function(){
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				var saveRequest = {
					gcId : r.get("gcId"),
					empiId : r.get("empiId"),
					status : "1"
				};
				util.rmi.jsonRequest({
						serviceId:this.saveServiceId,
						serviceAction:this.writeOffAction,
						method:"execute",
						op:"update",
						schema:this.entryName,
						body:saveRequest
					},
					function(code,msg,json){
						this.saving = false
						if(code > 300){
							this.processReturnMsg(code,msg,this.doWriteOff,[saveRequest],json.body);
							this.fireEvent("exception", code, msg, saveData); // **进行异常处理
							return
						}
						this.refresh();
					},
				this)
			},
			onBeforeRemove : function(entryName,r){
				var body = {
					empiId:r.get("empiId")
				};
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.tumourQuestionnaireService",
							serviceAction : "existHR",
							method : "execute",
							body : body
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				var isDelete = true;
				if(result.json.body){
					isDelete = result.json.body.existHR;
				}
				if(!isDelete){
					Ext.Msg.alert("提示","健康档案已注销,问题不能删除！");
				}
				return isDelete;
			}
		});