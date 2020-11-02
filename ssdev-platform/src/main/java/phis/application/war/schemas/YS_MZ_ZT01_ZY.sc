<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_ZT01" sort="ZTLB,ZTBH" alias="个人处方组套">
	<item id="ZTBH" alias="组套编号" type="int" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="ZTMC" alias="组套名称"  anchor="100%" colspan="2" width="250" type="string" length="20" not-null="1"/>
	<item id="ZTLB" alias=" " type="string" length="1" renderer="ZtlbRender" defaultValue="1" />
	<item id="SSLB" alias="所属类别" type="int" length="1" display="0" defaultValue="1">
		<dic id="phis.dictionary.comboType"/>
	</item>
	
	<item id="XMXQ" alias="项目选取" type="int" length="1" display="0"/>
	<item id="SFQY" alias="启用" type="int" width="50" length="1" display="0" defaultValue="0" renderer="onRenderer">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="YGDM" alias="员工代码" type="sting" length="10" display="0" defaultValue="%user.userId"/>
	<item id="JGID" alias="机构" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="KSDM" alias="科室代码" type="string" length="10" display="0"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" display="0" queryable="true"/>
</entry>