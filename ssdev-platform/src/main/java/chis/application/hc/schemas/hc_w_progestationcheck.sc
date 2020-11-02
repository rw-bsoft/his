<?xml version="1.0" encoding="UTF-8"?>
<entry  entityName="chis.application.hc.schemas.hc_w_progestationcheck" alias="妻子孕前检查信息">
	<item alias="主键" id="recordId" length="16" not-null="true" type="string" pkey="true" generator="assigned" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>		
    <item id="phrId" alias="phrid" length="20" type="string" not-null="1" display="0" />
    <item id="empiId" alias="empiid" length="32" type="string" not-null="1" display="0" />
    <item id="SG" alias="身高（cm）" maxValue="250" type="int"/>
    <item id="TZ" alias="体重（kg）" maxValue="200" type="int"/>
    <item alias="体重指数" id="TZZS" type="double" fixed="true" />
    <item alias="心率" id="XL" type="int"/>
    <item alias="收缩压" id="SSY" maxValue="300" type="int"/>
    <item alias="舒张压" id="SZY" maxValue="200" type="int"/>
    <item alias="精神状态" id="JSZT" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="精神状态异常情况" id="JSYCQK" length="30" type="string"/>
    <item alias="智力" id="ZL" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="智力异常情况" id="ZLYCQK" length="30" type="string"/>
    <item alias="五官" id="WG" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="五官异常情况" id="WGYCQK" length="30" type="string"/>
    <item alias="特殊体态" id="TSTT" length="1" type="string">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="特殊体态情况" id="TSTTQK" length="30" type="string"/>
    <item alias="特殊面容" id="TSMR" length="1" type="string">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="特殊面容情况" id="TSMRQK" length="30" type="string"/>
    <item alias="皮肤毛发" id="PFMF" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="皮肤毛发异常情况" id="PFMFYCQK" length="30" type="string"/>
    <item alias="甲状腺" id="JZX" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="甲状腺异常情况" id="JZXYCQK" length="30" type="string"/>
    <item alias="肺部" id="FB" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="肺部异常情况" id="FBYCQK" length="30" type="string"/>
    <item alias="心脏节律整齐" id="XZJLZQ" length="1" type="string">
        <dic>
			<item key="0" text="是" />
			<item key="1" text="否" />
		</dic>
	</item>
    <item alias="杂音" id="XZZY" length="1" type="string">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="肝脾" id="GP" length="1" type="string">
        <dic>
			<item key="0" text="未触及" />
			<item key="1" text="触及" />
		</dic>
	</item>
    <item alias="肝脾情况" id="GPQK" length="30" type="string"/>
    <item alias="四肢脊柱" id="SZJZ" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="四肢脊柱异常情况" id="SZJZYCQK" length="30" type="string"/>
    <item alias="其他描述" id="QTMS" length="50" colspan="2" type="string"/>
    <item alias="转诊建议" id="YBJCZZJY" length="100" colspan="3" type="string"/>
    <item id="YBJCRQ" alias="一般检查日期" type="date" />
    <item id="YBJCYS" alias="一般检查医师"  length="100" type="string"/>
    <item alias="阴毛" id="YM" length="1" type="string">
	    <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="阴毛异常情况" id="YMYCQK" length="30" type="string"/>
    <item alias="乳房" id="RF" length="1" type="string">
	    <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="乳房异常情况" id="RFYCQK" length="30" type="string"/>
    <item alias="外阴" id="WY" length="1" type="string">
        <dic>
			<item key="0" text="未见异常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="外阴异常情况" id="WQKCQK" length="30" type="string"/>
    <item alias="阴道" id="YD" length="1" type="string">
        <dic>
			<item key="0" text="未见异常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="阴道异常情况" id="YDYCQK" length="30" type="string"/>
    <item alias="分泌物" id="FMW" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="分泌物异常情况" id="FMWYCQK" length="30" type="string"/>
    <item alias="宫颈" id="GJ" length="1" type="string">
        <dic>
			<item key="0" text="光滑" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="宫颈异常情况" id="GJYCQK" length="30" type="string" colspan="2"/>
    <item alias="子宫大小" id="ZGDX" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="大" />
			<item key="2" text="小" />
		</dic>
	</item>
    <item alias="子宫活动" id="ZGHD" length="1" type="string">
        <dic>
			<item key="0" text="好" />
			<item key="1" text="差" />
		</dic>
	</item>
    <item alias="子宫包块" id="ZGBK" length="1" type="string">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="子宫双侧附件" id="ZGSCFJ" length="1" type="string">
        <dic>
			<item key="0" text="未见异常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="子宫双侧附件异常情况" id="ZGSCFJYCQK" length="30" colspan="2" type="string"/>
    <item id="FKJCZZJY" alias="妇科检查转诊建议"  length="200" colspan="3" type="string"/>
    <item id="FKJCRQ" alias="妇科检查日期" type="date" colspan="2"/>
    <item id="FKJCYS" alias="妇科检查医师"  length="100" type="string"/>
    
    <item alias="妇科B超检查" id="FKBCJC" length="1" type="string">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
			<item key="2" text="不能确定" />
		</dic>
	</item>
    <item alias="妇科B超检查异常情况" id="BCJCYCQK" length="50" colspan="2" type="string"/>
    <item id="BCJCZZJY" alias="B超检查转诊建议"  length="200" colspan="2" type="string"/>
    <item id="BCJCRQ" alias="B超检查日期" type="date" />
    <item id="BCJCYS" alias="B超检查医师"  length="100" type="string"/>
    
    <item alias="线索细胞" id="XSXB" length="1" type="string">
	    <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="念珠菌感染" id="NKJGR" length="1" type="string">
        <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="滴虫感染" id="DCGR" length="1" type="string">
        <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="清洁度" id="QJD" length="1" type="string">
        <dic>
			<item key="0" text="I" />
			<item key="1" text="II" />
			<item key="2" text="III" />
			<item key="3" text="IV" />
		</dic>
	</item>
    <item alias="胺臭味实验" id="ACWSY" length="1" type="string">
        <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
		</dic>
	</item>
    <item alias="PH值" id="PHZ" length="1" type="string">
        <dic>
			<item key="0" text="小于4.5" />
			<item key="1" text="大于等于4.5" />
		</dic>
	</item>
    <item alias="HB（g╱L）" id="HB" type="double"/>
    <item alias="RBC（10^12╱L）" id="RBC" type="double"/>
    <item alias="PLT（10^9╱L）" id="PLT" type="double"/>
    <item alias="WBC（10^9╱L）" id="WBC" type="double"/>
    <item alias="N（%）" id="N" type="double"/>
    <item alias="E（%）" id="E" type="double"/>
    <item alias="B（%）" id="B" type="double"/>
    <item alias="L（%）" id="L" type="double"/>
    <item alias="M（%）" id="M" type="double"/>
    <item alias="尿液常规检查" id="NYCGJC" length="1" type="string">
        <dic>
			<item key="0" text="未见异常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="尿液检查异常情况" id="NYJCYCQK" length="50" type="string"/>
    <item alias="血型" id="XX" length="1" type="string">
        <dic>
			<item key="1" text="A型" />
			<item key="2" text="B型" />
			<item key="3" text="AB型" />
			<item key="4" text="O型" />
		</dic>
	</item>
    <item alias="RH血型" id="RHXX" length="1" type="string">
        <dic>
			<item key="0" text="阳性" />
			<item key="1" text="阴性" />
		</dic>
	</item>
    <item alias="血糖（mmol╱L）" id="XT" type="double"/>
    <item alias="梅毒螺旋体" id="MDLXT" length="1" type="string">
	<dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="淋球菌筛查" id="LQJSC" length="1" type="string">
        <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="沙眼衣原体筛查" id="SQKYTSC" length="1" type="string">
        <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="乙肝血清学" id="YGXQX" length="1" type="string">
        <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
	<item alias="HBs—Ag" id="HBSAG" length="1" type="string">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>
	<item alias="HBs—Ab" id="HBSAB" length="1" type="string">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>
	<item alias="HBe—Ag" id="HBEAG" length="1" type="string">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>
	<item alias="HBe—Ab" id="HBEAB" length="1" type="string">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>
	<item alias="HBc—Ab" id="HBCAB" length="1" type="string">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>		
    <item alias="谷丙转氨酶（U╱L）" id="GBZAM" type="double"/>
    <item alias="肌酐（umol╱L）" id="GG" type="double"/>
    <item alias="促甲状腺激素（ulU╱ml）" id="TSH" type="double"/>
    <item alias="风疹病毒IgG" id="FZBD" length="1" type="string">
	    <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="巨细胞病毒IgG" id="JXBBDIGG" length="1" type="string">
	    <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="巨细胞病毒IgM" id="JXBBDIGM" length="1" type="string">
	    <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="弓形虫IgG" id="GXCIGG" length="1" type="string">
	    <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="弓形虫IgM" id="GXCIGM" length="1" type="string">
	    <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="其他检验" id="QTJY" length="50" type="string" />
    <item id="JYJCRQ" alias="检验检查日期" type="date" colspan="2"/>
    <item id="JYJCYS" alias="检验检查医师"  length="100" type="string"/>
    <item id="QTJYZYJG" alias="其他检查主要结果" length="500" type="string" colspan="3" xtype="textarea"/>
    <item id="QTJCRQ" alias="其他检查日期" type="date" />
    <item id="QTJCYS" alias="其他检查医师"  length="100" type="string"/>    
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.manageUnit.id" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入人员" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false" 
		defaultValue="%server.date.today" queryable="true" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
		  defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
</entry>
