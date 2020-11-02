$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedPatientSelectionLeftList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.showRowNumber = false;
	phis.application.fsb.script.FamilySickBedPatientSelectionLeftList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedPatientSelectionLeftList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				phis.application.fsb.script.FamilySickBedPatientSelectionLeftList.superclass.initPanel
						.call(this);
				// 控制发票号码在中途结算和出院结算时 在List列表不显示
				// var arrColumn = this.grid.getColumnModel().config;
				var arrColumn = this.cm.config;
				arrColumn[0].hidden = true;
				// this.grid.getView().refresh();
				return this.grid
			},
			cypbRender : function(v, params, reocrd) {
				if (v == '出院证明') {
					params.style = "color:red;";
				}
				return v;
			},
			onReady : function() {
				phis.application.fsb.script.FamilySickBedPatientSelectionLeftList.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			loadData : function() {
				this.requestData.serviceId = "phis.familySickBedPatientSelectionService";
				this.requestData.serviceAction = "getPatientList";
				this.requestData.schema = this.schema;
				phis.application.fsb.script.FamilySickBedPatientSelectionLeftList.superclass.loadData
						.call(this);
			},
			onRowClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("click", record.data);
				}
			},
			onDblClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("dblClick", record.data);
				}
			},
			onStoreLoadData : function(store, records, ops) {
				if(this.fphm){
					var _this = this;
					var deferFunction = function(){
						_this.doPrint(_this.fphm);
					}
					deferFunction.defer(1000);
				}
				if (records.length == 0) {
					document.getElementById("ZTJS_BRLB_1").innerHTML = "共 0 条";
					this.fireEvent("noRecord", this);
					return
				}

				var store = this.grid.getStore();
				var n = store.getCount()
				if (document.getElementById("ZTJS_BRLB_1")) {
					document.getElementById("ZTJS_BRLB_1").innerHTML = "共 " + n
							+ " 条";
				}
			},
			doPrint : function(fphm) {
				if(!fphm){
					return ;
				}
				var LODOP=getLodop();  
	    		LODOP.PRINT_INITA(3,8,643,680,"家床发票套打");
	    		
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "familySickBedPatientSelectionService",
					serviceAction : "printMoth",
						fphm : fphm
					});
				this.fphm = false;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(29,70,160,25,ret.json.FPHM);
				LODOP.ADD_PRINT_TEXT(55,70,100,25,ret.json.ZYHM);
				LODOP.ADD_PRINT_TEXT(55,410,40,25,ret.json.N);
				LODOP.ADD_PRINT_TEXT(55,470,40,25,ret.json.Y);
				LODOP.ADD_PRINT_TEXT(55,530,40,25,ret.json.R);
				LODOP.ADD_PRINT_TEXT(80,70,160,25,ret.json.RYLB);
				LODOP.ADD_PRINT_TEXT(80,410,160,25,ret.json.GZDW);
				LODOP.ADD_PRINT_TEXT(105,70,160,25,ret.json.BRXM);
				LODOP.ADD_PRINT_TEXT(105,410,160,25,ret.json.ZYRQ);
				LODOP.ADD_PRINT_TEXT(140,70,100,25,ret.json.SFXM1);
				LODOP.ADD_PRINT_TEXT(165,70,100,25,ret.json.SFXM2);
				LODOP.ADD_PRINT_TEXT(190,70,100,25,ret.json.SFXM3);
				LODOP.ADD_PRINT_TEXT(215,70,100,25,ret.json.SFXM4);
				LODOP.ADD_PRINT_TEXT(240,70,100,25,ret.json.SFXM5);
				LODOP.ADD_PRINT_TEXT(265,70,100,25,ret.json.SFXM6);
				LODOP.ADD_PRINT_TEXT(290,70,100,25,ret.json.SFXM7);
				LODOP.ADD_PRINT_TEXT(315,70,100,25,ret.json.SFXM8);
				LODOP.ADD_PRINT_TEXT(340,70,100,25,ret.json.SFXM9);
				LODOP.ADD_PRINT_TEXT(365,70,100,25,ret.json.SFXM10);
				LODOP.ADD_PRINT_TEXT(390,70,100,25,ret.json.SFXM11);
				LODOP.ADD_PRINT_TEXT(415,70,100,25,ret.json.SFXM12);
				LODOP.ADD_PRINT_TEXT(440,70,100,25,ret.json.SFXM13);
				LODOP.ADD_PRINT_TEXT(465,70,100,25,ret.json.SFXM14);
				LODOP.ADD_PRINT_TEXT(490,70,100,25,ret.json.SFXM15);
				LODOP.ADD_PRINT_TEXT(515,70,100,25,ret.json.SFXM16);
				LODOP.ADD_PRINT_TEXT(140,175,100,25,ret.json.XMJE1);
				LODOP.ADD_PRINT_TEXT(165,175,100,25,ret.json.XMJE2);
				LODOP.ADD_PRINT_TEXT(190,175,100,25,ret.json.XMJE3);
				LODOP.ADD_PRINT_TEXT(215,175,100,25,ret.json.XMJE4);
				LODOP.ADD_PRINT_TEXT(240,175,100,25,ret.json.XMJE5);
				LODOP.ADD_PRINT_TEXT(265,175,100,25,ret.json.XMJE6);
				LODOP.ADD_PRINT_TEXT(290,175,100,25,ret.json.XMJE7);
				LODOP.ADD_PRINT_TEXT(315,175,100,25,ret.json.XMJE8);
				LODOP.ADD_PRINT_TEXT(340,175,100,25,ret.json.XMJE9);
				LODOP.ADD_PRINT_TEXT(365,175,100,25,ret.json.XMJE10);
				LODOP.ADD_PRINT_TEXT(390,175,100,25,ret.json.XMJE11);
				LODOP.ADD_PRINT_TEXT(415,175,100,25,ret.json.XMJE12);
				LODOP.ADD_PRINT_TEXT(440,175,100,25,ret.json.XMJE13);
				LODOP.ADD_PRINT_TEXT(465,175,100,25,ret.json.XMJE14);
				LODOP.ADD_PRINT_TEXT(490,175,100,25,ret.json.XMJE15);
				LODOP.ADD_PRINT_TEXT(515,175,100,25,ret.json.XMJE16);
				LODOP.ADD_PRINT_TEXT(515,280,100,25,ret.json.FYHJ);
				LODOP.ADD_PRINT_TEXT(540,114,317,25,ret.json.ZFJE);
				LODOP.ADD_PRINT_TEXT(565,70,500,65,ret.json.BZ);
				LODOP.ADD_PRINT_TEXT(635,450,120,25,ret.json.SYY);
				LODOP.ADD_PRINT_TEXT(140,470,100,25,ret.json.ZFJE);
				LODOP.ADD_PRINT_TEXT(165,470,100,25,ret.json.BNZHZF);
				LODOP.ADD_PRINT_TEXT(190,470,100,25,ret.json.LNZHZF);
				LODOP.ADD_PRINT_TEXT(215,470,100,25,ret.json.YBZH);
				LODOP.ADD_PRINT_TEXT(240,470,100,25,ret.json.YBHJ);
				LODOP.ADD_PRINT_TEXT(265,470,100,25,ret.json.JKHJ);
				LODOP.ADD_PRINT_TEXT(290,470,100,25,ret.json.FYHJ);
				LODOP.ADD_PRINT_TEXT(315,470,100,25,ret.json.CYBJ);
				LODOP.ADD_PRINT_TEXT(340,470,100,25,ret.json.BJXJ);
				LODOP.ADD_PRINT_TEXT(365,470,100,25,ret.json.CYTK);
				LODOP.ADD_PRINT_TEXT(390,470,100,25,ret.json.TKXJ);
				if (LODOP.SET_PRINTER_INDEXA(ret.json.JCJSDYJMC)){
					if((ret.json.FPYL+"")=='1'){
						LODOP.PREVIEW();
					}else{
						LODOP.PRINT();
					}
				}else{
					LODOP.PREVIEW();
				}
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='ZTJS_BRLB_1' align='center' style='color:blue'>共 0 条</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			onKeypress : function(e) {
				// if (e.getKey() == 40 || e.getKey() == 38) {
				// this.onRowClick(this.grid);
				// }
				if (e.getKey() == e.ENTER) {
					this.onDblClick(this.grid);
				}
			},
			doQuery : function(data) {
				var store = this.grid.getStore();
				var n = store.getCount()
				var value = data.value.toUpperCase();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (data.key == "ZYHM") {
						var leftStr = ['', '0', '00', '000', '0000', '00000',
								'000000', '0000000', '00000000', '000000000',
								'0000000000'];
						if ((r.get(data.key).length - value.length) < 0) {
							MyMessageTip.msg("提示", "该家床号码不在结算病人列表", true);
							return;
						}
						if (r.get(data.key).toUpperCase() == leftStr[r
								.get(data.key).length
								- value.length]
								+ value) {
							this.grid.getSelectionModel().selectRow(i);
							var record = this.grid.store.getAt(i);
							if (record) {
								var rdata = record.data;
								this.fireEvent("dblClick", rdata);
							}
							return;
						}
					} else if (r.get(data.key).toUpperCase() == value) {
						this.grid.getSelectionModel().selectRow(i);
						var record = this.grid.store.getAt(i);
						if (record) {
							var rdata = record.data;
							this.fireEvent("dblClick", rdata);
						}
						return;
					}
				}
				if (data.key == "ZYHM") {
					MyMessageTip.msg("提示", "该家床号码不在结算病人列表", true);
					return;
				} else {
					MyMessageTip.msg("提示", "该床号不在结算病人列表", true);
					return;
				}
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				var selected = null;
				var defaultItem = null;
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable) {
						continue
					}
					selected = it.id;
					defaultItem = it;
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return [];
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = new Ext.form.Label({
							text : fields[0].text
						});
				this.cndFldCombox = new Ext.form.Hidden({
							value : fields[0].value
						});

				var cndField;
				if (defaultItem) {
					if (defaultItem.dic) {
						defaultItem.dic.src = this.entryName + "." + it.id
						defaultItem.dic.defaultValue = defaultItem.defaultValue
						defaultItem.dic.width = 80
						cndField = this.createDicField(defaultItem.dic)
						cndField.on("select", this.doRefresh, this)
						cndField.setEditable(false);
					} else {
						cndField = this.createNormalField(defaultItem)
					}
				} else {
					cndField = new Ext.form.TextField({
								width : 120,
								selectOnFocus : true,
								name : "dftcndfld",
								enableKeyEvents : true
							})
				}
				this.cndField = cndField
				var queryBtn = new Ext.Toolbar.Button({
							text : '',
							iconCls : "query",
							notReadOnly : true
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doRefresh, this);
				return [combox, '-', cndField, '-', queryBtn]
			},
			doRefresh : function() {
				var cndField = this.cndField;

				var module = this.opener.module.form;
				if (module.form.getForm().findField("ZYHM")) {
					module.form.getForm().findField("ZYHM").fireEvent(
							"changeLabel", cndField.getValue());// 点击结算类型
					// 触发Form住院号码变成发票号码
				}
				if (module.form.getForm().findField("FPHM")) {
					module.form.getForm().findField("FPHM").fireEvent(
							"changeLabel", cndField.getValue());// 点击结算类型
					// 触发Form住院号码变成发票号码
				}

				if (cndField.getValue() == 10) {

					var arrColumn = this.cm.config;
					arrColumn[0].hidden = false;
					this.grid.getView().refresh();

					this.opener.JSLX = 10;
					this.schema = 'JC_BRRY_CYLB';
					// this.requestData.cnd = ['and',['and', ['eq', ['$',
					// 'b.ZFPB'], ['i', 0]],['ne', ['$', 'b.JSLX'], ['i', 4]]],
					// ['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]]];
					this.requestData.cnd = [
							'and',
							[
									'and',
									['eq', ['$', 'b.ZFPB'], ['i', 0]],
									['eq', ['$', 'a.JGID'],
											['s', this.mainApp['phisApp'].deptId]]],
							['or', ['eq', ['$', 'b.JSLX'], ['i', 5]],
									['$', 'b.JSLX = 1 and a.CYPB <> 8']]];
					this.refresh();
				} else if (cndField.getValue() == 5) {

					var arrColumn = this.cm.config;
					arrColumn[0].hidden = true;
					this.grid.getView().refresh();

					this.opener.JSLX = 5;
					this.schema = 'JC_BRRY_CYLB_ZC';
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'a.CYPB'], ['i', 1]]];
					this.refresh();
				} else if (cndField.getValue() == 1) {

					var arrColumn = this.cm.config;
					arrColumn[0].hidden = true;
					this.grid.getView().refresh();

					this.opener.JSLX = 1;
					this.schema = 'JC_BRRY_ZT';
					this.requestData.cnd = [
							'and',
							['notin', ['$', 'a.BRXZ'],
									[['s','select BRXZ from GY_BRXZ where DBPB > 0']]],
							['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'a.CYPB'], ['i', 0]]];
					this.refresh();
				} else if (cndField.getValue() == 4) {

					var arrColumn = this.cm.config;
					arrColumn[0].hidden = true;
					this.grid.getView().refresh();

					this.opener.JSLX = 4;
					this.schema = 'JC_BRRY_CYLB_ZC';
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'a.CYPB'], ['i', 1]]];
					this.refresh();
				}
				this.opener.doNew();
			}
		});