$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalPatientSelectionLeftList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.showRowNumber = false;
	phis.application.hos.script.HospitalPatientSelectionLeftList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalPatientSelectionLeftList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				phis.application.hos.script.HospitalPatientSelectionLeftList.superclass.initPanel
						.call(this);
				// 控制发票号码在中途结算和出院结算时 在List列表不显示
				// var arrColumn = this.grid.getColumnModel().config;
				var arrColumn = this.cm.config;
				arrColumn[1].hidden = true;
				// this.grid.getView().refresh();
				return this.grid
			},
			cypbRender : function(v, params, reocrd) {
				if (v == '出院证明') {
					params.style = "color:red;";
				}
				return v;
			},
			jsxzRender : function(v, params, reocrd) {
				params.style = "color:red;";
				return v;
			},
			onReady : function() {
				phis.application.hos.script.HospitalPatientSelectionLeftList.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			loadData : function() {
				this.requestData.serviceId = "phis.hospitalPatientSelectionService";
				this.requestData.serviceAction = "getPatientList_JSGL";
				this.requestData.JSLX = this.cndField.value.key==undefined?this.cndField.getValue():this.cndField.value.key;
				this.requestData.schema = this.schema;
				phis.application.hos.script.HospitalPatientSelectionLeftList.superclass.loadData
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
			//住院发票打印
			doPrint : function(fphm) {
				if(!fphm){
					return ;
				}
				var LODOP=getLodop();
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "hospitalPatientSelectionService",
					serviceAction : "printMoth",
						fphm : fphm
					});
				this.fphm = false;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor", "#0000FF");
				LODOP.SET_PRINT_PAGESIZE(0,'21cm','10.1cm',"")
				LODOP.ADD_PRINT_TEXT("10mm", "30mm", "20mm", "5mm", ret.json.ZYH);
				LODOP.ADD_PRINT_TEXT("10mm", "78mm", "20mm", "5mm", "非盈利");
				LODOP.ADD_PRINT_TEXT("10mm", "112mm", "20mm", "5mm", ret.json.BAHM);
				LODOP.ADD_PRINT_TEXT("10mm", "150mm", "20mm", "5mm", ret.json.ZYHM);
				LODOP.ADD_PRINT_TEXT("15mm", "30mm", "10mm", "5mm", ret.json.RYYY);
				LODOP.ADD_PRINT_TEXT("15mm", "47mm", "8mm", "5mm", ret.json.RYMM);
				LODOP.ADD_PRINT_TEXT("15mm", "55mm", "8mm", "5mm", ret.json.RYDD);
				LODOP.ADD_PRINT_TEXT("15mm", "70mm", "10mm", "5mm", ret.json.CYYY);
				LODOP.ADD_PRINT_TEXT("15mm", "80mm", "8mm", "5mm", ret.json.CYMM);
				LODOP.ADD_PRINT_TEXT("15mm", "90mm", "8mm", "5mm", ret.json.CYDD);
				LODOP.ADD_PRINT_TEXT("15mm", "130mm", "8mm", "5mm", ret.json.days);
				
				LODOP.ADD_PRINT_TEXT("20mm", "21mm", "20mm", "5mm", ret.json.XM);
				LODOP.ADD_PRINT_TEXT("20mm", "55mm", "15mm", "5mm", ret.json.XB);//性别
				LODOP.ADD_PRINT_TEXT("20mm", "74mm", "30mm", "5mm", ret.json.JSFS);//结算方式
				LODOP.ADD_PRINT_TEXT("20mm", "136mm", "40mm", "5mm", ret.json.YLZH);//社会保障号码
				//明细打印
				LODOP.ADD_PRINT_TEXT("32mm", "13mm","26mm", "5mm", ret.json.XMMC1);
				LODOP.ADD_PRINT_TEXT("32mm", "40mm", "20mm", "5mm", ret.json.XMJE1);
				LODOP.ADD_PRINT_TEXT("37mm", "13mm","26mm", "5mm", ret.json.XMMC4);
				LODOP.ADD_PRINT_TEXT("37mm", "40mm", "20mm", "5mm", ret.json.XMJE4);
				LODOP.ADD_PRINT_TEXT("42mm", "13mm","26mm", "5mm", ret.json.XMMC7);
				LODOP.ADD_PRINT_TEXT("42mm", "40mm", "20mm", "5mm", ret.json.XMJE7);				
				LODOP.ADD_PRINT_TEXT("47mm", "13mm","26mm", "5mm", ret.json.XMMC10);
				LODOP.ADD_PRINT_TEXT("47mm", "40mm", "20mm", "5mm", ret.json.XMJE10);
				LODOP.ADD_PRINT_TEXT("52mm", "13mm","26mm", "5mm", ret.json.XMMC13);
				LODOP.ADD_PRINT_TEXT("52mm", "40mm", "20mm", "5mm", ret.json.XMJE13);				
				LODOP.ADD_PRINT_TEXT("57mm", "13mm","26mm", "5mm", ret.json.XMMC16);
				LODOP.ADD_PRINT_TEXT("57mm", "40mm", "20mm", "5mm", ret.json.XMJE16);
				
				LODOP.ADD_PRINT_TEXT("32mm", "68mm", "26mm", "5mm", ret.json.XMMC2);
				LODOP.ADD_PRINT_TEXT("32mm", "96mm", "20mm", "5mm", ret.json.XMJE2);
				LODOP.ADD_PRINT_TEXT("37mm", "68mm", "26mm", "5mm", ret.json.XMMC5);
				LODOP.ADD_PRINT_TEXT("37mm", "96mm", "20mm", "5mm", ret.json.XMJE5);
				LODOP.ADD_PRINT_TEXT("42mm", "68mm", "26mm", "5mm", ret.json.XMMC8);
				LODOP.ADD_PRINT_TEXT("42mm", "96mm", "20mm", "5mm", ret.json.XMJE8);				
				LODOP.ADD_PRINT_TEXT("47mm", "68mm", "26mm", "5mm", ret.json.XMMC11);
				LODOP.ADD_PRINT_TEXT("47mm", "96mm", "20mm", "5mm", ret.json.XMJE11);
				LODOP.ADD_PRINT_TEXT("52mm", "68mm", "26mm", "5mm", ret.json.XMMC14);
				LODOP.ADD_PRINT_TEXT("52mm", "96mm", "20mm", "5mm", ret.json.XMJE14);				
				LODOP.ADD_PRINT_TEXT("57mm", "68mm", "26mm", "5mm", ret.json.XMMC17);
				LODOP.ADD_PRINT_TEXT("57mm", "96mm", "20mm", "5mm", ret.json.XMJE17);

				LODOP.ADD_PRINT_TEXT("32mm", "124mm", "26mm", "5mm", ret.json.XMMC3);
				LODOP.ADD_PRINT_TEXT("32mm", "151mm", "20mm", "5mm", ret.json.XMJE3);
				LODOP.ADD_PRINT_TEXT("37mm", "124mm", "26mm", "5mm", ret.json.XMMC6);
				LODOP.ADD_PRINT_TEXT("37mm", "151mm", "20mm", "5mm", ret.json.XMJE6);
				LODOP.ADD_PRINT_TEXT("42mm", "124mm", "26mm", "5mm", ret.json.XMMC9);
				LODOP.ADD_PRINT_TEXT("42mm", "151mm", "20mm", "5mm", ret.json.XMJE9);				
				LODOP.ADD_PRINT_TEXT("47mm", "124mm", "26mm", "5mm", ret.json.XMMC12);
				LODOP.ADD_PRINT_TEXT("47mm", "151mm", "20mm", "5mm", ret.json.XMJE12);
				LODOP.ADD_PRINT_TEXT("52mm", "124mm", "26mm", "5mm", ret.json.XMMC15);
				LODOP.ADD_PRINT_TEXT("52mm", "151mm", "20mm", "5mm", ret.json.XMJE15);				
				LODOP.ADD_PRINT_TEXT("57mm", "124mm", "26mm", "5mm", ret.json.XMMC18);
				LODOP.ADD_PRINT_TEXT("57mm", "151mm", "20mm", "5mm", ret.json.XMJE18);
				//小计金额
				LODOP.ADD_PRINT_TEXT("62mm", "13mm", "20mm", "5mm", "小计：");
				LODOP.ADD_PRINT_TEXT("62mm", "40mm", "40mm", "5mm", ret.json.XJJE1);
				LODOP.ADD_PRINT_TEXT("62mm", "96mm", "40mm", "5mm", ret.json.XJJE2);				
				LODOP.ADD_PRINT_TEXT("62mm", "151mm", "40mm", "5mm", ret.json.XJJE3);
				
				LODOP.ADD_PRINT_TEXT("68mm", "30mm", "80mm", "6mm", ret.json.DXZJE);
				LODOP.ADD_PRINT_TEXT("68mm", "120mm", "30mm", "6mm", ret.json.FYHJ);
				LODOP.ADD_PRINT_TEXT("74mm", "30mm", "20mm", "5mm", ret.json.JKHJ);
				//LODOP.ADD_PRINT_TEXT("74mm", "85mm", "20mm", "5mm", ret.json.BJJE);
				LODOP.ADD_PRINT_TEXT("74mm", "85mm", "20mm", "5mm", ret.json.BJXJ);//zhaojian 2017-09-29 解决补缴金额不显示问题
				LODOP.ADD_PRINT_TEXT("74mm", "130mm", "20mm", "5mm", ret.json.CYTK);

				LODOP.ADD_PRINT_TEXT("80mm", "30mm", "20mm", "5mm", ret.json.YBHJ);
				LODOP.ADD_PRINT_TEXT("80mm", "65mm", "20mm", "5mm", ret.json.GRZHZF);
				LODOP.ADD_PRINT_TEXT("80mm", "95mm", "20mm", "5mm", ret.json.QTYBZF);
				LODOP.ADD_PRINT_TEXT("80mm", "135mm", "20mm", "5mm", ret.json.ZFJE);
				LODOP.ADD_PRINT_TEXT("68mm", "145mm", "40mm", "12mm", ret.json.BZ);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 7);
				
				LODOP.ADD_PRINT_TEXT("85mm", "30mm", "60mm", "5mm", ret.json.JGMC);
				LODOP.ADD_PRINT_TEXT("85mm", "100mm", "20mm", "5mm", ret.json.SYY);
				LODOP.ADD_PRINT_TEXT("85mm", "126mm", "20mm", "5mm", ret.json.N);
				LODOP.ADD_PRINT_TEXT("85mm", "138mm", "8mm", "5mm", ret.json.Y);
				LODOP.ADD_PRINT_TEXT("85mm", "148mm", "8mm", "5mm", ret.json.R);
				if(ret.json.njjbjsxx){
            		LODOP.ADD_PRINT_TEXT("54mm", "0mm", "25mm", 20, "统筹支付："+ret.json.njjbjsxx.BCTCZFJE);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
					LODOP.ADD_PRINT_TEXT("54mm", "25mm", "25mm", 20, "大病救助："+ret.json.njjbjsxx.BCDBJZZF);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
					LODOP.ADD_PRINT_TEXT("54mm", "50mm", "25mm", 20, "大病保险："+ret.json.njjbjsxx.BCDBBXZF);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("54mm", "75mm", "25mm", 20, "民政补助："+ret.json.njjbjsxx.BCMZBZZF);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("54mm", "100mm", "25mm", 20, "帐户支付："+ret.json.njjbjsxx.BCZHZFZE);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("54mm", "125mm", "25mm", 20, "现金支付："+ret.json.njjbjsxx.BCXZZFZE);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "0mm", "25mm", 20, "帐户支付自付："+ret.json.njjbjsxx.BCZHZFZF);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "25mm", "25mm", 20, "帐户支付自理："+ret.json.njjbjsxx.BCZHZFZL);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "50mm", "25mm", 20, "现金支付自付："+ret.json.njjbjsxx.BCXJZFZF);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "75mm", "25mm", 20, "现金支付自理："+ret.json.njjbjsxx.BCXJZFZL);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "100mm", "25mm", 20, "医保范围内费用："+ret.json.njjbjsxx.YBFWNFY);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "125mm", "50mm", 20, "帐户消费后余额："+ret.json.njjbjsxx.ZHXFHYE);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		var dicName = {
            			 id : "phis.dictionary.ybyllb"
          				};
						var dic=util.dictionary.DictionaryLoader.load(dicName);
						var di;
						di = dic.wraper[ret.json.njjbjsxx.YLLB];
						var text=""
						if (di) {
							text = di.text;
						}
					text=text.substring(text.indexOf("-")+1)
               		LODOP.ADD_PRINT_TEXT("20mm", "100mm", "30mm", "5mm","("+text+")");
                }
				if (LODOP.SET_PRINTER_INDEXA(ret.json.ZYJSDYJMC)){
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
							MyMessageTip.msg("提示", "该住院号码不在结算病人列表", true);
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
					MyMessageTip.msg("提示", "该住院号码不在结算病人列表", true);
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
					arrColumn[1].hidden = false;
					this.grid.getView().refresh();

					this.opener.JSLX = 10;
					this.schema = 'ZY_BRRY_CYLB';
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
					arrColumn[1].hidden = true;
					this.grid.getView().refresh();

					this.opener.JSLX = 5;
					this.schema = 'ZY_BRRY_CYLB_ZC';
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'a.CYPB'], ['i', 1]]];
					this.refresh();
				} else if (cndField.getValue() == 1) {

					var arrColumn = this.cm.config;
					arrColumn[1].hidden = true;
					this.grid.getView().refresh();

					this.opener.JSLX = 1;
					this.schema = 'ZY_BRRY_ZT';
					this.requestData.cnd = [
							'and',
							['notin', ['$', 'a.BRXZ'],
									[['s','select BRXZ from GY_BRXZ where DBPB > 0']]],
							['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'a.CYPB'], ['i', 0]]];
					this.refresh();
				} else if (cndField.getValue() == 4) {

					var arrColumn = this.cm.config;
					arrColumn[1].hidden = true;
					this.grid.getView().refresh();

					this.opener.JSLX = 4;
					this.schema = 'ZY_BRRY_CYLB_ZC';
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'a.CYPB'], ['i', 1]]];
					this.refresh();
				}
				this.opener.doNew();
			}
		});