$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.FaultyConForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.FaultyConForm.superclass.constructor.apply(
			this, [ cfg ])
	this.on("loadData", this.onLoadData, this);
}
Ext
		.extend(
				phis.application.sup.script.FaultyConForm,
				phis.script.TableForm,
				{
					initPanel : function(sc) {
						var form = phis.application.sup.script.FaultyConForm.superclass.initPanel
								.call(this, sc)
						var bsfscomb = form.getForm().findField("BSFS");
						var DJZT = this.form.getForm().findField("DJZT");
						DJZT.hide();
						var _tis = this;
						bsfscomb.on("change", function(combo, record, index) {
							var bskscomb = form.getForm().findField("BSKS");
							var value = combo.getValue();
							if (value == 0) {// 在库
								bskscomb.setValue("");
								bskscomb.disable();
							} else if (value == 1) {
								bskscomb.setValue("");
								bskscomb.enable();
							}
							if (_tis.newlist) {
								_tis.newlist.clear();
								_tis.newlist.editRecords = [];
							}
						});
						return form;
					},
					onLoadData : function() {
						var value = this.form.getForm().findField("BSFS")
								.getValue();
						var bskscomb = this.form.getForm().findField("BSKS");
						var DJZT = this.form.getForm().findField("DJZT")
								.getValue();
						this.form.getForm().findField("DJZT").hide();
						if (DJZT > 0) {
							this.form.getForm().findField("BSFS").disable();
							this.form.getForm().findField("BSKS").disable();
						} else {
							if (value == 0) {// 在库
								bskscomb.setValue("");
								bskscomb.disable();
							} else if (value == 1) {
								bskscomb.enable();
							}
							var bsfs = this.form.getForm().findField("BSFS");
							// bsfs.on("select", this.onSelect, this);
							bsfs.on("change", this.onSelect, this);
						}
					},
					onSelect : function() {
						if (this.list) {
							this.list.clear();
							this.list.editRecords = [];
						}
					}
				})