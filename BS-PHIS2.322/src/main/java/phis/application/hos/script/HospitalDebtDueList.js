$package("phis.application.hos.script");

$import("phis.script.SimpleList", "util.dictionary.SimpleDicFactory",
		 "util.helper.Helper")

phis.application.hos.script.HospitalDebtDueList = function(cfg) {
	this.serverParams = {
		"serviceAction" : cfg.serviceAction
	}
	this.dicValue = [];
	this.id = 1;
	this.printurl = util.helper.Helper.getUrl();
	this.disablePagingTbr = true;
	this.totalCount = 0;
	phis.application.hos.script.HospitalDebtDueList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.hos.script.HospitalDebtDueList, phis.script.SimpleList, {
	init : function() {
		this.addEvents({
					"gridInit" : true,
					"beforeLoadData" : true,
					"loadData" : true,
					"loadSchema" : true
				})
		this.requestData = {
			serviceId : this.listServiceId,
			schema : this.entryName,
			ksType : 1,
			method : 'execute',
			dicValue : this.dicValue,
			cnd : this.cnds,
			pageSize : this.pageSize > 0 ? this.pageSize : 0,
			pageNo : 1
		}
		if (this.serverParams) {
			Ext.apply(this.requestData, this.serverParams)
		}
		if (this.autoLoadSchema) {
			this.getSchema();
		}
	},
	initPanel : function(sc) {
		if (this.grid) {
			if (!this.isCombined) {
				this.fireEvent("beforeAddToWin", this.grid)
				this.addPanelToWin();
			}
			return this.grid;
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		this.schema = schema;
		this.isCompositeKey = schema.isCompositeKey;
		var items = schema.items
		if (!items) {
			return;
		}

		this.store = this.getStore(items)
		if (this.mutiSelect) {
			this.sm = new Ext.grid.CheckboxSelectionModel()
		}
		this.cm = new Ext.grid.ColumnModel(this.getCM(items))
		var cfg = {
			stripeRows : true,
			border : false,
			store : this.store,
			cm : this.cm,
			sm : this.sm,
			height : this.height,
			loadMask : {
				msg : '正在加载数据...',
				msgCls : 'x-mask-loading'
			},
			buttonAlign : 'center',
			clicksToEdit : true,
			frame : true,
//			plugins : this.array.length == 0 ? null : this.array,// modife by
			enableHdMenu : this.enableHdMenu,
			viewConfig : {
				enableRowBody : this.enableRowBody,
				getRowClass : this.getRowClass
			}
		}
		if (this.group)
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
		if (this.gridDDGroup) {
			cfg.ddGroup = this.gridDDGroup;
			cfg.enableDragDrop = true
		}
		if (this.summaryable) {
			$import("org.ext.ux.grid.GridSummary");
			var summary = new org.ext.ux.grid.GridSummary();
			cfg.plugins = [summary]
			this.summary = summary;
		}
		var cndbars = this.getCndBar(1)
		if (!this.disablePagingTbr) {
			cfg.bbar = this.getPagingToolbar(this.store)
		} else {
			cfg.bbar = this.bbar
		}
		if (!this.showButtonOnPT) {
			if (this.showButtonOnTop) {
				cfg.tbar = (cndbars.concat(this.tbar || [])).concat(this
						.createButtons())
			} else {
				cfg.tbar = cndbars.concat(this.tbar || [])
				cfg.buttons = this.createButtons()
			}
		}

		if (this.disableBar) {
			delete cfg.tbar;
			delete cfg.bbar;
			cfg.autoHeight = true;
			cfg.frame = false;
		}
		this.expansion(cfg);// add by yangl
		this.grid = new this.gridCreator(cfg)
		this.schema = schema;
		this.grid.on("afterrender", this.onReady, this)
		this.grid.on("contextmenu", function(e) {
					e.stopEvent()
				})
		this.grid.on("rowcontextmenu", this.onContextMenu, this)
		this.grid.on("rowdblclick", this.onDblClick, this)
		this.grid.on("rowclick", this.onRowClick, this)
		this.grid.on("keydown", function(e) {
					if (e.getKey() == e.PAGEDOWN) {
						e.stopEvent()
						this.pagingToolbar.nextPage()
						return
					}
					if (e.getKey() == e.PAGEUP) {
						e.stopEvent()
						this.pagingToolbar.prevPage()
						return
					}
					if (e.getKey() == e.ESC) {
						if (this.onESCKey) {
							this.onESCKey();
						}
						return
					}
				}, this)

		if (!this.isCombined) {
			this.fireEvent("beforeAddToWin", this.grid)
			this.addPanelToWin();
		}
		return this.grid
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
			this.id = it.id;
			if ((it.display <= 0 || it.display == 2) || !ac.canRead(it.acValue)) {
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
						if (it.type == 'double' || it.type == 'bigDecimal') {
							c.precision = it.precision;
							c.nullToValue = it.nullToValue;
							c.renderer = function(value, metaData, r, row, col,
									store) {
								if (value == null && this.nullToValue) {
									value = parseFloat(this.nullToValue)
									var retValue = this.precision ? value
											.toFixed(this.precision) : value;
									try {
										r.set(this.id, retValue);
									} catch (e) {
										// 防止新增行报错
									}
									return retValue;
								}
								if (value != null) {
									value = parseFloat(value);
									var retValue = this.precision ? value
											.toFixed(this.precision) : value;
									return retValue;
								}
							}
						}
					}
					break
				case 'timestamp' :
					// c.renderer = Ext.util.Format.dateRenderer('Y-m-d HH:m:s')
			}
			if (it.renderer) {
				var func
				func = eval("this." + it.renderer)
				if (typeof func == 'function') {
					c.renderer = func;
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
	},
	expansion : function(cfg) {
		// 底部 统计信息,未完善
		var label = new Ext.form.Label({
			html : "<div id='debtDue' align='center' style='color:blue'>"
					+ "总人数：&nbsp;&nbsp;"
					+ 0
					+ "&nbsp;&nbsp;人&nbsp;&nbsp;&nbsp;&nbsp;欠费：&nbsp;&nbsp;"
					+ 0
					+ "&nbsp;&nbsp;人&nbsp;&nbsp;&nbsp;&nbsp;合计：&nbsp;&nbsp;<&nbsp;&nbsp;自负：&nbsp;&nbsp;"
					+ "￥&nbsp;&nbsp;"+0+"&nbsp;&nbsp;&nbsp;&nbsp;缴款：&nbsp;&nbsp;"
					+ "￥&nbsp;&nbsp;"+0+"&nbsp;&nbsp;&nbsp;&nbsp;欠费：&nbsp;&nbsp;"
					+ "￥&nbsp;&nbsp;"+0+"&nbsp;&nbsp;></div>"
		})
		cfg.bbar = [];
		cfg.bbar.push(label);
	},
	radioCompent : function(value, metadata, record, rowIndex, colIndex, store) {
		var zyh = "";
		record.data.ck = record.data.ZYH
		zyh = record.data.ZYH;

		var jkTotal = 0;// 缴款金额
		var zfTotal = 0;// 自负金额
		var qfCount = 0;// 欠费人数
		var qfTotal = 0;// 欠费总金额

		for (i = 0; i < store.getCount(); i++) {
			var record = store.getAt(i);
			if ((record.data.ZFJE - record.data.JKJE) >= 0) {
				qfCount++;
				zfTotal += record.data.ZFJE;
				jkTotal += record.data.JKJE;
				qfTotal += (record.data.ZFJE - record.data.JKJE);
			}
		}
		jkTotal = (jkTotal * 1).toFixed(2);
		zfTotal = (zfTotal * 1).toFixed(2);
		qfTotal = (qfTotal * 1).toFixed(2);
		totalCount = store.getCount();
		document.getElementById("debtDue").innerHTML = "<div id='debtDue' align='center' style='color:blue'>"
				+ "总人数：&nbsp;&nbsp;"
				+ totalCount
				+ "&nbsp;&nbsp;人&nbsp;&nbsp;&nbsp;&nbsp;欠费：&nbsp;&nbsp;"
				+ qfCount
				+ "&nbsp;&nbsp;人&nbsp;&nbsp;&nbsp;&nbsp;合计：&nbsp;&nbsp;<&nbsp;&nbsp;自负：&nbsp;&nbsp;"
				+ "￥&nbsp;&nbsp;"+zfTotal+"&nbsp;&nbsp;&nbsp;&nbsp;缴款：&nbsp;&nbsp;"
				+ "￥&nbsp;&nbsp;"+jkTotal+"&nbsp;&nbsp;&nbsp;&nbsp;欠费：&nbsp;&nbsp;"
				+ "￥&nbsp;&nbsp;"+qfTotal+"&nbsp;&nbsp;></div>";

		return '<input dd="22" type="radio" name="' + zyh
				+ '" value="y" checked=true /> 是    '
				+ '<input dd="33" type="radio" name="' + zyh
				+ '" value="n" /> 否'
	},
	getCndBar : function(type) {
		var cklb = this.getComboDic(type);
		cklb.setEditable(false);
		cklb.emptyText = '请选择';
		cklb.on("select", this.onCatalogChanage, this);
//		cklb.expandAll()// 展开树
		this.cklb = cklb;
		this.type = type

		return [this.radioGroup(type), this.labeObj('ksType', type), cklb,
				this.butS, this.butD];
	},
	radioGroup : function(type) {
		this.butS = {
			xtype : "button",
			text : "刷新",
			iconCls : "query",
			scope : this,
			handler : this.doQuery,
			disabled : false
		};
		this.butD = {
			xtype : "button",
			text : "打印",
			iconCls : "print",
			scope : this,
			handler : this.doPrint,
			disabled : false
		};
		
//		this.lab = new Ext.form.Label({
//							text : " 催款金额："
//						})
//						
//		this.CKJE = new Ext.form.TextField({
//			id:'CKJE'
//		});
//		
//		this.CKJE.on("blur",function(){
//			var ckValue = this.CKJE.getValue();
//			if(ckValue.length == 0){
//			}else if((!(/(^((([0-9]|[1-9]\d{0,5})(\.\d{0,2})?)|(1000000\.0{0,2}))$)/.test(ckValue)))){//催款金额范围0~100万之间 小数点精度2位
//				Ext.Msg.alert("无效","催款金额有效数据范围(0~100万之间),精确到小数点后2位!");
//				this.CKJE.setValue("");
//			}
//		},this);

		return new Ext.form.RadioGroup({
					height : 20,
					width : 190,
					id : 'prompt',
					name : 'prompt', // 后台返回的JSON格式，直接赋值
					value : type,
					items : [{
								boxLabel : '按科室催款',
								name : 'prompt',
								inputValue : 1
							}, {
								boxLabel : '按病区催款',
								name : 'prompt',
								inputValue : 2
							}],
					listeners : {
						change : function(prompt, newValue, oldValue, eOpts) {
							var toolbar = this.grid.getTopToolbar();
							toolbar.removeAll();
							if (newValue.inputValue == 1) {
								toolbar.add(this.getCndBar(1));
								this.requestData.ksType = 1;
								this.requestData.dicValue = [];
								this.refresh();
							} else if (newValue.inputValue == 2) {
								toolbar.add(this.getCndBar(2));
								this.requestData.ksType = 1;
								this.requestData.dicValue = [];
								this.refresh();
							}
							toolbar.doLayout();
						},
						scope : this
					}
				});
	},
	labeObj : function(id, type) {
		var text = "";
		if (type == 1) {
			text = " 科室: "
		} else if (type == 2) {
			text = " 病区: "
		} else {
			text = type;
		}
		return new Ext.form.Label({
					id : id,
					text : text
				});
	},
	getComboDic : function(type) {
		this.dicId = "";
		var dic = {}
		if (type == 1) {
			dic = {
					id : "phis.dictionary.hospitalOffice",
					autoLoad : true,
					defaultValue : "",
					sliceType : 1
				}
		} else if (type == 2) {
			dic = {
					id : "phis.dictionary.lesionOffice",
					defaultValue : "",
					sliceType : 0
				}
		}
		return util.dictionary.SimpleDicFactory.createDic(dic);
	},
	findChildNodes : function(node, value) {
		var childnodes = node.childNodes;
		for (var i = 0; i < childnodes.length; i++) {
			var nd = childnodes[i];
			value.push(nd.id);
			if (nd.hasChildNodes()) {
				this.findChildNodes(nd, value);
			}
		}
	},
	onCatalogChanage : function(node, e) {
		this.Nodetext = this.cklb.getRawValue();
		
//		var value = [];
//		var tbar = this.grid.getTopToolbar();
//		if (node.hasChildNodes()) {// 有子节点
//			// 得到所有子节点
//			this.findChildNodes(node, value);
//		} else {// 没有子节点
//			value.push(node.id);
//		}
		this.dicValue = this.cklb.getValue();
	},
	doQuery : function() {
//		if(this.CKJE.getValue().length == 0){
//			Ext.Msg.alert('提示','请输入催款金额！');
//			return;
//		}
		var text = "";
		if (this.type == 1) {
			text = "科室";
		} else {
			text = "病区";
		}
		if (this.cklb.getValue() == 0) {
			Ext.MessageBox.alert("提示", "请选择" + text);
			return;
		}

		this.requestData.ksType = this.type;
		this.requestData.dicValue = this.dicValue;
		
		
		this.store.on("datachanged",function(a){
			if(this.store.getTotalCount() == 0){
				document.getElementById("debtDue").innerHTML = "<div id='debtDue' align='center' style='color:blue'>"
					+ "总人数：&nbsp;&nbsp;"
					+ 0
					+ "&nbsp;&nbsp;人&nbsp;&nbsp;&nbsp;&nbsp;欠费：&nbsp;&nbsp;"
					+ 0
					+ "&nbsp;&nbsp;人&nbsp;&nbsp;&nbsp;&nbsp;合计：&nbsp;&nbsp;<&nbsp;&nbsp;自负：&nbsp;&nbsp;"
					+ "￥&nbsp;&nbsp;"+0+"&nbsp;&nbsp;&nbsp;&nbsp;缴款：&nbsp;&nbsp;"
					+ "￥&nbsp;&nbsp;"+0+"&nbsp;&nbsp;&nbsp;&nbsp;欠费：&nbsp;&nbsp;"
					+ "￥&nbsp;&nbsp;"+0+"&nbsp;&nbsp;></div>";
			}
		},this)
		this.refresh();
	},
	doPrint : function() {
		if (this.store.getCount() == 0) {
			Ext.Msg.alert("提示", "请先刷新数据,再点击打印!");
			return;
		}
//		if(this.CKJE.getValue().length == 0){
//			Ext.Msg.alert('提示','请输入催款金额！');
//			return;
//		}
		var radioGroupValue = [];
		var store = this.store;
		for (i = 0; i < store.getCount(); i++) {
			var record = store.getAt(i);
			var zyh = record.data.ZYH;// 获得住院号

			var el = Ext.fly(this.grid.getView().getCell(i, 1));// 将HTML转换成Element
			// 通过selector选择器获得radio的值
			var flag = el.child("input[dd='22']").dom.checked;// 如果为true,则在催款清单里显示催款单，否则，不显示

			if (flag) {
				radioGroupValue.push(zyh);
			}
		}

	//	var url = this.printurl
			//	+ "*.print?pages=phis.prints.jrxml.HospitalDebtDueList&queryType=1&ksType="
			//	+ this.type + "&dicValue=" + this.dicValue + "&text="
			//	+ encodeURIComponent(this.Nodetext) + "&zyhStr="
				//+ radioGroupValue.toString();
		var pages="phis.prints.jrxml.HospitalDebtDueList";
		 var url="resources/"+pages+".print?queryType=1&ksType="
				+ this.type + "&dicValue=" + this.dicValue + "&text="
				+ encodeURIComponent(this.Nodetext) + "&zyhStr="
				+ radioGroupValue.toString();
		/*window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
	  */
		 var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			//预览LODOP.PREVIEW();
			//预览LODOP.PRINT();
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			//预览
			LODOP.PREVIEW();
	}
})