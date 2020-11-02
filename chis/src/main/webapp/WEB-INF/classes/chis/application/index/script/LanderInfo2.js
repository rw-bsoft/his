$package("chis.application.index.script");

$import("app.desktop.Module");
$styleSheet("chis.resources.app.desktop.ExtendMain");

chis.application.index.script.LanderInfo2 = function(cfg) {
	
	chis.application.index.script.LanderInfo2.superclass.constructor.apply(this, [cfg]);

};

Ext.extend(chis.application.index.script.LanderInfo2, app.desktop.Module, {
	initPanel : function(sc) {
		if (this.panel) {
			return this.panel;
		}
		this.flag = true;
		this.data = this.getLanderInfo();
		var tpl = this.getTopTemplate();

		var panel = new Ext.Panel({
					border : false,
					frame : false,
					collapsible : false,
					width : 260,
					height : 130,
					layout : 'fit',
					region : 'north',
					html : tpl.apply(this.data)
				});

		this.panel = panel;
		return panel;
	},

	getTopTemplate : function() {
		// if (this.tpl) {
		// return this.tpl;
		// }
		var tpl = new Ext.XTemplate('<div class="box">'
						+ '<ul class="message">'
						+ '<li>用户姓名：{userName}</li>'
						+ '<li>所属机构：{userOrg}</li>'
						+ '<li>岗&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;位：{userJob}</li>'
						+ '<li>登录时间：{loadTime}</li>'
						+ '<li>在线时间：<span id="olt">{onLineTime}</span></li>'
						+ '</ul>' + '</div>');
		// this.tpl = tpl;
		return tpl;
	},

	loadData : function() {
		if(this.panel.body){
			if(this.flag == true){
				this.flag = false;
				return;
			}
			this.data = this.getLanderInfo();
			var tpl = this.getTopTemplate();
			this.panel.body.update(tpl.apply(this.data));
		}
	},

	getLanderInfo : function() {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.myPageService",
					serviceAction : "getLanderInfo",
					method:"execute"
				});
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		return result.json.body;
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
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						items : this.initPanel()
					});
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			win.on("add", function() {
						this.win.doLayout();
					}, this);
			this.win = win;
		}
		win.instance = this;
		return win;
	}
});