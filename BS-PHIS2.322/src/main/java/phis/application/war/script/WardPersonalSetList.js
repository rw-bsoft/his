$package("phis.application.war.script")

/**
 * 处方组套维护list zhangyq 2012.05.25
 */
$import("phis.script.SimpleList")

$styleSheet("phis.resources.css.app.biz.tplStyle")

phis.application.war.script.WardPersonalSetList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.group = "ZTLB";
	cfg.disableContextMenu = true;
	cfg.autoLoadData = false;
	phis.application.war.script.WardPersonalSetList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.war.script.WardPersonalSetList, phis.script.SimpleList, {
	onRenderer : function(value, metaData, r) {
		var SFQY = r.get("SFQY");
		var src = (SFQY == 1) ? "yes" : "no";
		return "<img src='" + ClassLoader.appRootOffsetPath
				+ "resources/phis/resources/images/" + src + ".png'/>";
	},
	ZtlbRender : function(value, params, r, row, col, store) {
		switch (value) {
			case "1" :
				return "西药";
			case "2" :
				return "中药";
			case "3" :
				return "草药";
			case "4" :
				return "项目";
			case "5" :
				return "文字医嘱";
		}
	},
	onReady : function() {
		phis.application.war.script.WardPersonalSetList.superclass.onReady.call(this);
		this.grid.un('rowcontextmenu', this.onContextMenu, this);
		this.grid.on('rowcontextmenu', function(grid, rowIndex, e) {
					e.preventDefault();
					grid.getSelectionModel().selectRow(rowIndex);
					var tip = new Ext.ToolTip({
								html : this.getHTML(rowIndex),
								dismissDelay : 0,
								width : 500,
								autoScroll : true,
								style : "background:#f9f9f9;"
							});
					tip.showAt(0, 0)
				}, this);
	},
	getHTML : function(rowIndex) {
		var r = this.grid.getStore().getAt(rowIndex);
		if (!this.tpl) {
			var url = document.URL;
			url = url + "resources/css/app/desktop/images/icons/AB1.gif";
			var img = "<img src='" + url + "' width='18px' height='15px' />";

			var html = "<table id='mytable' cellspacing='0'><caption> </caption> ";
			html += "<tr><th scope='col'>药品名称</th><th scope='col'>数量</th><th scope='col'>剂量</th><th scope='col'>用法</th><th scope='col'>频次</th></tr>";
			html += '<tpl for="body">'
			html += '<tr><td class="row">{XMMC}</td><td class="row">{XMSL}</td><td class="row">{YCJL}<tpl if="JLDW">{JLDW}</tpl></td><td class="row">{GYTJ_text}</td><td class="row">{SYPC_text}</td></tr>'
			html += "</tpl></table>";

			// html = "<div style='padding:10px;1px solid
			// #999;color:#555;background:#f9f9f9;max-height:620px;'>"
			// + html + "</div>";
			this.tpl = new Ext.XTemplate(html);
		}
		var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "simpleQuery",
					schema : "YS_MZ_ZT02_BQ",
					cnd : ['eq', ['$', 'a.ZTBH'], ['d', r.get("ZTBH")]],
					pageSize : 0,
					pageNo : 1
				});
		var data = resData.json;
		return this.tpl.apply(data);
	},
	onDblClick : function() {
		var lastIndex = this.grid.getSelectionModel().lastActive;
		var record = this.grid.store.getAt(lastIndex);
		if (record) {
			this.fireEvent("choose", record);
		}
	}
})
