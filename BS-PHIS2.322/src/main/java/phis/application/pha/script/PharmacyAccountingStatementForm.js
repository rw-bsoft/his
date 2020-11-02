$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyAccountingStatementForm = function(cfg) {
	cfg.width = 400;
	cfg.colCount = 2;
	phis.application.pha.script.PharmacyAccountingStatementForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.pha.script.PharmacyAccountingStatementForm,
		phis.script.TableForm, {
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryDataActionId
						});
				if (this.form && this.form.el) {
					this.form.el.unmask()
				}
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doPreserve);
					this.setButtonsState(['monthly'], false);
				} else {
					this.setButtonsState(['monthly'], true);
				}
				this.initFormData(ret.json.body);
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.form.getTopToolbar();
				} else {
					btns = this.form.buttons;
				}

				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			doCancel : function() {
				// onEsc();
				this.fireEvent("close", this)
			},
			doMonthly : function() {
				var values = this.getFormData();
				this.form.el.mask("正在保存数据...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : values
						});
				this.form.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doPreserve);
					return;
				}
				this.fireEvent("save", this);
				this.doCancel();
			}
		});