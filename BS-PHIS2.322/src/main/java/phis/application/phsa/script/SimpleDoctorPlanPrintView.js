$package("phis.application.phsa.script")

$import("phis.script.SimpleModule")
/**
 * 医生排班
 */
var queryKey=0;//点击上下星期的次数
phis.application.phsa.script.SimpleDoctorPlanPrintView = function(cfg) {
	phis.application.phsa.script.SimpleDoctorPlanPrintView.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.phsa.script.SimpleDoctorPlanPrintView,
	phis.script.SimpleModule, {
	
	initPanel : function() {
//		/this.mainFormId = "SimplePrint_mainform_drugstoreMonthly";
		if (this.panel)
			return this.panel;
		var panel = new Ext.Panel({
					border : false,
					html : this.getHtml(),
					frame : true,
					autoScroll : true,
					tbar : {
						xtype : "form",
						layout : "hbox",
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true,
						items : this.getTbar()
					}
				});
		this.panel = panel;
		panel.on("afterrender", this.onReady, this)
		return panel;
		
	},
	getTbar : function() {
		var tbar = [];
		/*
		this.queryTime = new Ext.form.DateField({
			name : 'storeDate',
			value : new Date(),
			width : 100,
			allowBlank : false,
			altFormats : 'Y-m-d',
			format : 'Y-m-d'
		});
		tbar.push(
				 this.queryTime
		);
		*/
		tbar.push({
			xtype : "button",
			text : "返回当前周",
			iconCls : "query",
			scope : this,
			handler : this.doQuery,
			disabled : false
		})
		tbar.push({
			xtype : "button",
			text : "上一星期",
			iconCls : "query",
			scope : this,
			handler : this.getUpDate,
			disabled : false
		})
		tbar.push({
			xtype : "button",
			text : "下一星期",
			iconCls : "query",
			scope : this,
			handler : this.getDownDate,
			disabled : false
		})
		tbar.push({
			xtype : "button",
			text : "打印",
			iconCls : "print",
			scope : this,
			handler : this.doPrint,
			disabled : false
		})
		
      tbar.push({
			xtype : "button",
			text :  "关闭",
			iconCls : "common_cancel",
			scope : this,
			handler : this.doClose,
			disabled : false
		  })
	   this.tbar = tbar;
       return tbar;
	},
	onReady : function() {
		this.initPHSARecord();
		//this.attachLnkEnvents();
	},
	doClose : function(){
		this.opener.closeCurrentTab();
	},
	getDateValue:function(nowTime,value){//获取查询星期数组
     	var date=new Date().format("Y-m-d");
		var backvalue=[];
		var arys= new Array();      
	    arys=date.split('-');     //日期为输入日期，格式为 2013-3-10
	    var ssdate=new Date(arys[0],parseInt(arys[1]-1),arys[2]);
	    var week=ssdate.getDay()  //就是你要的星期几
	    if(value==1){//上一星期
	    	for(var i=0;i<7;i++){
	 	    	 var LSTR_Year=ssdate.getFullYear();
	 		 	  var LSTR_Month=ssdate.getMonth(); 
	 		 	  var LSTR_Date=ssdate.getDate();
	 		  	  //处理 
	 		  	  var uom = new Date(LSTR_Year,LSTR_Month,LSTR_Date); 
	 		  	  uom.setDate(uom.getDate()+i-week+7*queryKey);//取得系统时间的前一天,重点在这里,负数是前几天,正数是后几天 
	 		  	  var LINT_MM=uom.getMonth(); 
	 		  	  LINT_MM++; 
	 		  	  var LSTR_MM=LINT_MM >= 10?LINT_MM:("0"+LINT_MM) 
	 		  	  var LINT_DD=uom.getDate(); 
	 		  	  var LSTR_DD=LINT_DD >= 10?LINT_DD:("0"+LINT_DD) 
	 		  	  //得到最终结果 
	 		  	  uom = uom.getFullYear() + "-" + LSTR_MM + "-" + LSTR_DD;
	 		  	backvalue.push(uom);
	 	     }
	    }else if(value==-1){//下一星期
	    	for(var i=0;i<7;i++){
	 	    	 var LSTR_Year=ssdate.getFullYear();
	 		 	  var LSTR_Month=ssdate.getMonth(); 
	 		 	  var LSTR_Date=ssdate.getDate();
	 		  	  //处理 
	 		  	  var uom = new Date(LSTR_Year,LSTR_Month,LSTR_Date); 
	 		  	  uom.setDate(uom.getDate()+i-week+7*queryKey);//取得系统时间的前一天,重点在这里,负数是前几天,正数是后几天 
	 		  	  var LINT_MM=uom.getMonth(); 
	 		  	  LINT_MM++; 
	 		  	  var LSTR_MM=LINT_MM >= 10?LINT_MM:("0"+LINT_MM) 
	 		  	  var LINT_DD=uom.getDate(); 
	 		  	  var LSTR_DD=LINT_DD >= 10?LINT_DD:("0"+LINT_DD) 
	 		  	  //得到最终结果 
	 		  	  uom = uom.getFullYear() + "-" + LSTR_MM + "-" + LSTR_DD;
	 		  	backvalue.push(uom);
	 	     }
	    }else{
	        for(var i=0;i<7;i++){
	 	    	 var LSTR_Year=ssdate.getFullYear();
	 		 	  var LSTR_Month=ssdate.getMonth(); 
	 		 	  var LSTR_Date=ssdate.getDate();
	 		  	  //处理 
	 		  	  var uom = new Date(LSTR_Year,LSTR_Month,LSTR_Date); 
	 		  	  uom.setDate(uom.getDate()+i-week);//取得系统时间的前一天,重点在这里,负数是前几天,正数是后几天 
	 		  	  var LINT_MM=uom.getMonth(); 
	 		  	  LINT_MM++; 
	 		  	  var LSTR_MM=LINT_MM >= 10?LINT_MM:("0"+LINT_MM) 
	 		  	  var LINT_DD=uom.getDate(); 
	 		  	  var LSTR_DD=LINT_DD >= 10?LINT_DD:("0"+LINT_DD) 
	 		  	  //得到最终结果 
	 		  	  uom = uom.getFullYear() + "-" + LSTR_MM + "-" + LSTR_DD;
	 		  	backvalue.push(uom);
	 	     }
	    }
	  this.backvalue=backvalue;
	  return backvalue;
	},
	/**
	 * 添加超链接触发事件
	 */
	attachLnkEnvents : function() {
		var btns = this.panel.body.query("a");
		if (btns) {
			for (var i = 0; i < btns.length; i++) {
				var btn = Ext.get(btns[i]);
				btn.on("click", this.dataDetail, this)
			}
		}
	},
	/**
	 * 初始化首页数据
	 */
	initPHSARecord : function(){ 
      },
    doQuery : function(value) {
    	if(value !=1 && value!=-1){
    		queryKey=0;
    	}
      var backValue=this.getTbar();
//      var dateTime=this.tjsj.getValue();
      var dateTime = new Date().format("Y-m-d");
      var bzRqXq=[];
      bzRqXq=this.getDateValue(dateTime,value);
      this.panel.body.update( this.getHtml(bzRqXq));
	 }, 
	 getUpDate : function() {
		 queryKey=queryKey-1;
		  var value=1;
	      this.doQuery(value);
	      
	  },
	  doPrint : function(){
			 var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",this.html);
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				//打印
//				LODOP.PRINT();
				//预览
				LODOP.PREVIEW();
				//设计
//				LODOP.PRINT_DESIGN();
	  }, 
	  getDownDate : function() {
		 queryKey=queryKey+1;
			  var value=-1;
		      this.doQuery(value);
		      
	  },
	  getHtml : function(on){
			var dates ;
			var bzRq=[];
	         if(on==null){
	       	           var dateTime=new Date();
	       	           bzRq=this.getDateValue(dateTime,0);
	              }else{
	       	           bzRq=on;
	         }
			var r = phis.script.rmi.miniJsonRequestSync({
				serviceId :this.serviceId,
				serviceAction : this.serviceAction,
				dateUp : bzRq
		    	});
	         if (r.code > 300) {
		             this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
	                	return;
	          } else {
		       if (!r.json.body) {
			        Ext.Msg.alert("提示", "无排班记录!");
			        return;
		        }
		         var dates = r.json.body; 
		     }
	         var pbKsdm=[];//排班科室
	         var pbLb = dates[0];
	         for(var k=0;k<pbLb.LENGTH;k++){
	        	 pbKsdm.push(pbLb['KSMC'+k])
	         } 
	         var PbksTrAdd='';//每行tr加载
	         var zgWidth='825px';//整个表格寬
	 	     var dyWidth='118px';//单个星期格寬
	 	     var ysStyle='';//突出显示属性
	 	     var toDate = new Date().format("Y-m-d");
	         for(var j=0;j<pbKsdm.length;j++){//科室名称
	        	  PbksTrAdd=PbksTrAdd+ 
	        	  '<tr>';
	        	  for(var i=0;i<bzRq.length;i++){//本周日期
	            	    	var SwPbryAdd='';//上午排班人员增加
	                 	    var XwPbryAdd='';//下午排班人员增加
	                 	    for(var z=0;z<dates.length;z++){
	                 	    	var obj = dates[z];
	                 	    	if(obj.GZRQ==bzRq[i] && obj.KSMC==pbKsdm[j] && obj.ZBLB==1){//对上午的人员增加
	                 	    		
	                          	    if(toDate==obj.GZRQ){
	                          	    	ysStyle='style="color:#CE0000;font-size:11px;"';
	                          	    	
	                          	    }else{
	                          	    	ysStyle=' style="font-size:11px;"';
	                          	    }
	                 	    		SwPbryAdd=SwPbryAdd+
		                                    '<tr>'
		                                       +'<td   '+ysStyle+'>'
		                                         +obj.PERSONNAME +"(上午)"
		                                       +'</td>'
		                                   + '</tr>'
	                 	    	}
	                           if(obj.GZRQ==bzRq[i] && obj.KSMC==pbKsdm[j] && obj.ZBLB==2){//对下午的人员增加
	                        	   
	                         	    if(toDate==obj.GZRQ){
	                         	    	ysStyle='style="color:#CE0000;font-size:11px;"';
	                         	    	
	                         	    }else{
	                         	    	ysStyle=' style="font-size:11px;"';
	                         	    }
	                        	   XwPbryAdd=XwPbryAdd
		                                   + '<tr>'
		                                       +'<td  '+ysStyle+'>'
		                                         +obj.PERSONNAME+"(下午)"
		                                       +'</td>'
		                                   + '</tr>'
	                 	    	}
	                 	    }
	                 	    var ksStyle="";//科室样式
		                 	   if(toDate==bzRq[i]){
		                 		    ksStyle='style="padding-left:5px;color:#CE0000;font-size:12px;"';
	                    	    	
	                    	    }else{
	                    	    	ksStyle='style="padding-left:5px;color:#003D79;font-size:12px;"';
	                    	    }
	                 	    
	                 	    PbksTrAdd=PbksTrAdd+
	            	             '<td  style = "border:1px solid black;border-collapse:collapse;" width='+dyWidth+' >'
	            	                 +'<table >'
	            	                   +'<tr>'
	            	                    +'<td width="25px" '+ksStyle+' >'
	            	                      +pbKsdm[j]
	            	                    +'</td>'
	            	                    +'<td >'
	            	                     +'<table >'
	            	                        +'<tr>'
	            	                           +'<td >'
	          	                                   +'<table>'
	         	                                    +SwPbryAdd
	         	                                   +'</table>'
	         	                              +'</td>'
	            	                      +'</tr>'
	            	                      +'<tr>'
	     	                                  +'<td >'
	       	                                    +'<table>'
	      	                                      +XwPbryAdd
	      	                                     +'</table>'
	      	                                 +'</td>'
	         	                           +'</tr>'
	            	                      +'</table>'
	            	                    +'</td>'
	            	                   +'</tr>'
	            	                 +'</table>'
	            	             +'</td>'
	            	    }
	        PbksTrAdd=PbksTrAdd     
	        	+'</tr>'
	         }
	       
	         
	         
	         /******
	          * html主体
	          * 
	          * */
			
			var html=
			'<html>'
			+ '<head >' 
//			     +'<style type="text/css">'
//			       
//			        +'table {'
//			          +'  border-collapse:collapse;'
//			           +' border:1px solid black;'
//			        +'}'
//			     +'</style>'
			 +'</head>'
			+'<body>'
	                    +'<p  style="margin-left:265px;font-weight:bold;font-size:14px;">'
	                           +'医生排班表('+bzRq[0]+'  至  '+bzRq[6]+')'
	                    +'</p>'
			     +'<table  style = "border:1px solid black;border-collapse:collapse;" width='+zgWidth+'>' 
		             +'<tr  >'
	                     + '<td height="30px" style="border:1px solid black;border-collapse:collapse;text-align:center;font-weight:bold;font-size:13px;" >'
	                        + '星期日'
	                           +'<br>'+'('+bzRq[0]+')'
	                           +'</br>'
	                     + '</td>'
			             + '<td height="30px" style="border:1px solid black;border-collapse:collapse;text-align:center;font-weight:bold;font-size:13px;">'
	                       + '星期一'
	                            +'<br>'+'('+bzRq[1]+')'
	                            +'</br>'
	                     + '</td>'
	                     + '<td height="30px" style="border:1px solid black;border-collapse:collapse;text-align:center;font-weight:bold;font-size:13px;">'
	                         + '星期二'
	                              +'<br>'+'('+bzRq[2]+')'
	                              +'</br>'
	                     + '</td>'
	                     + '<td height="30px" style="border:1px solid black;border-collapse:collapse;text-align:center;font-weight:bold;font-size:13px;">'
	                         + '星期三'
	                              +'<br>'+'('+bzRq[3]+')'
	                              +'</br>'
	                     + '</td>'
	                     + '<td height="30px" style="border:1px solid black;border-collapse:collapse;text-align:center;font-weight:bold;font-size:13px;">'
	                         + '星期四'
	                              +'<br>'+'('+bzRq[4]+')'
	                              +'</br>'
	                     + '</td>'
	                     + '<td height="30px" style="border:1px solid black;border-collapse:collapse;text-align:center;font-weight:bold;font-size:13px;">'
	                         + '星期五'
	                              +'<br>'+'('+bzRq[5]+')'
	                              +'</br>'
	                     + '</td>'
	                     + '<td height="30px" style="border:1px solid black;border-collapse:collapse;text-align:center;font-weight:bold;font-size:13px;">'
	                         + '星期六'
	                              +'<br>'+'('+bzRq[6]+')'
	                              +'</br>'
	                     + '</td>'
	                  +'</tr>'
		         + PbksTrAdd
			+ '</table>'
			+'</body>'
			+'</html>'
			this.html = html;
			return html;
		}
}) 
