/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("phis.application.reg.script");

$import("phis.script.SimpleList");

phis.application.reg.script.RegistrationDoctorManageList = function(cfg) {
	phis.application.reg.script.RegistrationDoctorManageList.superclass.constructor
			.apply(this, [cfg])
	this.autoLoadData = false;
	this.modal = true;
	this.disablePagingTbr = true;
}

Ext.extend(phis.application.reg.script.RegistrationDoctorManageList,
		phis.script.SimpleList, {
			onDblClick : function(grid, index, e) {
//				if (this.BRXX) {
					var lastIndex = grid.getSelectionModel().lastActive;
					var record = grid.store.getAt(lastIndex);
					if (record) {
						this.fireEvent("doctorChoose", record);
					}
//				}
			},
			onRowClick : function() {
				this.cndField.focus();
			},
			onESCKey : function() {
				this.fireEvent("doctorCancleChoose");
			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				this.requestData.serviceId = "phis.registeredManagementService";
				this.requestData.serviceAction = "querySchedulingDoctor";
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
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					if (this.YSDM) {
						this_ = this;
						Ext.Msg.alert("提示", "该预约医生未排班!", function() {
									this_.opener.formModule.doNew();
								});
					}else if (this.BRXX) {
						this.opener.formModule.doSave();
					}
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.grid.getSelectionModel().selectRow(0);
					if (this.BRXX) {
						this.cndField.focus();
					}
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				if (this.YSDM) {
					var YSstore = this.grid.getStore();
					var n = YSstore.getCount()
					this.wpb = true;
					for (var i = 0; i < n; i++) {
						var r = YSstore.getAt(i);
						if (r.data.YSDM == this.YSDM) {
							this.selectRow(i);
							this.fireEvent("doctorChoose", r);
							this.wpb = false;
							break;
						}
					}
					var form = this.opener.formModule.form.getForm();
					if ((!form.findField("JZYS").getValue())&&this.wpb) {
						this_ = this;
						Ext.Msg.alert("提示", "该预约医生未排班!", function() {
									this_.opener.formModule.doNew();
								});
					}
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
								name : "dftcndfld"
							})
				}
				this.cndField = cndField
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				cndField.on("keyup", this.oncndKeypress, this);
				return [combox, '-', cndField]
			},
			doCndQuery : function(button, e, addNavCnd) {
				var v = this.cndField.getValue()
				var kscnd = ['eq', ['$', 'a.KSDM'],['i', this.KSDM]];
				var pycnd = ['like', ['$', 'b.PYDM'],['s', v.toUpperCase() + '%']];
				this.requestData.ghsj = this.GHSJ;
				this.requestData.ghlb = this.GHLB;
				this.refresh()
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
						if (r.get("PYCODE").toUpperCase().indexOf(PYDM) >= 0) {
							this.grid.getSelectionModel().selectRow(i);
							return;
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
						this.fireEvent("doctorChoose", record);
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
			}
		});