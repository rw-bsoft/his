<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.def.schemas.DEF_DeformityRecord" alias="残疾人档案表">
	<item id="PHRID" alias="健康档案号" length="30" width="130"/>
	<item id="EMPIID" alias="empiId" length="30" width="120" display="0"/>
	<item id="CJRID" alias="残疾人档案主键" length="18" width="120" display="0"/>
	<item id="INCOMESOURCE" alias="收入" length="2" width="120" display="0"/>
	<item id="PERSONNAME" alias="姓名" type="string" length="20"/>
	<item id="IDCARD" alias="身份证号" type="string" length="20" width="160" />
	<item id="BIRTHDAY" alias="出生日期" type="date" width="75"/>
	<item id="SEXCODE" alias="性别" type="string" length="1" width="40"  defalutValue="9">
		<dic id="gp.dictionary.gender"/>
	</item>
	<item id="CJRLB" alias="残疾类别" type="string" length="1">
		<dic>
			<item key="1" text="肢体残疾"/>  
			<item key="2" text="脑瘫残疾"/>  
			<item key="3" text="智力残疾"/> 
		</dic>
	</item>
	<item id="REGIONCODE" alias="网格地址" type="string" length="25" width="200" colspan="2" anchor="100%" update="false">
		<dic id="gp.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="MOBILENUMBER" alias="本人电话" type="string" length="20" width="90"/>
	<item id="CONTACTPHONE" alias="联系电话" length="20" />
	<item id="MANAUNITID" alias="管辖机构" type="string" not-null="true" length="20" colspan="2" anchor="100%" width="180"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="gp.@manageUnit" showWholeText="true" includeParentMinLen="6" render="Tree"/>
	</item>
	<item id="MANADOCTORID" alias="责任医生" type="string" length="20" not-null="true" update="false">
		<dic id="gp.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
  	
	<item id="WORKPLACE" alias="工作单位" type="string" length="50"/>
	
	<item id="PARENTNAME" alias="家长姓名" length="20" />
	<item id="RELATION" alias="与残疾人关系" length="20" >
		<dic>
			<item key="1" text="丈夫" />
			<item key="2" text="妻子" />
			<item key="3" text="儿子" />
			<item key="4" text="女儿" />
			<item key="5" text="父亲" />
			<item key="6" text="母亲" />
			<item key="7" text="孙子" />
			<item key="8" text="孙女" />
			<item key="9" text="祖父" />
			<item key="10" text="祖母" />
			<item key="11" text="外祖父" />
			<item key="12" text="外祖母" />
			<item key="13" text="女婿" />
			<item key="14" text="儿媳" />
			<item key="15" text="哥哥" />
			<item key="16" text="姐姐" />
			<item key="17" text="弟弟" />
			<item key="18" text="妹妹" />
			<item key="19" text="孙女婿" />
			<item key="20" text="孙媳" />
			<item key="21" text="岳父" />
			<item key="22" text="岳母" />
			<item key="23" text="公公" />
			<item key="24" text="婆婆" />
			<item key="25" text="姐夫" />
			<item key="26" text="嫂子" />
			<item key="27" text="妹夫" />
			<item key="28" text="弟妹" />
			<item key="29" text="侄子" />
			<item key="30" text="侄女" />
			<item key="31" text="外孙" />
			<item key="32" text="外孙女" />
			<item key="33" text="朋友" />
			<item key="35" text="曾孙" />
			<item key="36" text="曾祖父" />
			<item key="37" text="曾祖母" />
			<item key="38" text="养父" />
			<item key="39" text="养母" />
			<item key="40" text="养子" />
			<item key="41" text="养女" />
			<item key="42" text="继父" />
			<item key="43" text="继母" />
			<item key="44" text="曾外孙女" />
			<item key="46" text="外甥" />
			<item key="47" text="外甥女" />
			<item key="48" text="堂(表)兄弟" />
			<item key="49" text="堂(表)姐妹" />
			<item key="98" text="其他" />
		</dic>
	</item>
	<item id="DEFORMITYDATE" alias="残疾时间" type="date" not-null="1" maxValue="%server.date.today"/>
	<item id="EXPLANATION" alias="需要说明的情况" length="500" xtype="textarea" colspan="3" width="200"/>
	<item id="CREATEDATE" alias="录入时间" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="CLOSEFLAG" alias="结案标识" type="string" length="1"
		display="0" defaultValue="n" queryable="true">
		<dic>
			<item key="n" text="未结案" />
			<item key="y" text="已结案" />
		</dic>
	</item>
	<item id="STATUS" alias="档案状态" type="string" length="1"
		defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="CANCELLATIONREASON" alias="档案注销原因" type="string" length="1"
		queryable="true" display="0">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="6" text="作废" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="CANCELLATIONUSER" alias="注销人" type="string" length="20" fixed="true" colspan="2" anchor="100%" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="CANCELLATIONDATE" alias="注销日期" type="date" maxValue="%server.date.today" fixed="true" display="0">
	</item>
	<item id="CANCELLATIONUNIT" alias="注销单位" type="string" length="20"
		fixed="true" colspan="2" anchor="100%" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="CREATEUNIT" alias="录入单位" type="string" length="20"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="CREATEUSER" alias="录入人" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	
	<item id="LASTMODIFYUSER" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="LASTMODIFYDATE" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="LASTMODIFYUNIT" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>
