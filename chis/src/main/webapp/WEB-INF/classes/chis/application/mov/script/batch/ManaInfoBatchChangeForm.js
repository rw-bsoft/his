/**
 * 批量修改管理医生表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.batch")
$import("chis.script.BizTableFormView")
chis.application.mov.script.batch.ManaInfoBatchChangeForm = function(cfg) {
	chis.application.mov.script.batch.ManaInfoBatchChangeForm.superclass.constructor.apply(
			this, [cfg]);
};
Ext.extend(chis.application.mov.script.batch.ManaInfoBatchChangeForm, chis.script.BizTableFormView,
		{

			onReady : function() {
				
				chis.application.mov.script.batch.ManaInfoBatchChangeForm.superclass.onReady
						.call(this);
				var targetDoctor = this.form.getForm()
						.findField("targetDoctor");
				if (targetDoctor) {
					targetDoctor.on("select", this.changeManaUnit, this);
				}
			},

			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method:"execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				this.setManaUnit(result.json.manageUnit)
			},

			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("targetUnit");
				if (!combox) {
					return;
				}

				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}

				combox.setValue(manageUnit)
				combox.disable();
			},

			setGroupBtnVisible : function(groupName, visible) {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount()
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					if (btn) {
						var group = btn.prop.group;
						if (!group) {
							return;
						}
						if (group == groupName) {
							btn.setVisible(visible)
						} else {
							btn.setVisible(!visible)
						}
					}
				}
			},

			setBtnApply : function() {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var count = btns.getCount();
				var saveBtn = btns.item(0)
				if (saveBtn) {
					saveBtn.enable();
				}

				if (count <= 2) {
					return;
				}

				var confirmBtn = btns.item(1)
				if (confirmBtn) {
					confirmBtn.disable();
				}

				var rejectBtn = btns.item(2)
				if (rejectBtn) {
					rejectBtn.disable();
				}
			},

			doSave : function() {
			
				this.fireEvent("save", this);
			},

			doCancel : function() {
				this.fireEvent("cancel", this);
			},

			doConfirm : function() {
				this.fireEvent("confirm", this);
			},

			doReject : function() {
				this.fireEvent("reject", this);
			}

		})