/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("phis.application.reg.script")

$import("phis.script.SimpleList")

phis.application.reg.script.RegistrationDepartmentList = function(cfg) {
	phis.application.reg.script.RegistrationDepartmentList.superclass.constructor.apply(
			this, [cfg])
	this.disablePagingTbr = true;
// this.cookie = util.cookie.CookieOperater;
}

Ext.extend(phis.application.reg.script.RegistrationDepartmentList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				var radiogroup = [];
				var itemName = ['拼音 ','五笔'];
				this.srf = 1;
				for (var i = 1; i < 3; i++) {
					radiogroup.push({
						xtype : "radio",
						checked : i == 1,
						boxLabel : itemName[i - 1],
						inputValue : i,
						name : "srf",
						listeners : {
							check : function(group, checked) {
								if (checked) {
									if (group.inputValue == 1) {
										this.srf = 1;
									} else if (group.inputValue == 2) {
										this.srf = 2;
									}
								}
							},
							scope : this
						}
					})
				}
				var me=this;
				this.yzcheckbox={
						id:"gh-yzbz",
						xtype : "checkbox",
						style:"background-color:red;",
//						checked : false,
						boxLabel : "义诊",
						inputValue : 1,
						name : "yzbz",
						listeners : { "check" : function(obj,ischecked){
									me.ghyzbz=ischecked;
							}}
					};
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([tbar, "-", radiogroup,"-",this.yzcheckbox]);
			},
			onDblClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("departmentChoose", record);
				}
			},
			onRowClick : function() {
				this.cndField.focus();
			},
			/*
			 * onESCKey : function() { this.cndField.focus(); },
			 */
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				this.requestData.serviceId = "phis.registeredManagementService";
				this.requestData.serviceAction = "querySchedulingDepartment";
				this.requestData.yytag = this.opener.formModule.yytag;
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				// ** add by yzh **
				this.resetButtons();
				debugger;
			},
			createNormalField : function(it) {
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					width : this.queryWidth || 150,// add by liyl 2012-06-17
													// 字典查询框宽度自定义
					value : it.defaultValue,
					enableKeyEvents : true
				}
				var field;
				switch (it.type) {
					case 'int' :
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
					if (it.selected == "true") {
						selected = it.id;
						defaultItem = it;
					}
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
				var combox = null;
				if (fields.length > 1) {
					combox = new Ext.form.ComboBox({
								store : store,
								valueField : "value",
								displayField : "text",
								value : selected,
								mode : 'local',
								triggerAction : 'all',
								emptyText : '选择查询字段',
								selectOnFocus : true,
								width : 100
							});
					combox.on("select", this.onCndFieldSelect, this)
					this.cndFldCombox = combox
				} else {
					combox = new Ext.form.Label({
								text : fields[0].text
							});
					this.cndFldCombox = new Ext.form.Hidden({
								value : fields[0].value
							});
				}

				var cndField;
				if (defaultItem) {
					if (defaultItem.dic) {
						defaultItem.dic.src = this.entryName + "." + it.id
						defaultItem.dic.defaultValue = defaultItem.defaultValue
						defaultItem.dic.width = 150
						cndField = this.createDicField(defaultItem.dic)
					} else {
						cndField = this.createNormalField(defaultItem)
					}
				} else {
					cndField = new Ext.form.TextField({
								width : 150,
								selectOnFocus : true,
								name : "dftcndfld",
								enableKeyEvents : true
							})
				}
				this.cndField = cndField
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				cndField.on("keyup", this.oncndKeypress, this);
				return [combox, '-', cndField]
			},
			oncndKeypress : function(f,e){
				if ((e.getKey() >= 48 && e.getKey() <= 57)
						|| (e.getKey() >= 65 && e.getKey() <= 90)
						|| (e.getKey() >= 96 && e.getKey() <= 111)
						|| e.getKey() == 8 || e.getKey() == 32
						|| e.getKey() == 47 || e.getKey() == 59
						|| e.getKey() == 61 || e.getKey() == 173
						|| e.getKey() == 188 || (e.getKey() >= 190 && e.getKey() <= 192)
						|| (e.getKey() >= 219 && e.getKey() <= 222)) {
					var store = this.grid.getStore();
					var n = store.getCount()
					var PYDM = this.cndField.getValue().toUpperCase();
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i);
						if(this.srf==1){
							if (r.get("PYDM").toUpperCase().indexOf(PYDM) >= 0) {
								this.grid.getSelectionModel().selectRow(i);
								return;
							}
							if (r.get("PYDM").toUpperCase().substring(0,PYDM.length)==PYDM) {
								this.grid.getSelectionModel().selectRow(i);
								return;
							}
						}else if(this.srf==2){
							if (r.get("WBDM").toUpperCase().indexOf(PYDM) >= 0) {
								this.grid.getSelectionModel().selectRow(i);
								return;
							}
							if (r.get("WBDM").toUpperCase().substring(0,PYDM.length)==PYDM) {
								this.grid.getSelectionModel().selectRow(i);
								return;
							}
							
						}
					}
				}
				
			},
			onQueryFieldEnter : function(f, e) {
				if (e.getKey() == e.ENTER) {
					e.stopEvent()
					var lastIndex = this.grid.getSelectionModel().lastActive;
					var record = this.grid.store.getAt(lastIndex);
					if (record) {
						this.fireEvent("departmentChoose", record);
					}
				}else if (e.getKey() == 40) {
					e.stopEvent()
					var lastIndex = this.grid.getSelectionModel().lastActive;
					this.grid.getStore();
					var n = this.grid.store.getCount()
					if(lastIndex<n){
						this.grid.getSelectionModel().selectRow(lastIndex+1)
					}else{
						this.grid.getSelectionModel().selectRow(lastIndex)
					}
				}else if(e.getKey() == 38){
					e.stopEvent()
					var lastIndex = this.grid.getSelectionModel().lastActive;
					if(lastIndex>0){
						this.grid.getSelectionModel().selectRow(lastIndex-1)
					}else{
						this.grid.getSelectionModel().selectRow(lastIndex)
					}
				}
			},
			onStoreLoadData : function(store, records, ops) {
				if(this.SBXH){
					var _this = this;
					var deferFunction = function(){
						if(!_this.SBXH){
						return;}
//						_this.doPrint(_this.SBXH);
					}
					deferFunction.defer(1000);
				}
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					var this_ = this;
					Ext.Msg.alert("提示", "请先排班挂号科室!", function() {
								this_.opener.opener.closeCurrentTab();// 关闭收费模块
							});
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.grid.getSelectionModel().selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
			},
			doPrint : function(sbxh){
				var LODOP=getLodop();  
	    		LODOP.PRINT_INITA(10,10,501,499,"挂号收费发票");
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "registeredManagementService",
					serviceAction : "printMoth",
					sbxh : sbxh
					});
				this.SBXH = false;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(23,34,130,25,ret.json.jzhm);
				LODOP.ADD_PRINT_TEXT(47,34,130,25,ret.json.xm);
				LODOP.ADD_PRINT_TEXT(72,34,130,25,ret.json.rq);
				LODOP.ADD_PRINT_TEXT(95,34,130,25,ret.json.ghf);
				LODOP.ADD_PRINT_TEXT(119,34,130,25,ret.json.zlf);
				LODOP.ADD_PRINT_TEXT(142,34,130,25,ret.json.zjf);
				LODOP.ADD_PRINT_TEXT(165,34,130,25,ret.json.blf);
				LODOP.ADD_PRINT_TEXT(216,33,130,25,ret.json.BNZHZF);
				LODOP.ADD_PRINT_TEXT(239,33,130,25,ret.json.LNZHZF);
				LODOP.ADD_PRINT_TEXT(263,33,130,25,ret.json.TCJJ);
				LODOP.ADD_PRINT_TEXT(287,33,130,25,ret.json.KNJZJJ);
				LODOP.ADD_PRINT_TEXT(310,33,130,25,ret.json.GRXJZF);
				LODOP.ADD_PRINT_TEXT(347,32,45,25,"门诊号码");
				LODOP.ADD_PRINT_TEXT(347,77,107,25,ret.json.mzhm);
				LODOP.ADD_PRINT_TEXT(398,31,45,25,ret.json.ghks);
				LODOP.ADD_PRINT_TEXT(398,75,41,25,ret.json.ampm);
				LODOP.ADD_PRINT_TEXT(398,115,35,25,ret.json.mzxh);
				LODOP.ADD_PRINT_TEXT(398,148,30,25,"号");
				LODOP.ADD_PRINT_TEXT(427,30,148,25,ret.json.title);
				
				LODOP.ADD_PRINT_TEXT(23,281,130,25,ret.json.jzhm);
				LODOP.ADD_PRINT_TEXT(47,281,130,25,ret.json.xm);
				LODOP.ADD_PRINT_TEXT(68,281,130,25,ret.json.rq);
				LODOP.ADD_PRINT_TEXT(92,281,130,25,ret.json.ghf);
				LODOP.ADD_PRINT_TEXT(117,281,130,25,ret.json.zlf);
				LODOP.ADD_PRINT_TEXT(141,281,130,25,ret.json.zjf);
				LODOP.ADD_PRINT_TEXT(165,281,130,25,ret.json.blf);
				LODOP.ADD_PRINT_TEXT(211,282,130,25,ret.json.BNZHZF);
				LODOP.ADD_PRINT_TEXT(234,282,130,25,ret.json.LNZHZF);
				LODOP.ADD_PRINT_TEXT(258,282,130,25,ret.json.TCJJ);
				LODOP.ADD_PRINT_TEXT(282,282,130,25,ret.json.KNJZJJ);
				LODOP.ADD_PRINT_TEXT(306,282,130,25,ret.json.GRXJZF);
				LODOP.ADD_PRINT_TEXT(344,283,45,25,"门诊号码");
				LODOP.ADD_PRINT_TEXT(344,325,107,25,ret.json.mzhm);
				LODOP.ADD_PRINT_TEXT(394,283,46,25,ret.json.ghks);
				LODOP.ADD_PRINT_TEXT(394,328,52,25,ret.json.ampm);
				LODOP.ADD_PRINT_TEXT(394,378,32,25,ret.json.mzxh);
				LODOP.ADD_PRINT_TEXT(394,408,32,25,"号");
				LODOP.ADD_PRINT_TEXT(424,283,158,25,ret.json.title);
				if (LODOP.SET_PRINTER_INDEXA(ret.json.GHDYJMC)){
					if((ret.json.FPYL+"")=='1'){
						LODOP.PREVIEW();
					}else{
						LODOP.PRINT();
					}
				}else{
					LODOP.PREVIEW();
				}
			}
		});