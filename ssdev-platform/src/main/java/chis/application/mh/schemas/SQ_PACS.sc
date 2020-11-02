<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mh.schemas.SQ_PACS" alias="胸片检查表"> 
	<item id="JLXH" pkey="true" alias="记录主键" type="string" length="30" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="30" startPos="1"/> 
		</key> 
	</item>  
	<item id="BGSJ" alias="报告时间" type="date"/>   
	<item id="BGMS" alias="报告描述" type="string" length="4000" fixed="true"  display="1"/>
	<item id="BGZD" alias="报告诊断" type="string" length="2000" fixed="true"  display="1"/>
	<item id="YCBZ" alias="异常标志" type="int" length="2" fixed="true"  display="1">  
		<dic> 
			<item key="1" text="异常"/>  
			<item key="0" text="无"/>  
		</dic> 		
	</item>   
	
	<item id="BGJY" alias="报告建议" type="string" length="1000" fixed="true"  display="1"/>
	<item id="BGYS" alias="报告医生" type="string" length="40" fixed="true"  display="1"/>
	<item id="JCYY" alias="检查医院" type="string" length="40" fixed="true"  display="1"/>
	<item id="YXH" alias="影像号" type="string" length="40" fixed="true"  display="1"/>
	<item id="JCBWFF" alias="检查部位方式" type="string" length="100" fixed="true"  display="1"/>
	<item id="ZDYY" alias="诊断医院" type="string" length="40" fixed="true"  display="1"/>


	<item id="JKKH" alias="健康卡号" type="string" length="20" fixed="true"  display="0"/>  
	<item id="GRBM" alias="个人编码" type="string" length="30" fixed="true"  display="0"/>  
	<item id="EMPIID" alias="EMPIID" type="string" length="32" fixed="true"  display="0"/>  
	<item id="JCH" alias="检查号" type="string" length="40" fixed="true"  display="0"/>  
	<item id="HZLX" alias="患者类型" type="int" length="2" fixed="true"  display="0"/>
	<item id="SQDH" alias="申请单号" type="string" length="40" fixed="true"  display="0"/>
	<item id="SBLX" alias="设备类型" type="string" length="40" fixed="true"  display="0"/>
	<item id="SBMX" alias="设备明细" type="string" length="40" fixed="true"  display="0"/>
	<item id="JCSJ" alias="检查时间" type="date"/>  
	<item id="JCZT" alias="检查状态" type="string" length="20" fixed="true"  display="0"/>
	<item id="SFYX" alias="随访有效" type="string" length="10" fixed="true"  display="0"/>
	 
	<item id="SFRQ" alias="随访日期" type="date" display="0"/>    
	<item id="INTIME" alias="引入时间" type="date" display="0"/>    
	<item id="CZGH" alias="操作工号" type="string" length="20" fixed="true"  display="0"/>
	
	<item id="YRBZ" alias="异常标志" type="int" length="1" fixed="true"  display="0"/>
	
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
