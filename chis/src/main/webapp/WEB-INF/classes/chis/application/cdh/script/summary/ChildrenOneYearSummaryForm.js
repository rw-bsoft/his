/**
 * 儿童三周岁小结表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.summary")
$import("chis.script.BizTableFormView")
chis.application.cdh.script.summary.ChildrenOneYearSummaryForm = function(cfg) {
	cfg.colCount = 4
	cfg.autoFieldWidth = false
	cfg.autoLodaSchema = true
	cfg.isCombined = true
	cfg.fldDefaultWidth = 155
	chis.application.cdh.script.summary.ChildrenOneYearSummaryForm.superclass.constructor
			.apply(this, [cfg])
	this.initServiceAction = "initChildrenOneYearSummaryRecord"
	this.on("loadNoData", this.onLoadNoData, this)
	this.on("save", this.onSave, this);
}

Ext.extend(chis.application.cdh.script.summary.ChildrenOneYearSummaryForm,
		chis.script.BizTableFormView, {

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"fieldName" : "phrId",
					"fieldValue" : this.exContext.ids["CDH_HealthCard.phrId"]
				};
			},

			onLoadNoData : function(empiId) {
				this.form.el.mask("正在初始化数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initServiceAction,
							method:"execute",
							body : {
								"empiId" : this.exContext.ids.empiId
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body) {
								this.initFormData(body);
								this.initDataId = null;
							}
						}, this)
			},

			doCreate : function() {
				this.form.el.mask("正在处理数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "autoCreateSummaryRecord",
							method:"execute",
							body : {
								"phrId" : this.exContext.ids["CDH_HealthCard.phrId"]
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body) {
								if (this.initDataId) {
									Ext.Msg.show({
												title : '记录已存在',
												msg : '已存在周岁小结，是否覆盖?',
												modal : false,
												width : 300,
												buttons : Ext.MessageBox.YESNO,
												multiline : false,
												fn : function(btn, text) {
													if (btn == "yes") {
														this.initFormData(body);
													} else {
														return
													}
												},
												scope : this
											})
								} else {
									this.initFormData(body);
									this.initDataId = null;
								}
							} else {
								Ext.Msg.alert("提示信息", "无任何体检记录，无法生成小结！")
								return
							}
						}, this)

			},

			onReady : function() {
				chis.application.cdh.script.summary.ChildrenOneYearSummaryForm.superclass.onReady
						.call(this)
				var form = this.form.getForm()
				var summaryDoctor = form.findField("summaryDoctor")
				if (summaryDoctor) {
					summaryDoctor.on("select", this.onSummaryUnitChange, this)
				}
			},

			onSummaryUnitChange : function(combo, node) {
				if (node.leaf && node.parentNode != null) {
					combo = this.form.getForm().findField("summaryUnit")
					combo.setValue({
								key : node.parentNode.id,
								text : node.parentNode.text
							})
				}
			},

			onSave : function(entryName, op, json, data) {
				this.fireEvent("refreshData", "H_01");
			}
		})