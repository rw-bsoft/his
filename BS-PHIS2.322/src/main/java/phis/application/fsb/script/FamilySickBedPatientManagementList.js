$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedPatientManagementList = function(cfg) {
	cfg.sortable = false;// 不能排序
	cfg.showButtonOnTop = false;
	phis.application.fsb.script.FamilySickBedPatientManagementList.superclass.constructor
			.apply(this, [cfg]);
	this.selectFirst = false;
}

Ext.extend(phis.application.fsb.script.FamilySickBedPatientManagementList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				var qysxzz = this.loadSystemParams({"privates":['QYSXZZ']});
				this.sxzzCSZ = qysxzz['QYSXZZ'];
				var grid = phis.application.fsb.script.FamilySickBedPatientManagementList.superclass.initPanel.call(this,sc);
				var panel = new Ext.Panel({
					border : false,
					layout : 'border',
					defaults : {
						border : false
					},
					tbar : [this.createButtons()],
					items : [{
								layout : "fit",
								border : false,
								region : 'center',
								items : [grid]
							}]
				})
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			expansion : function(cfg) {
				delete cfg.buttons;
				var radiogroup = [{
							xtype : "radio",
							boxLabel : '全 部',
							inputValue : 0,
							name : "jczt",
							checked : true,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '家 床',
							name : "jczt",
							inputValue : 2,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '撤床',
							name : "jczt",
							inputValue : 9,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}];
				cfg.tbar.push([radiogroup]);
			},
			afterCheck : function(radio, checked) {
				if (checked) {
					// this.refresh();
					this.jczt = radio.inputValue;
					this.doCndQuery();
				}

			},
			onReady : function() {
				phis.application.fsb.script.FamilySickBedPatientManagementList.superclass.onReady
				.call(this);
				this.grid.on('rowclick', function(grid, rowIndex,columnIndex,event){
					//alert(rowIndex+"+"+columnIndex);
					var store = this.grid.getStore();
					this.setListColor(store);
//					this.grid.getView().getRow(i).style.backgroundColor = "#FF6A6A";
					var r = store.getAt(rowIndex)
					if(r.data.XCTS<0){
						this.grid.getView().getRow(rowIndex).style.backgroundColor = "#FF6A6A";
						this.setCellColor(rowIndex,"#FF6A6A");
					}else if((r.data.XCTS-r.data.LJTS)<0){
						this.grid.getView().getRow(rowIndex).style.backgroundColor = "#4876FF";
						this.setCellColor(rowIndex,"#4876FF");
					}
				},this);
			},
			onStoreLoadData:function(store,records,ops){
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if(records.length == 0){
					return
				}
				this.setListColor(store);
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					if(this.selectedIndex+""=="0"){
						this.selectedIndex = 0;
						var r = store.getAt(0)
						if(r.data.XCTS<0){
							this.grid.getView().getRow(0).style.backgroundColor = "#FF6A6A";
							this.setCellColor(0,"#FF6A6A");
						}else if((r.data.XCTS-r.data.LJTS)<0){
							this.grid.getView().getRow(0).style.backgroundColor = "#4876FF";
							this.setCellColor(0,"#4876FF");
						}
					}
				}
				else{
					this.selectRow(this.selectedIndex);
					var r = store.getAt(this.selectedIndex)
					if(r.data.XCTS<0){
						this.grid.getView().getRow(this.selectedIndex).style.backgroundColor = "#FF6A6A";
						this.setCellColor(this.selectedIndex,"#FF6A6A");
					}else if((r.data.XCTS-r.data.LJTS)<0){
						this.grid.getView().getRow(this.selectedIndex).style.backgroundColor = "#4876FF";
						this.setCellColor(this.selectedIndex,"#4876FF");
					}
				}
			},
			setListColor : function(store){
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i)
					if(r.data.XCTS<0){
						this.grid.getView().getRow(i).style.backgroundColor = "#FFA07A";
						this.setCellColor(i,"#FFA07A");
					}else if((r.data.XCTS-r.data.LJTS)<0){
						this.grid.getView().getRow(i).style.backgroundColor = "#87CEEB";
						this.setCellColor(i,"#87CEEB");
					}
				}
			},
			setCellColor : function(i,color){
//				for (var i = 0; i < store.getCount(); i++) {
					this.grid.getView().getCell(i,0).style.backgroundColor = color;
					this.grid.getView().getCell(i,1).style.backgroundColor = color;
					this.grid.getView().getCell(i,2).style.backgroundColor = color;
					this.grid.getView().getCell(i,3).style.backgroundColor = color;
					this.grid.getView().getCell(i,4).style.backgroundColor = color;
					this.grid.getView().getCell(i,5).style.backgroundColor = color;
					this.grid.getView().getCell(i,6).style.backgroundColor = color;
					this.grid.getView().getCell(i,7).style.backgroundColor = color;
					this.grid.getView().getCell(i,8).style.backgroundColor = color;
					this.grid.getView().getCell(i,9).style.backgroundColor = color;
					this.grid.getView().getCell(i,10).style.backgroundColor = color;
					this.grid.getView().getCell(i,11).style.backgroundColor = color;
					this.grid.getView().getCell(i,12).style.backgroundColor = color;
					this.grid.getView().getCell(i,13).style.backgroundColor = color;
//				}
			},
			cypbRender : function(v, params, reocrd) {
				if (v == '出院证明') {
					params.style = "color:red;";
				}
				return v;
			},
			openModule : function(cmd, r, xy) {
				if (cmd == 'update') {
					if (r.data.CYPB == 1) {
						MyMessageTip.msg("提示", "病人已通知出院，不能修改!", true);
						return;
					}
				}
				var module = this.midiModules["PatientManage"]
				module.cmd = cmd;
				if (module) {
					var win = module.getWin()
					win.setTitle(module.title)
					win.show()
					var xy = win.getPosition();
					win.setPagePosition(xy[0], 0);
					if (!win.hidden) {
						if (cmd == "transform" || cmd == "update"
								|| cmd == "canceled" || cmd == "home") {
							module.on('update', function(){
								this.grid.getStore().load();
							}, this); 
							module.loadData()
							module.initButton();
						}
					}
				}
			},
			loadData : function() {
				if(this.sxzzCSZ == '0'){
					var zyzz = this.grid.getTopToolbar().find('cmd', 'hosReferrals');
					if(zyzz[0]){
						zyzz[0].hide();
					}
				}
				this.requestData.serviceId = "phis.familySickBedPatientManagementService";
				this.requestData.serviceAction = "getPatientList";
				phis.application.fsb.script.FamilySickBedPatientManagementList.superclass.loadData
						.call(this);
			},
			doTransform : function(item, e) {
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "请选择病人!", true);
					return
				}
				if (r.data.CYPB == 1) {
					MyMessageTip.msg("提示", "病人已通知出院，不能转换!", true);
					return;
				}
				this.loadModule("", this.entryName, item, r)
			},
			doCanceled : function(item, e) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.data.CYPB == 1) {
					MyMessageTip.msg("提示", "病人已通知出院，不能注销!", true);
					return;
				}
				this.loadModule("", this.entryName, item, r)
			},
			doHome : function(item, e) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				this.loadModule("", this.entryName, item, r)
			},
			doRefresh : function() {
				this.refresh();
			},
			doCards : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var module = this.createModule("brzkModule", this.cards);
				module.data = r.data;
				// module.on("commit",this.doFillIn,this);
				var win = module.getWin();
				win.add(module.initPanel());
				module.doFillIn(r.data);
				win.show();
			},
            
            doHosReferrals:function(item, e){
            	if(this.sxzzCSZ == '1'){
            		 var r = this.getSelectedRecord()
	                 if (!r) {
	                     return
	                 }
            		 if(!this.zyszMod){
							var module = this.createModule("zyszModule", this.hosReferrals);
							module.exContext = r.data;
							var win = module.getWin();
							win.add(module.initPanel());
							win.show();	
							this.zyszMod = module;
							this.zyszWin = win;
						}else{
							this.zyszMod.exContext = r.data;
							this.zyszMod.resetData();
							this.zyszWin.show();
						}
            	}else{
            		MyMessageTip.msg("提示", "未启用双向转诊配置!", true);
            	}
            },
			loadModule : function(cls, entryName, item, r) {
				cls = "phis.application.fsb.script.FamilySickBedPatientManagementForm";
				if (this.loading) {
					return
				}
				var cmd = item.cmd
				var cfg = {}
				cfg._mId = this.grid._mId // 增加module的id
				cfg.title = this.title + '-' + item.text
				cfg.entryName = entryName
				cfg.op = cmd
				cfg.exContext = {}
				Ext.apply(cfg.exContext, this.exContext)

				if (cmd != 'create') {
					if (this.isCompositeKey) {
						var pkeys = this.schema.pkeys;
						var initDataBody = {};
						for (var i = 0; i < pkeys.length; i++) {
							initDataBody[pkeys[i]] = r.get(pkeys[i])
						}
						cfg.initDataBody = initDataBody;
					} else {
						cfg.initDataId = r.id;
					}
					cfg.exContext[entryName] = r;
				}
				if (this.saveServiceId) {
					cfg.saveServiceId = this.saveServiceId;
				}
				var m = this.midiModules["PatientManage"]
				if (!m) {
					this.loading = true
					$require(cls, [function() {
										this.loading = false
										cfg.autoLoadData = false;
										var module = eval("new " + cls
												+ "(cfg)")
										module.on("save", this.onSave, this)
										module.on("close", this.active, this)
										module.opener = this
										module.setMainApp(this.mainApp)
										this.midiModules["PatientManage"] = module
										this.fireEvent("loadModule", module)
										this.openModule(cmd, r)
									}, this])
				} else {
					Ext.apply(m, cfg)
					this.openModule(cmd, r)
				}
			},
			doCndQuery : function(button, e, addNavCnd) { 
				var exCnd = null;
				// 家床状态
				var jczt = this.jczt;
				if (jczt == 2) {
					exCnd = ['eq', ['$', 'a.CYPB'], ['i', 0]];
				} else if (jczt == 9) {
					exCnd = ['eq', ['$', 'a.CYPB'], ['i', 1]];
				}else{
					exCnd = ['or',['eq', ['$', 'a.CYPB'], ['i', 1]],['eq', ['$', 'a.CYPB'], ['i', 0]]];
				}
				// ** modified by
				// yzh ,
				// 2010-06-09 **
				var initCnd = this.initCnd
				if (exCnd) {
					initCnd = ['and', exCnd, initCnd];
				}
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
							cnd.push(['s', '%' + v + '%'])
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
			},
			doPrint : function() {
				var r = this.getSelectedRecord()
				var module = this.createModule("hospMediRecordPrint",
						'phis.application.fsb.FSB/FSB/FSB0402');
				var ZYH = r.data.ZYH;
				if (ZYH == null) {
					MyMessageTip.msg("提示", "打印失败：无效的病人信息!", true);
					return;
				}
				module.ZYH = ZYH;
				module.initPanel();
				module.doPrintXY();
			},
			onContextMenu:function(grid,rowIndex,e){
				phis.application.fsb.script.FamilySickBedPatientManagementList.superclass.onContextMenu.call(this,grid,rowIndex,e);
				var cmenu = this.midiMenus['gridContextMenu']
				if(cmenu){
					var sxzz = cmenu.find('cmd', 'hosReferrals');
					if(sxzz[0]){
						sxzz[0].hide();
					}
				}
				phis.application.fsb.script.FamilySickBedPatientManagementList.superclass.onContextMenu.call(this,grid,rowIndex,e);
			}
		});