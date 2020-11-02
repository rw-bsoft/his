$package("phis.application.cic.script");

$import("phis.script.SimpleList", "org.ext.ux.CheckColumn");
/**
 * 就诊记录列表
 */
phis.application.cic.script.PrescriptionCopyCF02List = function(cfg) {
	// cfg.listServiceId = "medicalTechnicalSectionService";
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.cic.script.PrescriptionCopyCF02List.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.afterLoadData, this);
}, Ext.extend(phis.application.cic.script.PrescriptionCopyCF02List,
		phis.script.SimpleList, {
			initPanel : function(brid) {
				this.brid = brid;
				return phis.application.cic.script.PrescriptionCopyCF02List.superclass.initPanel
						.call(this);
			},
			onReady : function() {
				this.grid.on("beforeCheckedit", function() {
							return false
						}, this)
				phis.application.cic.script.PrescriptionCopyCF02List.superclass.onReady
						.call(this)

			},
			afterLoadData : function() {
				this.reloadYPZH();
			},
			reloadYPZH : function() {
				var count = this.store.getCount();
				var ypzh = 0;
				var lastYPZH = -1;
				for (var i = 0; i < count; i++) {
					var now_ypzh = this.store.getAt(i).get("YPZH");
					if (now_ypzh != lastYPZH) {
						ypzh++;
						this.store.getAt(i).set("YPZH_SHOW", ypzh);
						lastYPZH = now_ypzh;
					} else {
						this.store.getAt(i).set("YPZH_SHOW", ypzh);
					}
				}

			},
			showColor : function(v, params, data) {
				var YPZH = data.get("YPZH_SHOW") % 2 + 1;
				var PSPB = data.get("PSPB");
				var JYLX = data.get("JYLX");
				switch (YPZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				var msg = data.get("msg");
				if (msg) {
					if (msg.error_kc) {
						return "<img src='"
								+ ClassLoader.appRootOffsetPath
								+ "resources/phis/resources/images/i_error.gif' title='错误："
								+ msg.error_kc + "!'/>"
					}
				}
				if (JYLX == 1)
					return "<h2 style='color:red;font-size:14px;font-weight:bold'>非</h2>";
				return PSPB > 0 ? "<h2 style='color:red'>皮</h2>" : "";
			},
			// 加载数据时增加机构
			loadData : function() {
				this.requestData.cnd = [
						'and',
						['eq', ['$', 'a.JGID'],
								['s', this.mainApp['phisApp'].deptId]]];
				// ['eq', ['$', 'KSDM'], ['d', this.mainApp['phis'].MedicalId]]]

				phis.application.cic.script.PrescriptionCopyCF02List.superclass.loadData
						.call(this);
			},
			/**
			 * 重新加载处方02表
			 * 
			 * @param cfsb
			 */
			reloadData : function(cfsb) {
				if (!cfsb) {// 当cfsb为空时，说明cf01中没有处方，此时需要清空缓存中的处方明细
					this.clear();
					return;
				}
				this.requestData.cnd = [
						'and',
						['eq', ['$', 'a.CFSB'], ['d', cfsb]],
						['eq', ['$', 'a.JGID'],
								['s', this.mainApp['phisApp'].deptId]]];
				this.store.load();
			},
			getCM : function(items) {
				var cm = []
				var ac = util.Accredit;
				var expands = []
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer({
								header : '序号'
							}))
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {
						continue
					}
					if (it.expand) {
						var expand = {
							id : it.dic ? it.id + "_text" : it.id,
							alias : it.alias,
							xtype : it.xtype
						}
						expands.push(expand)
						continue
					}
					if (!this.fireEvent("onGetCM", it)) { // **
						// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
						continue;
					}
					var width = parseInt(it.width || 80)
					// if(width < 80){width = 80}
					var c = {
						header : it.alias,
						width : width,
						sortable : true,
						dataIndex : it.dic ? it.id + "_text" : it.id
					}
					if (!this.isCompositeKey && it.pkey) {
						c.id = it.id
					}
					switch (it.type) {
						case 'int' :
						case 'double' :
						case 'bigDecimal' :
							if (!it.dic) {
								c.css = "color:#00AA00;font-weight:bold;"
								c.align = "right"
								if (it.precision > 0) {
									var nf = '0.';
									for (var j = 0; j < it.precision; j++) {
										nf += '0';
									}
									c.renderer = Ext.util.Format
											.numberRenderer(nf);
								}
							}
							break
						case 'date' :
							c.renderer = function(v) {
								if (v && typeof v == 'string' && v.length > 10) {
									return v.substring(0, 10);
								}
								return v;
							}
							break
						case 'timestamp' :
						case 'datetime' :
							if (it.xtype == 'datefield') {
								c.renderer = function(v) {
									if (v && typeof v == 'string'
											&& v.length > 10) {
										return v.substring(0, 10);
									} else {
										return v;
									}
								}
							}
							break
					}
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.properties.xtype == "checkBox") {
						c.xtype = 'checkcolumn';
						c.editor = new Ext.ux.grid.CheckColumn();
						// editor.on("beforeedit",this.beforeCheckEdit,this)
					}
					if (this.fireEvent("addfield", c, it)) {
						cm.push(c)
					}
				}
				if (expands.length > 0) {
					this.rowExpander = this.getExpander(expands)
					cm = [this.rowExpander].concat(cm)
				}
				return cm
			},
			getAllData : function() {
				var selectList = [];
				var allItems = this.store.data.items;
				var tmp;
				for (var i = 0; i < allItems.length; i++) {
					tmp = allItems[i];
					selectList.push(tmp);
				}
				return selectList;
			}
		});