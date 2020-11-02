$package("app.modules.list")
$import("app.desktop.Module","util.rmi.loadXML","app.modules.list.LodopFuncs","util.widgets.SpinnerField")

app.modules.list.PrintWin = function(cfg) {
	app.modules.list.PrintWin.superclass.constructor.apply(this, [cfg])
}
Ext.extend(app.modules.list.PrintWin, app.desktop.Module, {
	initPanel:function(){
		var printerCombox=this.getPrinters()
		printerCombox.on("select",this.getPageSize,this)	
		var printConfig = new Ext.FormPanel({
			frame: true,
			defaults: {width: '95%'},
			shadow:true,
	        labelWidth: 75, 
	        bodyStyle:'padding:2px 20px 0 0',
	        autoScroll:true,
	        labelAlign:'top',
	        name:"当前打印参数",
	        title:"当前打印参数",
	        items: [{
	            layout:'column',
	            items:[{
	                columnWidth:.5,
	                layout: 'form',
	                defaultType: 'textfield',
	                items:[ printerCombox,
	                		this.getCombox({	
								fieldLabel: '纸张方向',
					            name: 'Orient',
					            anchor:'95%'
							},[['1','纵向'],['2','横向']]),
							{
								xtype : "numberfield",
								name:"top",
								fieldLabel : "上边距(默认单位:cm)",
								anchor:'95%'
							},
							new util.widgets.MyRadioGroup({
					            value:'whole',
					            name:"pages",
					            anchor:'95%',
					            fieldLabel: '打印内容',
					            items: [
					                {boxLabel: '全部页', name: 'gird-print-1',inputValue:'whole'},
					                {boxLabel: '当前页', name: 'gird-print-1',inputValue:'single'}
					            ]   	
							})
	                ]
	            },{
	                columnWidth:.5,
	                layout: 'form',
	                defaultType: 'textfield',
	                items:[this.getCombox({
							fieldLabel: '打印机支持纸张大小',
				            name: 'pageSize',
				            anchor:'95%'
						}),{
				            	xtype: 'spinnerfield',
				            	fieldLabel: '打印份数',
				            	 name: 'copies',
				            	minValue: 0,
				            	maxValue: 100,
				            	allowDecimals: true,
				            	decimalPrecision: 1,
				            	incrementValue: 1,
				            	accelerate: true ,
				            	allowDecimals:false,
							    anchor:'95%'
				            },{
								xtype : "numberfield",
								name:"left",
								fieldLabel : "左边距(默认单位:cm)",
								anchor:'95%'
							}/*,
			                {
								xtype : "numberfield",
								name:"but",
								fieldLabel : "下边距(默认单位:px)",
								anchor:'95%'
							},
							{
								xtype : "numberfield",
								name:"right",
								fieldLabel : "右边距(默认单位:px)",
								anchor:'95%'
							}*/
			         ]
	            }]
	        }]
		})
		this.printConfig=printConfig
		this.loadPrintSet()
		return printConfig
	},
	printPreview:function(btn){
		var fields=this.getFields();
		var page = fields.pages.getValue()
		var type = 0
		if(btn && btn.iconCls){
			if(btn.iconCls=="print"||btn.iconCls=="print-preview"){
				type = 1
			}else if(btn.iconCls=="doc"){
				type = 2
			}else if(btn.iconCls=="excel"){
				type = 3
			}
		}
		var cm = this.cm
		var cos = cm.getColumnsBy(function(c){
  			return !c.hidden;
		})
		var cname = []
		for(var i=0;i<cos.length;i++){
			cname.push(cos[i].dataIndex)
		}
		var printConfig = {
			title:this.title,
			page:page,
			requestData:this.requestData,
			cname:cname
		}
		this.fixPrintConfig(printConfig);
		var url ="list.print?type="+type+"&config="+encodeURI(encodeURI(Ext.encode(printConfig)))
		if(btn.iconCls=="print"||btn.iconCls=="print-preview"){
			//将打印设置以，隔开存cookie，顺序1.打印机 2.纸张大小 3.方向 4.上边距 5.左边距 
			var printSet=fields.printer.getValue()+","+fields.pageSize.getValue()+","+fields.Orient.getValue()+","+
			fields.top.getValue()+","+fields.left.getValue();
			var date = new Date()
		    date.setTime(date.getTime() + 3650*24*3600*1000)
		    date.toGMTString()
			Ext.util.Cookies.set(encodeURI(encodeURI('print'+this.title)),printSet,date,"")
			var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE(fields.Orient.getValue(),"","",fields.pageSize.getValue());
			LODOP.ADD_PRINT_HTM(fields.top.getValue()==""?5:fields.top.getValue()+"cm",fields.left.getValue()==""?5:fields.left.getValue()+"cm","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			if (fields.copies.getValue()!=""){
				LODOP.SET_PRINT_COPIES(fields.copies.getValue())
			}
			if(btn.iconCls=="print"){
				LODOP.SET_PRINTER_INDEXA(fields.printer.getValue())
				LODOP.PRINT()
			}else{
				if(fields.printer.getValue()!=""){
					LODOP.SET_PRINTER_INDEXA(fields.printer.getValue())
				}
				LODOP.PREVIEW()
			}
		}else{
			var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
			return printWin
		}
	},
	fixPrintConfig:function(cfg){	
	},
	getWin:function(){
		var win = this.win
		if(!win){
			win = new Ext.Window({
				title:"打印设置",
				width:450,
				autoHeight:true,
				constrain : true,
				closeAction:"hide",
				modal :true, 
				items:this.initPanel(),
				tbar:[{
					text:"直接打印",
					iconCls:"print",
					handler:this.printPreview,
					scope:this
				},{
					text:"打印预览",
					iconCls:"print-preview",
					handler:this.printPreview,
					scope:this
				},{
					text:"WORD",
					iconCls:"doc",
					handler:this.printPreview,
					scope:this
				},{
					text:"PDF",
					iconCls:"pdf",
					handler:this.printPreview,
					scope:this
				},{
					text:"EXCEL",
					iconCls:"excel",
					handler:this.printPreview,
					scope:this
				},{
					text:"关闭",
					iconCls:"common_cancel",
					handler:function(){
						this.getWin().hide();
					},
					scope:this
				}]
			})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("add", function() {
				this.win.doLayout()
			}, this)
			this.win = win
		}
		return win
	},
	getPrinters:function(){
		var LODOP=getLodop()
		var datas=[]
		for(var i=0;i<LODOP.GET_PRINTER_COUNT();i++){
			var data=[]
			data.push(i)
			data.push(LODOP.GET_PRINTER_NAME(i))
			datas.push(data)
		}
		return this.getCombox({
			fieldLabel: '选择打印机',
            name: 'printer',
            anchor:'95%',
            minListWidth:280
		},datas)
	},
	getPageSize:function(f){
		var LODOP=getLodop()
		var datas=[]
		var strPageSizeList=LODOP.GET_PAGESIZES_LIST(f.getValue(),"%");
		var Options=new Array(); 
		var Options=strPageSizeList.split("%");
		for (var i=0;i<Options.length;i++){
			var data=[]
			data.push(Options[i])
			data.push(Options[i])
			datas.push(data)
	   	}
	   	var field=this.getFields()
	   	field.pageSize.clearValue()
	   	field.pageSize.store.loadData(datas)
	},
	getCombox:function(cfg,data){
		var combox = new Ext.form.ComboBox({
    		typeAhead: true,
    		triggerAction: 'all',
  			forceSelection: true,  
    		lazyRender:true,
    		mode: 'local',
    		emptyText:'请选择...',
    		anchor:'95%',
    		store: new Ext.data.ArrayStore({
        		fields: [
            		'key',
            		'value'
        		],
        		data: []
    		}),
    		valueField: 'key',
    		displayField: 'value'
		});
		Ext.apply(combox,cfg)
		if(data){
			combox.store.loadData(data)
		}
		return combox;
	},
	getFields:function(){
		var fields = this.fields
		if(!fields){
			var items = this.printConfig.items.item(0).items
			var n = items.getCount()
			var fields = {}
			for(var i = 0; i < n; i ++){
				var items1 = items.get(i).items
				var n1= items1.getCount()
				for(var j = 0; j < n1; j ++){
					var f = items1.get(j)
					if(f.name)
						fields[f.name] = f
				}
			}
			this.fields= fields
		}
		return fields
	},
	loadPrintSet:function(){
		var printSet=Ext.util.Cookies.get(encodeURI(encodeURI('print'+this.title)));
		var fields=this.getFields();
		this.getPageSize(fields.printer)
		if(printSet!=null){
			var p=printSet.split(",")
			fields.printer.setValue(p[0])
			fields.pageSize.setValue(p[1])
			fields.Orient.setValue(p[2])
			fields.top.setValue(p[3])
			fields.left.setValue(p[4])
			fields.copies.setValue(1)
			
		}else{
			fields.top.setValue(0.15)
			fields.left.setValue(0.15)
			fields.printer.setValue(fields.printer.store.getAt(0).data.key)
			fields.pageSize.setValue("A4")
			fields.Orient.setValue(1)
			fields.copies.setValue(1)
		}
	}
})