$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalSettlementManagementQueryList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.sortable = false;// 不能排序
	phis.application.hos.script.HospitalSettlementManagementQueryList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalSettlementManagementQueryList,
		phis.script.SimpleList, {
	// 清单
	doInventory : function(data) {
		var module = this.createModule("mxtabModule",this.reftabModule);
		module.data = data;
		var win = module.getWin();
		win.add(module.initPanel());
		win.show();
	},
	onReady : function() {
		this.grid.on('cellDblclick', function(grid, rowIndex,columnIndex,event){
			var store = this.grid.getStore();
			var r = store.getAt(rowIndex);
			var data = {};
			if(columnIndex<=3){
				data.ZYH = r.data.ZYH;
				data.ZYGB = r.data.ZYGB;
				data.ZYGB_text = r.data.ZYGB_text;
				data.ZJJE = r.data.ZJJE;
				data.ZFJE = r.data.ZFJE;
				data.JSCS = r.data.JSCS;
			}else if(columnIndex>3){
				data.ZYH = r.data.ZYH2;
				data.ZYGB = r.data.ZYGB2;
				data.ZYGB_text = r.data.ZYGB2_text;
				data.ZJJE = r.data.ZJJE2;
				data.ZFJE = r.data.ZFJE2;
				data.JSCS = r.data.JSCS2;
			}
			if(data.ZYH==0){
				MyMessageTip.msg("提示", "当前项目没有收费明细!", true);
				return;
			}
			this.doInventory(data);
		},this);
		this.grid.on('cellclick', function(grid, rowIndex,columnIndex,event){
			//alert(rowIndex+"+"+columnIndex);
			var store = this.grid.getStore();
			var n = store.getCount();
			for (var i = 0; i < n; i++) {
				this.grid.getView().getRow(i).style.backgroundColor = "#FFFFFF";
				this.grid.getView().getCell(i,0).style.backgroundColor = "#FFFFFF";
				this.grid.getView().getCell(i,1).style.backgroundColor = "#FFFFFF";
				this.grid.getView().getCell(i,2).style.backgroundColor = "#FFFFFF";
				this.grid.getView().getCell(i,3).style.backgroundColor = "#FFFFFF";
				this.grid.getView().getCell(i,4).style.backgroundColor = "#FFFFFF";
				this.grid.getView().getCell(i,5).style.backgroundColor = "#FFFFFF";
				this.grid.getView().getCell(i,6).style.backgroundColor = "#FFFFFF";
			}
			if(columnIndex<=3){
				this.grid.getView().getCell(rowIndex,1).style.backgroundColor = "#C9C9C9";
				this.grid.getView().getCell(rowIndex,2).style.backgroundColor = "#C9C9C9";
				this.grid.getView().getCell(rowIndex,3).style.backgroundColor = "#C9C9C9";
			}else if(columnIndex>3){
				this.grid.getView().getCell(rowIndex,4).style.backgroundColor = "#C9C9C9";
				this.grid.getView().getCell(rowIndex,5).style.backgroundColor = "#C9C9C9";
				this.grid.getView().getCell(rowIndex,6).style.backgroundColor = "#C9C9C9";
			}
			
		},this);
		if (this.showBtnOnLevel) {
			var otherTbar = new Ext.Toolbar(this.createButtons("two"));
			this.otherTbar = otherTbar;
			this.grid.getView().showBtnOnLevel = true;
			this.grid.add(otherTbar)
			this.grid.doLayout();
			this.grid.getView().refresh();
		}
		if (this.autoLoadData == '0' || this.autoLoadData == 'false') {
			this.autoLoadData = false;
		}
		// phis.script.SimpleList.superclass.onReady.call(this);
		if (this.autoLoadData) {
			this.loadData();
		}
		var el = this.grid.el
		if (!el) {
			return
		}
		// Ext.get(el).on("keyup", function(e) {
		// this.onShortcutKey(this, e);
		// }, this);
	},
			loadData : function() {
				this.requestData.serviceId = "phis.hospitalPatientSelectionService";
				this.requestData.serviceAction = "querySelectionList";
				this.requestData.body = this.ldata;
				phis.application.hos.script.HospitalSettlementManagementQueryList.superclass.loadData
						.call(this);
			},
			onStoreLoadData : function(store, records, ops) {
				if (records.length == 0) {
					document.getElementById("ZTJS_FYZK_HJ1" + this.openBy).innerHTML = "";
					this.fireEvent("noRecord", this);
					return
				}
				// var store = this.grid.getStore();
				var n = store.getCount();
				var ZJJE = 0;
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					ZJJE += r.get("ZJJE");
					ZJJE += r.get("ZJJE2");
				}
				var numStr = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
				var rmbStr = ['分', '角', '', '元', '拾', '佰', '千', '万', '拾', '佰',
						'千']
				ZJJE = parseFloat(ZJJE).toFixed(2);
				var ZJJEStr = ZJJE + "";
				var l = ZJJEStr.length;
				var RMB = "";
				for (var i = 0; i < l; i++) {
					if('-'==ZJJEStr.charAt(i)){
							RMB += '负';
					}else{
						if (l - i != 3) {
							RMB += numStr[ZJJEStr.charAt(i)];
							RMB += rmbStr[l - i - 1];
						}
					}
				}
				if(ZJJE>0){
					this.fireEvent("setSettle", false);
				}else{
					this.fireEvent("setSettle", true);
				}
				document.getElementById("ZTJS_FYZK_HJ1" + this.openBy).innerHTML = " 合 计 金 额 ："
						+ ZJJE + "<br/>人民币大写：" + RMB;
			},
			expansion : function(cfg) {
				cfg.enableHdMenu = false;// 不显示menu
				cfg.enableColumnHide = false;// 不显示menu
				cfg.enableDragDrop = false;// 不能拖动
				cfg.enableColumnMove = false;// 不能拖动
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='ZTJS_FYZK_HJ1"
							+ this.openBy
							+ "' align='left' style='color:blue'> 合 计 金 额 ：<br/>人民币大写：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			labelClear : function() {
				document.getElementById("ZTJS_FYZK_HJ1" + this.openBy).innerHTML = " 合 计 金 额 ：<br/>人民币大写：";
			},
			getCM : function(items) {
				var cm = []
				var ac = util.Accredit;
				var expands = []
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if (this.mutiSelect) {
					cm.push(this.sm);
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2)
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
						sortable : this.sortable,// add by yangl 增加是否启用排序参数
						dataIndex : it.dic ? it.id + "_text" : it.id
					}
					if (!this.isCompositeKey && it.pkey == "true") {
						c.id = it.id
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
					}
					switch (it.type) {
						case 'int' :
						case 'double' :
						case 'bigDecimal' :
							if (!it.dic) {
								c.css = "color:#00AA00;font-weight:bold;"
								c.align = "right"
								if (it.type == 'double'
										|| it.type == 'bigDecimal') {
									c.precision = it.precision;
									c.nullToValue = it.nullToValue;
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if ((col == 5 || col == 6)&& r.data.ZYGB2 == '') {
											return "";
										}
										if (value == null && this.nullToValue) {
											value = parseFloat(this.nullToValue)
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											try {
												r.set(this.id, retValue);
											} catch (e) {
												// 防止新增行报错
											}
											return retValue;
										}
										if (value != null) {
											value = parseFloat(value);
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											return retValue;
										}
									}
								}
							}
							break
						case 'timestamp' :
							// c.renderer = Ext.util.Format.dateRenderer('Y-m-d
							// HH:m:s')
					}
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
						if (it.summaryRenderer) {
							var func = eval("this." + it.summaryRenderer)
							if (typeof func == 'function') {
								c.summaryRenderer = func
							}
						}
					}
					if (this.fireEvent("addfield", c, it)) {
						cm.push(c)
					}
				}
				if (expands.length > 0) {
					this.rowExpander = this.getExpander(expands)
					cm = [this.rowExpander].concat(cm)
					this.array.push(this.rowExpander)// add by taoy
				}
				return cm
			}
		});