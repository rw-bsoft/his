$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.StorageOfMaterialsForm = function(cfg) {
	cfg.showButtonOnTop = false;
	cfg.remoteUrl = "Manufacturer";
	cfg.remoteTpl = '<td width="6px" style="background-color:#deecfd">{numKey}.</td><td width="140px">{DWMC}';
	phis.application.sup.script.StorageOfMaterialsForm.superclass.constructor
			.apply(this, [ cfg ])
	this.on("loadData", this.onLoadData, this);
}
Ext
		.extend(
				phis.application.sup.script.StorageOfMaterialsForm,
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
						this.form.getForm().findField("DWMC").enable();
						// this.focusFieldAfter(-1, 800)
						this.afterDoNew();
						this.resetButtons();
					},
					onLoadData : function() {
						if (this.getFormData()) {
							this.form.getForm().findField("DWMC").setValue(
									this.DWMC);
							this.data["DWMC"] = this.DWMC;
							if (this.getFormData().PDDJ > 0) {
								this.form.getForm().findField("DWMC").disable();
							}
							var thdj = 0;
							var sig = 0;
							if (this.getFormData().THDJ != "0") {
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
								+ "['eq',['$','item.properties.DJLX'],['s','RK']]],"
								+ "['eq',['$','item.properties.YWLB'],['i',1]]],"
								+ "['eq',['$','item.properties.TSBZ'],['i',0]]],"
								+ "['eq',['$','item.properties.FSZT'],['i',1]]]";

						if (op == "back" || op != 0) {
							filters = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"
									+ this.mainApp['phis'].treasuryId
									+ "]],"
									+ "['eq',['$','item.properties.DJLX'],['s','RK']]],"
									+ "['eq',['$','item.properties.YWLB'],['i',-1]]],"
									+ "['eq',['$','item.properties.FSZT'],['i',1]]]";
						}
						if (op == "pd") {
							filters = "['and',['and',['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"
									+ this.mainApp['phis'].treasuryId
									+ "]],"
									+ "['eq',['$','item.properties.DJLX'],['s','RK']]],"
									+ "['eq',['$','item.properties.YWLB'],['i',1]]],"
									+ "['eq',['$','item.properties.TSBZ'],['i',1]]],"
									+ "['eq',['$','item.properties.DJQZ'],['s','PY']]],"
									+ "['eq',['$','item.properties.FSZT'],['i',1]]]";
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
					getRemoteDicReader : function() {
						return new Ext.data.JsonReader({
							root : 'mats',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mtssearch'
						}, [ {
							name : 'numKey'
						}, {
							name : 'DWMC'
						}, {
							name : 'DWXH'
						} ]);
					},
					// 快速录入后 数据回填
					setBackInfo : function(obj, record) {
						obj.collapse();
						this.form.getForm().findField("DWMC").setValue(
								record.get("DWMC"));
						this.data["DWXH"] = {
							"text" : record.get("DWMC"),
							"key" : record.get("DWXH")
						};
					}

				})