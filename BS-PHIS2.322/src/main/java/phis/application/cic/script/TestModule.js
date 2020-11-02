$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.TestModule = function(cfg) {
	phis.application.cic.script.TestModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.cic.script.TestModule, phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					defaults : {
						border : false
					},
					buttons : this.createButtons()
						// height : 637,
					});
				this.panel = panel;
				return panel;
			},
			getHtml : function() {
				if (!this.tpl) {
					var tpl = new Ext.XTemplate(
							'<table id="table1"  width="743" height="284" border="1">',
							'<tr><td colspan="8">测试网页table</td> </tr>',
							'<tr><td>第1列-{name:ellipsis(8)}</td><td>第2列-{title}</td></tr>',
							'<tpl for="cfList">',
							'<tr><td>{nr1:nl2br}</td><td>{nr2}</td></tr>', '</tpl>',
							'</table>')
					this.tpl = tpl;
				}
				Ext.util.Format
				return this.tpl;
			},
			doTest4 : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "loadSystemParams",
							body : {
								privates : ['YXWGHBRJZ',"QYMZDZBL"]
							}
						});
				if (res && res.json.body) {
					if (res.json.body.YXWGHBRJZ != "1") {
						// var newBtn = this.grid.getTopToolbar().items.item(7);
						var newBtn = this.grid.getTopToolbar().find('cmd',
								'save')[0].disable();
						newBtn.hide();
					}
				}
				this.sysParams = res.json.body;
			},
			doTest1 : function() {
				var data1 = {
					name : 'Jack Slocum',
					title : 'Lead Developer',
					cfList : [{
								nr1 : null,
								nr2 : 3
							}, {
								nr1 : 'Zachary',
								nr2 : 2
							}, {
								nr1 : 'John James',
								nr2 : 0
							}]
				};
				this.getHtml().overwrite(this.panel.body, data1);

			},
			doTest3 : function() {
				var data1 = {
					name : 'Jack Slocum',
					title : 'Lead Developer',
					cfList : []
				};
				this.getHtml().overwrite(this.panel.body, data1);

			},
			doTest2 : function() {

				var data2 = {
					name : 'BB Black',
					title : '测试2的',
					cfList : [{
								nr1 : '斯蒂芬',
								nr2 : 3
							}, {
								nr1 : '鬼地方',
								nr2 : 2
							}, {
								nr1 : '111 123',
								nr2 : 0
							}]
				};
				this.getHtml().overwrite(this.panel.body, data2);
				var trs = document.getElementById('table1')
						.getElementsByTagName('tr');

				for (var i = 0; i < trs.length; i++) {
					trs[i].onmousedown = function() {
						tronmousedown(this);
					}
					trs[i].onclick = function() {
						tronclick(this);
					}
				}
				function tronmousedown(obj) {
					for (var o = 0; o < trs.length; o++) {
						if (trs[o] == obj) {
							trs[o].style.backgroundColor = '#DFEBF2';
						} else {
							trs[o].style.backgroundColor = '';
						}
					}
				}
				function tronclick(obj) {
//					for(var prop in obj) {
//						alert(prop)
//					}
					alert(obj.cells[0].innerHTML)
				}

			}

		});