$package("phis.application.war.script");

$import("phis.script.SimpleList");
phis.application.war.script.MedicalRecordsNotReviewList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = false;
	cfg.listServiceId = "medicalRecordsQueryService";
	cfg.serverParams = {
		serviceAction : "listMedicalRecords",
		type : "1"
	};
	phis.application.war.script.MedicalRecordsNotReviewList.superclass.constructor
			.apply(this, [cfg]);

},

Ext.extend(phis.application.war.script.MedicalRecordsNotReviewList,
		phis.script.SimpleList, {
			onDblClick : function(grid, index, e) {
				this.doSeeRecord();
			},
			doMedicalDetails : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				var BLBH = record.get("BLBH");
				this.showMedicalDetailsModule(BLBH);
			},
			showMedicalDetailsModule : function(BLBH) {
				var moduleCfg = null;
				var res = this.mainApp.taskManager
						.loadModuleCfg(this.refMedicalDetails);
				if (!res.code) {
					moduleCfg = res;
				} else if (res.code != 200) {
					Ext.MessageBox.alert("错误", res.msg)
					return
				}
				if (!moduleCfg) {
					moduleCfg = res.json.body;
				}
				var cfg = {
					showButtonOnTop : true,
					border : false,
					frame : false,
					autoLoadSchema : false,
					isCombined : true,
					exContext : {}
				};
				Ext.apply(moduleCfg, moduleCfg.properties);
				Ext.apply(cfg, moduleCfg);
				var cls = moduleCfg.script;
				if (!cls) {
					return;
				}
				$import(cls);
				var tplModule = eval("new " + cls + "(cfg)");
				tplModule.setMainApp(this.mainApp);
				tplModule.opener = this;
				tplModule.BLBH = BLBH;
				var de_win = tplModule.getWin();
				this.de_win = de_win;
				this.de_win.show();
			},
			doSeeRecord : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				var BLBH = record.get("BLBH");
				if (record.get("BLLB") == '2000001') {
					var moduleCfg = this.mainApp.taskManager.loadModuleCfg(this.basyView);
					var cfg = {
						showButtonOnTop : true,
						border : false,
						frame : false,
						autoLoadSchema : false,
						isCombined : true,
						exContext : {}
					};
					Ext.apply(cfg, moduleCfg.json.body);
					var cls = cfg.script;
					if (!cls) {
						return;
					}
					$import(cls);
					var module = eval("new " + cls + "(cfg)");
					module.setMainApp(this.mainApp);
					module.opener = this;
					var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : "emrMedicalRecordsService",
								serviceAction : "queryBASY",
								ZYH : record.get("JZXH"),
								BLLB : record.get("BLLB"),
								cnd : ['eq', ['$', 'JZXH'],
										['i', record.get("JZXH")]]
							});
					var code = res.code;
					var msg = res.msg;
					if (code >= 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					module.exContext.SFSY = res.json.SFSY;// 是否审阅
					module.exContext.SYQX = res.json.SYQX;// 审阅权限
					module.exContext.SXQX = res.json.SXQX;// 书写权限
					module.exContext.DYQX = res.json.DYQX;// 打印权限
					module.exContext.BRXX = res.json.JBXX;
					module.exContext.ZYSSJL = res.json.ZYSSJL;
					module.exContext.ZYZDJL = res.json.ZYZDJL;
					var win = module.getWin();
					win.show();
				} else {
					var type = "HTML";
					window.open(ClassLoader.appRootOffsetPath
							+ 'FileContent.outputStream?BLBH=' + BLBH
							+ '&type=' + type);
				}
			},
			createNormalField : function(it) {
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					width : this.queryWidth || 150,// add by liyl 2012-06-17
					// 字典查询框宽度自定义
					value : it.defaultValue
				}
				var field;
				switch (it.type) {
					case 'int' :
					case 'long' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						field = new Ext.form.NumberField(cfg)
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						field = new Ext.form.DateField(cfg)
						break;
					case 'string' :
						field = new Ext.form.TextField(cfg)
						break;
				}
				return field;
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				// yzh ,
				// 2010-06-09 **
//				if (this.store.getCount() == 0) {
//					return;
//				}
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
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
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
						case 'long' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							// add by liyl 07.25 解决拼音码查询大小写问题
							if (it.id == "PYDM" || it.id == "WBDM") {
								v = v.toUpperCase();
							}
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
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
			}

		});