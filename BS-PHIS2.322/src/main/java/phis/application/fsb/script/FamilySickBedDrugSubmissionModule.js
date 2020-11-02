$package("phis.application.fsb.script");

$import("phis.script.SimpleModule");

phis.application.fsb.script.FamilySickBedDrugSubmissionModule = function(cfg) {
	cfg.yzlx = 0;
	phis.application.fsb.script.FamilySickBedDrugSubmissionModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedDrugSubmissionModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'west',
										width : 230,
										items : this.getLeftList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getRightList()
									}],
							tbar : this.getTbar()
						});
				this.panel = panel;
				return panel;
			},
			getLeftList : function() {
				this.leftList = this.createModule("leftList", this.leftRef);
				this.leftList.on("selectRecord", this.onSelectRecord, this);
				return this.leftList.initPanel();
			},
			getRightList : function() {
				this.rightList = this.createModule("rightList", this.rightRef);
				this.rightList.on("loadData",this.onRightLoadData,this)
				return this.rightList.initPanel();
			},
			getTbar : function() {
				var label = new Ext.form.Label({
							"text" : "发药药房:"
						})
				this.yf = this.createDicField({
							"width" : 100,
							"id" : "phis.dictionary.pharmacy_jcfy",
							// "filter" : [ 'eq', [ '$', 'item.properties.JGID'
							// ],
							// [ 's', '%user.manageUnit.id' ] ],
							"emptyText" : "全部药房"
						})
				this.radio1 = new Ext.form.Radio({
							xtype : "radio",
							checked : true,
							boxLabel : '全部',
							inputValue : 0,
							name : "yzlxfy",
							id : "qb",
							clearCls : true
						});
				this.radio1.on('check', this.onChange, this);
				this.radio2 = new Ext.form.Radio({
							xtype : "radio",
							id : "ls",
							boxLabel : '临时',
							inputValue : 1,
							name : "yzlxfy",
							clearCls : true
						});
				this.radio2.on('check', this.onChange, this);
				this.radio3 = new Ext.form.Radio({
							xtype : "radio",
							id : "cq",
							boxLabel : '长期',
							inputValue : 2,
							name : "yzlxfy",
							clearCls : true
						});
				this.radio3.on('check', this.onChange, this);
				return [label, this.yf, this.radio1, this.radio2, this.radio3,
						this.createButtons()]
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
			// 刷新
			doRefresh : function() {
				var body = {};
				var yfsb = this.yf.getValue();
				if (yfsb && yfsb != null && yfsb != "") {
					body["YFSB"] = yfsb;
				}
				var yzlx = this.yzlx;
				if (yzlx != 0) {
					body["YZLX"] = yzlx;
				}
				if (this.ZYH) {
					body["ZYH"] = this.ZYH;
				}
				this.leftList.requestData.body = body;
				this.leftList.loadData();
			},
			// 全部
			onChange : function() {
				if (this.radio1.getValue() == true) {
					this.yzlx = 0;
				} else if (this.radio2.getValue() == true) {
					this.yzlx = 1;
				} else if (this.radio3.getValue() == true) {
					this.yzlx = 2;
				}
				this.doRefresh();
			},
			// 左边病人选中
			onSelectRecord : function() {
				var records = this.leftList.getSelectedRecords();
				this.rightList.clear();
				var length = records.length;
				if (length == 0) {
					return;
				}
				var zyhs = new Array();
				for (var i = 0; i < length; i++) {
					var r = records[i];
					zyhs.push(r.get("ZYH"));
				}
				var cnd = [
						'and',
						['in', ['$', 'a.ZYH'], zyhs],
						['or', ['eq', ['$', 'a.TJZX'], ['i', 0]],
								['isNull', ['$', 'a.TJZX']]],
						['ge', ['$', 'a.YPLX'], ['i', 1]],
						['ne', ['$', 'a.ZFYP'], ['i', 1]],
						['eq', ['$', 'a.LSBZ'], ['i', 0]],
						['or', ['eq', ['$', 'a.YSBZ'], ['i', 0]],
						['and',['eq', ['$', 'a.YSBZ'], ['i', 1]],['eq', ['$', 'a.YSTJ'],['i', 1]]]]];
//				var yfsb = this.yf.getValue();
//				if (yfsb && yfsb != null && yfsb != "") {
//					cnd = ['and', cnd, ['eq', ['$', 'a.YFSB'], ['l', yfsb]]]
//				}
				var yzlx = this.yzlx;
				if (yzlx != 0) {
					if (yzlx == 1) {
						cnd = ['and', cnd, ['eq', ['$', 'a.LSYZ'], ['i', 1]]]
					} else {
						cnd = ['and', cnd, ['eq', ['$', 'a.LSYZ'], ['i', 0]]]
					}
				}
				this.panel.el.mask("正在加载","x-mask-loading")
				this.rightList.initCnd = cnd;
				this.rightList.requestData.cnd = cnd;
				this.rightList.loadData();
			},
			doZx : function() {
				var records = this.rightList.getSelectedRecords();
				if (!records || records.length == 0) {
					MyMessageTip.msg("提示", "请先选择执行数据", false);
					return;
				}
				var body = [];
				var l = records.length;
				for (var i = 0; i < l; i++) {
//					if(records[i].data.YSBZ==1&&records[i].data.YSTJ==0){
//						var store = this.rightList.grid.getStore();
//						var n = store.getCount()
//						for (var j = 0; j < n; j++) {
//							var r = store.getAt(j)
//							if(r.data.JLXH==records[i].data.JLXH){
//								MyMessageTip.msg("提示","第"+(j+1)+ "行医嘱，医生未提交不能执行", false);
//								return
//							}
//						}
//					}else{
//						body.push(records[i].data);
//					}
					body.push(records[i].data);
				}
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body
						});
				this.panel.el.unmask();
				if (r.code > 300) {
					if (r.code == 509) {
						this.fireEvent("save", this)
					}
					this.processReturnMsg(r.code, r.msg, this.doSave);
					return;
				}
				MyMessageTip.msg("提示", "保存成功!", true);
				this.fireEvent("save", this)
				this.doRefresh();
			},
			// 重写为了关闭按钮在开医嘱那显示 非开医嘱那不显示
			createButtons : function(level) {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				if (this.butRule) {
					var ac = util.Accredit
					if (ac.canCreate(this.butRule)) {
						this.actions.unshift({
									id : "create",
									name : "新建"
								})
					}
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (!this.ZYH && action.id == "cancel") {
						continue
					}
					if (action.properties && action.properties.hide) {
						continue
					}
					level = level || 'one';
					if (action.properties) {
						action.properties.level = action.properties.level
								|| 'one';
					} else {
						action.properties = {};
						action.properties.level = 'one';
					}
					if (action.properties && action.properties.level != level) {
						continue;
					}
					// ** add by yzh **
					var btnFlag;
					if (action.notReadOnly)
						btnFlag = false
					else
						btnFlag = (this.exContext && this.exContext.readOnly) || false

					if (action.properties && action.properties.scale) {
						action.scale = action.properties.scale
					}
					var btn = {
						accessKey : f1 + i,
						text : action.name
								+ (this.noDefaultBtnKey ? "" : "(F" + (i + 1)
										+ ")"),
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle == "true"),
						scale : action.scale || "small",
						// ** add by yzh **
						disabled : btnFlag,
						notReadOnly : action.notReadOnly,

						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			},
			doCancel : function() {
				this.fireEvent("cancel", this);
			},
			onRightLoadData:function(){
			this.panel.el.unmask();
			},
			doPrint : function() {
				var records = this.rightList.getSelectedRecords();
				var length = records.length;
				if (length == 0) {
					return;
				}
				var jlxhs = new Array();
				for (var i = 0; i < length; i++) {
					var r = records[i];
					jlxhs.push(r.get("JLXH"));
				}
				var pages = "phis.prints.jrxml.JCDoctorAdviceSubmit";
				var url = "resources/" + pages + ".print?silentPrint=1&jlxhs="
						+ jlxhs;
				if (url.length > 3000) {
					MyMessageTip.msg("提示", "提交数据过多,会导致打印失败,请分次提交!", true);
					return false;
				}
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
			}
		});