$package("chis.application.sch.script");

$import("app.modules.form.TableFormView", "util.rmi.loadJsonAsync",
		"util.rmi.jsonRequest");

chis.application.sch.script.TestFormView = function(cfg) {

	chis.application.sch.script.TestFormView.superclass.constructor.apply(this, [cfg]);

};

Ext.extend(chis.application.sch.script.TestFormView, app.modules.form.TableFormView, {

			onReady : function() {
				chis.application.sch.script.TestFormView.superclass.onReady.call(this);
				this.setButtonsReadOnly(['save',2], false);
			},

			setButtonsReadOnly : function(m, readOnly) {

				var btns;
				var btn;
				if (this.showButtonOnTop &&  this.form.getTopToolbar()) {
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
							(readOnly) ? btn.enable() : btn.disable();
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
							(readOnly) ? btn.enable() : btn.disable();
						}
					}
				}
			}

		});
