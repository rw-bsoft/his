$package("phis.application.war.script")

/**
 * 处方组套维护list zhangyq 2012.05.25
 */
$import("phis.script.SimpleList")
$styleSheet("phis.resources.css.app.biz.tplStyle")
phis.application.war.script.WardAdviceClinicSetList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.disableContextMenu = true;
	phis.application.war.script.WardAdviceClinicSetList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.war.script.WardAdviceClinicSetList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.war.script.WardAdviceClinicSetList.superclass.onReady
						.call(this);
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
					url = url
							+ "resources/css/app/desktop/images/icons/AB1.gif";
					var img = "<img src='" + url
							+ "' width='18px' height='15px' />";

					var html = "<table id='mytable' cellspacing='0'><caption> </caption> ";
					html += "<tr><th scope='col'>项目名称</th><th scope='col'>数量</th><th scope='col'>频次</th></tr>";
					html += '<tpl for="body">'
					html += '<tr><td class="row">{XMMC}</td><td class="row">{XMSL}</td><td class="row">{SYPC_text}&nbsp;</td></tr>'
					html += "</tpl></table>";

					// html = "<div style='padding:10px;1px solid
					// #999;color:#555;background:#f9f9f9;max-height:620px;'>"
					// + html + "</div>";
					this.tpl = new Ext.XTemplate(html);
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "simpleQuery",
							schema : "BQ_ZT02_XM",
							cnd : ['eq', ['$', 'a.ZTBH'], ['d', r.get("ZTBH")]],
							pageSize : 0,
							pageNo : 1
						});
				var data = resData.json;
				return this.tpl.apply(data);
			},
		//2013-10-11添加双击事件 modify by gejj
		onDblClick : function() {
			var lastIndex = this.grid.getSelectionModel().lastActive;
			var record = this.grid.store.getAt(lastIndex);
			if (record) {
				this.fireEvent("choose", record);
			}
		}

		})
