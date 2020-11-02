$package("chis.script")
$import("app.modules.chart.DiggerCardWraper","util.dictionary.DictionaryLoader",
		"org.ext.ux.portal.MyPortal", "app.viewport.WelcomePortal")
chis.script.EHRPortal = function(cfg) {
	this.portlets = {}
	this.portletModules = {}
	this.colCount = 2;
	this.rowCount = 3;
	this.moduleDefine = {
		C : "CHART_Hypertension",
		D : "CHART_Diabetics",
		G : "CHART_Pregant"
	};
	chis.script.EHRPortal.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.EHRPortal, app.viewport.WelcomePortal, {
			// override
			initPanel : function() {
				var myPage = this.myPage
				if (!myPage.modules) {
					var myPageAppId = myPage.id
					var re = util.rmi.miniJsonRequestSync({
								serviceId : "chis.appConfigLocator",
								method:"execute",
								id : myPageAppId
							})
					if (re.code < 300) {
						if(re.json.body)
						myPage.modules = re.json.body.modules
					}
				}
				this.modules = myPage.modules
				if (this.modules) {
					this.colCount = Math.ceil(this.modules.length / 2.0)
				}
				var portlets = this.portlets
				var cfg = {}
				cfg.items = []
				// cfg.title = this.title
				this.initPortlets();
				for (var i = 0; i < this.colCount; i++) {
					for (var j = 0; j < this.rowCount; j++) {
						var index = i + "." + j
						var p = portlets[index]
						var column = {
							// style : 'padding:5px 5px 0px 5px',
							items : []
						}

						column.columnWidth = 1 / this.rowCount;

						cfg.items.push(column)
						if (p) {
							column.items.push(p)
						}
					}
				}
				this.panel = new Ext.ux.Portal(cfg)
				return this.panel;
			},

			// override
			initPortlets : function() {
				var modules = this.modules
				if (!modules) {
					return
				}
				var portlets = this.portlets
				var col = 0
				var row = 0
				for (var i = 0; i < modules.length; i++) {
					var mod = modules[i]
					if (mod.script) {
						$import(mod.script)
						Ext.apply(mod, {
									enableCnd : false,
									autoLoadSchema : false,
									autoLoadLoad : false,
									isCombined : true,
									maximum : true,
									disableBar : true,
									disablePagingTbr : true,
									showButtonOnPT : false,
									showCndsBar : false,
									mainApp : this.mainApp
								})

						if (!mod.entryName) {
							mod.entryName = this.moduleDefine[this.initModules[0]
									.substring(0, 1)]
									|| "EMPTY";
						}

						if (mod.requireKeys) {
							var alias = mod.alias;
							if (alias && alias.indexOf('{') > -1) {
								alias = eval("(" + alias + ")")
								alias = alias[this.initModules[0].substring(0,
										1)]
							}

							var rkey = mod.requireKeys;
							var cndfield = mod.requireKeys;

							if (rkey.indexOf('{') > -1) {
								rkey = eval("(" + rkey + ")")
								rkey = rkey[this.initModules[0].substring(0, 1)]
										|| ''
							}

							if (rkey && rkey.length > 0) {
								cndfield = (alias ? alias + "." : '')
										+ (rkey.indexOf('.') > -1 ? rkey
												.split('.')[1] : rkey)

								Ext.apply(mod, {
											cnds : this.getQueryCnd(['eq',
															['$', cndfield],
															['s', this[rkey]]],
													mod)
										})
								mod[rkey] = this[rkey];
							}
						}

						var m = eval("new " + mod.script + "(mod)")
						var p = this.getPortlet(m)
						if (col >= this.colCount) {
							col = 0
							row++
						}
						portlets[col + "." + row] = p
						this.portletModules[col + "." + row] = m
						col++
					}
				}
				this.clearIds();
			},

			getQueryCnd : function(cnds, mod) {
				if (!mod.dateField) {
					return cnds;
				}
				cnds = [
						'and',
						[
								'eq',
								['tochar', ['$', mod.dateField],
										['s', 'yyyy-mm']],
								['s', Date.parseDate(this.mainApp.serverDate,'Y-m-d').format("Y-m")]], cnds]
				return cnds
			},

			// override
			loadData : function() {
				var ms = this.portletModules
				for (var m in ms) {
					if (ms[m].requireKeys) {
						var alias = ms[m].alias;
						if (alias && alias.indexOf('{') > -1) {
							alias = eval("(" + alias + ")")
							alias = alias[this.initModules[0].substring(0, 1)]
						}

						var rkey = ms[m].requireKeys;
						var cndfield = ms[m].requireKeys;

						if (rkey.indexOf('{') > -1) {
							rkey = eval("(" + rkey + ")")
							rkey = rkey[this.initModules[0].substring(0, 1)]
									|| ''
						}

						if (rkey && rkey.length > 0) {
							cndfield = (alias ? alias + "." : '')
									+ (rkey.indexOf('.') > -1
											? rkey.split('.')[1]
											: rkey)

							if (ms[m].requestData) {
								ms[m].requestData.cnd = this.getQueryCnd(['eq',
												['$', cndfield],
												['s', this[rkey]]], ms[m])
							}
							ms[m][rkey] = this[rkey];
							// delete this[rkey];
						}
					}

					if (ms[m] && ms[m].loadData) {
						ms[m].loadData()
					} else if (ms[m] && ms[m].refresh) {
						ms[m].refresh()
					}
				}
				this.clearIds();
			},

			clearIds : function() {
				// @@ 2010-2-25 modified by chinnsii
				var items = ["healthRecordStatus", "phrId",
						"MDC_DiabetesRecord.phrId",
						"MDC_HypertensionRecord.phrId",
						"MDC_TumourRecord.tumourId",
						"MDC_DiabetesFixGroup.fixId",
						"PIV_VaccinateRecord.phrId", "CDH_HealthCard.phrId",
						"CDH_DefectRegister.phrId",
						"CDH_DebilityChildren.phrId",
						"CDH_DeliveryRecord.deliveryId",
						"MHC_PregnantRecord.pregnantId",
						"MDC_Hypertension.planId", "PSY_PsychosisRecord.phrId",
						"PER_CheckupRegister.checkupNo", "sexCode",
						"EHR_HealthRecord_readOnly",
						"MDC_DiabetesRecord_readOnly",
						"MDC_HypertensionRecord_readOnly",
						"MDC_TumourRecord_readOnly",
						"MDC_DiabetesFixGroup_readOnly",
						"PIV_VaccinateRecord_readOnly",
						"CDH_HealthCard_readOnly",
						"CDH_DefectRegister_readOnly",
						"CDH_DebilityChildren_readOnly",
						"CDH_DeliveryRecord_readOnly",
						"MHC_PregnantRecord_readOnly",
						"PUB_VisitPlan_readOnly",
						"PSY_PsychosisRecord_readOnly",
						"PER_CheckupRegister_readOnly"];
				for (var i = 0; i < items.length; i++) {
					delete this[items[i]];
				}
			},

			// // private
			// getPortlet : function(module) {
			// var p = module.initPanel()
			// p.frame = false
			// return new Ext.ux.Portlet({
			// title : module.title,
			// height : 240,
			// width : '110%',
			// layout : "fit",
			// //plugins : new Ext.ux.MaximizeTool(),
			// items : p,
			// _m : module
			// })
			// },

			getPortlet : function(module) {
				var p = module.initPanel()
				p.frame = false
				return new Ext.ux.MyPortlet({
							// title : module.title,
							tbar : [module.title],
							height : module.height || 220,
							width : '100%',
							layout : "fit",
							items : p
						})
			},

			// override
			getWin : function() {
				var win = this.win
				if (!win) {
					var cfg = {
						id : this.id,
						title : this.title,
						width : this.width,
						height : this.height,
						iconCls : 'bogus',
						shim : true,
						layout : "fit",
						items : this.initPanel(),
						animCollapse : true,
						constrainHeader : true,
						minimizable : true,
						constrain:true,
						maximizable : true,
						shadow : false
					}
					if (!this.mainApp) {
						cfg.closeAction = 'hide'
					}
					win = new Ext.Window(cfg)
					win.on("resize", this.onWinResize, this)
					win.on("show", function() {
								jsReady = true;
							});
					// win.on("show",this.initMap,this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win
			},
			onWinResize : function() {
			}

		})
