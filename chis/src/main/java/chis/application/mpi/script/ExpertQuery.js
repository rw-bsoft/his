/**
 * 个人基本信息高级搜索
 * 
 * @author tianj
 */
$package("chis.application.mpi.script");

$import("chis.script.BizSimpleListView", "util.Accredit",
		"util.dictionary.TreeDicFactory");

chis.application.mpi.script.ExpertQuery = function(cfg) {
	cfg.entryName = "chis.application.mpi.schemas.MPI_DemographicInfo"
	cfg.idList = ["personName", "sexCode", "birthday", "bloodTypeCode",
			"nationalityCode", "nationCode", "educationCode",
			"maritalStatusCode"]
	cfg.refSchema = [{
				groupName : "phones",
				itemName : "phone",
				items : [{
							id : "phoneTypeCode",
							type : "string",
							alias : "电话类型",
							dic : {
								id : "phone"
							}
						}, {
							id : "phoneNo",
							alias : "电话号码",
							type : "string"
						}]
			}, {
				groupName : "certificates",
				itemName : "certificate",
				items : [{
							id : "certificateTypeCode",
							type : "string",
							alias : "证件类型",
							dic : {
								id : "certificate"
							}
						}, {
							id : "certificateNo",
							type : "string",
							alias : "证件号码"
						}]
			}, {
				groupName : "cards",
				itemName : "card",
				items : [{
							id : "cardTypeCode",
							type : "string",
							alias : "卡类型",
							dic : {
								id : "card"
							}
						}, {
							id : "cardNo",
							type : "string",
							alias : "卡号"
						}]
			}]
	cfg.height = 450;
	cfg.modal = cfg.modal;
	cfg.enableCnd = false;
	cfg.listServiceId = "chis.empiService";
	cfg.autoLoadData = false;
	cfg.serviceAction = "advancedSearch";
	chis.application.mpi.script.ExpertQuery.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.mpi.script.ExpertQuery,
		chis.script.BizSimpleListView, {
			clear : function() {
				if (!this.form) {
					return;
				}
				for (var i = 0; i < this.idList.length; i++) {
					var field = this.form.getForm().findField(this.idList[i]);
					if (field) {
						field.reset();
						if (field.getName() == "personName") {
							field.focus(true, true);
						}
					}
				}

				for (var i = 0; i < this.refSchema.length; i++) {
					var refSc = this.refSchema[i];
					for (var j = 0; j < refSc.items.length; j++) {
						var field = this.form.getForm()
								.findField(refSc.items[j].id);
						if (field) {
							field.reset();
						}
					}
				}
				this.store.removeAll();
			},

			warpPanel : function(grid) {
				var searchPanel = this.createSearchPanel();
				this.form = searchPanel;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : [{
										layout : "fit",
										split : true,
										title : '',
										height : 200,
										region : 'north',
										items : searchPanel
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										height : 300,
										width : this.width,
										items : grid
									}],
							buttons : [{
										xtype : "button",
										text : "查找",
										handler : this.doQuery,
										scope : this
									}, {
										xtype : "button",
										text : "选择",
										handler : this.onEmpiSelected,
										scope : this
									}, {
										xtype : "button",
										text : "重置",
										handler : this.clear,
										scope : this
									}, {
										xtype : "button",
										text : "取消",
										handler : function() {
											this.close();
										},
										scope : this
									}]

						});
				grid.__this = this;

				return panel;
			},

			addPanelToWin : function() {
				if (!this.fireEvent("panelInit", this.grid)) {
					return;
				};
				var win = this.getWin();
				win.setTitle("个人信息高级搜索");
				var panel = this.warpPanel(this.grid);
				win.add(panel);
				win.doLayout();
			},

			createSearchPanel : function() {
				var col = [{
							columnWidth : .5,
							layout : 'form',
							border : false,
							frame : false,
							defaultType : 'textfield',
							autoHeight : true,
							items : []
						}, {
							columnWidth : .5,
							layout : 'form',
							border : false,
							frame : false,
							defaultType : 'textfield',
							autoHeight : true,
							items : []
						}];

				var col = [{
							columnWidth : .5,
							layout : 'form',
							border : false,
							frame : false,
							defaultType : 'textfield',
							autoHeight : true,
							items : []
						}, {
							columnWidth : .5,
							layout : 'form',
							border : false,
							frame : false,
							defaultType : 'textfield',
							autoHeight : true,
							items : []
						}];
				var sw = 0;
				var i = 0;

				for (; i < this.idList.length; i++) {
					var it = this.getSchemaItemById(this.idList[i])
					if (it) {
						var f = this.createField(it);

						f.index = i;
						f.anchor = '95%';
						col[sw].items.push(f);
						sw = sw == 0 ? 1 : 0;
					}
				}

				// add items defined in other schema files
				for (var j = 0; j < this.refSchema.length; j++) {
					for (var k = 0; k < this.refSchema[j].items.length; k++) {
						var it = this.refSchema[j].items[k];
						var f = this.createField(it);
						f.index = i++;
						f.anchor = '95%';
						col[sw].items.push(f);
						sw = sw == 0 ? 1 : 0;
					}
				}

				var searchPanelCfg = {
					title : "基本信息",
					header : false,
					border : false,
					frame : true,
					autoHeight : true,
					autoWidth : true,
					border : false,
					tbar : [],
					shadow : false,
					items : {
						layout : 'column',
						items : col,
						autoWidth : true
					}
				};
				return new Ext.FormPanel(searchPanelCfg);
			},

			getSchemaItemById : function(id) {
				for (var i = 0; i < this.schema.items.length; i++) {
					var item = this.schema.items[i];
					if (id == item.id) {
						return item;
					}
				}
			},

			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200;
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					width : defaultWidth,
					value : it.defaultValue
				};
				if (it.inputType) {
					cfg.inputType = it.inputType;
				}
				if (it.fixed == true) {
					cfg.disabled = true;
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true;
				}
				if (it.evalOnServer && ac.canRead(it.acValue)) {
					cfg.disabled = true;
				}
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					cfg.disabled = true;
				}
				if (this.op == "update" && !ac.canUpdate(it.acValue)) {
					cfg.disabled = true;
				}
				if (it.dic) {
					it.dic.src = this.entryName + "." + it.id;
					it.dic.defaultValue = it.defaultValue;
					it.dic.width = defaultWidth;
					var combox = this.createDicField(it.dic);
					Ext.apply(combox, cfg);
					return combox;
				}
				if (it.length) {
					cfg.maxLength = it.length;
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false;
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = 300
						break;
				}
				return cfg;
			},

			doQuery : function() {
				var cnd
				var form = this.form.getForm();
				var props = {};
				// get values in IdList
				for (var i = 0; i < this.idList.length; i++) {
					var id = this.idList[i];
					var field = form.findField(id);
					if (!field) {
						continue;
					}
					var value = field.getValue();
					if (!value) {
						continue;
					}
					if (typeof(value) == "string") {
						value = value.trim();
					}
					if (value.length == 0) {
						continue;
					}
					props[id] = value
				}
				// get values in refSchemas
				for (var i = 0; i < this.refSchema.length; i++) {
					var group = this.refSchema[i]
					var groupName = group.groupName;
					var subProps = {};
					for (var j = 0; j < group.items.length; j++) {
						var item = group.items[j];
						id = item.id;
						var field = form.findField(id);
						if (!field) {
							continue;
						}
						var value = field.getValue().trim();
						if (value.length == 0) {
							continue;
						}
						subProps[id] = value;
					}
					if (!props[group.groupName]) {
						props[group.groupName] = [];
					}
					props[groupName].push(subProps);
				}

				this.requestData.serviceAction = this.serviceAction;
				this.requestData.body = props;
				this.refresh();
			},

			onEmpiSelected : function() {
				var selectedRecord = this.getSelectedRecord();
				if (selectedRecord) {
					this.close();
					this.fireEvent("empiSelected", selectedRecord);
				}
			},

			close : function() {
				this.win.hide();
			},

			getWin : function() {
				var win = this.win;
				var closeAction = "close";
				if (!this.mainApp) {
					closeAction = "hide";
				}
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width || 800,
								height : this.height || 450,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : true
							});
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					win.on("show", function() {
								this.clear();
							}, this);
					win.on("add", function() {
								this.win.doLayout();
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					this.win = win;
				}
				win.instance = this;
				return win;
			},

			onDblClick : function(grid, index, e) {
				this.onEmpiSelected();
			}
		})
