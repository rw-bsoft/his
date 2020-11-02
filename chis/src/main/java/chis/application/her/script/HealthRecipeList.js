$package("chis.application.her.script");

$import("chis.script.BizSimpleListView");

chis.application.her.script.HealthRecipeList = function(cfg) {
	if(cfg.actions==undefined)
	{
		cfg.actions = [{
				id : "update",
				name : "查看",
				ref : "chis.application.her.HER/HER/HE04_01"
			}]
	}
	cfg.listServiceId = "chis.healthRecipelManageService";
	cfg.listAction = "listHealthRecipel";
	chis.application.her.script.HealthRecipeList.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.her.script.HealthRecipeList,
		chis.script.BizSimpleListView, {
			onStoreBeforeLoad : function(store, op) {
				var r = this.getSelectedRecord()
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				if (this.requestData.cnd == null) {
					if (this.entryName == "chis.application.her.schemas.HER_HealthRecipeRecord_GR") {
						this.initCnd = ['eq', ['$', 'a.empiId'],
								['s', this.exContext.ids.empiId]];
						this.requestData.cnd = ['eq', ['$', 'a.empiId'],
								['s', this.exContext.ids.empiId]];
					} else {
						this.initCnd = null;
					}
				} else {
					if (this.entryName == "chis.application.her.schemas.HER_HealthRecipeRecord_GR") {
						this.initCnd = ['eq', ['$', 'a.empiId'],
								['s', this.exContext.ids.empiId]];
						this.requestData.cnd = this.requestData.cnd;
					} else {
						this.initCnd = null;
					}
				}
			},
			doCndQuery : function(button, e, addNavCnd) {
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var f = this.cndField
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v === "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.refresh()
					return
				}
				if (f.getXType() == "datefield") {
					v = v.format("Y-m-d")
				}
				if (f.getXType() == "datetimefield") {
					v = v.format("Y-m-d H:i:s")
				}
				// 替换'，解决拼sql语句查询的时候报错
				v = v.replace(/'/g, "''")
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							cnd[0] = 'like'
							cnd.push(['s', '%' + v + '%'])
							break;
						case "date" :
							// v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"to_char(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (it.xtype == "datefield") {
								// v = v.format("Y-m-d")
								cnd[1] = [
										'$',
										"to_char(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd')"]
								cnd.push(['s', v])
							} else {
								// v = v.format("Y-m-d H:i:s")
								cnd[1] = [
										'$',
										"to_char(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd HH:mm:ss')"]
								cnd.push(['s', v])
							}

							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					if (xy) {
						win.setPosition(xy[0] || this.xy[0], xy[1]
										|| this.xy[1])
					}
					win.setTitle(module.title)
					win.show()
					this.fireEvent("openModule", module)
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								module.doCreate()
								break;
							case "read" :
							case "update" :
								var formData = this.castListDataToForm(r.data,
										this.schema);
								module.initFormData(formData);
						}
					}
				}
			},
			fixRequestData : function(requestData) {
				requestData.serviceAction = this.listAction;

			}

		});