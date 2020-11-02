$package("chis.script.gis")
$import("app.modules.chart.ChartView")

chis.script.gis.powerChartView = function(cfg) {
	this.chartName = cfg.chartName || "Spline"
	this.catelog = "powerChart"
	this.showButtonOnTop = true
	this.entryNameArray = [];
	this.regionDefine = ['center', 'west', 'east', 'south', 'north'];
	chis.script.gis.powerChartView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.gis.powerChartView, app.modules.chart.ChartView, {
	initPanel : function() { // override
		if (this.panel) {
			return this.panel
		}

		if (this.entryName.indexOf(",") > -1) {
			this.entryNameArray = this.entryName.split(",");
		} else {
			this.entryNameArray = [this.entryName];
		}

		var items = [];
		for (var i = 0; i < this.entryNameArray.length; i++) {
			var p = this.createSubPanel(this.entryNameArray[i]);
			p["region"] = this.regionDefine[i];
			items.push(p);
		}

		var cfg = {
			layout : "border",
			width : this.width,
			height : this.height,
			border : false,
			items : items
		}
		if (this.showPrtActionOnBottom) {
			if (this.showCndsBar) {
				cfg.tbar = [];
			}
			cfg.bbar = this.createButtons()
		} else {
			if (this.showCndsBar) {
				cfg.tbar = this.createButtons()
			}
		}
		var panel = new Ext.Panel(cfg)
		if (this.isCombined) {
			panel.on("render", this.onWinShow, this)
		}
		this.panel = panel
		return panel;
	},

	refresh : function() {
		for (var i = 0; i < this.entryNameArray.length; i++) {
			var p = this.panel.findById("panel_" + this.entryNameArray[i])
			if (p) {
				p.body.update(this.getChartHTML(this.entryNameArray[i]))
			}
		}
		// this.panel.body.update(this.initChartHTML())
	},

	createSubPanel : function(entryName) {
		if (!entryName)
			return;
		var cfg = {
			id : "panel_" + entryName,
			layout : "fit",
			width : this.width,
			height : this.height,
			border : false,
			html : this.getChartHTML(entryName)
		}
		var panel = new Ext.Panel(cfg)
		return panel;
	},

	initChartHTML : function() {
		var entryName = this.entryName
		this.getChartHTML(entryName);
	},

	getChartHTML : function(entryName) {
		if (entryName) {
			var ret = util.rmi.miniJsonRequestSync({
						serviceId : "reportSchemaLoader",
						method:"execute",
						schema : entryName
					})
			if (ret.code == 200) {
				this.schema = ret.json.body
				this.title = this.schema.title
			} else {
				// alert(ret.msg)
				return;
			}
		}

		this.chartType = this.schema.chart.chartType || this.chartType

		if (this.chartType.indexOf(".") == -1)
			this.chartType = this.catelog + "." + this.chartType;

		this.template = this.maximum
				? this.schema.chart.template + "_b"
				: this.schema.chart.template;

		if (this.chartUtil) {
			this.chartUtil.un("render", this.onChartRender, this)
			this.chartUtil.un("click", this.onChartClick, this)
		}
		var h = this.height - 10
		var exactFit = null
		if (this.autoResize) {
			h = "100%"
			exactFit = "exactFit"
		}
		var chart = new FusionCharts(this.chartType, this.chartId, "100%", h,
				"0", "1", "FFFFFF", exactFit);
		var url = this.getUrl(entryName)
		chart.setDataURL(url);
		chart.on("render", this.onChartRender, this)
		chart.on("click", this.onChartClick, this)
		var html = chart.getSWFHTML()
		this.chartUtil = chart;
		return html
	},

	getQueryParams : function() {
		if (this.exContext.ids.phrId) {
			this.exQueryParam["phrId"] = this.exContext.ids.phrId;
		}
		if (this.exContext.empiData.sexCode) {
			this.exQueryParam["sexCode"] = this.exContext.empiData.sexCode;
		}
		if (this.exContext.empiData.empiId) {
			this.exQueryParam["empiId"] = this.exContext.empiData.empiId;
		}
		if (this.exContext.args.maxMonth) {
			this.exQueryParam["maxMonth"] = this.exContext.args.maxMonth;
		}
		if (this.exContext.ids["MHC_PregnantRecord.pregnantId"]) {
			this.exQueryParam["pregnantId"] = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
		}

		var q = []
		this.exQueryParam["chartId"] = this.chartId
		this.exQueryParam["template"] = this.template
		for (var name in this.exQueryParam) {
			var v = this.exQueryParam[name]
			if (typeof v == "object") {
				v = v.key
			}
			q.push(name + ";" + encodeURIComponent(v))
		}
		if (this.exQueryParam["phrId"]) {
			delete this.exQueryParam["phrId"];
		}

		if (this.exQueryParam["empiId"]) {
			delete this.exQueryParam["empiId"];
		}

		return q.join("@")
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
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			this.win = win
		}
		win.instance = this;
		return win
	}
})