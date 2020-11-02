/**
 * 家族谱页面
 * 
 * @author tianj
 */
$package("chis.script")

$import("app.modules.chart.ChartView")

chis.script.demographicView = function(cfg) {
	cfg.width = 600;
	cfg.height = 400;
	cfg.entryName = "DemoGraphic";
	cfg.serviceId = "chis.demoGraphicService";
	cfg.chartName = "DragNode";
	cfg.catelog = "powerChart";
	cfg.showButtonOnTop = true;
	cfg.exContext = cfg.exContext || {}
	cfg.exContext.ids = {
		birthday : cfg.birthday || cfg.exContext.ids.birthday,
		empiId : cfg.empiId || cfg.exContext.ids.empiId
	};
	chis.script.demographicView.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.script.demographicView, app.modules.chart.ChartView, {
			initChartHTML : function() {
				if (this.chartUtil) {
					this.chartUtil.un("click", this.onDemoGraphicClick, this);
				}
				var h = this.height - 10;
				if (this.autoResize) {
					h = "100%";
				}
				var chart = new FusionCharts(this.catelog + "."
								+ this.chartName, this.chartId, '100%', h, "0",
						"1", "FFFFFF");

				chart.on("click", this.onDemoGraphicClick, this);

				var url = this.getUrl();
				chart.setDataURL(url);

				var html = chart.getSWFHTML();
				this.chartUtil = chart;
				return html;
			},

			getResourcePath : function() {
				var catalog = 'gis';
				var offsetPath = ClassLoader.appRootOffsetPath;
				return offsetPath + "resources/chis/resources/" + catalog + "/";
			},

			getUrl : function() {
				var url = window.location.href;
				if (this.localUrl) {
					url = this.localUrl;
				}else {
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.publicService",
								serviceAction : "getServiceRpcUrl",
								method : "execute"
							});
					this.localUrl = result.json.localUrl;
					url = this.localUrl;
				}
				//为解决跨域问题
				var locationHost = window.location.host;
				var host = locationHost.substring(0,locationHost.indexOf(":"))
				if(host == "localhost"){
					var urlParseObj = this.parseUrl(url);
					url = urlParseObj.protocol+"//"+host+":"+urlParseObj.port+urlParseObj.pathname;
				}
				var n = url.lastIndexOf("/");
				url = url.substring(0, n) + "/";
				url += this.entryName + ".graphic?q=" + this.getQueryParams();
				return url;
			},

			reloadUrl : function() {
				var url = this.getUrl();
				this.chartUtil.setDataURL(url);
			},

			onDemoGraphicClick : function() {
				if (arguments[0] == "") {
					return;
				}
				var empiId = arguments[0];
				if (this.exContext.ids.empiId != empiId) {
					// 让EHRView自动刷新
					this.fireEvent("refreshData", "all");
					this.exContext.ids.empiId = empiId;
				}
				var url = this.getUrl();
				this.chartUtil.setDataURL(url);
				if (this.ehrview) {
					this.ehrview.actionName = "EHR_HealthRecord";
					this.ehrview.exContext.ids["empiId"] = empiId;
					this.ehrview.refresh();
				} else {
					this.list.onSelectEMPI({
								empiId : empiId,
								birthday : this.exContext.ids.birthday
							});
				}
			},

			getQueryParams : function() {
				var q = [];
				this.exQueryParam["chartId"] = this.chartId;
				this.exQueryParam["path"] = this.getResourcePath();
				this.exQueryParam["empiId"] = this.exContext.ids.empiId;
				for (var name in this.exQueryParam) {
					var v = this.exQueryParam[name];
					if (typeof v == "object") {
						v = v.key;
					}
					q.push(name + ";" + encodeURIComponent(v));
				}
				return q.join("@");
			},

			getWin : function() {
				var win = this.win;
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
						constrain : true,
						constrainHeader : true,
						minimizable : true,
						maximizable : true,
						shadow : false
					};
					if (!this.mainApp) {
						cfg.closeAction = 'hide';
					}
					win = new Ext.Window(cfg);
					win.on("hide", function() {
								this.close();
							}, this)
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				win.instance = this;
				return win;
			},

			close : function() {
				this.exContext.ids = {};
			},
			parseUrl:function(url) {
				var r = {
				    protocol: /([^\/]+:)\/\/(.*)/i,
				    host: /(^[^\:\/]+)((?:\/|:|$)?.*)/,
				    port: /\:?([^\/]*)(\/?.*)/,
				    pathname: /([^\?#]+)(\??[^#]*)(#?.*)/
				};
			    var tmp, res = {};
			    res["href"] = url;
			    for (p in r) {
			        tmp = r[p].exec(url);
			        res[p] = tmp[1];
			        url = tmp[2];
			        if (url === "") {
			            url = "/";
			        }
			        if (p === "pathname") {
			            res["pathname"] = tmp[1];
			            res["search"] = tmp[2];
			            res["hash"] = tmp[3];
			        }
			    }
			   // console.log(url);
			    return res;
			}
		})