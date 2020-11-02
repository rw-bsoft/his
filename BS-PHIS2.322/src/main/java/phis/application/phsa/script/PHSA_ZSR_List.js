$package("phis.application.phsa.script")

$import("phis.script.SimpleList","phis.script.ux.GroupSummary","phis.script.ux.RowExpander")
/**
 * 总收入数据明细展示list
 */
phis.application.phsa.script.PHSA_ZSR_List = function(cfg) {
	this.serverParams = {serviceAction:cfg.queryActionId};
	cfg.disablePagingTbr = true;
	cfg.group = "LB_text";
	cfg.width="900";
	cfg.groupTextTpl = "<table width='45%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='20%'>&nbsp;&nbsp;<b>类别:{[values.rs[0].data.LB_text]}</b></td><td width='20%'><div align='left'><b>&nbsp;&nbsp;({[values.rs.length]} 条记录)</b></div></td></tr></table>"
	phis.application.phsa.script.PHSA_ZSR_List.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(phis.application.phsa.script.PHSA_ZSR_List,
		phis.script.SimpleList, {
		initPanel : function(sc) {
			this.summaryable = true;
			if (this.grid) {
				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid);
					this.addPanelToWin();
				}
				return this.grid;
			}
			var schema = sc;
			if (!schema) {
				var re = util.schema.loadSync(this.entryName);
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel);
					return;
				}
			}
			this.schema = schema;
			this.isCompositeKey = schema.compositeKey;
			var items = schema.items
			if (!items) {
				return;
			}
			this.store = this.getStore(items)
			this.cm = new Ext.grid.ColumnModel(this.getCM(items));
			var cfg = {
				border : false,
				store : this.store,
				cm : this.cm,
				height : this.height,
				loadMask : {
					msg : '正在加载数据...',
					msgCls : 'x-mask-loading'
				},
				buttonAlign : 'center',
				clicksToEdit : 1,
				frame : true,
				plugins : this.rowExpander,
				viewConfig : {
					// forceFit : true,
					enableRowBody : this.enableRowBody,
					getRowClass : this.getRowClass
				}
			};
			if (this.sm) {
				cfg.sm = this.sm;
			}
			if (this.viewConfig) {
				Ext.apply(cfg.viewConfig, this.viewConfig);
			}
			if (this.group) {
				cfg.view = new Ext.grid.GroupingView({
							// forceFit : true,
							showGroupName : true,
							enableNoGroups : false,
							hideGroupedColumn : true,
							enableGroupingMenu : false,
							columnsText : "表格字段",
							groupByText : "使用当前字段进行分组",
							showGroupsText : "表格分组",
							groupTextTpl : this.groupTextTpl
						});
			}
			if (this.gridDDGroup) {
				cfg.ddGroup = this.gridDDGroup;
				cfg.enableDragDrop = true;
			}
			if (this.summaryable) {
//				var summary = new org.ext.ux.grid.GridSummary();
				var summary = new Ext.grid.GroupSummary();
				cfg.plugins = [summary];
				this.summary = summary;
			}
			var cndbars = this.getCndBar(items);
			if (!this.disablePagingTbr) {
				cfg.bbar = this.getPagingToolbar(this.store)
			} else {
				cfg.bbar = this.bbar;
			}
			if (!this.showButtonOnPT) {
				if (this.showButtonOnTop) {
					cfg.tbar = (cndbars.concat(this.tbar || []))
							.concat(this.createButtons());
				} else {
					cfg.tbar = cndbars.concat(this.tbar || []);
					cfg.buttons = this.createButtons();
				}
			}
			this.expansion(cfg);// add by yangl
			this.grid = new this.gridCreator(cfg);
			// this.grid.getTopToolbar().enableOverflow = true
			this.grid.on("render", this.onReady, this);
			this.grid.on("contextmenu", function(e) {
						e.stopEvent();
					});
			this.grid.on("rowcontextmenu", this.onContextMenu, this);
			this.grid.on("rowdblclick", this.onDblClick, this);
			this.grid.on("rowclick", this.onRowClick, this);
			this.grid.on("keydown", function(e) {
						if (e.getKey() == e.PAGEDOWN) {
							e.stopEvent();
							this.pagingToolbar.nextPage();
							return
						}
						if (e.getKey() == e.PAGEUP) {
							e.stopEvent();
							this.pagingToolbar.prevPage();
							return
						}
					}, this);
	
			if (!this.isCombined) {
				this.fireEvent("beforeAddToWin", this.grid);
				this.addPanelToWin();
			}
			return this.grid;
		},
		getCM:function(items){
			var cm = [];
			var ac =  util.Accredit;
			var expands = [];
			if (this.showRowNumber) {
				cm.push(new Ext.grid.RowNumberer());
			}
			for(var i = 0; i <items.length; i ++){
				var it = items[i];
				if((it.display <= 0 || it.display == 2 || it.hidden == true)  || !ac.canRead(it.acValue)){
					continue;
				}
				if(it.expand){
					var expand = {
						id: it.dic ? it.id + "_text" : it.id,
						alias:it.alias,
						xtype:it.xtype
					}
					expands.push(expand);
					continue;
				}
				if (!this.fireEvent("onGetCM", it)) { // **
					// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
					continue;
				}
				var width = parseInt(it.width || 80);
				//if(width < 80){width = 80}
				var c = {
					header:it.alias,
					width:width,
					sortable:true,
					dataIndex: it.dic ? it.id + "_text" : it.id
				};
				if(!this.isCompositeKey && it.pkey){
					c.id = it.id;
				}
				//添加summaryType和summaryRenderer
				if (it.summaryType) {
					c.summaryType = it.summaryType;
					if (it.summaryRenderer) {
						var func = eval("this." + it.summaryRenderer)
						if (typeof func == 'function') {
							c.summaryRenderer = func;
						}
					}
				}
				switch(it.type){
					case 'int':
					case 'double':
					case 'bigDecimal':
						if(!it.dic){
							c.css = "color:#00AA00;font-weight:bold;"
							c.align = "right"
							if(it.precision > 0){
								var nf = '0.';
								for(var j=0;j<it.precision;j++){
									nf += '0';
								}
								c.renderer = Ext.util.Format.numberRenderer(nf);
							}
						}
						break
					case 'date':
						c.renderer = function(v){
							 if (v && typeof v =='string' && v.length >10) {
				                return v.substring(0,10);
				            }
				            return v;
						}
						break
					case 'timestamp':
					case 'datetime':
						if (it.xtype == 'datefield') {
							c.renderer = function(v) {
								if (v && typeof v == 'string' && v.length > 10) {
									return v.substring(0, 10);
								} else {
									return v;
								}
							}
						}
						break
				}			
				if(it.renderer){
					var func
					func = eval("this."+it.renderer)
					if(typeof func == 'function'){
						c.renderer = func
					}
				}
				
				if(this.fireEvent("addfield",c,it)){
					cm.push(c)
				}
			}
			if(expands.length > 0){
				this.rowExpander = this.getExpander(expands)
				cm = [this.rowExpander].concat(cm)
			}
			return cm
		},
		/**
		 * 扩展顶部工具栏
		 * 		添加开始统计时间和结束统计时间
		 * @param cfg
		 */
		expansion : function(cfg) {
			// 顶部工具栏
			var label = new Ext.form.Label({
						text : "统计日期"
					});
			this.beginDate = new Ext.form.DateField({
					name : 'storeDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d'
				});
			this.endDate = new Ext.form.DateField({
				name : 'storeDate',
				value : new Date().add(Date.DAY,-1) ,
				width : 100,
				allowBlank : false,
				altFormats : 'Y-m-d',
				format : 'Y-m-d'
			});
			var tbar = cfg.tbar;
			delete cfg.tbar;
			cfg.tbar = [];
			cfg.tbar.push([label, '-', this.beginDate,' 至 ',this.endDate, tbar]);
			cfg.bbar = [];
			this.hj = new Ext.form.Label({
				text : "",
				style : "color:#335ebd;font-weight:bold;"
			});
			cfg.bbar.push('->','合计：', this.hj);
		},
		
		/**
		 * 挂号费总计
		 * @param v
		 * @param params
		 * @param data
		 * @returns
		 */
		totalGHF : function(v, params, data) {
			if (params.style) {
				params.style += "font-size:16px;"
			}
			return  v.toFixed(2)
		},
		/**
		 * 医疗费总计
		 * @param v
		 * @param params
		 * @param data
		 * @returns
		 */
		totalYLF : function(v, params, data) {
			if (params.style) {
				params.style += "font-size:16px;"
			}
			return  v.toFixed(2)
		},
		/**
		 * 药品费总计
		 * @param v
		 * @param params
		 * @param data
		 * @returns
		 */
		totalYPF : function(v, params, data) {
			if (params.style) {
				params.style += "font-size:16px;"
			}
			return  v.toFixed(2)
		},
		/**
		 * 其他费总计
		 * @param v
		 * @param params
		 * @param data
		 * @returns
		 */
		totalQTF : function(v, params, data) {
			if (params.style) {
				params.style += "font-size:16px;"
			}
			return  v.toFixed(2)
		},
		/**
		 * 显示合计
		 * @param v
		 * @param params
		 * @param data
		 * @returns {String}
		 */
		showHJ : function(v, params, data) {
			return '合计';
		},
		/**
		 * 刷新按钮实现
		 */
		doRefresh : function() {
			var beginDate = new Date(this.beginDate.getValue()).format("Y-m-d");
			var endDate = new Date(this.endDate.getValue()).format("Y-m-d");
			if (beginDate != null && endDate != null && beginDate != ""
				&& endDate != "" && beginDate > endDate) {
				Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
				return;
			}
			this.requestData.body={
					beginDate : beginDate,
					endDate : endDate
			};
			this.store.load({
				callback: function(records, options, success){
					this.setHJValue();    // 调用重新合计
				},
				scope: this//作用域为this。必须加上否则this.setHJValue(); 无法调用
			});
			
			
		},
		/**
		 * 设置合计值
		 */
		setHJValue : function(){
			var items = this.store.data.items;
			var hj = 0;
			if(items){
				var item;
				for(var i=0; i<items.length ; i++){
					item = items[i];
					hj += item.data.GHF;
					hj += item.data.YLF;
					hj += item.data.YPF;
					hj += item.data.QTF;
				}
			}
			//取两位小数
			hj = parseInt(hj*100)/100;
			this.hj.setText(hj);
		},
		setZSR : function(zsr, queryTime){
			this.beginDate.setValue(new Date(queryTime));
			this.endDate.setValue(Date.parseDate(Date.getServerDate(),'Y-m-d').add(Date.DAY,-1));
			this.hj.setText(zsr);
		}
});