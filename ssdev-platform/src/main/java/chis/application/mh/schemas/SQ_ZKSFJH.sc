<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mh.schemas.SQ_ZKSFJH" alias="质控随访计划" sort="diagnosisDate"> 
	<item id="JLXH" pkey="true" alias="记录主键" type="string" length="30" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="30" startPos="1"/> 
		</key> 
	</item>  
	<item id="GRBM" alias="个人编码" type="string" length="30" fixed="true"  display="0"/>  
	<item id="EMPIID" alias="EMPIID" type="string" length="32" fixed="true"  display="0"/>  
	<item id="ZBLB" alias="指标类别" type="int" length="2" fixed="true"  display="1">
		<dic> 
			<item key="1" text="血压"/>  
			<item key="2" text="空腹血糖"/>  
			<item key="3" text="糖化血红蛋白"/>  
			<item key="4" text="血常规"/> 
			<item key="5" text="尿微量白蛋白"/> 
			<item key="6" text="尿常规"/> 
			<item key="7" text="肾功能"/> 
			<item key="8" text="血脂"/> 
			<item key="9" text="胸片"/> 
			<item key="10" text="心电图"/> 
			<item key="11" text="眼底检查"/> 
			<item key="12" text="尿比值"/> 
			<item key="13" text="尿肌酐"/> 
		</dic> 		
	</item>  
	<item id="JHSFRQ" alias="计划随访日期" type="date" display="1"/>  
	<item id="SJSFRQ" alias="实际随访日期" type="date" display="1"/>  
	<item id="WCBZ" alias="完成标志" type="int" length="2" fixed="true"  display="0">
		<dic> 
			<item key="1" text="完成"/>  
			<item key="0" text="未完成"/>  
		</dic> 		
	</item>  
	<item id="SFLB" alias="随访类别" type="int" length="2" fixed="true"  display="0">
		<dic> 
			<item key="7" text="高血压"/>  
			<item key="8" text="糖尿病"/>  
		</dic> 		
	</item>  
	<item id="KSRQ" alias="开始日期" type="date" display="0"/>  
	<item id="JSRQ" alias="结束日期" type="date" display="0"/>  
	<item id="TXRQ" alias="提醒日期" type="date" display="0"/>  
	<item id="YXBZ" alias="有效标志" type="int" length="2" fixed="true"  display="0"/>
	<item id="CZGH" alias="操作工号" type="string" length="10" fixed="true"  display="0"/>  
	<item id="LRGH" alias="录入工号" type="string" length="10" fixed="true"  display="0"/>  
	<item id="CZJD" alias="操作街道" type="string" length="10" fixed="true"  display="0"/>  
	<item id="CZTD" alias="操作团队" type="string" length="10" fixed="true"  display="0"/>  
	<item id="GXJD" alias="管辖街道" type="string" length="10" fixed="true"  display="0"/>  
	<item id="GXTD" alias="管辖团队" type="string" length="10" fixed="true"  display="0"/>  
	<item id="JLBH" alias="记录编号" type="int" length="18" fixed="true"  display="0"/>  
	<item id="YCBZ" alias="异常标志" type="int" length="1" fixed="true"  display="0"/>  
	<item id="BZXX" alias="备注信息" type="string" length="400" fixed="true"  display="0"/>  
	<item id="GLSJ" alias="管理时间" type="date" display="0"/>  
	<item id="INTIME" alias="引入日期" type="date" display="0"/>  
	<item id="JCGF" alias="检查规范" type="int" length="1" fixed="true"  display="0"/>  
	<item id="UPD" alias="UPD" type="int" length="1" fixed="true"  display="0"/>  
	<item id="ZB" alias="ZB" type="int" length="8" fixed="true"  display="0"/>  
	<item id="KDYS" alias="开单医生" type="string" length="20" fixed="true"  display="0"/>  
	<item id="ZKYS" alias="质控医生" type="string" length="20" fixed="true"  display="0"/>  

	<item id="createUnit" alias="创建单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="0" defaultValue="%user.userId">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="0" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
