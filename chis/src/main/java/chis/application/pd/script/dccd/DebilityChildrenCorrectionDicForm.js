/**
 * 体弱儿随访指导意见字典页面
 * 
 * @author : yaozh
 */
$package("chis.application.pd.script.dccd")
$import("chis.script.BizTableFormView")
chis.application.pd.script.dccd.DebilityChildrenCorrectionDicForm = function(cfg) {
	cfg.colCount = 2;
	cfg.width = 780;
	chis.application.pd.script.dccd.DebilityChildrenCorrectionDicForm.superclass.constructor
			.apply(this, [cfg]);

}

Ext.extend(chis.application.pd.script.dccd.DebilityChildrenCorrectionDicForm,
		chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.pd.script.dccd.DebilityChildrenCorrectionDicForm.superclass.onReady
						.call(this);
				var diseaseType = this.form.getForm().findField("diseaseType");
				if (diseaseType) {
					diseaseType.on("select", this.onDiseaseSelect, this);
				}
			},

			onDiseaseSelect : function(field) {
				var value = field.getValue();
				if (!value) {
					return;
				}
				this.form.getForm().findField("suggestion").setValue("");
				var res = util.rmi.miniJsonRequestSync({
							serviceId : "chis.debilityChildrenService",
							serviceAction : "getChildCorrection",
							method:"execute",
							body : {
								"diseaseType" : value
							}
						})
				if (res.code == 200) {
					var body = res.json.body;
					if (body) {
						this.initFormData(body);
						this.op = "update";
					}
				} else {
					this.processReturnMsg(res.code, res.msg)
				}
			}

		});