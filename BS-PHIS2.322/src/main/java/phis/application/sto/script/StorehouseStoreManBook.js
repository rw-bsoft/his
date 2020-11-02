$package("phis.application.sto.script");
$import("phis.script.SimpleList","util.helper.Helper");

phis.application.sto.script.StorehouseStoreManBook = function(cfg) {
	cfg.autoLoadData = true;
	phis.application.sto.script.StorehouseStoreManBook.superclass.constructor.apply(
			this, [cfg]);
	this.printurl = util.helper.Helper.getUrl();
};
Ext.extend(phis.application.sto.script.StorehouseStoreManBook,
		phis.script.SimpleList, {
	initPanel : function(sc) {
		if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
		this.tbar=this.initConditionFields();
// this.requestData.jzrq=this.tbar[1].getValue().format('Ymd');
		 var grid = phis.application.sto.script.StorehouseStoreManBook.superclass.initPanel
						.apply(this, sc);
				this.grid = grid;
				return grid;
	},		
	loadData : function() {
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.serviceAction;
				this.requestData.body ={"yksb":this.mainApp['phis'].storehouseId,"zblb":this.tbar[0].getValue(),"pydm":this.tbar[1].getValue()}
				phis.application.sto.script.StorehouseStoreManBook.superclass.loadData.call(this);
//				this.clear();
//				var body = {};
//// body["ksrq"] = beginDate;
//// body["jsrq"] = endDate;
//				body["yksb"] = this.mainApp['phis'].storehouseId;// 药库识别
//				this.clear();
//				this.requestData.pageNo = 1;
//				this.requestData.pageSize = 25;
//				this.requestData.serviceId = this.serviceId;
//				this.requestData.serviceAction = this.serviceAction;
//				this.requestData.body = body;
//				if (this.store) {
//					if (this.disablePagingTbr) {
//						this.store.load();
//					} else {
//						var pt = this.grid.getBottomToolbar();
//						if (this.requestData.pageNo == 1) {
//							pt.cursor = 0;
//						}
//						pt.doLoad(pt.cursor);
//					}
//				}
//				this.resetButtons();
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store); // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick();
				}
			},
	initConditionFields : function() {
				var items = [];				
				var yplxStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[0, '全部'], [2, '西药'], [3, '中成药'], [4, '草药'], [32, '材料'], [49, '防保业务'], [17, '自费药']]
						});
				items.push(new Ext.form.ComboBox({
							store : yplxStore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : 0
						}));
				items.push(new Ext.form.TextField({
				id:'pydm',
				fieldLabel : '拼音码',
				emptyText:'拼音码',
				name : 'pydm',
				width : 90,
				allowBlank : true,
				blankText : '请输入拼音码',
				//maskRe : new RegExp('[A-Z]', 'gi'),
			    enableKeyEvents:true
				}));	
				items.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this,
							handler : this.loadData
						});		
				var date = new Date().format('Y-m-d');
				var beginDate = date.substring(0, date.lastIndexOf("-"))
						+ "-01";
				items.push(new Ext.form.Label({
						   text:'从：'
						  }));
				items.push(new Ext.form.DateField({
							name : 'beginDate',
							value : beginDate,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						}));
				items.push(new Ext.form.Label({
						   text:'至：'
						  }));
				items.push(new Ext.form.DateField({
							name : 'endDate',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						}));
				items.push({
							xtype : "button",
							text : "打印",
							iconCls : "print",
							scope : this,
							handler : this.doPrint
						});
				return items
			},
		//zhaojian 2017-09-26 药库保管员账册查询条件增加账簿类别
		/*doCndQuery : function(button, e, addNavCnd) { // ** modified by yzh ,
		// 2010-06-09 **
		var initCnd = this.initCnd;
		var itid = this.cndFldCombox.getValue();
		var items = this.schema.items;
		var it;
		for (var i = 0; i < items.length; i++) {
			if (items[i].id == itid) {
				it = items[i];
				break;
			}
		}
		if (!it) {
			if (addNavCnd) {
				if (initCnd) {
					this.requestData.cnd = ['and', initCnd, this.navCnd];
				} else {
					this.requestData.cnd = this.navCnd;
				}
				this.refresh();
				return
			} else {
				return;
			}
		}
		this.resetFirstPage();
		var v = this.cndField.getValue();
		var rawV = this.cndField.getRawValue();
		if (v == null || v == "" || rawV == null || rawV == "") {
			var cnd = [];
			this.queryCnd = null;
			if (addNavCnd) {
				if (initCnd) {
					this.requestData.cnd = ['and', initCnd, this.navCnd];
				} else {
					this.requestData.cnd = this.navCnd;
				}
				this.refresh();
				return
			} else {
				if (initCnd)
					cnd = initCnd;
			}
			this.requestData.cnd = cnd.length == 0 ? null : cnd;
			this.refresh();
			return
		}
		var refAlias = it.refAlias || "a";
		var cnd = ['eq', ['$', refAlias + "." + it.id]];
		if (it.dic) {
			if (it.dic.render == "Tree") {
				// var node = this.cndField.selectedNode
				// @@ modified by chinnsii 2010-02-28, add "!node"
				cnd[0] = 'eq';
				// if (!node || !node.isLeaf()) {
				// cnd[0] = 'like'
				// cnd.push(['s', v + '%'])
				// } else {
				cnd.push(['s', v]);
				// }
			} else {
				cnd.push(['s', v]);
			}
		} else {
			switch (it.type) {
				case 'int' :
					cnd.push(['i', v]);
					break;
				case 'double' :
				case 'bigDecimal' :
					cnd.push(['d', v]);
					break;
				case 'string' :
					// add by liyl 07.25 解决拼音码查询大小写问题
					if (it.id == "PYDM" || it.id == "WBDM") {
						v = v.toUpperCase();
					}
					cnd[0] = 'like';
					cnd.push(['s', v + '%']);
					break;
				case "date" :
					v = v.format("Y-m-d");
					cnd[1] = ['$',
							"str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"];
					cnd.push(['s', v]);
					break;
			}
		}
		this.queryCnd = cnd;
		if (initCnd) {
			cnd = ['and', initCnd, cnd];
		}
		if (addNavCnd) {
			this.requestData.cnd = ['and', cnd, this.navCnd];
			this.refresh();
			return
		}
		this.requestData.cnd = cnd;
		this.refresh();
	},*/
			onDblClick : function(grid, index, e) {
				this.doPrint();
			},
			doClose : function(){
				this.opener.closeCurrentTab();
			},
			doPrint : function() {
				var r = this.grid.getSelectionModel().getSelected();
				//var url = this.printurl + "*.print?pages=phis.prints.jrxml.StorehouseStoreManBookReport";
				var pages="phis.prints.jrxml.StorehouseStoreManBookReport";
				 var url="resources/"+pages+".print?type=1";
				url += "&ksrq=" + this.tbar[4].getValue().format('Y-m-d')+"&yksb="+this.mainApp['phis'].storehouseId;// 药库识别
				url += "&jsrq="+this.tbar[6].getValue().format('Y-m-d');
				// 药品信息(名称,规格,单位)
				
				var ypmc = encodeURI(encodeURI((r.get("YPMC"))));
				var ypgg = encodeURI(encodeURI(r.get("YPGG")));
				var ypdw = encodeURI(encodeURI(r.get("YPDW")));
				var cdmc = encodeURI(encodeURI(r.get("CDMC")));
				url += "&ypmc="+encodeURI(ypmc);// 药品名称
				url += "&ypgg="+encodeURI(ypgg);// 药品规格
				url += "&ypdw="+encodeURI(ypdw);// 药品单位
				url += "&cdmc="+encodeURI(cdmc);// 产地名称
				url += "&ypxh="+r.get("YPXH");// 药品序号
				url += "&ypcd="+encodeURI(r.get("YPCD"));// 药品产地
				url += "&temp=" + new Date().getTime();
				/*window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
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
});