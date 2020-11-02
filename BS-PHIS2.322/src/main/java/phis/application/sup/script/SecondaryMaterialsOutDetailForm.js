$package("phis.application.sup.script")

$import("phis.script.TableForm")

phis.application.sup.script.SecondaryMaterialsOutDetailForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.SecondaryMaterialsOutDetailForm.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this);
}
Ext.extend(phis.application.sup.script.SecondaryMaterialsOutDetailForm,
		phis.script.TableForm, {
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
		for (var i = 0; i < n; i++) {
			var it = items[i]
			var f = form.findField(it.id)
			if (f) {
				if (!(arguments[0] == 1)) { // whether set defaultValue,
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
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.initDataId && !this.initDataBody) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
					serviceId : this.loadServiceId,
					method:"execute",
					schema : this.entryName,
					pkey : this.initDataId,
					body : this.initDataBody,
					action : this.op, // 按钮事件
					module : this._mId
						// 增加module的id
					}, function(code, msg, json) {
					if (this.form && this.form.el) {
						this.form.el.unmask()
					}
					this.loading = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.loadData)
						return
					}
					if (json.body) {
						this.doNew()
						this.initFormData(json.body)
						// 查询病人姓名，来源
						var initDJXH = {};
						initDJXH["DJXH"] = this.initDataId
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : "secondaryMaterialsOutService",
									serviceAction : "getBrxxByDjxh",
									method:"execute",
									body : initDJXH
								});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							return false;
						} else {
							this.form.getForm().findField("BRXM")
									.setValue(r.json.BRXM);
							if (r.json.BRLY == 1) {
								this.form.getForm().findField("BRLY")
										.setValue("门诊");
							}
							if (r.json.BRLY == 2) {
								this.form.getForm().findField("BRLY")
										.setValue("住院");
							}

							this.fireEvent("loadData", this.entryName,
									json.body);
						}
					}
					if (this.op == 'create') {
						this.op = "update"
					}

				}, this)// jsonRequest
			},
			onLoadData : function() {
				if (this.getFormData()) {
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
				var filters = "['and',['and', ['and',['and',['eq',['$','item.properties.KFXH'],['i',"
						+ this.mainApp['phis'].treasuryId
						+ "]],['eq',['$','item.properties.DJLX'],['s','CK']]],['eq',['$','item.properties.YWLB'],['i',-1]]],['eq', ['$','item.properties.TSBZ'],['i', 0]]],['eq', ['$','item.properties.FSZT'],['i', 1]]]";
				if (op == "back" || op != 0) {
					filters = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"
							+ this.mainApp['phis'].treasuryId
							+ "]],['eq',['$','item.properties.DJLX'],['s','CK']]],['eq',['$','item.properties.YWLB'],['i',1]]],['eq', ['$','item.properties.FSZT'],['i', 1]]]";
				}
				if (op == "pd") {
					filters = "['and',['and', ['and',['and',['eq',['$','item.properties.KFXH'],['i',"
							+ this.mainApp['phis'].treasuryId
							+ "]],['eq',['$','item.properties.DJLX'],['s','CK']]],['eq',['$','item.properties.YWLB'],['i',-1]]], ['eq', ['$','item.properties.TSBZ'], ['i', 1]]],['eq', ['$','item.properties.FSZT'],['i', 1]]]";
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