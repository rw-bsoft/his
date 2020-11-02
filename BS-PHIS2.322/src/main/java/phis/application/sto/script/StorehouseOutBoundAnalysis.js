/**
 * 采购分析
 * @author AD
 */
$package("phis.application.sto.script");

$import("app.desktop.Module",
		"org.ext.ux.TabCloseMenu",
		"app.modules.common",
		"util.dictionary.TreeDicFactory",
		"util.dictionary.DictionaryLoader",
		"phis.script.SimpleModule");
$import("phis.script.widgets.Spinner","phis.script.FilterList","phis.script.ux.IFrame");

phis.application.sto.script.StorehouseOutBoundAnalysis = function(cfg) {
	cfg.autoLoadData = false;
	cfg.summaryable = true;
	cfg.disablePagingTbr = true;
	cfg.rootVisible = true;// 设置根节点
	cfg.parentText = "全部药品";// 根节点名称
	phis.application.sto.script.StorehouseOutBoundAnalysis.superclass.constructor.apply(
			this, [cfg]);
};
Ext.extend(phis.application.sto.script.StorehouseOutBoundAnalysis, phis.script.FilterList, {
	initPanel:function(sc){
		var grid = this.getCountList(sc);
		var report = this.getReport();
		var tree = this.getTree();
		if(!grid){
		   return
		}
//		var tree = util.dictionary.TreeDicFactory.createTree({id:this.dicId,parentKey:this.mainApp.deptRef,rootVisible:true});
//		tree.autoScroll = true;
//		tree.on("click",this.onTreeClick,this);
//		var actions = this.actions;
//		this.tree = tree;
//		this.setTreeRoot();
		var panel = new Ext.Panel({
		    height : this.height,
			layout : "border",
			tbar: this.getTbar(),
			items:[
			   { layout:'fit',
			     region:'west',
			     width : 180,
			     items:[tree]
			   },{
			     region:'center',
			     layout:'anchor',
			     defaults:{
			    	 anchor: '100% 50%'
			     },
			     items:[grid,report]
			   }
			]
		});
		this.panel = panel;
		return this.panel;

	},
	loadData: function() {
		if(!this.queryData){
			return;
		}
		this.requestData.body =  this.queryData;
		phis.application.sto.script.StorehouseOutBoundAnalysis.superclass.loadData.call(this);
	},
	loadReport: function() {
		if(!this.report || !this.queryData) {
			return ;
		}
		this.report.setSrc(this.createUrl("1"));
	},
	getCountList: function(sc) {
		var grid = phis.application.sto.script.StorehouseOutBoundAnalysis.superclass.initPanel.call(this,sc);
		this.requestData.serviceId=this.serviceId;
		this.requestData.serviceAction=this.serviceAction;
		return grid;
	},
	getReport: function() {
		var pages="phis.prints.jrxml.ReportForOutBoundAnalysis";
		var url="resources/"+pages+".print?type=1";
		var report = new Ext.ux.ManagedIframePanel({
			autoCreate: true,
			defaultSrc:url
		});
		this.report = report;
		return report;
	},
	getTree: function() {
		var navDic = this.navDic;
		var tf = util.dictionary.TreeDicFactory.createDic({
			dropConfig : {
				ddGroup : 'gridDDGroup',
				notifyDrop : this.onTreeNotifyDrop,
				scope : this
			},
			id : navDic,
			parentText : this.parentText,
			parentKey : "",
			rootVisible : this.rootVisible || false
		});
		this.tree = tf.tree;
		tf.tree.on("click", this.onTreeNodeClick, this);
		return tf.tree;
	},
	onTreeNodeClick : function(node, e) {
//		this.tree.getSelectionModel().selNode.attributes.key
		//this.onCountClick();
	},
	getTbar: function() {
		var dateStart = new Ext.form.DateField({
			//id: 'datestart',
			format:'Y-m-d',
			name: 'dateStart',
			value: mainApp.serverDate
		});
		this.dateStart=dateStart;
		var dateEnd = new Ext.form.DateField({
			//id: 'dateend',
			format:'Y-m-d',
			name: 'dateEnd',
			value: mainApp.serverDate
		});
		this.dateEnd=dateEnd;
		 var store = new Ext.data.JsonStore({
			 	fields : ['value', 'text'],
				data : [{
					value:'JHJE',text:'进货总额'
				},{
					value:'JXCE',text:'进销差额'
				},{
					value:'KL',text:'扣率'
				},{
					value:'LSJE',text:'零售总额'
				},{
					value:'CKSL',text:'出库数量'
				},{
					value:'YPMC',text:'药品名称'
				}]
		    });

		 var countType = new Ext.form.ComboBox({
			 	//id: 'sorttype',
				store : store,
				valueField : "value",
				displayField : "text",
				value : 'JHJE',
				mode : 'local',
				triggerAction : 'all',
				emptyText : '选择查询字段',
				selectOnFocus : true,
				width : 100
			});
		countType.on("select", this.onCountTypeChange, this);
		this.countType=countType
		var topNum = new Ext.ux.form.Spinner({
			//id: 'topnum',
			value: 10,
			width: 50,
			strategy: {
				xtype : "number",
				minValue: 1,
				maxValue: 999
			}
		});
		this.topNum=topNum;
		var tbar = new Ext.Toolbar({
			items: [
			        '-',
			        "开始时间",
			        '-',
			        dateStart,
			        '-',
			        "--",
			        '-',
			        dateEnd,
			        '-',
			        countType,
			        '-',
			        '前',
			        topNum,
			        '名',
			        '-',
			        {
			        	text: '统计',
			        	iconCls: 'query',
			        	name: 'count',
			        	handler: this.onCountClick,
			        	scope: this
			        },{
			        	text: '打印',
			        	iconCls: 'print',
			        	name: 'print',
			        	handler: this.onPrintClick,
			        	scope: this
			        },{
			        	text: '导出',
			        	iconCls: 'excel',
			        	name: 'export',
			        	handler: this.onExportClick,
			        	scope: this
			        },
			        '-'
			        ]
		});
		this.tbar = tbar;
		return tbar;
	},
	onCountTypeChange: function(combo,data) {
		this.onCountClick();
	},
	onCountClick: function() {
		var tbar = this.panel.getTopToolbar();
		var yplb = "";
		var selectedNode = this.tree.getSelectionModel().selNode;
		if(selectedNode) {
			yplb = selectedNode.attributes.key;
		}
		delete this.queryData;

		with(tbar){
			var ds =this.dateStart.getValue().format('Y-m-d'),//开始时间
			de = this.dateEnd.getValue().format('Y-m-d'),//结束时间
			sorttype =this.countType.getValue(),//统计方式
			topnum =this.topNum.getValue();//前几位
			this.queryData = {ds:ds,de:de,sorttype:sorttype,topnum:topnum,yplb:yplb};
			this.refreshData();
		}
	},
	refreshData: function() {
		this.loadData();
		this.loadReport();
	},
	createUrl: function(type) {
		var pages="phis.prints.jrxml.ReportForOutBoundAnalysis";
		var url="resources/"+pages+".print?type="+type;
		var param = "&ds="+this.queryData.ds+"&de="+this.queryData.de
		+"&sorttype="+this.queryData.sorttype+"&topnum="+this.queryData.topnum+"&yplb="+this.queryData.yplb;
		return url+param;
	},
	onPrintClick: function() {
		if(!this.queryData) return;
		this.actionPrint({url:this.createUrl("1")});
	},
	onExportClick: function() {
		if(!this.queryData) return;
		this.actionExport({url:this.createUrl("3")});
	},
	onExitClick: function() {
		this.panel.destroy();
	},
	textRenderer: function(v, params, data) {
		return "合计";
	},
	actionPrint: function(param) {
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		LODOP.SET_PRINT_PAGESIZE("0","","","");
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:param.url,httpMethod:"get"}));
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		LODOP.PREVIEW();
	},
	actionExport: function(param) {
		var printWin = window.open(param.url,"","height="+(screen.height-100)+", width="+(screen.width-10)
			+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
	},
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('A4');";
	}
});