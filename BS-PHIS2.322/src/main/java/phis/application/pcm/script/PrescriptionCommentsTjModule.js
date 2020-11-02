/**
 * 处方点评报表
 * 
 * @author caijy
 */
$package("phis.application.pcm.script");

$import("phis.script.SimpleModule");

phis.application.pcm.script.PrescriptionCommentsTjModule = function(cfg) {
	cfg.isNewOpen = 1;
	phis.application.pcm.script.PrescriptionCommentsTjModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsTjModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : this.getTbar(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'north',
										height : 200,
										collapsible : true,
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "",
										region : 'center',
										items : this.getPrint()
									}]
						});
				this.panel = panel;
				return panel;
			},
			// 获取上面的list
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("txbbData", this.onTxbbData, this);
				return this.list.initPanel();
			},
			// 获取下面的图形报表
			getPrint : function() {
				this.printModule = this.createModule("printModule",
						this.refPrint);
				this.printModule.opener = this;
				return this.printModule.initPanel();
			},
			getTbar : function() {
				this.datefrom = new Ext.ux.form.Spinner({
							fieldLabel : 'datefrom',
							name : 'storeDate',
							width : 120,
							value : new Date().format('Y-m-d'),
							strategy : {
								xtype : "date"
							}
						})
				this.dateto = new Ext.ux.form.Spinner({
							fieldLabel : 'dateto',
							name : 'storeDate',
							width : 120,
							value : new Date().format('Y-m-d'),
							strategy : {
								xtype : "date"
							}
						})
				this.dplx = this.getSelectBox();
				var tbar = [this.datefrom, '-', this.dateto, '-', this.dplx]
				return tbar.concat(this.createButtons())
			},
			// 生成条件下拉框
			getSelectBox : function() {
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == "DPLX") {
						it = items[i]
						break
					}
				}
				it.dic.src = this.entryName + "." + it.id;
				it.dic.defaultValue = it.defaultValue;
				it.dic.width = 100;
				f = this.createDicField(it.dic);
				f.on("select", this.onSelect, this);
				return f;
			},
			createDicField : function(dic) {
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory"

				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				return field
			},
			// 刷新数据
			doRefresh : function() {
				var datef = this.datefrom.getValue();
				var datet = this.dateto.getValue();
				var dplx = this.dplx.getValue();
				this.list.requestData.body = {
					"DPLX" : dplx,
					"DPRQKS" : datef,
					"DPRQJS" : datet
				}
				this.list.loadData();
			},
			// 加载图形报表
			onTxbbData : function(data) {
				this.printModule.data = data;
				this.printModule.loadDataf();
			}
		});