$package("phis.application.cfg.script")
$import("phis.script.SimpleList")
phis.application.cfg.script.AntimicrobialDrugUseCausesList = function(cfg) {
	cfg.showRowNumber = true;
	phis.application.cfg.script.AntimicrobialDrugUseCausesList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.AntimicrobialDrugUseCausesList,
		phis.script.SimpleList, {
			refresh : function() {
				phis.application.cfg.script.AntimicrobialDrugUseCausesList.superclass.refresh
						.apply(this, [])
				this.reloadYY();
			},
			doAction : function(item, e) {
				// 点修改按钮，先判断该抗菌药物使用原因有没有被使用
				if (item.cmd == "update") {
					var r = this.getSelectedRecord();
					phis.script.rmi.jsonRequest({
								serviceId : this.serviceId,
								serviceAction : "queryIfUsed",
								syyy : r.get("DYMC")
							}, function(code, msg, json) {
								this.grid.el.unmask()
								if (code >= 300) {
									this.processReturnMsg(code, msg);
									return;
								}
								if (json.canUpdate == false) {
									Ext.Msg.alert("提示", "该抗菌药使用原因已被使用，不能修改！");
									return;
								}
								phis.application.cfg.script.AntimicrobialDrugUseCausesList.superclass.doAction
										.apply(this, [item, e])
							}, this)

				} else {
					phis.application.cfg.script.AntimicrobialDrugUseCausesList.superclass.doAction
							.apply(this, [item, e])
				}

			},
			doRemove : function() {
				var r = this.getSelectedRecord();
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							pkey : r.get("DYID"),
							syyy : r.get("DYMC")
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.canDelete == false) {
								Ext.Msg.alert("提示", "该抗菌药使用原因已被使用，不能删除！");
							} else {
								MyMessageTip.msg("提示", "删除成功!", true);
								this.refresh();
							}
						}, this)
			},
			// 重新读取抗菌药物使用原因字典
			reloadYY : function() {
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "reloadYY",
							id : "phis.dictionary.AntibacterialUseReason"
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								Ext.Msg.alert("提示", "抗菌药使用原因字典重载失败 ！");
							}
						}, this)
			}

		})
