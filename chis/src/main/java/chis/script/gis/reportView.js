$package("chis.script.gis")

$import("app.desktop.Module", "util.dictionary.TreeDicFactory",
		"util.rmi.jsonRequest", "util.rmi.miniJsonRequestSync")

chis.script.gis.reportView = function(cfg) {
	var def = {
		width : 620,
		westWidth : 200,
		navDic : 'reportCollect',
		listField : "regionCode",
		exQueryParam : {}
	}
	Ext.apply(this, def)
	chis.script.gis.reportView.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.script.gis.reportView, app.desktop.Module, {
	initPanel : function() {
		if (this.panel) {
			return this.panel
		}
		var navDic = this.navDic
		var tr = util.dictionary.TreeDicFactory.createDic({
					id : navDic,
					checkModel : "childCascade"
				})

		var items = [];
		items.push({
					split : true,
					title : '',
					layout : 'fit',
					region : 'west',
					width : this.westWidth,
					items : [tr.tree]
				});
		var tabpanel = this.createTabPanel();
		items.push(tabpanel);
		var cfg = {
			layout : 'border',
			width : this.width,
			height : this.height,
			border : false,
			frame : false,
			items : items
		}
		cfg.buttons = this.createButtons()
		var panel = new Ext.Panel(cfg)
		tr.tree.on("click", this.doPrint, this)
		this.panel = panel
		this.tree = tr.tree
		return panel
	},
	
	createDicField : function(dic) {
		var cls = "util.dictionary.";
		if (!dic.render) {
			cls += "Tree";
		} else {
			cls += dic.render;
		}
		cls += "DicFactory"

		var factory = eval("(" + cls + ")")
		var field = factory.createDic(dic)
		return field
	},

	createTabPanel : function() {
		if (this.mainTab) {
			return this.mainTab;
		}
		var ifWidth = document.body.offsetWidth - 220
		var ifHeight = document.body.offsetHeight - 120
		var tabitem = [{
			id:"reportView_tab",
			viewType : "report",
			layout : "fit",
			title : "报表统计",
			html : "<Iframe id='report_Iframe' width=" + ifWidth + " height="
					+ ifHeight + " onload='reportViewMask(\"reportView_tab\")'/>",
			exCfg : {
				script : "app.modules.chart.DiggerChartView",
				href : "ichart",
				showCndsBar : false
			}
		}]
		var printRadio=this.printRadio;
		if(!printRadio){
			printRadio = new Ext.form.RadioGroup({
					hideLabel : true,
					value : 'single',
					autoWidth : true,
					autoHeight : true,
					items : [{
								boxLabel : '普通预览',
								name : 'gird-print-1',
								inputValue : '1',
								checked : true
							}, {
								boxLabel : 'DOC预览',
								name : 'gird-print-1',
								inputValue : '2'
							}, {
								boxLabel : 'PDF预览',
								name : 'gird-print-1',
								inputValue : '0'
							}, {
								boxLabel : 'EXCEL预览',
								name : 'gird-print-1',
								inputValue : '3'
							}]
				});
				this.printRadio=printRadio;
		}
		var tabpanel = new Ext.TabPanel({
					tbar : [{
									xtype : "panel",
									width : 400,
									height : 40,
									frame : true,
									layout : "form",
									items :printRadio
									},
									"　网格地址:", this.createDicField({
										id : "areaGrid",
										parentKey : this.mainApp.regionCode,
										rootVisible:true
										}),
										"-",
										{
										text : "查询",
										handler : this.doShowReport,
										iconCls : "query",
										scope : this
									}],
					border : true,
					bodyBorder : true,
					region : 'center',
					activeTab : 0,
					tabPosition : 'bottom',
					items : tabitem
				});

		this.mainTab = tabpanel
		return tabpanel;
	},

	doPrint : function(node, e) {
		var jrxml = node.id;
		this.jrxml=jrxml;
	},
	doShowReport : function() {
		var type = this.printRadio.getValue().inputValue;
		var lhref = location.href
		var index = lhref.indexOf("HZEHR/")
		var tbar = this.mainTab.getTopToolbar();
		var url;
		var f;
		for(var i=0;i<tbar.items.getCount();i++)
		{
		var v=tbar.items.item(i);
			if(v.getXTypes()=="component/box/field/textfield/trigger/treeField"&&v.tree.getSelectionModel().getSelectedNode())
			{
				f=v.tree.getSelectionModel().getSelectedNode().attributes
				url = lhref.substring(0, index + 6) + ".print?type=" + type
				+ "&pages=" + this.jrxml+"&regionCode="+f.key+"&name="+f.text;
				break;
			}else
			{
			 	url = lhref.substring(0, index + 6) + ".print?type=" + type
				+ "&pages=" + this.jrxml;
			}
		}
		Ext.getCmp("reportView_tab").el.mask("正在生成报表...","x-mask-loading")
		this.url=url
		var myDate = new Date();
		document.getElementById('report_Iframe').src = url+myDate.getMilliseconds();
	},
	loadJsonData : function() {
		var req = {
			serviceId : this.serviceId || "chis.simpleReport",
			schema : this.entryName,
			title : this.title,
			pageSize : 500
		}
		if (this.exQueryParam) {
			for (var name in this.exQueryParam) {
				var v = this.exQueryParam[name]
				if (typeof v == "object") {
					v = v.key
				}
				req[name] = v;
			}
		}
		util.rmi.jsonRequest(req, function(code, msg, json) {
					if (code < 300) {
						if (json.totalCount > 0) {
							this.data = json.body
						} else {
							this.data = [];
						}

					} else {
						if (msg == "NotLogon" && this.mainApp) {
							this.fireEvent("NotLogon", this.loadJsonData, this);
						} else {
							alert(msg)
						}
					}
				}, this)
	},

	createButtons : function() {
		var actions = this.actions
		var buttons = []
		if (!actions) {
			return buttons
		}
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			var btn = {
				text : action.name,
				ref : action.ref,
				cmd : action.delegate || action.id,
				iconCls : action.iconCls || action.id,
				enableToggle : (action.toggle == "true"),
				script : action.script,
				handler : this.doAction,
				scope : this
			}
			buttons.push(btn)
		}
		return buttons
	},

	doAction : function(item, e) {
		var cmd = item.cmd
		var script = item.script
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
	},

	getWin : function() {
		var win = this.win
		if (!win) {
			var cfg = {
				id : this.id,
				title : this.title,
				width : this.width,
				height : this.height,
				iconCls : 'bogus',
				shim : true,
				layout : "fit",
				items : this.initPanel(),
				animCollapse : true,
				constrainHeader : true,
				minimizable : true,
				maximizable : true,
				shadow : false
			}
			if (!this.mainApp) {
				cfg.closeAction = 'hide'
			}
			win = new Ext.Window(cfg)
			win.on("resize", this.onWinResize, this)
			win.on("show", function() {
						jsReady = true;
					});
			// win.on("show",this.initMap,this)
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			this.win = win
		}
		win.instance = this;
		return win
	},

	onWinResize : function() {
	}

})

reportViewMask = function(reportView_tab){
	Ext.getCmp(reportView_tab).el.unmask()
}
