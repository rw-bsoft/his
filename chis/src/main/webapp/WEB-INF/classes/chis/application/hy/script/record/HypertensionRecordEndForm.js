// 高血压档案创建面板
$package("chis.application.hy.script.record");

$import("chis.script.BizTableFormView");

chis.application.hy.script.record.HypertensionRecordEndForm = function(cfg) {
	chis.application.hy.script.record.HypertensionRecordEndForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.hy.script.record.HypertensionRecordEndForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.hy.script.record.HypertensionRecordEndForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var visitEffect = form.findField("visitEffect");
				this.visitEffect = visitEffect;
				visitEffect.on("select", this.onVisitEffectSelect, this);
				visitEffect.on("keyup", this.onVisitEffectSelect, this);

				var cancellationReason = form.findField("cancellationReason");
				this.cancellationReason = cancellationReason;
				cancellationReason.on("select", this.onReasonSelect, this);
				cancellationReason.on("keyup", this.onReasonSelect, this);
			},
			onWinShow : function() {
				var form = this.form.getForm();
				var visitEffect = form.findField("visitEffect");
				this.visitEffect = visitEffect;
				this.onVisitEffectSelect();
				this.onReasonSelect();
			},
			onVisitEffectSelect : function() {
				var value = this.visitEffect.getValue();
				var form = this.form.getForm();
				var cancellationReason = form.findField("cancellationReason");
				this.cancellationReason = cancellationReason;
				if (value == "9") {
					cancellationReason.allowBlank = false
					cancellationReason.enable();
				} else {
					cancellationReason.allowBlank = true
					cancellationReason.setValue();
					cancellationReason.disable();
				}
				this.onReasonSelect()
			},
			onReasonSelect : function() {
				var value = this.cancellationReason.getValue();
				var form = this.form.getForm();
				var deadReason = form.findField("deadReason");
				var deadDate = form.findField("deadDate");
				if (value == "1") {
					deadReason.allowBlank = false
					deadDate.allowBlank = false
					deadReason.enable();
					deadDate.enable();
				} else {
					deadReason.allowBlank = true
					deadDate.allowBlank = true
					deadReason.setValue();
					deadReason.disable();
					deadDate.setValue();
					deadDate.disable();
				}
				this.validate();
			}
		});