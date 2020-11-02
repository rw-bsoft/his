$package("phis.application.med.script");

$import("phis.script.SimpleModule", "phis.script.util.DateUtil");
phis.application.med.script.WardProjectListModule = function(cfg) {
	this.westWidth = cfg.westWidth || 250;
	/**
	 * 定义显示类型，该值共有project和patient两个选项，中Applications.xml中配置
	 * project表示按项目提交 , patient表示按病人提交
	 */
//	alert(cfg.properties.showType)
//	this.showType = cfg.showType;
	this.height = 450;
	
	var al_jgid = "";//机构ID
	var al_hsql = "";//输入科室
	var ad_today = "";//确认时间
	var al_fhbz = "";//复核标志
	phis.application.med.script.WardProjectListModule.superclass.constructor.apply(
			this, [cfg]);
},

Ext.extend(phis.application.med.script.WardProjectListModule,
		phis.script.SimpleModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		this.al_jgid = this.mainApp['phisApp'].deptId;//机构ID
		this.al_hsql = this.mainApp['phis'].wardId;//经张伟确认：输入科室是指当前登录病区
		var data = new Date();
		var tbar = [{xtype : "tbfill"}];
		this.lable = new Ext.form.Label({
			id : 'label',
			text : '',
			style : {
				'font-size' : '15',
				'color':'red'
			}
		});
		tbar.push(this.lable);
		this.al_fhbz = this.getFHBZ();//复核标志
//		this.ad_today = this.date2str(new Date(),'yyyy-mm-dd') + ' 00:00:00';//指定当前日期的0点
		this.ad_today = Date.getServerDate() + ' 00:00:00';//指定当前日期的0点
		//显示Button按钮
		var btn = new Ext.Button({
		 autoShow:'true',
		 text:'显示'
		});
		btn.on("click",this.changeRightSelect,this);
		 
		var panel = new Ext.Panel({
			border : false,//True表示为显示出面板body元素的边框，false则隐藏（缺省为true），默认下，边框是一套2px宽的内边框，但可在bodyBorder中进一步设置
			frame : true,//True表示为面板的边框外框可自定义的，false表示为边框可1px的点线（默认为false）。
			layout : 'border',
			width : this.width,
			height : this.height,
			tbar : tbar,
			items : [ {
				xtype : 'panel',
				layout : "fit",
				split : true,
				collapsible : false,//面板是否可以收缩，缺省为false
//				tbar : [{xtype : "button"},btn] ,
				region : 'west',
				width : 280,
				items : this.getShowTypeList("left")
			}, {
				layout : "fit",
				xtype : 'panel',
				split : true,
				title : '',
				region : 'center',
				width : 280,
				items : this.getShowTypeList("right")
			}]
		});
		return panel;
	},
	getShowTypeList : function(bearing){
		if(this.showType == "project"){//病区项目提交页面按项目提交
			if(bearing == "left"){//病区项目提交页面左边显示
				return this.getShowList(this.PROJECT_LEFT, "prL");
			}else{//病区项目提交页面右边显示
				return this.getShowList(this.PROJECT_RIGHT, "prR");
			}
		}else{//病区项目提交页面按病人提交
			if(bearing == "left"){//病区项目提交页面左边显示
				return this.getShowList(this.PATIENT_LEFT, "paL");
			}else{//病区项目提交页面右边显示
				return this.getShowList(this.PATIENT_RIGHT, "paR");
			}
		}
	},

	/**
	 * 创建显示列表，包括项目列表和病人列表
	 * @param showName
	 * 			创建Module名字，在Applications.xml中配置的ID。
	 *          调用createModule方法中所使用的模块
	 * @param tmp
	 * 			显示方式，该值共有4个值prL、prR、paL、paR，分别使用4个不同的schema
	 * 			prL：按项目显示左边集合
	 * 			prR：按项目显示右边集合
	 * 			paL：按病人显示左边集合
	 * 			paR：按病人显示右边集合
	 * @returns
	 */
	getShowList : function(showName, tmp){
		var module = this.createModule(tmp, showName);
		module.on("save",this.mimiJsonRequest2Return,this);
		var filterCnd ;
		if(tmp == "prL"){//按项目显示左边集合
//			module.on("changeSelect",this.changeRightSelect,this);
			module.on("initSelect",this.initSelect,this);
			module.requestData.al_hsql = this.al_hsql;
			module.requestData.al_fhbz= this.al_fhbz;
			module.requestData.al_jgid= this.al_jgid;
			module.requestData.ad_today = this.ad_today;
			module.schema = "phis.application.med.schemas.MED_YLSF_PRL";
			
			filterCnd = this.getFilterCnd(tmp);
		}else if(tmp == "prR"){//按项目显示右边集合
			filterCnd = this.getFilterCnd(tmp);
			//定义同步左右两个列表数据方法
			module.on("syncData", this.syncData, this);
		}else if(tmp == "paL"){//按病人显示左边集合
//			module.on("changeSelect",this.changeRightSelect,this);
			module.on("initSelect",this.initSelect,this);
			module.requestData.al_hsql = this.al_hsql;
			module.requestData.al_fhbz= this.al_fhbz;
			module.requestData.al_jgid= this.al_jgid;
			module.requestData.ad_today = this.ad_today;
			filterCnd = this.getFilterCnd(tmp);
		}else{//按病人显示右边集合
			filterCnd = this.getFilterCnd(tmp);
			//定义同步左右两个列表数据方法
			module.on("syncData", this.syncData, this);
		}
		var list = module.initPanel();
		if(tmp == "paR" || tmp == "prR"){
			module.requestData.cnd = filterCnd;
		}
		return list;
	},
	/**
	 * 同步左右两个列表方法
	 * 		因右边列表后台会根据数据进行过滤，所以需要将左边列表也将不需要的数据删除
	 * @param data
	 * 		右边列表数据
	 */
	syncData : function(showType, data){
		this.showTyp = showType;
		var TL1 = "" , TR2 = "";
		if(this.showTyp == "project"){
			TL1 = "prL";
			TR2 = "prR";
		}else{
			TL1 = "paL";
			TR2 = "paR";
		}
		this.midiModules[TL1].syncData(data);
	},
	/**
	 * 重置请求数据
	 */
	resetRequest : function(){
		this.al_fhbz = this.getFHBZ();//复核标志
		var TL1 = "" , TR2 = "";
		if(this.showTyp == "project"){
			TL1 = "prL";
			TR2 = "prR";
		}else{
			TL1 = "paL";
			TR2 = "paR";
		}
		var filterCnd = this.getFilterCnd(TR2);
		this.midiModules[TL1].requestData.al_fhbz= this.al_fhbz;
		this.midiModules[TR2].requestData.cnd= filterCnd;
	},
	/**
	 * 获取过滤条件
	 * @param tmp
	 * @returns
	 */
	getFilterCnd : function(tmp){
		var filterCnd ;
		if(tmp == "prL"){//按项目显示左边集合
			/**
			 * (已不用，直接通过后台sql语句查询)
			 * WHERE (ZY_BQYZ.SRKS=:al_hsql) AND 
        		(ZY_BQYZ.JFBZ=1 OR ZY_BQYZ.JFBZ=9) AND
				(ZY_BQYZ.YPXH=GY_YLSF.FYXH) AND (join中已加入)
  				(ZY_BQYZ.XMLX>3) AND
				(ZY_BQYZ.SYBZ=0) AND
        		(ZY_BQYZ.LSBZ=0 OR ZY_BQYZ.LSBZ=2) AND
				(ZY_BQYZ.YZPB=0) AND 
				( ZY_BQYZ.YSBZ = 0 OR (ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) )  AND
		  		(ZY_BQYZ.QRSJ < :ad_today OR (ZY_BQYZ.QRSJ IS NULL))	 AND 
		  		ZFBZ = 0 and
				ZY_BQYZ.JGID = :al_jgid and 
		 		( :al_fhbz = 1 or ZY_BQYZ.FHBZ = 1) 
  				ORDER BY GY_YLSF.PYDM  (通过schema设置)
			 */
			filterCnd = ['and',
                          ['eq',['$','b.SRKS'],['d',this.al_hsql]],
                          ['or',['eq',['$','b.JFBZ'],['d',1]],['eq',['$','b.JFBZ'],['d',9]]],
                          ['gt',['$','b.XMLX'],['d',3]],
                          ['eq',['$','b.SYBZ'],['d',0]],
                          ['or',['eq',['$','b.LSBZ'],['d',0]],['eq',['$','b.LSBZ'],['d',2]]],
                          ['eq',['$','b.YZPB'],['d',0]],
                          ['or',['eq',['$','b.YSBZ'],['d',0]],['and',['eq',['$','b.YSBZ'],['d',1]],['eq',['$','b.YSTJ'],['d',1]]]],
                          ['or',['le',['$',"str(b.QRSJ,'yyyy-mm-dd hh24:mi:ss')"],['s',this.ad_today]],['isNull',['$','b.QRSJ']]],
                          ['eq',['$','b.ZFBZ',['d',0]]],
                          ['eq',['$','b.JGID'],['s',this.al_jgid]],
                          ['or',['eq',['$',this.al_fhbz],['d',0]],['eq',['$','b.FHBZ'],['d',1]]]
						 ];
		}else if(tmp == "prR"){//按项目显示右边集合
			/**
			 * WHERE (ZY_BQYZ.ZYH=ZY_BRRY.ZYH AND ZY_BRRY.CYPB=0) AND   (join中已加入但ZY_BRRY.CYPB=0未加入)
         		(ZY_BQYZ.JGID=ZY_BRRY.JGID ) AND   (join中已加入)
         		(ZY_BQYZ.SRKS=:al_hsql) AND
		 		(ZY_BQYZ.JFBZ=1 OR ZY_BQYZ.JFBZ=9) AND
		 		(ZY_BQYZ.XMLX>3) AND
         		(ZY_BQYZ.SYBZ=0) AND
				(ZY_BQYZ.LSBZ=0 OR ZY_BQYZ.LSBZ=2) AND
				(ZY_BQYZ.YZPB=0) AND 
				( ZY_BQYZ.YSBZ = 0 OR (ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) ) AND
		 		(ZY_BQYZ.QRSJ < :ad_today OR (ZY_BQYZ.QRSJ IS NULL))	and
				ZY_BQYZ.JGID = :al_jgid and 
		   		( :al_fhbz = 0 or ZY_BQYZ.FHBZ = 1)
		   		---此处已用上面一行代替al_fhbz值为0或1( :al_fhbz = 1 or ZY_BQYZ.FHBZ = 1 )
			 */
			filterCnd = ['and',
                          ['eq',['$','b.CYPB'],['d',0]],
                          ['eq',['$','a.SRKS'],['s',this.al_hsql]],
                          ['or',['eq',['$','a.JFBZ'],['d',1]],['eq',['$','a.JFBZ'],['d',9]]],
                          ['gt',['$','a.XMLX'],['d','3']],
                          ['eq',['$','a.SYBZ'],['d',0]],
                          ['or',['eq',['$','a.LSBZ'],['d',0]],['eq',['$','a.LSBZ'],['d',2]]],
                          ['eq',['$','a.YZPB'],['d',0]],
                          ['or',['eq',['$','a.YSBZ'],['d',0]],['and',['eq',['$','a.YSBZ'],['d',1]],['eq',['$','a.YSTJ'],['d',1]]]],
                          ['or',['le',['$',"str(a.QRSJ,'yyyy-mm-dd hh24:mi:ss')"],['s',this.ad_today]],['isNull',['$','a.QRSJ']]],
//                          timeCnd = ['ge', ['$', "str(XZSJ,'yyyy-mm-dd')"], ['s', datefrom]];
                          ['eq',['$','a.JGID'],['s',this.al_jgid]],
                          ['or',['eq',['$',this.al_fhbz],['d',0]],['eq',['$','a.FHBZ'],['d',1]]]
						 ];
		}else if(tmp == "paL"){//按病人显示左边集合
			/**
			 * (已不用，直接通过后台sql语句查询)
			 * ZY_BQYZ b   ZY_BRRY a
			 * WHERE ( ZY_BQYZ.ZYH = ZY_BRRY.ZYH ) AND  (join中已加入)
        		( ZY_BQYZ.JGID = ZY_BRRY.JGID ) AND     (join中已加入)
				( ZY_BQYZ.SRKS = :al_hsql ) AND
		        ( ZY_BQYZ.JFBZ=1 OR ZY_BQYZ.JFBZ=9 ) AND  
		        ( ZY_BQYZ.XMLX > 3 ) AND
		        ( ZY_BQYZ.SYBZ=0) AND
			    ( ZY_BQYZ.LSBZ=0 OR ZY_BQYZ.LSBZ=2) AND
				( ZY_BQYZ.YZPB=0) AND
				( ZY_BQYZ.YSBZ = 0 OR (ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) ) AND
				( ZY_BQYZ.QRSJ < :ad_today OR (ZY_BQYZ.QRSJ IS NULL)) and
				( ZY_BQYZ.JGID = :al_jgid ) and 
				( :al_fhbz = 1 or ZY_BQYZ.FHBZ = 1)
				ORDER BY ZY_BRRY.BRCH  (通过schema设置排序)
			 */
			filterCnd = ['and',
			               ['eq',['$','b.SRKS'],['d',this.al_hsql]],
			               ['or',['eq',['$','b.JFBZ'],['d',1]],['eq',['$','b.JFBZ'],['d',9]]],
			               ['gt',['$','b.XMLX'],['d','3']],
			               ['eq',['$','b.SYBZ'],['d',0]],
			               ['or',['eq',['$','b.LSBZ'],['d',0]],['eq',['$','b.LSBZ'],['d',2]]],
                           ['eq',['$','b.YZPB'],['d',0]],
                           ['or',['eq',['$','b.YSBZ'],['d',0]],['and',['eq',['$','b.YSBZ'],['d',1]],['eq',['$','b.YSTJ'],['d',1]]]],
                           ['or',['le',['$',"str(b.QRSJ,'yyyy-mm-dd hh24:mi:ss')"],['s',this.ad_today]],['isNull',['$','b.QRSJ']]],
                           ['eq',['$','b.JGID'],['s',this.al_jgid]],
                           ['or',['eq',['$',this.al_fhbz],['d',0]],['eq',['$','b.FHBZ'],['d',1]]]
			            ];
		}else{//按病人显示右边集合
			/**ZY_BQYZ a   ZY_BRRY b
			 * WHERE (ZY_BQYZ.ZYH=ZY_BRRY.ZYH AND ( ZY_BRRY.CYPB = 0) ) AND  (join中已加入但ZY_BRRY.CYPB=0未加入)
		         (ZY_BQYZ.JGID=ZY_BRRY.JGID ) AND						(join中已加入)
			     (ZY_BQYZ.SRKS=:al_hsql) AND
		         (ZY_BQYZ.JFBZ=1 OR ZY_BQYZ.JFBZ=9) AND 
			     (ZY_BQYZ.XMLX>3) AND
		         (ZY_BQYZ.SYBZ=0) AND
		         (ZY_BQYZ.LSBZ=0 OR ZY_BQYZ.LSBZ=2) AND
				 (ZY_BQYZ.YZPB=0) AND 
				 ( ZY_BQYZ.YSBZ = 0 OR (ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) ) AND
		 		 (ZY_BQYZ.QRSJ < :ad_today OR (ZY_BQYZ.QRSJ IS NULL))  AND 
				 ( ZY_BQYZ.ZFBZ = 0 ) and 
				 ( ZY_BQYZ.JGID = :al_jgid ) and 
				 ( :al_fhbz = 0 or ZY_BQYZ.FHBZ = 1 )
				 ---此处已用上面一行代替al_fhbz值为0或1( :al_fhbz = 1 or ZY_BQYZ.FHBZ = 1 )
			 */
			filterCnd = ['and',
                         ['eq',['$','b.CYPB'],['d',0]],
                         ['eq',['$','a.SRKS'],['s',this.al_hsql]],
                         ['or',['eq',['$','a.JFBZ'],['d',1]],['eq',['$','a.JFBZ'],['d',9]]],
                         ['gt',['$','a.XMLX'],['d','3']],
                         ['eq',['$','a.SYBZ'],['d',0]],
                         ['or',['eq',['$','a.LSBZ'],['d',0]],['eq',['$','a.LSBZ'],['d',2]]],
                         ['eq',['$','a.YZPB'],['d',0]],
                         ['or',['eq',['$','a.YSBZ'],['d',0]],['and',['eq',['$','a.YSBZ'],['d',1]],['eq',['$','a.YSTJ'],['d',1]]]],
                         ['or',['le',['$',"str(a.QRSJ,'yyyy-mm-dd hh24:mi:ss')"],['s',this.ad_today]],['isNull',['$','a.QRSJ']]],
                         ['eq',['$','a.ZFBZ'],['d',0]],
                         ['eq',['$','a.JGID'],['s',this.al_jgid]],
                         ['or',['eq',['$',this.al_fhbz],['d',0]],['eq',['$','a.FHBZ'],['d',1]]]
						 ];
		}
		return filterCnd;
	},
	/**
	 * JSON同步提交请求
	 * @param sId
	 * 		  配置的ServiceID
	 * @param sAction
	 * 		  请求的方法名(例如在Service中的方法名为doQuery，此时应传入query)
	 * @param parameter
	 * 		  需要发送给服务器的参数值，可在req.get("body")获取
	 * @param showMsg
	 * 		  请求成功后弹出的提示框
	 * 				诺不传该参数则不弹出提示框
	 * 				诺传入参数，成功：弹出showMsg;  失败：弹出"execute fail"消息框
	 * @returns
	 */
	mimiJsonRequest2Return : function(sId, sAction,parameter, showMsg){
		var result = phis.script.rmi.miniJsonRequestSync({
			serviceId : sId,
			serviceAction : sAction,
			body : parameter
		});
		if(showMsg != null){
			if(result.code == 200){
				Ext.Msg.alert('状态:', showMsg);
			}else{
				Ext.Msg.alert('状态:', "执行失败!");
			}
		}
		return result;
	},
	/**
	 * 初始化
	 * @param initSelects
	 * @param showTypes
	 */
	initSelect : function(initSelects, showTypes){
//		alert("initSelects:" + initSelects.length);
		this.showTyp = showTypes;
//		alert(this.showTyp);
		if(this.showTyp == "project"){
			this.midiModules["prR"].showProjectByLeftSelect(initSelects);
		}else{
			this.midiModules["paR"].showProjectByLeftSelect(initSelects);
		}
	},
	/**
	 * 左边修改后，右边显示
	 * @param leftSelect
	 */
	changeRightSelect : function(){
		var TL1 = "" , TR2 = "";
		if(this.showTyp == "project"){
			TL1 = "prL";
			TR2 = "prR";
		}else{
			TL1 = "paL";
			TR2 = "paR";
		}
		var leftSelect = this.midiModules[TL1].changeSelect();
//		rrrrrrrrrrrrrrrrr = leftSelect;
		this.midiModules[TR2].showProjectByLeftSelect(leftSelect);
	},
	/**
	 * 从后台获取复核标志系统参数
	 */
	getFHBZ : function(){
		var fhbzTmp = "1";
		//经张伟确认：输入科室是指当前登录病区
		var result = this.mimiJsonRequest2Return("wardProjectService", "getFHBZ", this.mainApp['phis'].wardId);
		if(result.code == 200){
			fhbzTmp = result.msg;
			if(fhbzTmp == 1 && result.json.body){
				this.lable.setText('发现' + result.json.body + '个未复核医嘱，请复核');
			}else{
				this.lable.setText('');
			}
		}else{
			Ext.Msg.alert('状态:', result.msg);
		}
		return fhbzTmp;
	}
});