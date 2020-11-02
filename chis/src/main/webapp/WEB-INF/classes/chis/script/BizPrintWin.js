/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")

$import("util.dictionary.TreeCheckDicFactory")

chis.script.BizPrintWin = function(cfg) {
	chis.script.BizPrintWin.superclass.constructor.apply(this, [ cfg ])
	this.on("winShow",this.onWinShow,this)
}

Ext.extend(chis.script.BizPrintWin, app.desktop.Module, {

	print : function() {
		var w = this.printPreview({
					iconCls : "print"
				})
		if (Ext.isIE) {
			w.print()
		} else {
			w.onload = function() {
				w.print()
			}
		}
	},
	getSelectedPage : function() {
		var nodes = this.win.items.itemAt(0).getChecked()
		var pages = []
		for (var i = 0; i < nodes.length; i++) {
			var n = nodes[i]
			pages.push(n.attributes.print)
		}
		return pages
	},
	printPreview : function(btn) {
		var type = 0
		if (btn && btn.iconCls) {
			if (btn.iconCls == "print") {
				type = 1
			} else if (btn.iconCls == "doc") {
				type = 2
			} else if (btn.iconCls == "excel") {
				type = 3
			}
		}
		var pages = this.getSelectedPage()
		if(pages.length == 0){
			Ext.Msg.alert("提示", "请选择需要打印的表单!");
			return;
		}
		var ids_str = ""
		for (var key in this.ids) {
			ids_str += ("&" + key + "=" + this.ids[key])
		}
		var url = "resources/" + pages + ".print?type=" + type + ids_str
//		var url = "form.print?type=" + type + ids_str + "&pages=" + pages
		var printWin = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		return printWin
	},
	getWin : function() {
		var win = this.win
		if (!win) {
			var ehrNavTree = util.dictionary.TreeCheckDicFactory.createTree({
						id : "chis.dictionary.ehrViewPrint",
						onlyLeafCheckable : true
					})
			this.ehrNavTree = ehrNavTree
			var win = new Ext.Window({
						title : "选择需要打印的档案",
//						layout : "fit",
						width : 360,
						height : 360,
						autoScroll:true,
						constrain : true,
						closeAction : "hide",
						bodyStyle:"background-color:#ffffff;",
						modal : true,
						items : ehrNavTree,
						tbar : [{
									text : "打印",
									iconCls : "print",
									handler : this.print,
									scope : this
								}, {
									text : "打印预览",
									iconCls : "print",
									handler : this.printPreview,
									scope : this
								}, {
									text : "WORD",
									iconCls : "doc",
									handler : this.printPreview,
									scope : this
								}, {
									text : "PDF",
									iconCls : "pdf",
									handler : this.printPreview,
									scope : this
								}, {
									text : "EXCEL",
									iconCls : "excel",
									handler : this.printPreview,
									scope : this
								}]
					})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("add", function() {
						this.win.doLayout()
					}, this)
			 win.on("show",function(){
		    	this.fireEvent("winShow")
		    },this)
		     win.on("hide",function(){
		    	this.fireEvent("hide")
		    },this)
			this.win = win
		}
		win.instance = this;
		return win
	}
	,
	onWinShow:function(){
		var nodes = this.ehrNavTree.getChecked()
		for (var i = 0; i < nodes.length; i++) {
			nodes[i].getUI().check(false);
		}
	}

})