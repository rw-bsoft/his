$package("chis.application.tr.script.phq")

$import("chis.script.BizCombinedModule2",
		"chis.script.util.query.CombinationSelectMultiple",
		"chis.script.util.widgets.MyMessageTip");

chis.application.tr.script.phq.TumourHealthQuestionModule = function(cfg) {
	chis.application.tr.script.phq.TumourHealthQuestionModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.width = 820;
	this.height = 600;
	this.itemHeight = 350;
	this.secondFrame = true;
	this.on("loadData",this.onLoadData,this);
	this.on("save", this.onTHQSave, this);
	this.queryServiceId = "chis.tumourQuestionnaireService";
	this.queryServiceActioin = "getTumourInspectionItems";
	this.queryEntryName = "chis.application.tr.schemas.MDC_TumourInspectionItemCommonQuery"
	this.buttonIndex=0;
}

Ext.extend(chis.application.tr.script.phq.TumourHealthQuestionModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var items = this.getPanelItems();
				var cfg = {
							border : false,
							split : this.split,
							hideBorders : true,
							frame : this.frame || false,
							layout : 'border',
							width : this.width || 600,
							height : this.height || 300,
							items : items
						};
				this.extendCfg(cfg);
				var panel = new Ext.Panel(cfg);
				this.panel = panel
				panel.on("afterrender", this.onReady, this)
				this.baseForm = this.midiModules[this.actions[0].id];
				//this.baseForm.on("save", this.THQSave, this);
				this.THQForm = this.midiModules[this.actions[1].id];
				this.THQItem = panel.items.items[1];
				return panel;
			},
			extendCfg : function(cfg){
				if(this.actions.length > 2){
					var actions = [];
					actions.push(this.actions[2]);
					var btns = this.createTbar(actions);
					cfg.tbar = btns;
				}
			},
			createTbar : function(actions){
				if(this.op == 'read'){
					return [];
				}
				var buttons = []
				if(!actions){
					return buttons
				}
				if(this.butRule){
				    var ac = util.Accredit
				    if(ac.canCreate(this.butRule)){
				    	this.actions.unshift({id:"create",name:"新建"})
				    }
				}
				var f1 = 112
				for(var i = 0; i < actions.length; i ++){
					var action = actions[i];
					var btn = {}
					btn.accessKey = f1 + i + this.buttonIndex,
					btn.cmd = action.id
					btn.text = action.name + "(F" + (i + 1 + this.buttonIndex) + ")",
					btn.iconCls = action.iconCls || action.id
					btn.script =  action.script
					btn.handler = this.doAction;
					btn.prop = {};
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					btn.scope = this;
					buttons.push(btn)
				}
				return buttons
			},
			doAction: function(item,e){
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if(script){
					$require(script,[function(){
						eval(script + '.do' +cmd +'.apply(this,[item,e])')	
					},this])
				}
				else{
					var action =  this["do"+cmd]
					if(action){
						action.apply(this,[item,e])
					}
				}
			},
			doSave : function(){
				var baseData = {};
				Ext.apply(baseData,this.exContext.args);
				var frmValues = this.baseForm.getFormData();
				var gcId = frmValues.gcId;
				if(!gcId && this.baseForm.initDataId){
					frmValues.gcId=this.baseForm.initDataId;
				}
				Ext.apply(baseData,frmValues);
				this.THQSave(baseData);
			},
			THQSave : function(baseData) {
				baseData.empiId = this.exContext.args.empiId;
				// 问卷模板编号
				baseData.masterplateId = this.exContext.args.masterplateId;
				// 就诊序号
				baseData.JZXH = this.exContext.args.JZXH;
				// 问卷模版类型
				baseData.masterplateType = this.exContext.args.masterplateType;
				var thqData = this.THQForm.getFormData();
				var saveData = {
					baseData : baseData,
					thqData : thqData,
					empiId : this.exContext.args.empiId,
					masterplateId : this.exContext.args.masterplateId
				};
				this.saveToServer(saveData)
			},
			onTHQSave : function(entryName, op, json, data) {
				var body = json.body;
				this.baseForm.initFormData(body);
				this.fireEvent("saveTHQ", entryName, op, json, data);
				var passport = body.passport;
				if (passport) {
					//this.win.hide();
					var itemType = body.itemType;
					this.screeningId = body.screeningId;
					var queryData = {
						itemType : itemType
					}
					var tiiData = this.queryData(queryData);
					var tiiNum = tiiData.tiiNum;
					if (tiiNum && tiiNum > 0) {
						this.showDataInSelectView(itemType)
					}
				} else {
					MyMessageTip.msg("提示", "数据保存成功！", true);
					this.win.hide();
				}
			},
			queryData : function(queryData) {
				this.panel.el.mask("正在查询,请稍后...", "x-mask-loading")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.queryServiceId,
							serviceAction : this.queryServiceActioin,
							method : "execute",
							body : queryData
						})
				this.panel.el.unmask()
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				return result.json["body"];
			},
			showDataInSelectView : function(itemType) {
				var tiiSelectView = this.midiModules["TumourInspectionItemSelectView"];
				var cfg = {
					entryName : this.queryEntryName,
					enableCnd : false,
					autoLoadData : false,
					selectFirst : false,
					modal : true,
					mutiSelect : true,
					isCombined:true,
					title : "检查项目建议",
					width : 600,
					height : 400,
					initCnd : ['eq', ['$', 'itemType'], ['s', itemType]]
				};
				if (!tiiSelectView) {
					var tiiSelectView = new chis.script.util.query.CombinationSelectMultiple(cfg);
					tiiSelectView.on("select", function(rs) {
								var empiId = this.exContext.args.empiId;
								var checkItems = [];
								for (var i = 0, len = rs.length; i < len; i++) {
									var r = rs[i];
									var ci = {};
									ci.screeningId = this.screeningId;
									ci.empiId = empiId;
									ci.itemId = r.get("itemId");
									ci.checkItem = r.get("definiteItemName");
									ci.highRiskType = r.get("itemType");
									checkItems.push(ci);
								}
								this.toSaveCheckItem(checkItems)
								this.win.hide();
							}, this);
					this.midiModules["TumourInspectionItemSelectView"] = tiiSelectView;
					tiiSelectView.initPanel();
				}
				tiiSelectView.requestData.cnd = ['eq', ['$', 'itemType'],
						['s', itemType]];
				var win = tiiSelectView.getWin();
				win.show();
				win.add(tiiSelectView.grid);
				tiiSelectView.loadData();
//				var handleFun = function(tiiSelectView){
//					return function(){
//						var rowNum = tiiSelectView.store.getCount();
//						if(rowNum == 0){
//							tiiSelectView.refresh();
//						}else{
//							var lastOptions = tiiSelectView.store.lastOptions;
//							tiiSelectView.store.reload(lastOptions);
//						}
//					}
//				}
//				setTimeout(handleFun(tiiSelectView),500);
			},
			toSaveCheckItem : function(data) {
				var saveCfg = {
					serviceId : "chis.tumourQuestionnaireService",
					serviceAction : "saveCheckItems",
					method : "execute",
					op : this.op,
					module : this._mId, // 增加module的id
					body : {
						checkItems : data
					}
				}
				util.rmi.jsonRequest(saveCfg, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.toSaveCheckItem, [saveRequest],
										json.body);
								this
										.fireEvent("exception", code, msg,
												saveData); // **进行异常处理
								return
							}
							MyMessageTip.msg("提示", "数据保存成功！", true);
						}, this)
			},
			getLoadRequest : function() {
				var source = "2"
				if (this.exContext.args.JZXH) {
					source = "1";
				}
				var reqBody = {
					gcId : this.exContext.args.gcId || '',
					empiId : this.exContext.args.empiId,
					masterplateId : this.exContext.args.masterplateId,
					source : this.exContext.args.source || source
				};
				return reqBody;
			},
			loadData : function() {
				if (this.baseForm) {
					Ext.apply(this.baseForm.exContext, this.exContext);
					this.baseForm.resetButtons();
				}
				if (this.THQForm) {
					Ext.apply(this.THQForm.exContext, this.exContext);
					this.THQItem.remove(this.THQForm.form);
					var HQForm = this.THQForm.initPanel();
					this.THQItem.add(HQForm);
					this.THQItem.doLayout();
				}
				var masterplateId = this.exContext.args.masterplateId;
				if (masterplateId) {
					this.THQForm.masterplateId = this.exContext.args.masterplateId;
				}
				chis.application.tr.script.phq.TumourHealthQuestionModule.superclass.loadData
						.call(this);
			},
			onLoadData : function(entryName,data){
				var update = data.update;
				if(this.panel.getTopToolbar()){
					var btns = this.panel.getTopToolbar().items;
					if (!btns) {
						return;
					}
					var saveBtn = btns.item(0);
					if(update){
						saveBtn.enable();
					}else{
						saveBtn.disable();
					}
				}
			},
			keyManageFunc : function(keyCode, keyName) {
				var m = this.baseForm;
				if (m) {
					if (m.btnAccessKeys) {
						var btn = m.btnAccessKeys[keyCode];
						if (btn && btn.disabled) {
							return;
						}
						if (!m.btnAccessKeys[keyCode]) {
							return;
						}
						m.doAction(m.btnAccessKeys[keyCode]);
					}
				}
			}
		});