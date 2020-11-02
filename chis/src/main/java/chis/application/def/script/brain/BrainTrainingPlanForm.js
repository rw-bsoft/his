$package("chis.application.def.script.brain");

$import("chis.script.BizTableFormView");

chis.application.def.script.brain.BrainTrainingPlanForm = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_BrainTrainingPlan"
	cfg.colCount = 3;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 120
	cfg.labelWidth = 80
	cfg.showButtonOnTop = true;
	chis.application.def.script.brain.BrainTrainingPlanForm.superclass.constructor.apply(this, [cfg]);
	this.saveServiceId = "chis.defBrainService"
	this.saveAction = "saveBrainTrainingPlan"
};

Ext.extend(chis.application.def.script.brain.BrainTrainingPlanForm, chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.def.script.brain.BrainTrainingPlanForm.superclass.onReady.call(this)

				var healingTarget = this.form.getForm()
						.findField("healingTarget")
				healingTarget.on("expand",this.onHealingTargetExpand,this)
				healingTarget.tree
						.on("checkchange", this.onHealingTarget, this)
				this.healingTarget = healingTarget
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
			saveToServer : function(saveData) {
				if (this.healingTarget.getValue().split(",").length != 4) {
					Ext.Msg.alert("消息", "康复目标必须选择四项")
					return
				}
				saveData.defId = this.exContext.r.get("id")
				saveData.empiId = this.exContext.ids.empiId
				saveData.phrId = this.exContext.ids.phrId
				chis.application.def.script.brain.BrainTrainingPlanForm.superclass.saveToServer.call(this,saveData)
			},
			doCreate : function() {
				this.fireEvent("create")
			}
		});