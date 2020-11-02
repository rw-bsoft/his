$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.ForRegistrationForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.ForRegistrationForm.superclass.constructor
			.apply(this, [ cfg ])
	this.on("loadData", this.onLoadData, this);
}
Ext
		.extend(
				phis.application.sup.script.ForRegistrationForm,
				phis.script.TableForm,
				{
					// add by yangl 重写doNew 防止光标乱跳的问题
					doNew : function() {
						this.op = "create"
						if (this.data) {
							this.data = {}
						}
						if (!this.schema) {
							return;
						}
						var form = this.form.getForm();
						form.reset();
						var items = this.schema.items
						var n = items.length
						for ( var i = 0; i < n; i++) {
							var it = items[i]
							var f = form.findField(it.id)
							if (f) {
								if (!(arguments[0] == 1)) { // whether set
									// defaultValue,
									// it will be setted when
									// there is no args.
									var dv = it.defaultValue;
									if (dv) {
										if ((it.type == 'date' || it.xtype == 'datefield')
												&& typeof dv == 'string'
												&& dv.length > 10) {
											dv = dv.substring(0, 10);
										}
										f.setValue(dv);
									}
								}
								if (!it.update && !it.fixed && !it.evalOnServer) {
									f.enable();
								}
								f.validate();
							}
						}
						this.setKeyReadOnly(false)
						this.startValues = form.getValues(true);
						this.fireEvent("doNew", this.form)
						// this.focusFieldAfter(-1, 800)
						this.afterDoNew();
						this.resetButtons();
					},
					onReady : function() {
						phis.application.sup.script.ForRegistrationForm.superclass.onReady
								.call(this)
						var form = this.form.getForm();
						var KFXH = form.findField("KFXH");
						if (KFXH) {
							KFXH.on("select", this.onSelectKFXH, this);
							KFXH.on("blur", this.onSelectKFXH, this);
						}
					},
					onSelectKFXH : function() {
						var form1 = this.form.getForm();
						var KFXH1 = form1.findField("KFXH");
						var kfxhvalue = 0;
						if (KFXH1.getValue()) {
							kfxhvalue = KFXH1.getValue();
						}
						this.listoper.remoteDicStore.baseParams = {
							"zblb" : 0,
							"kfxh" : kfxhvalue
						}
					},
					onLoadData : function() {
						var form = this.form.getForm();
						this.KFXH = form.findField("KFXH");
						if (this.KFXH.getValue()) {
							this.listoper.remoteDicStore.baseParams = {
								"zblb" : 0,
								"kfxh" : this.KFXH.getValue()
							}
						}
					},
					setQRRK : function(thdj) {
						var field = this.form.getForm().findField("QRRK");
						var filters = "";
						if (thdj > 0) {
							filters = "['and',['and',['eq',['$','item.properties.KFXH'],['i',"
									+ this.mainApp['phis'].treasuryId
									+ "]],"
									+ "['eq',['$','item.properties.DJLX'],['s','DR']]],"
									+ "['eq',['$','item.properties.YWLB'],['i',-1]]]"
						} else {
							filters = "['and',['and',['eq',['$','item.properties.KFXH'],['i',"
									+ this.mainApp['phis'].treasuryId
									+ "]],"
									+ "['eq',['$','item.properties.DJLX'],['s','DR']]],"
									+ "['eq',['$','item.properties.YWLB'],['i',1]]]"
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
									field.setValue(this.getAt(defaultIndex)
											.get('key'));
									defaultIndex = null;
								}
							})
						}
						field.store.load()
					}
				})