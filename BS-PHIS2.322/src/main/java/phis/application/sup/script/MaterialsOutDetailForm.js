$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.MaterialsOutDetailForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.MaterialsOutDetailForm.superclass.constructor
			.apply(this, [ cfg ])
	this.on("loadData", this.onLoadData, this);
}
Ext
		.extend(
				phis.application.sup.script.MaterialsOutDetailForm,
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
					onLoadData : function() {
						var LZFS = this.form.getForm().findField("LZFS")
								.getValue();
						var TSBZ = this.searchLzfs_Tsbz(LZFS);
						if (TSBZ == 1) {
							this.form.getForm().findField("KSDM").disable();
							this.form.getForm().findField("JBGH").disable();
						}
						if (this.getFormData()) {
							if (this.getFormData().DJZT == 1) {
								if (this.btn.getText().indexOf("弃审") > -1) {
									return;
								}
								this.btn.setText(this.btn.getText().replace(
										"审核", "弃审"));
							} else if (this.getFormData().DJZT == 0) {
								this.btn.setText(this.btn.getText().replace(
										"弃审", "审核"));
							}

							var thdj = 0;
							var sig = 0;
							if (this.getFormData().THDJ) {
								thdj = this.getFormData().THDJ;
								sig = 1;
							}
							if (sig == 0) {
								if (this.getFormData().PDDJ > 0) {
									thdj = "pd"
								}
							}
							this.doIs(thdj);

						}
					},
					doIs : function(op) {
						var field = this.form.getForm().findField("LZFS");
						var filters = "['and',['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"
								+ this.mainApp['phis'].treasuryId
								+ "]],"
								+ "['eq',['$','item.properties.DJLX'],['s','CK']]],"
								+ "['eq',['$','item.properties.YWLB'],['i',-1]]],"
								+ "['eq',['$','item.properties.FSZT'],['i',1]]],"
								+ "['eq',['$','item.properties.TSBZ'],['i',0]]]";
						if (op == "back" || op != 0) {
							filters = "['and',['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"
									+ this.mainApp['phis'].treasuryId
									+ "]],"
									+ "['eq',['$','item.properties.DJLX'],['s','CK']]],"
									+ "['eq',['$','item.properties.YWLB'],['i',1]]],"
									+ "['eq',['$','item.properties.FSZT'],['i',1]]],"
									+ "['eq',['$','item.properties.TSBZ'],['i',0]]]";
						}
						if (op == "pd") {
							filters = "['and',['and',['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"
									+ this.mainApp['phis'].treasuryId
									+ "]],"
									+ "['eq',['$','item.properties.DJLX'],['s','CK']]],"
									+ "['eq',['$','item.properties.YWLB'],['i',-1]]],"
									+ "['eq',['$','item.properties.TSBZ'],['i',1]]],"
									+ "['eq',['$','item.properties.FSZT'],['i',1]]],"
									+ "['eq',['$','item.properties.DJQZ'],['s','PK']]]";

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
					},
					searchLzfs_Tsbz : function(LZFS) {
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "materialsOutService",
							serviceAction : "queryLzfs_Tsbz",
							LZFS : LZFS
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doLoadReport);
						}
						return ret.json.ret;
					}
				})