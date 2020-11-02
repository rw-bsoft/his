$package("phis.application.cic.script")

$import("phis.script.TabModule")

phis.application.cic.script.UserDataTemporaryTabModule = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.cic.script.UserDataTemporaryTabModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.cic.script.UserDataTemporaryTabModule,
		phis.script.TabModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = this.getTabItems();
				var buttons = this.createTabButtons();
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							defaults : {
								border : false
							},
							autoScroll : true,
							tbar : buttons,
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.onBeforeTabChange, this);
				tab.activate(this.activateId);
				this.tab = tab
				return tab;
			},
			getTabItems : function() {
				var tabItems = [];
				for (var i = 0; i < 8; i++) {
					var title;
					switch (i) {
						case 0 :
							title = "全部";
							break;
						case 1 :
							title = "标点";
							break;
						case 2 :
							title = "单位";
							break;
						case 3 :
							title = "数学";
							break;
						case 4 :
							title = "数字";
							break;
						case 5 :
							title = "特殊";
							break;
						case 6 :
							title = "自定义";
							break;
						case 7 :
							title = "其他";
							break;
					}
					tabItems.push({
								frame : this.frame,
								layout : "fit",
								title : title,
								exCfg : [],
								id : "temp" + i,
								html : ""
							})
				}
				return tabItems;
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				phis.application.cic.script.UserDataTemporaryTabModule.superclass.onTabChange
						.call(this, tabPanel, newTab, curTab);
				var cnd;
				if (newTab.html) {
					return;
				}
				if (newTab.id == "temp1") {
					cnd = ["eq", ["$", "XMMC"], ["s", "标点符号"]];
				} else if (newTab.id == "temp2") {
					cnd = ["eq", ["$", "XMMC"], ["s", "单位符号"]];
				} else if (newTab.id == "temp3") {
					cnd = ["eq", ["$", "XMMC"], ["s", "数学符号"]];
				} else if (newTab.id == "temp4") {
					cnd = ["eq", ["$", "XMMC"], ["s", "数字序号"]];
				} else if (newTab.id == "temp5") {
					cnd = ["eq", ["$", "XMMC"], ["s", "特殊符号"]];
				} else if (newTab.id == "temp6") {
					cnd = ["eq", ["$", "XMMC"], ["s", "自定义"]];
				} else if (newTab.id == "temp7") {
					cnd = ["eq", ["$", "XMMC"], ["s", "其他"]];
				} else {
					cnd = null;
				}
				var f = newTab.id.substring(4, 5);
				var htmlData = this.getTemporaryHtml(f, cnd);
				Ext.getCmp(newTab.id).body.update(htmlData);
				this.onTabRender(f);
			},
			onTabRender : function(f) {
				if (typeof f == "string") {
					for (var i = 0; i < this.length; i++) {
						if (Ext.get("tem" + f + i)) {
							Ext.get("tem" + f + i).on("click",
									this.changeHtmlClore, this);
							Ext.get("tem" + f + i).on("dblclick",
									this.onHtmlDbClick, this);
						}
					}
				}
			},
			getTemporaryHtml : function(f, cnd) {
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "userDataBoxService",
							serviceAction : "getTemporaryData",
							cnd : cnd
						});
				var data = resData.json.body;
				var html = '';
				html += '<table border="2" cellpading="0" cellspacing="0">';
				html += '<tr>';
				this.length = data.length;
				for (var i = 0; i < data.length; i++) {
					if (i % 17 == 0 && i != 0) {
						html += '</tr>';
						html += '<tr>';
					}
					html += '<td align="center" width="50" height="25">';
					html += '<div id="tem' + f + i
							+ '" style="font-size:12px">';
					html += data[i].XMQZ;
					html += '</div>';
					html += '</td>';
				}
				html += '</tr>';
				html += '</table>';
				return html;
			},
			changeHtmlClore : function(info, div) {
				Ext.get(div.id).setStyle({
							color : 'red'
						});
				this.divValue = div.innerHTML;
				if (this.oldDivId && this.oldDivId != div.id) {
					Ext.get(this.oldDivId).setStyle({
								color : 'Black'
							});
				}
				this.oldDivId = div.id;
			},
			createTabButtons : function(level) {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}
					var btn = {
						accessKey : f1 + i,
						text : action.name + "(F" + (i + 1) + ")",
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle == "true"),
						scale : action.scale || "small",
						// ** add by yzh **
						disabled : false,
						notReadOnly : action.notReadOnly,

						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			},
			doAppoint : function() {
				if(!this.divValue){
					return;
				}
				var data=this.divValue;
				if(this.divValue=="用户"){
					data=this.mainApp.uname;
				}
				if(this.divValue=="日期1"){
					data=new Date().format("Y-m-d h:m:s");
				}
				if(this.divValue=="日期2"){
					data=new Date().format("Y年m月d日 h:m");
				}
				if(this.divValue=="日期3"){
					data=new Date().format("Y-m-d h:m");
				}
				if(this.divValue=="日期4"){
					data=new Date().format("Y-m-d");
				}
				this.fireEvent("appoint", data,1);
			},
			onHtmlDbClick:function(){
				this.doAppoint();
			},
			doCancel : function() {
				this.fireEvent("cancel");
			}

		});
