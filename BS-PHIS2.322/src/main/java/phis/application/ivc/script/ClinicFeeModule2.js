/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule","phis.application.yb.script.MedicareCommonMethod","phis.script.util.FileIoUtil",
	"phis.script.Phisinterface","phis.application.gp.script.GeneralPractitioner");

phis.application.ivc.script.ClinicFeeModule2=function(cfg){
	this.width=1020;
	this.height=550;
	cfg.modal=this.modal=true;
	Ext.apply(this,phis.application.yb.script.MedicareCommonMethod);
	Ext.apply(this,phis.script.Phisinterface);
	Ext.apply(this, phis.application.gp.script.GeneralPractitioner);
	phis.application.ivc.script.ClinicFeeModule2.superclass.constructor.apply(this,[cfg]);
}
Ext.extend(phis.application.ivc.script.ClinicFeeModule2,phis.script.SimpleModule,{
			loadSystemParam : function() {
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "publicService",
							serviceAction : "loadSystemParams",
							body : {
								privates : ['YFJKJM']
							}
						});
				this.systemParams = resData.json.body;
			},
			initPanel:function(){
				this.loadSystemParam();
				if(this.panel){
					return this.panel;
				}
				var panel=new Ext.Panel({
							width:this.width,
							height:this.height,
							layout:'border',
							defaults:{
								border:false
							},
							buttonAlign:'center',
							items:[{
										layout:"fit",
										title:'',
										region:'north',
										height:75,
										items:this.getForm()
									},{
										layout:"fit",
										title:'',
										region:'center',
										items:this.getModule3()
									}],
							tbar:new Ext.Toolbar({
										enableOverflow:true,
										items:(this.tbar||[]).concat([this.createButton()])
									})
						});
				this.panel=panel;
				this.panel.on("afterrender",this.onReady,this);
				this.getFphm();
				return panel;
			},
			onReady:function(){
			},
			getForm:function(){
				var module=this.createModule("Form ",this.refForm);
				module.on("qx",this.doQx,this);
				module.exContext=this.exContext;
				var formModule=module.initPanel();
				this.formModule=module;
				module.opener=this;
				var form=module.form;
				this.form=form;
				var mzgl=form.getForm().findField("MZGL");
				if(mzgl){
					mzgl.un("specialkey",module.onFieldSpecialkey,module);
					mzgl.on("specialkey",function(f,e){
								var key=e.getKey();
								if(key==e.ENTER){
									module.doENTER(f);
								}
							},this);
					mzgl.focus(false,200);
				}
				var cfts=form.getForm().findField("CFTS");
				if(cfts){
					cfts.un("specialkey",module.onFieldSpecialkey,module);
					cfts.on("specialkey",function(cfts,e){
								var key=e.getKey();
								if(key==e.ENTER){
									this.doCFTSENTER();
								}
							},this);
					cfts.on("change",function(){
								this.module.list.setCFTS(cfts.getValue());
							},this);
				}
				var ybmc=form.getForm().findField("YBMC");
				if(ybmc){
					ybmc.un("specialkey",module.onFieldSpecialkey,module);
					ybmc.on("specialkey",function(ybmc,e){
								var key=e.getKey();
								if(key==e.ENTER){
									this.doYBMCENTER();
								}
							},this);
				}
				var ysdm=form.getForm().findField("YSDM");
				if(ysdm){
					ysdm.un("specialkey",module.onFieldSpecialkey,module);
					ysdm.on("specialkey",function(ysdm,e){
								var key=e.getKey();
								if(key==e.ENTER){
									if(ysdm.getValue()){
										var ksdm=this.formModule.form.getForm().findField("KSDM");
										ksdm.focus();
									}
								}
							},this);
				}
				var ksdm=form.getForm().findField("KSDM");
				if(ksdm){
					ksdm.un("specialkey",module.onFieldSpecialkey,module);
					ksdm.on("specialkey",function(ksdm,e){
								var key=e.getKey();
								if(key==e.ENTER){
									if(ksdm.getValue()){
										this.doKSDMENTER();
									}
								}
							},this);
				}
				return formModule;
			},
			doKSDMENTER:function(){
				if(this.module.list.type==3){
					var cfts=this.formModule.form.getForm().findField("CFTS");
					cfts.focus(true);
				}else{
					var n=this.module.list.store.getCount();
					this.module.list.grid.startEditing(n-1,3);
				}
			},
			doCFTSENTER:function(){
				var n=this.module.list.store.getCount();
				this.module.list.grid.startEditing(n-1,3);
			},
			doYBMCENTER:function(){
				if(this.module.list.type==3){
					var cfts=this.formModule.form.getForm().findField("CFTS");
					cfts.focus(true);
				}else{
					var n=this.module.list.store.getCount();
					this.module.list.grid.startEditing(n-1,3);
				}
			},
			getModule3:function(){
				var module3=this.createModule("module3",this.refModule);
				module3.exContext=this.exContext;
				module3.opener=this;
				this.module=module3;
				return module3.initPanel();
			},
			doFz:function(){
				this.module.list.grid.stopEditing();
				if(!this.MZXX){
					MyMessageTip.msg("提示","请先调入病人信息",true);
					return;
				}
				var dqfphm=this.formModule.form.getForm().findField("FPHM").getValue();
				if(!dqfphm){
					Ext.Msg.alert("提示","请先维护发票号码!");
					return;
				}
				Ext.Msg.show({
							title:'对话框',
							msg:'请输入复制的发票号码:',
							modal:true,
							width:300,
							buttons:Ext.MessageBox.OKCANCEL,
							prompt:true,
							fn:function(btn,fphm){
								if(btn=="ok"){
									this.copyFphm(fphm);
								}
							},
							scope:this
						});
			},
			// 新病人
			doNewPerson:function(){
				this.module.list.grid.stopEditing();
				var pdms=phis.script.rmi.miniJsonRequestSync({
					serviceId:"clinicChargesProcessingService",
					serviceAction:"checkCardOrMZHM"
					});
				if(pdms.code>300){
					this.processReturnMsg(pdms.code,pdms.msg,this.doNewPerson);
					return;
				}else{
					if(!pdms.json.cardOrMZHM){
						Ext.Msg.alert("提示","该卡号门诊号码判断不存在!");
						return;
					}
				}
				var m=this.midiModules["newHealthRecordModule"];
				if(!m){
					$import("phis.application.pix.script.EMPIInfoModule");
					m=new phis.application.pix.script.EMPIInfoModule({
								entryName:"phis.application.pix.schemas.MPI_DemographicInfo",
								title:"个人基本信息查询",
								height:450,
								modal:true,
								mainApp:this.mainApp
							});
					m.on("onEmpiReturn",this.newPerson,this);
					this.midiModules["newHealthRecordModule"]=m;
				}
				var win=m.getWin();
				win.setPosition(250,100);
				win.show();
				// 1卡号
				var form=m.midiModules[m.entryName].form.getForm();
				if(pdms.json.cardOrMZHM==1){
					form.findField("MZHM").setDisabled(true);
				}
				// 2门诊号码
				if(pdms.json.cardOrMZHM==2){
					form.findField("cardNo").setValue(form.findField("MZHM").getValue());
					form.findField("personName").focus(true,200);
				}
			},
			newPerson:function(data,ifNew){
				var body={
					serviceId:"clinicChargesProcessingService",
					serviceAction:"queryCardNo",
					MZHM:data.MZHM
				}
				var rc=phis.script.rmi.miniJsonRequestSync(body);
				if(rc.code>300){
					this.processReturnMsg(rc.code,rc.msg);
					return;
				}
				if(ifNew){
					var body={
						serviceId:"clinicChargesProcessingService",
						serviceAction:"queryPerson"
					}
					body.MZHM=data.MZHM
					body.newPerson=true;
					var r=phis.script.rmi.miniJsonRequestSync(body);
					if(r.code>300){
						this.processReturnMsg(r.code,r.msg);
						return;
					}
					var mzgl=this.formModule.form.getForm().findField("MZGL");
					mzgl.setValue(rc.json.CARDNO||data.MZHM);
					this.formModule.doENTER(mzgl)
				}else{
					var mzgl=this.formModule.form.getForm().findField("MZGL");
					mzgl.setValue(rc.json.CARDNO||data.MZHM);
					this.formModule.doENTER(mzgl)
				}
			},
			doXg:function(){
				this.module.list.grid.stopEditing();
				var dqfphm=this.formModule.form.getForm().findField("FPHM").getValue();
				if(!dqfphm){
					Ext.Msg.alert("提示","请先维护发票号码!");
					return;
				}
				Ext.Msg.show({
							title:'对话框',
							msg:'输入当前发票号:',
							modal:true,
							width:300,
							buttons:Ext.MessageBox.OKCANCEL,
							prompt:true,
							value:dqfphm,
							fn:function(btn,fphm){
								if(btn=="ok"){
									if(!fphm||fphm==dqfphm){
										return;
									}
									if(fphm.length!=dqfphm.length){
										Ext.Msg.alert("提示","修改后的发票号码与原发票号码长度不相等");
										return;
									}
									if(fphm<dqfphm){/* 无需类型强转，直接字符串比较 */
										Ext.Msg.alert("提示","修改后的发票号码不能小于原发票号码");
										return;
									}
									this.updateFphm(fphm);
								}
							},
							scope:this
						})
			},
			// 复制发票号码
			copyFphm:function(fphm){
				var body={
					"FPHM":fphm,
					"MZXX":this.MZXX
				}
				var r=phis.script.rmi.miniJsonRequestSync({
							serviceId:"clinicChargesProcessingService",
							serviceAction:"saveCopyFphm",
							body:body
						});
				if(r.code>300){
					this.processReturnMsg(r.code,r.msg,this.copyFphm);
					return;
				}else{
					if(!r.json.body){
						Ext.Msg.alert("提示","该发票号码无收费数据!");
						return;
					}
					if(r.json.body.msg){
						Ext.Msg.alert("提示",r.json.body.msg);
						return;
					}
					var cfsbs=r.json.body;
					this.setCFD(cfsbs);
				}
			},
			// 修改发票号码
			updateFphm:function(fphm){
				var r=phis.script.rmi.miniJsonRequestSync({
							serviceId:"clinicChargesProcessingService",
							serviceAction:"updateNotesNumber",
							body:fphm
						});
				if(r.code>300){
					this.processReturnMsg(r.code,r.msg,this.updateFphm);
					return;
				}else{
					if(!r.json.body){
						Ext.Msg.alert("提示","修改后的发票号码不能过大!");
						return;
					}
					var fphm=r.json.body;
					var form=this.formModule.form.getForm();
					var FPHM=form.findField("FPHM");
					FPHM.setValue(fphm);
				}
			},
			// 获取发票号码
			getFphm:function(){
				var mzsf=this;
				var r=phis.script.rmi.miniJsonRequestSync({
							serviceId:"clinicChargesProcessingService",
							serviceAction:"getNotesNumber"
						});
				if(r.code>300){
					Ext.Msg.alert('提示',r.msg,function(){
								mzsf.opener.opener.closeCurrentTab();// 关闭收费模块
							});
					return;
				}else{
					if(!r.json.body){
						Ext.Msg.alert("提示","请先维护发票号码!",function(){
									mzsf.opener.opener.closeCurrentTab();// 关闭收费模块
								});
						return;
					}
					var fphm=r.json.body;
					var form=this.formModule.form.getForm();
					var FPHM=form.findField("FPHM");
					FPHM.setValue(fphm);
				}
			},
			doSave:function(item,e,callBack){// 预保存
				if(this.saveing)
					return;
				this.saveing=true;
				this.module.list.grid.stopEditing();
				var cfts=this.formModule.form.getForm().findField("CFTS");
				this.module.list.setCFTS(cfts.getValue());
				var store=this.module.list.grid.getStore();
				if(!this.MZXX){
					MyMessageTip.msg("提示","请先调入病人信息",true);
					this.saveing=false;
					return;
				}
				if(store.getCount()==0){
					Ext.Msg.alert("提示","当前没有可以保存信息!");
					this.saveing=false;
					return;
				}else if(store.getCount()==1){
					var r=store.getAt(0);
					if(!r.data.YPXH){
						Ext.Msg.alert("提示","当前没有可以保存信息!");
						this.saveing=false;
						return;
					}
				}
				var n=store.getCount();
				var data=[];
				for(var i=0;i<n;i++){
					var r=store.getAt(i);
					if(!r.data["YPMC"]){
						continue;
					}
					data.push(r.data);
					if(r.get("msg")){
						this.saveing=false;
						this.module.list.grid.startEditing(i,7);
						return false;
					}
					if(!r.get("YSDM")){
						MyMessageTip.msg("提示","开单医生不能为空！错误行 "+(i+1)+" 。",true);
						this.saveing=false;
						this.module.list.grid.getSelectionModel().select(i,4);
						this.module.list.onRowClick();
						this.formModule.form.getForm().findField("YSDM").focus();
						return false;
					}
					if(!r.get("KSDM")){
						MyMessageTip.msg("提示","开单科室不能为空！错误行 "+(i+1)+" 。",true);
						this.saveing=false;
						this.module.list.grid.getSelectionModel().select(i,4);
						this.module.list.onRowClick();
						this.formModule.form.getForm().findField("KSDM").focus();
						return false;
					}
				}
				if(this.MZXX.XZDL==1){// 性质大类是医保的必须录入诊断
					var zdmc=this.formModule.form.getForm().findField("JBMC").getValue();
					if(zdmc==null||zdmc==""){
						MyMessageTip.msg("提示","医保性质病人必须录入诊断",true);
						this.saveing=false;
						return;
					}else{
						this.MZXX.ZDMC=zdmc;
					}
				}
				var ret=phis.script.rmi.miniJsonRequestSync({
							serviceId:"clinicChargesProcessingService",
							serviceAction:"saveSettle",
							body:data,
							MZXX:this.MZXX
						});
				this.saveing=false;
				if(ret.code>300){
					this.processReturnMsg(ret.code,ret.msg);
					return;
				}
				this.MZXX.mxsave=false;
				MyMessageTip.msg("提示","收费预结算保存成功!",true);
				this.doQx();
			},
			doRemove:function(CFSB){
				for(var i=0;i<this.djs.length;i++){
					if(this.djs[i].CFSB==CFSB){
						this.djs.splice(i,1);
					}
				}
			},
			clone:function( obj ){
				var result={};
				for(var i in obj) {
					result[i]   =   obj[i] 
				} 
				return result 
			},
			// 费用结算
			doJs:function(item,e,callBack){
				this.module.list.grid.stopEditing();
				var store=this.module.list.grid.getStore();
				if(!this.MZXX){
					MyMessageTip.msg("提示","请先调入病人信息",true);
					return;
				}
				if(store.getCount()==0){
					Ext.Msg.alert("提示","当前没有可以结算信息!");
					return;
				}else if(store.getCount()==1){
					var r=store.getAt(0);
					if(!r.data.YPXH){
						Ext.Msg.alert("提示","当前没有可以结算信息!");
						return;
					}
				}
				//由于医保自费分离需更改病人性质,所以此处必须重新给门诊信息中病人性质赋值
				this.MZXX.BRXZ = this.formModule.form.getForm().findField("BRXZ").getValue();
				var n=store.getCount();
				var data=[];
				var fyyfArry=[];
				var isAllSelfPay = true;//是否全自费
				for(var i=0;i<n;i++){
					var r=store.getAt(i);
					if(!r.data["YPMC"]){
						continue;
					}
					if(isAllSelfPay && r.get("YYZBM")=="医保可报销"){
						isAllSelfPay = false;
					}
					data.push(this.clone(r.data));
					if(r.get("CFLX")==1||r.get("CFLX")==2||r.get("CFLX")==3){// 单据类型为1的表示药品
						fyyfArry.push(r.get("YFSB"));
					}
					if(r.get("msg")){
						MyMessageTip.msg("提示",r.get("msg"),true);
						this.module.list.grid.startEditing(i,7);
						return false;
					}
					if(!r.get("YSDM")){
						MyMessageTip.msg("提示","开单医生不能为空！错误行 "+(i+1)+" 。",true);
						this.module.list.grid.getSelectionModel().select(i,4);
						this.module.list.onRowClick();
						this.formModule.form.getForm().findField("YSDM").focus();
						return false;
					}
					if(!r.get("KSDM")){
						MyMessageTip.msg("提示","开单科室不能为空！错误行 "+(i+1)+" 。",true);
						this.module.list.grid.getSelectionModel().select(i,4);
						this.module.list.onRowClick();
						this.formModule.form.getForm().findField("KSDM").focus();
						return false;
					}
				}
				// 判断是否有效的发药窗口
				if(this.QYFYCK!=='0'&&!callBack){
					var ret_fy=phis.script.rmi.miniJsonRequestSync({
								serviceId:"clinicChargesProcessingService",
								serviceAction:"loadOpenPharmacyWin",
								body:fyyfArry
							});
					if(ret_fy.code>300){
						this.processReturnMsg(ret_fy.code,ret_fy.msg);
						return;
					}
					var body=ret_fy.json.body;
					this.QYFYCK=body.QYFYCK;
					if(this.QYFYCK=='1'){// 启用发药窗口
						if(body.warnMsg){
							Ext.Msg.confirm("提示",body.warnMsg+"是否继续结算？",function(btn){
										if(btn=='yes'){
											this.doJs(null,null,true)// 回调本方法
										}
									},this);
							return;
						}
					}
				}
				// 物资
				var wzbody={};
				wzbody["GHGL"]=this.MZXX.GHGL;
				wzbody["GHKS"]=this.MZXX.GHKS;
				var ret_wz=phis.script.rmi.miniJsonRequestSync({
							serviceId:"configLogisticsInventoryControlService",
							serviceAction:"verificationWPJFBZ",
							body:wzbody
						});
				if(ret_wz.code>300){
					this.processReturnMsg(ret_wz.code,ret_wz.msg);
					return;
				}
				var form=this.formModule.form.getForm();
				var zdmc=form.findField("JBMC").getValue();
				this.MZXX.ZDMC=zdmc;
				if(this.MZXX.BRXZ==2000 && isAllSelfPay){
					this.MZXX.BRXZ=1000;
				}
				
				//孕妇减免
				debugger;
				if(this.systemParams.YFJKJM == '1'){
					if(typeof this.YCFRecords == "undefined" || this.YCFRecords == null ||this.YCFRecords.length == 0 ){
						this.MZXX.YCFJMJE = 0;
						var store=this.module.list.grid.getStore();
						var newData = store.reader.jsonData.body;
						var oneYjxh = null;
						if(newData.length > 0){
							debugger;
							var yjxh = null;
							for(var i=0;i<newData.length;i++){
								if(newData[i].YJXH != null){
									oneYjxh = newData[i].YJXH;
									break;
								}							
							} 
						}
						if(typeof oneYjxh != "undefined" && oneYjxh != null){
							var ret =phis.script.rmi.miniJsonRequestSync({
								serviceId:"phis.plwService",
								serviceAction:"queryIsPLW",
								method:"execute",
								body:{"oneYjxh":oneYjxh}
							});
							
							if(ret.code < 300){
								debugger;
								var body = ret.json.body;
								if(typeof body != "undefined" && body != null && body.length > 0){
									this.YCFRecords = [];
									var ycfList = ['2062','2156','2068','2069','2081','2100','2101','2099','2098','2080','2301','16754','2126','12864'];
									var newStore = [];											
									for(var i=0;i<newData.length;i++){
										var v = "";
										if(typeof newData[i].YPXH != "undefined" && newData[i].YPXH != null){
											v = newData[i].YPXH.toString();
										}
										var yjxh = "";
										if(typeof newData[i].YJXH != "undefined" && newData[i].YJXH != null){
											yjxh = newData[i].YJXH.toString();
										}
												
										if(ycfList.indexOf(v) != -1 && body.indexOf(yjxh) != -1){
											debugger;
											this.YCFRecords.push(newData[i]);
										}
									}	
									
									if(this.YCFRecords.length > 0){
										debugger;
										this.getYCFJMList(this.YCFRecords, data);							
									}else{
										this.doPopJsWin(data);
									}	
								}else{
									this.doPopJsWin(data);
								}
							}
						}else{
							this.doPopJsWin(data);
						}
					}else{
						this.getYCFJMList(this.YCFRecords, data);	
					}
				}else{
					this.doPopJsWin(data);
				}
			},
					
			doPopJsWin:function(data){	
				debugger;
				if (this.isGP(this.MZXX.BRID,data)) {
					if(!this.gpselected){
						this.showJYModule(data);
						return;
					}
				}else{
					this.GPRecords = null;
					this.gpselected = false;
				}
				
				var ybdata=[];
				
				//if(this.MZXX.BRXZ==2000){
				if (this.GPRecords && this.GPRecords.length>0) {
					for (var i = 0; i < data.length; i++) {
						var isexists = false;
						var fyxhcount = 0;
						for (var j = 0; j < this.GPRecords.length; j++) {
							var record = this.GPRecords[j];						
							if (record.data.FYXH == (data[i].FYXH || data[i].YPXH)) {
								fyxhcount ++;
							}
						}
						if(fyxhcount>1){
								this.gpselected = false;
								this.doPopJsWin(data);
								return MyMessageTip.msg("提示", "同一种项目【" + data[i].YPMC + "】只能选择一个服务包进行减免，请重新选择！！", true);
						}
						for (var j = 0; j < this.GPRecords.length; j++) {
							var record = this.GPRecords[j];
							if (record.data.SERVICETIMES > 0 && record.data.FYXH == (data[i].FYXH || data[i].YPXH)) {
								if (record.data.SERVICETIMES < data[i].YPSL) {
									this.gpselected = false;
									this.doPopJsWin(data);
									return MyMessageTip.msg("提示", "该病人【" + record.data.FYMC + "】家医减免次数剩余不足，请重新开单！！", true);
								}
								isexists = true;
								break;
							}
						}
						if(!isexists){
							ybdata.push(this.clone(data[i]));
						}
					}
				}
				else{
					if(ybdata.length == 0){
						for (var i = 0; i < data.length; i++) {
							ybdata.push(this.clone(data[i]));
						}
					}
				}
				
				//孕产妇减免
				var YCFRecords = this.YCFRecords;
				if(typeof YCFRecords != "undefined" && YCFRecords != null && this.systemParams.YFJKJM == '1'){
					for(var i = 0; i < YCFRecords.length; i++){
						for(var j = 0; j < ybdata.length; j++){
							debugger;
							if(YCFRecords[i].YPXH == ybdata[j].YPXH){
								ybdata.splice(j, 1);
							}
						}
					}
				}

				
				if(ybdata.length==0 && this.MZXX.BRXZ==2000){
					this.MZXX.BRXZ=1000;
				}	
					
					
				//}
				var form=this.formModule.form.getForm();
				var store=this.module.list.grid.getStore();
				if(this.MZXX.BRXZ&&this.MZXX.BRXZ==6000){
					if(this.opener.mzjsnhdk&&this.opener.mzjsnhdk=="1"){
						// 进行农合登记和预结算
						var nh=phis.script.rmi.miniJsonRequestSync({
									serviceId:"xnhService",
									serviceAction:"mzdjandyjs",
									MZXX:this.MZXX,
									body:data
								});
						this.panel.el.unmask();
						this.opener.brlist.el.unmask();
						if(nh.code>300){
							this.processReturnMsg(nh.code,nh.msg);
							return;
						}else{
							if(nh.json&&nh.json.NHYJSXX){
								this.MZXX.NHYJSXX=nh.json.NHYJSXX;
							}
							this.midiModules["jsModule"]=null;
							var module=this.createModule("jsModule",this.jsModule);
							this.midiModules["jsModule"]=module;
							module.opener=this
							module.on("settlement",this.doQx,this);
							var win=module.getWin();
							win.add(module.initPanel());
							module.setData(data,this.MZXX);
							/*****begin 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
							win.setSize(350,480);
							/*****end 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
							win.show();
						}
					}else{
						// 弹出农合刷卡界面
						this.doMzjsnhdk();
					}
				}else{
					//var brxz=form.findField("BRXZ").getValue();
					// yx-南京金保业务流程
					if(this.MZXX.BRXZ&&this.MZXX.BRXZ==2000){
						var njjbbody={};
						njjbbody.USERID=this.mainApp.uid;
						njjbbody.empiId=this.MZXX.EMPIID;
						// 获取业务周期号
						var ret=phis.script.rmi.miniJsonRequestSync({
									serviceId:"phis.NjjbService",
									serviceAction:"getywzqh",
									body:njjbbody
								});
						if(ret.code<=300){
							this.ywzqh=ret.json.YWZQH;
						}else{
							Ext.Msg.alert("获取业务周期号失败！请确认是否签到。")
							return;
						}
						// 判断弹出读卡界面
						if(this.opener.mzjsjbdk&&this.opener.mzjsjbdk=="1"){
							this.opener.mzjsjbdk="0";
							var djbody={};
							var ybmc=this.formModule.form.getForm().findField("YBMC");
							var ysdm=this.formModule.form.getForm().findField("YSDM");
							var ksdm=this.formModule.form.getForm().findField("KSDM");
							if(!ybmc.getValue()){
								MyMessageTip.msg("提示","请录入医保疾病！",true);
								return;
							}
							var ybmctemp=ybmc.getValue();
							if((ybmctemp+"").indexOf(",")>0){
								ybmctemp=(ybmctemp+"").substring(0,(ybmctemp+"").indexOf(","));
							}
							djbody.YBJBBM=ybmctemp;
							djbody.YSDM=ysdm.getValue();
							djbody.YSMC=ysdm.getRawValue();
							djbody.KSDM=ksdm.getValue();
							djbody.ICD10=this.MZXX.ICD10;
							// 获取卡信息
							var getkxx=phis.script.rmi.miniJsonRequestSync({
										serviceId:"phis.NjjbService",
										serviceAction:"getnjjbkxx",
										body:njjbbody
									});
							if(getkxx.code<=300){
								if(getkxx.json&&getkxx.json.jbkxx){
									djbody.SHBZKH=getkxx.json.jbkxx.SHBZKH;
									djbody.JBR=this.mainApp.uid;
									djbody.LXDH=getkxx.json.jbkxx.LXDH;
								}else{
									MyMessageTip.msg("提示","获取医保信息失败！",true);
									this.running=false;
									return;
								}
							}else{
								MyMessageTip.msg("提示","获取医保信息失败！",true);
								this.running=false;
								return;
							}
							// 获取流水号
							var body={};
							body.ywlx="1";// 挂号
							body.SBXH=this.MZXX.GHGL;
							var getlsh=phis.script.rmi.miniJsonRequestSync({
										serviceId:"phis.NjjbService",
										serviceAction:"getnjjblshbyqx",
										body:body
									});
							if(getlsh.code>=300){
								MyMessageTip.msg("提示",getlsh.msg,true);
								return;
							}else{
								djbody.NJJBLSH=getlsh.json.lsh.NJJBLSH;
								djbody.GHSJ=getlsh.json.lsh.GHSJ;
								djbody.YLLB=getlsh.json.lsh.NJJBYLLB;
								djbody.YBZY=getlsh.json.lsh.YBZY==null?"":getlsh.json.lsh.YBZY;
								djbody.YBJBBM=getlsh.json.lsh.YBMC==null?"":getlsh.json.lsh.YBMC;
							}
							this.addPKPHISOBJHtmlElement();
							this.drinterfaceinit();
							// 先撤销处方明细
							var njjbcxmx={};
							njjbcxmx.LSH=djbody.NJJBLSH;
							njjbcxmx.CFH=djbody.NJJBLSH+"-0"+getlsh.json.JSCS;
							njjbcxmx.CFLSH="";
							njjbcxmx.JBR=this.mainApp.uid;
							var str=this.buildstr("2320",this.ywzqh,njjbcxmx);
							var drre=this.drinterfacebusinesshandle(str);
							var arr=drre.split("^")
							if(arr[0]==0||(arr[0]=="-2"&&arr[3].indexOf("没有需要删除")>-1)){
								// 清除费用明细成功
							}else{
								MyMessageTip.msg("撤销处方明细提示",drre,true);
								return;
							}
//							yx-注销-不去修改登记信息
//							if(getlsh.json.JSCS==0){
//								//如果一次没结算就去修改登记信息
//								var djstr=this.buildstr("2230",this.ywzqh,djbody);
//								var drdjre=this.drinterfacebusinesshandle(djstr);
//								var djarr=drdjre.split("^");
//								if(djarr[0]=="0"){
//									// 修改登记信息
//								}else{
//									MyMessageTip.msg("南京金保修改登记信息提示",drdjre,true);
//									return;
//								}
//								djarr="";
//							}
							// yx-上传费用明细-b
							var getsfmx=phis.script.rmi.miniJsonRequestSync({
										serviceId:"phis.NjjbService",
										serviceAction:"getnjjbsfmx",
										body:ybdata
									});
							var sfmxdata={};
							sfmxdata.YSDM=djbody.YSDM;
							sfmxdata.KSDM=djbody.KSDM;
							sfmxdata.YBJBBM=djbody.YBJBBM;
							sfmxdata.JBR=njjbcxmx.JBR;
							sfmxdata.NJJBLSH=djbody.NJJBLSH;
							sfmxdata.CFH=djbody.NJJBLSH+"-0"+getlsh.json.JSCS;
							if(getsfmx.code<=300){
								if(getsfmx.json.fyxxlist){
									var sfmxl=getsfmx.json.fyxxlist;									
									var le=6;//分批上传分组数
									var size=sfmxl.length/le;
									if(sfmxl.length%le>0){
										size=size+1;
									}
									size=Math.floor(size);
									var limit=0;
									for(var y=1;y<=size;y++){
										var sfmxs=[];
										for(var z=0;z<le&&limit<sfmxl.length;z++){
											var o=sfmxl[(y-1)*le+z];
											o.limit=limit;
											sfmxs.push(o)
											limit++;
										}
										sfmxdata.sfmx=sfmxs;
										var cfxxstr=this.buildstr("2310",this.ywzqh,sfmxdata);
										// yx-上传费用明细-e
										var cfre=this.drinterfacebusinesshandle(cfxxstr);
										var cfmxarr=cfre.split("^");
										if(cfmxarr[0]=="0"){
											// 上传成功
										}else{
											MyMessageTip.msg("上传费用明细提示",cfre,true);
											return;
										}
									}
								}else{
									MyMessageTip.msg("提示","无可收费信息！",true);
									return;
								}
							}else{
								MyMessageTip.msg("提示",getsfmx.msg,true);
								return;
							}
							// yx-预结算-b
							var yjsxx={};
							yjsxx.NJJBLSH=djbody.NJJBLSH;
							yjsxx.DJH="";
							yjsxx.YLLB=djbody.YLLB;
							yjsxx.JSRQ=new Date();
							yjsxx.CYRQ=new Date();
							yjsxx.CYYY="";// 出院原因
							if(djbody.YBZY&&djbody.YBZY.length>0){
								yjsxx.CYYY="03";
							}
							yjsxx.CYZDJBBM=djbody.YBJBBM;// 出院诊断疾病编码
							yjsxx.YJSLB="";// 月结算类别
							yjsxx.ZTJSBZ="0";// 中途结算标志
							yjsxx.JBR=djbody.JBR;// 经办人
							yjsxx.FMRQ=""// 分娩日期
							yjsxx.CC="";// 产次
							yjsxx.TRS="";// 胎儿数
							yjsxx.SHBZKH=djbody.SHBZKH;// 社会保障卡号
							yjsxx.ZYYYBH=djbody.YBZY// 转院医院编号
							yjsxx.KSDM=djbody.KSDM;// 科室编码
							yjsxx.YSDM=djbody.YSDM;// 医生编码
							yjsxx.SFWGHFJS="0";// 是否为挂号费结算
							yjsxx.ZSESHBZKH="";// 准生儿社会保障卡号
							yjsxx.SSSFCHBZ="1"// 手术是否成功标志
							var yjsxxstr=this.buildstr("2420",this.ywzqh,yjsxx);
							var reyjsxx=this.drinterfacebusinesshandle(yjsxxstr);
							var yjsarr=reyjsxx.split("^");
							var njjbyjsxx={};
							if(yjsarr[0]==0){
								var reyjs=yjsarr[2].split("|");
								njjbyjsxx.BCYLFZE=reyjs[0];// 本次医疗费总额
								njjbyjsxx.BCTCZFJE=reyjs[1];// 本次统筹支付金额
								njjbyjsxx.BCDBJZZF=reyjs[2];// 本次大病救助支付
								njjbyjsxx.BCDBBXZF=reyjs[3];// 本次大病保险支付
								njjbyjsxx.BCMZBZZF=reyjs[4];// 本次民政补助支付
								njjbyjsxx.BCZHZFZE=reyjs[5];// 本次帐户支付总额
								njjbyjsxx.BCXZZFZE=reyjs[6];// 本次现金支付总额
								njjbyjsxx.BCZHZFZF=reyjs[7];// 本次帐户支付自付
								njjbyjsxx.BCZHZFZL=reyjs[8];// 本次帐户支付自理
								njjbyjsxx.BCXJZFZF=reyjs[9];// 本次现金支付自付
								njjbyjsxx.BCXJZFZL=reyjs[10];// 本次现金支付自理
								njjbyjsxx.YBFWNFY=reyjs[11];// 医保范围内费用
								njjbyjsxx.ZHXFHYE=reyjs[12];// 帐户消费后余额
								njjbyjsxx.DBZBZBM=reyjs[13];// 单病种病种编码
								njjbyjsxx.SMXX=reyjs[14];// 说明信息
								njjbyjsxx.YFHJ=reyjs[15];// 药费合计
								njjbyjsxx.ZLXMFHJ=reyjs[16];// 诊疗项目费合计
								njjbyjsxx.BBZF=reyjs[17];// 补保支付
								njjbyjsxx.YLLB=reyjs[18];// 医疗类别
								njjbyjsxx.BY6=reyjs[19];// 备用6

							}else{
								MyMessageTip.msg("南京金保预结算提示",yjsarr,true);
								return;
							}
							njjbyjsxx.NJJBYJSXX=yjsxx;
							this.MZXX.njjbyjsxx=njjbyjsxx;
							// yx-预结算-e
						}else{// 没有读卡的流程
							this.doMzjsjbdk(this.ywzqh);
							return;
						}
					}

			this.gpselected = false;		
			this.sbxhlist = new Array()
			this.getGpXTCS(this.MZXX.EMPIID);
			for (var i = 0; i < store.getCount(); i++) {
				var r = store.getAt(i);
				//if (r.data.YPXH == this.JYFY) {
				if (this.JYFY.indexOf(r.data.YPXH) != -1) {					
					// this.logOnGP(this.JSXX.BRID , fphm.getValue());
					this.isGPBR = true
					this.sbxhlist[i] = r.data.SBXH;
				}
			}
			//console.log(this.isGP(this.MZXX.BRID))
			//显示家医减免信息，选中后进行减免
			//if (this.isGP(this.MZXX.BRID,data)) {
			//	this.showJYModule(data);
			//} else {
			debugger;
				this.showJSModule(data);
			//}
		}
	},
	
	
	getYCFJMList : function(input, oriData) {// 下面右边的列表
		debugger;
		var store = new Ext.data.ArrayStore({fields:['YPMC','YFDW','YFGG','YPDJ','YPSL','HJJE']});		
		var data = [];
		this.ycfjmPanelHeight = 500;
		for(var i=0;i<input.length;i++){
			var row = [];
			row.push(input[i].YPMC);
			row.push(input[i].YFDW);
			row.push(input[i].YFGG);
			row.push(input[i].YPDJ);
			row.push(input[i].YPSL);
			row.push(input[i].HJJE);
			data.push(row);
		}				
		store.loadData(data);
		 var list = new Ext.grid.GridPanel({
				store: store,
				columns: [
							 {
								 id :'YPMC',
								 header : '名称', 
								 width    : 230, 
		 						 sortable : true, 
		 						 dataIndex: 'YPMC'
							 },{
								 id :'YFDW',
								 header : '单位', 
								 width    : 50, 
		 						 sortable : true, 
		 						 dataIndex: 'YFDW'
							 },{
								 id :'YFGG',
								 header : '规格', 
								 width    : 50, 
		 						 sortable : true, 
		 						 dataIndex: 'YFGG'
							 },{
								 id :'YPDJ',
								 header : '单价', 
								 width    : 50, 
		 						 sortable : true, 
		 						 dataIndex: 'YPDJ'
							 },{
								 id :'YPSL',
								 header : '数量', 
								 width    : 50, 
		 						 sortable : true, 
		 						 dataIndex: 'YPSL'
							 },{
								 id :'HJJE',
								 header : '金额', 
								 width    : 60, 
		 						 sortable : true, 
		 						 dataIndex: 'HJJE'
							 }
						]
			 });
			
		 	var qd = false;
		 	var obj = this;
			var win = new Ext.Window({
				id : this.id,
				title : '孕产妇减免项目',
				width : 500,
				height: this.ycfjmPanelHeight,
				iconCls : 'icon-grid',
				shim : true,
				layout : "fit",
				animCollapse : true,
				constrain : true,
				resizable : false,
				constrainHeader : true,
				shadow : true,
				closable:false,
				modal:true,
				items : list,
				buttons : [{
					text : "确定",
					handler : function() {
						debugger;
						win.hide();
						obj.doPopJsWin(oriData);
					}
				}]
			});
			win.show();
	},
	
	
	showJSModule:function(data){
		var module=this.midiModules["jsModule"];
		if(!module){
			module=this.createModule("jsModule",this.jsModule);
			this.midiModules["jsModule"]=module;
			var sm=module.initPanel();
			module.opener=this;
			module.on("settlement",this.doQx,this);
			module.on("settlementFinish",this.doSettlementFinish,this);
		}
		var win=module.getWin();
		//todo 记录对应减免的医技序号 如果是医保病人，不进
		//根据次数 - 数量还是一单减一次？
		if (this.GPRecords && this.GPRecords.length>0) {
			var fphm = this.formModule.form.getForm().findField("FPHM").getValue();
			for (var i = 0; i < this.GPRecords.length; i++) {
				var record = this.GPRecords[i];
				var sycs = record.data.SERVICETIMES;
				for (var j = 0; j < data.length; j++) {
					if (record.data.SERVICETIMES > 0 && record.data.FYXH == (data[j].FYXH || data[j].YPXH)) {
						if (record.data.SERVICETIMES < data[j].YPSL) {
							return MyMessageTip.msg("提示", "该病人【" + record.data.FYMC + "】家医减免次数剩余不足，请重新开单！！", true);
						}
						record.data.SERVICETIMES = record.data.TOTSERVICETIMES - record.data.SERVICETIMES + Number(data[j].YPSL);//签约总次数-剩余服务次数+本次消耗次数
						sycs = sycs-data[j].YPSL;
						if(sycs<0){
						  break;	
						}else{
							record.data.COSTTIMES = data[j].YPSL;//消耗次数
							record.data.FPHM = fphm;
							record.data.HJJE = data[j].HJJE;
							//此处暂取免费
							debugger;
							data[j].YPDJ = 0;
							data[j].ZFBL = 0;
							data[j].HJJE = 0;
							data[j].JYLYJM = 1;//家医履约减免标识
							data[j].JYLYJMJE = record.data.HJJE;//家医履约减免金额
						}
					}
				}
			}
		}
		
		//孕产妇减免
		debugger;
		if(typeof this.YCFRecords != "undefined" && this.YCFRecords != null && this.systemParams.YFJKJM == '1'){
			for(var i=0;i<data.length;i++){
				for(var j=0;j<this.YCFRecords.length;j++){
					var v = data[i].YPXH;
					if(data[i].YPXH == this.YCFRecords[j].YPXH && data[i].SBXH == this.YCFRecords[j].SBXH){
						this.MZXX.YCFJMJE += data[i].HJJE;
						data[i].YCFJMJE = data[i].HJJE
						data[i].YPDJ = 0;
						data[i].ZFBL = 0;
						data[i].HJJE = 0;
					}
				}
			}
			this.YCFRecords = null;
		}


		
		
		module.setData(data,this.MZXX);
		/*****begin 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
		win.setSize(350,480);
		/*****end 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
		win.show();
	},
	doSettlementFinish:function(){
		/****begin 家医系统相关业务********************/
		//this.getGpParms();
		if (this.isGPBR) {
			this.logOnGP(this.MZXX.BRID, this.sbxhlist);
		}
		if (this.GPRecords && this.GPRecords.length>0) {
			this.updateServiceTimes(this.GPRecords);
		}
		/****end 家医系统相关业务********************/
	},
	unlock:function(){
		if(this.MZXX){
			var p={};
			p.YWXH='1003';
			p.BRID=this.MZXX.BRID;
			p.BRXM=this.MZXX.BRXM;
			if(!this.bclUnlock(p))
				return
		}
	},
	doQx:function(){
		var store=this.module.list.grid.getStore();
		if(store.getCount()>0){
			var r=store.getAt(0);
			if(r.data.YPXH){
				if(this.MZXX.mxsave){
					Ext.Msg.confirm("确认","当前收费明细已发生变化,是否预保存当前信息?",function(btn){
						if(btn=='yes'){
							this.doSave();
						}else{
							this.MZXX.mxsave=false;
							this.doQx();
						}
					},this);
					return;
				}
			}
		}
		// add by yangl 增加业务锁
		this.unlock();
		this.clearYbxx();
		this.formModule.doNew();
		var ksdm=this.formModule.form.getForm().findField("KSDM");
		var ysdm=this.formModule.form.getForm().findField("YSDM");
		ksdm.disable();
		ysdm.disable();
		this.module.list.clear();
		this.getFphm();
		//2019-06-12 zhaojian 医保病人结算医保和自费分离		
		this.opener.cfList.isExistsNopayJsd = this.MZXX.isExistsNopayJsd==undefined?false:this.MZXX.isExistsNopayJsd;			
		this.opener.cfList.jzkhNopayJsd = this.MZXX.JZKH==undefined?"":this.MZXX.JZKH;	
		this.MZXX=false;
		this.module.list.totalCount();
		this.opener.cfList.loadData();
		this.module.module.form.setSYBRMsg();
		this.opener.mzjsnhdk="";// 重置农合读卡标志
	},
	showCFD:function(MZXX){
		this.MZXX=MZXX;
		var module=this.midiModules["cfdModule"];
		if(!module){
			module=this.createModule("cfdModule",this.cfdList);
			this.midiModules["cfdModule"]=module;
			module.opener=this
			module.MZXX=MZXX;
			var sm=module.initPanel();
			var win=module.getWin();
			win.add(sm)
			win.show();
		}else{
			module.MZXX=MZXX;
			var win=module.getWin();
			module.loadData();
			win.show();
		}
	},
	doCancel:function(){
		var win=this.getWin();
		if(win)
			win.hide();
	},
	createButton:function(){
		if(this.op=='read'){
			return [];
		}
		var actions=this.actions;
		var buttons=[];
		if(!actions){
			return buttons;
		}
		if(this.butRule){
			var ac=util.Accredit;
			if(ac.canCreate(this.butRule)){
				this.actions.unshift({
					id:"create",
					name:"新建"
				});
			}
		}
		var res=phis.script.rmi.miniJsonRequestSync({
			serviceId:"publicService",
			serviceAction:"loadSystemParams",
			body:{
				// 私有参数
				privates:['SFQYJKJSAN']
			}
		});
		var code=res.code;
		var msg=res.msg;
		if(code>=300){
			this.processReturnMsg(code,msg);
			return;
		}
		for(var i=0;i<actions.length;i++){
			var action=actions[i];
			if(action.id=='newPhysical'&&res.json.body&&res.json.body.SFQYJKJSAN=='0'){
				continue;
			}
			var btn={};
			btn.cmd=action.id;
			btn.text=action.name;
			btn.iconCls=action.iconCls||action.id;
			btn.script=action.script;
			btn.handler=this.doAction;
			btn.prop={};
			Ext.apply(btn.prop,action);
			Ext.apply(btn.prop,action.properties);
			btn.scope=this;
			buttons.push(btn);
		}
		return buttons;
	},
	// 加载收费信息到list
	setCFD:function(djs){
		this.module.list.brxz=this.formModule.MZXX.BRXZ;
		this.djs=djs;
		this.module.list.djs=djs;
		this.module.list.loadData();
		/** ********add by lizhi at 2017-06-20处理科室代码带不过去的问题*********** */
		var ksdm=this.formModule.form.getForm().findField("KSDM");
		if(ksdm&&!ksdm.getValue()&&djs&&djs[0]&&djs[0].KDKS){
			ksdm.setValue(djs[0].KDKS);
		}
		/** ********add by lizhi at 2017-06-20处理科室代码带不过去的问题*********** */
	},
	doAction:function(item,e){
		var cmd=item.cmd
		var script=item.script
		cmd=cmd.charAt(0).toUpperCase()+cmd.substr(1)
		if(script){
			$require(script,[function(){
				eval(script+'.do'+cmd+'.apply(this,[item,e])')
			},this])
		}else{
			var action=this["do"+cmd]
			if(action){
				action.apply(this,[item,e])
			}
		}
	},
	doZDCR:function(){
		if(!this.MZXX){
			MyMessageTip.msg("提示","请先调入病人信息",true);
			return;
		}
		if((!this.formModule.form.getForm().findField("KSDM").getValue())
			||(!this.formModule.form.getForm().findField("YSDM").getValue())){
			if(!this.formModule.form.getForm().findField("YSDM").getValue()){
				this.formModule.form.getForm().findField("YSDM").focus(false,200);
				MyMessageTip.msg("提示","请先选择当前医生!",true);
			}else{
				this.formModule.form.getForm().findField("KSDM").focus(false,200);
				MyMessageTip.msg("提示","请先选择当前科室!",true);
			}
			return;
		}
		this.MZXX.msg='no';
		var ret=phis.script.rmi.miniJsonRequestSync({
			serviceId:"clinicChargesProcessingService",
			serviceAction:"saveZDCR",
			body:this.MZXX
		});
		if(ret.code>300){
			this.processReturnMsg(ret.code,ret.msg,this.doZDCR);
			return;
		}else{
			if(ret.json.body){
				var list=this.module.list;
				var griddata=list.grid.store.data;
				for(var i=0;i<ret.json.body.length;i++){
					var bdata=ret.json.body[i];
					if(i==0){
						list.doJX();
						var row=list.selectedIndex;
						var rowItem=griddata.itemAt(row);
						bdata.YPZH=rowItem.get("YPZH");
						bdata.DJLY=6;
						bdata.YFSB=list.remoteDicStore.baseParams.pharmacyId
						if(!list.setMedRecordIntoList(bdata,rowItem,row)){
							return;
						}
						this.MZXX.mxsave=true;
						list.getPayProportion(bdata,rowItem);
					}else{
						list.doInsert();
						var row=list.selectedIndex;
						var rowItem=griddata.itemAt(row);
						bdata.YPZH=rowItem.get("YPZH");
						bdata.DJLY=6;
						bdata.YFSB=list.remoteDicStore.baseParams.pharmacyId
						if(!list.setMedRecordIntoList(bdata,rowItem,row)){
							return;
						}
						this.MZXX.mxsave=true;
						list.getPayProportion(bdata,rowItem);
					}
				}
				list.doInsert();
			}

				}
			},
			doCZSave:function(DJXH){
				var same=false;
				for(var i=0;i<DJXH.length;i++){
					for(var j=0;j<this.djs.length;j++){
						if(this.djs[j].DJXH==DJXH[i]){
							same=true;
						}
					}
					if(!same){
						var dj={};
						dj.CFSB=DJXH[i];
						dj.CFLX="2";
						this.djs.push(dj);
					}
				}
				this.setCFD(this.djs);
			},
			doPrintSet:function(){
				// var LODOP = getLodop();
				// LODOP.PRINT_INITA(10, 10, 950, 450, "门诊收费发票2");
				// LODOP.SET_PRINT_STYLE("FontColor", "#0000FF");
				// LODOP.SET_PRINT_PAGESIZE(0,'25.5cm','13.5cm',"")
				// LODOP.ADD_PRINT_TEXT(35, 60, 130, 25, "业务流水号");
				// LODOP.ADD_PRINT_TEXT(35, 240, 130, 25, "非盈利");
				// LODOP.ADD_PRINT_TEXT(35, 490, 150, 25, "发票号码");
				// LODOP.ADD_PRINT_TEXT(58, 30, 100, 25, "姓名");//
				// LODOP.ADD_PRINT_TEXT(58, 140, 100, 25, "性别");//性别
				// LODOP.ADD_PRINT_TEXT(58, 240, 80, 25, "结算方式");//结算方式
				// LODOP.ADD_PRINT_TEXT(58, 460, 200, 25, "保障号码");//社会保障号码
				// //非医保明细打印
				// LODOP.ADD_PRINT_TEXT(100, 10, 152, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(100, 164, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(100, 190, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(121, 10, 152, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(121, 164, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(121, 190, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(142, 10, 152, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(142, 164, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(142, 190, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(163, 10, 152, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(163, 164, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(163, 190, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(184, 10, 152, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(184, 164, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(184, 190, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(205, 10, 152, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(205, 164, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(205, 190, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(226, 10, 152, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(226, 164, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(226, 190, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(247, 10, 152, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(247, 164, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(247, 190, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(100, 305, 150, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(100, 480, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(100, 520, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(121, 305, 150, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(121, 480, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(121, 520, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(142, 305, 150, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(142, 480, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(142, 520, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(163, 305, 150, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(163, 480, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(163, 520, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(184, 305, 150, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(184, 480, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(184, 520, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(205, 305, 150, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(205, 480, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(205, 520, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(226, 305, 150, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(226, 480, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(226, 520, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(247, 305, 150, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(247, 480, 25, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(247, 520, 35, 20, "收费明细1");
				// LODOP.ADD_PRINT_TEXT(345, 120, 300, 25, "大写金额");
				// LODOP.ADD_PRINT_TEXT(345, 480, 100, 25, "金额");
				// LODOP.ADD_PRINT_TEXT(375, 60, 40, 25, "医保支付");
				// LODOP.ADD_PRINT_TEXT(375, 100, 300, 25, "备注");
				// LODOP.ADD_PRINT_TEXT(375, 165, 60, 25, "个人账户金额");
				// LODOP.ADD_PRINT_TEXT(375, 260, 60, 25, "其他医保支付");
				// LODOP.ADD_PRINT_TEXT(375, 480, 80, 25, "个人支付");
				// LODOP.ADD_PRINT_TEXT(400, 60, 180, 25, "机构");
				// LODOP.ADD_PRINT_TEXT(400, 270, 100, 25, "收费员");
				// LODOP.ADD_PRINT_TEXT(400, 400, 60, 25, "年");
				// LODOP.ADD_PRINT_TEXT(400, 465, 40, 25, "月");
				// LODOP.ADD_PRINT_TEXT(400, 510, 40, 25, "日");
				// LODOP.ADD_PRINT_TEXT(400,570,60,20,"页");//打印页
				// LODOP.ADD_PRINT_TEXT(30, 720, 130, 15, "门诊号码");
				// LODOP.ADD_PRINT_TEXT(48, 720, 130, 15, "姓名");
				// LODOP.ADD_PRINT_TEXT(66, 720, 90, 15, "项目1");
				// LODOP.ADD_PRINT_TEXT(86, 720, 90, 15, "项目1");
				// LODOP.ADD_PRINT_TEXT(104, 720, 90, 15, "费用日期");
				// LODOP.ADD_PRINT_TEXT(122, 720, 90, 15, "收费员");
				// LODOP.ADD_PRINT_TEXT(142, 720, 90, 15, "流水号");
				// LODOP.ADD_PRINT_TEXT(175, 720, 130, 20, "门诊号码");
				// LODOP.ADD_PRINT_TEXT(195, 720, 130, 20, "姓名");
				// LODOP.ADD_PRINT_TEXT(215, 720, 90, 20, "项目1");
				// LODOP.ADD_PRINT_TEXT(235, 720, 90, 20, "项目1");
				// LODOP.ADD_PRINT_TEXT(255, 720, 90, 20, "费用日期");
				// LODOP.ADD_PRINT_TEXT(275, 720, 90, 20, "收费员");
				// LODOP.ADD_PRINT_TEXT(295, 720, 90, 20, "流水号");
				// LODOP.ADD_PRINT_TEXT(175, 720, 130, 60,"作");
				// LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				// LODOP.ADD_PRINT_TEXT(235, 720, 130, 70,"废");
				// LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				// LODOP.ADD_PRINT_TEXT(335, 720, 130, 20, "门诊号码");
				// LODOP.ADD_PRINT_TEXT(355, 720, 130, 20, "姓名");
				// LODOP.ADD_PRINT_TEXT(375, 720, 90, 20, "项目1");
				// LODOP.ADD_PRINT_TEXT(395, 720, 90, 20, "项目1");
				// LODOP.ADD_PRINT_TEXT(415, 720, 90, 20, "费用日期");
				// LODOP.ADD_PRINT_TEXT(435, 720, 90, 20, "收费员");
				// LODOP.ADD_PRINT_TEXT(455, 720, 90, 20, "流水号");
				// LODOP.ADD_PRINT_TEXT(335, 720, 130, 60, "作");
				// LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				// LODOP.ADD_PRINT_TEXT(395, 720, 130, 70, "废");
				// LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				// LODOP.PRINT_SETUP();
				// LODOP.PRINT_DESIGN();
			},
			doPrint:function(){
				var LODOP=getLodop();
				var ret=phis.script.rmi.miniJsonRequestSync({
							serviceId:"clinicChargesProcessingService",
							serviceAction:"printMoth",
							fphm:"17006200053"
						});
				if(ret.code>300){
					this.processReturnMsg(ret.code,ret.msg);
					return null;
				}
				for(var i=0;i<ret.json.mzfps.length;i++){
					var mzfp=ret.json.mzfps[i];
					LODOP.SET_PRINT_STYLE("ItemType",4);
					LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
					LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT","Full-Width");
					LODOP.SET_PRINT_PAGESIZE(0,'21cm','12.7cm',"")
					LODOP.ADD_PRINT_TEXT("5mm",490,150,25,"序号"+mzfp.KSGHSL);
					LODOP.ADD_PRINT_TEXT("10mm",60,130,25,mzfp.MZXH);
					LODOP.ADD_PRINT_TEXT("10mm",240,130,25,"非盈利");
					LODOP.ADD_PRINT_TEXT("10mm",490,150,25,mzfp.FPHM);
					LODOP.ADD_PRINT_TEXT("15mm",30,100,25,mzfp.XM);//
					LODOP.ADD_PRINT_TEXT("15mm",140,100,25,mzfp.XB);// 性别
					LODOP.ADD_PRINT_TEXT("15mm",240,80,25,mzfp.JSFS);// 结算方式
					LODOP.ADD_PRINT_TEXT("15mm",460,200,25,mzfp.YLZH);// 社会保障号码
					// 非医保明细打印
					LODOP.ADD_PRINT_TEXT("30mm","2mm","60mm",20,mzfp.MXMC1);
					LODOP.ADD_PRINT_TEXT("30mm",164,25,20,mzfp.MXSL1);
					LODOP.ADD_PRINT_TEXT("30mm",190,"20mm",20,mzfp.MXJE1);
					LODOP.ADD_PRINT_TEXT("35mm","2mm",152,20,mzfp.MXMC3);
					LODOP.ADD_PRINT_TEXT("35mm",164,25,20,mzfp.MXSL3);
					LODOP.ADD_PRINT_TEXT("35mm",190,"20mm",20,mzfp.MXJE3);
					LODOP.ADD_PRINT_TEXT("40mm","2mm","60mm",20,mzfp.MXMC5);
					LODOP.ADD_PRINT_TEXT("40mm",164,25,20,mzfp.MXSL5);
					LODOP.ADD_PRINT_TEXT("40mm",190,"20mm",20,mzfp.MXJE5);
					LODOP.ADD_PRINT_TEXT("45mm","2mm","60mm",20,mzfp.MXMC7);
					LODOP.ADD_PRINT_TEXT("45mm",164,25,20,mzfp.MXSL7);
					LODOP.ADD_PRINT_TEXT("45mm",190,"20mm",20,mzfp.MXJE7);
					LODOP.ADD_PRINT_TEXT("50mm","2mm","60mm",20,mzfp.MXMC9);
					LODOP.ADD_PRINT_TEXT("50mm",164,25,20,mzfp.MXSL9);
					LODOP.ADD_PRINT_TEXT("50mm",190,"20mm",20,mzfp.MXJE9);
					LODOP.ADD_PRINT_TEXT("55mm","2mm","60mm",20,mzfp.MXMC11);
					LODOP.ADD_PRINT_TEXT("55mm",164,25,20,mzfp.MXSL11);
					LODOP.ADD_PRINT_TEXT("55mm",190,"20mm",20,mzfp.MXJE11);
					LODOP.ADD_PRINT_TEXT("60mm","2mm","60mm",20,mzfp.MXMC13);
					LODOP.ADD_PRINT_TEXT("60mm",164,25,20,mzfp.MXSL13);
					LODOP.ADD_PRINT_TEXT("60mm",190,"20mm",20,mzfp.MXJE13);
					LODOP.ADD_PRINT_TEXT("65mm","2mm","60mm",20,mzfp.MXMC15);
					LODOP.ADD_PRINT_TEXT("65mm",164,25,20,mzfp.MXSL15);
					LODOP.ADD_PRINT_TEXT("65mm",190,"20mm",20,mzfp.MXJE15);
					LODOP.ADD_PRINT_TEXT("30mm",305,"60mm",20,mzfp.MXMC2);
					LODOP.ADD_PRINT_TEXT("30mm",480,25,20,mzfp.MXSL2);
					LODOP.ADD_PRINT_TEXT("30mm",520,"20mm",20,mzfp.MXJE2);
					LODOP.ADD_PRINT_TEXT("35mm",305,"60mm",20,mzfp.MXMC4);
					LODOP.ADD_PRINT_TEXT("35mm",480,25,20,mzfp.MXSL4);
					LODOP.ADD_PRINT_TEXT("35mm",520,"20mm",20,mzfp.MXJE4);
					LODOP.ADD_PRINT_TEXT("40mm",305,"60mm",20,mzfp.MXMC6);
					LODOP.ADD_PRINT_TEXT("40mm",480,25,20,mzfp.MXSL6);
					LODOP.ADD_PRINT_TEXT("40mm",520,"20mm",20,mzfp.MXJE6);
					LODOP.ADD_PRINT_TEXT("45mm",305,"60mm",20,mzfp.MXMC8);
					LODOP.ADD_PRINT_TEXT("45mm",480,25,20,mzfp.MXSL8);
					LODOP.ADD_PRINT_TEXT("45mm",520,"20mm",20,mzfp.MXJE8);
					LODOP.ADD_PRINT_TEXT("50mm",305,"60mm",20,mzfp.MXMC10);
					LODOP.ADD_PRINT_TEXT("50mm",480,25,20,mzfp.MXSL10);
					LODOP.ADD_PRINT_TEXT("50mm",520,"20mm",20,mzfp.MXJE10);
					LODOP.ADD_PRINT_TEXT("55mm",305,"60mm",20,mzfp.MXMC12);
					LODOP.ADD_PRINT_TEXT("55mm",480,25,20,mzfp.MXSL12);
					LODOP.ADD_PRINT_TEXT("55mm",520,"20mm",20,mzfp.MXJE12);
					LODOP.ADD_PRINT_TEXT("60mm",305,"60mm",20,mzfp.MXMC14);
					LODOP.ADD_PRINT_TEXT("60mm",480,25,20,mzfp.MXSL14);
					LODOP.ADD_PRINT_TEXT("60mm",520,"20mm",20,mzfp.MXJE14);
					LODOP.ADD_PRINT_TEXT("65mm",305,"60mm",20,mzfp.MXMC16);
					LODOP.ADD_PRINT_TEXT("65mm",480,25,20,mzfp.MXSL16);
					LODOP.ADD_PRINT_TEXT("65mm",520,"20mm",20,mzfp.MXJE16);
					LODOP.ADD_PRINT_TEXT("93mm",120,300,25,mzfp.DXZJE);
					LODOP.ADD_PRINT_TEXT("93mm",480,100,25,mzfp.HJJE);
					LODOP.ADD_PRINT_TEXT("98mm",60,40,25,mzfp.JZ);
					LODOP.ADD_PRINT_TEXT("98mm",100,300,25,mzfp.BZ);
					LODOP.ADD_PRINT_TEXT("98mm",165,60,25,mzfp.GRZHZF);
					LODOP.ADD_PRINT_TEXT("98mm",260,60,25,mzfp.QTYBZF);
					//zhaojian 2019-07-18 增加家医履约减免金额
					//LODOP.ADD_PRINT_TEXT("98mm",480,80,25,mzfp.GRZF);
					LODOP.ADD_PRINT_TEXT("98mm",480,320,25,mzfp.GRZF);
					LODOP.ADD_PRINT_TEXT("103mm",60,180,25,mzfp.JGMC);
					LODOP.ADD_PRINT_TEXT("103mm",270,100,25,mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("103mm",400,60,25,mzfp.YYYY);
					LODOP.ADD_PRINT_TEXT("103mm",465,40,25,mzfp.MM);
					LODOP.ADD_PRINT_TEXT("103mm",510,40,25,mzfp.DD);
					LODOP.ADD_PRINT_TEXT("103mm",570,60,20,mzfp.PAGE);// 打印页
					if(mzfp.SFXM1&&i==0){
						LODOP.ADD_PRINT_TEXT("10mm","185mm",130,15,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("14mm","185mm",130,15,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("18mm","185mm",90,15,mzfp.SFXM1);
						LODOP.ADD_PRINT_TEXT("22mm","185mm",90,15,mzfp.XMJE1);
						LODOP.ADD_PRINT_TEXT("26mm","185mm",90,15,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("30mm","185mm",90,15,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("34mm","185mm",90,15,mzfp.MZXH);
					}
					if(mzfp.SFXM4&&i==1){
						LODOP.ADD_PRINT_TEXT("10mm","185mm",130,15,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("14mm","185mm",130,15,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("18mm","185mm",90,15,mzfp.SFXM4);
						LODOP.ADD_PRINT_TEXT("22mm","185mm",90,15,mzfp.XMJE4);
						LODOP.ADD_PRINT_TEXT("26mm","185mm",90,15,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("30mm","185mm",90,15,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("34mm","185mm",90,15,mzfp.MZXH);
					}else if(i==1){
						LODOP.ADD_PRINT_TEXT("10mm","185mm","10mm",60,"作");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
						LODOP.ADD_PRINT_TEXT("20mm","185mm","10mm",70,"废");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
					}
					if(mzfp.SFXM7&&i==2){
						LODOP.ADD_PRINT_TEXT("10mm","185mm",130,15,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("14mm","185mm",130,15,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("18mm","185mm",90,15,mzfp.SFXM7);
						LODOP.ADD_PRINT_TEXT("22mm","185mm",90,15,mzfp.XMJE7);
						LODOP.ADD_PRINT_TEXT("26mm","185mm",90,15,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("20mm","185mm",90,15,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("24mm","185mm",90,15,mzfp.MZXH);
					}else if(i==2){
						LODOP.ADD_PRINT_TEXT("10mm","185mm","10mm",60,"作");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
						LODOP.ADD_PRINT_TEXT("20mm","185mm","10mm",70,"废");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
					}
					if(mzfp.SFXM2&&i==0){
						LODOP.ADD_PRINT_TEXT("45mm","185mm",130,20,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("49mm","185mm",130,20,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("53mm","185mm",90,20,mzfp.SFXM2);
						LODOP.ADD_PRINT_TEXT("57mm","185mm",90,20,mzfp.XMJE2);
						LODOP.ADD_PRINT_TEXT("61mm","185mm",90,20,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("65mm","185mm",90,20,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("69mm","185mm",90,20,mzfp.MZXH);
					}else if(i==0){
						LODOP.ADD_PRINT_TEXT("50mm","185mm","10mm",60,"作");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
						LODOP.ADD_PRINT_TEXT("60mm","185mm","10mm",70,"废");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
					}
					if(mzfp.SFXM5&&i==1){
						LODOP.ADD_PRINT_TEXT("45mm","185mm",130,20,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("49mm","185mm",130,20,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("53mm","185mm",90,20,mzfp.SFXM5);
						LODOP.ADD_PRINT_TEXT("57mm","185mm",90,20,mzfp.XMJE5);
						LODOP.ADD_PRINT_TEXT("61mm","185mm",90,20,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("65mm","185mm",90,20,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("69mm","185mm",90,20,mzfp.MZXH);
					}else if(i==1){
						LODOP.ADD_PRINT_TEXT("50mm","185mm","10mm",60,"作");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
						LODOP.ADD_PRINT_TEXT("60mm","185mm","10mm",70,"废");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
					}
					if(mzfp.SFXM8&&i==2){
						LODOP.ADD_PRINT_TEXT("45mm","185mm",130,20,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("49mm","185mm",130,20,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("53mm","185mm",90,20,mzfp.SFXM8);
						LODOP.ADD_PRINT_TEXT("57mm","185mm",90,20,mzfp.XMJE8);
						LODOP.ADD_PRINT_TEXT("61mm","185mm",90,20,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("65mm","185mm",90,20,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("69mm","185mm",90,20,mzfp.MZXH);
					}else if(i==2){
						LODOP.ADD_PRINT_TEXT("50mm","185mm","10mm",60,"作");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
						LODOP.ADD_PRINT_TEXT("60mm","185mm","10mm",70,"废");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
					}
					if(mzfp.SFXM3&&i==0){
						LODOP.ADD_PRINT_TEXT("82mm","185mm",130,20,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("86mm","185mm",130,20,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("90mm","185mm",90,20,mzfp.SFXM3);
						LODOP.ADD_PRINT_TEXT("94mm","185mm",90,20,mzfp.XMJE3);
						LODOP.ADD_PRINT_TEXT("98mm","185mm",90,20,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("102mm","185mm",90,20,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("106mm","185mm",90,20,mzfp.MZXH);
					}else if(i==0){
						LODOP.ADD_PRINT_TEXT("90mm","185mm","10mm",60,"作");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
						LODOP.ADD_PRINT_TEXT("100mm","185mm",130,"10mm","废");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
					}
					if(mzfp.SFXM6&&i==1){
						LODOP.ADD_PRINT_TEXT("82mm","185mm",130,20,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("86mm","185mm",130,20,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("90mm","185mm",90,20,mzfp.SFXM6);
						LODOP.ADD_PRINT_TEXT("94mm","185mm",90,20,mzfp.XMJE6);
						LODOP.ADD_PRINT_TEXT("98mm","185mm",90,20,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("102mm","185mm",90,20,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("106mm","185mm",90,20,mzfp.MZXH);
					}else if(i==1){
						LODOP.ADD_PRINT_TEXT("90mm","185mm","10mm",60,"作");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
						LODOP.ADD_PRINT_TEXT("100mm","185mm",130,"10mm","废");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
					}
					if(mzfp.SFXM9&&i==2){
						LODOP.ADD_PRINT_TEXT("82mm","185mm",130,20,mzfp.MZHM);
						LODOP.ADD_PRINT_TEXT("86mm","185mm",130,20,mzfp.XM);
						LODOP.ADD_PRINT_TEXT("90mm","185mm",90,20,mzfp.SFXM9);
						LODOP.ADD_PRINT_TEXT("94mm","185mm",90,20,mzfp.XMJE9);
						LODOP.ADD_PRINT_TEXT("98mm","185mm",90,20,mzfp.FYRQ);
						LODOP.ADD_PRINT_TEXT("102mm","185mm",90,20,mzfp.SFY);
						LODOP.ADD_PRINT_TEXT("106mm","185mm",90,20,mzfp.MZXH);
					}else if(i==2){
						LODOP.ADD_PRINT_TEXT("90mm","185mm","10mm",60,"作");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
						LODOP.ADD_PRINT_TEXT("90mm","185mm",130,"10mm","废");
						LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
					}
					if(LODOP.SET_PRINTER_INDEXA(ret.json.MZHJSFDYJMC)){
						if((ret.json.FPYL+"")=='1'){
							LODOP.PREVIEW();
						}else{
							LODOP.PRINT();
						}
					}else{
						LODOP.PREVIEW();
					}
				}
			},
			doNewPhysical:function(){
				var form=this.createModule("refTjModule",this.refTjModule);
				form.on("onFormLoad",this.onFormLoad,this);
				this.AduitFormWin=form.getWin();
				this.AduitFormWin.on("show",function(){
							form.doLoad();
						},this);
				this.AduitFormWin.add(form.initPanel());
				this.AduitFormWin.setPosition(315,160);
				this.AduitFormWin.show();
			},
			onFormLoad:function(data){
				this.newPerson(data);
			},
			//读卡
			doYbdk:function(){
				var store=this.module.list.grid.getStore();
				if(!this.MZXX){
					MyMessageTip.msg("提示","请先调入病人信息",true);
					this.saveing=false;
					return;
				}
				if(store.getCount()==0){
					Ext.Msg.alert("提示","当前没有可以保存信息!");
					this.saveing=false;
					return;
				}else if(store.getCount()==1){
					var r=store.getAt(0);
					if(!r.data.YPXH){
						Ext.Msg.alert("提示","当前没有可以保存信息!");
						this.saveing=false;
						return;
					}
				}
				var n=store.getCount();
				var data=[];
				for(var i=0;i<n;i++){
					var r=store.getAt(i);
					if(!r.data["YPMC"]){
						continue;
					}
					data.push(r.data);
				}
				// 按医保中心的门诊就诊情况数据文件格式和门诊药费、项目收费明细数据文件格式生成指定文件名的数据文件
				var ret=phis.script.rmi.miniJsonRequestSync({
							serviceId:"yBService",
							serviceAction:"saveMzjzxxAndCfsh",
							body:data,
							MZXX:this.MZXX
						});
				if(ret.code>300){
					this.processReturnMsg(ret.code,ret.msg);
					return;
				}
				downloadXmlFile("c:\\njyb\\mzcfsj.xml",ret.json.xmlStr);
			},
			delYbFiles:function(){// add by lizhi 2017-07-10关闭窗口时删除医保文件
				if(this.MZXX.BRXZ&&this.MZXX.BRXZ==3000){// 删除医保结算文件
					delXmlFile("c:\\njyb\\mzcfsj.xml");
					delXmlFile("c:\\njyb\\mzjzxx.xml");
					delXmlFile("c:\\njyb\\mzjshz.xml");
				}
			},
			doMzjsnhdk:function(){
				this.panel.el.mask();
				this.opener.brlist.el.mask("正在进行农合预结算！");
				this.midiModules["jsnhdjlist"]=null;
				var jsnhdjmodule=this.createModule("jsnhdjlist","phis.application.ivc.IVC/IVC/IVC010109");
				jsnhdjmodule.on("nhdkreturn",this.doNHMzjs,this);
				jsnhdjmodule.on("close",function(){
							this.panel.el.unmask();
							this.opener.brlist.el.unmask();
						},this);
				var win=jsnhdjmodule.getWin();
				win.add(jsnhdjmodule.initPanel());
				win.show();
				jsnhdjmodule.selectRow(0);
			},
			// 金保读卡
			doMzjsjbdk:function(ywzqh){
				this.addPKPHISOBJHtmlElement();
				this.drinterfaceinit();
				var dkstr=this.buildstr("2100",ywzqh,"");
				var drdkre=this.drinterfacebusinesshandle(dkstr);
				var arr=drdkre.split("^");
				if(arr[0]=="0"){
					var canshu=arr[2].split("|")
					var data={};
					data.SHBZKH=canshu[0];//社会保障卡号
					data.XM=canshu[4];//姓名
					data.XB=canshu[5];//性别
					data.YLRYLB=canshu[6];//医疗人员类别
					data.SBXH=this.MZXX.GHGL;
					var getghxx=phis.script.rmi.miniJsonRequestSync({
								serviceId:"phis.NjjbService",
								serviceAction:"getnjjbghxx",
								body:data
							});
					if(getghxx.code<=300){
						if(getghxx.json.body){
							data.NJJBYLLB=getghxx.json.body.NJJBYLLB;
							data.SBXH=getghxx.json.body.SBXH;
							data.YBMC=getghxx.json.body.YBMC;
						}
					}else{
						this.mzjsjbdk="0";
						MyMessageTip.msg("提示：",getghxx.msg,true);
						return;
					}

				}else{
					MyMessageTip.msg("提示：",arr[3],true);
					return;
				}
				this.midiModules["jsjbdk"]=null;
				var jsjbdkmodule=this.createModule("jsjbdk","phis.application.reg.REG/REG/REG0110");
				var panel=jsjbdkmodule.initPanel();
				jsjbdkmodule.on("njjbdkreturn",this.donjjbjs,this);
				var win=jsjbdkmodule.getWin();
				win.add(panel);
				win.show();
				jsjbdkmodule.initFormData(data);
			},
			// 南京金保结算
			donjjbjs:function(){
				this.opener.mzjsjbdk="1";
				this.doJs(null,null,true);
			},
			doNHMzjs:function(cardid){
				// 读卡程序
				var dicName={
					id:"phis.dictionary.Hcnqzj"
				};
				var qzj=util.dictionary.DictionaryLoader.load(dicName);
				var di=qzj.wraper[this.mainApp.deptId];
				var ipandport=""
				if(di){
					ipandport=di.text;
				}
				if(ipandport==""){
					alert("未找到当前操作员工所在的医院的农合前置机配置！")
				}
				var body={};
				body.ipandport=ipandport;
				body.deptId=this.mainApp.deptId;
				body.operator=this.mainApp.uid;
				body.cardid=cardid;
				if(this.MZXX&&this.MZXX.GHGL){
					body.GHGL=this.MZXX.GHGL;
				}else{
					MyMessageTip.msg("提示","请选择农合病人！",true);
					return;
				}
				// 后台请求信息
				var ret=phis.script.rmi.miniJsonRequestSync({
							serviceId:"phis.xnhService",
							serviceAction:"getghxx",
							body:body
						});
				if(ret.code>300){
					this.panel.el.unmask();
					this.opener.brlist.el.unmask();
					MyMessageTip.msg("提示",ret.msg,true);
					return;
				}else{
					if(ret.json.body){
						this.opener.mzjsnhdk="1";// 门诊结算农合读卡标记
						this.doJs(null,null,true);
					}
				}
			}
			,doYycl:function(){
				this.midiModules["FsyyList"]=null;
				var FsyyModule = this.createModule("FsyyList","phis.application.reg.REG/REG/REG09");
				FsyyModule.width=880;
				FsyyModule.height=400;
				FsyyModule.opener = this;
				FsyyModule.on("hide",this.refreshlist,this)
				var win = FsyyModule.getWin();
				win.add(FsyyModule.initPanel());
				win.show();
			}
			,refreshlist:function(){
				this.opener.cfList.refresh();
			}
		});