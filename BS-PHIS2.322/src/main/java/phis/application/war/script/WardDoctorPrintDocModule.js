$package("phis.application.war.script");

$import("phis.script.TabModule","org.ext.ux.layout.TableFormLayout");

phis.application.war.script.WardDoctorPrintDocModule = function(
		cfg) {
	phis.application.war.script.WardDoctorPrintDocModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("tabchange", this.onChange, this)
}
Ext.extend(
		phis.application.war.script.WardDoctorPrintDocModule,
		phis.script.TabModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							deferredRender : false,
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : false,
							//autoHeight : true,
							buttonAlign : "center",
							defaults : {
								frame : false,
								//autoHeight : true,
								autoWidth : true
							},
							items : tabItems,
							buttons : [{
										xtype : "button",
										text : "继续打印",
										handler : this.doJxdy,
										scope : this
									}, {
										xtype : "button",
										text : "按页打印",
										handler : this.doAydy,
										scope : this
									}, {
										xtype : "button",
										text : "指定行打印",
										handler : this.doZdhdy,
										scope : this
									}, {
										xtype : "button",
										text : "重整打印",
										handler : this.doCzdy,
										scope : this
									}/*, {
										xtype : "button",
										text : "重整清空",
										handler : this.doCzqk,
										scope : this
									}*/],
							tbar : this.getTbar()
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return this.tab;
			},
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				this.radiogroup = new Ext.form.RadioGroup({
							fieldLabel : "",
							width : 380,
							items : [{
										boxLabel : '开嘱打印',
										inputValue : 1,
										cmd : "yzzt",
										name : "yzzt",
										checked : true,
										clearCls : true
									}, {
										boxLabel : '停嘱打印',
										cmd : "yzzt",
										name : "yzzt",
										inputValue : 2,
										clearCls : true
									}]
						});
				this.yzzt = 1
				this.radiogroup.on('change', function(radiogroup, radio) {
							var yzzt = radio.inputValue;
							if(yzzt==2&&this.tab.activeTab.id=="cqyz"){
							var module=this.midiModules[this.tab.activeTab.id];
							if(module.store.getCount()>0){
							MyMessageTip.msg("提示", "开嘱有记录未打印,不能停嘱打印!", true);
							this.radiogroup.setValue(1);
							yzzt=1;
							}
							}
							this.yzzt = yzzt
							if(yzzt==2&&this.tab.activeTab.id=="lsyz"){ 
								MyMessageTip.msg("提示", "临时医嘱无停嘱打印!", true);
								this.tab.activate(this.activateId)
								}
							this.loadData();
						}, this);
					return [this.radiogroup]	
			},
			onChange : function(tabPanel, newTab, curTab) {
				// 临时医嘱没有停嘱
				if (this.yzzt == 2 && newTab.id == "lsyz") {
					MyMessageTip.msg("提示", "临时医嘱无停嘱打印!", true);
					this.tab.activate(this.activateId)
					return;
				}
				this.loadData();
			},
			loadData:function(){
				if(this.exContext.brxx!=undefined)
					this.zyh=this.exContext.brxx.data.ZYH;
				   
			if(!this.zyh||!this.yzzt){
			return;}
			var yzqx=this.tab.activeTab.id=="lsyz"?2:1;
			var module=this.midiModules[this.tab.activeTab.id];
			module.requestData.body={"ZYH":this.zyh,"YZZT":this.yzzt,"YZLX":yzqx}
			module.loadData();
			},
			doNew:function(){
				this.zyh=null;
			if(this.midiModules["cqyz"]){
			this.midiModules["cqyz"].clear();
			}
			if(this.midiModules["lsyz"]){
			this.midiModules["lsyz"].clear();
			}	
			},
			//继续打印
			doJxdy:function(){
				if(!this.zyh||!this.yzzt){
				return;}
				var yzqx=this.tab.activeTab.id=="lsyz"?2:1;
				var body={"TYPE":"jxdy","ZYH":this.zyh,"YZZT":this.yzzt,"YZLX":yzqx}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : body
					});
				if(r.code>300){
				MyMessageTip.msg("提示", r.msg, true);
				return;
				}
				var dyyzs=r.json.body.dyyzs;
				var tzyzs=r.json.body.tzyzs;
				if((dyyzs==null||dyyzs=="")&&(tzyzs==null||tzyzs=="")){
				return;
				}
				this.loadData();
				var sftd=this.getSFTD();
				if(sftd==null){
				MyMessageTip.msg("提示", "是否套打参数设置有误", true);
				return;}
				var fileName;
				
				 var zyhs=this.zyh;
					
					var zyh;
					if(zyhs.indexOf("#")>0)
						{
						zyh=zyhs.split("#")[0];
						YEWYH=zyhs.split("#")[1];
						}
					else
						{ 
						YEWYH=0;
						zyh=this.zyh;
						}
				
				if(sftd==1){
				fileName=yzqx==1?"WardDoctorPrint_TD":"WardDoctorPrint_ls_TD";
				}else{
				fileName=yzqx==1?"WardDoctorPrint":"WardDoctorPrint_ls";
				}
				var url="resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&dyyzs="+dyyzs;
				if(tzyzs!=""){
				url+="&tzyzs="+tzyzs;
				}
				url+="&ZYH="+zyh+"&YEWYH="+YEWYH+"&DYLX=jxdy&yzzt="+this.yzzt;
				//url+="&ZYH="+this.zyh+"&DYLX=jxdy&yzzt="+this.yzzt;
//				var printWin=window.open(
//						url,
//						"",
//						"height="
//								+ (screen.height - 100)
//								+ ", width="
//								+ (screen.width - 10)
//								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
//			printWin.onafterprint = function() {
//					printWin.close();
//				};		
								var rehtm=util.rmi.loadXML({url : url,httpMethod : "get"});						
								rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
								//去掉打印body的边框
								rehtm = rehtm.replace("<body", "<body style='margin: 0'")
								var LODOP = getLodop();
								LODOP.PRINT_INIT("打印控件");
								LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
								// 预览LODOP.PREVIEW();
								// 预览LODOP.PRINT();
								LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%",
										rehtm);
								LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
										"Full-Width");
								// 预览
								LODOP.PREVIEW();
			},
			//按页打印
			doAydy:function(){
				if(!this.zyh||!this.yzzt){
				return;}
				if(this.yzzt==2){
				MyMessageTip.msg("提示", "停嘱打印只有继续打印", true);
				return;
				}
				this.aydyform = new Ext.FormPanel({
								frame : true,
								layout : 'tableform',
								layoutConfig : {
								columns : 2
								},
								labelWidth : 15,
								defaultType : 'textfield',
								shadow : true,
								items : [{
											fieldLabel : '从',
											name : 'from'
										},{
											text : '页',
											forId : 'y1',
											xtype : "label"
										},{
											fieldLabel : '到',
											name : 'to'
										},{
											text : '页',
											forId : 'y1',
											xtype : "label"
										}]
							})
					var yzlx=this.tab.activeTab.id=="cqyz"?"长期医嘱":"临时医嘱"
					var win = new Ext.Window({
								layout : "form",
								title : yzlx+'补打',
								width : 200,
								height : 130,
								resizable : true,
								modal : true,
								iconCls : 'x-logon-win',
								constrainHeader : true,
								shim : true,
								// items:this.form,
								buttonAlign : 'center',
								closable : false,
								buttons : [{
											text : '确定',
											handler : this.doOnAydy,
											scope : this
										}, {
											text : '取消',
											handler : this.doAydyCancel,
											scope : this
										}]
							})
					this.chiswin = win
					this.chiswin.add(this.aydyform);
					this.chiswin.show();
			},
			doOnAydy:function(){
				var yzqx=this.tab.activeTab.id=="lsyz"?2:1;
				var pageFrom =this.aydyform.getForm().findField("from").getValue();
				var pageTo=this.aydyform.getForm().findField("to").getValue();
				if(pageFrom==null||pageFrom==undefined||pageFrom==""){
				MyMessageTip.msg("提示", "请选择正确的开始页码", true);
				return;
				}
					if(isNaN(pageFrom)||pageFrom<=0){
					MyMessageTip.msg("提示", "起始页码输入格式不正确,请输入大于0的整数", true);
					return;
					}
				if(pageTo!=null&&pageTo!=undefined&&pageTo!=""){
					if(isNaN(pageTo)||pageTo<=0){
					MyMessageTip.msg("提示", "终止页码输入格式不正确,请输入大于0的整数", true);
					return;
					}
					if(pageTo<pageFrom){
					MyMessageTip.msg("提示", "页码范围不对", true);
					return;
					}
				}
				
				var body={"TYPE":"aydy","ZYH":this.zyh,"YZZT":this.yzzt,"YZLX":yzqx,"pageFrom":pageFrom,"pageTo":pageTo}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : body
					});
				if(r.code>300){
				MyMessageTip.msg("提示", r.msg, true);
				return;
				}
				var sfysj=r.json.body.sfysj;
				if(sfysj!="true"){
				MyMessageTip.msg("提示", "该页码范围无数据", true);
				return;
				}
				this.doAydyCancel();
				var sftd=this.getSFTD();
				if(sftd==null){
				MyMessageTip.msg("提示", "是否套打参数设置有误", true);
				return;}
				var fileName;
				if(sftd==1){
				fileName=yzqx==1?"WardDoctorPrint_TD":"WardDoctorPrint_ls_TD";
				}else{
				fileName=yzqx==1?"WardDoctorPrint":"WardDoctorPrint_ls";
				}
				if(pageTo==""){
				pageTo=0;
				}
//			var printWin=window.open(
//						"resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+this.zyh+"&DYLX=aydy&pageFrom="+pageFrom+"&pageTo="+pageTo,
//						"",
//						"height="
//								+ (screen.height - 100)
//								+ ", width="
//								+ (screen.width - 10)
//								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
//			printWin.onafterprint = function() {
//					printWin.close();
//				};		
				
				var zyhs=this.zyh;
				var zyh;
				if(zyhs.indexOf("#")>0)
					{
					zyh=zyhs.split("#")[0];
					YEWYH=zyhs.split("#")[1];
					}
				else
					{
					YEWYH=0;
					zyh=this.zyh;
					}
								//var url="resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+this.zyh+"&DYLX=aydy&pageFrom="+pageFrom+"&pageTo="+pageTo;
				var url="resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+zyh+"&YEWYH="+YEWYH+"&DYLX=aydy&pageFrom="+pageFrom+"&pageTo="+pageTo;
							
				var rehtm=util.rmi.loadXML({url : url,httpMethod : "get"});						
								rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
								//去掉打印body的边框
								rehtm = rehtm.replace("<body", "<body style='margin: 0'")
								var LODOP = getLodop();
								LODOP.PRINT_INIT("打印控件");
								LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
								// 预览LODOP.PREVIEW();
								// 预览LODOP.PRINT();
								LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%",
										rehtm);
								LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
										"Full-Width");

								// 预览
								LODOP.PREVIEW();
			},
			doAydyCancel:function(){
			this.chiswin.close();
			},
			//指定行打印
			doZdhdy:function(){
			if(!this.zyh||!this.yzzt){
			return;}
			
			var zyhs=this.zyh;
			var zyh;
			if(zyhs.indexOf("#")>0)
				{
				zyh=zyhs.split("#")[0];
				YEWYH=zyhs.split("#")[1];
				}
			else
				{
				YEWYH=0;
				zyh=this.zyh;
				}
		
			if(this.yzzt==2){
				MyMessageTip.msg("提示", "停嘱打印只有继续打印", true);
				return;
				}
				var yzqx=this.tab.activeTab.id=="lsyz"?2:1;
				if(yzqx==1){
				this.zdhList = this.createModule("zdhList", this.zdhListRef);
				this.zdhList.on("qr", this.onZdhdyQr, this);
				this.zdhList.on("close", this.onZdhdyClose, this);
				var win = this.zdhList.getWin();
				win.add(this.zdhList.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.zdhList.requestData.cnd=['and',['eq',['$','b.YZQX'],["i",yzqx]],['eq',['$','b.ZYH'],["l",zyh]]]
					
					if(YEWYH!=0)
					{ 
					
					var cnds=['and',['eq',['$','a.YEWYH'],["i",YEWYH]]]
					this.zdhList.requestData.cnd=['and',this.zdhList.requestData.cnd,cnds]
					
					}
					this.zdhList.loadData();
				}
				}else{
				this.zdhList_ls = this.createModule("zdhList_ls", this.zdhListRef_ls);
				this.zdhList_ls.on("qr", this.onZdhdyQr, this);
				this.zdhList_ls.on("close", this.onZdhdyClose_ls, this);
				var win = this.zdhList_ls.getWin();
				win.add(this.zdhList_ls.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.zdhList_ls.requestData.cnd=['and',['eq',['$','b.YZQX'],["i",yzqx]],['eq',['$','b.ZYH'],["l",zyh]]]
					
					if(YEWYH!=0)
					{ 
					
					var cnds=['and',['eq',['$','a.YEWYH'],["i",YEWYH]]]
					this.zdhList_ls.requestData.cnd=['and',this.zdhList_ls.requestData.cnd,cnds]
					
					}
					this.zdhList_ls.loadData();
				}
				}
			},
			onZdhdyQr:function(r){
			var yzqx=this.tab.activeTab.id=="lsyz"?2:1;
			if(yzqx==1){
			this.onZdhdyClose();
			}else{
			this.onZdhdyClose_ls();
			}
			var sftd=this.getSFTD();
				if(sftd==null){
				MyMessageTip.msg("提示", "是否套打参数设置有误", true);
				return;}
			var fileName;
				if(sftd==1){
				fileName=yzqx==1?"WardDoctorPrint_TD":"WardDoctorPrint_ls_TD";
				}else{
				fileName=yzqx==1?"WardDoctorPrint":"WardDoctorPrint_ls";
				}
//			var printWin=window.open(
//						"resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+this.zyh+"&DYLX=zdhdy&DYYM="+r.get("DYYM")+"&DYHH="+r.get("DYHH"),
//						"",
//						"height="
//								+ (screen.height - 100)
//								+ ", width="
//								+ (screen.width - 10)
//								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
//			printWin.onafterprint = function() {
//					printWin.close();
//				};	
				
				 var zyhs=this.zyh;
					var zyh;
					if(zyhs.indexOf("#")>0)
						{
						zyh=zyhs.split("#")[0];
						YEWYH=zyhs.split("#")[1];
						}
					else
						{
						zyh=this.zyh;
						YEWYH=0;
						}
				//var url="resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+this.zyh+"&DYLX=zdhdy&DYYM="+r.get("DYYM")+"&DYHH="+r.get("DYHH");
					var url="resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+zyh+"&YEWYH="+YEWYH+"&DYLX=zdhdy&DYYM="+r.get("DYYM")+"&DYHH="+r.get("DYHH");
					
					var rehtm=util.rmi.loadXML({url : url,httpMethod : "get"});						
								rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
								//去掉打印body的边框
								rehtm = rehtm.replace("<body", "<body style='margin: 0'")
				var LODOP = getLodop();
								LODOP.PRINT_INIT("打印控件");
								LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
								// 预览LODOP.PREVIEW();
								// 预览LODOP.PRINT();
								LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%",rehtm);
								LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
										"Full-Width");

								// 预览
								LODOP.PREVIEW();
			},
			onZdhdyClose:function(){
			this.zdhList.getWin().hide();
			},
			onZdhdyClose_ls:function(){
			this.zdhList_ls.getWin().hide();
			},
			//重整打印
			doCzdy:function(){
			if(!this.zyh||!this.yzzt){
			return;}
			if(this.yzzt==2){
				MyMessageTip.msg("提示", "停嘱打印只有继续打印", true);
				return;
				}
			var yzqx=this.tab.activeTab.id=="lsyz"?2:1;
			
			
			  var zyhs=this.zyh;
				var zyh;
				if(zyhs.indexOf("#")>0)
					{
					zyh=zyhs.split("#")[0];
					YEWYH=zyhs.split("#")[1];
					}
				else
					{
					zyh=this.zyh;
					YEWYH=0;
					}
			
				var body={"TYPE":"czdy","ZYH":this.zyh,"YZLX":yzqx}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : body
					});
				if(r.code>300){
				MyMessageTip.msg("提示", r.msg, true);
				return;
				}
				this.loadData();
				var sftd=this.getSFTD();
				if(sftd==null){
				MyMessageTip.msg("提示", "是否套打参数设置有误", true);
				return;}
				var fileName;
				if(sftd==1){
				fileName=yzqx==1?"WardDoctorPrint_TD":"WardDoctorPrint_ls_TD";
				}else{
				fileName=yzqx==1?"WardDoctorPrint":"WardDoctorPrint_ls";
				}
//				var printWin=window.open(
//						"resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+this.zyh+"&DYLX=czdy",
//						"",
//						"height="
//								+ (screen.height - 100)
//								+ ", width="
//								+ (screen.width - 10)
//								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
//			printWin.onafterprint = function() {
//					printWin.close();
//				};	
				//var url="resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+this.zyh+"&DYLX=czdy";
				var url="resources/phis.prints.jrxml."+fileName+".print?silentPrint=1&YZLX="+yzqx+"&ZYH="+zyh+"&YEWYH="+YEWYH+"&DYLX=czdy";
				
				var rehtm=util.rmi.loadXML({url : url,httpMethod : "get"});						
								rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
								//去掉打印body的边框
								rehtm = rehtm.replace("<body", "<body style='margin: 0'")
			var LODOP = getLodop();
								LODOP.PRINT_INIT("打印控件");
								LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
								// 预览LODOP.PREVIEW();
								// 预览LODOP.PRINT();
								LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%",rehtm);
								LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
										"Full-Width");

								// 预览
								LODOP.PREVIEW();
			},
			//查询是否套打参数
			getSFTD:function(){
			if(this.SFTD){
			return this.SFTD;
			}
			var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.querySFTDAction
					});
				if(r.code>300){
				MyMessageTip.msg("提示", r.msg, true);
				return;
				}
				this.SFTD=r.json.sftd
				return this.SFTD;
			}
		})