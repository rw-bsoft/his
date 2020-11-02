$package("chis.application.def.script.brain")
$import("util.Accredit", "chis.script.BizTableFormView",
		"chis.script.util.helper.Helper", "chis.script.ICCardField",
		"util.widgets.LookUpField", "chis.script.util.Vtype")
chis.application.def.script.brain.BrainTerminalEvaluateForm = function(cfg) {
	this.entryName = 'chis.application.def.schemas.DEF_BrainTerminalEvaluate'
	chis.application.def.script.brain.BrainTerminalEvaluateForm.superclass.constructor.apply(this,
			[cfg])
	this.nowDate = this.mainApp.serverDate
	this.on("winShow",this.onWinShow,this)
	this.on("loadData",this.onLoadData,this)
	this.on("doNew",this.onDoNew,this)
	this.saveServiceId = "chis.defBrainService"
	this.saveAction = "saveBrainTerminalEvaluate"
}
Ext.extend(chis.application.def.script.brain.BrainTerminalEvaluateForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.def.script.brain.BrainTerminalEvaluateForm.superclass.onReady
						.call(this)

				var healingTarget = this.form.getForm()
						.findField("healingTarget")
				healingTarget.on("expand",this.onHealingTargetExpand,this)
				healingTarget.tree.on("checkchange", this.onHealingTarget, this)
				this.healingTarget = healingTarget
				
				var lastScoreF = this.form.getForm().findField("lastScore")
				lastScoreF.on("select", this.onCalculateScore, this)
				lastScoreF.on("blur", this.onCalculateScore, this)
				lastScoreF.on("keyup", this.onCalculateScore, this)
				this.lastScoreF = lastScoreF
			},
			onHealingTargetExpand:function(combo){
				combo.tree.expandAll();
			}
			,
			onHealingTarget : function(node) {
				var parentNode = node.parentNode
				var nodes = parentNode.childNodes
				for (var i = 0; i < nodes.length; i++) {
					if (nodes[i].id != node.id) {
						nodes[i].getUI().check(false);
					}
				}
			},
			doSave:function(){
				Ext.Msg.show({
					title : '提示信息',
					msg : '结案后该份档案将无法操作，是否继续？',
					modal : true,
					minWidth : 300,
					maxWidth : 600,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if(btn == 'yes'){
							chis.application.def.script.brain.BrainTerminalEvaluateForm.superclass.doSave.call(this)
						}
					},
					scope : this
				})
			}
			,
			saveToServer : function(saveData) {
				if (this.healingTarget.getValue().split(",").length != 4) {
					Ext.Msg.alert("消息", "康复目标必须选择四项")
					return
				}
				saveData.defId = this.exContext.r.get("id")
				saveData.empiId = this.exContext.ids.empiId
				saveData.phrId = this.exContext.ids.phrId
				chis.application.def.script.brain.BrainTerminalEvaluateForm.superclass.saveToServer.call(this,saveData)
				if(this.win){
					this.win.hide()
				}
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : 800,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : true,
								constrain:true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true,
								items : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			}
			,
			onLoadData:function(){
				this.validate()
			}
			,
			loadData : function() {
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading");
				}
				if(this.result){
					this.loadDataByLocal(this.result)
				}else{
					var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.defBrainService",
							serviceAction:"queryBrainTerminalEvaluate",
							method:"execute",
							schema : "chis.application.def.schemas.DEF_BrainTerminalEvaluate",
							cnd : ["eq", ["$", "defId"],
									["s", this.defId]]
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.loadData);
					return
				}
				if (result.json.body) {
					this.initFormData(result.json.body);
					this.fireEvent("loadData", this.entryName, result.json.body);
				} else {
					this.initDataId = null;
					// **
					// 没有加载到数据，通常用于以fieldName和fieldValue为条件去加载记录，如果没有返回数据，则为新建操作，此处可做一些新建初始化操作
					this.fireEvent("loadNoData", this);
				}
				if (this.op == 'create') {
					this.op = "update";
				}
				}
				
				
				this.form.el.unmask()
//				chis.application.def.script.brain.BrainTerminalEvaluateForm.superclass.loadData.call(this)
			}
			,
			onCalculateScore:function(){
				if(!this.firstScore){
					return
				}
				var score = Number(this.lastScoreF.getValue()) -  Number(this.firstScore)
				this.form.getForm().findField("updateScore").setValue(score)
			}
			,
			onDoNew:function(){
				this.form.getForm().findField("lastScore").setValue(this.lastScore)
				this.form.getForm().findField("updateScore").setValue(Number(this.lastScore)-Number(this.firstScore))
			}
			,
			onWinShow:function(){
				this.loadData()
			}
		});