$package("phis.application.fsb.script");

$import("phis.script.SimpleModule");

phis.application.fsb.script.FsbNursePlanModule = function(cfg) {
	// this.exContext = {};
	// this.listServiceId = "wardPatientManageService";
	// this.modeName = "dataview";
	cfg.width = 1024;
	cfg.height = 900;
	phis.application.fsb.script.FsbNursePlanModule.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext.extend(phis.application.fsb.script.FsbNursePlanModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				this.zyh = this.data.ZYH;
				this.brbqdm = this.data.BRBQ;
				// this.zyh = '123';
				// this.brbqdm = '312';
				var brInfo = this.getBRInfo();
				var tbar = [];
				this.brInfolable = new Ext.form.Label({
					text : brInfo,
					// html:brInfo,
					style : {
						'height' : '32px',
						'text-align' : 'left',
						'color' : 'blue',
						'overflow' : 'auto',
						'font-size' : '18',
						'padding-left' : '5px',
						'word-spacing' : '8px'
					}
				});
				// tbar.push(this.brInfolable);
				var actions = this.actions;
				var centerTBar = [];
				// 遍历Applications.xml中配置的Tab页，并将其添加到centerTBar中
				if (!this.readOnly) {
					for ( var i = 0; i < actions.length; i++) {
						var ac = actions[i];
						var btn = {};
						btn.id = ac.id;
						btn.text = ac.name, btn.iconCls = ac.iconCls || ac.id;
						// 添加doAction点击事件,调用doAction方法
						btn.handler = this.doAction;
						btn.name = ac.id;
						btn.scope = this;
						centerTBar.push(btn);
					}
				}
				var panel = new Ext.Panel({
					frame : false,
					layout : 'border',
					// tbar : tbar,
					// items : [leftPanel,centerPanel, rightPanel]
					items : [ {
						layout : "fit",
						split : true,
						title : '护理计划',
						collapsible : true,
						region : 'west',
						width : 160,
						items : [ this.getNPTree() ]
					}, {
						layout : "fit",
						split : true,
						width : 510,
						region : 'center',
						tbar : this.createButtons(),
						items : [ this.getNPDataShow() ]
					}, {
						layout : "fit",
						split : true,
						collapsible : true,
						title : '',
						region : 'east',
						width : 260,
						items : [ this.getNPDataView() ]
					} ]
				});
				this.panel = panel;
				return panel;
			},
			/**
			 * 获取病人信息
			 * 
			 * @returns {String}
			 */
			getBRInfo : function() {
				var bq = "";
				if (this.data.BRQK) {
					var dic = util.dictionary.DictionaryLoader.load({
						id : 'phis.dictionary.patientSituation'
					});
					var n = dic.wraper[this.data.BRQK];
					bq = n.text;
				}
				if (!bq) {
					bq = "无";
				}
				if (!this.data.GMYW) {
					this.data.GMYW = "无";
				}

				var brInfo = '家床号码:' + this.data.JCHM + '姓名:' + this.data.BRXM
						+ '  ' + this.data.ZYHM + '  ' + this.data.BRXB_text
						+ '' + this.data.AGE + ' 岁   ' + '  病情: ' + bq
						+ '  过敏药物: ' + this.data.GMYW;
				return brInfo;
			},
			/**
			 * 创建护理记录左边树
			 * 
			 * @returns
			 */
			getNPTree : function() {
				var tree = this.createModule(this.NPTree, this.NPTree);
				tree.on("nodeClick", this.doNodeClick, this);
				return tree.initPanel({
					'zyh' : this.zyh
				});
			},
			/**
			 * 创建护理记录模块右边数据
			 * 
			 * @returns
			 */
			getNPDataView : function() {
				var view = this.createModule(this.NPDataView, this.NPDataView);
				if (this.readOnly) {
					for ( var i = 0; i < view.actions.length; i++) {
						for ( var i = 0; i < view.actions.length; i++) {
							if (!view.actions[i].properties) {
								view.actions[i].properties = {};
							}
							view.actions[i].properties.hide = true;
						}
					}
				}
				view.on("refreshTree", this.refreshTree, this);
				return view.initPanel({
					'zyh' : this.zyh,
					'brbqdm' : this.brbqdm
				});
			},
			/**
			 * 护理记录数据显示
			 * 
			 * @returns
			 */
			getNPDataShow : function() {
				var show = this.createModule(this.NPDataShow, this.NPDataShow);
				return show.initPanel({
					'zyh' : this.zyh
				});
			},
			/**
			 * 选择左边树中的叶子节点后，改变右边护理记录录入
			 * 
			 * @param node
			 */
			doNodeClick : function(node) {
				if (node.attributes.children) {
					this.jlbh = node.attributes['id'];
				} else {
					this.jlbh = node.attributes['id'];
				}
				this.midiModules[this.NPDataView].doNodeClick(node);
			},
			/**
			 * 保存完成刷新树节点与护理记录展示表单
			 */
			refreshTree : function() {
				// 刷新树节点
				this.midiModules[this.NPTree].refreshTree({
					'zyh' : this.zyh
				});
				// 刷新护理展示表单
				this.midiModules[this.NPDataShow].reloadData({
					'zyh' : this.zyh
				});
			},
			doAction : function(item, e) {
				if (item.cmd == "createPlan") {// 新增
					this.midiModules[this.NPDataView].addJl02();
				} else if (item.cmd == "removePlan") {// 删除
					if (!this.jlbh || this.jlbh == '') {
						Ext.Msg.alert('提示', '请先选择需要删除的护理记录!');
					} else {
						var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "fsbNurseRecordService",
							serviceAction : "removeHLJH",
							body : {
								'JLBH' : this.jlbh
							}
						});
						if (result.code == 200) {
							MyMessageTip.msg("提示", "删除成功!", true);
							// Ext.Msg.alert('Status', '删除成功!');
							this.jlbh = '';
							this.midiModules[this.NPDataView].addJl02();
							this.refreshTree();
						} else {
							this.processReturnMsg(result.code, result.msg);
							return;
						}
					}
				} else if (item.cmd == "printPlan") {
					this.doPrint();
				} else if (item.cmd == "close") {
					this.doClose();
				}
			},
			doInitData : function(data) {
				this.data = data;
			},
			/**
			 * 当护理记录关闭后，又选择其它病人后再次打开。需要重置数据
			 * 
			 * @param data
			 */
			doFillIn : function(data) {
				this.data = data;
				this.jlbh = '';
				this.zyh = this.data.ZYH;
				this.brbqdm = this.data.BRBQ;
				this.brInfolable.setText(this.getBRInfo());
				this.refreshTree();
				this.midiModules[this.NPDataView].addJl02();
				this.midiModules[this.NPDataView].changeBrInfo({
					'zyh' : this.zyh,
					'brbqdm' : this.brbqdm
				});
				this.midiModules[this.NPDataShow].initPanel({
					'zyh' : this.zyh
				});
			},
			doPrint : function() {
				if (this.zyh == null) {
					MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
					return;
				}
				var module = this.createModule("fsbNursePlanDataShowprint",
						"phis.application.fsb.FSB/FSB/FSB130604")
				module.ZYH = this.zyh;
				module.initPanel();
				module.doPrint();
			},
			doClose : function() {
				this.win.hide();
			}
		});