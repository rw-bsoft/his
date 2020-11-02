$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.AllocationManagementForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.AllocationManagementForm.superclass.constructor.apply(
			this, [cfg])
	this.on("loadData", this.onLoadData, this);
}
Ext.extend(phis.application.sup.script.AllocationManagementForm,
		phis.script.TableForm, {
			onLoadData : function() {
				if (this.getFormData()) {
					if (this.getFormData().DJZT == 1) {
						if (this.btn.getText().indexOf("弃审") > -1) {
							return;
						}
						this.btn
								.setText(this.btn.getText().replace("审核", "弃审"));
					} else if (this.getFormData().DJZT == 0) {
						this.btn
								.setText(this.btn.getText().replace("弃审", "审核"));
					}
					var thdj = 0;
					if (this.getFormData().THDJ) {
						thdj = this.getFormData().THDJ;
					}
					this.doIs(thdj);
				} else {
					this.doIs(0);
				}
			},
			doIs : function(op) {
				var field = this.form.getForm().findField("LZFS");
				var filters = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"
						+ this.mainApp['phis'].treasuryId
						+ "]],['eq',['$','item.properties.DJLX'],['s','DB']]],['eq',['$','item.properties.YWLB'],['i',-1]]],['eq',['$','item.properties.TSBZ'],['i',0]]]";
				if (op == "back" || op != 0) {
					filters = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"
							+ this.mainApp['phis'].treasuryId
							+ "]],['eq',['$','item.properties.DJLX'],['s','DB']]],['eq',['$','item.properties.YWLB'],['i',1]]],['eq',['$','item.properties.TSBZ'],['i',0]]]";
				}
				field.store.removeAll();
				field.store.proxy = new Ext.data.HttpProxy({
							method : "GET",
							url : util.dictionary.SimpleDicFactory.getUrl({
										id : "phis.dictionary.transfermodes",
										filter : filters
									})
						})
				if (!this.getFormData()) {
					var defaultIndex = 0;
					field.store.on("load", function() {
						if (defaultIndex != null) {
							field.setValue(this.getAt(defaultIndex).get('key'));
							defaultIndex = null;
						}
					})
				}
				field.store.load()
			}
		})