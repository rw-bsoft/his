/**
 * 修改各档责任医生表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.manage")
$import("chis.script.BizTableFormView", "util.widgets.LookUpField")
chis.application.mov.script.manage.ManaInfoChangeForm = function(cfg) {
	chis.application.mov.script.manage.ManaInfoChangeForm.superclass.constructor.apply(this,
			[cfg]);
};
Ext.extend(chis.application.mov.script.manage.ManaInfoChangeForm, chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.mov.script.manage.ManaInfoChangeForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var personNameField = form.findField("personName");
				if (personNameField) {
					personNameField.on("lookup", this.doQuery, this)
					personNameField.on("clear", this.doNew, this)
				}
			},

			doQuery : function(field) {
				if (!field.disabled) {
					var m = this.midiModules["EMPIInfoModule"];
					if (!m) {
						$import("chis.application.mpi.script.EMPIInfoModule")
						m = new chis.application.mpi.script.EMPIInfoModule({
									entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
									title : "个人基本信息查询",
									height : 450,
									modal : true,
									mainApp : this.mainApp
								})
						m.on("onEmpiReturn", this.onPeopleSelect, this)
						this.midiModules["EMPIInfoModule"] = m;
					}
					var win = m.getWin();
					win.setPosition(250, 100);
					win.show();
				}
			},

			onPeopleSelect : function(data) {
				var form = this.form.getForm();
				var personName = form.findField("personName");
				personName.setValue(data.personName);
				this.data.idCard = data.idCard;
				this.data.empiId = data.empiId;
				this.fireEvent("queryEmpiId", data.empiId);
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
			},

			setLookUpDisabled : function(disabled) {
				var personNameField = this.form.getForm()
						.findField("personName");
				if (personNameField) {
					personNameField.setDisabled(disabled);
				}
			},

			setGroupBtnVisible : function(groupName, visible) {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i);
					if (btn) {
						var group = btn.prop.group;
						if (!group) {
							return;
						}
						if (group == groupName) {
							btn.setVisible(visible);
						} else {
							btn.setVisible(!visible);
						}
					}
				}
			},

			setBtnApply : function() {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;

				var count = btns.getCount();
				if (!btns) {
					return;
				}
				var saveBtn = btns.item(0);
				if (saveBtn) {
					saveBtn.enable();
				}

				if (count <= 2) {
					return;
				}

				var confirmBtn = btns.item(1);
				if (confirmBtn) {
					confirmBtn.disable();
				}

				var rejectBtn = btns.item(2);
				if (rejectBtn) {
					rejectBtn.disable();
				}
			}

		})