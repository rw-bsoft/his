$package("phis.application.mds.script");

$import("phis.script.SimpleModule");

phis.application.mds.script.MedicinesManagePriceCalculateModule = function(cfg) {
	phis.application.mds.script.MedicinesManagePriceCalculateModule.superclass.constructor
			.apply(this, [cfg]);
}
var ctr;
Ext.extend(phis.application.mds.script.MedicinesManagePriceCalculateModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : 600,
							height : 250,
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
										region : 'north',
										width : 600,
										height : 80,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 600,
										items : this.getModules()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				ctr=this;
				this.panel = panel;
				return panel;
			},
			//上面的结果框
			getForm : function() {
				if (this.form) {
					return this.form;
				}
				var form = new Ext.form.FormPanel({
							labelAlign : "right",
							//labelWidth : this.labelWidth || 80,
							iconCls : 'bogus',
							border : false,
							frame : true,
							autoHeight : true,
							autoWidth : true,
							defaultType : 'textfield',
							shadow : true,
							items : new Ext.form.TextArea({
										width : 570,
										name:"calu",
										disabled:true,
										hideLabel :true
									})
						})
				this.form = form;
				return form;
			},
			//下面的整体module
			getModules : function() {
				var module = new Ext.Panel({
							border : false,
							width : 600,
							height : 170,
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
										width : 300,
										// height : 80,
										items : this.getButton()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 300,
										items : this.getList()
									}]
						});
				this.module = module;
				return module;
			},
			//按钮框
			getButton : function() {
				var button = new Ext.Panel({
							widht : 300,
							bodyPadding : 5,
							layout : {
								type : 'table',
								columns : 5
								// 设置表格布局默认列数
							},
							defaultType : 'button',
							defaults : {
								minWidth : 50,
								handler : this.btnClick
							},
							items : [{
										text : '7',
										symbol : '7'
									}, {
										text : '8',
										symbol : '8'
									}, {
										text : '9',
										symbol : '9'
									}, {
										text : '+',
										symbol : '+'
									}, {
										text : '-',
										symbol : '-'
									}, {
										text : '4',
										symbol : '4'
									}, {
										text : '5',
										symbol : '5'
									}, {
										text : '6',
										symbol : '6'
									}, {
										text : '*',
										symbol : '*'
									}, {
										text : '/',
										symbol : '/'
									}, {
										text : '1',
										symbol : '1'
									}, {
										text : '2',
										symbol : '2'
									}, {
										text : '3',
										symbol : '3'
									}, {
										text : ' ',
										symbol : '('
									}, {
										text : ' ',
										symbol : ')'
									}, {
										text : '0',
										symbol : '0',
										colspan:2,
										minWidth : 100
									}, {
										text : '.',
										symbol : '.'
									}, {
										text : '←',
										symbol : 'back'
									}, {
										text : '清除',
										symbol : 'clear'
									}]
						},this)
						this.button=button;
						return button;
			},
			//右边Lit列表数据
			getList : function() {// 下面右边的列表
				var store = new Ext.data.ArrayStore({
							fields : [{
										name : 'LB'
									}]
						})
				var data = [['实际进价'], ['标准零价']];
				store.loadData(data)
				 var list = new Ext.grid.GridPanel({
       								store: store,
       								 columns: [
           										 {
               							 id       :'LB',
                						 header   : '', 
               							 width    : 160, 
                						 sortable : true, 
                						 dataIndex: 'LB'
           										 }]
				 });
				list.on("rowdblclick",this.onDblClick,this)
				this.list=list;
				return list;
			},
			//右边List点击
			onDblClick : function(grid,index,e) {
			var lb=this.list.getSelectionModel().getSelected().get("LB")
			var nr=this.form.getForm().findField("calu").getValue()||""
			if(nr.length>0&&!this.compareLast(nr,["+","-","*","/","("],1)){
			return
			}
			this.form.getForm().findField("calu").setValue(nr+lb)
			},
			//计算器按钮点击
			btnClick:function(btn){
			var b= btn.symbol;//界面点击的按钮
			var nr=ctr.form.getForm().findField("calu").getValue()||""
			if(b=="back"){//退格键
			if(nr.length>4&&ctr.compareLast(nr,["实际进价","标准零价"],4)){
			ctr.form.getForm().findField("calu").setValue(nr.substring(0,nr.length-4))
			}else{
			if(nr.length>0){
			ctr.form.getForm().findField("calu").setValue(nr.substring(0,nr.length-1))
			}
			}
			}
			else if(b=="clear"){//清除键
			ctr.form.getForm().findField("calu").setValue("");
			}
			else if(b=="+"||b=="-"||b=="*"||b=="/"){//计算符
			if(nr.length==0||ctr.compareLast(nr,["+","-","*","/"],1)){//如果前面那个字符是加减乘除 不增加
			return;
			}else{
			ctr.form.getForm().findField("calu").setValue(nr+b);
			}
			//由于暂时未找到带()的计算逻辑 故先去除
			}else if(b=="("||b==")"){//左右括号
//			if(b=="("&&nr.length!=0&&!ctr.compareLast(nr,["+","-","*","/"],1)){//如果前面那个字符不是加减乘除 不增加
//			return;
//			}
//			if(b==")"&&(nr.length==0||(nr.length!=0&&ctr.compareLast(nr,["+","-","*","/","."],1)))){//如果前面那个字符不是加减乘除 不增加
//			return;
//			}
//			ctr.form.getForm().findField("calu").setValue(nr+b);
				return;
			}else if(b=="."){//"."
			if(nr.length!=0&&!ctr.compareLast(nr,["1","2","3","4","5","6","7","8","9","0"],1)){//如果前面那个字符不是数字 不增加
			return;
			}else{
				ctr.form.getForm().findField("calu").setValue(nr+b);
			}
			}else{//数字
			if(nr.length!=0&&!ctr.compareLast(nr,["+","-","*","/",".","("],1)){//如果前面那个字符不是加减乘除 不增加
			return;
			}else{
				ctr.form.getForm().findField("calu").setValue(nr+b);
			}
			}
			},
			//判断传入数据最后几位是否包含集合中的字符,第一个参数是要判断的数据,第二个参数是判断的字符,第三个参数是最后几位
			compareLast:function(data,m,l){
			var d=data.substring(data.length-l,data.length);
			for(var i in m){
			if(d==m[i]){
			return true;}
			}
			return false;
			},
			doNew:function(){
			this.form.getForm().findField("calu").setValue("");
			},
			doCommit:function(){
			var d=this.form.getForm().findField("calu").getValue();
			if(!d||d==null||d==""){
			return;
			}
			this.fireEvent("commit",d);
			this.doCancel();
			},
			doCancel:function(){
			this.getWin().hide();
			}
			
		});