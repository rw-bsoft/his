$package("chis.application.tr.script.seemingly")

$import("chis.script.BizTableFormView")

chis.application.tr.script.seemingly.TumourSeeminglyForm = function(cfg) {
	chis.application.tr.script.seemingly.TumourSeeminglyForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.tr.script.seemingly.TumourSeeminglyForm,
		chis.script.BizTableFormView, {
			getSaveRequest : function(savaData) {
				savaData.empiId = this.exContext.args.empiId;
				if (!savaData.recheckStatus) {
					savaData.recheckStatus = 'n'
				}
				return savaData;
			},

			onWinShow : function() {
				this.setButton(["save"], true)
			},

			onLoadData : function() {
				var data = this.exContext[this.entryName].data;
				var recheck = data.recheckStatus;
				if (recheck == "n") {
					this.setButton(["save"], true)
				} else {
					this.setButton(["save"], false)
				}
			},

			setButton : function(m, flag) {
				var btns;
				var btn;
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
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
							(flag) ? btn.enable() : btn.disable();
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
							(flag) ? btn.enable() : btn.disable();
						}
					}
				}
			}
		});