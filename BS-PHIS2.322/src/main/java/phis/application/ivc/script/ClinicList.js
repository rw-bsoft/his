/**
 * 待发药处方列表
 * 
 * @author : caijy
 */
$package("phis.application.ivc.script")

$import("phis.script.SimpleList")
//门诊发票打印方法是doPrint
phis.application.ivc.script.ClinicList = function(cfg) {
	cfg.disablePagingTbr = true;
	// cfg.summaryable = true;
	this.queryWidth=100;
	phis.application.ivc.script.ClinicList.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.application.ivc.script.ClinicList, phis.script.SimpleList, {
	// 增加效期限制小天
	loadData : function() {
		this.requestData.serviceId = "phis."+this.serviceId;
		this.requestData.serviceAction = this.queryServiceActionID;
		phis.application.ivc.script.ClinicList.superclass.loadData.call(this);
	},
	onRowClick : function() {
		this.cndField.focus();
	},
	onDblClick : function(grid, index, e) {
		this.opener.mzjsnhdk="";//重置门诊结算农合读卡标记
		var lastIndex = grid.getSelectionModel().lastActive;
		var record = grid.store.getAt(lastIndex);
		if (record) {
			//this.opener.brlist.collapse();
			this.fireEvent("brselect", record);
		}
	},
	onBrselectByJzkh : function(JZKH) {
			var store = this.grid.getStore();
			var n = store.getCount();
			for (var i = 0; i < n; i++) {
				var r = store.getAt(i);
				if ((r.get("MZHM").toUpperCase()).indexOf(JZKH) >= 0) {
					this.grid.getSelectionModel().selectRow(i);
					var record = this.grid.store.getAt(i);
					if (record) {
						this.fireEvent("brselect", record);
					}
					return;
				}
			}
	},
	// 刚打开页面时候默认选中第一条数据
	onStoreLoadData : function(store, records, ops) {
		if(this.fphm){
			var _this = this;
			var deferFunction = function(){
				_this.doPrint(_this.fphm);
				if(_this.isExistsNopayJsd){
					//2019-06-12 zhaojian 医保病人结算医保和自费分离
					Ext.Msg.confirm("确认","当前结算病人还有单据尚未收费，要继续收费吗？",function(btn){
						if(btn=='yes'){
							_this.onBrselectByJzkh(_this.jzkhNopayJsd);
						}
					},_this);
				}
			}
			deferFunction.defer(1000);
		}
		this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
		if (records.length == 0) {
			document.getElementById("MZSF_BRLB_1").innerHTML = "共 0 条";
			this.fireEvent("noRecord", this);
			return
		}
		if (!this.selectedIndex || this.selectedIndex >= records.length) {
			this.selectRow(0);
			this.onRowClick(this.grid);
		} else {
			this.selectRow(this.selectedIndex);
			this.selectedIndex = 0;
			this.onRowClick(this.grid);
		}
		var store = this.grid.getStore();
		var n = store.getCount()
		//yx-2017-11-14-增加已收费变红-b
		var girdcount = 0;
		store.each(function(r) {
			if(r.get("ISSF")==1){
				this.grid.getView().getRow(girdcount).style.backgroundColor = '#E0FFFF';
			}
			girdcount += 1;
		}, this);
		//yx-2017-11-14-增加已收费变红-e
		document.getElementById("MZSF_BRLB_1").innerHTML = "共 " + n + " 条";	
	},// 缩短字段查询框长度
	doPrint : function(fphm) {
		var LODOP=getLodop();  
		var ret = phis.script.rmi.miniJsonRequestSync({
			serviceId : "clinicChargesProcessingService",
			serviceAction : "printMoth",
				fphm : fphm
			});
		this.fphm = false;
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg);
			return null;
		}
		//医保暂时不在本系统打发票
		if(ret.json.BRXZ=="3000"){
			return null;
		}
		for(var i = 0 ; i < ret.json.mzfps.length ; i ++){
			if(i>0){
				LODOP.NewPageA();
			}
			var mzfp = ret.json.mzfps[i];
			LODOP.SET_PRINT_STYLE("ItemType",4);
			LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			LODOP.SET_PRINT_PAGESIZE(0,'21cm','12.7cm',"")
			LODOP.ADD_PRINT_TEXT("2mm", 470, 150, 25, "序号 "+mzfp.YFFYSL);
			LODOP.SET_PRINT_STYLEA(0, "FontSize", 15);
			//LODOP.ADD_PRINT_TEXT("5mm", 490, 150, 25, "序号 "+mzfp.KSGHSL);
			LODOP.ADD_PRINT_TEXT("14mm", 60, 130, 25, mzfp.MZXH);
			LODOP.ADD_PRINT_TEXT("14mm", 240, 130, 25, "非盈利");
			LODOP.ADD_PRINT_TEXT("14mm", 490, 150, 25, mzfp.FPHM);
			LODOP.ADD_PRINT_TEXT("19mm", 30, 100, 25, mzfp.XM);//
			LODOP.ADD_PRINT_TEXT("19mm", 140, 100, 25, mzfp.XB);//性别
			//LODOP.ADD_PRINT_TEXT("19mm", 240, 80, 25, mzfp.JSFS);//结算方式
			//begin 2018-12-31 zhaojian 发票打印区分居民医保和职工医保
			if(ret.json.YLRYLB=="51"){//普通居民
				LODOP.ADD_PRINT_TEXT("19mm", 240, 120, 25, "城乡居民医疗保险");//结算方式
			}else{
				LODOP.ADD_PRINT_TEXT("19mm", 240, 80, 25, mzfp.JSFS);//结算方式
			}
			//end 2018-12-31 zhaojian 发票打印区分居民医保和职工医保
			LODOP.ADD_PRINT_TEXT("19mm", 460, 200, 25, mzfp.YLZH);//社会保障号码
			//非医保明细打印
			LODOP.ADD_PRINT_TEXT("34mm", "2mm", "60mm", 20, mzfp.MXMC1);
			LODOP.ADD_PRINT_TEXT("34mm", 164, 30, 20, mzfp.MXSL1);
			LODOP.ADD_PRINT_TEXT("34mm", 190, "20mm", 20, mzfp.MXJE1);
			LODOP.ADD_PRINT_TEXT("39mm", "2mm", 152, 20, mzfp.MXMC3);
			LODOP.ADD_PRINT_TEXT("39mm", 164, 30, 20, mzfp.MXSL3);
				LODOP.ADD_PRINT_TEXT("39mm", 190, "20mm", 20, mzfp.MXJE3);
				LODOP.ADD_PRINT_TEXT("44mm", "2mm", "60mm", 20, mzfp.MXMC5);
				LODOP.ADD_PRINT_TEXT("44mm", 164, 30, 20, mzfp.MXSL5);
				LODOP.ADD_PRINT_TEXT("44mm", 190, "20mm", 20, mzfp.MXJE5);
				LODOP.ADD_PRINT_TEXT("49mm", "2mm", "60mm", 20, mzfp.MXMC7);
				LODOP.ADD_PRINT_TEXT("49mm", 164, 30, 20, mzfp.MXSL7);
				LODOP.ADD_PRINT_TEXT("49mm", 190, "20mm", 20, mzfp.MXJE7);
				LODOP.ADD_PRINT_TEXT("54mm", "2mm", "60mm", 20, mzfp.MXMC9);
				LODOP.ADD_PRINT_TEXT("54mm", 164, 30, 20, mzfp.MXSL9);
				LODOP.ADD_PRINT_TEXT("54mm", 190, "20mm", 20, mzfp.MXJE9);
				LODOP.ADD_PRINT_TEXT("59mm", "2mm", "60mm", 20, mzfp.MXMC11);
				LODOP.ADD_PRINT_TEXT("59mm", 164, 30, 20, mzfp.MXSL11);
				LODOP.ADD_PRINT_TEXT("59mm", 190, "20mm", 20, mzfp.MXJE11);				
				LODOP.ADD_PRINT_TEXT("64mm", "2mm", "60mm", 20, mzfp.MXMC13);
				LODOP.ADD_PRINT_TEXT("64mm", 164, 30, 20, mzfp.MXSL13);
				LODOP.ADD_PRINT_TEXT("64mm", 190, "20mm", 20, mzfp.MXJE13);				
				LODOP.ADD_PRINT_TEXT("69mm", "2mm", "60mm", 20, mzfp.MXMC15);
				LODOP.ADD_PRINT_TEXT("69mm", 164, 30, 20, mzfp.MXSL15);
				LODOP.ADD_PRINT_TEXT("69mm", 190, "20mm", 20, mzfp.MXJE15);
				
				LODOP.ADD_PRINT_TEXT("34mm", 305, "60mm", 20, mzfp.MXMC2);
				LODOP.ADD_PRINT_TEXT("34mm", 480, 30, 20, mzfp.MXSL2);
				LODOP.ADD_PRINT_TEXT("34mm", 520, "20mm", 20, mzfp.MXJE2);
				LODOP.ADD_PRINT_TEXT("39mm", 305, "60mm", 20, mzfp.MXMC4);
				LODOP.ADD_PRINT_TEXT("39mm", 480, 30, 20, mzfp.MXSL4);
				LODOP.ADD_PRINT_TEXT("39mm", 520, "20mm", 20, mzfp.MXJE4);
				LODOP.ADD_PRINT_TEXT("44mm", 305, "60mm", 20, mzfp.MXMC6);
				LODOP.ADD_PRINT_TEXT("44mm", 480, 30, 20, mzfp.MXSL6);
				LODOP.ADD_PRINT_TEXT("44mm", 520, "20mm", 20, mzfp.MXJE6);
				LODOP.ADD_PRINT_TEXT("49mm", 305, "60mm", 20, mzfp.MXMC8);
				LODOP.ADD_PRINT_TEXT("49mm", 480, 30, 20, mzfp.MXSL8);
				LODOP.ADD_PRINT_TEXT("49mm", 520, "20mm", 20, mzfp.MXJE8);			
				LODOP.ADD_PRINT_TEXT("54mm", 305, "60mm", 20, mzfp.MXMC10);
				LODOP.ADD_PRINT_TEXT("54mm", 480, 30, 20, mzfp.MXSL10);
				LODOP.ADD_PRINT_TEXT("54mm", 520, "20mm", 20, mzfp.MXJE10);				
				LODOP.ADD_PRINT_TEXT("59mm", 305, "60mm", 20, mzfp.MXMC12);
				LODOP.ADD_PRINT_TEXT("59mm", 480, 30, 20, mzfp.MXSL12);
				LODOP.ADD_PRINT_TEXT("59mm", 520, "20mm", 20, mzfp.MXJE12);				
				LODOP.ADD_PRINT_TEXT("64mm", 305, "60mm", 20, mzfp.MXMC14);
				LODOP.ADD_PRINT_TEXT("64mm", 480, 30, 20, mzfp.MXSL14);
				LODOP.ADD_PRINT_TEXT("64mm", 520, "20mm", 20, mzfp.MXJE14);				
				LODOP.ADD_PRINT_TEXT("69mm", 305, "60mm", 20, mzfp.MXMC16);
				LODOP.ADD_PRINT_TEXT("69mm", 480, 30, 20, mzfp.MXSL16);
				LODOP.ADD_PRINT_TEXT("69mm", 520, "20mm", 20, mzfp.MXJE16);	
				LODOP.ADD_PRINT_TEXT("97mm", 120, 300, 25, mzfp.DXZJE);
				
				LODOP.ADD_PRINT_TEXT("97mm", 480, 100, 25, mzfp.HJJE);
				
				LODOP.ADD_PRINT_TEXT("102mm", 60, 40, 25, mzfp.JZ);
				LODOP.ADD_PRINT_TEXT("100mm", 100, 300, 20, mzfp.BZ);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 7);
				LODOP.ADD_PRINT_TEXT("102mm", 165, 60, 25, mzfp.GRZHZF);
				LODOP.ADD_PRINT_TEXT("102mm", 260, 60, 25, mzfp.QTYBZF);
				//zhaojian 2019-07-18 增加家医履约减免金额
				//LODOP.ADD_PRINT_TEXT("98mm", 480, 80, 25, mzfp.GRZF);
				LODOP.ADD_PRINT_TEXT("102mm", 480, 320, 25, mzfp.GRZF);
				
				LODOP.ADD_PRINT_TEXT("107mm", 60, 180, 25, mzfp.JGMC);
				LODOP.ADD_PRINT_TEXT("107mm", 270, 100, 25, mzfp.SFY);
				
				LODOP.ADD_PRINT_TEXT("107mm", 400, 60, 25, mzfp.YYYY);
				LODOP.ADD_PRINT_TEXT("107mm", 465, 40, 25, mzfp.MM);
				LODOP.ADD_PRINT_TEXT("107mm", 510, 40, 25, mzfp.DD);
				
				LODOP.ADD_PRINT_TEXT("107mm",570,60,20,mzfp.PAGE);//打印页
				if(mzfp.SFXM1 && i==0){
				LODOP.ADD_PRINT_TEXT("14mm", "185mm", 130, 15, mzfp.MZHM);
				LODOP.ADD_PRINT_TEXT("18mm", "185mm", 130, 15, mzfp.XM);
				LODOP.ADD_PRINT_TEXT("22mm", "185mm", 90, 15, mzfp.SFXM1);
				LODOP.ADD_PRINT_TEXT("26mm", "185mm", 90, 15, mzfp.XMJE1);
				LODOP.ADD_PRINT_TEXT("30mm", "185mm", 90, 15, mzfp.FYRQ);
				LODOP.ADD_PRINT_TEXT("34mm", "185mm", 90, 15, mzfp.SFY);
				LODOP.ADD_PRINT_TEXT("38mm", "185mm", 90, 15, mzfp.MZXH);
				}
				if(mzfp.SFXM4 && i==1){
				LODOP.ADD_PRINT_TEXT("14mm", "185mm", 130, 15, mzfp.MZHM);
				LODOP.ADD_PRINT_TEXT("18mm", "185mm", 130, 15, mzfp.XM);
				LODOP.ADD_PRINT_TEXT("22mm", "185mm", 90, 15, mzfp.SFXM4);
				LODOP.ADD_PRINT_TEXT("26mm", "185mm", 90, 15, mzfp.XMJE4);
				LODOP.ADD_PRINT_TEXT("30mm", "185mm", 90, 15, mzfp.FYRQ);
				LODOP.ADD_PRINT_TEXT("34mm", "185mm", 90, 15, mzfp.SFY);
				LODOP.ADD_PRINT_TEXT("38mm", "185mm", 90, 15, mzfp.MZXH);
				
				}else if (i==1){
					LODOP.ADD_PRINT_TEXT("10mm", "185mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("20mm", "185mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				}
				if(mzfp.SFXM7 && i==2){
				LODOP.ADD_PRINT_TEXT("14mm", "185mm", 130, 15, mzfp.MZHM);
				LODOP.ADD_PRINT_TEXT("18mm", "185mm", 130, 15, mzfp.XM);
				LODOP.ADD_PRINT_TEXT("22mm", "185mm", 90, 15, mzfp.SFXM7);
				LODOP.ADD_PRINT_TEXT("24mm", "185mm", 90, 15, mzfp.XMJE7);
				LODOP.ADD_PRINT_TEXT("30mm", "185mm", 90, 15, mzfp.FYRQ);
				LODOP.ADD_PRINT_TEXT("24mm", "185mm", 90, 15, mzfp.SFY);
				LODOP.ADD_PRINT_TEXT("28mm", "185mm", 90, 15, mzfp.MZXH);
				
				}else if (i==2){
					LODOP.ADD_PRINT_TEXT("10mm", "185mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("20mm", "185mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				}
				
                if(mzfp.SFXM2 && i==0){
					LODOP.ADD_PRINT_TEXT("49mm", "185mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("53mm", "185mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("57mm", "185mm", 90, 20, mzfp.SFXM2);
					LODOP.ADD_PRINT_TEXT("61mm", "185mm", 90, 20, mzfp.XMJE2);
					LODOP.ADD_PRINT_TEXT("65mm", "185mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("69mm", "185mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("73mm", "185mm", 90, 20, mzfp.MZXH);
                }else if(i==0){
                	LODOP.ADD_PRINT_TEXT("50mm", "185mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("60mm", "185mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }
                
                if(mzfp.SFXM5 && i==1){
					LODOP.ADD_PRINT_TEXT("49mm", "185mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("53mm", "185mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("57mm", "185mm", 90, 20, mzfp.SFXM5);
					LODOP.ADD_PRINT_TEXT("61mm", "185mm", 90, 20, mzfp.XMJE5);
					LODOP.ADD_PRINT_TEXT("65mm", "185mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("69mm", "185mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("74mm", "185mm", 90, 20, mzfp.MZXH);
                }else if(i==1){
                	LODOP.ADD_PRINT_TEXT("50mm", "185mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("60mm", "185mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }
                
                if(mzfp.SFXM8 && i==2){
					LODOP.ADD_PRINT_TEXT("49mm", "185mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("53mm", "185mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("57mm", "185mm", 90, 20, mzfp.SFXM8);
					LODOP.ADD_PRINT_TEXT("61mm", "185mm", 90, 20, mzfp.XMJE8);
					LODOP.ADD_PRINT_TEXT("65mm", "185mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("69mm", "185mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("74mm", "185mm", 90, 20, mzfp.MZXH);
                }else if(i==2){
                	LODOP.ADD_PRINT_TEXT("50mm", "185mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("60mm", "185mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }                
                
                if(mzfp.SFXM3 && i==0){
					LODOP.ADD_PRINT_TEXT("86mm", "185mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("90mm", "185mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("94mm", "185mm", 90, 20, mzfp.SFXM3);
					LODOP.ADD_PRINT_TEXT("98mm", "185mm", 90, 20, mzfp.XMJE3);
					LODOP.ADD_PRINT_TEXT("102mm", "185mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("106mm", "185mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("110mm", "185mm", 90, 20, mzfp.MZXH);				
                }else if(i==0){
                	LODOP.ADD_PRINT_TEXT("90mm", "185mm", "10mm", 60, "作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("100mm", "185mm", 130, "10mm", "废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }
                
                if(mzfp.SFXM6 && i==1){
					LODOP.ADD_PRINT_TEXT("86mm", "185mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("90mm", "185mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("94mm", "185mm", 90, 20, mzfp.SFXM6);
					LODOP.ADD_PRINT_TEXT("98mm", "185mm", 90, 20, mzfp.XMJE6);
					LODOP.ADD_PRINT_TEXT("102mm", "185mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("106mm", "185mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("110mm", "185mm", 90, 20, mzfp.MZXH);			
                }else if(i==1){
                	LODOP.ADD_PRINT_TEXT("90mm", "185mm", "10mm", 60, "作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("100mm", "185mm", 130, "10mm", "废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }                
                if(mzfp.SFXM9 && i==2){
					LODOP.ADD_PRINT_TEXT("86mm", "185mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("90mm", "185mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("94mm", "185mm", 90, 20, mzfp.SFXM9);
					LODOP.ADD_PRINT_TEXT("98mm", "185mm", 90, 20, mzfp.XMJE9);
					LODOP.ADD_PRINT_TEXT("102mm", "185mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("106mm", "185mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("110mm", "185mm", 90, 20, mzfp.MZXH);				
                }else if(i==2){
                	LODOP.ADD_PRINT_TEXT("90mm", "185mm", "10mm", 60, "作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("90mm", "185mm", 130, "10mm", "废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }
             if(i==ret.json.mzfps.length-1){
                	if(ret.json.njjbjsxx){
                		LODOP.ADD_PRINT_TEXT("70mm", "5mm", "30mm", 20, "统筹支付："+ret.json.njjbjsxx.BCTCZFJE);
						LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
						LODOP.ADD_PRINT_TEXT("70mm", "35mm", "30mm", 20, "大病救助："+ret.json.njjbjsxx.BCDBJZZF);
						LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
						LODOP.ADD_PRINT_TEXT("70mm", "65mm", "45mm", 20, "帐户支付自理："+ret.json.njjbjsxx.BCZHZFZL);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("70mm", "110mm", "45mm", 20, "帐户支付自付："+ret.json.njjbjsxx.BCZHZFZF);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
						LODOP.ADD_PRINT_TEXT("75mm", "5mm", "30mm", 20, "大病保险："+ret.json.njjbjsxx.BCDBBXZF);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("75mm", "35mm", "30mm", 20, "民政补助："+ret.json.njjbjsxx.BCMZBZZF);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("75mm", "65mm", "50mm", 20, "现金支付自付："+ret.json.njjbjsxx.BCXJZFZF);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("75mm", "110mm", "50mm", 20, "现金支付自理："+ret.json.njjbjsxx.BCXJZFZL);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("80mm", "5mm", "30mm", 20, "帐户支付："+ret.json.njjbjsxx.BCZHZFZE);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize",10);
                		LODOP.ADD_PRINT_TEXT("80mm", "35mm", "30mm", 20, "现金支付："+ret.json.njjbjsxx.BCXZZFZE);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("80mm", "65mm", "50mm", 20, "医保范围内费用："+ret.json.njjbjsxx.YBFWNFY);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("80mm", "110mm", "50mm", 20, "帐户消费后余额："+ret.json.njjbjsxx.ZHXFHYE);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		var dicName = {
            			 id : "phis.dictionary.ybyllb"
          				};
						var dic=util.dictionary.DictionaryLoader.load(dicName);
						var di;
						di = dic.wraper[ret.json.njjbjsxx.YLLB];
						var text=""
						if (di) {
							text = di.text;
						}
						text=text.substring(text.indexOf("-")+1)
               			LODOP.ADD_PRINT_TEXT("12mm", 275, 120, 25,"("+text+")");
                	}
                }    
			if (LODOP.SET_PRINTER_INDEXA(ret.json.MZHJSFDYJMC)){
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
	getCndBar : function(items) {
		var fields = [];
		if (!this.enableCnd) {
			return []
		}
		var selected = null;
		var defaultItem = null;
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if (!it.queryable) {
				continue
			}
			if (it.selected && it.selected == "true") {
				selected = it.id;
				defaultItem = it;
			}
			fields.push({
						// change "i" to "it.id"
						value : it.id,
						text : it.alias
					})
		}
		if (fields.length == 0) {
			return [];
		}
		var store = new Ext.data.JsonStore({
					fields : ['value', 'text'],
					data : fields
				});
		var combox = null;
		if (fields.length > 1) {
			combox = new Ext.form.ComboBox({
						store : store,
						valueField : "value",
						displayField : "text",
						value : selected,
						mode : 'local',
						triggerAction : 'all',
						emptyText : '选择查询字段',
						selectOnFocus : true,
						width : 70
					});
			combox.on("select", this.onCndFieldSelect, this)
			this.cndFldCombox = combox
		} else {
			combox = new Ext.form.Label({
						text : fields[0].text
					});
			this.cndFldCombox = new Ext.form.Hidden({
						value : fields[0].value
					});
		}

		var cndField;
		if (defaultItem) {
			if (defaultItem.dic) {
				defaultItem.dic.src = this.entryName + "." + it.id
				defaultItem.dic.defaultValue = defaultItem.defaultValue
				defaultItem.dic.width = 100
				cndField = this.createDicField(defaultItem.dic)
			} else {
				cndField = this.createNormalField(defaultItem)
			}
		} else {
			cndField = new Ext.form.TextField({
						width : 100,
						selectOnFocus : true,
						name : "dftcndfld",
						enableKeyEvents : true
					})
		}
		cndField.enableKeyEvents=true;
		this.cndField = cndField
		cndField.on("specialkey", this.onQueryFieldEnter, this)
		cndField.on("keyup", this.oncndKeypress, this);
		var queryBtn = new Ext.Toolbar.Button({
							text : '',
							iconCls : "refresh",
							notReadOnly : true
						})
		this.queryBtn = queryBtn
		queryBtn.on("click", this.loadData, this);
		return [combox, '-', cndField, '-', queryBtn]
	},
	oncndKeypress : function(f, e) {
		if ((e.getKey() >= 48 && e.getKey() <= 57)
				|| (e.getKey() >= 65 && e.getKey() <= 90)
				|| (e.getKey() >= 96 && e.getKey() <= 111) || e.getKey() == 8 || e.getKey() == 13
				|| e.getKey() == 32 || e.getKey() == 47 || e.getKey() == 59
				|| e.getKey() == 61 || e.getKey() == 173 || e.getKey() == 188
				|| (e.getKey() >= 190 && e.getKey() <= 192)
				|| (e.getKey() >= 219 && e.getKey() <= 222)) {
			var store = this.grid.getStore();
			var n = store.getCount()
			var JZKH = this.cndField.getValue().toUpperCase();
			var BRXM = this.cndField.getValue()
			for (var i = 0; i < n; i++) {
				var r = store.getAt(i);
				if ((r.get("MZHM").toUpperCase()).indexOf(JZKH) >= 0) {
					this.grid.getSelectionModel().selectRow(i);
					if(e.getKey() == 13){
						var record = this.grid.store.getAt(i);
						if (record) {
							this.fireEvent("brselect", record);
						}
					}
					return;
				}
				if ((r.get("BRXM").toUpperCase()).indexOf(BRXM) >= 0) {
					this.grid.getSelectionModel().selectRow(i);
					if(e.getKey() == 13){
						var record = this.grid.store.getAt(i);
						if (record) {
							this.fireEvent("brselect", record);
						}
					}
					return;
				}
			}
		}
	},
	onCndFieldSelect : function(item, record, e) {
		var tbar = this.grid.getTopToolbar()
		var tbarItems = tbar.items.items
		var itid = record.data.value
		var items = this.schema.items
		var it
		for (var i = 0; i < items.length; i++) {
			if (items[i].id == itid) {
				it = items[i]
				break
			}
		}
		var field = this.cndField
		// field.destroy()
		field.hide();
		var f = this.midiComponents[it.id]
		if (!f) {
			if (it.dic) {
				it.dic.src = this.entryName + "." + it.id
				it.dic.defaultValue = it.defaultValue
				it.dic.width = 150
				f = this.createDicField(it.dic)
			} else {
				f = this.createNormalField(it)
			}
			f.on("specialkey", this.onQueryFieldEnter, this)
			this.midiComponents[it.id] = f
		} else {
			f.on("specialkey", this.onQueryFieldEnter, this)
			// f.rendered = false
			f.show();
		}
		f.enableKeyEvents=true;
		f.on("keyup", this.oncndKeypress, this);
		this.cndField = f
		tbarItems[2] = f
		tbar.doLayout()
	},
	onQueryFieldEnter : function(f, e) {
		if (e.getKey() == e.ENTER) {
			e.stopEvent()
			var lastIndex = this.grid.getSelectionModel().lastActive;
			var record = this.grid.store.getAt(lastIndex);
			if (record) {
				//this.opener.brlist.collapse();
				this.fireEvent("brselect", record);
			}
		} else if (e.getKey() == 40) {
			e.stopEvent()
			var lastIndex = this.grid.getSelectionModel().lastActive;
			this.grid.getStore();
			var n = this.grid.store.getCount()
			if (lastIndex < n) {
				this.grid.getSelectionModel().selectRow(lastIndex + 1)
			} else {
				this.grid.getSelectionModel().selectRow(lastIndex)
			}
		} else if (e.getKey() == 38) {
			e.stopEvent()
			var lastIndex = this.grid.getSelectionModel().lastActive;
			if (lastIndex > 0) {
				this.grid.getSelectionModel().selectRow(lastIndex - 1)
			} else {
				this.grid.getSelectionModel().selectRow(lastIndex)
			}
		} else if (e.getKey() == 27){
			this.fireEvent("brCanclebrselect");
		}
	},
	totalCFS : function(v, params, data) {
		return v == null
				? '0'
				: ('<span style="font-size:14px;color:black;">共&#160;' + v + ' 条</span>');
	},
	expansion : function(cfg) {
		// 底部 统计信息,未完善
		var label = new Ext.form.Label({
			html : "<div id='MZSF_BRLB_1' align='center' style='color:blue'>共 0 条</div>"
		})
		cfg.bbar = [];
		cfg.bbar.push(label);
	}
});